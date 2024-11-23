package org.example.booking.api.services.mapper;

import org.example.booking.api.dtos.BillGeneral;
import org.example.booking.api.dtos.BillResponse;
import org.example.booking.api.dtos.BillStatistics;
import org.example.booking.api.entities.Bill;
import org.example.booking.api.entities.Ticket;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {BillMapper.class})
public interface BillMapper {
    Ticket toEntity(Ticket ticket);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Ticket partialUpdate(Ticket ticketDto, @MappingTarget Ticket ticket);

    Bill toEntity(BillResponse billResponse);

    @AfterMapping
    default void linkTickets(@MappingTarget Bill bill) {
        bill.getTickets().forEach(ticket -> ticket.setBill(bill));
    }

    @Mapping(target = "roundTrip", source = "roundTrip")
    BillResponse billToBillResponse(Bill bill);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Bill partialUpdate(BillResponse billResponse, @MappingTarget Bill bill);

    BillStatistics toStatistics(Bill bill);

    @Mapping(source = "createDate", target = "createDate")
    BillGeneral toBillGeneral(Bill bill);
}
