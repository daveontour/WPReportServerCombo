package au.com.quaysystems.qrm.wp.model;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;

import au.com.quaysystems.qrm.server.MatrixPainter;

@Entity
@Table( name="RISK" )
public class Risk {
	@Column(columnDefinition="TEXT")
	String title;
	String riskProjectCode;
	@Column(columnDefinition="TEXT")
	String description;
	@Column(columnDefinition="TEXT")
	String cause;
	@Column(columnDefinition="TEXT")
	String consequence;
	int owner;
	int manager;
	double inherentProb;
	double inherentImpact;
	double treatedProb;
	double treatedImpact;
	double inherentAbsProb;
	double treatedAbsProb;
	boolean impRep;
	boolean impSafety;
	boolean impEnviron;
	boolean impCost;
	boolean impTime;
	boolean impSpec;
	boolean treatAvoid;
	boolean treatRetention;
	boolean treatTransfer;
	boolean treatMinimise;
	boolean treated;
	boolean summaryRisk;
	boolean useCalContingency;
	boolean useCalProb;
	double likeType;
	double likeAlpha;
	double likeT;
	double likePostType;
	double likePostAlpha;
	double likePostT;
	double estContingency;
	Integer rank;
	String start;
	String end;
	@Transient
	Category primcat;
	@Transient
	Category seccat;
	int primcatID;
	int seccatID;
	@OneToOne (fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name="RISK_MITIGATION", joinColumns=@JoinColumn(name="risk_id"),inverseJoinColumns=@JoinColumn(name="mitigation_id"))
	Mitigation mitigation;
	@OneToOne (fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name="RISK_RESPONSE", joinColumns=@JoinColumn(name="risk_id"),inverseJoinColumns=@JoinColumn(name="response_id"))
	Response response;
	@OneToMany (fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name="RISK_CONTROL", joinColumns=@JoinColumn(name="risk_id"),inverseJoinColumns=@JoinColumn(name="control_id"))
	@OrderColumn
	Control[] controls;
	@OneToMany (fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name="RISK_COMMENT", joinColumns=@JoinColumn(name="risk_id"),inverseJoinColumns=@JoinColumn(name="comment_id"))
	@OrderColumn
	Comment[] comments;
	@ElementCollection
	@CollectionTable(name="RISK_OBJECTIVES", joinColumns=@JoinColumn(name="risk_id"))
	@OrderColumn
	Integer [] objectiveIDs;
	@Transient
	HashMap<String, Boolean> objectives;
	int treatedTolerance;
	int inherentTolerance;
	double currentProb;
	double currentImpact;
	int currentTolerance;
	int projectID;
	@Id
	int id;
	@OneToOne (fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name="RISK_AUDIT", joinColumns=@JoinColumn(name="risk_id"),inverseJoinColumns=@JoinColumn(name="audit_id"))
	Audit audit;
	@Transient
	String[] incidents;
	@ElementCollection
	@CollectionTable(name="RISK_INCIDENTIDS", joinColumns=@JoinColumn(name="risk_id"))
	@OrderColumn
	Integer[] incidentIDs;
	@Transient
	String[] reviews;
	@ElementCollection
	@CollectionTable(name="RISK_REVIEWIDS", joinColumns=@JoinColumn(name="risk_id"))
	@OrderColumn
	Integer[] reviewIDs;
	@Lob
	byte[] preLikeImage;
	@Lob
	byte[] postLikeImage;
	@Lob
	byte[] matImage;
	public void normalise(QRMImport qrmImport, boolean prepareMatrix) {
		
		if (prepareMatrix) {
			ByteArrayOutputStream bOutPre = new ByteArrayOutputStream();
			ByteArrayOutputStream bOutPost = new ByteArrayOutputStream();
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			long days = 0;
			try {
				Date startDate = df.parse(start);
				Date endDate = df.parse(end);
				days = (endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24);
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
			LikelihoodChart.paintLikelihoodChart(bOutPre, true, true, days, this.likeT, this.likeAlpha, this.likeType,
					this.inherentAbsProb, false, treated);
			LikelihoodChart.paintLikelihoodChart(bOutPost, true, false, days, this.likePostT, this.likePostAlpha,
					this.likePostType, this.treatedAbsProb, false, treated);
			preLikeImage = bOutPre.toByteArray();
			postLikeImage = bOutPost.toByteArray();
		}
		ModelToleranceMatrix matrix = new ModelToleranceMatrix();
		
		for (Project project : qrmImport.projects){
			if (project.id == this.projectID){
				matrix.tolString = project.matrix.tolString;
				matrix.maxImpact = project.matrix.maxImpact;
				matrix.maxProb = project.matrix.maxProb;
			}
		}
		ModelRiskLite risk = new ModelRiskLite();
		
		risk.inherentImpact = this.inherentImpact;
		risk.inherentProb = this.inherentProb;
		risk.treatedImpact = this.treatedImpact;
		risk.treatedProb = this.treatedProb;
		
		ByteArrayOutputStream matOut = new ByteArrayOutputStream();
		try {
			ImageIO.write(MatrixPainter.getPNGSingleRisk(matrix, 150,150, risk), "png", matOut);
		} catch (IOException e) {
		}

		matImage = matOut.toByteArray();

		objectiveIDs = new Integer[objectives.keySet().size()];
		int i=0;
		for (String objStr : objectives.keySet()){
			objectiveIDs[i++] = Integer.parseInt(objStr);
		}

		incidentIDs = new Integer[incidents.length];
		i=0;
		for (String objStr : incidents){
			incidentIDs[i++] = Integer.parseInt(objStr);
		}

		reviewIDs = new Integer[reviews.length];
		i=0;
		for (String objStr : reviews){
			reviewIDs[i++] = Integer.parseInt(objStr);
		}

		for (Comment comment:comments){
			comment.normalise();
		}

		this.primcatID = this.primcat.id;
		this.seccatID = this.seccat.id;

		if (this.audit != null){
			if (this.audit.auditEval != null){
				this.audit.auditEval.type = "auditEval";
				this.audit.auditEval.riskID = this.id;
			}
			if (this.audit.auditEvalRev != null){
				this.audit.auditEvalRev.type = "auditEvalRev";
				this.audit.auditEvalRev.riskID = this.id;
			}
			if (this.audit.auditEvalApp != null){
				this.audit.auditEvalApp.type = "auditEvalApp";
				this.audit.auditEvalApp.riskID = this.id;
			}

			if (this.audit.auditIdent != null){
				this.audit.auditIdent.type = "auditIdent";
				this.audit.auditIdent.riskID = this.id;
			}
			if (this.audit.auditIdentRev != null){
				this.audit.auditIdentRev.type = "auditIdentRev";
				this.audit.auditIdentRev.riskID = this.id;
			}
			if (this.audit.auditIdentApp != null){
				this.audit.auditIdentApp.type = "auditIdentApp";
				this.audit.auditIdentApp.riskID = this.id;
			}

			if (this.audit.auditMit != null){
				this.audit.auditMit.type = "auditMit";
				this.audit.auditMit.riskID = this.id;
			}
			if (this.audit.auditMitRev != null){
				this.audit.auditMitRev.type = "auditMitRev";
				this.audit.auditMitRev.riskID = this.id;
			}
			if (this.audit.auditMitApp != null){
				this.audit.auditMitApp.type = "auditMitApp";
				this.audit.auditMitApp.riskID = this.id;
			}
			
			this.audit.auditItems = new AuditItem[9];
			
			this.audit.auditItems[0] = this.audit.auditEval;
			this.audit.auditItems[1] = this.audit.auditEvalRev;
			this.audit.auditItems[2] = this.audit.auditEvalApp;
			this.audit.auditItems[3] = this.audit.auditIdent;
			this.audit.auditItems[4] = this.audit.auditIdentRev;
			this.audit.auditItems[5] = this.audit.auditIdentApp;
			this.audit.auditItems[6] = this.audit.auditMit;
			this.audit.auditItems[7] = this.audit.auditMitRev;
			this.audit.auditItems[8] = this.audit.auditMitApp;
		}		
	}
}