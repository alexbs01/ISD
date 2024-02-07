package es.udc.ws.app.restservice.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.app.restservice.dto.RestMatchDto;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

public class JsonToRestMatchDtoConversor {

    public static ObjectNode toObjectNode(RestMatchDto match) {

        ObjectNode matchObject = JsonNodeFactory.instance.objectNode();

        matchObject.put("maxTicketCount", match.getMaxTicketCount())
                .put("visitorName", match.getVisitorName())
                .put("matchDate", match.getMatchDate().toString())
                .put("ticketPrice", match.getTicketPrice())
                .put("matchID", match.getMatchID())
                .put("ticketsSold", match.getTicketsSold());
        return matchObject;
    }

    public static ArrayNode toArrayNode(List<RestMatchDto> matches) {

        ArrayNode matchNodes = JsonNodeFactory.instance.arrayNode();
        for (RestMatchDto matchDto : matches) {
            ObjectNode matchObject = toObjectNode(matchDto);
            matchNodes.add(matchObject);
        }

        return matchNodes;
    }

    public static RestMatchDto toRestMatchDto(InputStream jsonMatch) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonMatch);

            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                ObjectNode matchObject = (ObjectNode) rootNode;

                long matchId = 0;
                if(matchObject.get("maxTicketCount").intValue() == 0) {
                    matchObject.put("maxTicketCount", -1);
                }
                if(matchObject.get("ticketPrice").intValue() == 0) {
                    matchObject.put("ticketPrice", -1);
                }
                int maxTicketCount = matchObject.get("maxTicketCount").intValue();
                String visitorName = matchObject.get("visitorName").textValue().trim();
                LocalDateTime matchDate = LocalDateTime.parse(matchObject.get("matchDate").textValue().trim());
                float ticketPrice = matchObject.get("ticketPrice").floatValue();
                int ticketsSold = matchObject.get("ticketsSold").intValue();

                return new RestMatchDto(maxTicketCount, visitorName, matchDate, ticketPrice, matchId, ticketsSold);


            }
        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

}
