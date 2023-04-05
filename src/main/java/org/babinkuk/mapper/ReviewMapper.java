package org.babinkuk.mapper;

import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.babinkuk.entity.Review;
import org.babinkuk.vo.ReviewVO;
import org.mapstruct.BeanMapping;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * mapper for the entity @link {@link Review} and its DTO {@link ReviewVO}
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
public interface ReviewMapper {
	
	public ReviewMapper reviewMapperInstance = Mappers.getMapper(ReviewMapper.class);
	
	@Named("toEntity")
	Review toEntity(ReviewVO reviewVO);
	
	@Named("toVO")
	ReviewVO toVO(Review review);
	
	@IterableMapping(qualifiedByName = "toVO")
	@BeanMapping(ignoreByDefault = true)
	Iterable<ReviewVO> toVO(Iterable<Review> reviewList);
	
}