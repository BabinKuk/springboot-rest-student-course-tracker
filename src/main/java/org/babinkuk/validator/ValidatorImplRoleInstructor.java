package org.babinkuk.validator;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.exception.ObjectValidationException;
import org.babinkuk.vo.BaseVO;
import org.babinkuk.vo.CourseVO;
import org.babinkuk.vo.InstructorVO;
import org.babinkuk.vo.ReviewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * for future purpose 
 * if special valdations are required depending on the role 
 * 
 * @author Nikola
 *
 */
@Component("validator.ROLE_INSTRUCTOR")
public class ValidatorImplRoleInstructor implements Validator {

private final Logger log = LogManager.getLogger(getClass());
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private ValidatorHelper validatorHelper;
	
	@Override
	public BaseVO validate(BaseVO vo, ActionType action, ValidatorType validatorType) throws ObjectValidationException {
		log.info("ROLE_INSTRUCTOR Validating {} {} (vo={})", action, validatorType, vo);
		
		List<ValidatorException> exceptionList = new LinkedList<ValidatorException>();
		
		if (ActionType.DELETE == action) {
			log.info("delete action disabled");
			
			String message = String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), action);
			ObjectValidationException e = new ObjectValidationException(message);
			throw e;
		
		} else {
			exceptionList.addAll(validatorHelper.validate(vo, action, validatorType));
			
			ObjectValidationException e = new ObjectValidationException("Validation failed");
			
			for (ValidatorException validationException : exceptionList) {
				//log.error(validationException.getErrorCode().getMessage());
				e.addValidationError(messageSource.getMessage(validationException.getErrorCode().getMessage(), new Object[] {}, LocaleContextHolder.getLocale()));
			}
			
			if (e.hasErrors()) {
				throw e;
			}
		}
		
		return vo;
	}
	
	@Override
	public void validate(CourseVO vo, ActionType action, ValidatorType validatorType) throws ObjectValidationException {
		log.info("ROLE_INSTRUCTOR Validating {} {} (vo={})", action, validatorType, vo);
		
		List<ValidatorException> exceptionList = new LinkedList<ValidatorException>();
		
		// READ/CREATE/UPDATE actions enabled
		if (ActionType.READ == action || ActionType.CREATE == action || ActionType.UPDATE == action) {
			log.info("read/create/update actions only");
			exceptionList.addAll(validatorHelper.validate(vo, action, validatorType));
			
			ObjectValidationException e = new ObjectValidationException("Validation failed");
			
			for (ValidatorException validationException : exceptionList) {
				//log.error(validationException.getErrorCode().getMessage());
				e.addValidationError(messageSource.getMessage(validationException.getErrorCode().getMessage(), new Object[] {}, LocaleContextHolder.getLocale()));
			}
			
			if (e.hasErrors()) {
				throw e;
			}
		
		// ENROLL/WITHDRAW STUDENT actions enabled
		} else if (ValidatorType.STUDENT == validatorType && (ActionType.ENROLL == action || ActionType.WITHDRAW == action)) {
			// intentionally empty
			log.info("intentionally empty {} {}", action, validatorType);
		
		// DELETE, ENROLL/WITHDRAW INSTRUCTOR actions disabled
		} else {
			//String message = String.format(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), action);
			String message = String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), action);
			ObjectValidationException e = new ObjectValidationException(message);
			throw e;
		}
	}

	@Override
	public void validate(ReviewVO vo, ActionType action, ValidatorType validatorType) throws ObjectValidationException {
		log.info("ROLE_INSTRUCTOR Validating {} {} (vo={})", action, validatorType, vo);
		
		List<ValidatorException> exceptionList = new LinkedList<ValidatorException>();
		
		// READ/CREATE/UPDATE actions enabled
		if (ActionType.READ == action || ActionType.CREATE == action || ActionType.UPDATE == action) {
			log.info("read/create/update actions only");
			exceptionList.addAll(validatorHelper.validate(vo, action, validatorType));
			
			ObjectValidationException e = new ObjectValidationException("Validation failed");
			
			for (ValidatorException validationException : exceptionList) {
				//log.error(validationException.getErrorCode().getMessage());
				e.addValidationError(messageSource.getMessage(validationException.getErrorCode().getMessage(), new Object[] {}, LocaleContextHolder.getLocale()));
			}
			
			if (e.hasErrors()) {
				throw e;
			}

		} else {
			//String message = String.format(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), action);
			String message = String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), action);
			ObjectValidationException e = new ObjectValidationException(message);
			throw e;
		}
	}
	
	@Override
	public void validate(int id, ActionType action, ValidatorType validatorType) throws ObjectNotFoundException {
		log.info("ROLE_INSTRUCTOR Validating {} {} (id={})", action, validatorType, id);
		
		List<ValidatorException> exceptionList = new LinkedList<ValidatorException>();
		
		// DELETE action disabled
		if (ActionType.DELETE == action) {
			log.info("delete action disabled");
			
			String message = String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), action);
			ObjectValidationException e = new ObjectValidationException(message);
			throw e;
		
		} else {
			exceptionList.addAll(validatorHelper.validate(id, validatorType));
			
			ObjectValidationException e = new ObjectValidationException("Validation failed");
			
			for (ValidatorException validationException : exceptionList) {
				//log.error(validationException.getErrorCode().getMessage());
				e.addValidationError(messageSource.getMessage(validationException.getErrorCode().getMessage(), new Object[] {}, LocaleContextHolder.getLocale()));
			}
			
			if (e.hasErrors()) {
				throw e;
			}
		}
	}

}
