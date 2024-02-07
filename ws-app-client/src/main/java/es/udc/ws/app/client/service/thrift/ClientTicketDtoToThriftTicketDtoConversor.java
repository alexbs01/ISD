package es.udc.ws.app.client.service.thrift;


import es.udc.ws.app.client.service.dto.ClientTicketDto;
import es.udc.ws.match.thrift.ThriftTicketDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ClientTicketDtoToThriftTicketDtoConversor {

    public static ThriftTicketDto toThriftTicketDto(ClientTicketDto clientTicketDto) {

        long ticketId = clientTicketDto.getTicketId();

        return new ThriftTicketDto(
                ticketId,
                clientTicketDto.getCreditCard(),
                clientTicketDto.getEmail(),
                clientTicketDto.getNumberEntries(),
                clientTicketDto.getSaleTime().toString(),
                clientTicketDto.isClaimed(),
                clientTicketDto.getMatchId()
        );


    }

    public static List<ClientTicketDto> toClientTicketDto(List<ThriftTicketDto> ticketDtos) {

        List<ClientTicketDto> ticketDtoList = new ArrayList<>(ticketDtos.size());

        for (ThriftTicketDto ticket : ticketDtos) {
            ticketDtoList.add(toClientTicketDto(ticket));
        }
        return ticketDtoList;

    }

    public static ClientTicketDto toClientTicketDto(ThriftTicketDto ticketDto) {


        return new ClientTicketDto(
                ticketDto.ticketId,
                ticketDto.creditCard,
                ticketDto.mail,
                ticketDto.numberEntries,
                LocalDateTime.parse(ticketDto.saleTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                ticketDto.claim,
                ticketDto.matchId);
    }
}
