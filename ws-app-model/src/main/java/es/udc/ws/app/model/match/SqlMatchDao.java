package es.udc.ws.app.model.match;

import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;

public interface SqlMatchDao {
    Match create(Connection connection, Match match);

    List<Match> findByDate(Connection connection, LocalDateTime startDate,LocalDateTime endDate);

    Match find(Connection connection, Long matchID)
            throws InstanceNotFoundException;

    void update(Connection connection, Match match)
            throws InstanceNotFoundException;

    void remove(Connection connection, Long matchID)
            throws InstanceNotFoundException;
}