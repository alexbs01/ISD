package es.udc.ws.app.restservice.dto;
import es.udc.ws.app.model.match.Match;

import java.util.ArrayList;
import java.util.List;

public class MatchToRestMatchDtoConversor {

    public static List<RestMatchDto> toRestMatchDtos(List<Match> matches) {
        List<RestMatchDto> matchDtos = new ArrayList<>(matches.size());
        for (int i = 0; i < matches.size(); i++) {
            Match match = matches.get(i);
            matchDtos.add(toRestMatchDto(match));
        }
        return matchDtos;
    }


    public static RestMatchDto toRestMatchDto(Match match) {
        return new RestMatchDto(match.getMaxTicketCount(), match.getVisitorName(), match.getMatchDate(),
                match.getTicketPrice(), match.getMatchID(), match.getTicketsSold());
    }

    public static Match toMatch(RestMatchDto match) {
        return new Match(match.getMatchID(), match.getVisitorName(), match.getMatchDate(), match.getTicketPrice(),
                match.getMaxTicketCount());
    }

}
