package au.com.quaysystems.qrm.server.wpreport;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;


/*
 *  Each time the client issues a poll request, the time is recorded so that 
 *  stale polling sessions can be cleaned up by a timer task. 
 */
@SuppressWarnings("serial")
public class ServletUserMessageManager extends HttpServlet{


	private Properties configProp = new Properties();
	private int sessionTimeout;
	public static ConcurrentHashMap<String, Member> sessionMemberMap = new ConcurrentHashMap<String, Member>();
	private static  Map<String, AsyncContext> asyncContexts = new ConcurrentHashMap<String, AsyncContext>();
	private static Logger log = Logger.getLogger("au.com.quaysystems.qrm");
	static Integer CLEANUP_INITIAL_DELAY = 60000;
	static Integer CLEANUP_FREQUENCY = 60000;
	static Integer POLL_NO_REFRESH_TIMEOUT = 90000;

	@Override
	public void init(final ServletConfig sc){
		SOP("User Message Manager Processor Started");

		InputStream in;
		try {
			in = new FileInputStream(sc.getServletContext().getRealPath("/QRM.properties"));
			try {
				configProp.load(in);
			} catch (IOException e) {
				log.error("QRM Stack Trace", e);
			}
		} catch (FileNotFoundException e1) {
			log.error("QRM Stack Trace", e1);
		}


		try {

			CLEANUP_INITIAL_DELAY = Integer.parseInt(configProp.getProperty("CLEANUP_INITIAL_DELAY",CLEANUP_INITIAL_DELAY.toString()));
			CLEANUP_FREQUENCY = Integer.parseInt(configProp.getProperty("CLEANUP_FREQUENCY",CLEANUP_FREQUENCY.toString()));
			POLL_NO_REFRESH_TIMEOUT = Integer.parseInt(configProp.getProperty("POLL_NO_REFRESH_TIMEOUT",POLL_NO_REFRESH_TIMEOUT.toString()));
			
			System.out.println("Initial delay before starting clean up task (s): "+CLEANUP_INITIAL_DELAY/1000);
			System.out.println("Frequency of clean up task (s): "+CLEANUP_FREQUENCY/1000);
			System.out.println("No Poll Refresh timeout (s): "+POLL_NO_REFRESH_TIMEOUT/1000);
			
			try {
				sessionTimeout = Integer.parseInt(configProp.getProperty("SESSION_TIMEOUT"));
			} catch (NumberFormatException e) {
				sessionTimeout = 600;
			}
			
			System.out.println("Session timeout (s): "+sessionTimeout);
			
			new Timer().schedule(new CleanUpTask(), CLEANUP_INITIAL_DELAY, CLEANUP_FREQUENCY);
		} catch (Exception e) {
			log.error("Could not start Cleanup Timer Task");
			log.error("QRM Stack Trace", e);
		}
	}

	private static void SOP(String string) {
		System.out.println(">>>>> "+string);
	}

	public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		final String sessID = req.getSession().getId();
		String ipAddress = req.getHeader("X-FORWARDED-FOR"); 
		
		if (ipAddress == null) {  
			ipAddress = req.getRemoteAddr();  
		}
		

		final String userEmail = req.getParameter("userEmail");
		Member member = sessionMemberMap.get(sessID);
		
		if (member == null){
			member = new Member();
			member.sessionID = sessID;
			member.userEmail = userEmail;
			member.callback = req.getParameter("callback");
			member.lastRequest = new Date().getTime();
			member.ip = ipAddress;
			sessionMemberMap.put(sessID, member);
		} else {
			member.callback = req.getParameter("callback");
		}

		SOP("Registering Chat Channel "+ sessID);

		final AsyncContext ctx = req.startAsync(req, res);
		asyncContexts.put(sessID, ctx);
		ctx.setTimeout(100000);
		ctx.addListener(new AsyncListener() {

			@Override
			public void onComplete(AsyncEvent event) throws IOException {
				SOP("onComplete Called");
				asyncContexts.remove(sessID);
			}

			@Override
			public void onTimeout(AsyncEvent event) throws IOException {
				try {
					AsyncContext ac = asyncContexts.get(sessID);
					if (ac != null) {
						sendMessage(ac, sessionMemberMap.get(sessID).callback+"({\"timeout\":\"true\"})");
						ac.complete();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(AsyncEvent event) throws IOException {
				asyncContexts.remove(sessID);
			}
			@Override
			public void onStartAsync(AsyncEvent event) throws IOException {
			}
		});				

		try {
			if(req.getParameter("reset").equalsIgnoreCase("true")){
				member.queue.clear();
			} else if (member.queue.size() > 0){
				String message = member.queue.poll();
				sendMessage(ctx, message);
				ctx.complete();
			}
		} catch (Exception e) {
			e.printStackTrace();
			SOP("Error in Non Zero Queue Processing");
		}
	}

	private static void sendMessage(final AsyncContext ac, final String message) throws IOException{
		if (ac != null){
			sendMessage(ac.getResponse().getWriter(), message);
		}
	}
	private static void sendMessage(PrintWriter writer, String message) throws IOException {
		writer.print(message);
		writer.flush();
	}


	public static synchronized void notifyUserMessage(String userEmail, String msg, Integer dur, String ip) throws IOException	{

		for (Member m : sessionMemberMap.values()){

			if (!m.userEmail.equalsIgnoreCase(userEmail) || !m.ip.equalsIgnoreCase(ip)){
				continue;
			}

			AsyncContext ac = asyncContexts.get(m.sessionID);
			if (ac != null){
				sendMessage(ac, m.callback+"({\"msg\":\"true\",\"message\":\""+msg+"\", \"duration\":"+dur+"})");
				ac.complete();
			} else {
				m.queue.add(m.callback+"({\"msg\":\"true\",\"message\":\""+msg+"\", \"duration\":"+dur+"})");
			}
		}
	}
	
	public static synchronized void notifyUserReportReady(String userEmail, Long id, String ip) throws IOException	{

		for (Member m : sessionMemberMap.values()){

			if (!m.userEmail.equalsIgnoreCase(userEmail) || !m.ip.equalsIgnoreCase(ip)){
				continue;
			}

			AsyncContext ac = asyncContexts.get(m.sessionID);
			if (ac != null){
				sendMessage(ac, m.callback+"({\"reportReady\":\"true\",\"reportID\":\""+id+"\", \"duration\":1000})");
				ac.complete();
			} else {
				m.queue.add(m.callback+"({\"reportReady\":\"true\",\"reportID\":\""+id+"\", \"duration\":1000})");
			}
		}
	}

	private static class CleanUpTask extends TimerTask {
		public void run() {
			try {
				
				for( String key :  (String [])sessionMemberMap.keySet().toArray(new String[sessionMemberMap.keySet().size()])) {
					
					Member m = sessionMemberMap.get(key);

					if (m != null ){
						if (m.queue.size() < 1 && asyncContexts.get(key) == null ){
							synchronized(sessionMemberMap){
								Member sessionRecord = sessionMemberMap.get(key);
								if (sessionRecord == null){
									continue;
								} else {
									expireSession(key);
								}
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void expireSession(String sessID){
		try {
			sessionMemberMap.remove(sessID);
			System.out.println("### SessionControl ###  Expiring Session "+sessID);
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}
	}
}

