package es.udc.ws.app.client.service.thrift;


import es.udc.ws.app.client.service.dto.ClientMatchDto;
import es.udc.ws.match.thrift.ThriftMatchDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class ClientMatchDtoToThriftMatchDtoConversor {

    public static ThriftMatchDto toThriftMatchDto(
            ClientMatchDto clientMatchDto) {

        Long matchId = clientMatchDto.getMatchID();

        return new ThriftMatchDto(
                matchId == null ? -1 : matchId.longValue(),
                clientMatchDto.getVisitorName(),
                clientMatchDto.getMatchDate().toString(),
                (short) (clientMatchDto.getTicketPrice()),
                (short) (clientMatchDto.getMaxTicketCount()),
                (short) (clientMatchDto.getTicketsSold()));


    }

    public static List<ClientMatchDto> toClientMatchDtos(List<ThriftMatchDto> matchs) {

        List<ClientMatchDto> clientMatchDtos = new ArrayList<>(matchs.size());

        for (ThriftMatchDto match : matchs) {
            clientMatchDtos.add(toClientMatchDto(match));
        }
        return clientMatchDtos;

    }

    private static ClientMatchDto toClientMatchDto(ThriftMatchDto match) {


        return new ClientMatchDto(
                match.getMaxTickets(),
                match.getVisitorName(),
                LocalDateTime.parse(match.getMatchDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                match.getTicketPrice(),
                match.getMatchId(),
                match.getTicketsSold());


    }
}
