package au.com.quaysystems.qrm.server;

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
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import au.com.quaysystems.qrm.wp.model.AvailableReport;
import au.com.quaysystems.qrm.wp.model.QRMImport;
import au.com.quaysystems.qrm.wp.model.ReportJob;

@SuppressWarnings("serial")
public class ServletReportProcessor  extends HttpServlet{

	private String hostUser;
	private String hostPass;
	private String hostURLReportAudit;
	private String demoID;
	private String demoKey;
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
		if (action.equalsIgnoreCase("neworder")){
			System.out.println("New Order Received");
			response.getWriter().print("OrederReceived");
			return;
		}
	}
	
	public void serviceReport(HttpServletRequest req, HttpServletResponse response) throws IOException  {
		try {
			String reportData = req.getParameter("reportData");
			String reportID = req.getParameter("reportID");
			String ipAddress = req.getHeader("X-FORWARDED-FOR"); 
			
			if (ipAddress == null) {  
				ipAddress = req.getRemoteAddr();  
			}
			
			final QRMImport imp = gson.fromJson(reportData, QRMImport.class);			
			boolean registeredSite = PersistenceUtils.checkSiteKey(imp.siteKey, imp.siteID);
			
			if (!registeredSite){
				// site is not registered, so check if has the correct demo keys
				if (!(imp.siteKey.equalsIgnoreCase(demoKey) && imp.siteID.equalsIgnoreCase(demoID))){
					ServletUserMessageManager.notifyUserMessage(imp.userEmail, "Your site is unregistrerd and the demo key and ID are incorrect. Report not processed",3000,ipAddress);
					return;
				}
			}
			
			try {
				AsyncMessage message = new AsyncMessage("reportChannel", reportData, reportID, ipAddress);
				message.send();
				ServletUserMessageManager.notifyUserMessage(imp.userEmail, "Report Queued for Execution",1500,ipAddress);
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
		String ipAddress = req.getHeader("X-FORWARDED-FOR"); 
		
		if (ipAddress == null) {  
			ipAddress = req.getRemoteAddr();  
		}
		
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
			byteArrayStream.close();
			response.getOutputStream().close();

		} catch (Exception e) {
			e.printStackTrace();
			ServletUserMessageManager.notifyUserMessage(userEmail, "Error Retreiving Report.", 3000, ipAddress);
		}
	}

	public void removeReport(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {

		//Doesn't actually remove the record. Contents are nulled out and marked not to be shown to user. Kept for auditting and billing purposes
		String userEmail = req.getParameter("userEmail");
		String userLogin = req.getParameter("userLogin");
		String siteKey = req.getParameter("siteKey");
		String id = req.getParameter("id");

		final Session auditsess = PersistenceUtils.getAuditSession();

		try {

			Transaction txn = auditsess.beginTransaction();
			ReportJob job = (ReportJob) auditsess.createCriteria(ReportJob.class)
					.add(Restrictions.eq("id",Long.parseLong(id)))
					.add(Restrictions.eq("siteKey",siteKey))
					.add(Restrictions.eq("userEmail",userEmail))
					.add(Restrictions.eq("userLogin",userLogin))
					.uniqueResult();
			job.showUser = false;
			job.reportResult = null;
			auditsess.update(job);
			txn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		auditsess.close();

		getUserReports( req, response);
	}
	@SuppressWarnings("unchecked")
	public void getUserReports(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {

		String userEmail = req.getParameter("userEmail");
		String userLogin = req.getParameter("userLogin");
		String siteKey = req.getParameter("siteKey");
		String siteID = req.getParameter("siteID");
		String callback= req.getParameter("callback");

		if (!PersistenceUtils.checkSiteKey(siteKey, siteID)){
			String res = callback+"({'error':'Your site has not been registered. You can generate reports but they will not be archived'})";
			response.setContentType("text/javascript");
			PrintWriter out = response.getWriter();
			out.println(res);
			return;
		}

		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		List<ReportJob> job = null;
		Session auditsess = PersistenceUtils.getAuditSession();

		try {

			job =  auditsess.createCriteria(ReportJob.class)
					.add(Restrictions.eq("siteKey",siteKey))
					.add(Restrictions.eq("userEmail",userEmail))
					.add(Restrictions.eq("userLogin",userLogin))
					.add(Restrictions.eq("showUser",true))
					.list();
			
		} catch (Exception e) {
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

	@SuppressWarnings("unchecked")
	public void getAvailableReports(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {

		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		List<AvailableReport> reports = null;					
		Session adminsess = PersistenceUtils.getAdminSession();
		String siteKey = req.getParameter("siteKey");

		try {
			reports =  adminsess.createCriteria(AvailableReport.class)
					.add(Restrictions.or(
							Restrictions.eq("publicReport", true),
							Restrictions.and(Restrictions.eq("publicReport", false),Restrictions.eq("privateReportSiteID", siteKey))
					)).list();		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			adminsess.close();
		}

		String callback= req.getParameter("callback");
		String json = gson.toJson(reports);
		String res = callback+"("+json+")";

		response.setContentType("text/javascript");
		PrintWriter out = response.getWriter();
		out.println(res);
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
				demoID = configProp.getProperty("DEMOID");
				demoKey = configProp.getProperty("DEMOKEY");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch ( Exception e) {
			e.printStackTrace();
		}

		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Integer.class, new IntegerDeserializer());
		builder.registerTypeAdapter(Double.class, new DoubleDeserializer());
		gson = builder.create();
	}
}
