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
import org.hibernate.criterion.Restrictions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import au.com.quaysystems.qrm.wp.model.AvailableReport;

@SuppressWarnings("serial")
public class ServletGetAvailableReports  extends HttpServlet{


	@SuppressWarnings("unchecked")
	public void service(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {

		System.out.println(">>> Get Available Report");

		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		List<AvailableReport> reports = null;					
		Session adminsess = PersistenceUtils.getAdminSession();
		String siteKey = req.getParameter("siteKey");

		System.out.println(">>> Get Available Report DB");

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
			System.out.println("<<< Get Available Report DB");
		}


		String callback= req.getParameter("callback");
		String json = gson.toJson(reports);
		String res = callback+"("+json+")";

		response.setContentType("text/javascript");
		PrintWriter out = response.getWriter();
		out.println(res);

		System.out.println("<<< Get Available Report");

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
