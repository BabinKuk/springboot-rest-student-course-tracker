package org.babinkuk.controller;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.service.CourseService;
import org.babinkuk.service.CourseServiceImpl;
import org.babinkuk.service.InstructorService;
import org.babinkuk.service.InstructorServiceImpl;
import org.babinkuk.service.ReviewService;
import org.babinkuk.service.ReviewServiceImpl;
import org.babinkuk.service.StudentService;
import org.babinkuk.service.StudentServiceImpl;
import org.babinkuk.vo.CourseVO;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.babinkuk.controller.Api.ROOT;
import static org.babinkuk.controller.Api.STUDENTS;
import static org.babinkuk.controller.Api.INSTRUCTORS;
import static org.babinkuk.controller.Api.REVIEWS;
import static org.babinkuk.controller.Api.COURSES;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
public class JpaTest {
	
	public static final Logger log = LogManager.getLogger(JpaTest.class);
	
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
	
	@Value("${sql.script.course.update}")
	private String sqlUpdateCourse;
	
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
		jdbc.execute(sqlUpdateCourse);
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
	void deleteCourseAndValidateCascadingObjects() throws Exception {
		log.info("deleteCourseAndValidateCascadingObjects");
		
		String validationRole = ROLE_ADMIN;
		int id = 1;
		
		// get course with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/{id}", id)
				.param("validationRole", validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.title", is("test course"))) // verify json element
			.andExpect(jsonPath("$.reviewsVO", hasSize(1))) // verify json element
			.andExpect(jsonPath("$.reviewsVO[0].id", is(1)))
			.andExpect(jsonPath("$.reviewsVO[0].comment", is("test review")))
			.andExpect(jsonPath("$.studentsVO", hasSize(1))) // verify json element
			.andExpect(jsonPath("$.studentsVO[0].id", is(1)))
			.andExpect(jsonPath("$.studentsVO[0].firstName", is("firstNameStudent")))
			.andExpect(jsonPath("$.studentsVO[0].lastName", is("lastNameStudent")))
			.andExpect(jsonPath("$.studentsVO[0].emailAddress", is("firstNameStudent@babinuk.com")))
			.andExpect(jsonPath("$.instructorVO.id", is(1)))
			.andExpect(jsonPath("$.instructorVO.firstName", is("firstNameInstr")))
			.andExpect(jsonPath("$.instructorVO.lastName", is("lastNameInstr")))
			.andExpect(jsonPath("$.instructorVO.emailAddress", is("firstNameInstr@babinuk.com")))
			;
		
		// delete course
		mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + COURSES + "/{id}", id)
				.param("validationRole", validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(getMessage(CourseServiceImpl.COURSE_DELETE_SUCCESS)))) // verify json element
			;

		// student must be unchanged
		// get student with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", id)
				.param("validationRole", validationRole)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.firstName", is("firstNameStudent"))) // verify json element
			.andExpect(jsonPath("$.lastName", is("lastNameStudent")))
			.andExpect(jsonPath("$.emailAddress", is("firstNameStudent@babinuk.com")))
			;
		
		// instructor must be unchanged
		// get instructor with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/{id}", id)
				.param("validationRole", validationRole)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.firstName", is("firstNameInstr")))
			.andExpect(jsonPath("$.lastName", is("lastNameInstr")))
			.andExpect(jsonPath("$.emailAddress", is("firstNameInstr@babinuk.com")))
			;
		
		// all related reviews are deleted
		// get review with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS + "/{id}", id)
				.param("validationRole", validationRole)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_review_id_not_found"), id)))) // verify json element
			;

	}
	
	@Test
	void deleteInstructorAndValidateCascadingObjects() throws Exception {
		log.info("deleteInstructorAndValidateCascadingObjects");
		
		String validationRole = ROLE_ADMIN;
		int id = 1;
		
		// get instructor with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/{id}", id)
				.param("validationRole", validationRole)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.firstName", is("firstNameInstr")))
			.andExpect(jsonPath("$.lastName", is("lastNameInstr")))
			.andExpect(jsonPath("$.emailAddress", is("firstNameInstr@babinuk.com")))
			;
		
		// get course with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/{id}", id)
				.param("validationRole", validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.title", is("test course"))) // verify json element
			.andExpect(jsonPath("$.reviewsVO", hasSize(1))) // verify json element
			.andExpect(jsonPath("$.reviewsVO[0].id", is(1)))
			.andExpect(jsonPath("$.reviewsVO[0].comment", is("test review")))
			.andExpect(jsonPath("$.studentsVO", hasSize(1))) // verify json element
			.andExpect(jsonPath("$.studentsVO[0].id", is(1)))
			.andExpect(jsonPath("$.studentsVO[0].firstName", is("firstNameStudent")))
			.andExpect(jsonPath("$.studentsVO[0].lastName", is("lastNameStudent")))
			.andExpect(jsonPath("$.studentsVO[0].emailAddress", is("firstNameStudent@babinuk.com")))
			.andExpect(jsonPath("$.instructorVO.id", is(1)))
			.andExpect(jsonPath("$.instructorVO.firstName", is("firstNameInstr")))
			.andExpect(jsonPath("$.instructorVO.lastName", is("lastNameInstr")))
			.andExpect(jsonPath("$.instructorVO.emailAddress", is("firstNameInstr@babinuk.com")))
			;
		
		// delete instructor
		mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + INSTRUCTORS + "/{id}", id)
				.param("validationRole", validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(getMessage(InstructorServiceImpl.INSTRUCTOR_DELETE_SUCCESS)))) // verify json element
			;
		
		// get instructor with id=1 (non existing)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/{id}", id)
				.param("validationRole", validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_instructor_id_not_found"), id)))) //verify json element
			;
		
		// get course with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/{id}", id)
				.param("validationRole", validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.title", is("test course"))) // verify json element
			.andExpect(jsonPath("$.reviewsVO", hasSize(1))) // verify json element
			.andExpect(jsonPath("$.reviewsVO[0].id", is(1)))
			.andExpect(jsonPath("$.reviewsVO[0].comment", is("test review")))
			.andExpect(jsonPath("$.studentsVO", hasSize(1))) // verify json element
			.andExpect(jsonPath("$.studentsVO[0].id", is(1)))
			.andExpect(jsonPath("$.studentsVO[0].firstName", is("firstNameStudent")))
			.andExpect(jsonPath("$.studentsVO[0].lastName", is("lastNameStudent")))
			.andExpect(jsonPath("$.studentsVO[0].emailAddress", is("firstNameStudent@babinuk.com")))
			.andExpect(jsonPath("$.instructorVO", nullValue()))
			;
		
		// student must be unchanged
		// get student with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", id)
				.param("validationRole", validationRole)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.firstName", is("firstNameStudent"))) // verify json element
			.andExpect(jsonPath("$.lastName", is("lastNameStudent")))
			.andExpect(jsonPath("$.emailAddress", is("firstNameStudent@babinuk.com")))
			;
		
		// get review with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS + "/{id}", 1)
				.param("validationRole", ROLE_ADMIN)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.comment", is("test review"))) // verify json element
			;
	}
	
	@Test
	void deleteStudentAndValidateCascadingObjects() throws Exception {
		log.info("deleteStudentAndValidateCascadingObjects");
		
		String validationRole = ROLE_ADMIN;
		int id = 1;
		
		// delete course_student
		jdbc.execute(sqlDeleteCourseStudent);
		
//		List<Map<String,Object>> courseStudentList = new ArrayList<Map<String,Object>>();
//		courseStudentList = jdbc.queryForList("select * from course_student");
//		log.info("courseStudentList.size() " + courseStudentList.size());
//		for (Map m : courseStudentList){
//			m.forEach((key, value) -> log.info(key + " : " + value));
//		}
		
		// get student with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", id)
				.param("validationRole", validationRole)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.firstName", is("firstNameStudent")))
			.andExpect(jsonPath("$.lastName", is("lastNameStudent")))
			.andExpect(jsonPath("$.emailAddress", is("firstNameStudent@babinuk.com")))
			;
		
		// get course with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/{id}", id)
				.param("validationRole", validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.title", is("test course"))) // verify json element
			.andExpect(jsonPath("$.reviewsVO", hasSize(1)))
			.andExpect(jsonPath("$.reviewsVO[0].id", is(1)))
			.andExpect(jsonPath("$.reviewsVO[0].comment", is("test review")))
			.andExpect(jsonPath("$.studentsVO", hasSize(0)))
			.andExpect(jsonPath("$.instructorVO.id", is(1)))
			.andExpect(jsonPath("$.instructorVO.firstName", is("firstNameInstr")))
			.andExpect(jsonPath("$.instructorVO.lastName", is("lastNameInstr")))
			.andExpect(jsonPath("$.instructorVO.emailAddress", is("firstNameInstr@babinuk.com")))
			;
		
		// enroll new student id=1
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{courseId}" + STUDENT_ENROLL, id, id)
				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(getMessage(CourseServiceImpl.COURSE_SAVE_SUCCESS)))) // verify json element
			;
		
//		courseStudentList = jdbc.queryForList("select * from course_student");
//		log.info("courseStudentList.size() after enroll " + courseStudentList.size());
//		for (Map m : courseStudentList){
//			m.forEach((key, value) -> log.info(key + " : " + value));
//		}
		
		// get course with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/{id}", id)
				.param("validationRole", validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.title", is("test course"))) // verify json element
			.andExpect(jsonPath("$.reviewsVO", hasSize(1)))
			.andExpect(jsonPath("$.reviewsVO[0].id", is(1)))
			.andExpect(jsonPath("$.reviewsVO[0].comment", is("test review")))
			.andExpect(jsonPath("$.studentsVO", hasSize(1)))
			.andExpect(jsonPath("$.studentsVO[0].id", is(1)))
			.andExpect(jsonPath("$.studentsVO[0].firstName", is("firstNameStudent")))
			.andExpect(jsonPath("$.studentsVO[0].lastName", is("lastNameStudent")))
			.andExpect(jsonPath("$.studentsVO[0].emailAddress", is("firstNameStudent@babinuk.com")))
			.andExpect(jsonPath("$.instructorVO.id", is(1)))
			.andExpect(jsonPath("$.instructorVO.firstName", is("firstNameInstr")))
			.andExpect(jsonPath("$.instructorVO.lastName", is("lastNameInstr")))
//			.andExpect(jsonPath("$.instructorVO.emailAddress", is("firstNameInstr@babinuk.com")))
			;
		
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
		
//		courseStudentList = jdbc.queryForList("select * from course_student");
//		log.info("courseStudentList.size() after delete " + courseStudentList.size());
//		for (Map m : courseStudentList){
//			m.forEach((key, value) -> log.info(key + " : " + value));
//		}

		// clear query results from cache
		entityManager.clear();

		// get course with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/{id}", id)
				.param("validationRole", validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.title", is("test course"))) // verify json element
			.andExpect(jsonPath("$.reviewsVO", hasSize(1))) // verify json element
			.andExpect(jsonPath("$.reviewsVO[0].id", is(1)))
			.andExpect(jsonPath("$.reviewsVO[0].comment", is("test review")))
			.andExpect(jsonPath("$.studentsVO", hasSize(0)))
			.andExpect(jsonPath("$.instructorVO.firstName", is("firstNameInstr")))
			.andExpect(jsonPath("$.instructorVO.lastName", is("lastNameInstr")))
			.andExpect(jsonPath("$.instructorVO.emailAddress", is("firstNameInstr@babinuk.com")))
			;
		
		// instructor must be unchanged
		// get instructor with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/{id}", id)
				.param("validationRole", validationRole)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.firstName", is("firstNameInstr")))
			.andExpect(jsonPath("$.lastName", is("lastNameInstr")))
			.andExpect(jsonPath("$.emailAddress", is("firstNameInstr@babinuk.com")))
			;
		
		// get review with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS + "/{id}", 1)
				.param("validationRole", ROLE_ADMIN)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.comment", is("test review"))) // verify json element
			;
	}
	
	@Test
	void deleteReviewAndValidateCascadingObjects() throws Exception {
		log.info("deleteReviewAndValidateCascadingObjects");
		
		String validationRole = ROLE_ADMIN;
		int id = 1;
		
		// get review with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS + "/{id}", 1)
				.param("validationRole", ROLE_ADMIN)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.comment", is("test review"))) // verify json element
			;

		// get course with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/{id}", id)
				.param("validationRole", validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.title", is("test course"))) // verify json element
			.andExpect(jsonPath("$.reviewsVO", hasSize(1)))
			.andExpect(jsonPath("$.reviewsVO[0].id", is(1)))
			.andExpect(jsonPath("$.reviewsVO[0].comment", is("test review")))
			.andExpect(jsonPath("$.studentsVO", hasSize(1)))
			.andExpect(jsonPath("$.studentsVO[0].id", is(1)))
			.andExpect(jsonPath("$.studentsVO[0].firstName", is("firstNameStudent")))
			.andExpect(jsonPath("$.studentsVO[0].lastName", is("lastNameStudent")))
			.andExpect(jsonPath("$.studentsVO[0].emailAddress", is("firstNameStudent@babinuk.com")))
			.andExpect(jsonPath("$.instructorVO.id", is(1)))
			.andExpect(jsonPath("$.instructorVO.firstName", is("firstNameInstr")))
			.andExpect(jsonPath("$.instructorVO.lastName", is("lastNameInstr")))
			.andExpect(jsonPath("$.instructorVO.emailAddress", is("firstNameInstr@babinuk.com")))
			;

		// delete review
		mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + REVIEWS + "/{id}", id)
				.param("validationRole", validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(getMessage(ReviewServiceImpl.REVIEW_DELETE_SUCCESS)))) // verify json element
			;
		
		// get review with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS + "/{id}", id)
				.param("validationRole", validationRole)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_review_id_not_found"), id)))) // verify json element
			;
		
		// get course with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/{id}", id)
				.param("validationRole", validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.title", is("test course"))) // verify json element
			.andExpect(jsonPath("$.reviewsVO", hasSize(0))) // verify json element
			.andExpect(jsonPath("$.reviewsVO[0].comment", is("test review")))
			.andExpect(jsonPath("$.studentsVO", hasSize(0)))
			.andExpect(jsonPath("$.instructorVO.firstName", is("firstNameInstr")))
			.andExpect(jsonPath("$.instructorVO.lastName", is("lastNameInstr")))
			.andExpect(jsonPath("$.instructorVO.emailAddress", is("firstNameInstr@babinuk.com")))
			;
		
		// instructor must be unchanged
		// get instructor with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/{id}", id)
				.param("validationRole", validationRole)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.firstName", is("firstNameInstr")))
			.andExpect(jsonPath("$.lastName", is("lastNameInstr")))
			.andExpect(jsonPath("$.emailAddress", is("firstNameInstr@babinuk.com")))
			;
		
		// student must be unchanged
		// get student with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", id)
				.param("validationRole", validationRole)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.firstName", is("firstNameStudent"))) // verify json element
			.andExpect(jsonPath("$.lastName", is("lastNameStudent")))
			.andExpect(jsonPath("$.emailAddress", is("firstNameStudent@babinuk.com")))
			;
	
		// get review with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS + "/{id}", 1)
				.param("validationRole", ROLE_ADMIN)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.comment", is("test review"))) // verify json element
			;
	}
	
	private String getMessage(String str) {
		return messageSource.getMessage(str, new Object[] {}, LocaleContextHolder.getLocale());
	}
}
