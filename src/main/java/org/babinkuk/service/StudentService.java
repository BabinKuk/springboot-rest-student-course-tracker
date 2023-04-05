package org.babinkuk.service;

import org.babinkuk.common.ApiResponse;
import org.babinkuk.exception.ObjectException;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.vo.StudentVO;

public interface StudentService {
	
	public Iterable<StudentVO> getAllStudents();
	
	public StudentVO findById(int id) throws ObjectNotFoundException;
	
	public StudentVO findByEmail(String email) throws ObjectNotFoundException;
	
	public ApiResponse saveStudent(StudentVO studentVO) throws ObjectException;
	
	public ApiResponse deleteStudent(int id) throws ObjectNotFoundException;
		
}
