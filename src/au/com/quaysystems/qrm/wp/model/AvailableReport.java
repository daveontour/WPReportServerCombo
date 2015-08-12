package au.com.quaysystems.qrm.wp.model;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.google.gson.annotations.Expose;

@Entity
@Table( name="AVAILREPORT" )
public class AvailableReport {
	@Id
	@Expose
	public Long id;
	@Expose
	public String title;
	public String filename;
	@Expose
	public boolean risk = false;
	@Expose
	public boolean project = false;
	@Expose
	public boolean incident = false;
	@Expose
	public boolean review = false;
	@Expose
	public boolean req_riskMatrix = false;
	@Expose
	public boolean req_relMatrix = false;
	@Expose
	public boolean showRiskExplorer = false;
	@Expose
	public boolean showSingleRisk = false;
	@Expose
	public boolean showRank = false;
	@Expose
	public boolean showCalender = false;
	@Expose
	public boolean showRelMatrix = false;
	@Expose
	public boolean showReview = false;
	@Expose
	public boolean showIncident = false;
	@Expose
	public boolean showDashboard = false;

}