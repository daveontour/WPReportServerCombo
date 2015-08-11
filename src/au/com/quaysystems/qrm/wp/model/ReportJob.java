package au.com.quaysystems.qrm.wp.model;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.annotations.Expose;

@Entity
@Table( name="REPORTJOB" )
public class ReportJob {
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	@Expose
	public Long id;
	public String userEmail;
	@Expose
	public String reportTitle; 
	public String siteID;
	public String siteKey;
	public String userDisplayName;
	public String siteName;
	public String userLogin;
	@Expose
	public Date submittedDate;
	@Expose
	public Date completedDate;
	@Expose
	public Boolean completed = false;
	public String reportName;
	public String reportID;
	@Lob
	public byte[] reportResult;
	public String ip;

}