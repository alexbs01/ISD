package es.udc.ws.app.client.service;

import es.udc.ws.app.client.service.dto.ClientMatchDto;
import es.udc.ws.app.client.service.dto.ClientTicketDto;
import es.udc.ws.app.client.service.exceptions.ClaimingException;
import es.udc.ws.app.client.service.exceptions.ClientMatchNotRemovableException;
import es.udc.ws.app.client.service.exceptions.MatchPlayedException;
import es.udc.ws.app.client.service.exceptions.MatchSoldOutException;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public interface ClientMatchService {

    Long addMatch(ClientMatchDto match)
            throws InputValidationException;

    void updateMatch(ClientMatchDto match)
            throws InputValidationException, InstanceNotFoundException;

    void removeMatch(Long matchId) throws InstanceNotFoundException,
            ClientMatchNotRemovableException;


    List<ClientMatchDto> findMatchById(Long matchId)throws InstanceNotFoundException, SQLException;
    List<ClientMatchDto> findMatchByDates(LocalDateTime date);


    Long buyTicket(Long matchId, String creditCard, String mail, Integer numberEntries)
            throws InstanceNotFoundException, MatchSoldOutException, InputValidationException, MatchPlayedException;
    ClientTicketDto ticketClaim(Long ticketId, String creditCard)
            throws InstanceNotFoundException, ClaimingException, InputValidationException;
    List<ClientTicketDto> listUserTickets(String mail);


}
