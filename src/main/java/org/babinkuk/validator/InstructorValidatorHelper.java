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

@Component
public class InstructorValidatorHelper {
	
	private final Logger log = LogManager.getLogger(getClass());
	
	@Autowired
	private BusinessValidator validator;
	
	public List<ValidatorException> validate(BaseVO vo, boolean isInsert, ValidatorType validatorType) throws ObjectValidationException {
		List<ValidatorException> exceptions = new LinkedList<ValidatorException>();
		
		try {
			validator.validateFirstName(vo.getFirstName());
		} catch (ValidatorException e) {
			exceptions.add(e);
		}
		
		try {
			validator.validateLastName(vo.getLastName());
		} catch (ValidatorException e) {
			exceptions.add(e);
		}
		
		try {
			validator.validateEmail(vo);
		} catch (ValidatorException e) {
			exceptions.add(e);
		}
		
		if (!isInsert) {
			try {
				validator.objectExists(vo);
			} catch (ValidatorException e) {
				exceptions.add(e);
			}
		}
		
		return exceptions;
	}
	
	public BaseVO validate(int id, ValidatorType validatorType) throws ObjectNotFoundException {
		BaseVO vo = null;
		
		try {
			vo = validator.objectExists(id, validatorType);
		} catch (ObjectNotFoundException e) {
			log.error(e.getMessage());
			throw e;
		}
		
		return vo;
	}
}
