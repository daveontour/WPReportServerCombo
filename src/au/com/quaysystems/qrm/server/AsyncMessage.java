package au.com.quaysystems.qrm.server;

public class AsyncMessage implements Runnable{

	private String CHANNEL;
	private String reportData;
	private String reportID;
	private String ipAddress;

	public AsyncMessage(String channel,  String reportData, String reportID, String ipAddress ){
		this.CHANNEL = channel;
		this.reportData = reportData;
		this.reportID = reportID;
		this.ipAddress = ipAddress;
	}
	public void send(){
		if (CHANNEL.endsWith("reportChannel")){
			ServletListenerConcurrentManager.reportQueue.execute(this);
		}
	}

	@Override
	public void run() {

		// The run() method is called by the PoolThread which is assigned to execute this runnable
		if (CHANNEL.endsWith("reportChannel")){
			ServletListenerConcurrentManager.repProcessor.deliver(reportData, reportID, ipAddress);
		}
	}
}
