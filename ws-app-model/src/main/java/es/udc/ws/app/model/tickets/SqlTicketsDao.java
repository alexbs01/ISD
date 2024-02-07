package es.udc.ws.app.model.tickets;

import es.udc.ws.app.model.match.Match;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.sql.Connection;
import java.util.List;

public interface SqlTicketsDao {
    Ticket create(Connection connection, Ticket ticket, Long matchId) throws InstanceNotFoundException;

    List<Ticket> listTicketsByMatch(Connection connection, Match match);

    List<Ticket> listTicketsByUser(Connection connection, String mail);

    boolean existsByMatchId(Connection connection, Long matchId);

    Ticket find(Connection connection, long ticketId) throws InstanceNotFoundException;

    void update(Connection connection, Ticket ticket) throws InstanceNotFoundException;

    void remove(Connection connection, Long ticketId) throws InstanceNotFoundException;
}
