package es.udc.ws.app.model.matchService;

import es.udc.ws.app.model.match.Match;
import es.udc.ws.app.model.matchService.exceptions.ClaimingException;
import es.udc.ws.app.model.matchService.exceptions.MatchNotRemovableException;
import es.udc.ws.app.model.matchService.exceptions.MatchPlayedException;
import es.udc.ws.app.model.matchService.exceptions.MatchSoldOutException;
import es.udc.ws.app.model.tickets.Ticket;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public interface MatchService {


    Match addMatch(Match match) throws InputValidationException;

    void updateMatch(Match match) throws InputValidationException,
            InstanceNotFoundException;

    List<Match> findMatchByDates(LocalDateTime startDate, LocalDateTime endDate);

    Match findMatchById(Long matchID) throws InstanceNotFoundException, SQLException;

    List<Ticket> listUserTickets(String mail);

    Ticket ticketClaim(Long idTicket, String creditCard)
            throws InstanceNotFoundException, ClaimingException, InputValidationException;

    void removeMatch(Long matchId)
            throws InstanceNotFoundException, MatchNotRemovableException;

    Long buyTicket (Long matchId, String creditCard, String mail, Integer numberEntries)
            throws InstanceNotFoundException, MatchSoldOutException, InputValidationException, MatchPlayedException;
}
