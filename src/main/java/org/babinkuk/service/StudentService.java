package org.babinkuk.service;

import org.babinkuk.common.ApiResponse;
import org.babinkuk.exception.ObjectException;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.vo.StudentVO;

public interface StudentService {
	
	/**
	 * get student list
	 * 
	 * @return Iterable<StudentVO>
	 */
	public Iterable<StudentVO> getAllStudents();
	
	/**
	 * get student (by id)
	 * 
	 * @param id
	 * @return StudentVO
	 * @throws ObjectNotFoundException
	 */
	public StudentVO findById(int id) throws ObjectNotFoundException;
	
	/**
	 * get student (by email)
	 * 
	 * @param email
	 * @return StudentVO
	 * @throws ObjectNotFoundException
	 */
	public StudentVO findByEmail(String email) throws ObjectNotFoundException;
	
	/**
	 * save student (on insert/update)
	 * 
	 * @param studentVO
	 * @return
	 * @throws ObjectException
	 */
	public ApiResponse saveStudent(StudentVO studentVO) throws ObjectException;
	
	/**
	 * delete student
	 * 
	 * @param id
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public ApiResponse deleteStudent(int id) throws ObjectNotFoundException;
}
