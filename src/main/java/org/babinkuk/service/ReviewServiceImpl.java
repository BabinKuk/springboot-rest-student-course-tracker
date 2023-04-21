package org.babinkuk.service;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.common.ApiResponse;
import org.babinkuk.dao.CourseRepository;
import org.babinkuk.dao.ReviewRepository;
import org.babinkuk.entity.Course;
import org.babinkuk.entity.Review;
import org.babinkuk.exception.ObjectException;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.mapper.ReviewMapper;
import org.babinkuk.vo.CourseVO;
import org.babinkuk.vo.ReviewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ReviewServiceImpl implements ReviewService {
	
	private final Logger log = LogManager.getLogger(getClass());
	
	private static String REVIEW_SAVE_SUCCESS = "review_save_success";
	private static String REVIEW_DELETE_SUCCESS = "review_delete_success";
	
	@Autowired
	private ReviewRepository reviewRepository;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private ReviewMapper reviewMapper;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private CourseService courseService;
	
	@Autowired
	public ReviewServiceImpl(ReviewRepository reviewRepository) {
		this.reviewRepository = reviewRepository;
	}
	
	public ReviewServiceImpl() {
		// TODO Auto-generated constructor stub
	}
	
	private String getMessage(String str) {
		return messageSource.getMessage(str, new Object[] {}, LocaleContextHolder.getLocale());
	}
	
	@Override
	public ReviewVO findById(int id) throws ObjectNotFoundException {
		
		Optional<Review> result = reviewRepository.findById(id);
		
		Review review = null;
		ReviewVO reviewVO = null;
		
		if (result.isPresent()) {
			review = result.get();
			log.info("inst ({})", review);
			
			// mapping
			reviewVO = reviewMapper.toVO(review);
			log.info("reviewVO ({})", reviewVO);
			
			return reviewVO;
		} else {
			// not found
			String message = String.format(getMessage("error_code_review_id_not_found"), id);
			log.warn(message);
			throw new ObjectNotFoundException(message);
		}
	}
	
	@Override
	public ApiResponse saveReview(ReviewVO reviewVO) throws ObjectException {
		
		ApiResponse response = new ApiResponse();
		
		response.setStatus(HttpStatus.OK);
		response.setMessage(getMessage(REVIEW_SAVE_SUCCESS));
		
		Optional<Review> entity = reviewRepository.findById(reviewVO.getId());
		
		Review review = null;
		
		if (entity.isPresent()) {
			review = entity.get();
			log.info("entity ({})", entity);
			log.info("mapping for update");
			
			// mapping
			review = reviewMapper.toEntity(reviewVO, review);
		} else {
			// review not found
			log.info("mapping for insert");
			
			// mapping
			review = reviewMapper.toEntity(reviewVO);
		}
		
		log.info("review ({})", review);

		reviewRepository.save(review);
		
		return response;
	}
	
	@Override
	public ApiResponse saveReview(CourseVO courseVO) throws ObjectException {
		
		ApiResponse response = new ApiResponse();
		
		response.setStatus(HttpStatus.OK);
		response.setMessage(getMessage(REVIEW_SAVE_SUCCESS));
		
		courseService.saveCourse(courseVO);
		
		return response;
	}
	
	@Override
	public ApiResponse deleteReview(int id) throws ObjectNotFoundException {
		
		ApiResponse response = new ApiResponse();
		
		response.setStatus(HttpStatus.OK);
		response.setMessage(getMessage(REVIEW_DELETE_SUCCESS));
		
		reviewRepository.deleteById(id);
		
		return response;
	}

	@Override
	public Iterable<ReviewVO> getAllReviews() {
		return reviewMapper.toVO(reviewRepository.findAll());
	}

}
