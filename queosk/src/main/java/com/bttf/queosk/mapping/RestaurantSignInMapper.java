package com.bttf.queosk.mapping;

import com.bttf.queosk.domain.RestaurantSignInForm;
import com.bttf.queosk.entity.RestaurantEntity;
import com.bttf.queosk.util.KakaoGeoAddress;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

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
