package org.babinkuk.validator;

import java.util.Arrays;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.service.InstructorService;
import org.babinkuk.vo.BaseVO;
import org.babinkuk.vo.InstructorVO;
import org.babinkuk.vo.StudentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BusinessValidator {
	
	private final Logger log = LogManager.getLogger(getClass());
	
	@Autowired
	private InstructorService instructorService;
	
	/**
	 * @param name
	 * @throws ValidationException
	 */
	public void validateFirstName(String name) throws ValidatorException {
		validateStringIsBlank(name, ValidatorCodes.ERROR_CODE_FIRST_NAME_EMPTY);
	}
	
	/**
	 * @param name
	 * @throws ValidationException
	 */
	public void validateLastName(String name) throws ValidatorException {
		validateStringIsBlank(name, ValidatorCodes.ERROR_CODE_LAST_NAME_EMPTY);
	}
	
	/**
	 * @param email
	 * @throws ValidationException
	 */
	public void validateEmail(InstructorVO vo) throws ValidatorException {
		validateStringIsBlank(vo.getEmailAddress(), ValidatorCodes.ERROR_CODE_EMAIL_EMPTY);
		validateEmailFormat(vo.getEmailAddress(), ValidatorCodes.ERROR_CODE_EMAIL_INVALID);
		emailExists(vo);
	}
	
	/**
	 * @param email
	 * @param errorCode
	 * @throws ValidatorException
	 */
	public void validateEmailFormat(String email, ValidatorCodes errorCode) throws ValidatorException {
		if (!validateEmailAddress(email)) {
			throw new ValidatorException(errorCode);
		}
	}

	/**
	 * validate email format
	 * @param email
	 * @return
	 */
	private boolean validateEmailAddress(String email) {
		if (StringUtils.isNotBlank(email)) {
			email = StringUtils.upperCase(StringUtils.replace(email, " ", ""));
			for (String pattern : Arrays.asList("[A-Z0-9._%+-]+@[A-Z0-9.-]+")) {
				if (Pattern.matches(pattern, email)) {
					return true;
				}
			}
			return false;
		} else {
			// if empty return true
			return true;
		}
		
	}

	/**
	 * validate if email already exist must be unique (call repository findByEmail)
	 * @param vo
	 * @param isInsert
	 * @return
	 * @throws ValidatorException
	 */
	public void emailExists(InstructorVO vo) throws ValidatorException {
		//validateStringIsBlank(employee, ValidatorCodes.ERROR_CODE_EMPLOYEE_INVALID);
		
		//BaseVO dbVO = null;
		//if (vo instanceof InstructorVO) {
		
		//}
		InstructorVO dbVO = instructorService.findByEmail(vo.getEmailAddress());
		
		if (dbVO == null) {
			// email not found
			// that's ok
			log.info("email not found");
		} else {
			log.info("email found");
			if (dbVO.getId() == vo.getId()) {
				// same employee, email has not changed
				log.info("belongs to same instructor/student, email has not changed");
			} else {
				// another employee with same email already exists in db
				log.error(ValidatorCodes.ERROR_CODE_EMAIL_ALREADY_EXIST.getMessage());
				throw new ValidatorException(ValidatorCodes.ERROR_CODE_EMAIL_ALREADY_EXIST);
			}
		}
	}
	
	/**
	 * validate if object already exist
	 * @param employee
	 * @param isInsert
	 * @return
	 * @throws ValidatorException
	 */
	public void objectExists(InstructorVO vo) throws ValidatorException {
		
		InstructorVO result;
		//if (vo instanceof InstructorVO) {
			log.info("validate instructor on update");
			result = instructorService.findById(vo.getId());
			
			if (result != null) {
				// id found
				log.info("instructor id found");
			} else {
				// id not found
				log.error("result.notPresent");
				throw new ValidatorException(ValidatorCodes.ERROR_CODE_INSTRUCTOR_INVALID);
			}
//		} else if (vo instanceof StudentVO) {
//			log.info("validate student on update");
//			result = studentService.findById(vo.getId());
//			
//			if (result != null) {
//				// id found
//				log.info("instructor id found");
//			} else {
//				// id not found
//				log.error("result.notPresent");
//				throw new ValidatorException(ValidatorCodes.ERROR_CODE_STUDENT_INVALID);
//			}
//			
//		}
		
	}

	/**
	 * @param str
	 * @param errorCode
	 * @throws ValidatorException
	 */
	private void validateStringIsBlank(String str, ValidatorCodes errorCode) throws ValidatorException {
		if (StringUtils.isBlank(str)) {
			throw new ValidatorException(errorCode);
		}
	}

	/**
	 * @param id
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public InstructorVO objectExists(int id) throws ObjectNotFoundException {
		
		InstructorVO dbVO = instructorService.findById(id);
		
//		if (dbVO == null) {
//			String message = String.format("Instructor with id=%s not found.", id);
//			log.warn(message);
//			throw new ObjectNotFoundException(message);
//		}
		
		return dbVO;
	}

}
