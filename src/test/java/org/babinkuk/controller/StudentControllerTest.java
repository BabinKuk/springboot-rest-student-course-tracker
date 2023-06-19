package org.babinkuk.controller;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.service.StudentService;
import org.babinkuk.service.StudentServiceImpl;
import org.babinkuk.validator.ActionType;
import org.babinkuk.validator.ValidatorCodes;
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
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.babinkuk.controller.Api.ROOT;
import static org.babinkuk.controller.Api.STUDENTS;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
public class StudentControllerTest {
	
	public static final Logger log = LogManager.getLogger(StudentControllerTest.class);
	
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
	private StudentService studentService;
	
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
	void getAllStudents() throws Exception {
		log.info("getAllStudents");
		
		// get all students
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS)
				.param("validationRole", ROLE_ADMIN)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is size 1
			;

		// add another student
		StudentVO studentVO = new StudentVO("firstName", "lastName", "emailAddress");
		
		studentService.saveStudent(studentVO);
				
		// get all students (different validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS)
				.param("validationRole", ROLE_INSTRUCTOR)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
		
		// get all students (different validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS)
				.param("validationRole", ROLE_STUDENT)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
		
		// get all students (without validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS)
			//	.param("validationRole", "ROLE_STUDENT")
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
		
		// get all students (not existing validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS)
				.param("validationRole", ROLE_NOT_EXIST)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
	}
	
	@Test
	void getStudent() throws Exception {
		log.info("getStudent");
		
		// get student with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", 1)
				.param("validationRole", ROLE_ADMIN)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.firstName", is("firstNameStudent"))) // verify json element
			;

		// get student with id=2 (non existing)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", 2)
				.param("validationRole", ROLE_ADMIN)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_student_id_not_found"), 2)))) // verify json element
			;
		
		// get student with id=1 (validationRole ROLE_INSTRUCTOR)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", 1)
				.param("validationRole", ROLE_INSTRUCTOR)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.lastName", is("lastNameStudent"))) // verify json element
			;

		// get student with id=2 (non existing) (validationRole ROLE_INSTRUCTOR)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", 2)
				.param("validationRole", ROLE_INSTRUCTOR)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_student_id_not_found"), 2)))) // verify json element
			;
		
		// get student with id=1 (validationRole ROLE_STUDENT)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", 1)
				.param("validationRole", ROLE_STUDENT)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.emailAddress", is("firstNameStudent@babinuk.com"))) // verify json element
			;

		// get student with id=2 (non existing) (validationRole ROLE_STUDENT)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", 2)
				.param("validationRole", ROLE_STUDENT)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_student_id_not_found"), 2)))) // verify json element
			;
		
		// get student with id=1 (without validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", 1)
				//.param("validationRole", "ROLE_STUDENT")
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.emailAddress", is("firstNameStudent@babinuk.com"))) // verify json element
			;
		
		// get student with id=2 (non existing) (without validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", 2)
				//.param("validationRole", ROLE_STUDENT)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_student_id_not_found"), 2)))) // verify json element
			;
		
		// get student with id=1 (non existing) (without validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", 1)
				.param("validationRole", ROLE_NOT_EXIST)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.firstName", is("firstNameStudent"))) // verify json element
			.andExpect(jsonPath("$.lastName", is("lastNameStudent"))) // verify json element
			;
	
		// get student with id=2 (non existing) (not existing validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", 2)
				.param("validationRole", ROLE_NOT_EXIST)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_student_id_not_found"), 2)))) // verify json element
			;
	}
	
	@Test
	void addStudentRoleAdmin() throws Exception {
		log.info("addStudentRoleAdmin");
		
		String validationRole = ROLE_ADMIN;
		
		// create student
		StudentVO studentVO = new StudentVO("firstName", "lastName", "emailAddress@email.hr");
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + STUDENTS)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(studentVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(getMessage(StudentServiceImpl.STUDENT_SAVE_SUCCESS)))) // verify json element
			;
		
		// additional check
		// get all students
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS)
				.param("validationRole", validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
		
		// additional check
		studentVO = studentService.findByEmail("emailAddress@email.hr");
		
		log.info(studentVO.toString());
		
		assertNotNull(studentVO,"studentVO null");
		assertNotNull(studentVO.getFirstName(),"studentVO.getFirstName() null");
		assertNotNull(studentVO.getLastName(),"studentVO.getLastName() null");
		assertNotNull(studentVO.getEmailAddress(),"studentVO.getEmailAddress() null");
		assertEquals("firstName", studentVO.getFirstName(),"studentVO.getFirstName() NOK");
		assertEquals("lastName", studentVO.getLastName(),"studentVO.getLastName() NOK");
	}

	@Test
	void addStudentRoleInstructor() throws Exception {
		log.info("addStudentRoleInstructor");
		
		String validationRole = ROLE_INSTRUCTOR;
		
		// create student
		StudentVO studentVO = new StudentVO("firstName", "lastName", "emailAddress@email.hr");
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + STUDENTS)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(studentVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(getMessage(StudentServiceImpl.STUDENT_SAVE_SUCCESS)))) // verify json element
			;
		
		// additional check
		// get all students
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS)
				.param("validationRole", validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
		
		// additional check
		studentVO = studentService.findByEmail("emailAddress@email.hr");
		
		log.info(studentVO.toString());
		
		assertNotNull(studentVO,"studentVO null");
		assertNotNull(studentVO.getFirstName(),"studentVO.getFirstName() null");
		assertNotNull(studentVO.getLastName(),"studentVO.getLastName() null");
		assertNotNull(studentVO.getEmailAddress(),"studentVO.getEmailAddress() null");
		assertEquals("firstName", studentVO.getFirstName(),"studentVO.getFirstName() NOK");
		assertEquals("lastName", studentVO.getLastName(),"studentVO.getLastName() NOK");
	}
	
	@Test
	void addStudentRoleStudent() throws Exception {
		log.info("addStudentRoleStudent");
		
		String validationRole = ROLE_STUDENT;
		
		// create student
		StudentVO studentVO = new StudentVO("firstName", "lastName", "emailAddress@email.hr");
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + STUDENTS)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(studentVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.CREATE)))) // verify json root element message
			;
		
		// additional check
		// get all students
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS)
				.param("validationRole", validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is still size 1
			;
	}
	
	@Test
	void addStudentNoRole() throws Exception {
		log.info("addStudentNoRole");
		
		// create student
		StudentVO studentVO = new StudentVO("firstName", "lastName", "emailAddress@email.hr");
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + STUDENTS)
				//.param("validationRole", ROLE_STUDENT)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(studentVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.CREATE)))) // verify json root element message
			;
		
		// additional check
		// get all students
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS)
				//.param("validationRole", ROLE_STUDENT)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is still size 1
			;
	}
	
	@Test
	void addStudentRoleNotExist() throws Exception {
		log.info("addStudentRoleNotExist");
		
		String validationRole = ROLE_NOT_EXIST;
		
		// create student
		StudentVO studentVO = new StudentVO("firstName", "lastName", "emailAddress@email.hr");
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + STUDENTS)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(studentVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			;
		
		// additional check
		// get all students
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS)
				.param("validationRole", validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is stil size 1
			;
	}
	
	@Test
	void updateStudentRoleAdmin() throws Exception {
		log.info("updateStudentRoleAdmin");
		
		String validationRole = ROLE_ADMIN;
		
		// check if student id 1 exists
		StudentVO studentVO = studentService.findById(1);
		log.info(studentVO.toString());
		
		assertNotNull(studentVO,"studentVO null");
		assertEquals(1, studentVO.getId());
		assertNotNull(studentVO.getFirstName(),"studentVO.getFirstName() null");
		assertEquals("firstNameStudent", studentVO.getFirstName(),"assertEquals studentVO.getFirstName() failure");
		
		// update student
		studentVO.setFirstName("firstName");
		studentVO.setLastName("lastName");
		studentVO.setEmailAddress("email@email.com");
				
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(studentVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(getMessage(StudentServiceImpl.STUDENT_SAVE_SUCCESS)))) // verify json element
			;
		
		// additional check
		// get student with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", 1)
				.param("validationRole", validationRole)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.firstName", is("firstName"))) // verify json element
			.andExpect(jsonPath("$.lastName", is("lastName"))) // verify json element
			.andExpect(jsonPath("$.emailAddress", is("email@email.com"))) // verify json element
			;
	}
	
	@Test
	void updateStudentRoleInstructor() throws Exception {
		log.info("updateStudentRoleInstructor");
		
		String validationRole = ROLE_INSTRUCTOR;
		
		// check if student id 1 exists
		StudentVO studentVO = studentService.findById(1);
		log.info(studentVO.toString());
		
		assertNotNull(studentVO,"studentVO null");
		assertEquals(1, studentVO.getId());
		assertNotNull(studentVO.getFirstName(),"studentVO.getFirstName() null");
		assertEquals("firstNameStudent", studentVO.getFirstName(),"assertEquals studentVO.getFirstName() failure");
		
		// update student
		studentVO.setFirstName("firstName");
		studentVO.setLastName("lastName");
		studentVO.setEmailAddress("email@email.com");
				
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(studentVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(getMessage(StudentServiceImpl.STUDENT_SAVE_SUCCESS)))) // verify json element
			;
		
		// additional check
		// get student with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", 1)
				.param("validationRole", validationRole)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.firstName", is("firstName"))) // verify json element
			.andExpect(jsonPath("$.lastName", is("lastName"))) // verify json element
			.andExpect(jsonPath("$.emailAddress", is("email@email.com"))) // verify json element
			;
	}
	
//	@Test
	void updateStudentRoleStudent() throws Exception {
		log.info("updateStudentRoleStudent");
		
		String validationRole = ROLE_STUDENT;
		
		// check if student id 1 exists
		StudentVO studentVO = studentService.findById(1);
		log.info(studentVO.toString());
		
		assertNotNull(studentVO,"studentVO null");
		assertEquals(1, studentVO.getId());
		assertNotNull(studentVO.getFirstName(),"studentVO.getFirstName() null");
		assertEquals("firstNameStudent", studentVO.getFirstName(),"assertEquals studentVO.getFirstName() failure");
		
		// update student
		studentVO.setFirstName("firstName");
		studentVO.setLastName("lastName");
		studentVO.setEmailAddress("email@email.com");
				
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(studentVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.UPDATE)))) // verify json root element message
			;
		
		// additional check
		// get student with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", 1)
				.param("validationRole", validationRole)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.firstName", is("firstNameInstr"))) // verify json element
			.andExpect(jsonPath("$.lastName", is("lastNameInstr"))) // verify json element
			.andExpect(jsonPath("$.emailAddress", is("firstNameInstr@babinuk.com"))) // verify json element
			;
	}
	
	@Test
	void updateStudentNoRole() throws Exception {
		log.info("updateStudentNoRole");
		
		// check if student id 1 exists
		StudentVO studentVO = studentService.findById(1);
		log.info(studentVO.toString());
		
		assertNotNull(studentVO,"studentVO null");
		assertEquals(1, studentVO.getId());
		assertNotNull(studentVO.getFirstName(),"studentVO.getFirstName() null");
		assertEquals("firstNameStudent", studentVO.getFirstName(),"assertEquals studentVO.getFirstName() failure");
		
		// update student
		studentVO.setFirstName("firstName");
		studentVO.setLastName("lastName");
		studentVO.setEmailAddress("email@email.com");
				
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS)
				//.param("validationRole", ROLE_STUDENT)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(studentVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.UPDATE)))) // verify json root element message
			;
		
		// additional check
		// get student with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", 1)
				//.param("validationRole", ROLE_STUDENT)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.firstName", is("firstNameStudent"))) // verify json element
			.andExpect(jsonPath("$.lastName", is("lastNameStudent"))) // verify json element
			.andExpect(jsonPath("$.emailAddress", is("firstNameStudent@babinuk.com"))) // verify json element
			;
	}
	
	@Test
	void updateStudentRoleNotExist() throws Exception {
		log.info("updateStudentRoleNotExist");
		
		String validationRole = ROLE_NOT_EXIST;
		
		// check if student id 1 exists
		StudentVO studentVO = studentService.findById(1);
		log.info(studentVO.toString());
		
		assertNotNull(studentVO,"studentVO null");
		assertEquals(1, studentVO.getId());
		assertNotNull(studentVO.getFirstName(),"studentVO.getFirstName() null");
		assertEquals("firstNameStudent", studentVO.getFirstName(),"assertEquals studentVO.getFirstName() failure");
		
		// update student
		studentVO.setFirstName("firstName");
		studentVO.setLastName("lastName");
		studentVO.setEmailAddress("email@email.com");
				
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(studentVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			;
		
		// additional check
		// get student with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", 1)
				.param("validationRole", validationRole)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.firstName", is("firstNameStudent"))) // verify json element
			.andExpect(jsonPath("$.lastName", is("lastNameStudent"))) // verify json element
			.andExpect(jsonPath("$.emailAddress", is("firstNameStudent@babinuk.com"))) // verify json element
			;
	}
	
	@Test
	void deleteStudentRoleAdmin() throws Exception {
		log.info("deleteStudentRoleAdmin");
		
		String validationRole = ROLE_ADMIN;
		
		// check if student id 1 exists
		int id = 1;
		StudentVO studentVO = studentService.findById(id);
		log.info(studentVO.toString());
		
		assertNotNull(studentVO,"studentVO null");
		assertEquals(1, studentVO.getId());
		assertNotNull(studentVO.getFirstName(),"studentVO.getFirstName() null");
		assertEquals("firstNameStudent", studentVO.getFirstName(),"assertEquals studentVO.getFirstName() failure");
				
		// delete student
		mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + STUDENTS + "/{id}", id)
				.param("validationRole", validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(getMessage(StudentServiceImpl.STUDENT_DELETE_SUCCESS)))) // verify json element
			;
		
		// get student with id=1 (non existing)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", id)
				.param("validationRole", validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_student_id_not_found"), id)))) //verify json element
			;
	}
	
	@Test
	void deleteStudentRoleInstructor() throws Exception {
		log.info("deleteStudentRoleInstructor");
		
		String validationRole = ROLE_INSTRUCTOR;
		
		// check if student id 1 exists
		int id = 1;
		StudentVO studentVO = studentService.findById(id);
		log.info(studentVO.toString());
		
		assertNotNull(studentVO,"studentVO null");
		assertEquals(1, studentVO.getId());
		assertNotNull(studentVO.getFirstName(),"studentVO.getFirstName() null");
		assertEquals("firstNameStudent", studentVO.getFirstName(),"assertEquals studentVO.getFirstName() failure");
				
		// delete student
		mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + STUDENTS + "/{id}", id)
				.param("validationRole", validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.DELETE)))) // verify json root element message
			;
	}
	
	@Test
	void deleteStudentRoleStudent() throws Exception {
		log.info("deleteStudentRoleStudent");
		
		String validationRole = ROLE_STUDENT;
		
		// check if student id 1 exists
		int id = 1;
		StudentVO studentVO = studentService.findById(id);
		log.info(studentVO.toString());
		
		assertNotNull(studentVO,"studentVO null");
		assertEquals(1, studentVO.getId());
		assertNotNull(studentVO.getFirstName(),"studentVO.getFirstName() null");
		assertEquals("firstNameStudent", studentVO.getFirstName(),"assertEquals studentVO.getFirstName() failure");
				
		// delete student
		mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + STUDENTS + "/{id}", id)
				.param("validationRole", validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.DELETE)))) // verify json root element message
			;
	}

	@Test
	void deleteStudentNoRole() throws Exception {
		log.info("deleteStudentNoRole");
		
		// check if student id 1 exists
		int id = 1;
		StudentVO studentVO = studentService.findById(id);
		log.info(studentVO.toString());
		
		assertNotNull(studentVO,"studentVO null");
		assertEquals(1, studentVO.getId());
		assertNotNull(studentVO.getFirstName(),"studentVO.getFirstName() null");
		assertEquals("firstNameStudent", studentVO.getFirstName(),"assertEquals studentVO.getFirstName() failure");
				
		// delete student
		mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + STUDENTS + "/{id}", id)
				//.param("validationRole", ROLE_STUDENT)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.DELETE)))) // verify json root element message
			;
	}
	
	@Test
	void deleteStudentRoleNotExist() throws Exception {
		log.info("deleteStudentRoleNotExist");
		
		String validationRole = ROLE_NOT_EXIST;
		
		// check if student id 1 exists
		int id = 1;
		StudentVO studentVO = studentService.findById(id);
		log.info(studentVO.toString());
		
		assertNotNull(studentVO,"studentVO null");
		assertEquals(1, studentVO.getId());
		assertNotNull(studentVO.getFirstName(),"studentVO.getFirstName() null");
		assertEquals("firstNameStudent", studentVO.getFirstName(),"assertEquals studentVO.getFirstName() failure");
				
		// delete student
		mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + STUDENTS + "/{id}", id)
				.param("validationRole", validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			;
	}
	
	private String getMessage(String str) {
		return messageSource.getMessage(str, new Object[] {}, LocaleContextHolder.getLocale());
	}
}
