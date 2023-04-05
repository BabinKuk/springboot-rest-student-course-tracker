package org.babinkuk.service;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.common.ApiResponse;
import org.babinkuk.dao.StudentRepository;
import org.babinkuk.entity.Instructor;
import org.babinkuk.entity.Student;
import org.babinkuk.exception.ObjectException;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.mapper.StudentMapper;
import org.babinkuk.vo.StudentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class StudentServiceImpl implements StudentService {
	
	private final Logger log = LogManager.getLogger(getClass());
	
	private static String STUDENT_SAVE_SUCCESS = "student_save_success";
	private static String STUDENT_DELETE_SUCCESS = "student_delete_success";
	
	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	private ObjectMapper mapper;
		
//	@Autowired
//	private StudentMapper studentMapper;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	public StudentServiceImpl(StudentRepository studentRepository) {
		this.studentRepository = studentRepository;
	}
	
	public StudentServiceImpl() {
		// TODO Auto-generated constructor stub
	}
	
	private String getMessage(String str) {
		return messageSource.getMessage(str, new Object[] {}, LocaleContextHolder.getLocale());
	}
	
	@Override
	public StudentVO findById(int id) throws ObjectNotFoundException {
		
		Optional<Student> result = studentRepository.findById(id);
		
		Student student = null;
		StudentVO studentVO = null;
		
		if (result.isPresent()) {
			student = result.get();
			log.info("student ({})", student);
			
//			// mapping
//			instructorVO = instructorMapper.toVO(instructor);
//			log.info("instVO ({})", instructorVO);
			
			return studentVO;
		} else {
			// not found
			String message = String.format(getMessage("error_code_student_id_not_found"), id);
			log.warn(message);
			throw new ObjectNotFoundException(message);
		}
	}
	
	@Override
	public StudentVO findByEmail(String email) {
		
		StudentVO studentVO = null;
		
		Optional<Student> result = studentRepository.findByEmail(email);
		
		Student student = null;
		
		if (result.isPresent()) {
			student = result.get();
			
//			// mapping
//			studentVO = instructorMapper.toVO(student);
//			log.info("studentVO ({})", studentVO);
		} else {
			// not found
			String message = String.format(getMessage("error_code_student_email_not_found"), email);
			log.warn(message);
			//throw new ObjectNotFoundException(message);
		}

		return studentVO;
	}
		
	@Override
	public ApiResponse saveStudent(StudentVO studentVO) throws ObjectException {
		
		ApiResponse response = new ApiResponse();
		
		response.setStatus(HttpStatus.OK);
		response.setMessage(getMessage(STUDENT_SAVE_SUCCESS));
		
		Optional<Student> entity = studentRepository.findById(studentVO.getId());
		
		Student student = null;
		
		if (entity.isPresent()) {
			student = entity.get();
			log.info("entity ({})", entity);
			log.info("mapping for update");
			
//			// mapping
//			student = instructorMapper.toEntity(studentVO, student);
//			log.info("inst ({})", student);

		} else {
			// instructor not found
			log.info("mapping for insert");
			
//			// mapping
//			student = instructorMapper.toEntity(studentVO);
//			log.info("Instructor ({})", student);
		}
		
		studentRepository.save(student);
		
		return response;
	}
	
	@Override
	public ApiResponse deleteStudent(int id) throws ObjectNotFoundException {
		
		ApiResponse response = new ApiResponse();
		
		response.setStatus(HttpStatus.OK);
		response.setMessage(getMessage(STUDENT_DELETE_SUCCESS));
		
		studentRepository.deleteById(id);
		
		return response;
	}

	@Override
	public Iterable<StudentVO> getAllStudents() {
		//return instructorMapper.toVO(instructorRepository.findAll());
		return null;
	}

}