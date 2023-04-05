package org.babinkuk.validator;

import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.exception.ObjectValidationException;
import org.babinkuk.vo.InstructorVO;

public interface InstructorValidator {
	
	/** 
	 * @param instructor/student
	 * @param isInsert
	 * @param action
	 * @return
	 * @throws EmployeeValidationException
	 */
	public InstructorVO validate(InstructorVO vo, boolean isInsert, ActionType action) throws ObjectValidationException;

	/**
	 * @param id
	 * @return
	 * @throws EmployeeNotFoundException
	 */
	public InstructorVO validate(int id, ActionType action) throws ObjectNotFoundException;

}
