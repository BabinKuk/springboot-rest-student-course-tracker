package org.babinkuk.validator;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.service.CourseService;
import org.babinkuk.service.StudentService;
import org.babinkuk.vo.StudentVO;
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
import static org.babinkuk.controller.Api.STUDENTS;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
public class StudentValidatorTest {
	
	public static final Logger log = LogManager.getLogger(StudentValidatorTest.class);
	
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
	private StudentService studentService;
		
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
	private String sqladdStudent;
	
	@Value("${sql.script.instructor.delete}")
	private String sqlDeleteInstructor;
	
	@Value("${sql.script.instructor-detail.insert}")
	private String sqladdStudentDetail;
	
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

		jdbc.execute(sqladdStudentDetail);
		jdbc.execute(sqladdStudent);
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
	void addEmptyStudentRoleAdmin() throws Exception {

		addEmptyStudent(ROLE_ADMIN);
	}
	
	@Test
	void addEmptyStudentRoleInstructor() throws Exception {

		addEmptyStudent(ROLE_INSTRUCTOR);
	}

	private void addEmptyStudent(String validationRole) throws Exception {
		log.info("addEmptyStudentRoleInstructor {}", validationRole);
		
		// create invalid student (empty fields)
		StudentVO studentVO = new StudentVO();
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + STUDENTS)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(studentVO)) // generate json from java object
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
		// get all students
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS)
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
	void addStudentInvalidEmailRoleAdmin() throws Exception {

		addStudentInvalidEmail(ROLE_ADMIN);
	}
	
	@Test
	void addStudentInvalidEmailRoleIstructor() throws Exception {

		addStudentInvalidEmail(ROLE_INSTRUCTOR);
	}
	
	private void addStudentInvalidEmail(String validationRole) throws Exception {
		log.info("addStudentInvalidEmail {}", validationRole);
		
		// create invalid student (empty fields)
		StudentVO studentVO = new StudentVO();
		String emailAddress = "this is invalid email";
		studentVO.setEmailAddress(emailAddress);
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + STUDENTS)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(studentVO)) // generate json from java object
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
		// get all students
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS)
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
	void addStudentEmailNotUniqueRoleAdmin() throws Exception {

		addStudentEmailNotUnique(ROLE_ADMIN);
	}
	
	@Test
	void addStudentEmailNotUniqueRoleInstructor() throws Exception {

		addStudentEmailNotUnique(ROLE_INSTRUCTOR);
	}
	
	private void addStudentEmailNotUnique(String validationRole) throws Exception {
		log.info("addStudentEmailNotUnique {}", validationRole);
		
		// create invalid student (empty fields)
		StudentVO studentVO = new StudentVO();
		// this email already exists in db
		String emailAddress = "firstNameStudent@babinuk.com";
		studentVO.setEmailAddress(emailAddress);
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + STUDENTS)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(studentVO)) // generate json from java object
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
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS)
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
	void updateStudentInvalidIdRoleAdmin() throws Exception {

		updateStudentInvalidId(ROLE_ADMIN);
	}
	
	@Test
	void updateStudentInvalidIdRoleInstructor() throws Exception {

		updateStudentInvalidId(ROLE_INSTRUCTOR);
	}
	
	private void updateStudentInvalidId(String validationRole) throws Exception {
		log.info("updateStudentInvalidId {}", validationRole);
		
		int id = 2;
		
		// check if student id 2 exists
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", id)
				.param("validationRole", validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_student_id_not_found"), id)))) // verify json element
			;
		
		// create invalid student 
		StudentVO studentVO = new StudentVO("firstName", "lastName", "emailAddress@email.hr");
		studentVO.setId(id);
		
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(studentVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_student_id_not_found"), id)))) // verify json element
			;
	}
	
	@Test
	void updateEmptyStudentRoleAdmin() throws Exception {

		updateEmptyStudent(ROLE_ADMIN);
	}
	
	@Test
	void updateEmptyStudentRoleInstructor() throws Exception {

		updateEmptyStudent(ROLE_INSTRUCTOR);
	}
	
	private void updateEmptyStudent(String validationRole) throws Exception {
		log.info("updateEmptyStudent {}", validationRole);
		
		int id = 1;
		
		// check if student id 1 exists
		StudentVO studentVO = studentService.findById(id);
		
		assertNotNull(studentVO,"studentVO null");
		assertEquals(1, studentVO.getId());
		assertNotNull(studentVO.getFirstName(),"studentVO.getFirstName() null");
		assertEquals("firstNameStudent", studentVO.getFirstName(),"assertEquals studentVO.getFirstName() failure");
		
		// update instructor
		studentVO.setFirstName("");
		studentVO.setLastName("");
		studentVO.setEmailAddress("");
		
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(studentVO)) // generate json from java object
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
	void updateStudentInvalidEmailRoleAdmin() throws Exception {

		updateStudentInvalidEmail(ROLE_ADMIN);
	}
	
	@Test
	void updateStudentInvalidEmailRoleInstructor() throws Exception {

		updateStudentInvalidEmail(ROLE_INSTRUCTOR);
	}
	
	private void updateStudentInvalidEmail(String validationRole) throws Exception {
		log.info("updateStudentInvalidEmail {}", validationRole);
		
		int id = 1;
		
		// check if student id 1 exists
		StudentVO studentVO = studentService.findById(id);
		
		assertNotNull(studentVO,"studentVO null");
		assertEquals(1, studentVO.getId());
		assertNotNull(studentVO.getFirstName(),"studentVO.getFirstName() null");
		assertEquals("firstNameStudent", studentVO.getFirstName(),"assertEquals studentVO.getFirstName() failure");
		
		// update student
		studentVO.setEmailAddress("this is invalid email");
		
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(studentVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
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
	void updateStudentEmailNotUniqueRoleAdmin() throws Exception {
		
		updateStudentEmailNotUnique(ROLE_ADMIN);
	}
	
	@Test
	void updateStudentEmailNotUniqueRoleInstructor() throws Exception {
		
		updateStudentEmailNotUnique(ROLE_INSTRUCTOR);
	}
	
	private void updateStudentEmailNotUnique(String validationRole) throws Exception {
		log.info("updateStudentEmailNotUnique {}", validationRole);
		
		int id = 1;
		
		// check if student id 1 exists
		StudentVO studentVO = studentService.findById(id);
		
		assertNotNull(studentVO,"studentVO null");
		assertEquals(1, studentVO.getId());
		assertNotNull(studentVO.getFirstName(),"studentVO.getFirstName() null");
		assertEquals("firstNameStudent", studentVO.getFirstName(),"assertEquals studentVO.getFirstName() failure");
		
		// create new student
		StudentVO newStudentVO = new StudentVO("firstName", "lastName", "email@email.com");
		newStudentVO.setId(0);
		
		// save new student
		studentService.saveStudent(newStudentVO);
		
		// check if new student exists
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS)
				.param("validationRole", validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is size 2
			;
		
		StudentVO dbNewStudentVO = studentService.findByEmail(newStudentVO.getEmailAddress());
		
		assertNotNull(dbNewStudentVO,"dbNewStudentVO null");
		//assertEquals(1, dbNewStudentVO.getId());
		assertNotNull(dbNewStudentVO.getFirstName(),"dbNewStudentVO.getFirstName() null");
		assertEquals(dbNewStudentVO.getFirstName(), dbNewStudentVO.getFirstName(),"assertEquals dbNewStudentVO.getFirstName() failure");
		assertEquals(dbNewStudentVO.getEmailAddress(), dbNewStudentVO.getEmailAddress(),"assertEquals dbNewStudentVO.getEmailAddress() failure");
		
		// update student email (value belong to other instructor id 1)
		dbNewStudentVO.setEmailAddress(studentVO.getEmailAddress());
				
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(dbNewStudentVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(getMessage(VALIDATION_FAILED), ActionType.UPDATE)))) // verify json root element message
			.andExpect(jsonPath("$.errors", hasSize(1))) // verify that json root element $ size
//        .andExpect(jsonPath("$.errors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_FIRST_NAME_EMPTY.getMessage()), ActionType.CREATE))))
//        .andExpect(jsonPath("$.errors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_LAST_NAME_EMPTY.getMessage()), ActionType.CREATE))))
	        .andExpect(jsonPath("$.errors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_EMAIL_ALREADY_EXIST.getMessage()), ActionType.CREATE))))
			;
	}
	
	@Test
	void updateStudentNotExistRoleAdmin() throws Exception {
		
		updateStudentNotExist(ROLE_ADMIN);
	}
	
	@Test
	void updateStudentNotExistRoleInstructor() throws Exception {
		
		updateStudentNotExist(ROLE_INSTRUCTOR);
	}
	
	private void updateStudentNotExist(String validationRole) throws Exception {
		log.info("updateStudentNotExistRoleAdmin {}", validationRole);
		
		int id = 2;
		
		// get student with id=2 (non existing)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", id)
				.param("validationRole", validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_student_id_not_found"), 2)))) // verify json element
			;
		
		// create new student
		StudentVO studentVO = new StudentVO("firstName", "lastName", "email@email.com");
		studentVO.setId(id);
		
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(studentVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_student_id_not_found"), id)))) // verify json element
			;
	}
	
	private String getMessage(String str) {
		return messageSource.getMessage(str, new Object[] {}, LocaleContextHolder.getLocale());
	}
}
