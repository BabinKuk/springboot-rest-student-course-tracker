package org.babinkuk.vo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.babinkuk.vo.diff.DiffField;
import org.babinkuk.vo.diff.Diffable;

@Diffable(id = "id")
public class CourseVO {

	private int id;

	@DiffField
	private String title;
	
	@DiffField
	private InstructorVO instructorVO;
	
	@DiffField
	private List<ReviewVO> reviewsVO = new ArrayList<ReviewVO>();
	
	@DiffField
	private Set<StudentVO> studentsVO = new HashSet<StudentVO>();
	
	public CourseVO() {
		// TODO Auto-generated constructor stub
	}
	
	public CourseVO(String title) {
		this.title = title;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public InstructorVO getInstructorVO() {
		return instructorVO;
	}

	public void setInstructorVO(InstructorVO instructorVO) {
		this.instructorVO = instructorVO;
	}
	
	public List<ReviewVO> getReviewsVO() {
		return reviewsVO;
	}

	public void setReviewsVO(List<ReviewVO> reviewsVO) {
		this.reviewsVO = reviewsVO;
	}
	
	public Set<StudentVO> getStudentsVO() {
		return studentsVO;
	}

	public void setStudentsVO(Set<StudentVO> students) {
		this.studentsVO = students;
	}

	// convenience methods
	public void addReviewVO(ReviewVO review) {
		if (reviewsVO == null) {
			reviewsVO = new ArrayList<ReviewVO>();
		}
		
		reviewsVO.add(review);
	}
	
	public void addStudentVO(StudentVO student) {
		if (studentsVO == null) {
			studentsVO = new HashSet<StudentVO>();
		}
		
		studentsVO.add(student);
	}
	
	@Override
	public String toString() {
		return "CourseVO [id=" + id + ", title=" + title + ", instructorVO=" + instructorVO + "]";
	}	
}
