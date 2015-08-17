package au.com.quaysystems.qrm.wp.model;
/*
 * 
 */


import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;

@NamedNativeQueries( {
	@NamedNativeQuery(name = "getAllMatrix", query = "SELECT tolerancematrix.*, 0 AS generation, 'title' AS projectTitle  from tolerancematrix", resultClass = ModelToleranceMatrix.class),
	@NamedNativeQuery(name = "getProjectMats", callable = true, query = "call getProjectMats(:projectID )", resultClass = ModelToleranceMatrix.class)
})
@Entity
@Table(name = "tolerancematrix")
public class ModelToleranceMatrix implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1380225913567394552L;

	/** The dateEntered. */
	@Column(updatable = false, insertable = false)
	public Date dateEntered;

	/** The dateUpdated. */
	@Column(updatable = false, insertable = false)
	public Date dateUpdated;

	/** The generation. */
	@Transient
	public int generation;

	/** The matrixID. */
	@Id
	@GeneratedValue
	public Long matrixID;

	/** The max impact. */
	public Integer maxImpact;

	/** The max prob. */
	public Integer maxProb;

	/** The tol string. */
	public String tolString;

	/** The project title. */
	@Transient
	public String projectTitle;

	/** The projectID. */
	public Long projectID;

	/** The impact1. */
	public String impact1;

	/** The impact2. */
	public String impact2;

	/** The impact3. */
	public String impact3;

	/** The impact4. */
	public String impact4;

	/** The impact5. */
	public String impact5;

	/** The impact6. */
	public String impact6;

	/** The impact7. */
	public String impact7;

	/** The impact8. */
	public String impact8;

	/** The prob1. */
	public String prob1;

	/** The prob2. */
	public String prob2;

	/** The prob3. */
	public String prob3;

	/** The prob4. */
	public String prob4;

	/** The prob5. */
	public String prob5;

	/** The prob6. */
	public String prob6;

	/** The prob7. */
	public String prob7;

	/** The prob8. */
	public String prob8;

	/** The prob val1. */
	public Double probVal1;

	/** The prob val2. */
	public Double probVal2;

	/** The prob val3. */
	public Double probVal3;

	/** The prob val4. */
	public Double probVal4;

	/** The prob val5. */
	public Double probVal5;

	/** The prob val6. */
	public Double probVal6;

	/** The prob val7. */
	public Double probVal7;

	/** The prob val8. */
	public Double probVal8;

	/**
	 * Instantiates a new model tolerance matrix.
	 */
	public ModelToleranceMatrix() {
	}



	/**
	 * Gets the project title.
	 * 
	 * @return the project title
	 */
	public final String getProjectTitle() {
		return projectTitle;
	}

	/**
	 * Sets the project title.
	 * 
	 * @param projectTitle
	 *            the new project title
	 */
	public final void setProjectTitle(final String projectTitle) {
		this.projectTitle = projectTitle;
	}

	/**
	 * Gets the projectID.
	 * 
	 * @return the projectID
	 */
	public final Long getProjectID() {
		return projectID;
	}

	/**
	 * Sets the projectID.
	 * 
	 * @param projectID
	 *            the new projectID
	 */
	public final void setProjectID(final Long projectID) {
		this.projectID = projectID;
	}

	/**
	 * Gets the dateEntered.
	 * 
	 * @return the dateEntered
	 */
	public final Date getDateEntered() {
		return dateEntered;
	}

	/**
	 * Sets the dateEntered.
	 * 
	 * @param dateEntered
	 *            the dateEntered to set
	 */
	public final void setDateEntered(final Date dateEntered) {
		this.dateEntered = dateEntered;
	}

	/**
	 * Gets the dateUpdated.
	 * 
	 * @return the dateUpdated
	 */
	public final Date getDateUpdated() {
		return dateUpdated;
	}

	/**
	 * Sets the dateUpdated.
	 * 
	 * @param dateUpdated
	 *            the dateUpdated to set
	 */
	public final void setDateUpdated(final Date dateUpdated) {
		this.dateUpdated = dateUpdated;
	}

	/**
	 * Gets the generation.
	 * 
	 * @return the generation
	 */
	public final int getGeneration() {
		return generation;
	}

	/**
	 * Sets the generation.
	 * 
	 * @param generation
	 *            the generation to set
	 */
	public final void setGeneration(final int generation) {
		this.generation = generation;
	}

	/**
	 * Gets the matrix id.
	 * 
	 * @return the internalID
	 */
	public final Long getMatrixID() {
		return matrixID;
	}

	/**
	 * Sets the matrix id.
	 * 
	 * @param internalID
	 *            the internalID to set
	 */
	public final void setMatrixID(final Long internalID) {
		this.matrixID = internalID;
	}

	/**
	 * Gets the max impact.
	 * 
	 * @return the maxImpact
	 */
	public final Integer getMaxImpact() {
		return maxImpact;
	}

	/**
	 * Sets the max impact.
	 * 
	 * @param maxImpact
	 *            the maxImpact to set
	 */
	public final void setMaxImpact(final Integer maxImpact) {
		this.maxImpact = maxImpact;
	}

	/**
	 * Gets the max prob.
	 * 
	 * @return the maxProb
	 */
	public final Integer getMaxProb() {
		return maxProb;
	}

	/**
	 * Sets the max prob.
	 * 
	 * @param maxProb
	 *            the maxProb to set
	 */
	public final void setMaxProb(final Integer maxProb) {
		this.maxProb = maxProb;
	}

	/**
	 * Gets the tol string.
	 * 
	 * @return the toleranceString
	 */
	public final String getTolString() {
		return tolString;
	}

	/**
	 * Sets the tol string.
	 * 
	 * @param toleranceString
	 *            the toleranceString to set
	 */
	public final void setTolString(final String toleranceString) {
		this.tolString = toleranceString;
	}

	/**
	 * Gets the risk context.
	 * 
	 * @return the risk context
	 */
	public final String getRiskContext() {
		return projectTitle;
	}

	/**
	 * Sets the risk context.
	 * 
	 * @param str
	 *            the new risk context
	 */
	public final void setRiskContext(final String str) {
		projectTitle = str;
	}
}
