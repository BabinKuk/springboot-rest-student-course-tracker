package org.babinkuk.validator;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.service.CourseService;
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
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.babinkuk.controller.Api.ROOT;
import static org.babinkuk.controller.Api.COURSES;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
public class CourseValidatorTest {
	
	public static final Logger log = LogManager.getLogger(CourseValidatorTest.class);
	
	private static String ROLE_ADMIN = "ROLE_ADMIN";
	private static String ROLE_INSTRUCTOR = "ROLE_INSTRUCTOR";
	private static String ROLE_STUDENT = "ROLE_STUDENT";
	private static String ROLE_NOT_EXIST = "ROLE_NOT_EXIST";
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
	void addEmptyCourseRoleAdmin() throws Exception {

		addEmptyCourse(ROLE_ADMIN);
	}
	
	@Test
	void addEmptyCourseRoleInstructor() throws Exception {

		addEmptyCourse(ROLE_INSTRUCTOR);
	}

	private void addEmptyCourse(String validationRole) throws Exception {
		log.info("addEmptyCourse {}", validationRole);
		
		// create invalid course (empty title)
		CourseVO courseVO = new CourseVO();
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + COURSES)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(courseVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
		.andExpect(status().is4xxClientError())
		.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
		.andExpect(jsonPath("$.message", is(String.format(getMessage(VALIDATION_FAILED), ActionType.CREATE)))) // verify json root element message
		.andExpect(jsonPath("$.errors", hasSize(1))) // verify that json root element $ is size 1
        .andExpect(jsonPath("$.errors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_TITLE_EMPTY.getMessage()), ActionType.CREATE))))
		;
		
		// additional check
		// get all students
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES)
				.param("validationRole", validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is now size 1
			.andDo(MockMvcResultHandlers.print())
			;
	}
	
	@Test
	void addCourseTitleNotUniqueRoleAdmin() throws Exception {

		addCourseTitleNotUnique(ROLE_ADMIN);
	}
	
	@Test
	void addCourseTitleNotUniqueRoleInstructor() throws Exception {

		addCourseTitleNotUnique(ROLE_INSTRUCTOR);
	}
	
	private void addCourseTitleNotUnique(String validationRole) throws Exception {
		log.info("addCourseTitleNotUnique {}", validationRole);
		
		int id = 1;
		
		// check if course id 1 exists
		CourseVO courseVO = courseService.findById(id);
		
		assertNotNull(courseVO,"courseVO null");
		assertEquals(1, courseVO.getId());
		assertNotNull(courseVO.getTitle(),"courseVO.getTitle() null");
		
		// create invalid course (title already exists in db)
		CourseVO newCourseVO = new CourseVO(courseVO.getTitle());
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + COURSES)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(newCourseVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(getMessage(VALIDATION_FAILED), ActionType.CREATE)))) // verify json root element message
			.andExpect(jsonPath("$.errors", hasSize(1))) // verify that json root element $ is size 1
	        .andExpect(jsonPath("$.errors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_TITLE_ALREADY_EXIST.getMessage()), ActionType.CREATE))))
			;
		
		// additional check
		// get all instructors
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES)
				.param("validationRole", validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is size 1
			.andDo(MockMvcResultHandlers.print())
			;
	}
	
	@Test
	void updateCourseInvalidIdRoleAdmin() throws Exception {

		updateCourseInvalidId(ROLE_ADMIN);
	}
	
	@Test
	void updateCourseInvalidIdRoleInstructor() throws Exception {

		updateCourseInvalidId(ROLE_INSTRUCTOR);
	}
	
	private void updateCourseInvalidId(String validationRole) throws Exception {
		log.info("updateCourseInvalidId {}", validationRole);
		
		// check if course id 1 exists
		CourseVO courseVO = courseService.findById(1);
		
		assertNotNull(courseVO,"studentVO null");
		assertEquals(1, courseVO.getId());
		assertNotNull(courseVO.getTitle(),"courseVO.getTitle() null");
		
		// check if course id 2 exists
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/{id}", 2)
				.param("validationRole", validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_course_id_not_found"), 2)))) // verify json element
			;
		
		// update course with id=2 (non existing)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{id}", 2)
				.param("validationRole", validationRole)
				.param("title", "courseTitle")
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_course_id_not_found"), 2)))) // verify json element
			;
	}
	
	@Test
	void updateEmptyCourseRoleAdmin() throws Exception {

		updateEmptyCourse(ROLE_ADMIN);
	}
	
	@Test
	void updateEmptyCourseRoleInstructor() throws Exception {

		updateEmptyCourse(ROLE_INSTRUCTOR);
	}
	
	private void updateEmptyCourse(String validationRole) throws Exception {
		log.info("updateEmptyCourse {}", validationRole);
		
		int id = 1;
		
		// check if course id 1 exists
		CourseVO courseVO = courseService.findById(id);
		
		assertNotNull(courseVO,"studentVO null");
		assertEquals(1, courseVO.getId());
		assertNotNull(courseVO.getTitle(),"courseVO.getTitle() null");
		
		String courseTitle = "";
		
		// update course with empty title
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{id}", id)
				.param("validationRole", validationRole)
				.param("title", courseTitle)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			//.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(getMessage(VALIDATION_FAILED), ActionType.UPDATE)))) // verify json root element message
			.andExpect(jsonPath("$.errors", hasSize(1))) // verify that json root element $ is size 1
	        .andExpect(jsonPath("$.errors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_TITLE_EMPTY.getMessage()), ActionType.CREATE))))
			;
	}
	
	@Test
	void updateCourseTitleNotUniqueRoleAdmin() throws Exception {
		
		updateCourseTitleNotUnique(ROLE_ADMIN);
	}
	
	@Test
	void updateCourseTitleNotUniqueRoleInstructor() throws Exception {
		
		updateCourseTitleNotUnique(ROLE_INSTRUCTOR);
	}
	
	private void updateCourseTitleNotUnique(String validationRole) throws Exception {
		log.info("updateCourseTitleNotUnique {}", validationRole);
		
		int id = 1;
		
		// check if course id 1 exists
		CourseVO courseVO = courseService.findById(id);
		
		assertNotNull(courseVO,"studentVO null");
		assertEquals(1, courseVO.getId());
		assertNotNull(courseVO.getTitle(),"courseVO.getTitle() null");
		
		// create new course
		CourseVO newCourseVO = new CourseVO("New course");
		
		// save new course
		courseService.saveCourse(newCourseVO);
		
		// check if new course exists
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES)
				.param("validationRole", validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
		
		CourseVO dbNewCourseVO = courseService.findByTitle(newCourseVO.getTitle());
		
		assertNotNull(dbNewCourseVO,"dbNewCourseVO null");
		//assertEquals(1, dbNewStudentVO.getId());
		assertNotNull(dbNewCourseVO.getTitle(),"dbNewCourseVO.getTitle() null");
		assertEquals(newCourseVO.getTitle(), dbNewCourseVO.getTitle(),"assertEquals dbNewCourseVO.getTitle() failure");
		
		// update course title (value belong to course id 1)
		String courseTitle = courseVO.getTitle();
		
		// update course with new title
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{id}", dbNewCourseVO.getId())
				.param("validationRole", validationRole)
				.param("title", courseTitle)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			//.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(getMessage(VALIDATION_FAILED), ActionType.UPDATE)))) // verify json root element message
			.andExpect(jsonPath("$.errors", hasSize(1))) // verify that json root element $ is size 1
	        .andExpect(jsonPath("$.errors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_TITLE_ALREADY_EXIST.getMessage()), ActionType.CREATE))))
			;
	}
	
	private String getMessage(String str) {
		return messageSource.getMessage(str, new Object[] {}, LocaleContextHolder.getLocale());
	}
}
