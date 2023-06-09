package org.babinkuk;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.dao.ReviewRepository;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.service.CourseService;
import org.babinkuk.service.ReviewService;
import org.babinkuk.vo.CourseVO;
import org.babinkuk.vo.ReviewVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ReviewServiceTest {
	
	public static final Logger log = LogManager.getLogger(ReviewServiceTest.class);
	
	@Autowired
	private JdbcTemplate jdbc;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private ReviewService reviewService;
	
	@Autowired
	private CourseService courseService;
	
	@Autowired
	private ReviewRepository reviewRepository;
	
	@Value("${sql.script.review.insert}")
	private String sqlAddReview;
	
	@Value("${sql.script.review.delete}")
	private String sqlDeleteReview;
	
	@Value("${sql.script.course.insert}")
	private String sqlAddCourse;
	
	@Value("${sql.script.course.delete}")
	private String sqlDeleteCourse;
	
	@Value("${sql.script.instructor.insert}")
	private String sqlAddInstructor;
	
	@Value("${sql.script.instructor.delete}")
	private String sqlDeleteInstructor;
	
	@Value("${sql.script.student.insert}")
	private String sqlAddStudent;
	
	@Value("${sql.script.student.delete}")
	private String sqlDeleteStudent;
	
	@Value("${sql.script.course-student.insert}")
	private String sqlAddCourseStudent;
	
	@Value("${sql.script.course-student.delete}")
	private String sqlDeleteCourseStudent;
	
	@BeforeEach
    public void setupDatabase() {
		log.info("BeforeEach");

		jdbc.execute(sqlAddInstructor);
		jdbc.execute(sqlAddCourse);
		jdbc.execute(sqlAddReview);
		jdbc.execute(sqlAddStudent);
		jdbc.execute(sqlAddCourseStudent);
	}
	
	@Test
	void getReview() {
		log.info("getReview");
		
		ReviewVO reviewVO = reviewService.findById(1);
		
		assertNotNull(reviewVO,"reviewVO null");
		assertEquals(1, reviewVO.getId());
		assertNotNull(reviewVO.getComment(),"reviewVO.getComment() null");
		assertEquals("test review", reviewVO.getComment(),"assertEquals reviewVO.getComment() failure");
		
		assertNotEquals("test review ", reviewVO.getComment(),"assertEquals reviewVO.getComment() failure");
		
		// assert not existing review
		Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
			reviewService.findById(2);
		});
		
		String expectedMessage = "Review with id=2 not found.";
		String actualMessage = exception.getMessage();

	    assertTrue(actualMessage.contains(expectedMessage));
	}
	
	@Test
	void addReview() {
		log.info("addReview");
		
		// first find review
		CourseVO courseVO = courseService.findById(1);
		
		// create review
		// set id 0: this is to force a save of new item ... instead of update
		ReviewVO reviewVO = new ReviewVO("new review");
		reviewVO.setId(0);
		
		// add to course
		courseVO.addReviewVO(reviewVO);
		
		reviewService.saveReview(courseVO);
		
		// assert
		assertEquals(2, courseVO.getReviewsVO().size());
		
		for (ReviewVO reVo : courseVO.getReviewsVO()) {
			assertNotNull(reVo.getComment());
		}
	}	
	
	@Test
	void updateReview() {
		log.info("updateReview");
		
		ReviewVO reviewVO = reviewService.findById(1);
		
		// update comment
		String newComment = "update test review";
		reviewVO.setComment(newComment);
		
		reviewService.saveReview(reviewVO);
		
		// fetch again
		reviewVO = reviewService.findById(1);
		
		// assert
		assertEquals(newComment, reviewVO.getComment(), "find by id after update");
	}
	
	@Test
	void deleteReview() {
		log.info("deleteReview");
		
		// first get review
		ReviewVO reviewVO = reviewService.findById(1);
		
		// assert
		assertNotNull(reviewVO, "return true");
		assertEquals(1, reviewVO.getId());
		assertNotNull(reviewVO.getComment(),"reviewVO.getComment() null");
		
		// delete
		reviewService.deleteReview(1);
		
		// assert not existing review
		Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
			reviewService.findById(1);
		});
				
		String expectedMessage = "Review with id=1 not found.";
		String actualMessage = exception.getMessage();
		
	    assertTrue(actualMessage.contains(expectedMessage));

		// delete not existing review
		exception = assertThrows(EmptyResultDataAccessException.class, () -> {
			reviewService.deleteReview(2);
		});
	}
	
	@Test
	void getAllReviews() {
		log.info("getAllReviews");
		
		Iterable<ReviewVO> reviews = reviewService.getAllReviews();
		
		// assert
		if (reviews instanceof Collection<?>) {
			assertEquals(1, ((Collection<?>) reviews).size(), "reviews size not 1");
		}
		
		// add new review
		CourseVO courseVO = courseService.findById(1);
		
		// create review
		// set id 0: this is to force a save of new item ... instead of update
		ReviewVO reviewVO = new ReviewVO("new review");
		reviewVO.setId(0);
		
		// add to course
		courseVO.addReviewVO(reviewVO);
		
		reviewService.saveReview(courseVO);
		
		reviews = reviewService.getAllReviews();
		
		// assert
		if (reviews instanceof Collection<?>) {
			assertEquals(2, ((Collection<?>) reviews).size(), "reviews size not 2");
		}
		
//		// delete review
//		reviewService.deleteReview(2);
//		
//		reviews = reviewService.getAllReviews();
//		log.info("after delete " + reviews.toString());
//		
//		// assert
//		if (reviews instanceof Collection<?>) {
//			assertEquals(1, ((Collection<?>) reviews).size(), "reviews size not 1");
//		}
	}
	
	@AfterEach
	public void setupAfterTransaction() {
		log.info("AfterEach");

		jdbc.execute(sqlDeleteCourseStudent);
		jdbc.execute(sqlDeleteStudent);
		jdbc.execute(sqlDeleteReview);
		jdbc.execute(sqlDeleteCourse);
		jdbc.execute(sqlDeleteInstructor);
	}
	
	private String getMessage(String str) {
		return messageSource.getMessage(str, new Object[] {}, LocaleContextHolder.getLocale());
	}
}
