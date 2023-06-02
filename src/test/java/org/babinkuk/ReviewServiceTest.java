package org.babinkuk;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.dao.ReviewRepository;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.service.ReviewService;
import org.babinkuk.vo.ReviewVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class ReviewServiceTest {
	
	public static final Logger log = LogManager.getLogger(ReviewServiceTest.class);
	
	@Autowired
	private JdbcTemplate jdbc;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private ReviewService reviewService;
	
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
	
	@BeforeEach
    public void setupDatabase() {
		log.info("BeforeEach");
		// todo setup database insert data etc...
		jdbc.execute(sqlAddInstructor);
		jdbc.execute(sqlAddCourse);
		jdbc.execute(sqlAddReview);
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
	
	@AfterEach
	public void setupAfterTransaction() {
		log.info("AfterEach");
		// todo delete database, data etc...
		jdbc.execute(sqlDeleteReview);
		jdbc.execute(sqlDeleteCourse);
		jdbc.execute(sqlDeleteInstructor);
	}
	
	private String getMessage(String str) {
		return messageSource.getMessage(str, new Object[] {}, LocaleContextHolder.getLocale());
	}
}
