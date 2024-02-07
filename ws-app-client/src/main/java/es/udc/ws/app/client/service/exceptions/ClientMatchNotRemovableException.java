package es.udc.ws.app.client.service.exceptions;

public class ClientMatchNotRemovableException extends Exception {

    private Long matchId;

    public ClientMatchNotRemovableException(Long matchId) {
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
