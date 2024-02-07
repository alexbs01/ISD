package es.udc.ws.app.restservice.json;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.app.restservice.dto.RestTicketDto;

import java.util.List;

public class JsonToRestTicketDtoConversor {

    public static ObjectNode toObjectNode(RestTicketDto ticket) {

        ObjectNode saleNode = JsonNodeFactory.instance.objectNode();

        if (ticket.getTicketId() != null) {
            saleNode.put("ticketId", ticket.getTicketId());
        }
        saleNode.put("numberEntries", ticket.getNumberEntries())
                .put("saleTime", ticket.getSaleTime().toString())
                .put("claimed", ticket.isClaimed())
                .put("matchId", ticket.getMatchId())
                .put("creditCard", ticket.getCreditCard());

        return saleNode;
    }

    public static ArrayNode toArrayNode(List<RestTicketDto> tickets) {
        ArrayNode ticketNodes = JsonNodeFactory.instance.arrayNode();

        for (RestTicketDto ticketDto : tickets) {
            ObjectNode ticketObject = toObjectNode(ticketDto);
            ticketNodes.add(ticketObject);
        }

        return ticketNodes;
    }
}
