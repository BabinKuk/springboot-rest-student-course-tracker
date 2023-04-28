package org.babinkuk.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.aspectj.util.IStructureModel;
import org.babinkuk.entity.Instructor;
import org.babinkuk.entity.InstructorDetail;
import org.babinkuk.entity.Student;
//import org.babinkuk.vo.InstructorDetailVO;
import org.babinkuk.vo.InstructorVO;
import org.babinkuk.vo.StudentVO;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.BeforeMapping;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * mapper for the entity @link {@link Instructor} and its DTO {@link InstructorVO}
 * 
 * @author Nikola
 */
@Mapper
(
	componentModel = "spring",
	unmappedSourcePolicy = ReportingPolicy.WARN,
	imports = {StringUtils.class, Objects.class},
	uses = {InstructorDetailMapper.class}
)
public interface InstructorMapper {
	
	public InstructorMapper instructorMapperInstance = Mappers.getMapper(InstructorMapper.class);
	public InstructorDetailMapper instructorDetailMapperInstance = Mappers.getMapper(InstructorDetailMapper.class);
	
//	@BeforeMapping
//	default void beforeMapInstructorDetail(@MappingTarget Instructor entity, InstructorVO instructorVO) {
//		System.out.println("beforeMapInstructorDetail");
//		if (StringUtils.isNotBlank(instructorVO.getYoutubeChannel()) && StringUtils.isNotBlank(instructorVO.getHobby())) {
//			InstructorDetail instructorDetail = instructorDetailMapperInstance.toEntity(instructorVO);
//			instructorDetail.setInstructor(entity);
//			entity.setInstructorDetail(instructorDetail);
//		}
//		System.out.println(instructorDetail.toString());
//		System.out.println(entity.toString());
//	}
	
//	@Named("setDetails")
//	default InstructorDetail setDetails(InstructorVO instructorVO) {
//		System.out.println("default setDetails");
//		// instructor details
//		InstructorDetail instructorDetail = instructorDetailMapperInstance.toEntity(instructorVO);
//		Instructor entity = new Instructor();
//		entity.setId(instructorVO.getId());
//		instructorDetail.setInstructor(entity);
//		System.out.println(instructorDetail.toString());
//		return instructorDetail;
//	}
	
	@AfterMapping
	default void afterMapInstructor(@MappingTarget Instructor entity, InstructorVO instructorVO) {
//		System.out.println("toEntity afterMapInstructor");
		//System.out.println(instructorDetail.toString());
//		System.out.println(instructorVO.toString());

		// instructor details
		InstructorDetail instructorDetail = instructorDetailMapperInstance.toEntity(instructorVO, entity);
		instructorDetail.setInstructor(entity);
		entity.setInstructorDetail(instructorDetail);
//		System.out.println(entity.toString());
	}
	
	// for insert
	@Named("toEntity")
	@Mapping(source = "emailAddress", target = "email")
	//@Mapping(source = "instructorVO", target= "instructorDetail", qualifiedByName = "setDetails")
	Instructor toEntity(InstructorVO instructorVO);
	
	// for update
	@Named("toEntity")
	@Mapping(source = "emailAddress", target = "email")
	Instructor toEntity(InstructorVO instructorVO, @MappingTarget Instructor instructor);
    
	@Named("toVO")
	@Mapping(source = "email", target = "emailAddress")
	InstructorVO toVO(Instructor instructor);
	
	@IterableMapping(qualifiedByName = "toVO")
	@BeanMapping(ignoreByDefault = true)
	Iterable<InstructorVO> toVO(Iterable<Instructor> instructorLst);
	
	@AfterMapping
	default void setDetails(@MappingTarget InstructorVO instructorVO, Instructor entity) {
//		System.out.println("toVO aftermapping setDetails");
		// instructor details
		if (entity.getInstructorDetail() != null) {
			System.out.println(entity.getInstructorDetail());
			instructorVO.setYoutubeChannel(entity.getInstructorDetail().getYoutubeChannel());
			instructorVO.setHobby(entity.getInstructorDetail().getHobby());
		}
	}
	
}