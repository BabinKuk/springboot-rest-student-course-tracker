package org.babinkuk.controller;

import org.babinkuk.service.CourseService;
import org.babinkuk.service.StudentService;
import org.babinkuk.validator.ActionType;
import org.babinkuk.validator.ValidatorFactory;
import org.babinkuk.validator.ValidatorRole;
import org.babinkuk.validator.ValidatorType;
import org.babinkuk.vo.CourseVO;
import org.babinkuk.vo.ReviewVO;
import org.babinkuk.vo.StudentVO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.common.ApiResponse;
import org.babinkuk.exception.ObjectException;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.exception.ObjectValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

import static org.babinkuk.controller.Api.ROOT;
import static org.babinkuk.controller.Api.COURSES;

@RestController
@RequestMapping(ROOT + COURSES)
public class CourseController {
	
	private final Logger log = LogManager.getLogger(getClass());
	
	// service
	private CourseService courseService;
	
	private StudentService studentService;
	
	@Autowired
	private ValidatorFactory validatorFactory;
	
	@Autowired
	private ObjectMapper mapper;
	
	public CourseController() {
		// TODO Auto-generated constructor stub
	}

	@Autowired
	public CourseController(CourseService courseService, StudentService studentService) {
		this.courseService = courseService;
		this.studentService = studentService;
	}

	/**
	 * get course list
	 *
	 * @param 
	 * @return ResponseEntity
	 */
	@GetMapping("/get")
	public ResponseEntity<Iterable<CourseVO>> getAllCourses() {
		log.info("Called CourseController.getAllCourses");

		return ResponseEntity.of(Optional.ofNullable(courseService.getAllCourses()));
	}
	
	/**
	 * expose GET "/courses/get/{courseId}"
	 *
	 * @param 
	 * @return ResponseEntity
	 */
	@GetMapping("/get/{courseId}")
	public ResponseEntity<CourseVO> getCourse(@PathVariable int courseId) {
		log.info("Called CourseController.getCourse(courseId={})", courseId);
		
		return ResponseEntity.of(Optional.ofNullable(courseService.findById(courseId)));
	}
	
	/**
	 * expose POST "/courses"
	 * 
	 * @param courseVO
	 * @return
	 * @throws JsonProcessingException
	 */
	@PostMapping("")
	public ResponseEntity<ApiResponse> addCourse(
			@RequestBody CourseVO courseVO,
			@RequestParam(name="validationRole", required = false) ValidatorRole validationRole) throws JsonProcessingException {
		log.info("Called CourseController.addCourse({})", mapper.writeValueAsString(courseVO));
		
		// in case id is passed in json, set to 0
		// this is to force a save of new item ... instead of update
		courseVO.setId(0);
		
//		courseVO = (CourseVO) validatorFactory.getValidator(validationRole).validate(courseVO, true, ActionType.CREATE, ValidatorType.STUDENT);
		
		return ResponseEntity.of(Optional.ofNullable(courseService.saveCourse(courseVO)));
	}
	
	/**
	 * expose PUT "/courses"
	 * 
	 * @param courseVO
	 * @return
	 * @throws JsonProcessingException
	 */
	@PutMapping("")
	public ResponseEntity<ApiResponse> updateCourse(
			@RequestBody CourseVO courseVO,
			@RequestParam(name="validationRole", required = false) ValidatorRole validationRole) throws JsonProcessingException {
		log.info("Called CourseController.updateCourse({})", mapper.writeValueAsString(courseVO));

//		courseVO = (CourseVO) validatorFactory.getValidator(validationRole).validate(courseVO, false, ActionType.UPDATE, ValidatorType.STUDENT);

		return ResponseEntity.of(Optional.ofNullable(courseService.saveCourse(courseVO)));
	}
	
	/**
	 * expose DELETE "/{courseId}"
	 * 
	 * @param courseId
	 * @return
	 */
	@DeleteMapping("/{courseId}")
	public ResponseEntity<ApiResponse> deleteCourse(
			@PathVariable int courseId, 
			@RequestParam(name="validationRole", required = false) ValidatorRole validationRole) {
		log.info("Called CourseController.deleteCourse(courseId={}, validationType={})", courseId, validationRole);
		
//		CourseVO courseVO = (CourseVO) validatorFactory.getValidator(validationRole).validate(courseId, ActionType.DELETE, ValidatorType.STUDENT);
		
		return ResponseEntity.of(Optional.ofNullable(courseService.deleteCourse(courseId)));
	}
	
	/**
	 * enroll student to the course
	 * expose PUT "/{courseId}/student/{studentId}"
	 * 
	 * @param courseId
	 *@param studentId
	 * @return
	 */
	@PutMapping("/{courseId}/student/{studentId}")
	public ResponseEntity<ApiResponse> enrollStudent(
			@PathVariable int courseId,
			@PathVariable int studentId,
			@RequestParam(name="validationRole", required = false) ValidatorRole validationRole) throws JsonProcessingException {
		log.info("Called ReviewController.enrollStudent(id={}) for courseId={}", studentId, courseId);
		
		// first find course
		CourseVO courseVO = courseService.findById(courseId);
		
		// next find student
		StudentVO studentVO = studentService.findById(studentId);
		
		courseVO.addStudentVO(studentVO);
		
//		reviewVO = (ReviewVO) validatorFactory.getValidator(validationRole).validate(reviewVO, true, ActionType.CREATE, ValidatorType.STUDENT);
		
		return ResponseEntity.of(Optional.ofNullable(courseService.saveCourse(courseVO)));
	}

	@ExceptionHandler
	public ResponseEntity<ApiResponse> handleException(Exception exc) {
		
		return new ApiResponse(HttpStatus.BAD_REQUEST, exc.getMessage()).toEntity();
	}
	
	@ExceptionHandler
	public ResponseEntity<ApiResponse> handleException(ObjectException exc) {

		return new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR, exc.getMessage()).toEntity();
	}

	@ExceptionHandler
	public ResponseEntity<ApiResponse> handleException(ObjectNotFoundException exc) {

		return new ApiResponse(HttpStatus.OK, exc.getMessage()).toEntity();
	}
	
	@ExceptionHandler
	public ResponseEntity<ApiResponse> handleException(ObjectValidationException exc) {
		ApiResponse apiResponse = new ApiResponse(HttpStatus.BAD_REQUEST, exc.getMessage());
		apiResponse.setErrors(exc.getValidationErrors());
		return apiResponse.toEntity();
	}
	
}
