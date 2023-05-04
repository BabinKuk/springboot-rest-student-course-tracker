package org.babinkuk.mapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.babinkuk.entity.Course;
import org.babinkuk.entity.Instructor;
import org.babinkuk.entity.InstructorDetail;
import org.babinkuk.entity.Review;
import org.babinkuk.entity.Student;
import org.babinkuk.vo.CourseVO;
import org.babinkuk.vo.InstructorVO;
import org.babinkuk.vo.ReviewVO;
import org.babinkuk.vo.StudentVO;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.BeforeMapping;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.util.CollectionUtils;

/**
 * mapper for the entity @link {@link Course} and its DTO {@link CourseVO}
 * 
 * @author Nikola
 */
@Mapper
(
	componentModel = "spring",
	unmappedSourcePolicy = ReportingPolicy.WARN,
	imports = {StringUtils.class, Objects.class},
	//if needed add uses = {add different classes for complex objects}
	uses = {ReviewMapper.class, InstructorMapper.class, StudentMapper.class} 
)
public interface CourseMapper {
	
	public CourseMapper courseMapperInstance = Mappers.getMapper(CourseMapper.class);
	public ReviewMapper reviewMapperInstance = Mappers.getMapper(ReviewMapper.class);
	public StudentMapper studentMapperInstance = Mappers.getMapper(StudentMapper.class);
	public InstructorMapper instructorMapperInstance = Mappers.getMapper(InstructorMapper.class);
	
	@BeforeMapping
	default void beforeMap(@MappingTarget Course entity, CourseVO courseVO) {
//		System.out.println("beforeMap(@MappingTarget Course entity, CourseVO courseVO)");
//		System.out.println(entity.toString());
//		System.out.println(courseVO.toString());
//		System.out.println("\n");
		System.out.println("beforeMapInstructor");
		// instructor
		if (courseVO.getInstructorVO() != null) {
//			System.out.println("set Instructor");
			Instructor instructor = new Instructor();
			instructor.setId(courseVO.getInstructorVO().getId());	
			entity.setInstructor(instructor);
			instructor = instructorMapperInstance.toEntity(courseVO.getInstructorVO(), entity.getInstructor());
		}
//		System.out.println(entity.toString());
		
		System.out.println("beforeMapStudents");
		// students
		if (!CollectionUtils.isEmpty(courseVO.getStudentsVO())) {
			Set<Student> students = new HashSet<Student>();
			for (StudentVO studentVO : courseVO.getStudentsVO()) {
				Student student = new Student();
				student.setId(studentVO.getId());
				student.addCourse(entity);
				students.add(student);
			}
			entity.setStudents(students);
//			System.out.println(entity.getStudents());
//			System.out.println(entity.toString());
		}
		
		System.out.println("beforeMapReviews");
		// reviews
		if (!CollectionUtils.isEmpty(courseVO.getReviewsVO())) {
			List<Review> reviewList = new ArrayList<Review>();
			for (ReviewVO reviewVO : courseVO.getReviewsVO()) {
				Review review = reviewMapperInstance.toEntity(reviewVO);
				reviewList.add(review);
			}
			entity.setReviews(reviewList);
//			System.out.println(entity.toString());
		}
		System.out.println(entity.toString());
	}
	
	@AfterMapping
	default void beforeMap(@MappingTarget CourseVO courseVO, Course entity) {
		System.out.println("@AfterMapping");
//		System.out.println("mapInstructor");
		// instructor
		if (entity.getInstructor() != null) {
//			System.out.println("set Instructor");
			InstructorVO instructorVO = instructorMapperInstance.toVO(entity.getInstructor());
//			instructorDetail.setInstructor(entity);	
			courseVO.setInstructorVO(instructorVO);
//			System.out.println(courseVO.toString());
		}
		
		System.out.println("mapStudents");
		// students
		if (!CollectionUtils.isEmpty(entity.getStudents())) {
//			System.out.println(entity.getStudents());
			Set<StudentVO> students = new HashSet<StudentVO>();
			for (Student student : entity.getStudents()) {
//				System.out.println(student.toString());
				StudentVO studentVO = studentMapperInstance.toVO(student);
				students.add(studentVO);
			}
//			System.out.println(students);
			courseVO.setStudentsVO(students);
		}
		
//		System.out.println("beforeMapReviews");
//		// reviews
//		if (!CollectionUtils.isEmpty(entity.getReviews())) {
//			List<Review> reviewList = new ArrayList<Review>();
//			for (ReviewVO reviewVO : courseVO.getReviewsVO()) {
//				Review review = reviewMapperInstance.toEntity(reviewVO);
//				reviewList.add(review);
//			}
//			entity.setReviews(reviewList);
//		}
	}
	
	// for insert
	@Named("toEntity")
	@Mapping(source = "instructorVO", target = "instructor")
	@Mapping(source = "reviewsVO", target = "reviews")
	@Mapping(source = "studentsVO", target = "students")
	Course toEntity(CourseVO courseVO);
	
	// for update
	@Named("toEntity")
	@Mapping(source = "instructorVO", target = "instructor")
	@Mapping(source = "reviewsVO", target = "reviews")
	@Mapping(source = "studentsVO", target = "students")
	Course toEntity(CourseVO courseVO, @MappingTarget Course course);
	
	@Named("toVO")
	@Mapping(source = "instructor", target = "instructorVO")
	@Mapping(source = "reviews", target = "reviewsVO")
	@Mapping(source = "students", target = "studentsVO")
	CourseVO toVO(Course course);
	
	@IterableMapping(qualifiedByName = "toVO")
	@BeanMapping(ignoreByDefault = true)
	Iterable<CourseVO> toVO(Iterable<Course> courseList);
	
}