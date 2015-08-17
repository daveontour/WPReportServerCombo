package au.com.quaysystems.qrm.server.wpreport;

import java.util.LinkedList;
import java.util.Queue;

public class Member {
	public boolean expired = false;
	public String sessionID;
	public String userEmail;
	public Long lastRequest;
	public String callback;
	public Queue<String> queue = new LinkedList<String>();
	public String ip;	
}