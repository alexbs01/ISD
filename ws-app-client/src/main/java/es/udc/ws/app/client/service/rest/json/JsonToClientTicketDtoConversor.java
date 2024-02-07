package es.udc.ws.app.client.service.rest.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.app.client.service.dto.ClientTicketDto;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JsonToClientTicketDtoConversor {
	public static ClientTicketDto toClientTicketDto(InputStream jsonTicket)throws ParsingException {
		try {

			ObjectMapper objectMapper = ObjectMapperFactory.instance();
			JsonNode rootNode = objectMapper.readTree(jsonTicket);
			if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
				throw new ParsingException("Unrecognized JSON (object expected)");
			} else {
				ObjectNode ticketObject = (ObjectNode) rootNode;

				JsonNode ticketIdNode = ticketObject.get("ticketId");
				if(ticketIdNode!=null) {
					long ticketId = ticketIdNode.longValue();

					String creditCard = ticketObject.get("creditCard").textValue().trim();
					String mail = ticketObject.get("mail").textValue().trim();
					int numberEntries = ticketObject.get("numberEntries").intValue();
					LocalDateTime saleTime = LocalDateTime.parse(ticketObject.get("saleTime").textValue().trim());
					boolean claimed = ticketObject.get("claimed").booleanValue();
					long matchId = ticketObject.get("matchId").longValue();

					return new ClientTicketDto(ticketId, creditCard, mail, numberEntries, saleTime, claimed, matchId);
				} else throw new InputValidationException("Bad json");

			}
		} catch (ParsingException ex) {
			throw ex;
		} catch (Exception e) {
			throw new ParsingException(e);
		}
	}

	public static ClientTicketDto toClientTicketPartialDto(InputStream jsonTicket)throws ParsingException {
		try {

			ObjectMapper objectMapper = ObjectMapperFactory.instance();
			JsonNode rootNode = objectMapper.readTree(jsonTicket);
			if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
				throw new ParsingException("Unrecognized JSON (object expected)");
			} else {
				ObjectNode ticketObject = (ObjectNode) rootNode;

				JsonNode ticketIdNode = ticketObject.get("ticketId");
				if(ticketIdNode!=null) {
					long ticketId = ticketIdNode.longValue();

					int numberEntries = ticketObject.get("numberEntries").intValue();
					LocalDateTime saleTime = LocalDateTime.parse(ticketObject.get("saleTime").textValue().trim());
					boolean claimed = ticketObject.get("claimed").booleanValue();
					long matchId = ticketObject.get("matchId").longValue();

					return new ClientTicketDto(ticketId, null, null, numberEntries, saleTime, claimed, matchId);
				} else throw new InputValidationException("Bad json");

			}
		} catch (ParsingException ex) {
			throw ex;
		} catch (Exception e) {
			throw new ParsingException(e);
		}
	}

	public static List<ClientTicketDto> toClientTicketDtos(InputStream jsonTickets) throws ParsingException {
		try {

			ObjectMapper objectMapper = ObjectMapperFactory.instance();
			JsonNode rootNode = objectMapper.readTree(jsonTickets);
			if (rootNode.getNodeType() != JsonNodeType.ARRAY) {
				throw new ParsingException("Unrecognized JSON (array expected)");
			} else {
				ArrayNode ticketArray = (ArrayNode) rootNode;
				List<ClientTicketDto> ticketDtos = new ArrayList<>(ticketArray.size());
				for (JsonNode ticketDto : ticketArray) {
					ticketDtos.add(toClientTicketDto(ticketDto));
				}

				return ticketDtos;
			}
		} catch (ParsingException ex) {
			throw ex;
		} catch (Exception e) {
			throw new ParsingException(e);
		}
	}

	private static ClientTicketDto toClientTicketDto(JsonNode matchNode) throws ParsingException{
		if (matchNode.getNodeType() != JsonNodeType.OBJECT) {
			throw new ParsingException("Unrecognized JSON (object expected)");
		} else {
			ObjectNode ticketObject = (ObjectNode) matchNode;

			JsonNode ticketIdNode = ticketObject.get("ticketId");
			if(ticketIdNode!=null) {
				long ticketId = ticketIdNode.longValue();
				int numberEntries = ticketObject.get("numberEntries").intValue();
				LocalDateTime saleTime = LocalDateTime.parse(ticketObject.get("saleTime").textValue().trim());
				boolean claimed = ticketObject.get("claimed").booleanValue();
				long matchId = ticketObject.get("matchId").longValue();
				String creditCard = ticketObject.get("creditCard").textValue().trim();

				return new ClientTicketDto(ticketId, creditCard, null, numberEntries, saleTime, claimed, matchId);
			} else throw new ParsingException("Bad json");
		}
	}

}
