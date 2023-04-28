package org.babinkuk.mapper;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.babinkuk.entity.Course;
import org.babinkuk.entity.Instructor;
import org.babinkuk.entity.Student;
import org.babinkuk.vo.CourseVO;
import org.babinkuk.vo.InstructorVO;
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
 * mapper for the entity @link {@link Employee} and its DTO {@link EmployeeVO}
 * 
 * @author Nikola
 */
@Mapper
(
	componentModel = "spring",
	unmappedSourcePolicy = ReportingPolicy.WARN,
	imports = {StringUtils.class, Objects.class}
	//if needed add uses = {add different classes for complex objects} 
)
public interface StudentMapper {
	
	public StudentMapper studentMapperInstance = Mappers.getMapper(StudentMapper.class);
	
	@AfterMapping
	default void afterMapStudent(@MappingTarget Student entity, StudentVO studentVO) {
		System.out.println("afterMapStudent");
		System.out.println(studentVO.toString());
		if (!StringUtils.isBlank(studentVO.getEmailAddress())) {
			entity.setEmail(studentVO.getEmailAddress());
		}
		System.out.println(entity.toString());
	}
	
	// for insert
	@Named("toEntity")
	@Mapping(source = "emailAddress", target = "email")
	Student toEntity(StudentVO studentVO);
	
	// for update
	@Named("toEntity")
	@Mapping(source = "emailAddress", target = "email")
	Student toEntity(StudentVO studentVO, @MappingTarget Student entity);
	
	@Named("toVO")
	@Mapping(source = "email", target = "emailAddress")
	StudentVO toVO(Student student);
	
	@Named("toVODetails")
	@Mapping(source = "email", target = "emailAddress")
	@Mapping(source = "student", target= "coursesVO", qualifiedByName = "setCourses")
	StudentVO toVODetails(Student student);
	
//	@AfterMapping
//	default void afterMapStudent(@MappingTarget StudentVO studentVO, Student entity) {
//		System.out.println("toVO afterMapStudent");
//		System.out.println(studentVO.toString());
//		System.out.println(entity.toString());
//	}
	
	@IterableMapping(qualifiedByName = "toEntity")
	@BeanMapping(ignoreByDefault = true)
	Iterable<Student> toEntity(Iterable<StudentVO> studentList);
	
	@IterableMapping(qualifiedByName = "toVO")
	@BeanMapping(ignoreByDefault = true)
	Iterable<StudentVO> toVO(Iterable<Student> studentList);
	
	@Named("setCourses")
	default Set<CourseVO> setCourses(Student entity) {
		System.out.println("default setCourses");
		Set<CourseVO> coursesVO = new HashSet<CourseVO>();
		// courses
		if (!CollectionUtils.isEmpty(entity.getCourses())) {
			for (Course course : entity.getCourses()) {
				CourseVO courseVO = new CourseVO();
				courseVO.setId(course.getId());
				courseVO.setTitle(course.getTitle());
				coursesVO.add(courseVO);
			}
//			System.out.println(coursesVO);
		}
		return coursesVO;
	}

}