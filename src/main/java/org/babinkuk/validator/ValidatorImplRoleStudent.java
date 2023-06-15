package org.babinkuk.validator;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.exception.ObjectValidationException;
import org.babinkuk.vo.BaseVO;
import org.babinkuk.vo.CourseVO;
import org.babinkuk.vo.ReviewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * if special valdations are required depending on the role 
 * implementation class for Student role
 * 
 * @author Nikola
 *
 */
@Component("validator.ROLE_STUDENT")
public class ValidatorImplRoleStudent implements Validator {

private final Logger log = LogManager.getLogger(getClass());
	
	@Autowired
	private ValidatorHelper validatorHelper;
	
	@Autowired
	private MessageSource messageSource;
	
	@Override
	public BaseVO validate(BaseVO vo, ActionType action, ValidatorType validatorType) throws ObjectValidationException {
		log.info("ROLE_STUDENT Validating {} {} (vo={})", action, validatorType, vo);
				
		List<ValidatorException> exceptionList = new LinkedList<ValidatorException>();
		
		// only READ action enabled
		if (ActionType.READ == action) {
			log.info("read action only");
			exceptionList.addAll(validatorHelper.validate(vo, action, validatorType));
			
			String message = String.format(messageSource.getMessage("validation_failed", new Object[] {}, LocaleContextHolder.getLocale()), action);
			ObjectValidationException e = new ObjectValidationException(message);
			
			for (ValidatorException validationException : exceptionList) {
				//log.error(validationException.getErrorCode().getMessage());
				e.addValidationError(messageSource.getMessage(validationException.getErrorCode().getMessage(), new Object[] {}, LocaleContextHolder.getLocale()));
			}
			
			if (e.hasErrors()) {
				throw e;
			}

		} else {
			String message = String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), action);
			ObjectValidationException e = new ObjectValidationException(message);
			throw e;
		}
		
		return vo;
	}

	@Override
	public void validate(CourseVO vo, ActionType action, ValidatorType validatorType) throws ObjectValidationException {
		log.info("ROLE_STUDENT Validating {} {} (vo={})", action, validatorType, vo);
		
		List<ValidatorException> exceptionList = new LinkedList<ValidatorException>();
		
		// only READ action enabled
		if (ActionType.READ == action) {
			log.info("read action only");
			exceptionList.addAll(validatorHelper.validate(vo, action, validatorType));
			
			String message = String.format(messageSource.getMessage("validation_failed", new Object[] {}, LocaleContextHolder.getLocale()), action);
			ObjectValidationException e = new ObjectValidationException(message);
			
			for (ValidatorException validationException : exceptionList) {
				//log.error(validationException.getErrorCode().getMessage());
				e.addValidationError(messageSource.getMessage(validationException.getErrorCode().getMessage(), new Object[] {}, LocaleContextHolder.getLocale()));
			}
			
			if (e.hasErrors()) {
				throw e;
			}
			
		} else {
			String message = String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), action);
			ObjectValidationException e = new ObjectValidationException(message);
			throw e;
		}
	}

	@Override
	public void validate(ReviewVO vo, ActionType action, ValidatorType validatorType) throws ObjectValidationException {
		log.info("ROLE_STUDENT Validating {} {} (vo={})", action, validatorType, vo);
		
		List<ValidatorException> exceptionList = new LinkedList<ValidatorException>();
		
		// only READ and CREATE actions enabled
		if (ActionType.READ == action || ActionType.CREATE == action) {
			log.info("read/create actions only");
			exceptionList.addAll(validatorHelper.validate(vo, action, validatorType));
			
			String message = String.format(messageSource.getMessage("validation_failed", new Object[] {}, LocaleContextHolder.getLocale()), action);
			ObjectValidationException e = new ObjectValidationException(message);
			
			for (ValidatorException validationException : exceptionList) {
				//log.error(validationException.getErrorCode().getMessage());
				e.addValidationError(messageSource.getMessage(validationException.getErrorCode().getMessage(), new Object[] {}, LocaleContextHolder.getLocale()));
			}
			
			if (e.hasErrors()) {
				throw e;
			}

		} else {
			String message = String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), action);
			ObjectValidationException e = new ObjectValidationException(message);
			throw e;
		}
	}
	
	@Override
	public void validate(int id, ActionType action, ValidatorType validatorType) throws ObjectNotFoundException {
		log.info("ROLE_STUDENT Validating {} {} (id={})", action, validatorType, id);
		
		List<ValidatorException> exceptionList = new LinkedList<ValidatorException>();
		
		// only READ action enabled
		if (ActionType.READ == action) {
			log.info("read action only");
			
			exceptionList.addAll(validatorHelper.validate(id, validatorType));
			
			String message = String.format(messageSource.getMessage("validation_failed", new Object[] {}, LocaleContextHolder.getLocale()), action);
			ObjectValidationException e = new ObjectValidationException(message);
			
			for (ValidatorException validationException : exceptionList) {
				//log.error(validationException.getErrorCode().getMessage());
				e.addValidationError(messageSource.getMessage(validationException.getErrorCode().getMessage(), new Object[] {}, LocaleContextHolder.getLocale()));
			}
			
			if (e.hasErrors()) {
				throw e;
			}
		
		} else {
			String message = String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), action);
			ObjectValidationException e = new ObjectValidationException(message);
			throw e;
		}
	}

}
