package org.babinkuk.mapper;

import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.babinkuk.entity.Instructor;
import org.babinkuk.entity.Student;
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
	
	@BeforeMapping
	default void beforeMapStudent(@MappingTarget Student entity, StudentVO studentVO) {
		System.out.println("beforeMapStudent");
		System.out.println(studentVO.toString());
		System.out.println(entity.toString());
	}
	
	@AfterMapping
	default void afterMapStudent(@MappingTarget Student entity, StudentVO studentVO) {
		System.out.println("afterMapStudent");
		System.out.println(studentVO.toString());
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
	
	@AfterMapping
	default void afterMapStudent(@MappingTarget StudentVO studentVO, Student entity) {
		System.out.println("toVO afterMapStudent");
		System.out.println(studentVO.toString());
		System.out.println(entity.toString());
	}
	
	@IterableMapping(qualifiedByName = "toVO")
	@BeanMapping(ignoreByDefault = true)
	Iterable<StudentVO> toVO(Iterable<Student> studentList);
	
}