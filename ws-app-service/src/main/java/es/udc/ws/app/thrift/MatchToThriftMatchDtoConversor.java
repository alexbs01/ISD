package es.udc.ws.app.thrift;

import es.udc.ws.app.model.match.Match;
import es.udc.ws.match.thrift.ThriftMatchDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MatchToThriftMatchDtoConversor {


    public static Match toMatch(ThriftMatchDto match) {

        LocalDateTime matchDate = LocalDateTime.parse(match.getMatchDate(), DateTimeFormatter.ISO_DATE_TIME);
        return new Match(match.getMatchId(),match.getVisitorName(),matchDate,match.getTicketPrice(),match.getMaxTickets());
    }

    public static List<ThriftMatchDto> toThriftMatchDtos(List<Match> matchs) {

        List<ThriftMatchDto> dtos = new ArrayList<>(matchs.size());

        for (Match match : matchs) {
            dtos.add(toThriftMatchDto(match));
        }
        return dtos;

    }

    public static ThriftMatchDto toThriftMatchDto(Match match) {

        return new ThriftMatchDto(match.getMatchID(),match.getVisitorName(),match.getMatchDate().toString(),(short)match.getTicketPrice(),(short)match.getMaxTicketCount(),(short)match.getTicketsSold());

    }
}
