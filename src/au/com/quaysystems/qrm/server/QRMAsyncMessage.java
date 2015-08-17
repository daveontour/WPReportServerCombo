package au.com.quaysystems.qrm.server;

public class QRMAsyncMessage implements Runnable{

	private String CHANNEL;
	private String reportData;
	private String reportID;
	private String ipAddress;

	public QRMAsyncMessage(String channel,  String reportData, String reportID, String ipAddress ){
		this.CHANNEL = channel;
		this.reportData = reportData;
		this.reportID = reportID;
		this.ipAddress = ipAddress;
	}
	public void send(){
		if (CHANNEL.endsWith("reportChannel")){
			ConcurrentManager.reportQueue.execute(this);
		}
	}

	@Override
	public void run() {

		// The run() method is called by the PoolThread which is assigned to execute this runnable
		if (CHANNEL.endsWith("reportChannel")){
			ConcurrentManager.repProcessor.deliver(reportData, reportID, ipAddress);
		}
	}
}
