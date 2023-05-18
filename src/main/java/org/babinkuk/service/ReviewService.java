package org.babinkuk.service;

import org.babinkuk.common.ApiResponse;
import org.babinkuk.exception.ObjectException;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.vo.CourseVO;
import org.babinkuk.vo.ReviewVO;

public interface ReviewService {
	
	/**
	 * get all reviews
	 * 
	 * @return Iterable<ReviewVO>
	 */
	public Iterable<ReviewVO> getAllReviews();
	
	/**
	 * get review
	 * 
	 * @param id
	 * @return ReviewVO
	 * @throws ObjectNotFoundException
	 */
	public ReviewVO findById(int id) throws ObjectNotFoundException;
	
	/**
	 * adding new review (related to existing course)
	 * 
	 * @param courseVO
	 * @return
	 * @throws ObjectException
	 */
	public ApiResponse saveReview(CourseVO courseVO) throws ObjectException;
	
	/**
	 * update existing review
	 * 
	 * @param reviewVO
	 * @return
	 * @throws ObjectException
	 */
	public ApiResponse saveReview(ReviewVO reviewVO) throws ObjectException;
	
	/**
	 * delete review
	 * 
	 * @param id
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public ApiResponse deleteReview(int id) throws ObjectNotFoundException;
		
}
