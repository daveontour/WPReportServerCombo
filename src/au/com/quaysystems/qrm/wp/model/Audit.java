package au.com.quaysystems.qrm.wp.model;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table( name="audit" )
public class Audit {
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	Long id;

	@Transient
	AuditItem auditIdent;
	@Transient
	AuditItem auditEval;
	@Transient
	AuditItem auditIdentRev;
	@Transient
	AuditItem auditIdentApp;
	@Transient
	AuditItem auditEvalRev;
	@Transient
	AuditItem auditEvalApp;
	@Transient
	AuditItem auditMit;
	@Transient
	AuditItem auditMitRev;
	@Transient
	AuditItem auditMitApp;
	
	Long riskID;
	
	@OneToMany (fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name="audit_audititem", joinColumns=@JoinColumn(name="audit_id"),inverseJoinColumns=@JoinColumn(name="audititem_id"))
	@OrderColumn
	AuditItem[] auditItems;
}
