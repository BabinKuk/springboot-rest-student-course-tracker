//package org.babinkuk.controller;
//
//import org.babinkuk.service.EmployeeService;
//import org.babinkuk.validator.ActionType;
//import org.babinkuk.validator.EmployeeValidatorFactory;
//import org.babinkuk.validator.ValidatorType;
//import org.babinkuk.vo.EmployeeVO;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.babinkuk.common.ApiResponse;
//import org.babinkuk.exception.EmployeeException;
//import org.babinkuk.exception.EmployeeNotFoundException;
//import org.babinkuk.exception.EmployeeValidationException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import java.util.Optional;
//
//import static org.babinkuk.controller.Api.ROOT;
//import static org.babinkuk.controller.Api.EMPLOYEES;
//
//@RestController
//@RequestMapping(ROOT + EMPLOYEES)
//public class EmployeeController {
//	
//	private final Logger log = LogManager.getLogger(getClass());
//	
//	// employee service
//	private EmployeeService employeeService;
//	
//	@Autowired
//	private EmployeeValidatorFactory validatorFactory;
//	
//	@Autowired
//	private ObjectMapper mapper;
//	
//	public EmployeeController() {
//		// TODO Auto-generated constructor stub
//	}
//	
//	@Autowired
//	public EmployeeController(EmployeeService employeeService) {
//		this.employeeService = employeeService;
//	}
//
//	/**
//	 * Create new employee (using topic)
//	 *
//	 * @param employeeVO
//	 * @return ResponseEntity
//	 */
//	@PostMapping("/topic")
//	public ResponseEntity<ApiResponse> addEmployeeTopic(
//			@RequestBody EmployeeVO employeeVO,
//			@RequestParam(name="validationType", required = false) ValidatorType validationType) throws JsonProcessingException {
//		log.info("Called EmployeeController.addEmployeeTopic({})", mapper.writeValueAsString(employeeVO));
//		
//		// in case id is passed in json, set to 0
//		// this is to force a save of new item ... instead of update
//		employeeVO.setId(0);
//		
//		employeeVO = validatorFactory.getValidator(validationType).validate(employeeVO, true, ActionType.CREATE);
//		
//		try {
//	        //return new ResponseEntity<>(MESSAGE_SENT_TO_QUEUE, HttpStatus.OK);
//			//return ResponseEntity.ok().body(employeeResponse);
//			return ResponseEntity.of(Optional.ofNullable(employeeService.sendEmployee(employeeVO, true)));
//			
//		} catch (Exception exception) {
//			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}
//	
//	/**
//	 * Create new employee (using queue)
//	 *
//	 * @param employeeVO
//	 * @return ResponseEntity
//	 */
//	@PostMapping("/queue")
//	public ResponseEntity<ApiResponse> addEmployeeQueue(
//			@RequestBody EmployeeVO employeeVO, 
//			@RequestParam(name="validationType", required = false) ValidatorType validationType) throws JsonProcessingException {
//		log.info("Called EmployeeController.addEmployeeQueue({})", mapper.writeValueAsString(employeeVO));
//		
//		// in case id is passed in json, set to 0
//		// this is to force a save of new item ... instead of update
//		employeeVO.setId(0);
//		
//		employeeVO = validatorFactory.getValidator(validationType).validate(employeeVO, true, ActionType.CREATE);
//				
//		try {
//	        //return new ResponseEntity<>(MESSAGE_SENT_TO_QUEUE, HttpStatus.OK);
//			//return ResponseEntity.ok().body(employeeResponse);
//			return ResponseEntity.of(Optional.ofNullable(employeeService.sendEmployee(employeeVO, false)));
//			
//		} catch (Exception exception) {
//			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}
//	
//	/**
//	 * expose PUT "/employees/topic"
//	 * 
//	 * @param employeeVO
//	 * @return
//	 * @throws JsonProcessingException
//	 */
//	@PutMapping("/topic")
//	public ResponseEntity<ApiResponse> updateEmployeeTopic(
//			@RequestBody EmployeeVO employeeVO,
//			@RequestParam(name="validationType", required = false) ValidatorType validationType) throws JsonProcessingException {
//		log.info("Called EmployeeController.updateEmployeeTopic({})", mapper.writeValueAsString(employeeVO));
//
//		employeeVO = validatorFactory.getValidator(validationType).validate(employeeVO, false, ActionType.UPDATE);
//		
//		try {
//			return ResponseEntity.of(Optional.ofNullable(employeeService.sendEmployee(employeeVO, true)));
//
//		} catch (Exception exception) {
//			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}
//	
//	/**
//	 * expose PUT "/employees/queue"
//	 * 
//	 * @param employeeVO
//	 * @return
//	 * @throws JsonProcessingException
//	 */
//	@PutMapping("/queue")
//	public ResponseEntity<ApiResponse> updateEmployeeQueue(
//			@RequestBody EmployeeVO employeeVO, 
//			@RequestParam(name="validationType", required = false) ValidatorType validationType) throws JsonProcessingException {
//		log.info("Called EmployeeController.updateEmployeeQueue({})", mapper.writeValueAsString(employeeVO));
//
//		employeeVO = validatorFactory.getValidator(validationType).validate(employeeVO, false, ActionType.UPDATE);
//		
//		try {
//			return ResponseEntity.of(Optional.ofNullable(employeeService.sendEmployee(employeeVO, false)));
//			
//		} catch (Exception exception) {
//			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}
//	
//	/**
//	 * get employee list
//	 *
//	 * @param 
//	 * @return ResponseEntity
//	 */
//	@GetMapping("/get")
//	public ResponseEntity<Iterable<EmployeeVO>> getAllEmployees() {
//		log.info("Called EmployeeController.getAllEmployees");
//		//return new ResponseEntity<>(employeeService.findAll(), HttpStatus.OK);
//		//return ResponseEntity.ok().body(employeeService.findAll());
//		return ResponseEntity.of(Optional.ofNullable(employeeService.getAllEmployees()));
//	}
//	
//	/**
//	 * expose GET "/employees/{employeeId}"
//	 *
//	 * @param 
//	 * @return ResponseEntity
//	 */
//	@GetMapping("/get/{employeeId}")
//	public ResponseEntity<EmployeeVO> getEmployee(@PathVariable int employeeId) {
//		log.info("Called EmployeeController.getEmployee(employeeId={})", employeeId);
//		
//		//return new ResponseEntity<>(employee, HttpStatus.OK);
//		//return ResponseEntity.ok().body(employee);
//		return ResponseEntity.of(Optional.ofNullable(employeeService.findById(employeeId)));
//	}
//	
//	/**
//	 * expose POST "/employees"
//	 * 
//	 * @param employeeVO
//	 * @return
//	 * @throws JsonProcessingException
//	 */
//	@PostMapping("")
//	public ResponseEntity<ApiResponse> addEmployee(
//			@RequestBody EmployeeVO employeeVO,
//			@RequestParam(name="validationType", required = false) ValidatorType validationType) throws JsonProcessingException {
//		log.info("Called EmployeeController.addEmployee({})", mapper.writeValueAsString(employeeVO));
//		
//		// in case id is passed in json, set to 0
//		// this is to force a save of new item ... instead of update
//		employeeVO.setId(0);
//		
//		employeeVO = validatorFactory.getValidator(validationType).validate(employeeVO, true, ActionType.CREATE);
//		
//		//return employee;
//		return ResponseEntity.of(Optional.ofNullable(employeeService.saveEmployee(employeeVO)));
//	}
//	
//	/**
//	 * expose PUT "/employees"
//	 * 
//	 * @param employeeVO
//	 * @return
//	 * @throws JsonProcessingException
//	 */
//	@PutMapping("")
//	public ResponseEntity<ApiResponse> updateEmployee(
//			@RequestBody EmployeeVO employeeVO,
//			@RequestParam(name="validationType", required = false) ValidatorType validationType) throws JsonProcessingException {
//		log.info("Called EmployeeController.updateEmployee({})", mapper.writeValueAsString(employeeVO));
//
//		employeeVO = validatorFactory.getValidator(validationType).validate(employeeVO, false, ActionType.UPDATE);
//
////		Employee dbEmployee = employeeService.findById(employee.getId());
////		
////		if (dbEmployee == null) {
////			throw new EmployeeException("Employee id not found " + employee.getId());
////		}
//		
//		//return employee;
//		return ResponseEntity.of(Optional.ofNullable(employeeService.saveEmployee(employeeVO)));
//	}
//	
//	/**
//	 * expose DELETE "/{employeeId}"
//	 * 
//	 * @param employeeId
//	 * @return
//	 */
//	@DeleteMapping("/{employeeId}")
//	public ResponseEntity<ApiResponse> deleteEmployee(
//			@PathVariable int employeeId, 
//			@RequestParam(name="validationType", required = false) ValidatorType validationType) {
//		log.info("Called EmployeeController.deleteEmployee(employeeId={}, validationType={})", employeeId, validationType);
//		
//		EmployeeVO employeeVO = validatorFactory.getValidator(validationType).validate(employeeId, ActionType.DELETE);
//		
//		return ResponseEntity.of(Optional.ofNullable(employeeService.deleteEmployee(employeeId)));
//	}
//
//	@ExceptionHandler
//	public ResponseEntity<ApiResponse> handleException(Exception exc) {
//		
//		return new ApiResponse(HttpStatus.BAD_REQUEST, exc.getMessage()).toEntity();
//	}
//	
//	@ExceptionHandler
//	public ResponseEntity<ApiResponse> handleException(EmployeeException exc) {
//
//		return new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR, exc.getMessage()).toEntity();
//	}
//
//	@ExceptionHandler
//	public ResponseEntity<ApiResponse> handleException(EmployeeNotFoundException exc) {
//
//		return new ApiResponse(HttpStatus.OK, exc.getMessage()).toEntity();
//	}
//	
//	@ExceptionHandler
//	public ResponseEntity<ApiResponse> handleException(EmployeeValidationException exc) {
//		ApiResponse apiResponse = new ApiResponse(HttpStatus.BAD_REQUEST, exc.getMessage());
//		apiResponse.setErrors(exc.getValidationErrors());
//		return apiResponse.toEntity();
//	}
//	
//}
