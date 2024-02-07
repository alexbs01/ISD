package es.udc.ws.app.model.matchService.exceptions;

public class MatchSoldOutException extends Exception{
    private Long matchId;

    public MatchSoldOutException(Long matchId) {
        super("Match with id=\"" + matchId + "\n has no remaining entries");
        this.matchId = matchId;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }
}
