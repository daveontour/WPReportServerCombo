package au.com.quaysystems.qrm.wp.model;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table( name="comment" )
public class Comment {
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	Long id;
	String comment_id;
	String comment_post_ID;
	String comment_author;
	String comment_author_email;
    String comment_date;
	@Column(columnDefinition="TEXT")
    String comment_content;
    int parentID;
    
    public void normalise(){
    	comment_content = comment_content.replaceAll("<p>", "");
    	comment_content = comment_content.replaceAll("</p>", "<br/>");
    	parentID = Integer.parseInt(comment_post_ID);
    }
}
