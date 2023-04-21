package org.babinkuk.service;

import org.babinkuk.common.ApiResponse;
import org.babinkuk.exception.ObjectException;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.vo.CourseVO;
import org.babinkuk.vo.ReviewVO;

public interface ReviewService {
	
	public Iterable<ReviewVO> getAllReviews();
	
	public ReviewVO findById(int id) throws ObjectNotFoundException;
	
	//public CourseVO findByEmail(String email) throws ObjectNotFoundException;
	
	/**
	 * adding new review (related to existing course)
	 * @param courseVO
	 * @return
	 * @throws ObjectException
	 */
	public ApiResponse saveReview(CourseVO courseVO) throws ObjectException;
	
	/**
	 * update existing review
	 * @param reviewVO
	 * @return
	 * @throws ObjectException
	 */
	public ApiResponse saveReview(ReviewVO reviewVO) throws ObjectException;
	
	public ApiResponse deleteReview(int id) throws ObjectNotFoundException;
		
}
