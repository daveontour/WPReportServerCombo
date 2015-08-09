package au.com.quaysystems.qrm.wp.util;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;

@SuppressWarnings("serial")
@NamedNativeQueries( {
	@NamedNativeQuery(name = "getAllProjectRiskLite", callable = true, query = "call getAllProjectRisk( :var_user_id,:var_projectID,:var_descendants)", resultClass = ModelRiskLite.class),
	@NamedNativeQuery(name = "getProjectRisksForUser", callable = true, query = "call getProjectRisks(:userID, :projectID, :descendants)", resultClass = ModelRiskLite.class),
	@NamedNativeQuery(name = "getRiskLite", callable = true, query = "call getRisk(:userID,:riskID,:projectID)", resultClass = ModelRiskLite.class)
})
@Entity
@Table(name="risk")
public class ModelRiskLite  extends IModelRiskLite {
	
	@Column(name="contextRank")
	public Long subjectiveRank;

	public Long getSubjectiveRank() {
		return subjectiveRank;
	}

	public void setSubjectiveRank(Long subjectiveRank) {
		this.subjectiveRank = subjectiveRank;
	}
}
