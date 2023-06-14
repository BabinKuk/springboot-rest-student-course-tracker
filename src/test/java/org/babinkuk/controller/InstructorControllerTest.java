package org.babinkuk.controller;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.service.InstructorService;
import org.babinkuk.service.InstructorServiceImpl;
import org.babinkuk.validator.ActionType;
import org.babinkuk.validator.ValidatorCodes;
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
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.babinkuk.controller.Api.ROOT;
import static org.babinkuk.controller.Api.INSTRUCTORS;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
public class InstructorControllerTest {
	
	public static final Logger log = LogManager.getLogger(InstructorControllerTest.class);
	
	private static String ROLE_ADMIN = "ROLE_ADMIN";
	private static String ROLE_INSTRUCTOR = "ROLE_INSTRUCTOR";
	private static String ROLE_STUDENT = "ROLE_STUDENT";
	private static String ROLE_NOT_EXIST = "ROLE_NOT_EXIST";
	
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
	
//	@Autowired
//	private CourseService courseService;
	
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
	void getAllInstructors() throws Exception {
		log.info("getAllInstructors");
		
		// get all instructors
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/get")
				.param("validationRole", ROLE_ADMIN)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is size 1
			;

		// add another instructor
		// set id 0: this is to force a save of new item ... instead of update
		InstructorVO instructorVO = new InstructorVO("firstName", "lastName", "emailAddress", "youtubeChannel", "hobby");
		instructorVO.setId(0);
		
		instructorService.saveInstructor(instructorVO);
				
		// get all instructors (different validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/get")
				.param("validationRole", ROLE_INSTRUCTOR)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
		
		// get all instructors (different validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/get")
				.param("validationRole", ROLE_STUDENT)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
		
		// get all instructors (without validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/get")
			//	.param("validationRole", "ROLE_STUDENT")
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
		
		// get all instructors (not existing validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/get")
				.param("validationRole", ROLE_NOT_EXIST)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
	}
	
	@Test
	void getInstructor() throws Exception {
		log.info("getInstructor");
		
		// get instructor with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/get/{id}", 1)
				.param("validationRole", ROLE_ADMIN)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.firstName", is("firstNameInstr"))) // verify json element
			;

		// get instructor with id=2 (non existing)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/get/{id}", 2)
				.param("validationRole", ROLE_ADMIN)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_instructor_id_not_found"), 2)))) // verify json element
			;
		
		// get instructor with id=1 (validationRole ROLE_INSTRUCTOR)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/get/{id}", 1)
				.param("validationRole", ROLE_INSTRUCTOR)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.lastName", is("lastNameInstr"))) // verify json element
			;

		// get instructor with id=2 (non existing) (validationRole ROLE_INSTRUCTOR)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/get/{id}", 2)
				.param("validationRole", ROLE_INSTRUCTOR)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_instructor_id_not_found"), 2)))) // verify json element
			;
		
		// get instructor with id=1 (validationRole ROLE_STUDENT)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/get/{id}", 1)
				.param("validationRole", ROLE_STUDENT)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.emailAddress", is("firstNameInstr@babinuk.com"))) // verify json element
			;

		// get instructor with id=2 (non existing) (validationRole ROLE_STUDENT)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/get/{id}", 2)
				.param("validationRole", ROLE_STUDENT)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_instructor_id_not_found"), 2)))) // verify json element
			;
		
		// get instructor with id=1 (without validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/get/{id}", 1)
				//.param("validationRole", "ROLE_STUDENT")
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.youtubeChannel", is("ytb test"))) // verify json element
			;
		
		// get instructor with id=2 (non existing) (without validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/get/{id}", 2)
				//.param("validationRole", ROLE_STUDENT)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_instructor_id_not_found"), 2)))) // verify json element
			;
		
		// get instructor with id=1 (non existing) (without validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/get/{id}", 1)
				.param("validationRole", ROLE_NOT_EXIST)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.hobby", is("test hobby"))) // verify json element
			;
	
		// get instructor with id=2 (non existing) (not existing validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/get/{id}", 2)
				.param("validationRole", ROLE_NOT_EXIST)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_instructor_id_not_found"), 2)))) // verify json element
			;
	}
	
	@Test
	void addInstructorRoleAdmin() throws Exception {
		log.info("addInstructorRoleAdmin");
		
		// create instructor
		InstructorVO instructorVO = new InstructorVO("firstName", "lastName", "emailAddress@email.hr", "youtubeChannel", "hobby");
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + INSTRUCTORS)
				.param("validationRole", ROLE_ADMIN)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(instructorVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(getMessage(InstructorServiceImpl.INSTRUCTOR_SAVE_SUCCESS)))) // verify json element
			;
		
		// additional check
		// get all instructors
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/get")
				.param("validationRole", ROLE_ADMIN)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
		
		// additional check
		instructorVO = instructorService.findByEmail("emailAddress@email.hr");
		
		log.info(instructorVO.toString());
		
		assertNotNull(instructorVO,"instructorVO null");
		assertNotNull(instructorVO.getFirstName(),"instructorVO.getFirstName() null");
		assertNotNull(instructorVO.getLastName(),"instructorVO.getLastName() null");
		assertNotNull(instructorVO.getEmailAddress(),"instructorVO.getEmailAddress() null");
		assertEquals("firstName", instructorVO.getFirstName(),"instructorVO.getFirstName() NOK");
		assertEquals("lastName", instructorVO.getLastName(),"instructorVO.getLastName() NOK");
	}

	@Test
	void addInstructorRoleInstructor() throws Exception {
		log.info("addInstructorRoleInstructor");
		
		// create instructor
		InstructorVO instructorVO = new InstructorVO("firstName", "lastName", "emailAddress@email.hr", "youtubeChannel", "hobby");
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + INSTRUCTORS)
				.param("validationRole", ROLE_INSTRUCTOR)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(instructorVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(getMessage(InstructorServiceImpl.INSTRUCTOR_SAVE_SUCCESS)))) // verify json element
			;
		
		// additional check
		// get all instructors
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/get")
				.param("validationRole", ROLE_INSTRUCTOR)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
		
		// additional check
		instructorVO = instructorService.findByEmail("emailAddress@email.hr");
		
		log.info(instructorVO.toString());
		
		assertNotNull(instructorVO,"instructorVO null");
		assertNotNull(instructorVO.getFirstName(),"instructorVO.getFirstName() null");
		assertNotNull(instructorVO.getLastName(),"instructorVO.getLastName() null");
		assertNotNull(instructorVO.getEmailAddress(),"instructorVO.getEmailAddress() null");
		assertEquals("firstName", instructorVO.getFirstName(),"instructorVO.getFirstName() NOK");
		assertEquals("lastName", instructorVO.getLastName(),"instructorVO.getLastName() NOK");
	}
	
	@Test
	void addInstructorRoleStudent() throws Exception {
		log.info("addInstructorRoleStudent");
		
		// create instructor
		InstructorVO instructorVO = new InstructorVO("firstName", "lastName", "emailAddress@email.hr", "youtubeChannel", "hobby");
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + INSTRUCTORS)
				.param("validationRole", ROLE_STUDENT)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(instructorVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.CREATE)))) // verify json root element message
			;
		
		// additional check
		// get all instructors
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/get")
				.param("validationRole", ROLE_STUDENT)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is still size 1
			;
	}
	
	@Test
	void addInstructorNoRole() throws Exception {
		log.info("addInstructorNoRole");
		
		// create instructor
		InstructorVO instructorVO = new InstructorVO("firstName", "lastName", "emailAddress@email.hr", "youtubeChannel", "hobby");
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + INSTRUCTORS)
				//.param("validationRole", ROLE_STUDENT)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(instructorVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.CREATE)))) // verify json root element message
			;
		
		// additional check
		// get all instructors
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/get")
				//.param("validationRole", ROLE_STUDENT)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is still size 1
			;
	}
	
	@Test
	void addInstructorRoleNotExist() throws Exception {
		log.info("addInstructorRoleNotExist");
		
		// create instructor
		InstructorVO instructorVO = new InstructorVO("firstName", "lastName", "emailAddress@email.hr", "youtubeChannel", "hobby");
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + INSTRUCTORS)
				.param("validationRole", ROLE_NOT_EXIST)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(instructorVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			;
		
		// additional check
		// get all instructors
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/get")
				.param("validationRole", ROLE_NOT_EXIST)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is stil size 1
			;
	}
	
	@Test
	void updateInstructorRoleAdmin() throws Exception {
		log.info("updateInstructorRoleAdmin");
		
		// check if instructor id 1 exists
		InstructorVO instructorVO = instructorService.findById(1);
		log.info(instructorVO.toString());
		
		assertNotNull(instructorVO,"instructorVO null");
		assertEquals(1, instructorVO.getId());
		assertNotNull(instructorVO.getFirstName(),"instructorVO.getFirstName() null");
		assertEquals("firstNameInstr", instructorVO.getFirstName(),"assertEquals instructorVO.getFirstName() failure");
		
		// update instructor
		instructorVO.setFirstName("firstName");
		instructorVO.setLastName("lastName");
		instructorVO.setEmailAddress("email@email.com");
		instructorVO.setYoutubeChannel("youtubeChannel");
		instructorVO.setHobby("hobi");
				
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + INSTRUCTORS)
				.param("validationRole", ROLE_ADMIN)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(instructorVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(getMessage(InstructorServiceImpl.INSTRUCTOR_SAVE_SUCCESS)))) // verify json element
			;
		
		// additional check
		// get instructor with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/get/{id}", 1)
				.param("validationRole", ROLE_ADMIN)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.firstName", is("firstName"))) // verify json element
			.andExpect(jsonPath("$.lastName", is("lastName"))) // verify json element
			.andExpect(jsonPath("$.emailAddress", is("email@email.com"))) // verify json element
			.andExpect(jsonPath("$.youtubeChannel", is("youtubeChannel"))) // verify json element
			.andExpect(jsonPath("$.hobby", is("hobi"))) // verify json element
			;
	}
	
	@Test
	void updateInstructorRoleInstructor() throws Exception {
		log.info("updateInstructorRoleInstructor");
		
		// check if instructor id 1 exists
		InstructorVO instructorVO = instructorService.findById(1);
		log.info(instructorVO.toString());
		
		assertNotNull(instructorVO,"instructorVO null");
		assertEquals(1, instructorVO.getId());
		assertNotNull(instructorVO.getFirstName(),"instructorVO.getFirstName() null");
		assertEquals("firstNameInstr", instructorVO.getFirstName(),"assertEquals instructorVO.getFirstName() failure");
		
		// update instructor
		instructorVO.setFirstName("firstName");
		instructorVO.setLastName("lastName");
		instructorVO.setEmailAddress("email@email.com");
		instructorVO.setYoutubeChannel("youtubeChannel");
		instructorVO.setHobby("hobi");
				
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + INSTRUCTORS)
				.param("validationRole", ROLE_INSTRUCTOR)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(instructorVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(getMessage(InstructorServiceImpl.INSTRUCTOR_SAVE_SUCCESS)))) // verify json element
			;
		
		// additional check
		// get instructor with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/get/{id}", 1)
				.param("validationRole", ROLE_INSTRUCTOR)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.firstName", is("firstName"))) // verify json element
			.andExpect(jsonPath("$.lastName", is("lastName"))) // verify json element
			.andExpect(jsonPath("$.emailAddress", is("email@email.com"))) // verify json element
			.andExpect(jsonPath("$.youtubeChannel", is("youtubeChannel"))) // verify json element
			.andExpect(jsonPath("$.hobby", is("hobi"))) // verify json element
			;
	}
	
	@Test
	void updateInstructorRoleStudent() throws Exception {
		log.info("updateInstructorRoleStudent");
		
		// check if instructor id 1 exists
		InstructorVO instructorVO = instructorService.findById(1);
		log.info(instructorVO.toString());
		
		assertNotNull(instructorVO,"instructorVO null");
		assertEquals(1, instructorVO.getId());
		assertNotNull(instructorVO.getFirstName(),"instructorVO.getFirstName() null");
		assertEquals("firstNameInstr", instructorVO.getFirstName(),"assertEquals instructorVO.getFirstName() failure");
		
		// update instructor
		instructorVO.setFirstName("firstName");
		instructorVO.setLastName("lastName");
		instructorVO.setEmailAddress("email@email.com");
		instructorVO.setYoutubeChannel("youtubeChannel");
		instructorVO.setHobby("hobi");
				
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + INSTRUCTORS)
				.param("validationRole", ROLE_STUDENT)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(instructorVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.UPDATE)))) // verify json root element message
			;
		
		// additional check
		// get instructor with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/get/{id}", 1)
				.param("validationRole", ROLE_STUDENT)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.firstName", is("firstNameInstr"))) // verify json element
			.andExpect(jsonPath("$.lastName", is("lastNameInstr"))) // verify json element
			.andExpect(jsonPath("$.emailAddress", is("firstNameInstr@babinuk.com"))) // verify json element
			.andExpect(jsonPath("$.youtubeChannel", is("ytb test"))) // verify json element
			.andExpect(jsonPath("$.hobby", is("test hobby"))) // verify json element
			;
	}
	
	@Test
	void updateInstructorNoRole() throws Exception {
		log.info("updateInstructorNoRole");
		
		// check if instructor id 1 exists
		InstructorVO instructorVO = instructorService.findById(1);
		log.info(instructorVO.toString());
		
		assertNotNull(instructorVO,"instructorVO null");
		assertEquals(1, instructorVO.getId());
		assertNotNull(instructorVO.getFirstName(),"instructorVO.getFirstName() null");
		assertEquals("firstNameInstr", instructorVO.getFirstName(),"assertEquals instructorVO.getFirstName() failure");
		
		// update instructor
		instructorVO.setFirstName("firstName");
		instructorVO.setLastName("lastName");
		instructorVO.setEmailAddress("email@email.com");
		instructorVO.setYoutubeChannel("youtubeChannel");
		instructorVO.setHobby("hobi");
				
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + INSTRUCTORS)
				//.param("validationRole", ROLE_STUDENT)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(instructorVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.UPDATE)))) // verify json root element message
			;
		
		// additional check
		// get instructor with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/get/{id}", 1)
				//.param("validationRole", ROLE_STUDENT)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.firstName", is("firstNameInstr"))) // verify json element
			.andExpect(jsonPath("$.lastName", is("lastNameInstr"))) // verify json element
			.andExpect(jsonPath("$.emailAddress", is("firstNameInstr@babinuk.com"))) // verify json element
			.andExpect(jsonPath("$.youtubeChannel", is("ytb test"))) // verify json element
			.andExpect(jsonPath("$.hobby", is("test hobby"))) // verify json element
			;
	}
	
	@Test
	void updateInstructorRoleNotExist() throws Exception {
		log.info("updateInstructorRoleNotExist");
		
		// check if instructor id 1 exists
		InstructorVO instructorVO = instructorService.findById(1);
		log.info(instructorVO.toString());
		
		assertNotNull(instructorVO,"instructorVO null");
		assertEquals(1, instructorVO.getId());
		assertNotNull(instructorVO.getFirstName(),"instructorVO.getFirstName() null");
		assertEquals("firstNameInstr", instructorVO.getFirstName(),"assertEquals instructorVO.getFirstName() failure");
		
		// update instructor
		instructorVO.setFirstName("firstName");
		instructorVO.setLastName("lastName");
		instructorVO.setEmailAddress("email@email.com");
		instructorVO.setYoutubeChannel("youtubeChannel");
		instructorVO.setHobby("hobi");
				
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + INSTRUCTORS)
				.param("validationRole", ROLE_NOT_EXIST)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(instructorVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			;
		
		// additional check
		// get instructor with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/get/{id}", 1)
				.param("validationRole", ROLE_NOT_EXIST)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.firstName", is("firstNameInstr"))) // verify json element
			.andExpect(jsonPath("$.lastName", is("lastNameInstr"))) // verify json element
			.andExpect(jsonPath("$.emailAddress", is("firstNameInstr@babinuk.com"))) // verify json element
			.andExpect(jsonPath("$.youtubeChannel", is("ytb test"))) // verify json element
			.andExpect(jsonPath("$.hobby", is("test hobby"))) // verify json element
			;
	}
	
	@Test
	void deleteInstructorRoleAdmin() throws Exception {
		log.info("deleteInstructorRoleAdmin");
		
		// check if instructor id 1 exists
		int id = 1;
		InstructorVO instructorVO = instructorService.findById(id);
		log.info(instructorVO.toString());
		
		assertNotNull(instructorVO,"instructorVO null");
		assertEquals(1, instructorVO.getId());
		assertNotNull(instructorVO.getFirstName(),"instructorVO.getFirstName() null");
		assertEquals("firstNameInstr", instructorVO.getFirstName(),"assertEquals instructorVO.getFirstName() failure");
				
		// delete instructor
		mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + INSTRUCTORS + "/{id}", id)
				.param("validationRole", ROLE_ADMIN)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(getMessage(InstructorServiceImpl.INSTRUCTOR_DELETE_SUCCESS)))) // verify json element
			;
		
		// get instructor with id=1 (non existing)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/get/{id}", id)
				.param("validationRole", ROLE_ADMIN)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_instructor_id_not_found"), id)))) //verify json element
			;
	}
	
	@Test
	void deleteInstructorRoleInstructor() throws Exception {
		log.info("deleteInstructorRoleInstructor");
		
		// check if instructor id 1 exists
		int id = 1;
		InstructorVO instructorVO = instructorService.findById(id);
		log.info(instructorVO.toString());
		
		assertNotNull(instructorVO,"instructorVO null");
		assertEquals(1, instructorVO.getId());
		assertNotNull(instructorVO.getFirstName(),"instructorVO.getFirstName() null");
		assertEquals("firstNameInstr", instructorVO.getFirstName(),"assertEquals instructorVO.getFirstName() failure");
				
		// delete instructor
		mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + INSTRUCTORS + "/{id}", id)
				.param("validationRole", ROLE_INSTRUCTOR)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.DELETE)))) // verify json root element message
			;
	}
	
	@Test
	void deleteInstructorRoleStudent() throws Exception {
		log.info("deleteInstructorRoleStudent");
		
		// check if instructor id 1 exists
		int id = 1;
		InstructorVO instructorVO = instructorService.findById(id);
		log.info(instructorVO.toString());
		
		assertNotNull(instructorVO,"instructorVO null");
		assertEquals(1, instructorVO.getId());
		assertNotNull(instructorVO.getFirstName(),"instructorVO.getFirstName() null");
		assertEquals("firstNameInstr", instructorVO.getFirstName(),"assertEquals instructorVO.getFirstName() failure");
				
		// delete instructor
		mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + INSTRUCTORS + "/{id}", id)
				.param("validationRole", ROLE_STUDENT)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.DELETE)))) // verify json root element message
			;
	}

	@Test
	void deleteInstructorNoRole() throws Exception {
		log.info("deleteInstructorNoRole");
		
		// check if instructor id 1 exists
		int id = 1;
		InstructorVO instructorVO = instructorService.findById(id);
		log.info(instructorVO.toString());
		
		assertNotNull(instructorVO,"instructorVO null");
		assertEquals(1, instructorVO.getId());
		assertNotNull(instructorVO.getFirstName(),"instructorVO.getFirstName() null");
		assertEquals("firstNameInstr", instructorVO.getFirstName(),"assertEquals instructorVO.getFirstName() failure");
				
		// delete instructor
		mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + INSTRUCTORS + "/{id}", id)
				//.param("validationRole", ROLE_STUDENT)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.DELETE)))) // verify json root element message
			;
	}
	
	@Test
	void deleteInstructorRoleNotExist() throws Exception {
		log.info("deleteInstructorRoleNotExist");
		
		// check if instructor id 1 exists
		int id = 1;
		InstructorVO instructorVO = instructorService.findById(id);
		log.info(instructorVO.toString());
		
		assertNotNull(instructorVO,"instructorVO null");
		assertEquals(1, instructorVO.getId());
		assertNotNull(instructorVO.getFirstName(),"instructorVO.getFirstName() null");
		assertEquals("firstNameInstr", instructorVO.getFirstName(),"assertEquals instructorVO.getFirstName() failure");
				
		// delete instructor
		mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + INSTRUCTORS + "/{id}", id)
				.param("validationRole", ROLE_NOT_EXIST)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			;
	}
	
	private String getMessage(String str) {
		return messageSource.getMessage(str, new Object[] {}, LocaleContextHolder.getLocale());
	}
}
