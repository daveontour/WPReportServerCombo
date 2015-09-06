package au.com.quaysystems.qrm.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

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

import au.com.quaysystems.qrm.wp.model.ReportJob;

@SuppressWarnings("serial")
public class ServletUserReports  extends HttpServlet{


	public void service(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
		getUserReports(req,response);
	}
	
	@SuppressWarnings("unchecked")
	public void getUserReports(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {

		System.out.println(">>> Get User Reports");
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
		System.out.println(">>> Get User Reports DB");

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
			System.out.println("<<< Get User Reports DB");
		}

		String json = gson.toJson(job);
		String res = callback+"("+json+")";

		response.setContentType("text/javascript");
		PrintWriter out = response.getWriter();
		out.println(res);

		System.out.println("<<< Get User Reports");

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
