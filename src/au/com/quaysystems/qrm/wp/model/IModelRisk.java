package au.com.quaysystems.qrm.wp.model;


import java.util.ArrayList;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@MappedSuperclass
public class IModelRisk extends IModelRiskLite {


	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1334742238396972117L;


	public Long promotedProjectID;
	@Temporal(TemporalType.DATE)
	public Date dateEntered;
	@Temporal(TemporalType.DATE)
	public Date dateUpdated;
	public Long idEvalApp;
	public Long idEvalRev;
	public Long idIDApp;
	public Long idIDRev;
	public Long idMitApp;
	public Long idMitPrep;
	public Long idMitRev;
	@Transient
	public int estMitCost;
	public boolean impCost;
	public boolean impEnvironment;
	public boolean impReputation;
	public boolean impSafety;
	public boolean impSpec;
	public boolean impTime;
	public Double likealpha;
	public Double likepostAlpha;
	public Double likepostProb;
	public Double likepostT;
	public Integer likepostType;
	public Double likeprob;
	public Double liket;
	public Integer liketype;
	public Long manager2ID;
	@Column(name = "manager2Name", updatable = false, insertable = false)
	public String manager2Name;
	public Long manager3ID;
	@Column(name = "manager3Name", updatable = false, insertable = false)
	public String manager3Name;
	@Transient
	public ModelToleranceMatrix matrix;
	@Transient
	public int mean;
	@Transient
	public boolean userUpdateSecurity;
	@Transient
	public ArrayList<Long> objectivesImpacted;
	public boolean treatmentAvoidance;
	public boolean treatmentReduction;
	public boolean treatmentRetention;
	public boolean treatmentTransfer;
	

	public IModelRisk() {
	}
}
