package au.com.quaysystems.qrm.server;

public class AsyncMessage implements Runnable{

	private String reportData;
	private String reportID;
	private String sessionToken;

	public AsyncMessage(String reportData, String reportID, String sessionToken ){
		this.reportData = reportData;
		this.reportID = reportID;
		this.sessionToken = sessionToken;
	}
	public void send(){
			ServletListenerConcurrentManager.reportQueue.execute(this);
	}

	@Override
	public void run() {
		// The run() method is called by the PoolThread which is assigned to execute this runnable
			ReportProcessor rp = new ReportProcessor(ServletListenerConcurrentManager.context);
			rp.deliver(reportData, reportID, sessionToken);
	}
}
