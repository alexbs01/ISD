package es.udc.ws.app.model.tickets;

import es.udc.ws.app.model.match.Match;
import es.udc.ws.app.model.matchService.MatchService;
import es.udc.ws.app.model.matchService.MatchServiceFactory;
import es.udc.ws.app.model.util.ModelConstants;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.sql.DataSourceLocator;
import es.udc.ws.util.sql.SimpleDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Jdbc3CcSqlTicketsDaoTest {

    private static final DataSource dataSource = new SimpleDataSource();
    private static MatchService matchService = null;
    private static SqlTicketsDao ticketsDao = null;

    private static final String removeAllMatches = "DELETE FROM GameMatch";
    private static final String removeAllTickets = "DELETE FROM Ticket";

    @BeforeAll
    public static void init() {

        /* Add "dataSource" to "DataSourceLocator". */
        DataSourceLocator.addDataSource(ModelConstants.APP_DATA_SOURCE, dataSource);

        matchService = MatchServiceFactory.getService();
        ticketsDao = SqlTicketsDaoFactory.getDao();

        try {
            Connection c = dataSource.getConnection();
            Statement stMatches = c.createStatement();
            Statement stTickets = c.createStatement();

            stTickets.execute(removeAllTickets);
            stMatches.execute(removeAllMatches);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    static void afterAll() {
        try {
            Connection c = dataSource.getConnection();
            Statement stMatches = c.createStatement();
            Statement stTickets = c.createStatement();

            stTickets.execute(removeAllTickets);
            stMatches.execute(removeAllMatches);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void create() {
        // Creamos los partidos
        LocalDateTime matchTime1 = LocalDateTime.of(2024, 10, 10, 10, 10);

        Match match1 = new Match("Los que dicen que 'estudian' magisterio FC", matchTime1, 5, 100);

        // Creamos los tickets
        Ticket ticket1 = new Ticket("0000000000000000", "dr.strange@gmail.com", 5);
        Ticket ticket2 = new Ticket("0000000000000002", "monkeyD.Luffy@gmail.com", 17);
        Ticket ticket3 = new Ticket("0000000000000003", "erenYaeger@gmail.com", 5);

        try { // Establecemos conexión con la DB
            Connection c = dataSource.getConnection();
            SqlTicketsDao ticketsDao = SqlTicketsDaoFactory.getDao();
            MatchService matchService = MatchServiceFactory.getService();

            try { // Insertamos los partidos en su tabla
                match1 = matchService.addMatch(match1);
            } catch (InputValidationException e) {
                throw new RuntimeException(e);
            }

            // Creamos las consultas que se usarán en estos test
            String queryTicket = "SELECT * FROM Ticket WHERE ticketId = ?";
            String queryMatch = "SELECT * FROM GameMatch WHERE matchId = ?";

            PreparedStatement psTicket = c.prepareStatement(queryTicket);
            PreparedStatement psMatch  = c.prepareStatement(queryMatch);
            psMatch.setLong(1, match1.getMatchID());

            // Ejecutamos la consulta de los partidos y vamos a la primera fila
            ResultSet rsMatch = psMatch.executeQuery();
            rsMatch.next();

            // Guardamos el id del ticket uno para más adelante hacer la consulta
            ticket1 = ticketsDao.create(c,ticket1,match1.getMatchID());

            // Volvemos a ejecutar la consulta de los partidos
            rsMatch = psMatch.executeQuery();
            rsMatch.next();

            ticketsDao.create(c, ticket2, match1.getMatchID());
            ticketsDao.create(c, ticket3, match1.getMatchID());

            rsMatch = psMatch.executeQuery();
            rsMatch.next();

            // Ejecutasmos la consulta de los tickets para comprobar que otras columnas se crearon bien
            psTicket.setLong(1, ticket1.getTicketId());
            ResultSet rsTicket = psTicket.executeQuery();
            rsTicket.next();

            assertEquals("0000000000000000", rsTicket.getString("creditCard"));
            assertEquals("dr.strange@gmail.com", rsTicket.getString("email"));

        } catch (SQLException | InstanceNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            afterAll();
        }
    }
}