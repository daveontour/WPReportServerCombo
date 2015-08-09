package au.com.quaysystems.qrm.wp.util;


import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/*
 *   IModelRiskLiteBasic, IModelRiskLite and IModelRisk are a heirarchy of superclasses that build upon each
 *   other. 
 *   
 *   They use @MappedSuperclass so that the data stored in a single table can be retrieved in 
 *   whichever of the three classes using the corresponding "*Model" which extends the MappedSuperClass 
 *   without modification, but is also defined as an entity.
 */

@MappedSuperclass
public class IModelRiskLiteBasic  implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1334742238396972117L;

	public Double estimatedContingencey = 0.0;
	public boolean useCalculatedContingency = false;
//	@Column(updatable=false, insertable=false)
	public Double postMitContingency = 0.0;
//	@Column(updatable=false, insertable=false)
	public Double preMitContingency = 0.0;
//	@Column(updatable=false, insertable=false)
	public Double contingencyPercentile = 0.0;
 	@Column(updatable=false, insertable=false)
  	public Double mitigationCost = 0.0;
	public boolean active;
	@Temporal(TemporalType.DATE)
	public Date startExposure;
	public String cause;
	public String consequences;
	public String description;
	@Temporal(TemporalType.DATE)
	public Date endExposure;
	@Id
	@GeneratedValue
	public Long riskID;
	public String externalID;
	public Long manager1ID;
	public Long ownerID;
	public Long primCatID;
	public Long projectID;
	public String riskProjectCode;
	public Long secCatID;
	public boolean summaryRisk;
	public String title;
	public boolean treated;
	public boolean forceDownParent;
	public boolean forceDownChild;
	public Double treatedImpact;
	public Double treatedProb;
	public Integer treatedTolerance;
	public String extObject;
	public Double inherentImpact;
	public Double inherentProb;
	public Integer inherentTolerance;
	@Transient
	public Long tempIndex;
	@Transient
	public Long relationshipID;
	@Transient
	public Long parentSummaryRisk;
	@Transient
	public Long origParentSummaryRisk;
	public Integer securityLevel;
	public Long matrixID;
	public String mitPlanSummary;
	public String mitPlanSummaryUpdate;
	public String impact;

	public IModelRiskLiteBasic(){}


	public Double getEstimatedContingencey() {
		return estimatedContingencey;
	}
	public void setEstimatedContingencey(Double estimatedContingencey) {
		this.estimatedContingencey = estimatedContingencey;
	}
	public boolean isUseCalculatedContingency() {
		return useCalculatedContingency;
	}
	public void setUseCalculatedContingency(boolean useCalculatedContingency) {
		this.useCalculatedContingency = useCalculatedContingency;
	}
	public Double getPostMitContingency() {
		return postMitContingency;
	}
	public void setPostMitContingency(Double postMitContingency) {
		this.postMitContingency = postMitContingency;
	}
	public Double getPreMitContingency() {
		return preMitContingency;
	}
	public void setPreMitContingency(Double preMitContingency) {
		this.preMitContingency = preMitContingency;
	}
	public Double getContingencyPercentile() {
		return contingencyPercentile;
	}
	public void setContingencyPercentile(Double contingencyPercentile) {
		this.contingencyPercentile = contingencyPercentile;
	}
	public Double getMitigationCost() {
		return mitigationCost;
	}
	public void setMitigationCost(Double mitigationCost) {
		this.mitigationCost = mitigationCost;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public Date getStartExposure() {
		return startExposure;
	}
	public void setStartExposure(Date startExposure) {
		this.startExposure = startExposure;
	}
	public String getCause() {
		return cause;
	}
	public void setCause(String cause) {
		this.cause = cause;
	}
	public String getConsequences() {
		return consequences;
	}
	public void setConsequences(String consequences) {
		this.consequences = consequences;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getEndExposure() {
		return endExposure;
	}
	public void setEndExposure(Date endExposure) {
		this.endExposure = endExposure;
	}
	public void setRiskID(Long riskID) {
		this.riskID = riskID;
	}
	public Long getManager1ID() {
		return manager1ID;
	}
	public void setManager1ID(Long manager1id) {
		manager1ID = manager1id;
	}
	public Long getOwnerID() {
		return ownerID;
	}
	public void setOwnerID(Long ownerID) {
		this.ownerID = ownerID;
	}
	public Long getPrimCatID() {
		return primCatID;
	}
	public void setPrimCatID(Long primCatID) {
		this.primCatID = primCatID;
	}
	public Long getProjectID() {
		return projectID;
	}
	public void setProjectID(Long projectID) {
		this.projectID = projectID;
	}
	public String getRiskProjectCode() {
		return riskProjectCode;
	}
	public void setRiskProjectCode(String riskProjectCode) {
		this.riskProjectCode = riskProjectCode;
	}
	public Long getSecCatID() {
		return secCatID;
	}
	public void setSecCatID(Long secCatID) {
		this.secCatID = secCatID;
	}
	public boolean isSummaryRisk() {
		return summaryRisk;
	}
	public void setSummaryRisk(boolean summaryRisk) {
		this.summaryRisk = summaryRisk;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public boolean isTreated() {
		return treated;
	}
	public void setTreated(boolean treated) {
		this.treated = treated;
	}
	public Double getTreatedImpact() {
		return treatedImpact;
	}
	public void setTreatedImpact(Double treatedImpact) {
		this.treatedImpact = treatedImpact;
	}
	public Double getTreatedProb() {
		return treatedProb;
	}
	public void setTreatedProb(Double treatedProb) {
		this.treatedProb = treatedProb;
	}
	public Integer getTreatedTolerance() {
		return treatedTolerance;
	}
	public void setTreatedTolerance(Integer treatedTolerance) {
		this.treatedTolerance = treatedTolerance;
	}
	public String getExtObject() {
		return extObject;
	}
	public void setExtObject(String extObject) {
		this.extObject = extObject;
	}
	public Double getInherentImpact() {
		return inherentImpact;
	}
	public void setInherentImpact(Double inherentImpact) {
		this.inherentImpact = inherentImpact;
	}
	public Double getInherentProb() {
		return inherentProb;
	}
	public void setInherentProb(Double inherentProb) {
		this.inherentProb = inherentProb;
	}
	public Integer getInherentTolerance() {
		return inherentTolerance;
	}
	public void setInherentTolerance(Integer inherentTolerance) {
		this.inherentTolerance = inherentTolerance;
	}
//	public Long getParentSummaryRisk() {
//		return parentSummaryRisk;
//	}
//	public void setParentSummaryRisk(Long parentSummaryRisk) {
//		this.parentSummaryRisk = parentSummaryRisk;
//	}
	public Integer getSecurityLevel() {
		return securityLevel;
	}
	public void setSecurityLevel(Integer securityLevel) {
		this.securityLevel = securityLevel;
	}
	public Long getMatrixID() {
		return matrixID;
	}
	public void setMatrixID(Long matrixID) {
		this.matrixID = matrixID;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	public String getExternalID() {
		return externalID;
	}


	public void setExternalID(String externalID) {
		this.externalID = externalID;
	}


	public String getMitPlanSummary() {
		return mitPlanSummary;
	}


	public void setMitPlanSummary(String mitPlanSummary) {
		this.mitPlanSummary = mitPlanSummary;
	}


	public String getImpact() {
		return impact;
	}


	public void setImpact(String impact) {
		this.impact = impact;
	}


	public String getMitPlanSummaryUpdate() {
		return mitPlanSummaryUpdate;
	}


	public void setMitPlanSummaryUpdate(String mitPlanSummaryUpdate) {
		this.mitPlanSummaryUpdate = mitPlanSummaryUpdate;
	}


	public boolean isForceDownParent() {
		return forceDownParent;
	}


	public void setForceDownParent(boolean forceDownParent) {
		this.forceDownParent = forceDownParent;
	}


	public boolean isForceDownChild() {
		return forceDownChild;
	}


	public void setForceDownChild(boolean forceDownChild) {
		this.forceDownChild = forceDownChild;
	}
}
