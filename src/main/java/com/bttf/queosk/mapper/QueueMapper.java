package com.bttf.queosk.mapper;

import com.bttf.queosk.dto.queuedto.QueueDto;
import com.bttf.queosk.dto.queuedto.QueueForm;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface QueueMapper {
    QueueMapper INSTANCE = Mappers.getMapper(QueueMapper.class);

    QueueForm.Response QueueDtoToQueueFormResponse(QueueDto queueDto);
}
