package org.babinkuk.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.vo.InstructorVO;
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
public class InstructorServiceTest {
	
	public static final Logger log = LogManager.getLogger(InstructorServiceTest.class);
	
	@Autowired
	private JdbcTemplate jdbc;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private InstructorService instructorService;
	
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
	void getInstructorById() {
		log.info("getInstructorByid");
		
		InstructorVO instructorVO = instructorService.findById(1);
		
		//log.info(instructorVO.toString());
		
		assertNotNull(instructorVO,"instructorVO null");
		assertEquals(1, instructorVO.getId());
		assertNotNull(instructorVO.getFirstName(),"instructorVO.getFirstName() null");
		assertNotNull(instructorVO.getLastName(),"instructorVO.getLastName() null");
		assertNotNull(instructorVO.getEmailAddress(),"instructorVO.getEmailAddress() null");
		assertNotNull(instructorVO.getYoutubeChannel(),"instructorVO.getYoutubeChannel() null");
		assertNotNull(instructorVO.getHobby(),"instructorVO.getHobby() null");
		assertEquals("firstNameInstr", instructorVO.getFirstName(),"instructorVO.getFirstName() NOK");
		assertEquals("lastNameInstr", instructorVO.getLastName(),"instructorVO.getLastName() NOK");
		assertEquals("firstNameInstr@babinuk.com", instructorVO.getEmailAddress(),"instructorVO.getEmailAddress() NOK");
		assertEquals("ytb test", instructorVO.getYoutubeChannel(),"instructorVO.getYoutubeChannel() NOK");
		assertEquals("test hobby", instructorVO.getHobby(),"instructorVO.getHobby() NOK");
		// not neccessary
		assertNotEquals("test hobb", instructorVO.getHobby(),"instructorVO.getHobby() NOK");
		
		// assert not existing instructor
		Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
			instructorService.findById(2);
		});
		
		String expectedMessage = "Instructor with id=2 not found.";
		String actualMessage = exception.getMessage();

	    assertTrue(actualMessage.contains(expectedMessage));
	}
	
	@Test
	void getInstructorByEmail() {
		log.info("getInstructorByEmail");
		
		InstructorVO instructorVO = instructorService.findByEmail("firstNameInstr@babinuk.com");
		
		log.info(instructorVO.toString());
		
		assertNotNull(instructorVO,"instructorVO null");
		assertEquals(1, instructorVO.getId());
		assertNotNull(instructorVO.getFirstName(),"instructorVO.getFirstName() null");
		assertNotNull(instructorVO.getLastName(),"instructorVO.getLastName() null");
		assertNotNull(instructorVO.getEmailAddress(),"instructorVO.getEmailAddress() null");
		assertNotNull(instructorVO.getYoutubeChannel(),"instructorVO.getYoutubeChannel() null");
		assertNotNull(instructorVO.getHobby(),"instructorVO.getHobby() null");
		assertEquals("firstNameInstr", instructorVO.getFirstName(),"instructorVO.getFirstName() NOK");
		assertEquals("lastNameInstr", instructorVO.getLastName(),"instructorVO.getLastName() NOK");
		assertEquals("firstNameInstr@babinuk.com", instructorVO.getEmailAddress(),"instructorVO.getEmailAddress() NOK");
		assertEquals("ytb test", instructorVO.getYoutubeChannel(),"instructorVO.getYoutubeChannel() NOK");
		assertEquals("test hobby", instructorVO.getHobby(),"instructorVO.getHobby() NOK");
		// not neccessary
		assertNotEquals("test hobb", instructorVO.getHobby(),"instructorVO.getHobby() NOK");
		
		// assert not existing instructor
		assertNull(instructorService.findByEmail("email"),"not existing instructor not null");
	}
	
	@Test
	void addInstructor() {
		log.info("addInstructor");
		
		// first create instructor
		// set id 0: this is to force a save of new item ... instead of update
		InstructorVO instructorVO = new InstructorVO("firstName", "lastName", "emailAddress", "youtubeChannel", "hobby");
		instructorVO.setId(0);
		
		instructorService.saveInstructor(instructorVO);
		
		InstructorVO instructorVO2 = instructorService.findByEmail("emailAddress");
		
		log.info(instructorVO2);

		// assert
		//assertEquals(2, instructorVO2.getId());
		assertEquals(instructorVO.getFirstName(), instructorVO2.getFirstName(),"instructorVO.getFirstName() NOK");
		assertEquals(instructorVO.getLastName(), instructorVO2.getLastName(),"instructorVO.getLastName() NOK");
		assertEquals(instructorVO.getEmailAddress(), instructorVO2.getEmailAddress(),"instructorVO.getEmailAddress() NOK");
		assertEquals(instructorVO.getYoutubeChannel(), instructorVO2.getYoutubeChannel(),"instructorVO.getYoutubeChannel() NOK");
		assertEquals(instructorVO.getHobby(), instructorVO2.getHobby(),"instructorVO.getHobby() NOK");
	}	
	
	@Test
	void updateInstructor() {
		log.info("updateInstructor");
		
		InstructorVO instructorVO = instructorService.findById(1);
				
		// update with new data
		String firstName = "ime";
		String lastName = "prezime";
		String email = "email";
		String hobby = "hobi";
		String ytb = "jutub";
		
		instructorVO.setFirstName(firstName);
		instructorVO.setLastName(lastName);
		instructorVO.setEmailAddress(email);
		instructorVO.setYoutubeChannel(ytb);
		instructorVO.setHobby(hobby);
		
		instructorService.saveInstructor(instructorVO);
		
		// fetch again
		InstructorVO instructorVO2 = instructorService.findById(1);
		
		// assert
		assertEquals(instructorVO.getId(), instructorVO2.getId());
		assertEquals(firstName, instructorVO2.getFirstName(),"instructorVO.getFirstName() NOK");
		assertEquals(lastName, instructorVO2.getLastName(),"instructorVO.getLastName() NOK");
		assertEquals(email, instructorVO2.getEmailAddress(),"instructorVO.getEmailAddress() NOK");
		assertEquals(ytb, instructorVO2.getYoutubeChannel(),"instructorVO.getYoutubeChannel() NOK");
		assertEquals(hobby, instructorVO2.getHobby(),"instructorVO.getHobby() NOK");
	}
	
	@Test
	void deleteInstructor() {
		log.info("deleteInstructor");
		
		// first get instructor
		InstructorVO instructorVO = instructorService.findById(1);
		
		// assert
		assertNotNull(instructorVO, "return true");
		assertEquals(1, instructorVO.getId());
		
		// delete
		instructorService.deleteInstructor(1);
		
		// assert not existing instructor
		Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
			instructorService.findById(1);
		});
				
		String expectedMessage = "Instructor with id=1 not found.";
		String actualMessage = exception.getMessage();
		
	    assertTrue(actualMessage.contains(expectedMessage));

		// delete not existing instructor
		exception = assertThrows(EmptyResultDataAccessException.class, () -> {
			instructorService.deleteInstructor(2);
		});
	}
	
	@Test
	void getAllInstructors() {
		log.info("getAllInstructors");
		
		Iterable<InstructorVO> instructors = instructorService.getAllInstructors();
		
		// assert
		if (instructors instanceof Collection<?>) {
			assertEquals(1, ((Collection<?>) instructors).size(), "instructors size not 1");
		}
		
		// create instructor
		// set id 0: this is to force a save of new item ... instead of update
		InstructorVO instructorVO = new InstructorVO("firstName", "lastName", "emailAddress", "youtubeChannel", "hobby");
		instructorVO.setId(0);
		
		instructorService.saveInstructor(instructorVO);
		
		instructors = instructorService.getAllInstructors();
		
		// assert
		if (instructors instanceof Collection<?>) {
			assertEquals(2, ((Collection<?>) instructors).size(), "instructors size not 2 after insert");
		}
		
		// delete instructor
		instructorService.deleteInstructor(2);
		
		instructors = instructorService.getAllInstructors();
		log.info("after delete " + instructors.toString());
		
		// assert
		if (instructors instanceof Collection<?>) {
			assertEquals(1, ((Collection<?>) instructors).size(), "instructors size not 1 after delete");
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
