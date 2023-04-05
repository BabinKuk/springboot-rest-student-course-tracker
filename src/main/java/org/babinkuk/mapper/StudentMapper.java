package org.babinkuk.mapper;

import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.babinkuk.entity.Student;
import org.babinkuk.vo.StudentVO;
import org.mapstruct.BeanMapping;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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
	
	@Named("toEntity")
	@Mapping(source = "emailAddress", target = "email")
	Student toEntity(StudentVO studentVO);
	
	@Named("toVO")
	@Mapping(source = "email", target = "emailAddress")
	StudentVO toVO(Student student);
	
	@IterableMapping(qualifiedByName = "toVO")
	@BeanMapping(ignoreByDefault = true)
	Iterable<StudentVO> toVO(Iterable<Student> studentList);
	
}