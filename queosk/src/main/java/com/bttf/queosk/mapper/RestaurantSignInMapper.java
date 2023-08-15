package com.bttf.queosk.mapper;

import com.bttf.queosk.dto.RestaurantSignInForm;
import com.bttf.queosk.entity.RestaurantEntity;
import com.bttf.queosk.util.KakaoGeoAddress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy =
        ReportingPolicy.IGNORE)
public interface RestaurantSignInMapper extends EntityMapper<RestaurantSignInForm, RestaurantEntity> {

    RestaurantSignInMapper MAPPER =
            Mappers.getMapper(RestaurantSignInMapper.class);


    @Override
    @Mapping(source = "dto.businessStartDate", target = "businessStartDate",
            dateFormat = "yyyyMMdd")
    @Mapping(source = "dto.address", target = "longitude",
            qualifiedByName = "getX")
    @Mapping(source = "dto.address", target = "latitude",
            qualifiedByName = "getY")
    RestaurantEntity toEntity(final RestaurantSignInForm dto);


    @Named("getX")
    default double getX(String address) {
        return KakaoGeoAddress.addressToCoordinate(address, "x");
    }

    @Named("getY")
    default double getY(String address) {
        return KakaoGeoAddress.addressToCoordinate(address, "y");
    }
}
