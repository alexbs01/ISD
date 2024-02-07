package es.udc.ws.app.client.service.rest.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import es.udc.ws.app.client.service.exceptions.ClaimingException;
import es.udc.ws.app.client.service.exceptions.ClientMatchNotRemovableException;
import es.udc.ws.app.client.service.exceptions.MatchPlayedException;
import es.udc.ws.app.client.service.exceptions.MatchSoldOutException;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

import java.io.InputStream;

public class JsonToClientExceptionConversor {

    public static Exception fromBadRequestErrorCode(InputStream ex) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(ex);
            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                String errorType = rootNode.get("errorType").textValue();
                if (errorType.equals("InputValidation")) {
					return toInputValidationException(rootNode);
				} else {
					throw new ParsingException("Unrecognized error type: " + errorType);
				}
            }
        } catch (ParsingException e) {
            throw e;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    private static InputValidationException toInputValidationException(JsonNode rootNode) {
        String message = rootNode.get("message").textValue();
        return new InputValidationException(message);
    }

	public static Exception fromNotFoundErrorCode(InputStream ex) throws ParsingException {
		try {
			ObjectMapper objectMapper = ObjectMapperFactory.instance();
			JsonNode rootNode = objectMapper.readTree(ex);
			if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
				throw new ParsingException("Unrecognized JSON (object expected)");
			} else {
				String errorType = rootNode.get("errorType").textValue();
				if (errorType.equals("InstanceNotFound")) {
					return toInstanceNotFoundException(rootNode);
				} else {
					throw new ParsingException("Unrecognized error type: " + errorType);
				}
			}
		} catch (ParsingException e) {
			throw e;
		} catch (Exception e) {
			throw new ParsingException(e);
		}
	}

    private static InstanceNotFoundException toInstanceNotFoundException(JsonNode rootNode) {
        String instanceId = rootNode.get("instanceId").textValue();
        String instanceType = rootNode.get("instanceType").textValue();
        return new InstanceNotFoundException(instanceId, instanceType);
    }

	public static Exception fromForbiddenErrorCode(InputStream ex) throws ParsingException {
		try {
			ObjectMapper objectMapper = ObjectMapperFactory.instance();
			JsonNode rootNode = objectMapper.readTree(ex);
			if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
				throw new ParsingException("Unrecognized JSON (object expected)");
			} else {
				String errorType = rootNode.get("errorType").textValue();
                return switch (errorType) {
                    case "matchNotRemovable" ->
							tomatchNotRemovableException(rootNode);
                    case "MatchPlayedException" ->
							new MatchPlayedException(rootNode.get("matchId").longValue());
                    case "MatchSoldOutException" ->
							new MatchSoldOutException(rootNode.get("matchId").longValue());
					case "ClaimingException" ->
							new ClaimingException(rootNode.get("ticketId").longValue());
                    default ->
							throw new ParsingException("Unrecognized error type: " + errorType);
                };
			}
		} catch (ParsingException e) {
			throw e;
		} catch (Exception e) {
			throw new ParsingException(e);
		}
	}
    private static ClientMatchNotRemovableException tomatchNotRemovableException(JsonNode rootNode) {
        Long matchId = rootNode.get("matchId").longValue();
        return new ClientMatchNotRemovableException(matchId);
    }

	public static Exception fromGoneErrorCode(InputStream ex) throws ParsingException {
		try {
			ObjectMapper objectMapper = ObjectMapperFactory.instance();
			JsonNode rootNode = objectMapper.readTree(ex);
			if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
				throw new ParsingException("Unrecognized JSON (object expected)");
			} else {
				String errorType = rootNode.get("errorType").textValue();
                switch (errorType) {
                    case "TicketClaimed" ->

                        //TODO: Implementar las excepciones de tickets

                            throw new Exception("CODIGO NO IMPLEMTADO");
                    case "MatchPlayed" ->

                        //TODO: Implementar las excepciones de tickets

                            throw new Exception("CODIGO NO IMPLEMTADO");
                    case "MatchSoldOut" ->

                        //TODO: Implementar las excepciones de tickets
                            throw new Exception("CODIGO NO IMPLEMTADO");
                    default -> throw new ParsingException("Unrecognized error type: " + errorType);
                }
			}
		} catch (ParsingException e) {
			throw e;
		} catch (Exception e) {
			throw new ParsingException(e);
		}
	}




}
