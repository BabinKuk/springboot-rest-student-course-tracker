package org.babinkuk.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.exception.ObjectNotFoundException;
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
public class StudentServiceTest {
	
	public static final Logger log = LogManager.getLogger(StudentServiceTest.class);
	
	@Autowired
	private JdbcTemplate jdbc;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private StudentService studentService;
	
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
	void getStudentById() {
		log.info("getStudentById");
		
		StudentVO studentVO = studentService.findById(1);
		
		//log.info(studentVO.toString());
		
		assertNotNull(studentVO,"studentVO null");
		assertEquals(1, studentVO.getId());
		assertNotNull(studentVO.getFirstName(),"studentVO.getFirstName() null");
		assertNotNull(studentVO.getLastName(),"studentVO.getLastName() null");
		assertNotNull(studentVO.getEmailAddress(),"studentVO.getEmailAddress() null");
		assertEquals("firstNameStudent", studentVO.getFirstName(),"studentVO.getFirstName() NOK");
		assertEquals("lastNameStudent", studentVO.getLastName(),"studentVO.getLastName() NOK");
		assertEquals("firstNameStudent@babinuk.com", studentVO.getEmailAddress(),"studentVO.getEmailAddress() NOK");
		// not neccessary
		assertNotEquals("test", studentVO.getFirstName(),"studentVO.getFirstName() NOK");
		
		// assert not existing instructor
		Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
			studentService.findById(2);
		});
		
		String expectedMessage = "Student with id=2 not found.";
		String actualMessage = exception.getMessage();

	    assertTrue(actualMessage.contains(expectedMessage));
	}
	
	@Test
	void getStudentByEmail() {
		log.info("getStudentByEmail");
		
		StudentVO studentVO  = studentService.findByEmail("firstNameStudent@babinuk.com");
		
		//log.info(studentVO.toString());
		
		assertNotNull(studentVO,"studentVO null");
		assertEquals(1, studentVO.getId());
		assertNotNull(studentVO.getFirstName(),"studentVO.getFirstName() null");
		assertNotNull(studentVO.getLastName(),"studentVO.getLastName() null");
		assertNotNull(studentVO.getEmailAddress(),"studentVO.getEmailAddress() null");
		assertEquals("firstNameStudent", studentVO.getFirstName(),"studentVO.getFirstName() NOK");
		assertEquals("lastNameStudent", studentVO.getLastName(),"studentVO.getLastName() NOK");
		assertEquals("firstNameStudent@babinuk.com", studentVO.getEmailAddress(),"studentVO.getEmailAddress() NOK");
		// not neccessary
		assertNotEquals("test", studentVO.getFirstName(),"studentVO.getFirstName() NOK");
		
		// assert not existing instructor
		assertNull(studentService.findByEmail("email"),"not existing student not null");
	}
	
	@Test
	void addStudent() {
		log.info("addStudent");
		
		// first create student
		// set id 0: this is to force a save of new item ... instead of update
		StudentVO studentVO = new StudentVO("firstName", "lastName", "emailAddress");
		studentVO.setId(0);
		
		studentService.saveStudent(studentVO);
		
		StudentVO studentVO2 = studentService.findByEmail("emailAddress");
		
		//log.info(studentVO2);

		// assert
		//assertEquals(2, studentVO2.getId());
		assertEquals(studentVO.getFirstName(), studentVO2.getFirstName(),"studentVO.getFirstName() NOK");
		assertEquals(studentVO.getLastName(), studentVO2.getLastName(),"studentVO.getLastName() NOK");
		assertEquals(studentVO.getEmailAddress(), studentVO2.getEmailAddress(),"studentVO.getEmailAddress() NOK");
	}
	
	@Test
	void updateStudent() {
		log.info("updateStudent");
		
		StudentVO studentVO = studentService.findById(1);
		
		// update with new data
		String firstName = "ime";
		String lastName = "prezime";
		String email = "email";
		
		studentVO.setFirstName(firstName);
		studentVO.setLastName(lastName);
		studentVO.setEmailAddress(email);

		studentService.saveStudent(studentVO);
		
		// fetch again
		StudentVO studentVO2 = studentService.findById(1);
		
		// assert
		assertEquals(studentVO.getId(), studentVO2.getId());
		assertEquals(firstName, studentVO2.getFirstName(),"studentVO.getFirstName() NOK");
		assertEquals(lastName, studentVO2.getLastName(),"studentVO.getLastName() NOK");
		assertEquals(email, studentVO2.getEmailAddress(),"studentVO.getEmailAddress() NOK");
	}
	
	@Test
	void deleteStudent() {
		log.info("deleteStudent");
		
		// first get student
		StudentVO studentVO = studentService.findById(1);
		
		// assert
		assertNotNull(studentVO, "return true");
		assertEquals(1, studentVO.getId());
		
		// delete
		studentService.deleteStudent(1);
		
		// assert not existing student
		Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
			studentService.findById(1);
		});
				
		String expectedMessage = "Student with id=1 not found.";
		String actualMessage = exception.getMessage();
		
	    assertTrue(actualMessage.contains(expectedMessage));

		// delete not existing student
		exception = assertThrows(EmptyResultDataAccessException.class, () -> {
			studentService.deleteStudent(2);
		});
	}
	
	@Test
	void getAllStudents() {
		log.info("getAllStudents");
		
		Iterable<StudentVO> students = studentService.getAllStudents();
		
		// assert
		if (students instanceof Collection<?>) {
			assertEquals(1, ((Collection<?>) students).size(), "students size not 1");
		}
		
		// create student
		// set id 0: this is to force a save of new item ... instead of update
		StudentVO studentVO = new StudentVO("firstName", "lastName", "emailAddress");
		studentVO.setId(0);
		
		studentService.saveStudent(studentVO);
		
		students = studentService.getAllStudents();
		
		// assert
		if (students instanceof Collection<?>) {
			assertEquals(2, ((Collection<?>) students).size(), "students size not 2 after insert");
		}
		
		// delete student
		studentService.deleteStudent(1);
		
		students = studentService.getAllStudents();
		log.info("after delete " + students.toString());
		
		// assert
		if (students instanceof Collection<?>) {
			assertEquals(1, ((Collection<?>) students).size(), "students size not 1 after delete");
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
