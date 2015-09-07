package au.com.quaysystems.qrm.wp.model;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table( name="reviewriskcomment" )
public class ReviewRiskComment {
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	Long id;
	int riskID;
	@Column(columnDefinition="TEXT")
	String comment;
	public void normalise() {
		// TODO Auto-generated method stub
		comment = comment.replaceAll("<p>", "");
		comment = comment.replaceAll("</p>", "<br/><br/>");
		
	}
}
