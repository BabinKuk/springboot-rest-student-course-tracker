package org.babinkuk.validator;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.service.CourseService;
import org.babinkuk.service.ReviewService;
import org.babinkuk.vo.ReviewVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.babinkuk.controller.Api.ROOT;
import static org.babinkuk.controller.Api.REVIEWS;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
public class ReviewValidatorTest {
	
	public static final Logger log = LogManager.getLogger(ReviewValidatorTest.class);
	
	private static String ROLE_ADMIN = "ROLE_ADMIN";
	private static String ROLE_INSTRUCTOR = "ROLE_INSTRUCTOR";
	private static String ROLE_STUDENT = "ROLE_STUDENT";
	private static String VALIDATION_FAILED = "validation_failed";
	
	private static MockHttpServletRequest request;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private JdbcTemplate jdbc;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	ObjectMapper objectMApper;
	
	@Autowired
	private ReviewService reviewService;
	
	@Autowired
	private CourseService courseService;
	
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
	
	@Value("${sql.script.instructor-detail.insert}")
	private String sqlAddInstructorDetail;
	
	@Value("${sql.script.instructor-detail.delete}")
	private String sqlDeleteInstructorDetail;
	
	@Value("${sql.script.student.insert}")
	private String sqlAddStudent;
	
	@Value("${sql.script.student.delete}")
	private String sqlDeleteStudent;
	
	@Value("${sql.script.course-student.insert}")
	private String sqlAddCourseStudent;
	
	@Value("${sql.script.course-student.delete}")
	private String sqlDeleteCourseStudent;
	
	public static final MediaType APPLICATION_JSON_UTF8 = MediaType.APPLICATION_JSON;
	
	@BeforeAll
	public static void setup() {
		log.info("BeforeAll");

		// init
		request = new MockHttpServletRequest();
	}
	
	@BeforeEach
    public void setupDatabase() {
		log.info("BeforeEach");

		jdbc.execute(sqlAddInstructorDetail);
		jdbc.execute(sqlAddInstructor);
		jdbc.execute(sqlAddCourse);
		jdbc.execute(sqlAddReview);
		jdbc.execute(sqlAddStudent);
		jdbc.execute(sqlAddCourseStudent);
	}
	
	@AfterEach
	public void setupAfterTransaction() {
		log.info("AfterEach");

		jdbc.execute(sqlDeleteCourseStudent);
		jdbc.execute(sqlDeleteStudent);
		jdbc.execute(sqlDeleteReview);
		jdbc.execute(sqlDeleteCourse);
		jdbc.execute(sqlDeleteInstructor);
		jdbc.execute(sqlDeleteInstructorDetail);
	}
	
	@Test
	void addEmptyReviewRoleAdmin() throws Exception {

		addEmptyReview(ROLE_ADMIN);
	}
	
	@Test
	void addEmptyReviewRoleInstructor() throws Exception {

		addEmptyReview(ROLE_INSTRUCTOR);
	}
	
	@Test
	void addEmptyReviewRoleStudent() throws Exception {

		addEmptyReview(ROLE_STUDENT);
	}
	
	private void addEmptyReview(String validationRole) throws Exception {
		log.info("addEmptyReview {}", validationRole);
		
		// create invalid review 
		ReviewVO reviewVO = new ReviewVO("");
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + REVIEWS + "/{courseId}", 1)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(reviewVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(getMessage(VALIDATION_FAILED), ActionType.CREATE)))) // verify json root element message
			.andExpect(jsonPath("$.errors", hasSize(1))) // verify that json root element $ is size 1
			.andExpect(jsonPath("$.errors[0]", is(String.format(getMessage(ValidatorCodes.ERROR_CODE_REVIEW_EMPTY.getMessage()), ActionType.CREATE))))
			;
		
		// additional check
		// get all reviews
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS)
				.param("validationRole", validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is now size 1
			.andDo(MockMvcResultHandlers.print())
			;
	}
	
	@Test
	void addEmptyReviewNoRole() throws Exception {
		log.info("addEmptyReviewNoRole");
		
		//String validationRole = ROLE_STUDENT;
		
		// create invalid review 
		ReviewVO reviewVO = new ReviewVO("");
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + REVIEWS + "/{courseId}", 1)
				//.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(reviewVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(getMessage(VALIDATION_FAILED), ActionType.CREATE)))) // verify json root element message
			.andExpect(jsonPath("$.errors", hasSize(1))) // verify that json root element $ is size 1
			.andExpect(jsonPath("$.errors[0]", is(String.format(getMessage(ValidatorCodes.ERROR_CODE_REVIEW_EMPTY.getMessage()), ActionType.CREATE))))
			;
		
		// additional check
		// get all reviews
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS)
				//.param("validationRole", validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is now size 1
			.andDo(MockMvcResultHandlers.print())
			;
	}
	
	@Test
	void addReviewInvalidCourseRoleAdmin() throws Exception {

		addReviewInvalidCourse(ROLE_ADMIN);
	}
	
	@Test
	void addReviewInvalidCourseRoleInstructor() throws Exception {

		addReviewInvalidCourse(ROLE_INSTRUCTOR);
	}
	
	@Test
	void addReviewInvalidCourseRoleStudent() throws Exception {

		addReviewInvalidCourse(ROLE_STUDENT);
	}
	
	private void addReviewInvalidCourse(String validationRole) throws Exception {
		log.info("addReviewInvalidCourse {}", validationRole);
		
		// create invalid review 
		ReviewVO reviewVO = new ReviewVO("review");
		
		// invalid course id 2
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + REVIEWS + "/{courseId}", 2)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(reviewVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_course_id_not_found"), 2)))) // verify json element.andExpect(jsonPath("$.message", is(String.format(getMessage(VALIDATION_FAILED), ActionType.CREATE)))) // verify json root element message
			;
		
		// additional check
		// get all reviews
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS)
				.param("validationRole", validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is now size 1
			.andDo(MockMvcResultHandlers.print())
			;
	}
	
	@Test
	void addReviewRoleInvalidCourseNoRole() throws Exception {
		log.info("addReviewRoleInvalidCourseNoRole");
		
		//String validationRole = ROLE_ADMIN;
		
		// create invalid review 
		ReviewVO reviewVO = new ReviewVO("review");
		
		// invalid course id 2
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + REVIEWS + "/{courseId}", 2)
				//.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(reviewVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_course_id_not_found"), 2)))) // verify json element.andExpect(jsonPath("$.message", is(String.format(getMessage(VALIDATION_FAILED), ActionType.CREATE)))) // verify json root element message
			;
		
		// additional check
		// get all reviews
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS)
				//.param("validationRole", validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is now size 1
			.andDo(MockMvcResultHandlers.print())
			;
	}
	
	@Test
	void updateInvalidReviewRoleAdmin() throws Exception {
		
		updateInvalidReview(ROLE_ADMIN);
	}
	
	@Test
	void updateInvalidReviewRoleInstructor() throws Exception {
		
		updateInvalidReview(ROLE_INSTRUCTOR);
	}
	
	@Test
	void updateInvalidReviewRoleStudent() throws Exception {
		
		updateInvalidReview(ROLE_STUDENT);
	}
	
	private void updateInvalidReview(String validationRole) throws Exception {
		log.info("updateInvalidReview {}", validationRole);
		
		int id = 2;
		
		// check if review id 2 exists
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS + "/{id}", id)
				.param("validationRole", validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_review_id_not_found"), id)))) // verify json element
			;
		
		// create invalid review (set invalid id 2)
		ReviewVO reviewVO = new ReviewVO("review");
		reviewVO.setId(id);
		
		if (validationRole.equals(ROLE_STUDENT)) {
			mockMvc.perform(MockMvcRequestBuilders.put(ROOT + REVIEWS)
					.param("validationRole", validationRole)
					.contentType(APPLICATION_JSON_UTF8)
					.content(objectMApper.writeValueAsString(reviewVO)) // generate json from java object
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().is4xxClientError())
				.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
				.andExpect(jsonPath("$.message", is(String.format(getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage()), ActionType.UPDATE)))) // verify json root element message
				;
		} else {
			mockMvc.perform(MockMvcRequestBuilders.put(ROOT + REVIEWS)
					.param("validationRole", validationRole)
					.contentType(APPLICATION_JSON_UTF8)
					.content(objectMApper.writeValueAsString(reviewVO)) // generate json from java object
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_review_id_not_found"), id)))) // verify json element
				;
		}	
	}
	
	@Test
	void updateInvalidReviewNoRole() throws Exception {
		log.info("updateReviewNoRoleStudent");
		
		//String validationRole = ROLE_STUDENT;
		int id = 2;
		
		// check if review id 2 exists
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS + "/{id}", id)
			//	.param("validationRole", validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_review_id_not_found"), id)))) // verify json element
			;
		
		// create invalid review (set invalid id 2)
		ReviewVO reviewVO = new ReviewVO("review");
		reviewVO.setId(id);
		
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + REVIEWS)
				//.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(reviewVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.UPDATE)))) // verify json root element message
			;
	}
	
	@Test
	void deleteInvalidReviewRoleAdmin() throws Exception {
		
		deleteInvalidReview(ROLE_ADMIN);
	}
	
	@Test
	void deleteInvalidReviewRoleInstructor() throws Exception {
		
		deleteInvalidReview(ROLE_INSTRUCTOR);
	}
	
	@Test
	void deleteInvalidReviewRoleStudent() throws Exception {
		
		deleteInvalidReview(ROLE_STUDENT);
	}

	private void deleteInvalidReview(String validationRole) throws Exception {
		log.info("deleteInvalidReview {}", validationRole);
		
		int id = 2;
		
		// check if review id 2 exists
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS + "/{id}", id)
				.param("validationRole", validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_review_id_not_found"), id)))) // verify json element
			;
		
		// delete review
		if (validationRole.equals(ROLE_ADMIN)) {
			mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + REVIEWS + "/{id}", id)
					.param("validationRole", validationRole)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(content().contentType(APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_review_id_not_found"), id)))) //verify json element
				;
		} else {
			mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + REVIEWS + "/{id}", id)
					.param("validationRole", validationRole)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().is4xxClientError())
				.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
				.andExpect(content().contentType(APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.DELETE)))) // verify json root element message
				;
		}
	}
	
	@Test
	void deleteInvalidReviewNoRole() throws Exception {
		log.info("deleteInvalidReviewNoRole");
		
		int id = 2;
		
		// check if review id 2 exists
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS + "/{id}", id)
				//.param("validationRole", validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_review_id_not_found"), id)))) // verify json element
			;
				
		// delete review
		mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + REVIEWS + "/{id}", id)
			//	.param("validationRole", ROLE_STUDENT)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.DELETE)))) // verify json root element message
			;
	}
	
	private String getMessage(String str) {
		return messageSource.getMessage(str, new Object[] {}, LocaleContextHolder.getLocale());
	}
}
