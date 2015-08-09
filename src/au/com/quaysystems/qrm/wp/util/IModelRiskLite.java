package au.com.quaysystems.qrm.wp.util;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@SuppressWarnings("serial")
@MappedSuperclass
public class IModelRiskLite  extends IModelRiskLiteBasic {

//	@Column(updatable=false, insertable=false)
	public Double preMitContingencyWeighted;
//	@Column(updatable=false, insertable=false)
	public Double postMitContingencyWeighted;
	@Temporal(TemporalType.DATE)
	public Date dateEvalApp;
	@Temporal(TemporalType.DATE)
	public Date dateEvalRev;
	@Temporal(TemporalType.DATE)
	public Date dateIDApp;
	@Temporal(TemporalType.DATE)
	public Date dateIDRev;
	@Temporal(TemporalType.DATE)
	public Date dateMitApp;
	@Temporal(TemporalType.DATE)
	public Date dateMitPrep;
	@Temporal(TemporalType.DATE)
	public Date dateMitRev;
	@Column(updatable=false, insertable=false)
	public String manager1Name;
	@Column(updatable=false, insertable=false)
	public String ownerName;
	@Column(updatable=false, insertable=false)
	public String primCatName;
	@Column(updatable=false, insertable=false)
	public String secCatName;
	@Transient
	public Double currentImpact;
	@Transient
	public Double currentProb;
	@Transient
	public Integer currentTolerance;
	
	public IModelRiskLite(){}

	public void setCurrentProbImpact(){
		if (this.treated) {
			currentImpact = treatedImpact;
			currentProb = treatedProb;
			currentTolerance = treatedTolerance;
		} else {
			currentImpact = inherentImpact;
			currentProb = inherentProb;
			currentTolerance = inherentTolerance;
		}
	}

	public Double getPreMitContingencyWeighted() {
		return preMitContingencyWeighted;
	}

	public void setPreMitContingencyWeighted(Double preMitContingencyWeighted) {
		this.preMitContingencyWeighted = preMitContingencyWeighted;
	}

	public Double getPostMitContingencyWeighted() {
		return postMitContingencyWeighted;
	}

	public void setPostMitContingencyWeighted(Double postMitContingencyWeighted) {
		this.postMitContingencyWeighted = postMitContingencyWeighted;
	}

	public Date getDateEvalApp() {
		return dateEvalApp;
	}

	public void setDateEvalApp(Date dateEvalApp) {
		this.dateEvalApp = dateEvalApp;
	}

	public Date getDateEvalRev() {
		return dateEvalRev;
	}

	public void setDateEvalRev(Date dateEvalRev) {
		this.dateEvalRev = dateEvalRev;
	}

	public Date getDateIDApp() {
		return dateIDApp;
	}

	public void setDateIDApp(Date dateIDApp) {
		this.dateIDApp = dateIDApp;
	}

	public Date getDateIDRev() {
		return dateIDRev;
	}

	public void setDateIDRev(Date dateIDRev) {
		this.dateIDRev = dateIDRev;
	}

	public Date getDateMitApp() {
		return dateMitApp;
	}

	public void setDateMitApp(Date dateMitApp) {
		this.dateMitApp = dateMitApp;
	}

	public Date getDateMitPrep() {
		return dateMitPrep;
	}

	public void setDateMitPrep(Date dateMitPrep) {
		this.dateMitPrep = dateMitPrep;
	}

	public Date getDateMitRev() {
		return dateMitRev;
	}

	public void setDateMitRev(Date dateMitRev) {
		this.dateMitRev = dateMitRev;
	}

	public String getManager1Name() {
		return manager1Name;
	}

	public void setManager1Name(String manager1Name) {
		this.manager1Name = manager1Name;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getPrimCatName() {
		return primCatName;
	}

	public void setPrimCatName(String primCatName) {
		this.primCatName = primCatName;
	}

	public String getSecCatName() {
		return secCatName;
	}

	public void setSecCatName(String secCatName) {
		this.secCatName = secCatName;
	}
}
