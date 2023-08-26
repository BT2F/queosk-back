package com.bttf.queosk.mapper.queuemapper;

import com.bttf.queosk.dto.queuedto.QueueDto;
import com.bttf.queosk.dto.queuedto.QueueForm;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface QueueMapper {
    QueueMapper INSTANCE = Mappers.getMapper(QueueMapper.class);

    QueueDto QueueFormRequestToQueueDto(QueueForm.Request queueFormRequest);

    QueueForm.Response QueueDtoToQueueFormResponse(QueueDto queueDto);
}
