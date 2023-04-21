package org.babinkuk.mapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

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
	
//	@BeforeMapping
//	default void beforeMapReviews(@MappingTarget Course entity, CourseVO courseVO) {
//		System.out.println("beforeMapReviews");
//		// reviews
//		if (!CollectionUtils.isEmpty(courseVO.getReviewsVO())) {
//			List<Review> list = new ArrayList<Review>();
//			for (ReviewVO reviewVO : courseVO.getReviewsVO()) {
//				Review review = reviewMapperInstance.toEntity(reviewVO);
//				list.add(review);
//			}
//			entity.setReviews(list);
//		}
//	}
//	
//	@BeforeMapping
//	default void beforeMapStudents(@MappingTarget Course entity, CourseVO courseVO) {
//		System.out.println("beforeMapStudents");
//		// students
//		if (!CollectionUtils.isEmpty(courseVO.getStudentsVO())) {
//			List<Student> list = new ArrayList<Student>();
//			for (StudentVO studentVO : courseVO.getStudentsVO()) {
//				Student student = studentMapperInstance.toEntity(studentVO);
//				list.add(student);
//			}
//			entity.setStudents(list);
//		}
//	}
	
	@BeforeMapping
	default void beforeMap(@MappingTarget Course entity, CourseVO courseVO) {
		System.out.println("beforeMapInstructor");
		// instructor
		if (courseVO.getInstructorVO() != null) {
			System.out.println("set Instructor");
			Instructor instructor = instructorMapperInstance.toEntity(courseVO.getInstructorVO());
//			instructorDetail.setInstructor(entity);	
			entity.setInstructor(instructor);
			System.out.println(entity.toString());
		}
		
		System.out.println("beforeMapStudents");
//		// students
//		if (!CollectionUtils.isEmpty(courseVO.getStudentsVO())) {
//			List<Student> list = new ArrayList<Student>();
//			for (StudentVO studentVO : courseVO.getStudentsVO()) {
//				Student student = studentMapperInstance.toEntity(studentVO);
//				list.add(student);
//			}
//			entity.setStudents(list);
//		}
		
		System.out.println("beforeMapReviews");
		// reviews
		if (!CollectionUtils.isEmpty(courseVO.getReviewsVO())) {
			List<Review> reviewList = new ArrayList<Review>();
			for (ReviewVO reviewVO : courseVO.getReviewsVO()) {
				Review review = reviewMapperInstance.toEntity(reviewVO);
				reviewList.add(review);
			}
			entity.setReviews(reviewList);
		}
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