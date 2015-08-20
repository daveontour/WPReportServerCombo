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
@Table( name="mitigation" )
public class Mitigation {
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	Long id;
	@Column(columnDefinition="TEXT")
    String mitPlanSummary;
	@Column(columnDefinition="TEXT")
    String mitPlanSummaryUpdate;
    @OneToMany (fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name="mitigation_mitstep", joinColumns=@JoinColumn(name="mitigation_id"),inverseJoinColumns=@JoinColumn(name="mitigationstep_id"))
    @OrderColumn
    MitPlan[] mitPlan;
}
