package es.udc.ws.app.client.service.rest;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.udc.ws.app.client.service.ClientMatchService;
import es.udc.ws.app.client.service.dto.ClientMatchDto;
import es.udc.ws.app.client.service.dto.ClientTicketDto;
import es.udc.ws.app.client.service.exceptions.ClaimingException;
import es.udc.ws.app.client.service.exceptions.ClientMatchNotRemovableException;
import es.udc.ws.app.client.service.exceptions.MatchPlayedException;
import es.udc.ws.app.client.service.exceptions.MatchSoldOutException;
import es.udc.ws.app.client.service.rest.json.JsonToClientExceptionConversor;
import es.udc.ws.app.client.service.rest.json.JsonToClientMatchDtoConversor;
import es.udc.ws.app.client.service.rest.json.JsonToClientTicketDtoConversor;
import es.udc.ws.util.configuration.ConfigurationParametersManager;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.json.ObjectMapperFactory;
import org.apache.hc.client5.http.fluent.Form;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpStatus;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

public class RestClientMatchService implements ClientMatchService {

    private final static String ENDPOINT_ADDRESS_PARAMETER = "RestClientMatchService.endpointAddress";
    private String endpointAddress;


    @Override
    public Long addMatch(ClientMatchDto match) throws InputValidationException, java.lang.RuntimeException {
        try {

            ClassicHttpResponse response = (ClassicHttpResponse) Request.post(getEndpointAddress() + "match").
                    bodyStream(toInputStream(match), ContentType.create("application/json")).
                    execute().returnResponse();
            System.out.println("Error: " + response);
            validateStatusCode(HttpStatus.SC_CREATED, response);

            return ObjectMapperFactory.instance().readTree(response.getEntity().getContent()).get("matchID").longValue();

        } catch (InputValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void updateMatch(ClientMatchDto match) throws InputValidationException, InstanceNotFoundException {
        try {

            ClassicHttpResponse response = (ClassicHttpResponse) Request.put(getEndpointAddress() +
                            "matchs/" + match.getMatchID()).
                    bodyStream(toInputStream(match), ContentType.create("application/json")).
                    execute().returnResponse();

            validateStatusCode(HttpStatus.SC_NO_CONTENT, response);

        } catch (InputValidationException | InstanceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeMatch(Long matchId) throws InstanceNotFoundException, ClientMatchNotRemovableException {
        try {

            ClassicHttpResponse response = (ClassicHttpResponse) Request.delete(getEndpointAddress() +
                            "matchs/" + matchId).
                    execute().returnResponse();

            validateStatusCode(HttpStatus.SC_NO_CONTENT, response);

        } catch (InstanceNotFoundException | ClientMatchNotRemovableException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ClientMatchDto> findMatchById(Long matchId) {
        try {
            String dateParameter = URLDecoder.decode(matchId.toString(), StandardCharsets.UTF_8);

            ClassicHttpResponse response = (ClassicHttpResponse) Request.get(getEndpointAddress() + "match/"
                            + URLEncoder.encode(dateParameter, StandardCharsets.UTF_8)).
                    execute().returnResponse();

            System.out.println(response);
            System.out.println(getEndpointAddress() + "match/"
                    + URLEncoder.encode(dateParameter, StandardCharsets.UTF_8));
            validateStatusCode(HttpStatus.SC_OK, response);

            return JsonToClientMatchDtoConversor.toClientmatchDtos(response.getEntity()
                    .getContent());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ClientMatchDto> findMatchByDates(LocalDateTime date) {
        try {

            String dateParameter = URLDecoder.decode(date.toString(), StandardCharsets.UTF_8);

            ClassicHttpResponse response = (ClassicHttpResponse) Request.get(getEndpointAddress() + "match?matchDate="
                            + URLEncoder.encode(dateParameter, StandardCharsets.UTF_8)).
                    execute().returnResponse();

            System.out.println(response);
            System.out.println(getEndpointAddress() + "match?matchDate="
                    + URLEncoder.encode(dateParameter, StandardCharsets.UTF_8));
            validateStatusCode(HttpStatus.SC_OK, response);

            return JsonToClientMatchDtoConversor.toClientmatchDtos(response.getEntity()
                    .getContent());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public Long buyTicket(Long matchId, String creditCard, String mail, Integer numberEntries) throws InstanceNotFoundException, MatchSoldOutException,
            InputValidationException, MatchPlayedException {
        try {
            ClassicHttpResponse response = (ClassicHttpResponse) Request.post(getEndpointAddress() + "ticket").
                    bodyForm(
                            Form.form().
                                    add("matchId", Long.toString(matchId)).
                                    add("creditCard", creditCard.replace('\'', '\0').trim()).
                                    add("mail", mail.replace('\'', '\0').trim()).
                                    add("numberEntries", Long.toString(numberEntries)).
                                    build()).
                    execute().returnResponse();
            validateStatusCode(HttpStatus.SC_CREATED, response);
            return ObjectMapperFactory.instance().readTree(response.getEntity().getContent()).get("ticketId").longValue();
        } catch (InstanceNotFoundException | InputValidationException | MatchPlayedException | MatchSoldOutException e){
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ClientTicketDto ticketClaim(Long ticketId, String creditCard) throws InstanceNotFoundException, ClaimingException, InputValidationException{
        try {
            ClassicHttpResponse response = (ClassicHttpResponse) Request.post(getEndpointAddress() + "ticket/" + ticketId).
                    bodyForm(
                            Form.form().
                                    add("creditCard", creditCard.replace('\'', '\0').trim()).
                                    build()).
                    execute().returnResponse();
            validateStatusCode(HttpStatus.SC_OK, response);

            return JsonToClientTicketDtoConversor.toClientTicketPartialDto(response.getEntity().getContent());

        } catch (InstanceNotFoundException | InputValidationException | ClaimingException e){
            throw e;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<ClientTicketDto> listUserTickets(String mail){
        try{
            ClassicHttpResponse response = (ClassicHttpResponse) Request.get(
                    getEndpointAddress() + "ticket" + "?mail=" + mail.replace('\'', '\0').trim())
                    .execute().returnResponse();
            validateStatusCode(HttpStatus.SC_OK, response);

            return JsonToClientTicketDtoConversor.toClientTicketDtos(response.getEntity().getContent());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized String getEndpointAddress() {
        if (endpointAddress == null) {
            endpointAddress = ConfigurationParametersManager
                    .getParameter(ENDPOINT_ADDRESS_PARAMETER);
        }
        return endpointAddress;
    }

    private InputStream toInputStream(ClientMatchDto match) {

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            ObjectMapper objectMapper = ObjectMapperFactory.instance();

            objectMapper.writer(new DefaultPrettyPrinter()).writeValue(outputStream,
                    JsonToClientMatchDtoConversor.toObjectNode(match));

            return new ByteArrayInputStream(outputStream.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void validateStatusCode(int successCode, ClassicHttpResponse response) throws Exception {

        try {

            int statusCode = response.getCode();

            /* Success? */
            if (statusCode == successCode) {
                return;
            }

            /* Handler error. */
            switch (statusCode) {
                case HttpStatus.SC_NOT_FOUND -> throw JsonToClientExceptionConversor.fromNotFoundErrorCode(
                        response.getEntity().getContent());
                case HttpStatus.SC_BAD_REQUEST -> throw JsonToClientExceptionConversor.fromBadRequestErrorCode(
                        response.getEntity().getContent());
                case HttpStatus.SC_FORBIDDEN -> throw JsonToClientExceptionConversor.fromForbiddenErrorCode(
                        response.getEntity().getContent());
                case HttpStatus.SC_GONE -> throw JsonToClientExceptionConversor.fromGoneErrorCode(
                        response.getEntity().getContent());
                default -> throw new RuntimeException("HTTP error; status code = "
                        + statusCode);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}
