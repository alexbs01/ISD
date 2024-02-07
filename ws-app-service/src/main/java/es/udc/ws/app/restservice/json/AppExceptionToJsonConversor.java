package es.udc.ws.app.restservice.json;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.app.model.matchService.exceptions.*;

public class AppExceptionToJsonConversor {

    public static ObjectNode tomatchNotRemovableException(MatchNotRemovableException ex) {

        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

        exceptionObject.put("errorType", "MatchNotRemovableException");
        exceptionObject.put("matchId", (ex.getMatchId() != null) ? ex.getMatchId() : null);

        return exceptionObject;
    }
    public static ObjectNode toMatchSoldOutException(MatchSoldOutException ex) {

        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

        exceptionObject.put("errorType", "MatchSoldOutException");
        exceptionObject.put("matchId", (ex.getMatchId() != null) ? ex.getMatchId() : null);

        return exceptionObject;
    }
    public static ObjectNode toClaimingException(ClaimingException ex) {

        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

        exceptionObject.put("errorType", "ClaimingException");
        exceptionObject.put("ticketId", (ex.getTicketId() != null) ? ex.getTicketId() : null);

        return exceptionObject;
    }
    public static ObjectNode toMatchPlayedException(MatchPlayedException ex) {

        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

        exceptionObject.put("errorType", "MatchPlayedException");
        exceptionObject.put("matchId", (ex.getMatchId() != null) ? ex.getMatchId() : null);
        if (ex.getMatchDate() != null) {
            exceptionObject.put("matchDate", ex.getMatchDate().toString());
        } else {
            exceptionObject.set("matchDate", null);
        }

        return exceptionObject;
    }
}
