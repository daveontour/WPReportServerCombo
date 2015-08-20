package au.com.quaysystems.qrm.wp.model;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table( name="response" )
public class Response {
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	Long id;
	@Column(columnDefinition="TEXT")
	String respPlanSummary;
	@Column(columnDefinition="TEXT")
	String respPlanSummaryUpdate;
	@OneToMany (fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name="response_respstep", joinColumns=@JoinColumn(name="response_id"),inverseJoinColumns=@JoinColumn(name="responsestep_id"))
	@OrderColumn
	RespPlan[] respPlan;
}
