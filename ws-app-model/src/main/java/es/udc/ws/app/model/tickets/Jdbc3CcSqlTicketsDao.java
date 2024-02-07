package es.udc.ws.app.model.tickets;

import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.sql.*;
import java.time.Instant;

public class Jdbc3CcSqlTicketsDao extends AbstractSqlTicketsDao {

    @Override
    public Ticket create(Connection connection, Ticket ticket, Long matchId) throws InstanceNotFoundException {

        //tickets time
        Timestamp actualDateTime = Timestamp.from(Instant.now());
        actualDateTime.setNanos(0);
        ticket.setSaleTime(actualDateTime.toLocalDateTime());

        String insertString = """
                INSERT INTO Ticket(creditCard, email, numberEntries, saleTime, matchId)
                VALUES (?, ?, ?, ?, (SELECT matchId
                                     FROM GameMatch
                                     WHERE matchId = ?))""";

        java.sql.Timestamp sqlDate = java.sql.Timestamp.valueOf(String.valueOf(actualDateTime));

        try(PreparedStatement preparedStatement = connection.prepareStatement(insertString, Statement.RETURN_GENERATED_KEYS)) {

            int i = 1;
            preparedStatement.setString(i++, ticket.getCreditCard());
            preparedStatement.setString(i++, ticket.getEmail());
            preparedStatement.setInt(i++, ticket.getNumberEntries());
            preparedStatement.setTimestamp(i++, sqlDate);
            preparedStatement.setLong(i, matchId);

            preparedStatement.executeUpdate();

            ResultSet rs = preparedStatement.getGeneratedKeys();

            if(!rs.next()) {
                throw new SQLException("JDBC driver did not return generated key.");
            }

            ticket.setTicketId(rs.getLong(1));
            ticket.setSaleTime(actualDateTime.toLocalDateTime());
            ticket.setMatchId(matchId);

            return ticket;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
