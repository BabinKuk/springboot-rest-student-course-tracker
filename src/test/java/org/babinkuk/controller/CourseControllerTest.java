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
import org.babinkuk.vo.InstructorVO;
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
	private static String INSTRUCTOR_ENROLL = "/instructor" + "/{instructorId}" + "/enroll";
	private static String INSTRUCTOR_WITHDRAW = "/instructor" + "/{instructorId}" + "/withdraw";
	private static String STUDENT_ENROLL = "/student" + "/{studentId}" + "/enroll";
	private static String STUDENT_WITHDRAW = "/student" + "/{studentId}" + "/withdraw";
	
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
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES)
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
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES)
				.param("validationRole", ROLE_INSTRUCTOR)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
		
		// get all courses (different validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES)
				.param("validationRole", ROLE_STUDENT)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
		
		// get all courses (without validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES)
			//	.param("validationRole", "ROLE_STUDENT")
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
		
		// get all courses (not existing validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES)
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
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/{id}", 1)
				.param("validationRole", ROLE_ADMIN)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.title", is("test course"))) // verify json element
			;

		// get course with id=2 (non existing)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/{id}", 2)
				.param("validationRole", ROLE_ADMIN)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_course_id_not_found"), 2)))) // verify json element
			;
		
		// get course with id=1 (validationRole ROLE_INSTRUCTOR)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/{id}", 1)
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
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/{id}", 2)
				.param("validationRole", ROLE_INSTRUCTOR)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_course_id_not_found"), 2)))) // verify json element
			;
		
		// get course with id=1 (validationRole ROLE_STUDENT)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/{id}", 1)
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
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/{id}", 2)
				.param("validationRole", ROLE_STUDENT)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_course_id_not_found"), 2)))) // verify json element
			;
		
		// get course with id=1 (without validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/{id}", 1)
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
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/{id}", 2)
				//.param("validationRole", validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_course_id_not_found"), 2)))) // verify json element
			;
		
		// get course with id=1 (non existing) (without validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/{id}", 1)
				//.param("validationRole", ROLE_NOT_EXIST)
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
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/{id}", 2)
				.param("validationRole", ROLE_NOT_EXIST)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_course_id_not_found"), 2)))) // verify json element
			;
	}
	
	@Test
	void addCourseRoleAdmin() throws Exception {

		addCourseSuccess(ROLE_ADMIN);
	}

	@Test
	void addCourseRoleInstructor() throws Exception {

		addCourseSuccess(ROLE_INSTRUCTOR);
	}
	
	private void addCourseSuccess(String validationRole) throws Exception {
		log.info("addCourseSuccess {}", validationRole);
		
		// create course
		CourseVO courseVO = new CourseVO("another course");
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + COURSES)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(courseVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(getMessage(CourseServiceImpl.COURSE_SAVE_SUCCESS)))) // verify json element
			;
		
		// additional check
		// get all courses
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES)
				.param("validationRole", validationRole)
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

		addCourseFail(ROLE_STUDENT);
	}
	
	@Test
	void addCourseNoRole() throws Exception {

		addCourseFail(null);
	}
	
	private void addCourseFail(String validationRole) throws Exception {
		log.info("addCourseFail {}", validationRole);
		
		// create course
		CourseVO courseVO = new CourseVO("another course");
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + COURSES)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(courseVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.CREATE)))) // verify json root element message
			;
		
		// additional check
		// get all courses
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES)
				.param("validationRole", validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is still size 1
			.andExpect(jsonPath("$[0].title", is("test course")))
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
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES)
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

		updateCourseSuccess(ROLE_ADMIN);
	}
	
	@Test
	void updateCourseRoleInstructor() throws Exception {

		updateCourseSuccess(ROLE_INSTRUCTOR);
	}
	
	private void updateCourseSuccess(String validationRole) throws Exception {
		log.info("updateCourseSuccess {}", validationRole);
		
		String courseTitle = "new test course";
		
		// update course id 1 with new title
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{id}", 1)
				.param("validationRole", validationRole)
				.param("title", courseTitle)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(getMessage(CourseServiceImpl.COURSE_SAVE_SUCCESS)))) // verify json element
			;
		
		// additional check
		// get course with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/{id}", 1)
				.param("validationRole", validationRole)
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
				.param("validationRole", validationRole)
				.param("title", courseTitle)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_course_id_not_found"), 2)))) // verify json element
			;
		
		// update course id 1 without title (required parameter)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{id}", 1)
				.param("validationRole", validationRole)
				//.param("title", courseTitle)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is("Required String parameter 'title' is not present"))) // verify json element
			;
			
		// update course id 1 with empty title (required parameter)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{id}", 1)
				.param("validationRole", validationRole)
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

		updateCourseFail(ROLE_STUDENT);
	}
	
	@Test
	void updateCourseNoRole() throws Exception {

		updateCourseFail(null);
	}
	
	private void updateCourseFail(String validationRole) throws Exception {
		log.info("updateCourseFail {}", validationRole);
		
		String courseTitle = "new test course";
		
		// update course id 1 with new title
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{id}", 1)
				.param("validationRole", validationRole)
				.param("title", courseTitle)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.UPDATE)))) // verify json root element message
			;
		
		// additional check
		// get course with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/{id}", 1)
				.param("validationRole", validationRole)
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
				.param("validationRole", validationRole)
				.param("title", courseTitle)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_course_id_not_found"), 2)))) // verify json element
			;
		
		// update course id 1 without title (required parameter)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{id}", 1)
				.param("validationRole", validationRole)
				//.param("title", courseTitle)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is("Required String parameter 'title' is not present"))) // verify json element
			;
		
		// update course id 1 with empty title (required parameter)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{id}", 1)
				.param("validationRole", validationRole)
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
		
		String validationRole = ROLE_ADMIN;
		
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
				.param("validationRole", validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(getMessage(CourseServiceImpl.COURSE_DELETE_SUCCESS)))) // verify json element
			;
		
		// get course with id=1 (non existing)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/{id}", id)
				.param("validationRole", validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_course_id_not_found"), id)))) //verify json element
			;
	}
	
	@Test
	void deleteCourseRoleInstructor() throws Exception {

		deleteCourseFail(ROLE_INSTRUCTOR);
	}
	
	@Test
	void deleteCourseRoleStudent() throws Exception {

		deleteCourseFail(ROLE_STUDENT);
	}
	
	@Test
	void deleteCourseNoRole() throws Exception {

		deleteCourseFail(null);
	}
	
	private void deleteCourseFail(String validationRole) throws Exception {
		log.info("deleteCourseRoleFail {}", validationRole);
		
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
				.param("validationRole", validationRole)
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
	
	@Test
	void enrollInstructorRoleAdmin() throws Exception {
		log.info("enrollInstructorRoleAdmin");
		
		String validationRole = ROLE_ADMIN;
		
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
		
		// check if instructor id 1 exists
		InstructorVO instructorVO = instructorService.findById(id);
		log.info(instructorVO.toString());
		
		assertNotNull(instructorVO,"courseVO null");
		assertEquals(1, instructorVO.getId());
		assertNotNull(instructorVO.getFirstName(),"instructorVO.getFirstName() null");
		assertEquals("firstNameInstr", instructorVO.getFirstName(),"assertEquals instructorVO.getFirstName() failure");
		
		// enroll instructor
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{courseId}" + INSTRUCTOR_ENROLL, id, id)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(getMessage(CourseServiceImpl.COURSE_SAVE_SUCCESS)))) // verify json element
			;
		
		// check course
		courseVO = courseService.findById(id);
		log.info(courseVO.toString());
		
		assertEquals(1, courseVO.getId());
		assertNotNull(courseVO.getInstructorVO(),"courseVO.getInstructorVO() not null");
		assertEquals(1, courseVO.getInstructorVO().getId(),"courseVO.getInstructorVO().getId()");
		assertEquals("firstNameInstr", courseVO.getInstructorVO().getFirstName(),"courseVO.getInstructorVO().getFirstName()");
		
		// enroll instructor (non existing course courseId=2)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{courseId}" + INSTRUCTOR_ENROLL, 2, id)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_course_id_not_found"), 2)))) // verify json element
			;
		
		// enroll non existing instructor id=2
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{courseId}" + INSTRUCTOR_ENROLL, id, 2)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_instructor_id_not_found"), 2)))) // verify json element
			;
	}
	
	@Test
	void enrollInstructorRoleInstructor() throws Exception {

		enrollInstructorFail(ROLE_INSTRUCTOR);
	}
	
	@Test
	void enrollInstructorRoleStudent() throws Exception {

		enrollInstructorFail(ROLE_STUDENT);
	}
	
	@Test
	void enrollInstructorNoRole() throws Exception {

		enrollInstructorFail(null);
	}
	
	private void enrollInstructorFail(String validationRole) throws Exception {
		log.info("enrollInstructorFail {}", validationRole);
		
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
		
		// check if instructor id 1 exists
		InstructorVO instructorVO = instructorService.findById(id);
		log.info(instructorVO.toString());
		
		assertNotNull(instructorVO,"courseVO null");
		assertEquals(1, instructorVO.getId());
		assertNotNull(instructorVO.getFirstName(),"instructorVO.getFirstName() null");
		assertEquals("firstNameInstr", instructorVO.getFirstName(),"assertEquals instructorVO.getFirstName() failure");
		
		// enroll instructor
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{courseId}" + INSTRUCTOR_ENROLL, id, id)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.ENROLL)))) // verify json root element message
			;
		
		// check course
		courseVO = courseService.findById(id);
		
		assertEquals(1, courseVO.getId());
		assertNull(courseVO.getInstructorVO(),"courseVO.getInstructorVO() null");
		
		// enroll instructor (non existing course courseId=2)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{courseId}" + INSTRUCTOR_ENROLL, 2, id)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_course_id_not_found"), 2)))) // verify json element
			;
		
		// enroll non existing instructor id=2
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{courseId}" + INSTRUCTOR_ENROLL, id, 2)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_instructor_id_not_found"), 2)))) // verify json element
			;
	}
	
	@Test
	void enrollInstructorRoleNotExist() throws Exception {
		log.info("enrollInstructorRoleNotExist");
		
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
		
		// check if instructor id 1 exists
		InstructorVO instructorVO = instructorService.findById(id);
		log.info(instructorVO.toString());
		
		assertNotNull(instructorVO,"courseVO null");
		assertEquals(1, instructorVO.getId());
		assertNotNull(instructorVO.getFirstName(),"instructorVO.getFirstName() null");
		assertEquals("firstNameInstr", instructorVO.getFirstName(),"assertEquals instructorVO.getFirstName() failure");
		
		// enroll instructor
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{courseId}" + INSTRUCTOR_ENROLL, id, id)
				.param("validationRole", ROLE_NOT_EXIST)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			//.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.ENROLL)))) // verify json root element message
			;
		
		// check course
		courseVO = courseService.findById(id);
		
		assertEquals(1, courseVO.getId());
		assertNull(courseVO.getInstructorVO(),"courseVO.getInstructorVO() null");
		
		// enroll instructor (non existing course courseId=2)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{courseId}" + INSTRUCTOR_ENROLL, 2, id)
				.param("validationRole", ROLE_NOT_EXIST)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			;
		
		// enroll non existing instructor id=2
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{courseId}" + INSTRUCTOR_ENROLL, id, 2)
				.param("validationRole", ROLE_NOT_EXIST)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			;
	}
	
	@Test
	void withdrawInstructorRoleAdmin() throws Exception {
		log.info("withdrawInstructorRoleAdmin");
		
		String validationRole = ROLE_ADMIN;
		
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
		
		// check if instructor id 1 exists
		InstructorVO instructorVO = instructorService.findById(id);
		log.info(instructorVO.toString());
		
		assertNotNull(instructorVO,"courseVO null");
		assertEquals(1, instructorVO.getId());
		assertNotNull(instructorVO.getFirstName(),"instructorVO.getFirstName() null");
		assertEquals("firstNameInstr", instructorVO.getFirstName(),"assertEquals instructorVO.getFirstName() failure");
		
		// enroll instructor
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{courseId}" + INSTRUCTOR_ENROLL, id, id)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(getMessage(CourseServiceImpl.COURSE_SAVE_SUCCESS)))) // verify json element
			;
		
		// check course
		courseVO = courseService.findById(id);
		log.info(courseVO.toString());
		
		assertEquals(1, courseVO.getId());
		assertNotNull(courseVO.getInstructorVO(),"courseVO.getInstructorVO() not null");
		assertEquals(1, courseVO.getInstructorVO().getId(),"courseVO.getInstructorVO().getId()");
		assertEquals("firstNameInstr", courseVO.getInstructorVO().getFirstName(),"courseVO.getInstructorVO().getFirstName()");
		
		// withdraw instructor id=1
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{courseId}" + INSTRUCTOR_WITHDRAW, id, id)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(getMessage(CourseServiceImpl.COURSE_SAVE_SUCCESS)))) // verify json element
			;
		
		// check course
		courseVO = courseService.findById(id);
		log.info(courseVO.toString());
		
		assertEquals(1, courseVO.getId());
		assertNull(courseVO.getInstructorVO(),"courseVO.getInstructorVO() null");
		
		// withdraw instructor (non existing course courseId=2)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{courseId}" + INSTRUCTOR_WITHDRAW, 2, id)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_course_id_not_found"), 2)))) // verify json element
			;
		
		// withdraw non existing instructor id=2
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{courseId}" + INSTRUCTOR_WITHDRAW, id, 2)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_instructor_id_not_found"), 2)))) // verify json element
			;
	}
	
	@Test
	void withdrawInstructorRoleInstructor() throws Exception {

		withdrawInstructorFail(ROLE_INSTRUCTOR);
	}

	@Test
	void withdrawInstructorRoleStudent() throws Exception {

		withdrawInstructorFail(ROLE_STUDENT);
	}
	
	@Test
	void withdrawInstructorNoRole() throws Exception {

		withdrawInstructorFail(null);
	}
	
	private void withdrawInstructorFail(String validationRole) throws Exception {
		log.info("withdrawInstructorFail {}", validationRole);
		
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
		
		// check if instructor id 1 exists
		InstructorVO instructorVO = instructorService.findById(id);
		log.info(instructorVO.toString());
		
		assertNotNull(instructorVO,"courseVO null");
		assertEquals(1, instructorVO.getId());
		assertNotNull(instructorVO.getFirstName(),"instructorVO.getFirstName() null");
		assertEquals("firstNameInstr", instructorVO.getFirstName(),"assertEquals instructorVO.getFirstName() failure");
		
		// enroll instructor
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{courseId}" + INSTRUCTOR_ENROLL, id, id)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.ENROLL)))) // verify json root element message
			;
		
		// check course
		courseVO = courseService.findById(id);
		log.info(courseVO.toString());
		
		assertEquals(1, courseVO.getId());
		assertNull(courseVO.getInstructorVO(),"courseVO.getInstructorVO() null");
		
		// withdraw instructor id=1
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{courseId}" + INSTRUCTOR_WITHDRAW, id, id)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.WITHDRAW)))) // verify json root element message
			;
		
		// check course
		courseVO = courseService.findById(id);
		log.info(courseVO.toString());
		
		assertEquals(1, courseVO.getId());
		assertNull(courseVO.getInstructorVO(),"courseVO.getInstructorVO() null");
		
		// withdraw instructor (non existing course courseId=2)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{courseId}" + INSTRUCTOR_WITHDRAW, 2, id)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_course_id_not_found"), 2)))) // verify json element
			;
		
		// withdraw non existing instructor id=2
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{courseId}" + INSTRUCTOR_WITHDRAW, id, 2)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_instructor_id_not_found"), 2)))) // verify json element
			;
	}
	
	@Test
	void withdrawInstructorRoleNotExist() throws Exception {
		log.info("withdrawInstructorRoleNotExist");
		
		String validationRole = ROLE_NOT_EXIST;
		
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
		
		// check if instructor id 1 exists
		InstructorVO instructorVO = instructorService.findById(id);
		log.info(instructorVO.toString());
		
		assertNotNull(instructorVO,"courseVO null");
		assertEquals(1, instructorVO.getId());
		assertNotNull(instructorVO.getFirstName(),"instructorVO.getFirstName() null");
		assertEquals("firstNameInstr", instructorVO.getFirstName(),"assertEquals instructorVO.getFirstName() failure");
		
		// enroll instructor
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{courseId}" + INSTRUCTOR_ENROLL, id, id)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			//.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.ENROLL)))) // verify json root element message
			;
		
		// check course
		courseVO = courseService.findById(id);
		log.info(courseVO.toString());
		
		assertEquals(1, courseVO.getId());
		assertNull(courseVO.getInstructorVO(),"courseVO.getInstructorVO() null");
		
		// withdraw instructor id=1
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{courseId}" + INSTRUCTOR_WITHDRAW, id, id)
				.param("validationRole", ROLE_NOT_EXIST)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			;
		
		// check course
		courseVO = courseService.findById(id);
		log.info(courseVO.toString());
		
		assertEquals(1, courseVO.getId());
		assertNull(courseVO.getInstructorVO(),"courseVO.getInstructorVO() null");
		
		// withdraw instructor (non existing course courseId=2)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{courseId}" + INSTRUCTOR_WITHDRAW, 2, id)
				.param("validationRole", ROLE_NOT_EXIST)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			;
		
		// withdraw non existing instructor id=2
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{courseId}" + INSTRUCTOR_WITHDRAW, id, 2)
				.param("validationRole", ROLE_NOT_EXIST)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			;
	}
	
	@Test
	void enrollStudentRoleAdmin() throws Exception {

		enrollStudentRoleInstructor(ROLE_ADMIN);
	}

	@Test
	void enrollStudentRoleInstructor() throws Exception {

		enrollStudentRoleInstructor(ROLE_INSTRUCTOR);
	}
	
	private void enrollStudentRoleInstructor(String validationRole) throws Exception {
		log.info("enrollStudentSuccess {}", validationRole);
		
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
		
		// create new student
		// set id 0: this is to force a save of new item ... instead of update
		StudentVO studentVO = new StudentVO("firstName", "lastName", "emailAddress@babinkuk.com");
		studentVO.setId(0);
				
		studentService.saveStudent(studentVO);
		
		// check new student
		StudentVO newStudentVO = studentService.findByEmail(studentVO.getEmailAddress());
		log.info(newStudentVO.toString());
		
		assertNotNull(newStudentVO,"courseVO null");
		assertNotNull(newStudentVO.getFirstName(),"newStudentVO.getFirstName() null");
		assertEquals(studentVO.getFirstName(), newStudentVO.getFirstName(),"assertEquals newStudentVO.getFirstName() failure");
		assertEquals(studentVO.getEmailAddress(), newStudentVO.getEmailAddress(),"assertEquals newStudentVO.getEmailAddress() failure");
		
		// enroll new student
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{courseId}" + STUDENT_ENROLL, id, newStudentVO.getId())
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(getMessage(CourseServiceImpl.COURSE_SAVE_SUCCESS)))) // verify json element
			;
		
		// check course
		courseVO = courseService.findById(id);
		log.info(courseVO.toString());
		
		assertEquals(1, courseVO.getId());
		assertNotNull(courseVO,"courseVO not null");
		assertEquals(2, courseVO.getStudentsVO().size(), "courseVO.getStudentsVO().size()");
        assertTrue(courseVO.getStudentsVO().stream().filter(o -> o.getEmailAddress().equals(studentVO.getEmailAddress())).findFirst().isPresent());
        
		// enroll student (non existing course courseId=2)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{courseId}" + STUDENT_ENROLL, 2, newStudentVO.getId())
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_course_id_not_found"), 2)))) // verify json element
			;
		
		// enroll non existing student id=2
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{courseId}" + STUDENT_ENROLL, id, 2222)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_student_id_not_found"), 2222)))) // verify json element
			;
	}
	
	@Test
	void enrollStudentRoleStudent() throws Exception {

		enrollStudentFail(ROLE_STUDENT);
	}
	
	@Test
	void enrollStudentNoRole() throws Exception {

		enrollStudentFail(null);
	}
	
	private void enrollStudentFail(String validationRole) throws Exception {
		log.info("enrollInstructorFail {}", validationRole);
		
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
		
		// create new student
		// set id 0: this is to force a save of new item ... instead of update
		StudentVO studentVO = new StudentVO("firstName", "lastName", "emailAddress@babinkuk.com");
		studentVO.setId(0);
				
		studentService.saveStudent(studentVO);
		
		// check new student
		StudentVO newStudentVO = studentService.findByEmail(studentVO.getEmailAddress());
		log.info(newStudentVO.toString());
		
		assertNotNull(newStudentVO,"courseVO null");
		assertNotNull(newStudentVO.getFirstName(),"newStudentVO.getFirstName() null");
		assertEquals(studentVO.getFirstName(), newStudentVO.getFirstName(),"assertEquals newStudentVO.getFirstName() failure");
		assertEquals(studentVO.getEmailAddress(), newStudentVO.getEmailAddress(),"assertEquals newStudentVO.getEmailAddress() failure");
		
		// enroll new student
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{courseId}" + STUDENT_ENROLL, id, newStudentVO.getId())
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
		.andExpect(status().is4xxClientError())
		.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
		.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.ENROLL)))) // verify json root element message
		;
		
		// check course
		courseVO = courseService.findById(id);
		log.info(courseVO.toString());
		
		assertEquals(1, courseVO.getId());
		assertNotNull(courseVO,"courseVO not null");
		assertEquals(1, courseVO.getStudentsVO().size(), "courseVO.getStudentsVO().size()");
        assertFalse(courseVO.getStudentsVO().stream().filter(o -> o.getEmailAddress().equals(studentVO.getEmailAddress())).findFirst().isPresent());
        
		// enroll student (non existing course courseId=2)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{courseId}" + STUDENT_ENROLL, 2, newStudentVO.getId())
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_course_id_not_found"), 2)))) // verify json element
			;
		
		// enroll non existing student id=2
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{courseId}" + STUDENT_ENROLL, id, 2222)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_student_id_not_found"), 2222)))) // verify json element
			;
	}
	@Test
	void enrollStudentRoleNotExist() throws Exception {
		log.info("enrollStudentRoleNotExist");
		
		String validationRole = ROLE_NOT_EXIST;
		
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
		
		// create new student
		// set id 0: this is to force a save of new item ... instead of update
		StudentVO studentVO = new StudentVO("firstName", "lastName", "emailAddress@babinkuk.com");
		studentVO.setId(0);
				
		studentService.saveStudent(studentVO);
		
		// check new student
		StudentVO newStudentVO = studentService.findByEmail(studentVO.getEmailAddress());
		log.info(newStudentVO.toString());
		
		assertNotNull(newStudentVO,"courseVO null");
		assertNotNull(newStudentVO.getFirstName(),"newStudentVO.getFirstName() null");
		assertEquals(studentVO.getFirstName(), newStudentVO.getFirstName(),"assertEquals newStudentVO.getFirstName() failure");
		assertEquals(studentVO.getEmailAddress(), newStudentVO.getEmailAddress(),"assertEquals newStudentVO.getEmailAddress() failure");
		
		// enroll new student
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{courseId}" + STUDENT_ENROLL, id, newStudentVO.getId())
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			//.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.ENROLL)))) // verify json root element message
			;
		
		// check course
		courseVO = courseService.findById(id);
		log.info(courseVO.toString());
		
		assertEquals(1, courseVO.getId());
		assertNotNull(courseVO,"courseVO not null");
		assertEquals(1, courseVO.getStudentsVO().size(), "courseVO.getStudentsVO().size()");
        assertFalse(courseVO.getStudentsVO().stream().filter(o -> o.getEmailAddress().equals(studentVO.getEmailAddress())).findFirst().isPresent());
        
		// enroll student (non existing course courseId=2)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{courseId}" + STUDENT_ENROLL, 2, newStudentVO.getId())
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			;
		
		// enroll non existing student id=2
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{courseId}" + STUDENT_ENROLL, id, 2222)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			;
	}
	
	@Test
	void withdrawStudentRoleAdmin() throws Exception {

		withdrawStudentSuccess(ROLE_ADMIN);
	}
	
	@Test
	void withdrawStudentRoleInstructor() throws Exception {

		withdrawStudentSuccess(ROLE_INSTRUCTOR);
	}
	
	private void withdrawStudentSuccess(String validationRole) throws Exception {
		log.info("withdrawStudentSuccess {}", validationRole);
		
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
		assertTrue(courseVO.getStudentsVO().stream().filter(o -> o.getId() == 1).findFirst().isPresent());
        
		// withdraw instructor id=1
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{courseId}" + STUDENT_WITHDRAW, id, id)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(getMessage(CourseServiceImpl.COURSE_SAVE_SUCCESS)))) // verify json element
			;
		
		// check course
		courseVO = courseService.findById(id);
		log.info(courseVO.toString());
		
		assertEquals(1, courseVO.getId());
		assertEquals(0, courseVO.getStudentsVO().size());
		assertFalse(courseVO.getStudentsVO().stream().filter(o -> o.getId() == 1).findFirst().isPresent());
        
		// withdraw instructor (non existing course courseId=2)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{courseId}" + STUDENT_WITHDRAW, 2, id)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_course_id_not_found"), 2)))) // verify json element
			;
		
		// withdraw non existing instructor id=2
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{courseId}" + STUDENT_WITHDRAW, id, 2)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_student_id_not_found"), 2)))) // verify json element
			;
	}
	
	@Test
	void withdrawStudentRoleStudent() throws Exception {

		withdrawStudentFail(ROLE_STUDENT);
	}
	
	@Test
	void withdrawStudentNoRole() throws Exception {

		withdrawStudentFail(null);
	}
	
	@Test
	void withdrawStudentRoleNotExist() throws Exception {
		log.info("withdrawStudentRoleNotExist");
		
		String validationRole = ROLE_NOT_EXIST;
		
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
		assertTrue(courseVO.getStudentsVO().stream().filter(o -> o.getId() == 1).findFirst().isPresent());
        
		// withdraw student id=1
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{courseId}" + STUDENT_WITHDRAW, id, id)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			//.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.WITHDRAW)))) // verify json root element message
			;
		
		// check course
		courseVO = courseService.findById(id);
		log.info(courseVO.toString());
		
		assertEquals(1, courseVO.getId());
		assertEquals(1, courseVO.getStudentsVO().size());
		assertTrue(courseVO.getStudentsVO().stream().filter(o -> o.getId() == 1).findFirst().isPresent());
        
		// withdraw student (non existing course courseId=2)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{courseId}" + STUDENT_WITHDRAW, 2, id)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			;
		
		// withdraw non existing student id=2
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{courseId}" + STUDENT_WITHDRAW, id, 2)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			;
	}

	private void withdrawStudentFail(String validationRole) throws Exception {
		log.info("withdrawStudentFail {}", validationRole);
		
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
		assertTrue(courseVO.getStudentsVO().stream().filter(o -> o.getId() == 1).findFirst().isPresent());
        
		// withdraw student id=1
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{courseId}" + STUDENT_WITHDRAW, id, id)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.WITHDRAW)))) // verify json root element message
			;
		
		// check course
		courseVO = courseService.findById(id);
		log.info(courseVO.toString());
		
		assertEquals(1, courseVO.getId());
		assertEquals(1, courseVO.getStudentsVO().size());
		assertTrue(courseVO.getStudentsVO().stream().filter(o -> o.getId() == 1).findFirst().isPresent());
        
		// withdraw student (non existing course courseId=2)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{courseId}" + STUDENT_WITHDRAW, 2, id)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_course_id_not_found"), 2)))) // verify json element
			;
		
		// withdraw non existing student id=2
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{courseId}" + STUDENT_WITHDRAW, id, 2)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_student_id_not_found"), 2)))) // verify json element
			;
	}
	private String getMessage(String str) {
		return messageSource.getMessage(str, new Object[] {}, LocaleContextHolder.getLocale());
	}
}
