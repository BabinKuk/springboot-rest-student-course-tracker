package org.babinkuk.vo;

import java.util.ArrayList;
import java.util.List;

import org.babinkuk.vo.diff.DiffField;
import org.babinkuk.vo.diff.Diffable;

@Diffable(id = "id")
public class InstructorVO extends BaseVO {

	private int id;
	
	@DiffField
	private String firstName;
	
	@DiffField
	private String lastName;
	
	@DiffField
	private String emailAddress;
	
	@DiffField
	private String youtubeChannel;
	
	@DiffField
	private String hobby;
	
//	@DiffField
//	private InstructorDetailVO instructorDetailVO;
	
//	private List<CourseVO> coursesVO;
	
	public InstructorVO() {
		// TODO Auto-generated constructor stub
	}

	public InstructorVO(String firstName, String lastName, String emailAddress) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.emailAddress = emailAddress;
	}
	
	public InstructorVO(String firstName, String lastName, String emailAddress, String youtubeChannel, String hobby) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.emailAddress = emailAddress;
		this.youtubeChannel = youtubeChannel;
		this.hobby = hobby;
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
	
	public String getYoutubeChannel() {
		return youtubeChannel;
	}

	public void setYoutubeChannel(String youtubeChannel) {
		this.youtubeChannel = youtubeChannel;
	}

	public String getHobby() {
		return hobby;
	}

	public void setHobby(String hobby) {
		this.hobby = hobby;
	}
	
//	public InstructorDetailVO getInstructorDetailVO() {
//		return instructorDetailVO;
//	}
//
//	public void setInstructorDetailVO(InstructorDetailVO instructorDetailVO) {
//		this.instructorDetailVO = instructorDetailVO;
//	}
//
//	public List<CourseVO> getCoursesVO() {
//		return coursesVO;
//	}
//
//	public void setCoursesVO(List<CourseVO> coursesVO) {
//		this.coursesVO = coursesVO;
//	}
//	
//	// convenience methods for bi-directional relationships
//	public void add(CourseVO courseVO) {
//		if (coursesVO == null) {
//			coursesVO = new ArrayList<CourseVO>();
//		}
//		
//		coursesVO.add(courseVO);
//		
//		courseVO.setInstructorId(this.id);
//	}

	@Override
	public String toString() {
		return "Instructor [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", emailAddress=" + emailAddress
				+ ", youtubeChannel=" + youtubeChannel+ ", hobby=" + hobby
				//+ ", instructorDetailVO=" + instructorDetailVO
				+ "]";
	}

}
