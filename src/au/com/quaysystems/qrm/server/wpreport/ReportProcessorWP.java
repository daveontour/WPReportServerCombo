package au.com.quaysystems.qrm.server.wpreport;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.hibernate.service.ServiceRegistry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import au.com.quaysystems.qrm.server.QRMAsyncMessage;
import au.com.quaysystems.qrm.wp.QRMImport;
import au.com.quaysystems.qrm.wp.model.AvailableReport;
import au.com.quaysystems.qrm.wp.model.ClientSites;
import au.com.quaysystems.qrm.wp.model.ReportJob;
import au.com.quaysystems.qrm.wp.model.ReportSet;

@SuppressWarnings("serial")
public class ReportProcessorWP  extends HttpServlet{

	private static Properties configProp = new Properties();
	private Configuration auditConfig;
	private Properties props;
	private String hostUser;
	private String hostPass;
	private String hibernateDialect;
	private String hostDriverClass;
	private String hostURLReportAudit;
	private String hostURLAdmin;
	private String demoID;
	private String demoKey;
	private String testID;
	private String testKey;
	private String testName;
	private Configuration adminConfig;
	private SessionFactory sfAdmin;
	private SessionFactory sfAudit;
	private Gson gson;

	public void service(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {

		String action = req.getParameter("action");

		if (action.equalsIgnoreCase("execute_report")){
			serviceReport(req, response);
			return;
		}
		if (action.equalsIgnoreCase("get_report")){
			getReport(req, response);
			return;
		}
		if (action.equalsIgnoreCase("get_userreports")){
			getUserReports(req, response);
			return;
		}
		if (action.equalsIgnoreCase("get_availablereports")){
			getAvailableReports(req, response);
			return;
		}
		if (action.equalsIgnoreCase("remove_report")){
			removeReport(req, response);
			return;
		}
	}
	
	public void serviceReport(HttpServletRequest req, HttpServletResponse response) throws IOException  {
		String callback= req.getParameter("callback");
		try {
			String reportData = req.getParameter("reportData");
			String reportID = req.getParameter("reportID");
			String ipAddress = req.getHeader("X-FORWARDED-FOR"); 
			
			if (ipAddress == null) {  
				ipAddress = req.getRemoteAddr();  
			}
			
			final QRMImport imp = gson.fromJson(reportData, QRMImport.class);			
			boolean registeredSite = checkSiteKey(imp.siteKey, imp.siteID);
			
			if (!registeredSite){
				// site is not registered, so check if has the correct demo keys
				if (!(imp.siteKey.equalsIgnoreCase(demoKey) && imp.siteID.equalsIgnoreCase(demoID))){
					ServletUserMessageManager.notifyUserMessage(imp.userEmail, "Your site is unregistrerd and the demo key and ID are incorrect. Report not processed",3000);
					return;
				}
			}
			
			try {
				QRMAsyncMessage message = new QRMAsyncMessage("reportChannel", reportData, reportID, ipAddress);
				message.send();
				ServletUserMessageManager.notifyUserMessage(imp.userEmail, "Report Queued for Execution",500);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getReport(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {

		String userEmail = req.getParameter("userEmail");
		String userLogin = req.getParameter("userLogin");
		String siteKey = req.getParameter("siteKey");
		String id = req.getParameter("id");

		
		try (Connection conn = DriverManager.getConnection(hostURLReportAudit, hostUser, hostPass)){

			ResultSet res = conn.createStatement().executeQuery("SELECT reportResult FROM REPORTJOB WHERE id = "+id+ " AND userEmail = '"+ userEmail+"' AND siteKey='"+siteKey+"' AND userLogin='"+userLogin+"'");
			res.first();

			ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
			InputStream stream = res.getBinaryStream("reportResult");
			int a1 = stream.read();
			while (a1 >= 0) {
				byteArrayStream.write((char) a1);
				a1 = stream.read();
			}

			response.setHeader("Content-Disposition", "attachment; filename=QRMReport.pdf");
			response.setContentType("application/pdf");
			byteArrayStream.writeTo(response.getOutputStream());

			res.close();
			conn.close();
			byteArrayStream.close();
			response.getOutputStream().close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void removeReport(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {


		String userEmail = req.getParameter("userEmail");
		String userLogin = req.getParameter("userLogin");
		String siteKey = req.getParameter("siteKey");
		String id = req.getParameter("id");

		ReportJob job = null;

		final Session auditsess = getAuditSession();

		try {

			Transaction txn = auditsess.beginTransaction();
			job = (ReportJob) auditsess.createCriteria(ReportJob.class)
					.add(Restrictions.eq("id",Long.parseLong(id)))
					.add(Restrictions.eq("siteKey",siteKey))
					.add(Restrictions.eq("userEmail",userEmail))
					.add(Restrictions.eq("userLogin",userLogin))
					.uniqueResult();
			job.showUser = false;
			auditsess.update(job);
			txn.commit();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		getUserReports( req, response);
	}
	@SuppressWarnings("unchecked")
	public void getUserReports(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {

		String userEmail = req.getParameter("userEmail");
		String userLogin = req.getParameter("userLogin");
		String siteKey = req.getParameter("siteKey");
		String siteID = req.getParameter("siteID");
		String callback= req.getParameter("callback");

		if (!checkSiteKey(siteKey, siteID)){
			String res = callback+"({'error':'Your site has not been registered. You can generate reports but they will not be archived'})";
			response.setContentType("text/javascript");
			PrintWriter out = response.getWriter();
			out.println(res);
			return;
		}

		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

		List<ReportJob> job = null;

		Session auditsess = getAuditSession();

		try {

			job =  auditsess.createCriteria(ReportJob.class)
					.add(Restrictions.eq("siteKey",siteKey))
					.add(Restrictions.eq("userEmail",userEmail))
					.add(Restrictions.eq("userLogin",userLogin))
					.add(Restrictions.eq("showUser",true))
					.list();		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			auditsess.close();
		}


		String json = gson.toJson(job);
		String res = callback+"("+json+")";

		response.setContentType("text/javascript");
		PrintWriter out = response.getWriter();
		out.println(res);

	}

	private boolean checkSiteKey(String siteKey, String siteID){
		Session sess = getAdminSession();
		@SuppressWarnings("rawtypes")
		List sites = sess.createCriteria(ClientSites.class)
		.add(Restrictions.eq("siteKey",siteKey))
		.add(Restrictions.eq("siteID",siteID))
		.list();
		return sites.size() == 1;
	}
	@SuppressWarnings("unchecked")
	public void getAvailableReports(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {

		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		List<AvailableReport> reports = null;					
		Session auditsess = getAuditSession();

		try {
			reports =  auditsess.createCriteria(AvailableReport.class).list();		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			auditsess.close();
		}

		String callback= req.getParameter("callback");
		String json = gson.toJson(reports);
		String res = callback+"("+json+")";

		response.setContentType("text/javascript");
		PrintWriter out = response.getWriter();
		out.println(res);
	}

	@SuppressWarnings("unchecked")
	public void init(final ServletConfig sc) {
		try {
			super.init(sc);
		} catch (ServletException e) {
			e.printStackTrace();
		}

		try ( InputStream in = new FileInputStream(sc.getServletContext().getRealPath("/WPQRM.properties"))){			
			try {
				configProp.load(in);
				configProp.put("REPORT_PATH", sc.getServletContext().getRealPath("/reports").replace("\\", "/")+"\\");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch ( Exception e) {
			e.printStackTrace();
		}

		props = configProp;

		hostURLReportAudit = props.getProperty("HOSTURLREPORTAUDIT");
		hostURLAdmin = props.getProperty("HOSTURLSITEADMIN");
		hostUser = props.getProperty("HOSTUSER");
		hostPass = props.getProperty("HOSTUSERPASS");
		hibernateDialect = props.getProperty("HIBERNATEDIALECT");
		hostDriverClass = props.getProperty("HOSTDRIVERCLASS");
		demoID = props.getProperty("DEMOID");
		demoKey = props.getProperty("DEMOKEY");
		testID = props.getProperty("TESTID");
		testKey = props.getProperty("TESTKEY");
		testName = props.getProperty("TESTNAME");

		adminConfig = new Configuration()
				.setProperty("hibernate.connection.driver_class",hostDriverClass)
				.setProperty("hibernate.hbm2ddl.auto", "update")
				.setProperty("hibernate.connection.url",hostURLAdmin)
				.setProperty("hibernate.connection.username",hostUser)
				.setProperty("hibernate.connection.password",hostPass)
				.setProperty("hibernate.dialect", hibernateDialect)
				.addAnnotatedClass(ClientSites.class);

		auditConfig = new Configuration()
				.setProperty("hibernate.connection.driver_class",hostDriverClass)
				.setProperty("hibernate.hbm2ddl.auto", "update")
				.setProperty("hibernate.connection.url",hostURLReportAudit)
				.setProperty("hibernate.connection.username",hostUser)
				.setProperty("hibernate.connection.password",hostPass)
				.setProperty("hibernate.dialect", hibernateDialect)
				.addAnnotatedClass(ReportJob.class)
				.addAnnotatedClass(AvailableReport.class);

		List<AvailableReport> reports = null;					
		Session auditsess = getAuditSession();

		try {
			reports =  auditsess.createCriteria(AvailableReport.class).list();
			if (reports.isEmpty()){
				initReports();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			auditsess.close();
		}

		if (!checkSiteKey(testKey, testID)){
			Session s = getAdminSession();
			ClientSites site = new ClientSites();
			site.siteID = testID;
			site.siteKey = testKey;
			site.siteName = testName;
			Transaction txn = s.beginTransaction();
			s.saveOrUpdate(site);
			txn.commit();
			s.close();
		}

		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Integer.class, new IntegerDeserializer());
		builder.registerTypeAdapter(Double.class, new DoubleDeserializer());
		gson = builder.create();
	}

	private void initReports() {

		Gson gson = new Gson();

		final ReportSet reports = gson.fromJson(configProp.getProperty("REPORTS"), ReportSet.class);		
		final Session auditsess = getAuditSession();

		auditsess.doWork(
				new Work() {
					@Override
					public void execute(java.sql.Connection arg0){
						try {
							Transaction txn = auditsess.beginTransaction();
							for (AvailableReport report:reports.reports){
								auditsess.saveOrUpdate(report);	
							}
							txn.commit();
						} catch (Exception e) {
							e.printStackTrace();
						}							
					}
				}
				);

	}

	private Session getAuditSession(){
		if (sfAudit == null){
			StandardServiceRegistryBuilder serviceRegistryBuilder = new StandardServiceRegistryBuilder();
			serviceRegistryBuilder.applySettings(auditConfig.getProperties());
			ServiceRegistry serviceRegistry = serviceRegistryBuilder.build();
			sfAudit = auditConfig.buildSessionFactory(serviceRegistry);
		}
		return sfAudit.openSession();
	}

	private Session getAdminSession(){
		if (sfAdmin == null){
			StandardServiceRegistryBuilder serviceRegistryBuilder = new StandardServiceRegistryBuilder();
			serviceRegistryBuilder.applySettings(adminConfig.getProperties());
			ServiceRegistry serviceRegistry = serviceRegistryBuilder.build();
			sfAdmin = adminConfig.buildSessionFactory(serviceRegistry);
		}
		return sfAdmin.openSession();
	}
}
