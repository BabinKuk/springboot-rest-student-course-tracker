package org.babinkuk.vo;

import java.util.HashSet;
import java.util.Set;

import org.babinkuk.entity.Student;

/**
 * instance of this class is used to represent student data
 * 
 * @author Nikola
 *
 */
public class StudentVO extends BaseVO {
	
	private int id;
	
	private String firstName;
	
	private String lastName;
	
	private String emailAddress;
	
	private Set<CourseVO> coursesVO = new HashSet<CourseVO>();
	
	public StudentVO() {
		// TODO Auto-generated constructor stub
	}
	
	public StudentVO(String firstName, String lastName, String emailAddress) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.emailAddress = emailAddress;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public Set<CourseVO> getCoursesVO() {
		return coursesVO;
	}

	public void setCoursesVO(Set<CourseVO> coursesVO) {
		this.coursesVO = coursesVO;
	}
	
	@Override
	public String toString() {
		return "StudentVO [firstName=" + firstName + ", lastName=" + lastName + ", emailAddress=" + emailAddress + ", courses=" + coursesVO + "]";
	}

}
