package es.udc.ws.app.restservice.dto;

import es.udc.ws.app.model.tickets.Ticket;

import java.util.ArrayList;
import java.util.List;

public class TicketToRestTicketDtoConversor {

    public static RestTicketDto toRestTicketDto(Ticket ticket) {
        return new RestTicketDto(
                ticket.getTicketId(),
                ticket.getNumberEntries(),
                ticket.getSaleTime(), ticket.isClaimed(),
                ticket.getMatchId(),
                ticket.getCreditCard().substring(ticket.getCreditCard().length()-4));
    }

    public static List<RestTicketDto> toRestTicketDto(List<Ticket> tickets) {
        List<RestTicketDto> ticketDtos = new ArrayList<>(tickets.size());

        for (Ticket ticket : tickets) {
            ticketDtos.add(toRestTicketDto(ticket));
        }

        return ticketDtos;
    }

}
