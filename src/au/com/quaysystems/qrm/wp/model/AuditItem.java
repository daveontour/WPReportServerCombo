package au.com.quaysystems.qrm.wp.model;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table( name="AUDITITEM" )
public class AuditItem {
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	Long id;
	@Column(columnDefinition="TEXT")
	String auditComment;
	String auditDate;
	int auditPerson;
	String type;
	int riskID;
}
