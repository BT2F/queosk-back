package com.bttf.queosk.mapper;

import com.bttf.queosk.dto.RestaurantDto;
import com.bttf.queosk.entity.Restaurant;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy =
        ReportingPolicy.IGNORE)
public interface RestaurantDtoMapper extends EntityMapper<RestaurantDto, Restaurant> {
    RestaurantDtoMapper MAPPER = Mappers.getMapper(RestaurantDtoMapper.class);

    @Override
    RestaurantDto toDto(final Restaurant entity);
}
