package es.udc.ws.app.client.service.rest.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.app.client.service.dto.ClientMatchDto;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JsonToClientMatchDtoConversor {
	public static ObjectNode toObjectNode(ClientMatchDto match) {

		ObjectNode matchObject = JsonNodeFactory.instance.objectNode();

		matchObject.put("maxTicketCount", match.getMaxTicketCount())
				.put("visitorName", match.getVisitorName())
				.put("matchDate", match.getMatchDate().toString())
				.put("ticketPrice", match.getTicketPrice())
				.put("matchID", match.getMatchID())
				.put("ticketsSold", match.getTicketsSold());
		return matchObject;
	}


	public static ClientMatchDto toClientMatchDto(InputStream jsonmatch) throws ParsingException {
		try {
			ObjectMapper objectMapper = ObjectMapperFactory.instance();
			JsonNode rootNode = objectMapper.readTree(jsonmatch);

			if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
				throw new ParsingException("Unrecognized JSON (object expected)");
			} else {
				return toClientMatchDto(rootNode);
			}
		} catch (ParsingException ex) {
			throw ex;
		} catch (Exception e) {
			throw new ParsingException(e);
		}
	}

	public static List<ClientMatchDto> toClientmatchDtos(InputStream jsonmatchs) throws ParsingException {
		try {

			ObjectMapper objectMapper = ObjectMapperFactory.instance();
			JsonNode rootNode = objectMapper.readTree(jsonmatchs);
			if (rootNode.getNodeType() != JsonNodeType.ARRAY) {
				throw new ParsingException("Unrecognized JSON (array expected)");
			} else {
				ArrayNode matchsArray = (ArrayNode) rootNode;
				List<ClientMatchDto> matchDtos = new ArrayList<>(matchsArray.size());
				for (JsonNode matchNode : matchsArray) {
					matchDtos.add(toClientMatchDto(matchNode));
				}

				return matchDtos;
			}
		} catch (ParsingException ex) {
			throw ex;
		} catch (Exception e) {
			throw new ParsingException(e);
		}
	}

	private static ClientMatchDto toClientMatchDto(JsonNode matchNode) throws ParsingException {
		if (matchNode.getNodeType() != JsonNodeType.OBJECT) {
			throw new ParsingException("Unrecognized JSON (object expected)");
		} else {
			ObjectNode matchObject = (ObjectNode) matchNode;

			JsonNode matchIdNode = matchObject.get("matchID");
			Long matchId = (matchIdNode != null) ? matchIdNode.longValue() : null;

			int maxTicketCount = matchObject.get("maxTicketCount").intValue();
			String visitorName = matchObject.get("visitorName").textValue().trim();
			LocalDateTime matchDate = LocalDateTime.parse(matchObject.get("matchDate").textValue().trim());
			float ticketPrice = matchObject.get("ticketPrice").floatValue();
			int ticketsSold = matchObject.get("ticketsSold").intValue();

			return new ClientMatchDto(maxTicketCount, visitorName, matchDate, ticketPrice, matchId, ticketsSold);


		}
	}

}
