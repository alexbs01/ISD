package es.udc.ws.app.restservice.servlets;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.app.model.matchService.MatchServiceFactory;
import es.udc.ws.app.model.matchService.exceptions.ClaimingException;
import es.udc.ws.app.model.matchService.exceptions.MatchPlayedException;
import es.udc.ws.app.model.matchService.exceptions.MatchSoldOutException;
import es.udc.ws.app.model.tickets.Ticket;
import es.udc.ws.app.restservice.dto.RestTicketDto;
import es.udc.ws.app.restservice.dto.TicketToRestTicketDtoConversor;
import es.udc.ws.app.restservice.json.AppExceptionToJsonConversor;
import es.udc.ws.app.restservice.json.JsonToRestTicketDtoConversor;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.servlet.RestHttpServletTemplate;
import es.udc.ws.util.servlet.ServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@SuppressWarnings("serial")
public class TicketServlet extends RestHttpServletTemplate{
    @Override
    protected void processPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, InputValidationException, InstanceNotFoundException {
        String id = req.getPathInfo();
        id = ServletUtils.normalizePath(id);
        if(id!=null && !id.isEmpty()){
            id= id.substring(1);
            String creditCard = ServletUtils.getMandatoryParameter(req, "creditCard");

            try {
                Ticket ticket = MatchServiceFactory.getService().ticketClaim(Long.parseLong(id), creditCard);
                RestTicketDto ticketDto = TicketToRestTicketDtoConversor.toRestTicketDto(ticket);
                ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_OK, JsonToRestTicketDtoConversor.toObjectNode(ticketDto), null);
            } catch (ClaimingException e) {
                ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_FORBIDDEN, AppExceptionToJsonConversor.toClaimingException(e), null);
            }
        }else {
            Long matchId = Long.valueOf(ServletUtils.getMandatoryParameter(req, "matchId"));
            String creditCard = ServletUtils.getMandatoryParameter(req, "creditCard");
            String mail = ServletUtils.getMandatoryParameter(req, "mail");
            Integer numberEntries = Integer.valueOf(ServletUtils.getMandatoryParameter(req, "numberEntries"));

            try {
                long ticket = MatchServiceFactory.getService().buyTicket(matchId,creditCard,mail,numberEntries);

                ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_CREATED, JsonNodeFactory.instance.objectNode().put("ticketId", ticket), null);
            } catch (MatchSoldOutException  e) {
                ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_FORBIDDEN, AppExceptionToJsonConversor.toMatchSoldOutException(e), null);
            } catch (MatchPlayedException e){
                ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_FORBIDDEN, AppExceptionToJsonConversor.toMatchPlayedException(e), null);
            }
        }
    }

    @Override
    protected void processGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, InputValidationException {

        ServletUtils.checkEmptyPath(req);
        String mail = ServletUtils.getMandatoryParameter(req, "mail");

        List<Ticket> tickets = MatchServiceFactory.getService().listUserTickets(mail);

        ArrayNode json = JsonNodeFactory.instance.objectNode().arrayNode();
        for (Ticket ticket : tickets) {
            ObjectNode ticketOBJ = JsonToRestTicketDtoConversor.toObjectNode(TicketToRestTicketDtoConversor.toRestTicketDto(ticket));
            json.add(ticketOBJ);
        }
        ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_OK, json, null);

    }
}
