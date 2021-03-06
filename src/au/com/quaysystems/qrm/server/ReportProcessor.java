package au.com.quaysystems.qrm.server;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.IPDFRenderOption;
import org.eclipse.birt.report.engine.api.IRenderOption;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.RenderOption;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import au.com.quaysystems.qrm.wp.model.Audit;
import au.com.quaysystems.qrm.wp.model.AuditItem;
import au.com.quaysystems.qrm.wp.model.AvailableReport;
import au.com.quaysystems.qrm.wp.model.Category;
import au.com.quaysystems.qrm.wp.model.Comment;
import au.com.quaysystems.qrm.wp.model.Control;
import au.com.quaysystems.qrm.wp.model.Incident;
import au.com.quaysystems.qrm.wp.model.Matrix;
import au.com.quaysystems.qrm.wp.model.MitPlan;
import au.com.quaysystems.qrm.wp.model.Mitigation;
import au.com.quaysystems.qrm.wp.model.Objective;
import au.com.quaysystems.qrm.wp.model.Project;
import au.com.quaysystems.qrm.wp.model.QRMImport;
import au.com.quaysystems.qrm.wp.model.ReportJob;
import au.com.quaysystems.qrm.wp.model.RespPlan;
import au.com.quaysystems.qrm.wp.model.Response;
import au.com.quaysystems.qrm.wp.model.Review;
import au.com.quaysystems.qrm.wp.model.ReviewRiskComment;
import au.com.quaysystems.qrm.wp.model.Risk;
import au.com.quaysystems.qrm.wp.model.User;

public class ReportProcessor {
	private static Properties configProp = new Properties();
	private IReportEngine engine;
	private Configuration repConfig;
	private String hostURLRoot;
	private String hostUser;
	private String hostPass;
	private String hibernateDialect;

	private SessionFactory sfReport;
	private Gson gson;

	public ReportProcessor(ServletContext sc) {
				
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Integer.class, new IntegerDeserializer());
		builder.registerTypeAdapter(Double.class, new DoubleDeserializer());
		
		try ( InputStream in = new FileInputStream(sc.getRealPath("/WPQRM.properties"))){			
			try {
				configProp.load(in);
				configProp.put("REPORT_PATH", sc.getRealPath("/reports").replace("\\", "/")+"\\");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch ( Exception e) {
			e.printStackTrace();
		}
		
		engine = BirtEngineFactory.getInstance(configProp);


		hostURLRoot = configProp.getProperty("HOSTURLROOT");
		hostUser = configProp.getProperty("HOSTUSER");
		hostPass = configProp.getProperty("HOSTUSERPASS");
		hibernateDialect = configProp.getProperty("HIBERNATEDIALECT");
		gson = builder.create();

	}

	@SuppressWarnings("unchecked")
	public void deliver(String reportData, String reportID, String sessionToken) {

		System.out.println(">>> Executing Report");
		HashMap<Object, Object> taskParamMap = new HashMap<Object, Object>();
		String dbname = new BigInteger(130, new SecureRandom()).toString(32);
		
//		dbname = "qrm3";

		QRMImport imp = gson.fromJson(reportData, QRMImport.class);
		boolean registeredSite = PersistenceUtils.checkSiteKey(imp.siteKey, imp.siteID);

		Connection conn;
		try {
			conn = DriverManager.getConnection(hostURLRoot, hostUser, hostPass);
			try  {
				ServletUserMessageManager.notifyUserMessage(imp.userEmail, "Preparing Server Data",sessionToken);
				createDatabase(conn, dbname);
				ServletUserMessageManager.notifyUserMessage(imp.userEmail, "Report Executing. Please Standby.",  sessionToken);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				conn.close();
			}
		} catch (SQLException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}

		try {

			Session auditsess = PersistenceUtils.getAuditSession();
			final Session sess = getSession(hostURLRoot + "/" + dbname);
			final ReportJob job = new ReportJob();

			final Session adminsess = PersistenceUtils.getAdminSession();
			AvailableReport report = (AvailableReport) adminsess.get(AvailableReport.class, Long.parseLong(reportID));
			adminsess.close();
			String reportName = report.filename;
			

			try {

				taskParamMap.put("useWatermark", !registeredSite);
				taskParamMap.put("userEmail", imp.userEmail);
				taskParamMap.put("userDisplayName", imp.userDisplayName);
				taskParamMap.put("siteName", imp.siteName);
				taskParamMap.put("userLogin", imp.userLogin);

				job.completed = false;
				job.siteID = imp.siteID;
				job.siteKey = imp.siteKey;
				job.siteName = imp.siteName;
				job.userDisplayName = imp.userDisplayName;
				job.userEmail = imp.userEmail;
				job.userLogin = imp.userLogin;
				job.reportID = reportID;
				job.reportName = reportName;
				job.submittedDate = new Date();
				job.ip = sessionToken;
				job.reportTitle = report.title;

				boolean prepareMatrix = false;

				if (report.req_riskMatrix) {
					prepareMatrix = true;
				}

				imp.normalise(prepareMatrix);

				Transaction txn = sess.beginTransaction();
				sess.save(imp);
				txn.commit();
				
				if (report.id == 9){   //Relative Matrix Report
					List<Risk> risks = sess.createCriteria(Risk.class).list();
					List<Matrix> mats = sess.createCriteria(Matrix.class).list();
					RelMatrixReportVisitor visitor = new RelMatrixReportVisitor();
					visitor.process(taskParamMap, risks, mats.get(0));
				}
				
				sess.close();

				Transaction txn2 = auditsess.beginTransaction();
				auditsess.save(job);
				txn2.commit();
				auditsess.close();

			} catch (Exception e) {
				e.printStackTrace();
				try  {
					ServletUserMessageManager.notifyUserMessage(imp.userEmail,"Sorry, an error was encountered on the Report Server - MG001", sessionToken);
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				return;
			}

			ByteArrayOutputStream os = new ByteArrayOutputStream();

			conn = DriverManager.getConnection(hostURLRoot + "/" + dbname, hostUser, hostPass);
			
			try  {

				IRenderOption options = new RenderOption();
				options.setOutputFormat(HTMLRenderOption.OUTPUT_FORMAT_PDF);
				options.setEmitterID("org.eclipse.birt.report.engine.emitter.pdf");
				options.setOption(IPDFRenderOption.PAGE_OVERFLOW, IPDFRenderOption.OUTPUT_TO_MULTIPLE_PAGES);
				options.setOutputStream(os);

				String file = configProp.getProperty("REPORT_PATH") + reportName;
				file = file.replace("\\", "/");
				InputStream is = new FileInputStream(file);

				IRunAndRenderTask task = engine.createRunAndRenderTask(engine.openReportDesign(is));
				task.setRenderOption(options);

				task.setParameterValues(taskParamMap);
				task.getAppContext().put("OdaJDBCDriverPassInConnection", conn);

				try {
					task.run();
				} catch (Exception e) {
					e.printStackTrace();
					try  {
						ServletUserMessageManager.notifyUserMessage(imp.userEmail,"Sorry, an error was encountered on the Report Server - MG002", sessionToken);
						return;
					} catch (Exception e2) {
						e2.printStackTrace();
					}			
				}

				job.reportResult = os.toByteArray();

			} catch (Exception e) {
				e.printStackTrace();
				try  {
					ServletUserMessageManager.notifyUserMessage(imp.userEmail,"Sorry, an error was encountered on the Report Server - MG003", sessionToken);
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				return;
			} finally {

				conn.close();
				try {
					System.out.println(">>> Report Complete");

					job.completed = true;
					job.completedDate = new Date();

					auditsess = PersistenceUtils.getAuditSession();
					Transaction txn = auditsess.beginTransaction();
					auditsess.update(job);
					txn.commit();

					try {
						ServletUserMessageManager.notifyUserReportReady(imp.userEmail, job.id, sessionToken);
					} catch (IOException e1) {
						e1.printStackTrace();
					}

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					auditsess.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			try  {
				ServletUserMessageManager.notifyUserMessage(imp.userEmail,"Sorry, an error was encountered on the Report Server - MG004", sessionToken);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		} finally {
			try {
				Connection conn2 = DriverManager.getConnection(hostURLRoot, hostUser, hostPass);
				try  {
					Statement stmt = conn2.createStatement();
					stmt.addBatch("DROP DATABASE IF EXISTS `" + dbname + "`");
					stmt.executeBatch();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					conn2.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private synchronized Session getSession(String hostURL) {

		System.setProperty("com.mchange.v2.log.MLog", "com.mchange.v2.log.FallbackMLog");
		System.setProperty("com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL", "WARNING");
		
		repConfig = new Configuration().setProperty("hibernate.cache.use_second_level_cache", "false")
//				.setProperty("hibernate.c3p0.min_size","0")
//				.setProperty("hibernate.c3p0.max_size","1")
//				.setProperty("hibernate.c3p0.timeout","5")
//				.setProperty("hibernate.c3p0.max_statements","50")
				.setProperty("hibernate.dialect", hibernateDialect).setProperty("hibernate.connection.url", hostURL)
				.setProperty("hibernate.connection.username", hostUser)
				.setProperty("hibernate.connection.password", hostPass)
				.addAnnotatedClass(AuditItem.class)
				.addAnnotatedClass(Audit.class)
				.addAnnotatedClass(Category.class)
				.addAnnotatedClass(Comment.class)
				.addAnnotatedClass(Incident.class)
				.addAnnotatedClass(Matrix.class)
				.addAnnotatedClass(MitPlan.class)
				.addAnnotatedClass(Mitigation.class)
				.addAnnotatedClass(Response.class)
				.addAnnotatedClass(Objective.class)
				.addAnnotatedClass(Control.class)
				.addAnnotatedClass(Control.class)
				.addAnnotatedClass(Risk.class)
				.addAnnotatedClass(Project.class)
				.addAnnotatedClass(ReviewRiskComment.class)
				.addAnnotatedClass(Review.class)
				.addAnnotatedClass(RespPlan.class)
				.addAnnotatedClass(User.class)
				.addAnnotatedClass(QRMImport.class);

		StandardServiceRegistryBuilder serviceRegistryBuilder = new StandardServiceRegistryBuilder();
		serviceRegistryBuilder.applySettings(repConfig.getProperties());
		ServiceRegistry serviceRegistry = serviceRegistryBuilder.build();
		sfReport = repConfig.buildSessionFactory(serviceRegistry);

		return sfReport.openSession();
	}

	private void createDatabase(Connection conn, String dbname) {
		try {
			Statement stmt = conn.createStatement();

			stmt.addBatch("DROP DATABASE IF EXISTS `" + dbname + "`");
			stmt.addBatch("CREATE DATABASE `" + dbname + "`");
			stmt.addBatch(" USE `" + dbname + "`");
			stmt.addBatch(
					"CREATE TABLE `audit` (   `id` bigint(20) NOT NULL,   `riskID` bigint(20) DEFAULT NULL,   PRIMARY KEY (`id`) ) ENGINE=MyISAM DEFAULT CHARSET=latin1");
			stmt.addBatch(
					"CREATE TABLE `audititem` (   `id` bigint(20) NOT NULL,   `auditComment` text,   `auditDate` varchar(255) DEFAULT NULL,   `auditPerson` int(11) NOT NULL,   `riskID` int(11) NOT NULL,   `type` varchar(255) DEFAULT NULL,   PRIMARY KEY (`id`) ) ENGINE=MyISAM DEFAULT CHARSET=latin1");
			stmt.addBatch(
					"CREATE TABLE `audit_audititem` (   `audit_id` bigint(20) NOT NULL,   `audititem_id` bigint(20) NOT NULL,   `auditItems_ORDER` int(11) NOT NULL,   PRIMARY KEY (`audit_id`,`auditItems_ORDER`),   UNIQUE KEY `UK_ec392k0qegf7qks9sxx6nbei7` (`audititem_id`) ) ENGINE=MyISAM DEFAULT CHARSET=latin1");
			stmt.addBatch(
					"CREATE TABLE `category` (   `id` int(11) NOT NULL,   `parentID` int(11) NOT NULL,   `primCat` bit(1) NOT NULL,   `projectID` int(11) NOT NULL,   `title` varchar(255) DEFAULT NULL,   PRIMARY KEY (`id`) ) ENGINE=MyISAM DEFAULT CHARSET=latin1");
			stmt.addBatch(
					"CREATE TABLE `comment` (   `id` bigint(20) NOT NULL,   `comment_author` varchar(255) DEFAULT NULL,   `comment_author_email` varchar(255) DEFAULT NULL,   `comment_content` text,   `comment_date` varchar(255) DEFAULT NULL,   `comment_id` varchar(255) DEFAULT NULL,   `comment_post_ID` varchar(255) DEFAULT NULL,   `parentID` int(11) NOT NULL,   PRIMARY KEY (`id`) ) ENGINE=MyISAM DEFAULT CHARSET=latin1");
			stmt.addBatch(
					"CREATE TABLE `control` (   `id` bigint(20) NOT NULL,   `contribution` varchar(255) DEFAULT NULL,   `description` text,   `effectiveness` varchar(255) DEFAULT NULL,   PRIMARY KEY (`id`) ) ENGINE=MyISAM DEFAULT CHARSET=latin1");
			stmt.addBatch(
					"CREATE TABLE `import_incident` (   `import_id` bigint(20) NOT NULL,   `incident_id` int(11) NOT NULL,   `incidents_ORDER` int(11) NOT NULL,   PRIMARY KEY (`import_id`,`incidents_ORDER`),   UNIQUE KEY `UK_57j57y9coim8t11vgjsv86rmf` (`incident_id`) ) ENGINE=MyISAM DEFAULT CHARSET=latin1");
			stmt.addBatch(
					"CREATE TABLE `import_project` (   `import_id` bigint(20) NOT NULL,   `project_id` int(11) NOT NULL,   `projects_ORDER` int(11) NOT NULL,   PRIMARY KEY (`import_id`,`projects_ORDER`),   UNIQUE KEY `UK_ix3okwfiakp0juf1fromh6txi` (`project_id`) ) ENGINE=MyISAM DEFAULT CHARSET=latin1");
			stmt.addBatch(
					"CREATE TABLE `import_review` (   `import_id` bigint(20) NOT NULL,   `review_id` bigint(20) NOT NULL,   `reviews_ORDER` int(11) NOT NULL,   PRIMARY KEY (`import_id`,`reviews_ORDER`) ) ENGINE=MyISAM DEFAULT CHARSET=latin1");
			stmt.addBatch(
					"CREATE TABLE `import_risk` (   `import_id` bigint(20) NOT NULL,   `risk_id` int(11) NOT NULL,   `risks_ORDER` int(11) NOT NULL,   PRIMARY KEY (`import_id`,`risks_ORDER`),   UNIQUE KEY `UK_ms6ow68gx4uvplh3o0xiihi3n` (`risk_id`) ) ENGINE=MyISAM DEFAULT CHARSET=latin1");
			stmt.addBatch(
					"CREATE TABLE `import_user` (   `import_id` bigint(20) NOT NULL,   `user_id` bigint(20) NOT NULL,   `users_ORDER` int(11) NOT NULL,   PRIMARY KEY (`import_id`,`users_ORDER`),   UNIQUE KEY `UK_ltoaxcxec812i34he28rmb9op` (`user_id`) ) ENGINE=MyISAM DEFAULT CHARSET=latin1");
			stmt.addBatch(
					"CREATE TABLE `incident` (   `id` int(11) NOT NULL,   `actions` text DEFAULT NULL,   `causes` bit(1) NOT NULL,  `time` bit(1) NOT NULL,  `environment` bit(1) NOT NULL, `safety` bit(1) NOT NULL,  `cost` bit(1) NOT NULL, `reputation` bit(1) NOT NULL,`spec` bit(1) NOT NULL, `consequences` bit(1) NOT NULL,   `controls` bit(1) NOT NULL,   `date` varchar(255) DEFAULT NULL,   `description` text,   `evaluated` bit(1) NOT NULL,   `identified` bit(1) NOT NULL,   `incidentCode` varchar(255) DEFAULT NULL,   `lessons` text DEFAULT NULL,   `reportedby` int(11) NOT NULL,   `resolved` bit(1) NOT NULL,   `title` text,   PRIMARY KEY (`id`) ) ENGINE=MyISAM DEFAULT CHARSET=latin1");
			stmt.addBatch(
					"CREATE TABLE `incidentcomment` (   `incident_id` int(11) NOT NULL,   `comments_id` bigint(20) NOT NULL,   `comments_ORDER` int(11) NOT NULL,   PRIMARY KEY (`incident_id`,`comments_ORDER`),   UNIQUE KEY `UK_9l2wfu645oujbhumxvglm8ptv` (`comments_id`) ) ENGINE=MyISAM DEFAULT CHARSET=latin1");
			stmt.addBatch(
					"CREATE TABLE `incidentrisk` (   `incident_id` int(11) NOT NULL,   `risks` int(11) DEFAULT NULL,   `risks_ORDER` int(11) NOT NULL,   PRIMARY KEY (`incident_id`,`risks_ORDER`) ) ENGINE=MyISAM DEFAULT CHARSET=latin1");
			stmt.addBatch(
					"CREATE TABLE `matrix` (   `matrix_id` bigint(20) NOT NULL AUTO_INCREMENT,   `maxImpact` int(11) NOT NULL,   `maxProb` int(11) NOT NULL,   `probVal1` int(11) NOT NULL,   `probVal2` int(11) NOT NULL,   `probVal3` int(11) NOT NULL,   `probVal4` int(11) NOT NULL,   `probVal5` int(11) NOT NULL,   `probVal6` int(11) NOT NULL,   `probVal7` int(11) NOT NULL,   `probVal8` int(11) NOT NULL,   `tolString` varchar(255) DEFAULT NULL,   PRIMARY KEY (`matrix_id`) ) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=latin1");
			stmt.addBatch(
					"CREATE TABLE `mitigation` (   `id` bigint(20) NOT NULL,   `mitPlanSummary` text,   `mitPlanSummaryUpdate` text,   PRIMARY KEY (`id`) ) ENGINE=MyISAM DEFAULT CHARSET=latin1");
			stmt.addBatch(
					"CREATE TABLE `mitigation_mitstep` (   `mitigation_id` bigint(20) NOT NULL,   `mitigationstep_id` bigint(20) NOT NULL,   `mitPlan_ORDER` int(11) NOT NULL,   PRIMARY KEY (`mitigation_id`,`mitPlan_ORDER`),   UNIQUE KEY `UK_cva4fbv47muyiugo5l7vnd581` (`mitigationstep_id`) ) ENGINE=MyISAM DEFAULT CHARSET=latin1");
			stmt.addBatch(
					"CREATE TABLE `mitplan` (   `id` bigint(20) NOT NULL,   `complete` double NOT NULL,   `cost` double NOT NULL,   `description` text,   `due` varchar(255) DEFAULT NULL,   `person` int(11) NOT NULL,   PRIMARY KEY (`id`) ) ENGINE=MyISAM DEFAULT CHARSET=latin1");
			stmt.addBatch(
					"CREATE TABLE `objective` (   `id` int(11) NOT NULL,   `parentID` int(11) NOT NULL,   `projectID` int(11) NOT NULL,   `title` text,   PRIMARY KEY (`id`) ) ENGINE=MyISAM DEFAULT CHARSET=latin1");
			stmt.addBatch(
					"CREATE TABLE `project` (   `id` int(11) NOT NULL,   `description` text,   `inheritParentCategories` bit(1) NOT NULL,   `inheritParentObjectives` bit(1) NOT NULL,   `parent_id` int(11) NOT NULL,   `projectCode` varchar(255) DEFAULT NULL,   `projectRiskManager` int(11) NOT NULL,   `title` text,   PRIMARY KEY (`id`) ) ENGINE=MyISAM DEFAULT CHARSET=latin1");
			stmt.addBatch(
					"CREATE TABLE `project_categories` (   `project_id` int(11) NOT NULL,   `category_id` int(11) NOT NULL,   `categories_ORDER` int(11) NOT NULL,   PRIMARY KEY (`project_id`,`categories_ORDER`),   UNIQUE KEY `UK_4ue443eo5qfg68f3ovq7u9fuv` (`category_id`) ) ENGINE=MyISAM DEFAULT CHARSET=latin1");
			stmt.addBatch(
					"CREATE TABLE `project_managersids` (   `project_id` int(11) NOT NULL,   `managersID` int(11) DEFAULT NULL,   `managersID_ORDER` int(11) NOT NULL,   PRIMARY KEY (`project_id`,`managersID_ORDER`) ) ENGINE=MyISAM DEFAULT CHARSET=latin1");
			stmt.addBatch(
					"CREATE TABLE `project_matrix` (   `matrix_id` bigint(20) DEFAULT NULL,   `project_id` int(11) NOT NULL,   PRIMARY KEY (`project_id`),   KEY `FK_83bsp7whlaf02030pfwprqpid` (`matrix_id`) ) ENGINE=MyISAM DEFAULT CHARSET=latin1");
			stmt.addBatch(
					"CREATE TABLE `project_objectives` (   `project_id` int(11) NOT NULL,   `objective_id` int(11) NOT NULL,   `objectives_ORDER` int(11) NOT NULL,   PRIMARY KEY (`project_id`,`objectives_ORDER`),   UNIQUE KEY `UK_bb51m2ws9qld04v9fk519jvya` (`objective_id`) ) ENGINE=MyISAM DEFAULT CHARSET=latin1");
			stmt.addBatch(
					"CREATE TABLE `project_ownersids` (   `project_id` int(11) NOT NULL,   `ownersID` int(11) DEFAULT NULL,   `ownersID_ORDER` int(11) NOT NULL,   PRIMARY KEY (`project_id`,`ownersID_ORDER`) ) ENGINE=MyISAM DEFAULT CHARSET=latin1");
			stmt.addBatch(
					"CREATE TABLE `project_usersids` (   `project_id` int(11) NOT NULL,   `usersID` int(11) DEFAULT NULL,   `usersID_ORDER` int(11) NOT NULL,   PRIMARY KEY (`project_id`,`usersID_ORDER`) ) ENGINE=MyISAM DEFAULT CHARSET=latin1");
			stmt.addBatch(
					"CREATE TABLE `qrmimport` (   `id` bigint(20) NOT NULL,   PRIMARY KEY (`id`) ) ENGINE=MyISAM DEFAULT CHARSET=latin1");
			stmt.addBatch(
					"CREATE TABLE `response` (   `id` bigint(20) NOT NULL,   `respPlanSummary` text,   `respPlanSummaryUpdate` text,   PRIMARY KEY (`id`) ) ENGINE=MyISAM DEFAULT CHARSET=latin1");
			stmt.addBatch(
					"CREATE TABLE `response_respstep` (   `response_id` bigint(20) NOT NULL,   `responsestep_id` bigint(20) NOT NULL,   `respPlan_ORDER` int(11) NOT NULL,   PRIMARY KEY (`response_id`,`respPlan_ORDER`),   UNIQUE KEY `UK_g6boraaa8bc5lyxp8u2gn038w` (`responsestep_id`) ) ENGINE=MyISAM DEFAULT CHARSET=latin1");
			stmt.addBatch(
					"CREATE TABLE `respplan` (   `id` bigint(20) NOT NULL,   `cost` double NOT NULL,   `description` text,   `person` int(11) NOT NULL,   PRIMARY KEY (`id`) ) ENGINE=MyISAM DEFAULT CHARSET=latin1");
			stmt.addBatch(
					"CREATE TABLE `review` (   `id` bigint(20) NOT NULL,   `actualdate` varchar(255) DEFAULT NULL,   `description` text,   `notes` text, `complete` bit(1) NOT NULL,   `responsible` int(11) NOT NULL,   `reviewCode` varchar(255) DEFAULT NULL,   `scheddate` varchar(255) DEFAULT NULL,   `title` varchar(255) DEFAULT NULL,   PRIMARY KEY (`id`) ) ENGINE=MyISAM DEFAULT CHARSET=latin1");
			stmt.addBatch(
					"CREATE TABLE `reviewriskcomment` (   `id` bigint(20) NOT NULL,   `comment` text,   `riskID` int(11) NOT NULL,   PRIMARY KEY (`id`) ) ENGINE=MyISAM DEFAULT CHARSET=latin1");
			stmt.addBatch(
					"CREATE TABLE `review_reviewriskcomment` (   `review_id` bigint(20) NOT NULL,   `reviewriskcomment_id` bigint(20) NOT NULL,   `riskComments_ORDER` int(11) NOT NULL,   PRIMARY KEY (`review_id`,`riskComments_ORDER`),   UNIQUE KEY `UK_q16alistcugy482lwqr5e613j` (`reviewriskcomment_id`) ) ENGINE=MyISAM DEFAULT CHARSET=latin1");
			stmt.addBatch(
					"CREATE TABLE `review_risk` (   `review_id` bigint(20) NOT NULL,   `risks` int(11) DEFAULT NULL,   `risks_ORDER` int(11) NOT NULL,   PRIMARY KEY (`review_id`,`risks_ORDER`) ) ENGINE=MyISAM DEFAULT CHARSET=latin1");
			stmt.addBatch(
					"CREATE TABLE `risk` (   `id` int(11) NOT NULL,   `cause` text,   `consequence` text,   `currentImpact` double NOT NULL,   `currentProb` double NOT NULL,   `currentTolerance` int(11) NOT NULL,   `description` text,   `end` varchar(255) DEFAULT NULL,   `estContingency` double NOT NULL,   `impCost` bit(1) NOT NULL,   `impEnviron` bit(1) NOT NULL,   `impRep` bit(1) NOT NULL,   `impSafety` bit(1) NOT NULL,   `impSpec` bit(1) NOT NULL,   `impTime` bit(1) NOT NULL,   `inherentAbsProb` double NOT NULL,   `inherentImpact` double NOT NULL,   `inherentProb` double NOT NULL,   `inherentTolerance` int(11) NOT NULL,   `likeAlpha` double NOT NULL,   `likePostAlpha` double NOT NULL,   `likePostT` double NOT NULL,   `likePostType` double NOT NULL,   `likeT` double NOT NULL,   `likeType` double NOT NULL,   `manager` int(11) NOT NULL,   `matImage` longblob,   `owner` int(11) NOT NULL, `rank` int(11), `postLikeImage` longblob,   `preLikeImage` longblob,   `primcatID` int(11) NOT NULL,   `projectID` int(11) NOT NULL,   `riskProjectCode` varchar(255) DEFAULT NULL,   `seccatID` int(11) NOT NULL,   `start` varchar(255) DEFAULT NULL,   `summaryRisk` bit(1) NOT NULL,   `title` text,   `treatAvoid` bit(1) NOT NULL,   `treatMinimise` bit(1) NOT NULL,   `treatRetention` bit(1) NOT NULL,   `treatTransfer` bit(1) NOT NULL,   `treated` bit(1) NOT NULL,   `treatedAbsProb` double NOT NULL,   `treatedImpact` double NOT NULL,   `treatedProb` double NOT NULL,   `treatedTolerance` int(11) NOT NULL,   `useCalContingency` bit(1) NOT NULL,   `useCalProb` bit(1) NOT NULL,   PRIMARY KEY (`id`) ) ENGINE=MyISAM DEFAULT CHARSET=latin1");
			stmt.addBatch(
					"CREATE TABLE `risk_audit` (   `audit_id` bigint(20) DEFAULT NULL,   `risk_id` int(11) NOT NULL,   PRIMARY KEY (`risk_id`),   KEY `FK_qgpgc8v8a2omli7nokuircnu3` (`audit_id`) ) ENGINE=MyISAM DEFAULT CHARSET=latin1");
			stmt.addBatch(
					"CREATE TABLE `risk_comment` (   `risk_id` int(11) NOT NULL,   `comment_id` bigint(20) NOT NULL,   `comments_ORDER` int(11) NOT NULL,   PRIMARY KEY (`risk_id`,`comments_ORDER`),   UNIQUE KEY `UK_8bu6x97n9iahb3ldfvksv3oyg` (`comment_id`) ) ENGINE=MyISAM DEFAULT CHARSET=latin1");
			stmt.addBatch(
					"CREATE TABLE `risk_control` (   `risk_id` int(11) NOT NULL,   `control_id` bigint(20) NOT NULL,   `controls_ORDER` int(11) NOT NULL,   PRIMARY KEY (`risk_id`,`controls_ORDER`),   UNIQUE KEY `UK_p65984kisa9h8a17v0r6a6dc8` (`control_id`) ) ENGINE=MyISAM DEFAULT CHARSET=latin1");
			stmt.addBatch(
					"CREATE TABLE `risk_incidentids` (   `risk_id` int(11) NOT NULL,   `incidentIDs` int(11) DEFAULT NULL,   `incidentIDs_ORDER` int(11) NOT NULL,   PRIMARY KEY (`risk_id`,`incidentIDs_ORDER`) ) ENGINE=MyISAM DEFAULT CHARSET=latin1");
			stmt.addBatch(
					"CREATE TABLE `risk_mitigation` (   `mitigation_id` bigint(20) DEFAULT NULL,   `risk_id` int(11) NOT NULL,   PRIMARY KEY (`risk_id`),   KEY `FK_ffr3pdr5aq1qi501wie9ai7f6` (`mitigation_id`) ) ENGINE=MyISAM DEFAULT CHARSET=latin1");
			stmt.addBatch(
					"CREATE TABLE `risk_objectives` (   `risk_id` int(11) NOT NULL,   `objectiveIDs` int(11) DEFAULT NULL,   `objectiveIDs_ORDER` int(11) NOT NULL,   PRIMARY KEY (`risk_id`,`objectiveIDs_ORDER`) ) ENGINE=MyISAM DEFAULT CHARSET=latin1");
			stmt.addBatch(
					"CREATE TABLE `risk_response` (   `response_id` bigint(20) DEFAULT NULL,   `risk_id` int(11) NOT NULL,   PRIMARY KEY (`risk_id`),   KEY `FK_smldx88ks18cwhqludoy86w6o` (`response_id`) ) ENGINE=MyISAM DEFAULT CHARSET=latin1");
			stmt.addBatch(
					"CREATE TABLE `risk_reviewids` (   `risk_id` int(11) NOT NULL,   `reviewIDs` int(11) DEFAULT NULL,   `reviewIDs_ORDER` int(11) NOT NULL,   PRIMARY KEY (`risk_id`,`reviewIDs_ORDER`) ) ENGINE=MyISAM DEFAULT CHARSET=latin1");
			stmt.addBatch(
					"CREATE TABLE `user` (   `userID` bigint(20) NOT NULL,   `display_name` varchar(255) DEFAULT NULL,   `id` varchar(255) DEFAULT NULL,   `user_email` varchar(255) DEFAULT NULL,   PRIMARY KEY (`userID`) ) ENGINE=MyISAM DEFAULT CHARSET=latin1");
			String view = "create view risks as select risk.*, u1.display_name as managerName, u2.display_name as ownerName, "
					+ "category.title as primCatName, c1.title as secCatName, "
					+ "mitigation.mitPlanSummary, mitigation.mitPlanSummaryUpdate, "
					+ "response.respPlanSummary, response.respPlanSummaryUpdate,"
					+ "a1.auditDate as auditIDDate, a2.auditDate as auditIDRevDate, a3.auditDate as auditIDAppDate, "
					+ "a4.auditDate as auditEvalDate, a5.auditDate as auditEvalRevDate, a6.auditDate as auditEvalAppDate, "
					+ "a7.auditDate as auditMitDate, a8.auditDate as auditMitRevDate, a9.auditDate as auditMitAppDate,  "
					+ "p1.title as projectTitle " + "from risk "
					+ "left outer join user as u1 on u1.userID = risk.manager "
					+ "left outer join user as u2 on u2.userID = risk.owner "
					+ "left outer join category on category.id = risk.primcatID "
					+ "left outer join category as c1 on c1.id = risk.seccatID "
					+ "left outer join risk_mitigation on risk_mitigation.risk_id = risk.id "
					+ "left outer join mitigation on mitigation.id = risk_mitigation.mitigation_id "
					+ "left outer join risk_response on risk_response.risk_id = risk.id "
					+ "left outer join response on response.id = risk_response.response_id "
					+ "left outer join audititem as a1 on a1.riskID = risk.id and a1.type = 'auditIdent' "
					+ "left outer join audititem as a2 on a2.riskID = risk.id and a2.type = 'auditIdentRev' "
					+ "left outer join audititem as a3 on a3.riskID = risk.id and a3.type = 'auditIdentApp' "
					+ "left outer join audititem as a4 on a4.riskID = risk.id and a4.type = 'auditEval' "
					+ "left outer join audititem as a5 on a5.riskID = risk.id and a5.type = 'auditEvalRev' "
					+ "left outer join audititem as a6 on a6.riskID = risk.id and a6.type = 'auditEvalApp' "
					+ "left outer join audititem as a7 on a7.riskID = risk.id and a7.type = 'auditMit' "
					+ "left outer join audititem as a8 on a8.riskID = risk.id and a8.type = 'auditMitRev' "
					+ "left outer join audititem as a9 on a9.riskID = risk.id and a9.type = 'auditMitApp' "
					+ "left outer join project as p1 on p1.id = risk.projectID";

			String view2 = "create view riskreviews as SELECT  review.*,review_risk.risks,review_reviewriskcomment.reviewriskcomment_id,reviewriskcomment.comment FROM review "
					+ "join review_risk ON review_risk.review_id = review.id "
					+ "join review_reviewriskcomment ON review_reviewriskcomment.review_id = review.id "
					+ "left outer join reviewriskcomment ON reviewriskcomment.id = review_reviewriskcomment.reviewriskcomment_id AND reviewriskcomment.riskID = review_risk.risks";

			stmt.addBatch(view);
			stmt.addBatch(view2);
			stmt.executeBatch();

		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}
