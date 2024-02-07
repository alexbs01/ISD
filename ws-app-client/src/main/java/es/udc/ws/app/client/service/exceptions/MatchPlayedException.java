package es.udc.ws.app.client.service.exceptions;

import java.time.LocalDateTime;

public class MatchPlayedException extends Exception{
    private long matchId;
    private LocalDateTime matchDate;
    public MatchPlayedException(long matchId){
        super("Match with id=\""+matchId+" has ben played");
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
