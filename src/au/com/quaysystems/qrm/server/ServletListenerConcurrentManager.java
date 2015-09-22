package au.com.quaysystems.qrm.server;

import java.io.FileInputStream;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ServletListenerConcurrentManager implements ServletContextListener {

	private static final Properties configProp = new Properties();

	public static WorkQueue reportQueue;
	public static  ServletContext context;
	public ServletListenerConcurrentManager(){	}

	public void contextInitialized(final ServletContextEvent se) {

		try {
			configProp.load(new FileInputStream(se.getServletContext().getRealPath("/WPQRM.properties")));
		} catch (Exception e) {
			e.printStackTrace();
		}

		String reportThreads = configProp.getProperty("NUM_REPORT_THREADS");
		if (reportThreads == null){
			reportThreads = "2";
		}
		
		context = se.getServletContext();
		reportQueue = new WorkQueue(Integer.parseInt(reportThreads));
	}
	public void contextDestroyed(final ServletContextEvent arg0) {}
}
