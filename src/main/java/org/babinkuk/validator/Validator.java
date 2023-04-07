package org.babinkuk.validator;

import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.exception.ObjectValidationException;
import org.babinkuk.vo.BaseVO;
import org.babinkuk.vo.InstructorVO;

public interface Validator {
	
	/** 
	 * @param instructor/student
	 * @param isInsert
	 * @param action
	 * @param validatorType
	 * @return
	 * @throws ObjectValidationException
	 */
	public BaseVO validate(BaseVO vo, boolean isInsert, ActionType action, ValidatorType validatorType) throws ObjectValidationException;

	/**
	 * @param id
	 * @param validatorType
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public BaseVO validate(int id, ActionType action, ValidatorType validatorType) throws ObjectNotFoundException;

}
