package au.com.quaysystems.qrm.wp.model;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table( name="USER" )
public class User {
	@Id
	Long userID;
	String id;
	String display_name;
	String user_email;
	public void normalise() {
		
		userID = Long.parseLong(id);
		
	}
}
