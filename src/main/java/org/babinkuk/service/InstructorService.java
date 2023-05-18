package org.babinkuk.service;

import org.babinkuk.common.ApiResponse;
import org.babinkuk.exception.ObjectException;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.vo.InstructorVO;

public interface InstructorService {
	
	/**
	 * get instructor list
	 * 
	 * @return Iterable<InstructorVO>
	 */
	public Iterable<InstructorVO> getAllInstructors();
	
	/**
	 * get instructor (by id)
	 * 
	 * @param id
	 * @return InstructorVO
	 * @throws ObjectNotFoundException
	 */
	public InstructorVO findById(int id) throws ObjectNotFoundException;
	
	/**
	 * get instructor (by email)
	 * 
	 * @param email
	 * @return InstructorVO
	 * @throws ObjectNotFoundException
	 */
	public InstructorVO findByEmail(String email) throws ObjectNotFoundException;
	
	/**
	 * save instructor (on insert/update)
	 * 
	 * @param instructorVO
	 * @return
	 * @throws ObjectException
	 */
	public ApiResponse saveInstructor(InstructorVO instructorVO) throws ObjectException;
	
	/**
	 * delete instructor
	 * 
	 * @param id
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public ApiResponse deleteInstructor(int id) throws ObjectNotFoundException;
		
}
