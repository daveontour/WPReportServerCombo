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
@Table( name="qrmimport" )
public class QRMImport {
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	public Long id;
    @OneToMany (fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name="import_project", joinColumns=@JoinColumn(name="import_id"),inverseJoinColumns=@JoinColumn(name="project_id"))
    @OrderColumn
	public Project[] projects;
    @OneToMany (fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name="import_risk", joinColumns=@JoinColumn(name="import_id"),inverseJoinColumns=@JoinColumn(name="risk_id"))
    @OrderColumn
    public Risk[] risks;
    @OneToMany (fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name="import_incident", joinColumns=@JoinColumn(name="import_id"),inverseJoinColumns=@JoinColumn(name="incident_id"))
    @OrderColumn
    public Incident[] incidents;
    @OneToMany (fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name="import_review", joinColumns=@JoinColumn(name="import_id"),inverseJoinColumns=@JoinColumn(name="review_id"))
    @OrderColumn
    public Review[] reviews;

    @OneToMany (fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name="import_user", joinColumns=@JoinColumn(name="import_id"),inverseJoinColumns=@JoinColumn(name="user_id"))
    @OrderColumn
    public User[] users;
    @Transient
    public String siteID;
    @Transient
    public String siteKey;
    @Transient
    public String siteName;
    @Transient
    public String userDisplayName;
    @Transient
    public String userEmail;
    @Transient
    public String userLogin;

	public void normalise(boolean prepareMatrix) {
		try {
			for(Project project :projects){
				project.normalise();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		try {
			for(Risk risk :risks){
				risk.normalise(this, prepareMatrix);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		try {
			for(Incident incident :incidents){
				incident.normalise();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		try {
			for(Review review :reviews){
				review.normalise();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		try {
			for(User user :users){
				user.normalise();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
	}
}
