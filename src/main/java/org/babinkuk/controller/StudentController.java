package org.babinkuk.controller;


import org.babinkuk.service.StudentService;
import org.babinkuk.validator.ActionType;
//import org.babinkuk.validator.StudentValidatorFactory;
import org.babinkuk.validator.ValidatorType;
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
import static org.babinkuk.controller.Api.STUDENTS;

@RestController
@RequestMapping(ROOT + STUDENTS)
public class StudentController {
	
	private final Logger log = LogManager.getLogger(getClass());
	
	// service
	private StudentService studentService;
	
//	@Autowired
//	private StudentValidatorFactory validatorFactory;
	
	@Autowired
	private ObjectMapper mapper;
	
	public StudentController() {
		// TODO Auto-generated constructor stub
	}

	@Autowired
	public StudentController(StudentService studentService) {
		this.studentService = studentService;
	}

	/**
	 * get student list
	 *
	 * @param 
	 * @return ResponseEntity
	 */
	@GetMapping("/get")
	public ResponseEntity<Iterable<StudentVO>> getAllStudents() {
		log.info("Called StudentController.getAllStudents");

		return ResponseEntity.of(Optional.ofNullable(studentService.getAllStudents()));
	}
	
	/**
	 * expose GET "/students/get/{studentId}"
	 *
	 * @param 
	 * @return ResponseEntity
	 */
	@GetMapping("/get/{studentId}")
	public ResponseEntity<StudentVO> getStudent(@PathVariable int studentId) {
		log.info("Called StudentController.getStudent(studentId={})", studentId);
		
		return ResponseEntity.of(Optional.ofNullable(studentService.findById(studentId)));
	}
	
	/**
	 * expose POST "/students"
	 * 
	 * @param studentVO
	 * @return
	 * @throws JsonProcessingException
	 */
	@PostMapping("")
	public ResponseEntity<ApiResponse> addStudent(
			@RequestBody StudentVO studentVO/*,
			@RequestParam(name="validationType", required = false) ValidatorType validationType*/) throws JsonProcessingException {
		log.info("Called StudentController.addStudent({})", mapper.writeValueAsString(studentVO));
		
		// in case id is passed in json, set to 0
		// this is to force a save of new item ... instead of update
		studentVO.setId(0);
		
//		studentVO = validatorFactory.getValidator(validationType).validate(studentVO, true, ActionType.CREATE);
		
		return ResponseEntity.of(Optional.ofNullable(studentService.saveStudent(studentVO)));
	}
	
	/**
	 * expose PUT "/students"
	 * 
	 * @param studentVO
	 * @return
	 * @throws JsonProcessingException
	 */
	@PutMapping("")
	public ResponseEntity<ApiResponse> updateStudent(
			@RequestBody StudentVO studentVO/*,
			@RequestParam(name="validationType", required = false) ValidatorType validationType*/) throws JsonProcessingException {
		log.info("Called StudentController.updateStudent({})", mapper.writeValueAsString(studentVO));

//		studentVO = validatorFactory.getValidator(validationType).validate(studentVO, false, ActionType.UPDATE);

		return ResponseEntity.of(Optional.ofNullable(studentService.saveStudent(studentVO)));
	}
	
	/**
	 * expose DELETE "/{studentId}"
	 * 
	 * @param studentId
	 * @return
	 */
	@DeleteMapping("/{studentId}")
	public ResponseEntity<ApiResponse> deleteStudent(
			@PathVariable int studentId/*, 
			@RequestParam(name="validationType", required = false) ValidatorType validationType*/) {
//		log.info("Called StudentController.deleteStudent(studentId={}, validationType={})", studentId, validationType);
		
//		StudentVO studentVO = validatorFactory.getValidator(validationType).validate(studentId, ActionType.DELETE);
		
		return ResponseEntity.of(Optional.ofNullable(studentService.deleteStudent(studentId)));
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
