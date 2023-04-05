package org.babinkuk.service;

import org.babinkuk.common.ApiResponse;
import org.babinkuk.exception.ObjectException;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.vo.InstructorVO;

public interface InstructorService {
	
	public Iterable<InstructorVO> getAllInstructors();
	
	public InstructorVO findById(int id) throws ObjectNotFoundException;
	
	public InstructorVO findByEmail(String email) throws ObjectNotFoundException;
	
	public ApiResponse saveInstructor(InstructorVO InstructorVO) throws ObjectException;
	
	public ApiResponse deleteInstructor(int id) throws ObjectNotFoundException;
		
}
