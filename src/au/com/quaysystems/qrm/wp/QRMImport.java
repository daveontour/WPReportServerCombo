package au.com.quaysystems.qrm.wp;
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

import au.com.quaysystems.qrm.wp.model.Review;
import au.com.quaysystems.qrm.wp.model.Risk;
import au.com.quaysystems.qrm.wp.model.User;
import au.com.quaysystems.qrm.wp.util.Incident;
import au.com.quaysystems.qrm.wp.util.Project;

@Entity
@Table( name="QRMIMPORT" )
public class QRMImport {
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	public Long id;
    @OneToMany (fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name="IMPORT_PROJECT", joinColumns=@JoinColumn(name="import_id"),inverseJoinColumns=@JoinColumn(name="project_id"))
    @OrderColumn
	public Project[] projects;
    @OneToMany (fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name="IMPORT_RISK", joinColumns=@JoinColumn(name="import_id"),inverseJoinColumns=@JoinColumn(name="risk_id"))
    @OrderColumn
    public Risk[] risks;
    @OneToMany (fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name="IMPORT_INCIDENT", joinColumns=@JoinColumn(name="import_id"),inverseJoinColumns=@JoinColumn(name="incident_id"))
    @OrderColumn
    public Incident[] incidents;
    @OneToMany (fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name="IMPORT_REVIEW", joinColumns=@JoinColumn(name="import_id"),inverseJoinColumns=@JoinColumn(name="review_id"))
    @OrderColumn
    public Review[] reviews;

    @OneToMany (fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name="IMPORT_USER", joinColumns=@JoinColumn(name="import_id"),inverseJoinColumns=@JoinColumn(name="user_id"))
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
		for(Project project :projects){
			project.normalise();
		}
		for(Risk risk :risks){
			risk.normalise(this, prepareMatrix);
		}
		for(Incident incident :incidents){
			incident.normalise();
		}
		for(Review review :reviews){
			review.normalise();
		}
		for(User user :users){
			user.normalise();
		}
	}
}
