package org.babinkuk.controller;

import org.babinkuk.service.CourseService;
import org.babinkuk.service.ReviewService;
import org.babinkuk.validator.ActionType;
import org.babinkuk.validator.ValidatorFactory;
import org.babinkuk.validator.ValidatorRole;
import org.babinkuk.validator.ValidatorType;
import org.babinkuk.vo.CourseVO;
import org.babinkuk.vo.ReviewVO;
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
import static org.babinkuk.controller.Api.REVIEWS;

@RestController
@RequestMapping(ROOT + REVIEWS)
public class ReviewController {
	
	private final Logger log = LogManager.getLogger(getClass());
	
	// service
	private ReviewService reviewService;
	
	// service
	private CourseService courseService;
	
	@Autowired
	private ValidatorFactory validatorFactory;
	
	@Autowired
	private ObjectMapper mapper;
	
	public ReviewController() {
		// TODO Auto-generated constructor stub
	}

	@Autowired
	public ReviewController(ReviewService reviewService, CourseService courseService) {
		this.reviewService = reviewService;
		this.courseService = courseService;
	}

	/**
	 * get review list
	 *
	 * @param 
	 * @return ResponseEntity
	 */
	@GetMapping("/get")
	public ResponseEntity<Iterable<ReviewVO>> getAllReviews() {
		log.info("Called ReviewController.getAllReviews");

		return ResponseEntity.of(Optional.ofNullable(reviewService.getAllReviews()));
	}
	
	/**
	 * expose GET "/reviews/get/{reviewId}"
	 *
	 * @param 
	 * @return ResponseEntity
	 */
	@GetMapping("/get/{reviewId}")
	public ResponseEntity<ReviewVO> getReview(@PathVariable int reviewId) {
		log.info("Called ReviewController.getReview(reviewId={})", reviewId);
		
		return ResponseEntity.of(Optional.ofNullable(reviewService.findById(reviewId)));
	}
	
	/**
	 * expose POST "/reviews"
	 * 
	 * @param reviewVO
	 * @return
	 * @throws JsonProcessingException
	 */
	@PostMapping("/{courseId}")
	public ResponseEntity<ApiResponse> addReview(
			@PathVariable int courseId,
			@RequestBody ReviewVO reviewVO,
			@RequestParam(name="validationRole", required = false) ValidatorRole validationRole) throws JsonProcessingException {
		log.info("Called ReviewController.addReview({}) for courseId={}", mapper.writeValueAsString(reviewVO), courseId);
		
		// first find course
		CourseVO courseVO = courseService.findById(courseId);
		
		// in case id is passed in json, set to 0
		// this is to force a save of new item ... instead of update
		reviewVO.setId(0);
		
		courseVO.addReviewVO(reviewVO);
		
//		reviewVO = (ReviewVO) validatorFactory.getValidator(validationRole).validate(reviewVO, true, ActionType.CREATE, ValidatorType.STUDENT);
		
		return ResponseEntity.of(Optional.ofNullable(reviewService.saveReview(courseVO)));
	}
	
	/**
	 * expose PUT "/reviews"
	 * 
	 * @param reviewVO
	 * @return
	 * @throws JsonProcessingException
	 */
	@PutMapping("")
	public ResponseEntity<ApiResponse> updateReview(
			@RequestBody ReviewVO reviewVO,
			@RequestParam(name="validationRole", required = false) ValidatorRole validationRole) throws JsonProcessingException {
		log.info("Called ReviewController.updateReview({})", mapper.writeValueAsString(reviewVO));
		
//		// first find course
//		CourseVO courseVO = courseService.findById(reviewVO.getCourseId());
		
//		reviewVO = (ReviewVO) validatorFactory.getValidator(validationRole).validate(reviewVO, false, ActionType.UPDATE, ValidatorType.STUDENT);

		return ResponseEntity.of(Optional.ofNullable(reviewService.saveReview(reviewVO)));
	}
	
	/**
	 * expose DELETE "/{reviewId}"
	 * 
	 * @param reviewId
	 * @return
	 */
	@DeleteMapping("/{reviewId}")
	public ResponseEntity<ApiResponse> deleteReview(
			@PathVariable int reviewId, 
			@RequestParam(name="validationRole", required = false) ValidatorRole validationRole) {
		log.info("Called ReviewController.deleteReview(reviewId={}, validationType={})", reviewId, validationRole);
		
//		ReviewVO reviewVO = (ReviewVO) validatorFactory.getValidator(validationRole).validate(reviewId, ActionType.DELETE, ValidatorType.STUDENT);
		
		return ResponseEntity.of(Optional.ofNullable(reviewService.deleteReview(reviewId)));
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
