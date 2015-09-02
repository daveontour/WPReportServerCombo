package au.com.quaysystems.qrm.server;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import au.com.quaysystems.qrm.wp.model.ClientSites;

@SuppressWarnings("serial")
public class ServletClientManager  extends HttpServlet{



	public void service(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {

		String action = req.getParameter("action");

		if (action.equalsIgnoreCase("new30dayorder")){
			newOrder30DayOrder(req, response);
			return;
		}
		if (action.equalsIgnoreCase("cancelorder")){
			cancelOrder(req, response);
			return;
		}
	}
	public void cancelOrder(HttpServletRequest req, HttpServletResponse response){
		
		System.out.println("Cancel ORDER");
		
		Session sess = PersistenceUtils.getAdminSession();
		
		try {
			sess.beginTransaction();

			@SuppressWarnings("rawtypes")
			ClientSites site = (ClientSites) sess.createCriteria(ClientSites.class)
			.add(Restrictions.eq("orderID",req.getParameter("orderID")))
			.uniqueResult();		
			site.validUntil = null;		
			sess.update(site);
			
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
	public void newOrder30DayOrder(HttpServletRequest req, HttpServletResponse response){
		
		System.out.println("NEW 30 DAY ORDER");
		
		Session sess = PersistenceUtils.getAdminSession();
		
		Date now = new Date();
		Date valid = new Date(now.getTime()+(31L*24L*60L*60L*1000L));
		System.out.println(now);
		System.out.println(valid);
		
		try {
			sess.beginTransaction();
			ClientSites site = new ClientSites();
			site.orderEmail = req.getParameter("orderEmail");
			site.orderID = req.getParameter("orderID");
			site.siteID = req.getParameter("siteID");
			site.siteKey = req.getParameter("siteKey");
			site.orderDate = new Date().toString();
			site.validUntil = valid;
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
