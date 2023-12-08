/***************************************************************************
 * File:  Course.java Course materials (23S) CST 8277
 * 
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @date August 28, 2022
 * 
 * Updated by:  Group NN
 *   studentId, firstName, lastName (as from ACSIS)
 *   studentId, firstName, lastName (as from ACSIS)
 *   studentId, firstName, lastName (as from ACSIS)
 *   studentId, firstName, lastName (as from ACSIS)
 * 
 */
package acmecollege.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@SuppressWarnings("unused")

/**
 * The persistent class for the course database table.
 */
//TODO CO01 - Add the missing annotations.
//TODO CO02 - Do we need a mapped super class?  If so, which one?
@Entity
@Table(name = "course")
@NamedQuery(name = Course.ALL_COURSES_QUERY, query = "SELECT c FROM Course c")
@NamedQuery(name = Course.IS_DUPLICATE_QUERY_NAME, query = "SELECT count(c) FROM Course c where c.courseTitle = :param1")
@AttributeOverride(name = "id", column = @Column(name = "course_id"))
public class Course extends PojoBase implements Serializable {
	private static final long serialVersionUID = 1L;
	
    public static final String ALL_COURSES_QUERY = "Course.findAll";
    public static final String IS_DUPLICATE_QUERY_NAME = "Course.isDuplicate";

	// TODO CO03 - Add missing annotations.
	@Basic(optional = false)
	@Column(name = "course_code", nullable = true, length = 7)
	private String courseCode;

	// TODO CO04 - Add missing annotations.
	@Basic(optional = false)
    @Column(name = "course_title", nullable = false, length = 100)
	private String courseTitle;

	// TODO CO05 - Add missing annotations.
	@Basic(optional = false)
	@Column(name = "year",  nullable = false)
	private int year;

	// TODO CO06 - Add missing annotations.
	@Basic(optional = false)
	@Column(name = "semester",  nullable = false, length = 6)
	private String semester;

	// TODO CO07 - Add missing annotations.
	@Basic(optional = false)
	@Column(name = "credit_units",  nullable = false)
	private int creditUnits;

	// TODO CO08 - Add missing annotations.
	@Basic(optional = false)
	@Column(name = "online",  nullable = false)
	private byte online;

	// TODO CO09 - Add annotations for 1:M relation.  Changes to this class should not cascade.
	@OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, mappedBy = "course")
	@JsonManagedReference(value="course")
	private Set<CourseRegistration> courseRegistrations = new HashSet<>();

	public Course() {
		super();
	}

	public Course(String courseCode, String courseTitle, int year, String semester, int creditUnits, byte online) {
		this();
		this.courseCode = courseCode;
		this.courseTitle = courseTitle;
		this.year = year;
		this.semester = semester;
		this.creditUnits = creditUnits;
		this.online = online;
	}

	public Course setCourse(String courseCode, String courseTitle, int year, String semester, int creditUnits, byte online) {
		setCourseCode(courseCode);
		setCourseTitle(courseTitle);
		setYear(year);
		setSemester(semester);
		setCreditUnits(creditUnits);
		setOnline(online);
		return this;
	}

	public String getCourseCode() {
		return courseCode;
	}

	public void setCourseCode(String courseCode) {
		this.courseCode = courseCode;
	}

	public String getCourseTitle() {
		return courseTitle;
	}

	public void setCourseTitle(String courseTitle) {
		this.courseTitle = courseTitle;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getSemester() {
		return semester;
	}

	public void setSemester(String semester) {
		this.semester = semester;
	}

	public int getCreditUnits() {
		return creditUnits;
	}

	public void setCreditUnits(int creditUnits) {
		this.creditUnits = creditUnits;
	}

	public byte getOnline() {
		return online;
	}

	public void setOnline(byte online) {
		this.online = online;
	}
	
	public Set<CourseRegistration> getCourseRegistrations() {
		return courseRegistrations;
	}

	public void setCourseRegistrations(Set<CourseRegistration> courseRegistrations) {
		this.courseRegistrations = courseRegistrations;
	}

	//Inherited hashCode/equals is sufficient for this Entity class

}
