package au.com.quaysystems.qrm.wp.util;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table( name="OBJECTIVE" )
public class Objective {
	@Column(columnDefinition="TEXT")
	String title;
    int projectID;
    @Id
    int id;
    int parentID;
}
