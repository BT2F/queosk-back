package com.bttf.queosk.mapper.settlementmapper;

import com.bttf.queosk.dto.settlementdto.SettlementDto;
import com.bttf.queosk.dto.settlementdto.SettlementResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SettlementMapper {
    SettlementMapper INSTANCE = Mappers.getMapper(SettlementMapper.class);

    SettlementResponse settlementDtoToSettlementResponse(SettlementDto settlementDto);
}
