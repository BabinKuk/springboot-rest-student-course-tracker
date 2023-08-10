package org.babinkuk.service;

import org.babinkuk.common.ApiResponse;
import org.babinkuk.exception.ObjectException;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.vo.CourseVO;

public interface CourseService {
	
	/**
	 * get courses list
	 * 
	 * @return Iterable<CourseVO>
	 */
	public Iterable<CourseVO> getAllCourses();
	
	/**
	 * get course
	 * 
	 * @param id
	 * @return CourseVO
	 * @throws ObjectNotFoundException
	 */
	public CourseVO findById(int id) throws ObjectNotFoundException;
	
	/**
	 * save course (on insert/update)
	 * 
	 * @param courseVO
	 * @return
	 * @throws ObjectException
	 */
	public ApiResponse saveCourse(CourseVO courseVO) throws ObjectException;
	
	/**
	 * delete course
	 * 
	 * @param id
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public ApiResponse deleteCourse(int id) throws ObjectNotFoundException;

	/**
	 * get course
	 * 
	 * @param title
	 * @return CourseVO
	 * @throws ObjectNotFoundException
	 */
	public CourseVO findByTitle(String title) throws ObjectNotFoundException;
		
}
