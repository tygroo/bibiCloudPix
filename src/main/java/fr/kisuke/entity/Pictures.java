package fr.kisuke.entity;

import org.joda.time.DateTime;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author bbonheur
 *
 */

@javax.persistence.Entity
public class Pictures implements Serializable, Entity {
	
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

	@Column(name = "SHORTNAMEHIGHT")
	private String shortNameHight;

	@Column(name = "ISSHAREHIGHT")
	private Boolean isHightShare;

	@Column(name = "NAMEMEDIUM")
	private String nameMed;

	@Column(name = "SHORTNAMEMEDIUM")
	private String shortNameMed;

	@Column(name = "ISSHAREMEDIUM")
	private Boolean isMediumShare;

	@Column(name = "NAMELOW")
	private String nameLow;

	@Column(name = "SHORTNAMELOW")
	private String shortNameLow;

	@Column(name = "ISSHARELOW")
	private Boolean isLowShare;

	// @Temporal(TemporalType.DATE)
	@Column(name = "CREATIONDATE")
	private DateTime creationDate;

	@ManyToOne(fetch = FetchType.EAGER)
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

	public String getShortNameHight() {
		return shortNameHight;
	}

	public void setShortNameHight(String shortNameHight) {
		this.shortNameHight = shortNameHight;
	}

	public String getShortNameMed() {
		return shortNameMed;
	}

	public void setShortNameMed(String shortNameMed) {
		this.shortNameMed = shortNameMed;
	}

	public String getShortNameLow() {
		return shortNameLow;
	}

	public void setShortNameLow(String shortNameLow) {
		this.shortNameLow = shortNameLow;
	}

	public Boolean getIsHightShare() {
		return isHightShare;
	}

	public void setIsHightShare(Boolean isHightShare) {
		this.isHightShare = isHightShare;
	}

	public Boolean getIsMediumShare() {
		return isMediumShare;
	}

	public void setIsMediumShare(Boolean isMediumShare) {
		this.isMediumShare = isMediumShare;
	}

	public Boolean getIsLowShare() {
		return isLowShare;
	}

	public void setIsLowShare(Boolean isLowShare) {
		this.isLowShare = isLowShare;
	}
}
