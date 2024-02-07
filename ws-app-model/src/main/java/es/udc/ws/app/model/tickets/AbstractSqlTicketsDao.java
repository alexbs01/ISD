package es.udc.ws.app.model.tickets;

import es.udc.ws.app.model.match.Match;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.sql.*;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSqlTicketsDao implements SqlTicketsDao{

    @Override
    public List<Ticket> listTicketsByMatch(Connection connection, Match match) {
        String queryString = "SELECT ticketId, creditCard, email, numberEntries, saleTime, claimed, matchId" +
                " FROM Ticket WHERE matchId = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)){

            preparedStatement.setLong(1, match.getMatchID());

            ResultSet resultSet = preparedStatement.executeQuery();

            List<Ticket> tickets = new ArrayList<>();
            while (resultSet.next()){
                int i = 1;
                long ticketId = resultSet.getLong(i++);
                String creditCard = resultSet.getString(i++);
                String email = resultSet.getString(i++);
                int numberEntries = resultSet.getInt(i++);
                Timestamp saleTimeTimestamp = resultSet.getTimestamp(i++);
                LocalDateTime saleTime = saleTimeTimestamp != null
                        ? saleTimeTimestamp.toLocalDateTime()
                        : null;
                boolean claimed = resultSet.getBoolean(i++);
                long matchId = resultSet.getLong(i);

                tickets.add(new Ticket(ticketId, creditCard, email, numberEntries, saleTime, claimed, matchId));
            }
            return tickets;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Ticket> listTicketsByUser(Connection connection, String mail) {
        String queryString = "SELECT ticketId, creditCard, email, numberEntries, saleTime, claimed, matchId" +
                " FROM Ticket WHERE email = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)){

            preparedStatement.setString(1, mail);

            ResultSet resultSet = preparedStatement.executeQuery();

            List<Ticket> tickets = new ArrayList<>();
            while (resultSet.next()){
                int i = 1;
                long ticketId = resultSet.getLong(i++);
                String creditCard = resultSet.getString(i++);
                String email = resultSet.getString(i++);
                int numberEntries = resultSet.getInt(i++);
                Timestamp saleTimeTimestamp = resultSet.getTimestamp(i++);
                LocalDateTime saleTime = saleTimeTimestamp != null
                        ? saleTimeTimestamp.toLocalDateTime()
                        : null;
                boolean claimed = resultSet.getBoolean(i++);
                long matchId = resultSet.getLong(i);

                tickets.add(new Ticket(ticketId, creditCard, email, numberEntries, saleTime, claimed, matchId));
            }
            return tickets;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Ticket find(Connection connection, long ticketId) throws InstanceNotFoundException {

        String queryString = "SELECT ticketId, creditCard, email, numberEntries, saleTime, claimed, matchId" +
                " FROM Ticket WHERE ticketId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

            /* Fill "preparedStatement". */
            int i = 1;
            preparedStatement.setLong(i, ticketId);

            /* Execute query. */
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                throw new InstanceNotFoundException(ticketId, Ticket.class.getName());
            }

            /* Get results. */

            i = 2;
            String creditCard = resultSet.getString(i++);
            String email = resultSet.getString(i++);
            int numberEnteries = resultSet.getInt(i++);
            Timestamp saleTimeTimestamp = resultSet.getTimestamp(i++);
            LocalDateTime saleTime = saleTimeTimestamp != null
                    ? saleTimeTimestamp.toLocalDateTime()
                    : null;
            boolean claimed = resultSet.getBoolean(i++);
            long matchId = resultSet.getLong(i);

            /* Return sale. */
            return new Ticket(ticketId, creditCard, email, numberEnteries, saleTime, claimed, matchId);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Connection connection, Ticket ticket) throws InstanceNotFoundException {

        String queryString = "UPDATE Ticket"
                + " SET creditCard = ?, email = ?, numberEntries = ?, claimed = ?, saleTime = ?"
                + " WHERE ticketId = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)){
            int i = 1;
            preparedStatement.setString(i++, ticket.getCreditCard());
            preparedStatement.setString(i++, ticket.getEmail());
            preparedStatement.setInt(i++, ticket.getNumberEntries());
            preparedStatement.setBoolean(i++, ticket.isClaimed());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(ticket.getSaleTime()));
            preparedStatement.setLong(i, ticket.getTicketId());

            int updateRows = preparedStatement.executeUpdate();
            if (updateRows==0){
                throw new InstanceNotFoundException(ticket.getTicketId(), Ticket.class.getName());
            }
        }catch (SQLException e){
            throw new   RuntimeException(e);
        }
    }

    @Override
    public boolean existsByMatchId(Connection connection, Long matchId) {

        /* Create "queryString". */
        String queryString = "SELECT COUNT(*) FROM Ticket WHERE matchId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

            /* Fill "preparedStatement". */
            int i = 1;
            preparedStatement.setLong(i++, matchId.longValue());

            /* Execute query. */
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                throw new SQLException("Error retrieving the number of sales for the match with id " + matchId);
            }

            /* Get results. */
            i = 1;
            Long numberOfSales = resultSet.getLong(i++);

            /* Return result. */
            return numberOfSales > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void remove(Connection connection, Long ticketId) throws InstanceNotFoundException{
        String queryString = "DELETE FROM Ticket WHERE ticketId = ?";

        try(PreparedStatement preparedStatement = connection.prepareStatement(queryString)){
            preparedStatement.setLong(1, ticketId);

            int removedRows = preparedStatement.executeUpdate();
            if(removedRows == 0){
                throw new InstanceNotFoundException(ticketId, Ticket.class.getName());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
