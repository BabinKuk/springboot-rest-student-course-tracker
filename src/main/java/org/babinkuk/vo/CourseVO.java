package org.babinkuk.vo;

import java.util.ArrayList;
import java.util.List;

import org.babinkuk.vo.diff.DiffField;
import org.babinkuk.vo.diff.Diffable;

@Diffable(id = "id")
public class CourseVO {

	private int id;

	@DiffField
	private String title;
	
	@DiffField
	private int instructorId;
	
	@DiffField
	private List<ReviewVO> reviewsVO = new ArrayList<ReviewVO>();
	
	@DiffField
	private List<StudentVO> studentsVO;
	
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

	public int getInstructorId() {
		return instructorId;
	}

	public void setInstructorId(int instructorId) {
		this.instructorId = instructorId;
	}
	
	public List<ReviewVO> getReviewsVO() {
		return reviewsVO;
	}

	public void setReviewsVO(List<ReviewVO> reviewsVO) {
		this.reviewsVO = reviewsVO;
	}
	
	public List<StudentVO> getStudentsVO() {
		return studentsVO;
	}

	public void setStudentsVO(List<StudentVO> students) {
		this.studentsVO = students;
	}

	// convenienca methods
	public void addReviewVO(ReviewVO review) {
		if (reviewsVO == null) {
			reviewsVO = new ArrayList<ReviewVO>();
		}
		
		reviewsVO.add(review);
	}
	
	public void addStudentVO(StudentVO student) {
		if (studentsVO == null) {
			studentsVO = new ArrayList<StudentVO>();
		}
		
		studentsVO.add(student);
	}
	
	@Override
	public String toString() {
		return "CourseVO [id=" + id + ", title=" + title + "]";
	}	
}
