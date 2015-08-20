package au.com.quaysystems.qrm.wp.model;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table( name="matrix" )
public class Matrix {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	Long matrix_id;
    public int maxImpact;
    public int maxProb;
    public String tolString;
    int probVal1;
    int probVal2;
    int probVal3;
    int probVal4;
    int probVal5;
    int probVal6;
    int probVal7;
    int probVal8;
}
