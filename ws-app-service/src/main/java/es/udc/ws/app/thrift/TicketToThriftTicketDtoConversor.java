package es.udc.ws.app.thrift;

import es.udc.ws.app.model.tickets.Ticket;
import es.udc.ws.app.restservice.dto.RestTicketDto;
import es.udc.ws.match.thrift.ThriftTicketDto;

import java.util.ArrayList;
import java.util.List;

public class TicketToThriftTicketDtoConversor {

    public static ThriftTicketDto toThriftTicketDto(Ticket ticket){
        return new ThriftTicketDto(ticket.getTicketId(), ticket.getCreditCard(), ticket.getEmail(), ticket.getNumberEntries(), ticket.getSaleTime().toString(), ticket.isClaimed(), ticket.getMatchId());
    }

    public static List<ThriftTicketDto> toThriftTicketDto(List<Ticket> tickets) {
        List<ThriftTicketDto> ticketDtos = new ArrayList<>(tickets.size());

        for (Ticket ticket : tickets) {
            ticketDtos.add(toThriftTicketDto(ticket));
        }

        return ticketDtos;
    }
}
