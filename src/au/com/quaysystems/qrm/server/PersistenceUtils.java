package au.com.quaysystems.qrm.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletConfig;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.hibernate.service.ServiceRegistry;

import com.google.gson.Gson;

import au.com.quaysystems.qrm.wp.model.AvailableReport;
import au.com.quaysystems.qrm.wp.model.ClientSites;
import au.com.quaysystems.qrm.wp.model.ReportJob;
import au.com.quaysystems.qrm.wp.model.ReportSet;

public class PersistenceUtils {
	
	private static Properties configProp = new Properties();
	private static Configuration auditConfig;
	private static Configuration adminConfig;
	private static Properties props;
	private static String hostUser;
	private static String hostPass;
	private static String hibernateDialect;
	private static String hostDriverClass;
	private static String hostURLReportAudit;
	private static String hostURLAdmin;
	private static String testID;
	private static String testKey;
	private static String testName;
	private static SessionFactory sfAdmin;
	private static SessionFactory sfAudit;
	private static boolean CACHE_BYPASS = false;
	private static boolean init = false;

	
	@SuppressWarnings("unchecked")
	public static void init(final ServletConfig sc) {
		
		if (init){
			return;
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
		testID = props.getProperty("TESTID");
		testKey = props.getProperty("TESTKEY");
		testName = props.getProperty("TESTNAME");
		CACHE_BYPASS   = Boolean.parseBoolean(configProp.getProperty("CACHE_BYPASS", "false"));

		
		adminConfig = new Configuration()
				.setProperty("hibernate.connection.driver_class",hostDriverClass)
				.setProperty("hibernate.hbm2ddl.auto", "update")
				.setProperty("hibernate.connection.url",hostURLAdmin)
				.setProperty("hibernate.connection.username",hostUser)
				.setProperty("hibernate.connection.password",hostPass)
				.setProperty("hibernate.dialect", hibernateDialect)
				.setProperty("hibernate.cache.use_query_cache", "false")
				.setProperty("hibernate.cache.use_second_level_cache", "false")
				.setProperty("hibernate.cache.use_structured_entries", "false")
				.setProperty("hibernate.cache.use_query_cache", "false")
				.setProperty("hibernate.show_sql", props.getProperty("DEBUG"))
				.addAnnotatedClass(ClientSites.class)
				.addAnnotatedClass(AvailableReport.class);

		auditConfig = new Configuration()
				.setProperty("hibernate.connection.driver_class",hostDriverClass)
				.setProperty("hibernate.hbm2ddl.auto", "update")
				.setProperty("hibernate.connection.url",hostURLReportAudit)
				.setProperty("hibernate.connection.username",hostUser)
				.setProperty("hibernate.connection.password",hostPass)
				.setProperty("hibernate.dialect", hibernateDialect)
				.setProperty("hibernate.cache.use_second_level_cache", "false")
				.setProperty("hibernate.cache.use_structured_entries", "false")
				.setProperty("hibernate.cache.use_query_cache", "false")
				.setProperty("hibernate.show_sql", props.getProperty("DEBUG"))
				.addAnnotatedClass(ReportJob.class);
				

		List<AvailableReport> reports = null;					
		Session adminsess = getAdminSession();

		try {
			reports =  adminsess.createCriteria(AvailableReport.class).list();
			if (reports.isEmpty()){
				initReports();
			}
			if (!checkSiteKey(testKey, testID)){
				ClientSites site = new ClientSites();
				site.siteID = testID;
				site.siteKey = testKey;
				site.siteName = testName;
				Transaction txn = adminsess.beginTransaction();
				adminsess.saveOrUpdate(site);
				txn.commit();
			} 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			adminsess.close();
		}
		
		init = true;
	}

	private static void initReports() {

		Gson gson = new Gson();
		ReportSet reports = gson.fromJson(configProp.getProperty("REPORTS"), ReportSet.class);		
		Session adminsess = getAdminSession();
		
		try {
			Transaction txn = adminsess.beginTransaction();
			for (AvailableReport report:reports.reports){
				adminsess.saveOrUpdate(report);	
			}
			txn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Session getAdminSession(){
		if (sfAdmin == null || CACHE_BYPASS) {
			StandardServiceRegistryBuilder serviceRegistryBuilder = new StandardServiceRegistryBuilder();
			serviceRegistryBuilder.applySettings(adminConfig.getProperties());
			ServiceRegistry serviceRegistry = serviceRegistryBuilder.build();
			sfAdmin = adminConfig.buildSessionFactory(serviceRegistry);
		}
		return sfAdmin.openSession();
	}

	public static Session getAuditSession(){
		if (sfAudit == null || CACHE_BYPASS) {
			StandardServiceRegistryBuilder serviceRegistryBuilder = new StandardServiceRegistryBuilder();
			serviceRegistryBuilder.applySettings(auditConfig.getProperties());
			ServiceRegistry serviceRegistry = serviceRegistryBuilder.build();
			sfAudit = auditConfig.buildSessionFactory(serviceRegistry);
		}
		return sfAudit.openSession();
	}
	
	public static boolean checkSiteKey(String siteKey, String siteID){
		Session sess = getAdminSession();
		@SuppressWarnings("rawtypes")
		List sites = sess.createCriteria(ClientSites.class)
		.add(Restrictions.eq("siteKey",siteKey))
		.add(Restrictions.eq("siteID",siteID))
		.list();
		return sites.size() == 1;
	}
}
