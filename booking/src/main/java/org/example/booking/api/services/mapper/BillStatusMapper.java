package org.example.booking.api.services.mapper;

import org.example.booking.api.dtos.BillStatusResponse;
import org.example.booking.api.entities.BillStatus;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {BillMapper.class})
public interface BillStatusMapper {
    BillStatusResponse toResponse(BillStatus billStatus);
}
