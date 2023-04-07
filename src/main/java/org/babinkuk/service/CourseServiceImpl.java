package org.babinkuk.service;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.common.ApiResponse;
import org.babinkuk.dao.CourseRepository;
import org.babinkuk.entity.Course;
import org.babinkuk.exception.ObjectException;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.mapper.CourseMapper;
import org.babinkuk.vo.CourseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CourseServiceImpl implements CourseService {
	
	private final Logger log = LogManager.getLogger(getClass());
	
	private static String COURSE_SAVE_SUCCESS = "course_save_success";
	private static String COURSE_DELETE_SUCCESS = "course_delete_success";
	
	@Autowired
	private CourseRepository courseRepository;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private CourseMapper courseMapper;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	public CourseServiceImpl(CourseRepository courseRepository) {
		this.courseRepository = courseRepository;
	}
	
	public CourseServiceImpl() {
		// TODO Auto-generated constructor stub
	}
	
	private String getMessage(String str) {
		return messageSource.getMessage(str, new Object[] {}, LocaleContextHolder.getLocale());
	}
	
	@Override
	public CourseVO findById(int id) throws ObjectNotFoundException {
		
		Optional<Course> result = courseRepository.findById(id);
		
		Course course = null;
		CourseVO courseVO = null;
		
		if (result.isPresent()) {
			course = result.get();
			log.info("inst ({})", course);
			
			// mapping
			courseVO = courseMapper.toVO(course);
			log.info("courseVO ({})", courseVO);
			
			return courseVO;
		} else {
			// not found
			String message = String.format(getMessage("error_code_course_id_not_found"), id);
			log.warn(message);
			throw new ObjectNotFoundException(message);
		}
	}
	
//	@Override
//	public CourseVO findByEmail(String email) {
//		
//		CourseVO courseVO = null;
//		
//		Optional<Course> result = courseRepository.findByEmail(email);
//		
//		Course course = null;
//		
//		if (result.isPresent()) {
//			course = result.get();
//			
//			// mapping
//			courseVO = courseMapper.toVO(course);
//			log.info("instVO ({})", courseVO);
//		} else {
//			// not found
//			String message = String.format(getMessage("error_code_course_email_not_found"), email);
//			log.warn(message);
//			//throw new ObjectNotFoundException(message);
//		}
//
//		return courseVO;
//	}
		
	@Override
	public ApiResponse saveCourse(CourseVO courseVO) throws ObjectException {
		
		ApiResponse response = new ApiResponse();
		
		response.setStatus(HttpStatus.OK);
		response.setMessage(getMessage(COURSE_SAVE_SUCCESS));
		
		Optional<Course> entity = courseRepository.findById(courseVO.getId());
		
		Course course = null;
		
		if (entity.isPresent()) {
			course = entity.get();
			log.info("entity ({})", entity);
			log.info("mapping for update");
			
//			// mapping
//			course = courseMapper.toEntity(courseVO, course);
//			log.info("course ({})", course);
		} else {
			// course not found
			log.info("mapping for insert");
			
//			// mapping
//			course = courseMapper.toEntity(courseVO);
//			log.info("course ({})", course);
		}
		
		courseRepository.save(course);
		
		return response;
	}
	
	@Override
	public ApiResponse deleteCourse(int id) throws ObjectNotFoundException {
		
		ApiResponse response = new ApiResponse();
		
		response.setStatus(HttpStatus.OK);
		response.setMessage(getMessage(COURSE_DELETE_SUCCESS));
		
		courseRepository.deleteById(id);
		
		return response;
	}

	@Override
	public Iterable<CourseVO> getAllCourses() {
		return courseMapper.toVO(courseRepository.findAll());
	}

}
