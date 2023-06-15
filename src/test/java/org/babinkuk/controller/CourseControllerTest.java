package org.babinkuk.controller;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.service.CourseService;
import org.babinkuk.service.CourseServiceImpl;
import org.babinkuk.service.InstructorService;
import org.babinkuk.service.ReviewService;
import org.babinkuk.service.StudentService;
import org.babinkuk.validator.ActionType;
import org.babinkuk.validator.ValidatorCodes;
import org.babinkuk.vo.CourseVO;
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
import static org.hamcrest.Matchers.contains;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.babinkuk.controller.Api.ROOT;
import static org.babinkuk.controller.Api.COURSES;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
public class CourseControllerTest {
	
	public static final Logger log = LogManager.getLogger(CourseControllerTest.class);
	
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
	private CourseService courseService;
	
	@Autowired
	private InstructorService instructorService;
	
	@Autowired
	private StudentService studentService;
	
	@Autowired
	private ReviewService reviewService;
	
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
	void getAllCourses() throws Exception {
		log.info("getAllCourses");
		
		// get all courses
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/get")
				.param("validationRole", ROLE_ADMIN)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is size 1
			;

		// add another course
		CourseVO courseVO = new CourseVO("another course");
		
		courseService.saveCourse(courseVO);
				
		// get all courses (different validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/get")
				.param("validationRole", ROLE_INSTRUCTOR)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
		
		// get all courses (different validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/get")
				.param("validationRole", ROLE_STUDENT)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
		
		// get all courses (without validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/get")
			//	.param("validationRole", "ROLE_STUDENT")
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
		
		// get all courses (not existing validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/get")
				.param("validationRole", ROLE_NOT_EXIST)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
	}
	
	@Test
	void getCourse() throws Exception {
		log.info("getCourse");
		
		// get course with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/get/{id}", 1)
				.param("validationRole", ROLE_ADMIN)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.title", is("test course"))) // verify json element
			;

		// get course with id=2 (non existing)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/get/{id}", 2)
				.param("validationRole", ROLE_ADMIN)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_course_id_not_found"), 2)))) // verify json element
			;
		
		// get course with id=1 (validationRole ROLE_INSTRUCTOR)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/get/{id}", 1)
				.param("validationRole", ROLE_INSTRUCTOR)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.title", is("test course"))) // verify json element
			.andExpect(jsonPath("$.reviewsVO", hasSize(1))) // verify json element
			.andExpect(jsonPath("$.reviewsVO[0].comment", is("test review")))
			.andExpect(jsonPath("$.studentsVO", hasSize(1))) // verify json element
			.andExpect(jsonPath("$.studentsVO[0].firstName", is("firstNameStudent")))
			;

		// get course with id=2 (non existing) (validationRole ROLE_INSTRUCTOR)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/get/{id}", 2)
				.param("validationRole", ROLE_INSTRUCTOR)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_course_id_not_found"), 2)))) // verify json element
			;
		
		// get course with id=1 (validationRole ROLE_STUDENT)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/get/{id}", 1)
				.param("validationRole", ROLE_STUDENT)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.reviewsVO", hasSize(1))) // verify json element
			.andExpect(jsonPath("$.reviewsVO[0].comment", is("test review")))
			.andExpect(jsonPath("$.studentsVO", hasSize(1))) // verify json element
			.andExpect(jsonPath("$.studentsVO[0].firstName", is("firstNameStudent")))
			;

		// get course with id=2 (non existing) (validationRole ROLE_STUDENT)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/get/{id}", 2)
				.param("validationRole", ROLE_STUDENT)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_course_id_not_found"), 2)))) // verify json element
			;
		
		// get course with id=1 (without validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/get/{id}", 1)
				//.param("validationRole", "ROLE_STUDENT")
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.reviewsVO", hasSize(1))) // verify json element
			.andExpect(jsonPath("$.reviewsVO[0].comment", is("test review")))
			.andExpect(jsonPath("$.studentsVO", hasSize(1))) // verify json element
			.andExpect(jsonPath("$.studentsVO[0].firstName", is("firstNameStudent")))
			.andExpect(jsonPath("$.studentsVO[0].emailAddress", is("firstNameStudent@babinuk.com")))
			;
		
		// get course with id=2 (non existing) (without validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/get/{id}", 2)
				//.param("validationRole", ROLE_STUDENT)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_course_id_not_found"), 2)))) // verify json element
			;
		
		// get course with id=1 (non existing) (without validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/get/{id}", 1)
				.param("validationRole", ROLE_NOT_EXIST)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.reviewsVO", hasSize(1))) // verify json element
			.andExpect(jsonPath("$.reviewsVO[0].comment", is("test review")))
			.andExpect(jsonPath("$.studentsVO", hasSize(1))) // verify json element
			.andExpect(jsonPath("$.studentsVO[0].firstName", is("firstNameStudent")))
			.andExpect(jsonPath("$.studentsVO[0].lastName", is("lastNameStudent")))
			;
	
		// get course with id=2 (non existing) (not existing validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/get/{id}", 2)
				.param("validationRole", ROLE_NOT_EXIST)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_course_id_not_found"), 2)))) // verify json element
			;
	}
	
	@Test
	void addCourseRoleAdmin() throws Exception {
		log.info("addCourseRoleAdmin");
		
		// create course
		CourseVO courseVO = new CourseVO("another course");
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + COURSES)
				.param("validationRole", ROLE_ADMIN)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(courseVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(getMessage(CourseServiceImpl.COURSE_SAVE_SUCCESS)))) // verify json element
			;
		
		// additional check
		// get all courses
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/get")
				.param("validationRole", ROLE_ADMIN)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			.andExpect(jsonPath("$[0].title", is("test course")))
			.andExpect(jsonPath("$[1].title", is("another course")))
			;
	}

	@Test
	void addCourseRoleInstructor() throws Exception {
		log.info("addCourseRoleInstructor");
		
		// create course
		CourseVO courseVO = new CourseVO("another course");
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + COURSES)
				.param("validationRole", ROLE_INSTRUCTOR)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(courseVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(getMessage(CourseServiceImpl.COURSE_SAVE_SUCCESS)))) // verify json element
			;
		
		// additional check
		// get all courses
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/get")
				.param("validationRole", ROLE_INSTRUCTOR)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			.andExpect(jsonPath("$[0].title", is("test course")))
			.andExpect(jsonPath("$[1].title", is("another course")))
			;
	}
	
	@Test
	void addCourseRoleStudent() throws Exception {
		log.info("addCourseRoleStudent");
		
		// create course
		CourseVO courseVO = new CourseVO("another course");
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + COURSES)
				.param("validationRole", ROLE_STUDENT)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(courseVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.CREATE)))) // verify json root element message
			;
		
		// additional check
		// get all courses
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/get")
				.param("validationRole", ROLE_STUDENT)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is still size 1
			.andExpect(jsonPath("$[0].title", is("test course")))
//			.andExpect(jsonPath("$[1].title", is("another course")))
			;
	}
	
	@Test
	void addCourseNoRole() throws Exception {
		log.info("addCourseNoRole");
		
		// create course
		CourseVO courseVO = new CourseVO("another course");
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + COURSES)
				//.param("validationRole", ROLE_STUDENT)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(courseVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.CREATE)))) // verify json root element message
			;
		
		// additional check
		// get all courses
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/get")
				//.param("validationRole", ROLE_STUDENT)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is still size 1
			.andExpect(jsonPath("$[0].title", is("test course")))
//			.andExpect(jsonPath("$[1].title", is("another course")))
			;
	}
	
	@Test
	void addCourseRoleNotExist() throws Exception {
		log.info("addCourseRoleNotExist");
		
		// create course
		CourseVO courseVO = new CourseVO("another course");
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + COURSES)
				.param("validationRole", ROLE_NOT_EXIST)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(courseVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			//.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.CREATE)))) // verify json root element message
			;
		
		// additional check
		// get all courses
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/get")
				.param("validationRole", ROLE_NOT_EXIST)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is still size 1
			.andExpect(jsonPath("$[0].title", is("test course")))
//			.andExpect(jsonPath("$[1].title", is("another course")))
			;
	}
	
	@Test
	void updateCourseRoleAdmin() throws Exception {
		log.info("updateCourseRoleAdmin");
		
		String courseTitle = "new test course";
				
		// update course id 1 with new title
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{id}", 1)
				.param("validationRole", ROLE_ADMIN)
				.param("title", courseTitle)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(getMessage(CourseServiceImpl.COURSE_SAVE_SUCCESS)))) // verify json element
			;
		
		// additional check
		// get course with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/get/{id}", 1)
				.param("validationRole", ROLE_ADMIN)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.title", is(courseTitle))) // verify json element
			.andExpect(jsonPath("$.reviewsVO", hasSize(1))) // verify json element
			.andExpect(jsonPath("$.reviewsVO[0].comment", is("test review")))
			.andExpect(jsonPath("$.studentsVO", hasSize(1))) // verify json element
			.andExpect(jsonPath("$.studentsVO[0].firstName", is("firstNameStudent")))
			;
		
		// update course with id=2 (non existing)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{id}", 2)
				.param("validationRole", ROLE_ADMIN)
				.param("title", courseTitle)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_course_id_not_found"), 2)))) // verify json element
			;
		
		// update course id 1 without title (required parameter)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{id}", 1)
				.param("validationRole", ROLE_ADMIN)
				//.param("title", courseTitle)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is("Required String parameter 'title' is not present"))) // verify json element
			;
				
		// update course id 1 with empty title (required parameter)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{id}", 1)
				.param("validationRole", ROLE_ADMIN)
				.param("title", "")
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(getMessage("validation_failed")))) // verify json element
			.andExpect(jsonPath("$.errors", hasSize(1))) // verify json element
			.andExpect(jsonPath("$.errors", contains(getMessage("error_code_title_empty")))) // verify json element
			;	
	}
	
	@Test
	void updateCourseRoleInstructor() throws Exception {
		log.info("updateCourseRoleInstructor");
		
		String courseTitle = "new test course";
				
		// update course id 1 with new title
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{id}", 1)
				.param("validationRole", ROLE_INSTRUCTOR)
				.param("title", courseTitle)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(getMessage(CourseServiceImpl.COURSE_SAVE_SUCCESS)))) // verify json element
			;
		
		// additional check
		// get course with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/get/{id}", 1)
				.param("validationRole", ROLE_INSTRUCTOR)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.title", is(courseTitle))) // verify json element
			.andExpect(jsonPath("$.reviewsVO", hasSize(1))) // verify json element
			.andExpect(jsonPath("$.reviewsVO[0].comment", is("test review")))
			.andExpect(jsonPath("$.studentsVO", hasSize(1))) // verify json element
			.andExpect(jsonPath("$.studentsVO[0].firstName", is("firstNameStudent")))
			;
		
		// update course with id=2 (non existing)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{id}", 2)
				.param("validationRole", ROLE_INSTRUCTOR)
				.param("title", courseTitle)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_course_id_not_found"), 2)))) // verify json element
			;
		
		// update course id 1 without title (required parameter)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{id}", 1)
				.param("validationRole", ROLE_INSTRUCTOR)
				//.param("title", courseTitle)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is("Required String parameter 'title' is not present"))) // verify json element
			;
				
		// update course id 1 with empty title (required parameter)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{id}", 1)
				.param("validationRole", ROLE_INSTRUCTOR)
				.param("title", "")
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(getMessage("validation_failed")))) // verify json element
			.andExpect(jsonPath("$.errors", hasSize(1))) // verify json element
			.andExpect(jsonPath("$.errors", contains(getMessage("error_code_title_empty")))) // verify json element
			;	
	}
	
	@Test
	void updateCourseRoleStudent() throws Exception {
		log.info("updateCourseRoleStudent");
		
		String courseTitle = "new test course";
				
		// update course id 1 with new title
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{id}", 1)
				.param("validationRole", ROLE_STUDENT)
				.param("title", courseTitle)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.UPDATE)))) // verify json root element message
			;
		
		// additional check
		// get course with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/get/{id}", 1)
				.param("validationRole", ROLE_STUDENT)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.title", is("test course"))) // verify json element
			.andExpect(jsonPath("$.reviewsVO", hasSize(1))) // verify json element
			.andExpect(jsonPath("$.reviewsVO[0].comment", is("test review")))
			.andExpect(jsonPath("$.studentsVO", hasSize(1))) // verify json element
			.andExpect(jsonPath("$.studentsVO[0].firstName", is("firstNameStudent")))
			;
		
		// update course with id=2 (non existing)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{id}", 2)
				.param("validationRole", ROLE_STUDENT)
				.param("title", courseTitle)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_course_id_not_found"), 2)))) // verify json element
			;
		
		// update course id 1 without title (required parameter)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{id}", 1)
				.param("validationRole", ROLE_STUDENT)
				//.param("title", courseTitle)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is("Required String parameter 'title' is not present"))) // verify json element
			//.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.UPDATE)))) // verify json root element message
			;
		
		// update course id 1 with empty title (required parameter)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{id}", 1)
				.param("validationRole", ROLE_STUDENT)
				.param("title", "")
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.UPDATE)))) // verify json root element message
			;
	}
	
	@Test
	void updateCourseNoRole() throws Exception {
		log.info("updateCourseNoRole");
		
		String courseTitle = "new test course";
				
		// update course id 1 with new title
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{id}", 1)
				//.param("validationRole", ROLE_STUDENT)
				.param("title", courseTitle)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.UPDATE)))) // verify json root element message
			;
		
		// additional check
		// get course with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/get/{id}", 1)
				//.param("validationRole", ROLE_STUDENT)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.title", is("test course"))) // verify json element
			.andExpect(jsonPath("$.reviewsVO", hasSize(1))) // verify json element
			.andExpect(jsonPath("$.reviewsVO[0].comment", is("test review")))
			.andExpect(jsonPath("$.studentsVO", hasSize(1))) // verify json element
			.andExpect(jsonPath("$.studentsVO[0].firstName", is("firstNameStudent")))
			;
		
		// update course with id=2 (non existing)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{id}", 2)
				//.param("validationRole", ROLE_STUDENT)
				.param("title", courseTitle)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_course_id_not_found"), 2)))) // verify json element
			;
		
		// update course id 1 without title (required parameter)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{id}", 1)
				//.param("validationRole", ROLE_STUDENT)
				//.param("title", courseTitle)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is("Required String parameter 'title' is not present"))) // verify json element
			//.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.UPDATE)))) // verify json root element message
			;
		
		// update course id 1 with empty title (required parameter)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{id}", 1)
				//.param("validationRole", ROLE_STUDENT)
				.param("title", "")
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.UPDATE)))) // verify json root element message
			;
	}
	
	@Test
	void updateCourseRoleNotExist() throws Exception {
		log.info("updateCourseRoleNotExist");
		
		String courseTitle = "new test course";
				
		// update course id 1 with new title
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{id}", 1)
				.param("validationRole", ROLE_NOT_EXIST)
				.param("title", courseTitle)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			//.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.UPDATE)))) // verify json root element message
			;
		
		// update course with id=2 (non existing)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{id}", 2)
				.param("validationRole", ROLE_NOT_EXIST)
				.param("title", courseTitle)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			//.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.UPDATE)))) // verify json root element message
			;
		
		// update course id 1 without title (required parameter)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{id}", 1)
				.param("validationRole", ROLE_NOT_EXIST)
				//.param("title", courseTitle)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is("Required String parameter 'title' is not present"))) // verify json element
			//.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.UPDATE)))) // verify json root element message
			;
		
		// update course id 1 with empty title (required parameter)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{id}", 1)
				.param("validationRole", ROLE_NOT_EXIST)
				.param("title", "")
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			//.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.UPDATE)))) // verify json root element message
			;
	}
	
	@Test
	void deleteCourseRoleAdmin() throws Exception {
		log.info("deleteCourseRoleAdmin");
		
		// check if course id 1 exists
		int id = 1;
		CourseVO courseVO = courseService.findById(id);
		log.info(courseVO.toString());
		
		assertNotNull(courseVO,"courseVO null");
		assertEquals(1, courseVO.getId());
		assertNotNull(courseVO.getTitle(),"courseVO.getTitle() null");
		assertEquals("test course", courseVO.getTitle(),"assertEquals courseVO.getTitle() failure");
		assertNull(courseVO.getInstructorVO(),"courseVO.getInstructorVO() null");
		assertEquals(1, courseVO.getStudentsVO().size());
				
		// delete course
		mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + COURSES + "/{id}", id)
				.param("validationRole", ROLE_ADMIN)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(getMessage(CourseServiceImpl.COURSE_DELETE_SUCCESS)))) // verify json element
			;
		
		// get course with id=1 (non existing)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/get/{id}", id)
				.param("validationRole", ROLE_ADMIN)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_course_id_not_found"), id)))) //verify json element
			;
	}
	
	@Test
	void deleteCourseRoleInstructor() throws Exception {
		log.info("deleteCourseRoleInstructor");
		
		// check if course id 1 exists
		int id = 1;
		CourseVO courseVO = courseService.findById(id);
		log.info(courseVO.toString());
		
		assertNotNull(courseVO,"courseVO null");
		assertEquals(1, courseVO.getId());
		assertNotNull(courseVO.getTitle(),"courseVO.getTitle() null");
		assertEquals("test course", courseVO.getTitle(),"assertEquals courseVO.getTitle() failure");
		assertNull(courseVO.getInstructorVO(),"courseVO.getInstructorVO() null");
		assertEquals(1, courseVO.getStudentsVO().size());
		
		// delete student
		mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + COURSES + "/{id}", id)
				.param("validationRole", ROLE_INSTRUCTOR)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.DELETE)))) // verify json root element message
			;
	}
	
	@Test
	void deleteCourseRoleStudent() throws Exception {
		log.info("deleteCourseRoleStudent");
		
		// check if course id 1 exists
		int id = 1;
		CourseVO courseVO = courseService.findById(id);
		log.info(courseVO.toString());
		
		assertNotNull(courseVO,"courseVO null");
		assertEquals(1, courseVO.getId());
		assertNotNull(courseVO.getTitle(),"courseVO.getTitle() null");
		assertEquals("test course", courseVO.getTitle(),"assertEquals courseVO.getTitle() failure");
		assertNull(courseVO.getInstructorVO(),"courseVO.getInstructorVO() null");
		assertEquals(1, courseVO.getStudentsVO().size());
		
		// delete student
		mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + COURSES + "/{id}", id)
				.param("validationRole", ROLE_STUDENT)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.DELETE)))) // verify json root element message
			;
	}
	
	@Test
	void deleteCourseNoRole() throws Exception {
		log.info("deleteCourseNoRole");
		
		// check if course id 1 exists
		int id = 1;
		CourseVO courseVO = courseService.findById(id);
		log.info(courseVO.toString());
		
		assertNotNull(courseVO,"courseVO null");
		assertEquals(1, courseVO.getId());
		assertNotNull(courseVO.getTitle(),"courseVO.getTitle() null");
		assertEquals("test course", courseVO.getTitle(),"assertEquals courseVO.getTitle() failure");
		assertNull(courseVO.getInstructorVO(),"courseVO.getInstructorVO() null");
		assertEquals(1, courseVO.getStudentsVO().size());
		
		// delete student
		mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + COURSES + "/{id}", id)
				//.param("validationRole", ROLE_INSTRUCTOR)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.DELETE)))) // verify json root element message
			;
	}
	
	@Test
	void deleteCourseRoleNotExist() throws Exception {
		log.info("deleteCourseRoleNotExist");
		
		// check if course id 1 exists
		int id = 1;
		CourseVO courseVO = courseService.findById(id);
		log.info(courseVO.toString());
		
		assertNotNull(courseVO,"courseVO null");
		assertEquals(1, courseVO.getId());
		assertNotNull(courseVO.getTitle(),"courseVO.getTitle() null");
		assertEquals("test course", courseVO.getTitle(),"assertEquals courseVO.getTitle() failure");
		assertNull(courseVO.getInstructorVO(),"courseVO.getInstructorVO() null");
		assertEquals(1, courseVO.getStudentsVO().size());
		
		// delete student
		mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + COURSES + "/{id}", id)
				.param("validationRole", ROLE_NOT_EXIST)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			//.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.DELETE)))) // verify json root element message
			;
	}
	
	private String getMessage(String str) {
		return messageSource.getMessage(str, new Object[] {}, LocaleContextHolder.getLocale());
	}
}
