package au.com.quaysystems.qrm.server;

import java.io.FileInputStream;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ConcurrentManager implements ServletContextListener {

	private static final Properties configProp = new Properties();
	public static ReportProcessor repProcessor;

	public static WorkQueue reportQueue;
	public ConcurrentManager(){	}

	public void contextInitialized(final ServletContextEvent se) {

		try {
			configProp.load(new FileInputStream(se.getServletContext().getRealPath("/QRM.properties")));
		} catch (Exception e) {
			e.printStackTrace();
		}

		String reportThreads = configProp.getProperty("NUM_REPORT_THREADS");
		if (reportThreads == null){
			reportThreads = "2";
		}

		repProcessor = new ReportProcessor(se.getServletContext());
		reportQueue = new WorkQueue(Integer.parseInt(reportThreads));
	}
	public void contextDestroyed(final ServletContextEvent arg0) {}
}
