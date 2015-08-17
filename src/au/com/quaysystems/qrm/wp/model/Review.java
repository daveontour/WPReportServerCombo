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
@Table( name="REVIEW" )
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
    @CollectionTable(name="REVIEW_RISK", joinColumns=@JoinColumn(name="review_id"))
    @OrderColumn
    int [] risks;
    @OneToMany (fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name="REVIEW_REVIEWRISKCOMMENT", joinColumns=@JoinColumn(name="review_id"),inverseJoinColumns=@JoinColumn(name="reviewriskcomment_id"))
    @OrderColumn
    ReviewRiskComment[] riskComments;
	public void normalise() {
		// TODO Auto-generated method stub
		
	}
}
