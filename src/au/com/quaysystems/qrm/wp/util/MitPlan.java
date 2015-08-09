package au.com.quaysystems.qrm.wp.util;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table( name="MITPLAN" )
public class MitPlan {
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	Long id;
	@Column(columnDefinition="TEXT")
    String description;
    int person;
    double cost;
    double complete;
    String due;
}
