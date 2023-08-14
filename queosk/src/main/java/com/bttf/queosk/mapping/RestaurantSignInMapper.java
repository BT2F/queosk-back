package com.bttf.queosk.mapping;

import com.bttf.queosk.domain.RestaurantSignInDto;
import com.bttf.queosk.entity.RestaurantEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy =
        ReportingPolicy.IGNORE)
public interface RestaurantSignInMapper extends EntityMapper<RestaurantSignInDto, RestaurantEntity> {
    RestaurantSignInMapper MAPPER =
            Mappers.getMapper(RestaurantSignInMapper.class);

    @Override
    @Mapping(source = "dto.businessStartDate", target = "businessStartDate",
            qualifiedByName = "stringToDateString")
    RestaurantEntity toEntity(final RestaurantSignInDto dto);

    @Named("stringToDateString")
    static String stringToDateString(String s) {
        return s.substring(0, 3) + "-" + s.charAt(4) + "-" + s.charAt(6);
    }

}
