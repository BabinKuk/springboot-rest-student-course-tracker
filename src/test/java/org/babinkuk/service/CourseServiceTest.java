package org.babinkuk.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.vo.CourseVO;
import org.babinkuk.vo.InstructorVO;
import org.babinkuk.vo.StudentVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CourseServiceTest {
	
	public static final Logger log = LogManager.getLogger(CourseServiceTest.class);
	
	@Autowired
	private JdbcTemplate jdbc;
	
	@Autowired
	private MessageSource messageSource;
	
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
	
	@Test
	void getCourse() {
		log.info("getCourse");
		
		CourseVO courseVO = courseService.findById(1);
		
		log.info(courseVO.toString());
		
		assertNotNull(courseVO,"courseVO null");
		assertEquals(1, courseVO.getId());
		assertNotNull(courseVO.getTitle(),"courseVO.courseVO() null");
		assertNotNull(courseVO.getStudentsVO(),"courseVO.getStudentsVO() null");
		assertNull(courseVO.getInstructorVO(),"courseVO.getInstructorVO() not null");
		assertEquals("test course", courseVO.getTitle(),"courseVO.getTitle() NOK");
		
		// assert not existing course
		Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
			courseService.findById(2);
		});
		
		String expectedMessage = "Course with id=2 not found.";
		String actualMessage = exception.getMessage();

	    assertTrue(actualMessage.contains(expectedMessage));
	}
	
	@Test
	void addCourse() {
		log.info("addCourse");
		
		// create course
		// set id 0: this is to force a save of new item ... instead of update
		CourseVO courseVO = new CourseVO("tecaj");
		courseVO.setId(0);
		
		courseService.saveCourse(courseVO);
		
		CourseVO courseVO2 = courseService.findById(2);
		
		log.info(courseVO2);

		// assert
		assertEquals(2, courseVO2.getId());
		assertNotNull(courseVO2,"courseVO2 null");
		assertEquals(courseVO.getTitle(), courseVO2.getTitle(),"courseVO.getTitle() NOK");
	}
	
	@Test
	void updateCourse() {
		log.info("updateCourse");
		
		CourseVO courseVO = courseService.findById(1);
		
		// update with new data
		String title = "naslov";
		InstructorVO instructorVO = instructorService.findById(1);
		// create student
		// set id 0: this is to force a save of new item ... instead of update
		StudentVO studentVO = new StudentVO("firstName", "lastName", "emailAddress");
		studentVO.setId(0);
		
		studentService.saveStudent(studentVO);
				
		StudentVO studentVO2 = studentService.findByEmail("emailAddress");
		
		courseVO.setTitle(title);
		courseVO.setInstructorVO(instructorVO);
		courseVO.addStudentVO(studentVO2);

		courseService.saveCourse(courseVO);
		
		// fetch again
		CourseVO courseVO2 = courseService.findById(1);
		
		// assert
		assertEquals(courseVO.getId(), courseVO2.getId());
		assertEquals(title, courseVO2.getTitle(),"courseVO.getFirstName() NOK");
		assertEquals(instructorVO.getId(), courseVO2.getInstructorVO().getId(),"courseVO.getInstructorVO().getId() NOK");
		assertEquals(2, courseVO2.getStudentsVO().size(),"courseVO.getStudentsVO().size() NOK");
		assertEquals(1, courseVO2.getReviewsVO().size(),"courseVO.getReviewsVO().size() NOK");
	}
	
	@Test
	void deleteCourse() {
		log.info("deleteCourse");
		
		// first get course
		CourseVO courseVO = courseService.findById(1);
		
		// assert
		assertNotNull(courseVO, "return true");
		assertEquals(1, courseVO.getId());
		
		// delete
		courseService.deleteCourse(1);
		
		// assert not existing student
		Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
			courseService.findById(1);
		});
			
		String expectedMessage = "Course with id=1 not found.";
		String actualMessage = exception.getMessage();
		
	    assertTrue(actualMessage.contains(expectedMessage));

		// delete not existing course
		exception = assertThrows(EmptyResultDataAccessException.class, () -> {
			courseService.deleteCourse(2);
		});
	}
	
	@Test
	void getAllCourses() {
		log.info("getAllCourses");
		
		Iterable<CourseVO> courses = courseService.getAllCourses();
		
		// assert
		if (courses instanceof Collection<?>) {
			assertEquals(1, ((Collection<?>) courses).size(), "courses size not 1");
		}
		
		// create course
		// set id 0: this is to force a save of new item ... instead of update
		CourseVO courseVO = new CourseVO("tecaj");
		courseVO.setId(0);
		
		courseService.saveCourse(courseVO);
		
		courses = courseService.getAllCourses();
		
		// assert
		if (courses instanceof Collection<?>) {
			assertEquals(2, ((Collection<?>) courses).size(), "courses size not 2 after insert");
		}
		
		// delete course
		courseService.deleteCourse(1);
		
		courses = courseService.getAllCourses();
		log.info("after delete " + courses.toString());
		
		// assert
		if (courses instanceof Collection<?>) {
			assertEquals(1, ((Collection<?>) courses).size(), "courses size not 1 after delete");
		}
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
	
	private String getMessage(String str) {
		return messageSource.getMessage(str, new Object[] {}, LocaleContextHolder.getLocale());
	}
}
