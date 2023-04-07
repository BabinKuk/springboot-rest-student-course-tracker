package org.babinkuk.service;

import org.babinkuk.common.ApiResponse;
import org.babinkuk.exception.ObjectException;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.vo.CourseVO;

public interface CourseService {
	
	public Iterable<CourseVO> getAllCourses();
	
	public CourseVO findById(int id) throws ObjectNotFoundException;
	
	//public CourseVO findByEmail(String email) throws ObjectNotFoundException;
	
	public ApiResponse saveCourse(CourseVO courseVO) throws ObjectException;
	
	public ApiResponse deleteCourse(int id) throws ObjectNotFoundException;
		
}
