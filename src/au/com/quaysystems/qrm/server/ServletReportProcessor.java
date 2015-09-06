package au.com.quaysystems.qrm.server;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class ServletReportProcessor  extends HttpServlet{

	
	public void service(HttpServletRequest req, HttpServletResponse response) throws IOException  {
		System.out.println(">>> Service Report");
		try {
			String reportData = req.getParameter("reportData");
			String reportID = req.getParameter("reportID");
			String reportEmail = req.getParameter("reportEmail");
			String ipAddress = req.getHeader("X-FORWARDED-FOR"); 
			
			if (ipAddress == null) {  
				ipAddress = req.getRemoteAddr();  
			}
			
			try {
				AsyncMessage message = new AsyncMessage("reportChannel", reportData, reportID, ipAddress);
				message.send();
				ServletUserMessageManager.notifyUserMessage(reportEmail, "Report Queued for Execution",1500,ipAddress);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void init(final ServletConfig sc) {
		try {
			super.init(sc);
		} catch (ServletException e) {
			e.printStackTrace();
		}
		
		PersistenceUtils.init(sc);
	}
}
