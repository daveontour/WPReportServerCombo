package au.com.quaysystems.qrm.wp.model;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

@Entity
@Table( name="review" )
public class Review {
	@Id
	Long id;
    String title;
	@Column(columnDefinition="TEXT")
    String description;
    String scheddate;
    String actualdate;
    String reviewCode;
    int responsible;
	@Column(columnDefinition="TEXT")
    String notes;
	Boolean complete = false;
    @ElementCollection
    @CollectionTable(name="review_risk", joinColumns=@JoinColumn(name="review_id"))
    @OrderColumn
    int [] risks;
    @OneToMany (fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name="review_reviewriskcomment", joinColumns=@JoinColumn(name="review_id"),inverseJoinColumns=@JoinColumn(name="reviewriskcomment_id"))
    @OrderColumn
    ReviewRiskComment[] riskComments;
	public void normalise() {
		description = description.replaceAll("<p>", "");
		description = description.replaceAll("</p>", "<br/><br/>");

		notes = notes.replaceAll("<p>", "");
		notes = notes.replaceAll("</p>", "<br/><br/>");


		if (riskComments == null) return;
		for (ReviewRiskComment comment:riskComments){
			comment.normalise();
		}

		
	}
}
