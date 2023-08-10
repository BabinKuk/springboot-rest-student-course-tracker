package org.babinkuk.validator;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.service.CourseService;
import org.babinkuk.service.InstructorService;
import org.babinkuk.vo.InstructorVO;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.babinkuk.controller.Api.ROOT;
import static org.babinkuk.controller.Api.INSTRUCTORS;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
public class InstructorValidatorTest {
	
	public static final Logger log = LogManager.getLogger(InstructorValidatorTest.class);
	
	private static String ROLE_ADMIN = "ROLE_ADMIN";
	private static String ROLE_INSTRUCTOR = "ROLE_INSTRUCTOR";
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
	private InstructorService instructorService;
		
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
	void addEmptyInstructorRoleAdmin() throws Exception {

		addEmptyInstructor(ROLE_ADMIN);
	}
	
	@Test
	void addEmptyInstructorRoleInstructor() throws Exception {

		addEmptyInstructor(ROLE_INSTRUCTOR);
	}

	private void addEmptyInstructor(String validationRole) throws Exception {
		log.info("addEmptyInstructorRoleInstructor {}", validationRole);
		
		// create invalid instructor (empty fields)
		InstructorVO instructorVO = new InstructorVO();
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + INSTRUCTORS)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(instructorVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(getMessage(VALIDATION_FAILED), ActionType.CREATE)))) // verify json root element message
			.andExpect(jsonPath("$.errors", hasSize(3))) // verify that json root element $ is size 3
	        .andExpect(jsonPath("$.errors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_FIRST_NAME_EMPTY.getMessage()), ActionType.CREATE))))
	        .andExpect(jsonPath("$.errors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_LAST_NAME_EMPTY.getMessage()), ActionType.CREATE))))
	        .andExpect(jsonPath("$.errors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_EMAIL_EMPTY.getMessage()), ActionType.CREATE))))
			;
		
		// additional check
		// get all instructors
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS)
				.param("validationRole", validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is size 1
			.andDo(MockMvcResultHandlers.print())
			;
	}

	@Test
	void addInstructorInvalidEmailRoleAdmin() throws Exception {

		addInstructorInvalidEmail(ROLE_ADMIN);
	}
	
	@Test
	void addInstructorInvalidEmailRoleIstructor() throws Exception {

		addInstructorInvalidEmail(ROLE_INSTRUCTOR);
	}
	
	private void addInstructorInvalidEmail(String validationRole) throws Exception {
		log.info("addInstructorInvalidEmail {}", validationRole);
		
		// create invalid instructor (empty fields)
		InstructorVO instructorVO = new InstructorVO();
		String emailAddress = "this is invalid email";
		instructorVO.setEmailAddress(emailAddress);
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + INSTRUCTORS)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(instructorVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(getMessage(VALIDATION_FAILED), ActionType.CREATE)))) // verify json root element message
			.andExpect(jsonPath("$.errors", hasSize(3))) // verify that json root element $ is size 3
	        .andExpect(jsonPath("$.errors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_FIRST_NAME_EMPTY.getMessage()), ActionType.CREATE))))
	        .andExpect(jsonPath("$.errors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_LAST_NAME_EMPTY.getMessage()), ActionType.CREATE))))
	        .andExpect(jsonPath("$.errors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_EMAIL_INVALID.getMessage()), ActionType.CREATE))))
			;
		
		// additional check
		// get all instructors
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS)
				.param("validationRole", validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is size 1
			.andDo(MockMvcResultHandlers.print())
			;
	}
	
	@Test
	void addInstructorEmailNotUniqueRoleAdmin() throws Exception {

		addInstructorEmailNotUnique(ROLE_ADMIN);
	}
	
	@Test
	void addInstructorEmailNotUniqueRoleInstructor() throws Exception {

		addInstructorEmailNotUnique(ROLE_INSTRUCTOR);
	}
	
	private void addInstructorEmailNotUnique(String validationRole) throws Exception {
		log.info("addInstructorEmailNotUnique {}", validationRole);
		
		// create invalid instructor (empty fields)
		InstructorVO instructorVO = new InstructorVO();
		// this email already exists in db
		String emailAddress = "firstNameInstr@babinuk.com";
		instructorVO.setEmailAddress(emailAddress);
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + INSTRUCTORS)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(instructorVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(getMessage(VALIDATION_FAILED), ActionType.CREATE)))) // verify json root element message
			.andExpect(jsonPath("$.errors", hasSize(3))) // verify that json root element $ is size 3
	        .andExpect(jsonPath("$.errors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_FIRST_NAME_EMPTY.getMessage()), ActionType.CREATE))))
	        .andExpect(jsonPath("$.errors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_LAST_NAME_EMPTY.getMessage()), ActionType.CREATE))))
	        .andExpect(jsonPath("$.errors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_EMAIL_ALREADY_EXIST.getMessage()), ActionType.CREATE))))
			;
		
		// additional check
		// get all instructors
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS)
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
	void updateInstructorInvalidIdRoleAdmin() throws Exception {

		updateInstructorInvalidId(ROLE_ADMIN);
	}
	
	@Test
	void updateInstructorInvalidIdRoleInstructor() throws Exception {

		updateInstructorInvalidId(ROLE_INSTRUCTOR);
	}
	
	private void updateInstructorInvalidId(String validationRole) throws Exception {
		log.info("updateInstructorInvalidId {}", validationRole);
		
		int id = 2;
		
		// check if instructor id 2 exists
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/{id}", id)
				.param("validationRole", validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_instructor_id_not_found"), id)))) // verify json element
			;
		
		// create invalid instructor 
		InstructorVO instructorVO = new InstructorVO("firstName", "lastName", "emailAddress@email.hr", "youtubeChannel", "hobby");
		instructorVO.setId(id);
		
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + INSTRUCTORS)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(instructorVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_instructor_id_not_found"), id)))) // verify json element
			;
	}
	
	@Test
	void updateEmptyInstructorRoleAdmin() throws Exception {

		updateEmptyInstructor(ROLE_ADMIN);
	}
	
	@Test
	void updateEmptyInstructorRoleInstructor() throws Exception {

		updateEmptyInstructor(ROLE_INSTRUCTOR);
	}
	
	private void updateEmptyInstructor(String validationRole) throws Exception {
		log.info("updateEmptyInstructor {}", validationRole);
		
		int id = 1;
		
		// check if instructor id 1 exists
		InstructorVO instructorVO = instructorService.findById(id);
		log.info(instructorVO.toString());
		
		assertNotNull(instructorVO,"instructorVO null");
		assertEquals(1, instructorVO.getId());
		assertNotNull(instructorVO.getFirstName(),"instructorVO.getFirstName() null");
		assertEquals("firstNameInstr", instructorVO.getFirstName(),"assertEquals instructorVO.getFirstName() failure");
		
		// update instructor
		instructorVO.setFirstName("");
		instructorVO.setLastName("");
		instructorVO.setEmailAddress("");
		instructorVO.setYoutubeChannel("");
		instructorVO.setHobby("");
		
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + INSTRUCTORS)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(instructorVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(getMessage(VALIDATION_FAILED), ActionType.UPDATE)))) // verify json root element message
			.andExpect(jsonPath("$.errors", hasSize(3))) // verify that json root element $ is size 3
	        .andExpect(jsonPath("$.errors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_FIRST_NAME_EMPTY.getMessage()), ActionType.CREATE))))
	        .andExpect(jsonPath("$.errors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_LAST_NAME_EMPTY.getMessage()), ActionType.CREATE))))
	        .andExpect(jsonPath("$.errors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_EMAIL_EMPTY.getMessage()), ActionType.CREATE))))
			;
	}
	
	@Test
	void updateInstructorInvalidEmailRoleAdmin() throws Exception {

		updateInstructorInvalidEmail(ROLE_ADMIN);
	}
	
	@Test
	void updateInstructorInvalidEmailRoleInstructor() throws Exception {

		updateInstructorInvalidEmail(ROLE_INSTRUCTOR);
	}
	
	private void updateInstructorInvalidEmail(String validationRole) throws Exception {
		log.info("updateInstructorInvalidEmail {}", validationRole);
		
		int id = 1;
		
		// check if instructor id 1 exists
		InstructorVO instructorVO = instructorService.findById(id);
		log.info(instructorVO.toString());
		
		assertNotNull(instructorVO,"instructorVO null");
		assertEquals(1, instructorVO.getId());
		assertNotNull(instructorVO.getFirstName(),"instructorVO.getFirstName() null");
		assertEquals("firstNameInstr", instructorVO.getFirstName(),"assertEquals instructorVO.getFirstName() failure");
		
		// update instructor email
		instructorVO.setEmailAddress("this is invalid email");
		
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + INSTRUCTORS)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(instructorVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(getMessage(VALIDATION_FAILED), ActionType.UPDATE)))) // verify json root element message
			.andExpect(jsonPath("$.errors", hasSize(1))) // verify that json root element $ is size 1
//        .andExpect(jsonPath("$.errors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_FIRST_NAME_EMPTY.getMessage()), ActionType.CREATE))))
//        .andExpect(jsonPath("$.errors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_LAST_NAME_EMPTY.getMessage()), ActionType.CREATE))))
	        .andExpect(jsonPath("$.errors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_EMAIL_INVALID.getMessage()), ActionType.CREATE))))
			;
	}
	
	@Test
	void updateInstructorEmailNotUniqueRoleAdmin() throws Exception {
		
		updateInstructorEmailNotUnique(ROLE_ADMIN);
	}
	
	@Test
	void updateInstructorEmailNotUniqueRoleInstructor() throws Exception {
		
		updateInstructorEmailNotUnique(ROLE_INSTRUCTOR);
	}
	
	private void updateInstructorEmailNotUnique(String validationRole) throws Exception {
		log.info("updateInstructorEmailNotUnique {}", validationRole);
		
		int id = 1;
		
		// check if instructor id 1 exists
		InstructorVO instructorVO = instructorService.findById(id);
		
		assertNotNull(instructorVO,"instructorVO null");
		assertEquals(1, instructorVO.getId());
		assertNotNull(instructorVO.getFirstName(),"instructorVO.getFirstName() null");
		assertEquals("firstNameInstr", instructorVO.getFirstName(),"assertEquals instructorVO.getFirstName() failure");
		
		// create new instructor
		InstructorVO newInstructorVO = new InstructorVO("firstName", "lastName", "email@email.com", "youtubeChannel", "hobby");
		newInstructorVO.setId(0);
		
		// save new instructor
		instructorService.saveInstructor(newInstructorVO);
		
		// check if new instructor exists
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS)
				.param("validationRole", validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is size 2
			;
		
		InstructorVO dbNewInstructorVO = instructorService.findByEmail(newInstructorVO.getEmailAddress());
		
		assertNotNull(dbNewInstructorVO,"dbNewInstructorVO null");
		assertNotNull(dbNewInstructorVO.getFirstName(),"dbNewInstructorVO.getFirstName() null");
		assertEquals(newInstructorVO.getFirstName(), dbNewInstructorVO.getFirstName(),"assertEquals dbNewInstructorVO.getFirstName() failure");
		assertEquals(newInstructorVO.getEmailAddress(), dbNewInstructorVO.getEmailAddress(),"assertEquals dbNewInstructorVO.getEmailAddress() failure");
		
		// update instructor email (value belong to other instructor id 1)
		dbNewInstructorVO.setEmailAddress(instructorVO.getEmailAddress());
				
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + INSTRUCTORS)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(dbNewInstructorVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(getMessage(VALIDATION_FAILED), ActionType.UPDATE)))) // verify json root element message
			.andExpect(jsonPath("$.errors", hasSize(1))) // verify that json root element $ size 1
//        .andExpect(jsonPath("$.errors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_FIRST_NAME_EMPTY.getMessage()), ActionType.CREATE))))
//        .andExpect(jsonPath("$.errors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_LAST_NAME_EMPTY.getMessage()), ActionType.CREATE))))
	        .andExpect(jsonPath("$.errors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_EMAIL_ALREADY_EXIST.getMessage()), ActionType.CREATE))))
			;
	}
	
	@Test
	void updateInstructorNotExistRoleAdmin() throws Exception {
		
		updateInstructorNotExist(ROLE_ADMIN);
	}
	
	@Test
	void updateInstructorNotExistRoleInstructor() throws Exception {
		
		updateInstructorNotExist(ROLE_INSTRUCTOR);
	}
	
	private void updateInstructorNotExist(String validationRole) throws Exception {
		log.info("updateInstructorNotExistRoleAdmin {}", validationRole);
		
		int id = 2;
		
		// get instructor with id=2 (non existing)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/{id}", id)
				.param("validationRole", validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_instructor_id_not_found"), 2)))) // verify json element
			;
		
		// create new instructor
		InstructorVO instructorVO = new InstructorVO("firstName", "lastName", "email@email.com", "youtubeChannel", "hobby");
		instructorVO.setId(id);
		
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + INSTRUCTORS)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(instructorVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_instructor_id_not_found"), id)))) // verify json element
			;
	}
	
	private String getMessage(String str) {
		return messageSource.getMessage(str, new Object[] {}, LocaleContextHolder.getLocale());
	}
}
