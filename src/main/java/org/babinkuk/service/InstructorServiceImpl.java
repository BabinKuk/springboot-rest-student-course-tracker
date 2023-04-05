package org.babinkuk.service;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.common.ApiResponse;
import org.babinkuk.dao.InstructorRepository;
import org.babinkuk.entity.Instructor;
import org.babinkuk.exception.ObjectException;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.mapper.InstructorMapper;
import org.babinkuk.vo.InstructorVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class InstructorServiceImpl implements InstructorService {
	
	private final Logger log = LogManager.getLogger(getClass());
	
	private static String MESSAGE_SUCCESS = "message_success";
	private static String MESSAGE_FAILED = "message_failure";
	private static String INSTRUCTOR_SAVE_SUCCESS = "instructor_save_success";
	private static String INSTRUCTOR_DELETE_SUCCESS = "instructor_delete_success";
	
	@Autowired
	private InstructorRepository instructorRepository;
	
	@Autowired
	JmsTemplate jmsTemplate;
	
	@Autowired
	private ObjectMapper mapper;
		
	@Autowired
	private InstructorMapper instructorMapper;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	public InstructorServiceImpl(InstructorRepository instructorRepository) {
		this.instructorRepository = instructorRepository;
	}
	
	public InstructorServiceImpl() {
		// TODO Auto-generated constructor stub
	}
	
	private String getMessage(String str) {
		return messageSource.getMessage(str, new Object[] {}, LocaleContextHolder.getLocale());
	}
	
	@Override
	public InstructorVO findById(int id) throws ObjectNotFoundException {
		
		Optional<Instructor> result = instructorRepository.findById(id);
		
		Instructor instructor = null;
		InstructorVO instructorVO = null;
		
		if (result.isPresent()) {
			instructor = result.get();
			log.info("inst ({})", instructor);
			
			// mapping
			instructorVO = instructorMapper.toVO(instructor);
			log.info("instVO ({})", instructorVO);
			
			return instructorVO;
		} else {
			// not found
			String message = String.format(getMessage("error_code_instructor_id_not_found"), id);
			log.warn(message);
			throw new ObjectNotFoundException(message);
		}
	}
	
	@Override
	public InstructorVO findByEmail(String email) {
		
		InstructorVO instructorVO = null;
		
		Optional<Instructor> result = instructorRepository.findByEmail(email);
		
		Instructor instructor = null;
		
		if (result.isPresent()) {
			instructor = result.get();
			
			// mapping
			instructorVO = instructorMapper.toVO(instructor);
			log.info("instVO ({})", instructorVO);
		} else {
			// not found
			String message = String.format(getMessage("error_code_instructor_email_not_found"), email);
			log.warn(message);
			//throw new ObjectNotFoundException(message);
		}

		return instructorVO;
	}
		
	@Override
	public ApiResponse saveInstructor(InstructorVO instructorVO) throws ObjectException {
		
		ApiResponse response = new ApiResponse();
		
		response.setStatus(HttpStatus.OK);
		response.setMessage(getMessage(INSTRUCTOR_SAVE_SUCCESS));
		
		Optional<Instructor> entity = instructorRepository.findById(instructorVO.getId());
		
		Instructor instructor = null;
		
		if (entity.isPresent()) {
			instructor = entity.get();
			log.info("entity ({})", entity);
			log.info("mapping for update");
			
			// mapping
			instructor = instructorMapper.toEntity(instructorVO, instructor);
			log.info("inst ({})", instructor);

		} else {
			// instructor not found
			log.info("mapping for insert");
			
			// mapping
			instructor = instructorMapper.toEntity(instructorVO);
			log.info("Instructor ({})", instructor);
		}
		
		instructorRepository.save(instructor);
		
		return response;
	}
	
	@Override
	public ApiResponse deleteInstructor(int id) throws ObjectNotFoundException {
		
		ApiResponse response = new ApiResponse();
		
		response.setStatus(HttpStatus.OK);
		response.setMessage(getMessage(INSTRUCTOR_DELETE_SUCCESS));
		
		instructorRepository.deleteById(id);
		
		return response;
	}

	@Override
	public Iterable<InstructorVO> getAllInstructors() {
		return instructorMapper.toVO(instructorRepository.findAll());
	}

}
