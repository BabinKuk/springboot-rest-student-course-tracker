package org.babinkuk.validator;

import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.exception.ObjectValidationException;
import org.babinkuk.vo.BaseVO;
import org.babinkuk.vo.CourseVO;
import org.babinkuk.vo.InstructorVO;
import org.babinkuk.vo.ReviewVO;

public interface Validator {
	
	/** 
	 * @param instructor/student
	 * @param action
	 * @param validatorType
	 * @return
	 * @throws ObjectValidationException
	 */
	public BaseVO validate(BaseVO vo, ActionType action, ValidatorType validatorType) throws ObjectValidationException;

	/** 
	 * @param course
	 * @param action
	 * @param validatorType
	 * @return
	 * @throws ObjectValidationException
	 */
	public void validate(CourseVO vo, ActionType action, ValidatorType validatorType) throws ObjectValidationException;
	
	/** 
	 * @param review
	 * @param action
	 * @param validatorType
	 * @return
	 * @throws ObjectValidationException
	 */
	public void validate(ReviewVO vo, ActionType action, ValidatorType validatorType) throws ObjectValidationException;
	
	/**
	 * @param id
	 * @param validatorType
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public void validate(int id, ActionType action, ValidatorType validatorType) throws ObjectNotFoundException;

}
