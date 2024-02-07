package es.udc.ws.app.model.matchService.exceptions;

import java.time.LocalDateTime;

public class MatchPlayedException extends Exception{
    private long matchId;
    private LocalDateTime matchDate;
    public MatchPlayedException(long matchId, LocalDateTime matchDate){
        super("Match with id=\""+matchId+" has been played on (matchDate = \"" +
                matchDate + "\")");
        this.matchId=matchId;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long ticketId) {
        this.matchId = ticketId;
    }

    public LocalDateTime getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(LocalDateTime matchDate) {
        this.matchDate = matchDate;
    }
}
