package au.com.quaysystems.qrm.server;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class ServletGetReport  extends HttpServlet{

	private String hostUser;
	private String hostPass;
	private String hostURLReportAudit;

	public void service(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {

		System.out.println(">>> Get Report");
		String userEmail = req.getParameter("userEmail");
		String userLogin = req.getParameter("userLogin");
		String siteKey = req.getParameter("siteKey");
		String id = req.getParameter("id");
		String sessionToken = req.getParameter("sessionToken"); 
		
		try {
			Connection conn = DriverManager.getConnection(hostURLReportAudit, hostUser, hostPass);
			try {

				ResultSet res = conn.createStatement().executeQuery("SELECT reportResult FROM reportjob WHERE id = "+id+ " AND userEmail = '"+ userEmail+"' AND siteKey='"+siteKey+"' AND userLogin='"+userLogin+"'");
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
				byteArrayStream.close();
				response.getOutputStream().close();

			} catch (Exception e) {
				e.printStackTrace();
				ServletUserMessageManager.notifyUserMessage(userEmail, "Error Retreiving Report.", sessionToken);
			} finally {
				conn.close();
			}
		} catch (SQLException e) {
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
		
		try ( InputStream in = new FileInputStream(sc.getServletContext().getRealPath("/WPQRM.properties"))){			
			try {
				Properties configProp = new Properties();
				configProp.load(in);
				configProp.put("REPORT_PATH", sc.getServletContext().getRealPath("/reports").replace("\\", "/")+"\\");
				hostUser = configProp.getProperty("HOSTUSER");
				hostPass = configProp.getProperty("HOSTUSERPASS");
				hostURLReportAudit = configProp.getProperty("HOSTURLREPORTAUDIT");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch ( Exception e) {
			e.printStackTrace();
		}
	}
}
