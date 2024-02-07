package es.udc.ws.app.model.match;

import java.sql.*;

public class Jdbc3CcSqlMatchDao extends AbstractSqlMatchDao {

    @Override
    public Match create(Connection connection, Match match) {

        /* Create "queryString". */
        String queryString = "INSERT INTO GameMatch"
                + " (matchDate, visitorName, maxTicketCount, ticketPrice, registrationDate,ticketsSold)"
                + " VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                queryString, Statement.RETURN_GENERATED_KEYS)) {

            /* Fill "preparedStatement". */
            int i = 1;
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(match.getMatchDate()));
            preparedStatement.setString(i++, match.getVisitorName());
            preparedStatement.setInt(i++, match.getMaxTicketCount());
            preparedStatement.setFloat(i++, match.getTicketPrice());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(match.getRegistrationDate()));
            preparedStatement.setInt(i++, match.getTicketsSold());


            /* Execute query. */
            preparedStatement.executeUpdate();

            /* Get generated identifier. */
            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (!resultSet.next()) {
                throw new SQLException(
                        "JDBC driver did not return generated key.");
            }
            Long matchID = resultSet.getLong(1);

            /* Return match. */
            return new Match(matchID, match.getVisitorName(), match.getMatchDate(), match.getTicketPrice(), match.getMaxTicketCount(), match.getTicketsSold(), match.getRegistrationDate());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
