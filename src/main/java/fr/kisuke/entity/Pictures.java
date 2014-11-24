package fr.kisuke.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.joda.time.DateTime;

/**
 * @author bbonheur
 *
 */

@javax.persistence.Entity
public class Pictures implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5383452069402635451L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_PICTURE")
	private Long id;

	@Column(name = "NAME")
	private String name;

	@Column(name = "PATH")
	private String path;

	@Column(name = "NAMEHIGHT")
	private String nameHight;


	@Column(name = "NAMEMEDIUM")
	private String nameMed;

	@Column(name = "NAMELOW")
	private String nameLow;

	// @Temporal(TemporalType.DATE)
	@Column(name = "CREATIONDATE")
	private DateTime creationDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "iduser", nullable = true)
	private Users user;

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(final String path) {
		this.path = path;
	}

	public String getNameMed() {
		return nameMed;
	}

	public void setNameMed(final String nameMed) {
		this.nameMed = nameMed;
	}

	public String getNameLow() {
		return nameLow;
	}

	public void setNameLow(final String nameLow) {
		this.nameLow = nameLow;
	}

	public DateTime getCreationDate() {
		return creationDate;// new DateTime(creationDate.getTime());
	}

	public void setCreationDate(final DateTime dateTime) {
		creationDate = dateTime;// .toDate();
	}

	public Users getUser() {
		return user;
	}

	public void setUser(final Users user) {
		this.user = user;
	}
	public String getNameHight() {
		return nameHight;
	}
	public void setNameHight(final String nameHight) {
		this.nameHight = nameHight;
	}
}
