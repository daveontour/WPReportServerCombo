package au.com.quaysystems.qrm.wp.model;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table( name="CATEGORY" )
public class Category {
    String title;
    @Id
    int id;
    boolean primCat;
    int parentID;
    int projectID;
}
