package org.babinkuk.mapper;

import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.babinkuk.entity.Instructor;
import org.babinkuk.entity.InstructorDetail;
import org.babinkuk.vo.InstructorVO;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * mapper for the entity @link {@link InstructorDetail} and its DTO {@link InstructorDetailVO}
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
public interface InstructorDetailMapper {
	
	public InstructorDetailMapper instructorDetailMapperInstance = Mappers.getMapper(InstructorDetailMapper.class);
	
	@Named("toEntity")
	@Mapping(source = "instructor.instructorDetail.id", target = "id")
	@Mapping(source = "instructorVO.youtubeChannel", target = "youtubeChannel")
	@Mapping(source = "instructorVO.hobby", target = "hobby")
	InstructorDetail toEntity(InstructorVO instructorVO, Instructor instructor);
	
	@Named("toEntity")
	@Mapping(source = "instructorVO.id", target = "id")
	@Mapping(source = "instructorVO.youtubeChannel", target = "youtubeChannel")
	@Mapping(source = "instructorVO.hobby", target = "hobby")
	InstructorDetail toEntity(InstructorVO instructorVO);
	
//	@Named("toVO")
//	@Mapping(source = "id", target = "instructor.instructorDetail.id")
//	@Mapping(surce = "youtubeChannel", target = "instructorVO.youtubeChannel")
//	@Mapping(source = "hobby", target = "instructorVO.hobby")
//	InstructorVO toVO(InstructorDetail entity);
	
}