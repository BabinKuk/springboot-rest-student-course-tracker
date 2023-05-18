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
 * special validations are required depending on the role
 * implementation class for Admin role
 * 
 * @author Nikola
 *
 */
@Component("validator.ROLE_ADMIN")
public class ValidatorImplRoleAdmin implements Validator {

private final Logger log = LogManager.getLogger(getClass());
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private ValidatorHelper validatorHelper;
	
	@Override
	public BaseVO validate(BaseVO vo, ActionType action, ValidatorType validatorType) throws ObjectValidationException {
		log.info("ROLE_ADMIN Validating {} {} (vo={})", action, validatorType, vo);
		
		List<ValidatorException> exceptionList = new LinkedList<ValidatorException>();
		
		// all action types are enabled
		exceptionList.addAll(validatorHelper.validate(vo, action, validatorType));
		
		ObjectValidationException e = new ObjectValidationException("Validation failed");
		
		for (ValidatorException validationException : exceptionList) {
			//log.error(validationException.getErrorCode().getMessage());
			e.addValidationError(messageSource.getMessage(validationException.getErrorCode().getMessage(), new Object[] {}, LocaleContextHolder.getLocale()));
		}
		
		if (e.hasErrors()) {
			throw e;
		}

		return vo;
	}

	@Override
	public void validate(int id, ActionType action, ValidatorType validatorType) throws ObjectNotFoundException {
		log.info("ROLE_ADMIN Validating {} {} (id={})", action, validatorType, id);
		
		List<ValidatorException> exceptionList = new LinkedList<ValidatorException>();
		
		// all action types are enabled
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

	@Override
	public void validate(CourseVO vo, ActionType action, ValidatorType validatorType) throws ObjectValidationException {
		log.info("ROLE_ADMIN Validating course {} {} (vo={})", action, validatorType, vo);
		
		List<ValidatorException> exceptionList = new LinkedList<ValidatorException>();
		
		// all action types are enabled
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

	@Override
	public void validate(ReviewVO vo, ActionType action, ValidatorType validatorType) throws ObjectValidationException {
		log.info("ROLE_ADMIN Validating review {} {} (vo={})", action, validatorType, vo);
		
		List<ValidatorException> exceptionList = new LinkedList<ValidatorException>();
		
		// all action types are enabled
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

}
