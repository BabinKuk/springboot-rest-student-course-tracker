package org.babinkuk.validator;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.exception.ObjectValidationException;
import org.babinkuk.vo.BaseVO;
import org.babinkuk.vo.InstructorVO;
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
@Component("validator.ROLE_EMPLOYEE")
public class ValidatorImplRoleEmployee implements Validator {

private final Logger log = LogManager.getLogger(getClass());
	
	@Autowired
	private InstructorValidatorHelper validatorHelper;
	
	@Autowired
	private MessageSource messageSource;
	
	@Override
	public BaseVO validate(BaseVO vo, boolean isInsert, ActionType action, ValidatorType validatorType) throws ObjectValidationException {
		log.info("ROLE_EMPLOYEE Validating {} {} (vo={})", action, validatorType, vo);
				
		List<ValidatorException> exceptionList = new LinkedList<ValidatorException>();
		
		// only READ action enabled
		if (ActionType.READ == action) {
			log.info("read action only");
			exceptionList.addAll(validatorHelper.validate(vo, isInsert, validatorType));
			
			ObjectValidationException e = new ObjectValidationException("Validation failed");
			
			for (ValidatorException validationException : exceptionList) {
				//log.error(validationException.getErrorCode().getMessage());
				e.addValidationError(messageSource.getMessage(validationException.getErrorCode().getMessage(), new Object[] {}, LocaleContextHolder.getLocale()));
			}
			
			if (e.hasErrors()) {
				throw e;
			}

		} else {
			//String message = String.format("Employee with id=%s not found.", id);
			//String message = String.format(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), action);
			String message = String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), action);
			ObjectValidationException e = new ObjectValidationException(message);
			throw e;
		}
		
		return vo;
	}

	@Override
	public BaseVO validate(int id, ActionType action, ValidatorType validatorType) throws ObjectNotFoundException {
		log.info("ROLE_EMPLOYEE Validating {} {} (id={})", action, validatorType, id);
		
		BaseVO vo = null;
		
		// only READ action enabled
		if (ActionType.READ == action) {
			log.info("read action only");
			
			try {
				vo = validatorHelper.validate(id, validatorType);
			} catch (ObjectNotFoundException e) {
				log.error(e.getMessage());
				throw e;
			}
		
		} else {
			//String message = String.format(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), action);
			String message = String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), action);
			ObjectValidationException e = new ObjectValidationException(message);
			throw e;
		}
		
		return vo;
	}


}
