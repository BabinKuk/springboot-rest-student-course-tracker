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
 * if special validations are required depending on the role 
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
	public BaseVO validate(BaseVO vo, boolean isInsert, ActionType action, ValidatorType validatorType) throws ObjectValidationException {
		log.info("ROLE_ADMIN Validating {} {} (vo={})", action, validatorType, vo);
		
		List<ValidatorException> exceptionList = new LinkedList<ValidatorException>();
		
		// all action types are enabled
		exceptionList.addAll(validatorHelper.validate(vo, isInsert, validatorType));
		
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
	public BaseVO validate(int id, ActionType action, ValidatorType validatorType) throws ObjectNotFoundException {
		log.info("ROLE_ADMIN Validating {} {} (id={})", action, validatorType, id);
		
		BaseVO vo = null;
		
		// all action types are enabled
		try {
			vo = validatorHelper.validate(id, validatorType);
		} catch (ObjectNotFoundException e) {
			log.error(e.getMessage());
			throw e;
		}
		
		return vo;
	}


}
