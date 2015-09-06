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
@Table( name="incident" )
public class Incident {
	@Column(columnDefinition="TEXT")
    String title;
	@Column(columnDefinition="TEXT")
    String description;
	@Id
    int id;
    String incidentCode;
    boolean resolved;
    boolean identified;
    boolean evaluated;
    boolean controls;
    boolean consequences;
    boolean causes;
    
    boolean time = false;
    boolean environment = false;
    boolean safety = false;
    boolean cost = false;
    boolean reputation = false;
    boolean spec = false;

    int reportedby;
    String lessons;
    String actions;
    String date;
    @OneToMany (fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name="incidentcomment", joinColumns=@JoinColumn(name="incident_id"))
    @OrderColumn
    Comment[] comments;
    @ElementCollection
    @CollectionTable(name="incidentrisk", joinColumns=@JoinColumn(name="incident_id"))
    @OrderColumn
    int [] risks;
	public void normalise() {
		
		description = description.replaceAll("<p>", "");
		description = description.replaceAll("</p>", "<br/><br/>");

		lessons = lessons.replaceAll("<p>", "");
		lessons = lessons.replaceAll("</p>", "<br/><br/>");

		actions = actions.replaceAll("<p>", "");
		actions = actions.replaceAll("</p>", "<br/><br/>");

		if (comments == null) return;
		for (Comment comment:comments){
			comment.normalise();
		}
	}
}
