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
@Component("validator.ROLE_MANAGER")
public class InstructorValidatorImplRoleManager implements InstructorValidator {

private final Logger log = LogManager.getLogger(getClass());
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private InstructorValidatorHelper validatorHelper;
	
	@Override
	public InstructorVO validate(InstructorVO vo, boolean isInsert, ActionType action) throws ObjectValidationException {
		log.info("ROLE_MANAGER Validating {} (vo={})", action, vo);
		
		List<ValidatorException> exceptionList = new LinkedList<ValidatorException>();
		
		if (ActionType.DELETE == action) {
			log.info("delete action disabled");
			
			//String message = String.format(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), action);
			String message = String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), action);
			ObjectValidationException e = new ObjectValidationException(message);
			throw e;
		
		} else {
			exceptionList.addAll(validatorHelper.validate(vo, isInsert));
			
			ObjectValidationException e = new ObjectValidationException("Validation failed");
			
			for (ValidatorException validationException : exceptionList) {
				//log.error(validationException.getErrorCode().getMessage());
				//e.addValidationError(validationException.getErrorCode().getMessage());
				e.addValidationError(messageSource.getMessage(validationException.getErrorCode().getMessage(), new Object[] {}, LocaleContextHolder.getLocale()));
			}
			
			if (e.hasErrors()) {
				throw e;
			}
		}
		
		return vo;
	}
	
	@Override
	public InstructorVO validate(int id, ActionType action) throws ObjectNotFoundException {
		log.info("ROLE_MANAGER Validating {} (id={})", action, id);
		
		InstructorVO vo = null;
		
		// DELETE action disabled
		if (ActionType.DELETE == action) {
			log.info("delete action disabled");
			
			//String message = String.format(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), action);
			String message = String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), action);
			ObjectValidationException e = new ObjectValidationException(message);
			throw e;
		
		} else {
			try {
				vo = validatorHelper.validate(id);
			} catch (ObjectNotFoundException e) {
				log.error(e.getMessage());
				throw e;
			}
		}
		
		return vo;
	}

}
