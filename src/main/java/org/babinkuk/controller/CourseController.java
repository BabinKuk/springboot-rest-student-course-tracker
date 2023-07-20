package org.babinkuk.controller;

import org.babinkuk.service.CourseService;
import org.babinkuk.service.InstructorService;
import org.babinkuk.service.StudentService;
import org.babinkuk.validator.ActionType;
import org.babinkuk.validator.ValidatorFactory;
import org.babinkuk.validator.ValidatorRole;
import org.babinkuk.validator.ValidatorType;
import org.babinkuk.vo.CourseVO;
import org.babinkuk.vo.InstructorVO;
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
	
	// services
	private CourseService courseService;
	
	private StudentService studentService;
	
	private InstructorService instructorService;
	
	@Autowired
	private ValidatorFactory validatorFactory;
	
	@Autowired
	private ObjectMapper mapper;
	
	public CourseController() {
		// TODO Auto-generated constructor stub
	}

	@Autowired
	public CourseController(CourseService courseService, StudentService studentService, InstructorService instructorService) {
		this.courseService = courseService;
		this.studentService = studentService;
		this.instructorService = instructorService;
	}

	/**
	 * expose GET "/courses"
	 * get course list
	 *
	 * @param 
	 * @return ResponseEntity
	 */
	@GetMapping("")
	public ResponseEntity<Iterable<CourseVO>> getAllCourses() {
		log.info("Called CourseController.getAllCourses");

		return ResponseEntity.of(Optional.ofNullable(courseService.getAllCourses()));
	}
	
	/**
	 * expose GET "/courses/{courseId}"
	 * get specific course
	 * 
	 * @param 
	 * @return ResponseEntity
	 */
	@GetMapping("/{courseId}")
	public ResponseEntity<CourseVO> getCourse(@PathVariable int courseId) {
		log.info("Called CourseController.getCourse(courseId={})", courseId);
		
		return ResponseEntity.of(Optional.ofNullable(courseService.findById(courseId)));
	}
	
	/**
	 * expose POST "/courses"
	 * add new course
	 * 
	 * @param courseVO
	 * @param validationRole
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
		
		validatorFactory.getValidator(validationRole).validate(courseVO, ActionType.CREATE, ValidatorType.COURSE);
		
		return ResponseEntity.of(Optional.ofNullable(courseService.saveCourse(courseVO)));
	}
	
	/**
	 * expose PUT "/courses/{courseId}"
	 * update course title
	 * 
	 * @param courseId
	 * @param courseTitle
	 * @param validationRole
	 * @return
	 * @throws JsonProcessingException
	 */
	@PutMapping("/{courseId}")
	public ResponseEntity<ApiResponse> updateCourse(
			//@RequestBody CourseVO courseVO,
			@PathVariable int courseId,
			@RequestParam(name="title", required = true) String courseTitle,
			@RequestParam(name="validationRole", required = false) ValidatorRole validationRole) throws JsonProcessingException {
		log.info("Called CourseController.updateCourse(id={}) newTitle={}", courseId, courseTitle);

		// first find course
		CourseVO courseVO = courseService.findById(courseId);
		
		// next set new title
		courseVO.setTitle(courseTitle);
		
		validatorFactory.getValidator(validationRole).validate(courseVO, ActionType.UPDATE, ValidatorType.COURSE);

		return ResponseEntity.of(Optional.ofNullable(courseService.saveCourse(courseVO)));
	}
	
	/**
	 * expose DELETE "/{courseId}"
	 * 
	 * @param courseId
	 * @param validationRole
	 * @return
	 */
	@DeleteMapping("/{courseId}")
	public ResponseEntity<ApiResponse> deleteCourse(
			@PathVariable int courseId, 
			@RequestParam(name="validationRole", required = false) ValidatorRole validationRole) {
		log.info("Called CourseController.deleteCourse(courseId={}, validationRole={})", courseId, validationRole);
		
		validatorFactory.getValidator(validationRole).validate(courseId, ActionType.DELETE, ValidatorType.COURSE);
		
		return ResponseEntity.of(Optional.ofNullable(courseService.deleteCourse(courseId)));
	}
	
	/**
	 * enroll student on a course
	 * expose PUT "/{courseId}/student/{studentId}/enroll"
	 * 
	 * @param courseId
	 * @param studentId
	 * @param validationRole
	 * @return
	 * @throws JsonProcessingException
	 */
	@PutMapping("/{courseId}/student/{studentId}/enroll")
	public ResponseEntity<ApiResponse> enrollStudent(
			@PathVariable int courseId,
			@PathVariable int studentId,
			@RequestParam(name="validationRole", required = false) ValidatorRole validationRole) throws JsonProcessingException {
		log.info("Called CourseController.enrollStudent(id={}) for courseId={}", studentId, courseId);
		
		// first find course
		CourseVO courseVO = courseService.findById(courseId);
		
		// next find student
		StudentVO studentVO = studentService.findById(studentId);
		
		validatorFactory.getValidator(validationRole).validate(courseVO, ActionType.ENROLL, ValidatorType.STUDENT);
		
		courseVO.addStudentVO(studentVO);
		
		return ResponseEntity.of(Optional.ofNullable(courseService.saveCourse(courseVO)));
	}

	/**
	 * withdraw student from a course
	 * expose PUT "/{courseId}/student/{studentId}/withdraw"
	 * 
	 * @param courseId
	 * @param studentId
	 * @param validationRole
	 * @return
	 * @throws JsonProcessingException
	 */
	@PutMapping("/{courseId}/student/{studentId}/withdraw")
	public ResponseEntity<ApiResponse> withdrawStudent(
			@PathVariable int courseId,
			@PathVariable int studentId,
			@RequestParam(name="validationRole", required = false) ValidatorRole validationRole) throws JsonProcessingException {
		log.info("Called CourseController.withdrawStudent(id={}) for courseId={}", studentId, courseId);
		
		// first find course
		CourseVO courseVO = courseService.findById(courseId);
		
		// next find student
		StudentVO studentVO = studentService.findById(studentId);
		
		validatorFactory.getValidator(validationRole).validate(courseVO, ActionType.WITHDRAW, ValidatorType.STUDENT);
		
		courseVO.removeStudentVO(studentVO);
		
		return ResponseEntity.of(Optional.ofNullable(courseService.saveCourse(courseVO)));
	}
	
	/**
	 * enroll instructor on a course
	 * expose PUT "/{courseId}/instructor/{instructorId}/enroll"
	 * 
	 * @param courseId
	 * @param instructorId
	 * @param validationRole
	 * @return
	 * @throws JsonProcessingException
	 */
	@PutMapping("/{courseId}/instructor/{instructorId}/enroll")
	public ResponseEntity<ApiResponse> enrollInstructor(
			@PathVariable int courseId,
			@PathVariable int instructorId,
			@RequestParam(name="validationRole", required = false) ValidatorRole validationRole) throws JsonProcessingException {
		log.info("Called CourseController.enrollInstructor(id={}) for courseId={}", instructorId, courseId);
		
		// first find course
		CourseVO courseVO = courseService.findById(courseId);
		
		// next find instructor
		InstructorVO instructorVO = instructorService.findById(instructorId);
		
		validatorFactory.getValidator(validationRole).validate(courseVO, ActionType.ENROLL, ValidatorType.INSTRUCTOR);
		
		courseVO.setInstructorVO(instructorVO);
		
		return ResponseEntity.of(Optional.ofNullable(courseService.saveCourse(courseVO)));
	}
	
	/**
	 * withdraw instructor from a course
	 * expose PUT "/{courseId}/instructor/{instructorId}/withdraw"
	 * 
	 * @param courseId
	 * @param instructorId
	 * @param validationRole
	 * @return
	 * @throws JsonProcessingException
	 */
	@PutMapping("/{courseId}/instructor/{instructorId}/withdraw")
	public ResponseEntity<ApiResponse> withdrawInstructor(
			@PathVariable int courseId,
			@PathVariable int instructorId,
			@RequestParam(name="validationRole", required = false) ValidatorRole validationRole) throws JsonProcessingException {
		log.info("Called CourseController.withdrawInstructor(id={}) for courseId={}", instructorId, courseId);
		
		// first find course
		CourseVO courseVO = courseService.findById(courseId);
		
		// next find instructor
		InstructorVO instructorVO = instructorService.findById(instructorId);

		validatorFactory.getValidator(validationRole).validate(courseVO, ActionType.WITHDRAW, ValidatorType.INSTRUCTOR);

		courseVO.setInstructorVO(null);
		
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
