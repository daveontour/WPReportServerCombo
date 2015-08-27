package au.com.quaysystems.qrm.server;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import au.com.quaysystems.qrm.wp.model.ClientSites;

@SuppressWarnings("serial")
public class ServletClientManager  extends HttpServlet{



	public void service(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {

		String action = req.getParameter("action");

		if (action.equalsIgnoreCase("neworder")){
			newOrder(req, response);
			return;
		}
	}
	
	public void newOrder(HttpServletRequest req, HttpServletResponse response){
		Session sess = PersistenceUtils.getAdminSession();
		
		try {
			sess.beginTransaction();
			ClientSites site = new ClientSites();
			site.orderEmail = req.getParameter("orderEmail");
			site.orderID = req.getParameter("orderID");
			site.siteID = req.getParameter("siteID");
			site.siteKey = req.getParameter("siteKey");
			site.orderDate = new Date().toString();
			sess.save(site);		
			sess.getTransaction().commit();
			try {
				response.getWriter().print("ok");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (Exception e) {
			try {
				response.getWriter().print("error");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} finally {
			sess.close();
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
