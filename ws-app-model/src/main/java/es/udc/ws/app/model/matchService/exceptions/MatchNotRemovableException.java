package es.udc.ws.app.model.matchService.exceptions;

public class MatchNotRemovableException extends Exception {

    private Long matchId;

    public MatchNotRemovableException(Long matchId) {
        super("Match with id=\"" + matchId + "\n cannot be deleted because it has sales");
        this.matchId = matchId;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }
}
