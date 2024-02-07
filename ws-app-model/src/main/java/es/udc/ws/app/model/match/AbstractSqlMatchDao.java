package es.udc.ws.app.model.match;

import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.List;

public abstract class AbstractSqlMatchDao implements SqlMatchDao{


    protected AbstractSqlMatchDao() {
    }


    @Override
    public List<Match> findByDate(Connection connection, LocalDateTime startDate, LocalDateTime endDate)  {

        List<Match> matchs = new ArrayList<>();

        String queryString =
                "SELECT matchId, matchDate, visitorName, maxTicketCount, ticketPrice, registrationDate,ticketsSold " +
                "FROM GameMatch " +
                "WHERE matchDate BETWEEN ? AND ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {
            Timestamp startTimestamp = Timestamp.valueOf(startDate);
            Timestamp endTimestamp = Timestamp.valueOf(endDate);

            preparedStatement.setTimestamp(1, startTimestamp);
            preparedStatement.setTimestamp(2, endTimestamp);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                long matchID = resultSet.getLong("matchId");
                Timestamp matchDateasTimestamp = resultSet.getTimestamp("matchDate");
                String visitorName = resultSet.getString("visitorName");
                float ticketPrice = resultSet.getFloat("ticketPrice");
                int ticketsSold = resultSet.getInt("ticketsSold");
                int maxTicketCount = resultSet.getInt("maxTicketCount");
                Timestamp registrationDateAsTimestamp = resultSet.getTimestamp("registrationDate");
                LocalDateTime registrationDate = registrationDateAsTimestamp.toLocalDateTime();
                LocalDateTime matchDate = matchDateasTimestamp.toLocalDateTime();

                Match match = new Match(matchID,visitorName,matchDate,ticketPrice,maxTicketCount,ticketsSold,registrationDate);
                matchs.add(match);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return matchs;

    }


    @Override
    public Match find(Connection connection, Long matchId) throws InstanceNotFoundException {
        String queryString = """
                SELECT matchId, matchDate, visitorName, maxTicketCount, ticketPrice, registrationDate, ticketsSold
                FROM GameMatch
                WHERE matchId = ?""";

        Match matchFound;

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {
            preparedStatement.setLong(1, matchId);
            ResultSet rs = preparedStatement.executeQuery();
            if (!rs.next()){
                throw new InstanceNotFoundException(matchId, Match.class.getName());
            }

            Timestamp tsMatchDate = rs.getTimestamp("matchDate");
            Timestamp tsRegistrationDate = rs.getTimestamp("registrationDate");

            matchFound = new Match(
                    rs.getLong("matchId"),
                    rs.getString("visitorName"),
                    tsMatchDate.toLocalDateTime(),
                    rs.getFloat("ticketPrice"),
                    rs.getInt("maxTicketCount"),
                    rs.getInt("ticketsSold"),
                    tsRegistrationDate.toLocalDateTime());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return matchFound;
    }

    @Override
    public void update(Connection connection, Match match) throws InstanceNotFoundException {
        String queryString = "UPDATE GameMatch"
                + " SET matchDate = ?, visitorName = ?, maxTicketCount = ?, ticketsSold = ?"
                + " WHERE matchId = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)){
            int i = 1;
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(match.getMatchDate()));
            preparedStatement.setString(i++, match.getVisitorName());
            preparedStatement.setInt(i++, match.getMaxTicketCount());
            preparedStatement.setInt(i++, match.getTicketsSold());
            preparedStatement.setLong(i, match.getMatchID());

            int updateRows = preparedStatement.executeUpdate();
            if (updateRows==0){
                throw new InstanceNotFoundException(match.getMatchID(), Match.class.getName());
            }
        }catch (SQLException e){
            throw new   RuntimeException(e);
        }

    }

    @Override
    public void remove(Connection connection, Long matchId)
            throws InstanceNotFoundException {

        /* Create "queryString". */
        String queryString = "DELETE FROM GameMatch WHERE" + " matchId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

            /* Fill "preparedStatement". */
            int i = 1;
            preparedStatement.setLong(i++, matchId);

            /* Execute query. */
            int removedRows = preparedStatement.executeUpdate();

            if (removedRows == 0) {
                throw new InstanceNotFoundException(matchId,
                        Match.class.getName());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
