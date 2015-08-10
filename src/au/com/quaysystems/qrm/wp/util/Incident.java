package au.com.quaysystems.qrm.wp.util;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import au.com.quaysystems.qrm.wp.model.Comment;

@Entity
@Table( name="INCIDENT" )
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
    int reportedby;
    String lessons;
    String actions;
    String date;
    @ElementCollection
    @CollectionTable(name="INCIDENTCOMMENT", joinColumns=@JoinColumn(name="incident_id"))
    @OrderColumn
    Comment[] comments;
    @ElementCollection
    @CollectionTable(name="INCIDENTRISK", joinColumns=@JoinColumn(name="incident_id"))
    @OrderColumn
    int [] risks;
	public void normalise() {
		if (comments == null) return;
		for (Comment comment:comments){
			comment.normalise();
		}
	}
}
