package au.com.quaysystems.qrm.wp.model;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;


@Entity
@Table( name="clientsites", indexes = {@Index(name = "siteKey_index",  columnList="siteKey", unique = true)})
public class ClientSites {
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	public Long id;
	public String siteName;
	public String siteID;
	@Column(name="siteKey", nullable = false)
	public String siteKey;
	public String orderID;
	public String orderEmail;
	public String orderDate;
	public Boolean active = true;
}