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
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

@Entity
@Table( name="project" )
public class Project {
	@Id
	public	int id;
	@Column(columnDefinition="TEXT")
	String title;
	@Column(columnDefinition="TEXT")
	String description;
	String projectCode;
    @ElementCollection
    @CollectionTable(name="project_ownersids", joinColumns=@JoinColumn(name="project_id"))
    @OrderColumn
	Integer[] ownersID;
    @ElementCollection
    @CollectionTable(name="project_managersids", joinColumns=@JoinColumn(name="project_id"))
    @OrderColumn
	Integer[] managersID;
    @ElementCollection
    @CollectionTable(name="project_usersids", joinColumns=@JoinColumn(name="project_id"))
    @OrderColumn
	Integer[] usersID;
	int projectRiskManager;
	
	@OneToOne (fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name="project_matrix", joinColumns=@JoinColumn(name="project_id"),inverseJoinColumns=@JoinColumn(name="matrix_id"))
	public Matrix matrix;
	
	boolean inheritParentCategories;
    boolean inheritParentObjectives;
    @OneToMany (fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name="project_categories", joinColumns=@JoinColumn(name="project_id"),inverseJoinColumns=@JoinColumn(name="category_id"))
    @OrderColumn
    Category[] categories;
    @OneToMany (fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name="project_objectives", joinColumns=@JoinColumn(name="project_id"),inverseJoinColumns=@JoinColumn(name="objective_id"))
    @OrderColumn
    Objective[] objectives;
    int parent_id;
    
	public void normalise() {
		// TODO Auto-generated method stub
		
	}
}
