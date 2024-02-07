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
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class AbstractSqlTicketsDaoTest {

    private static final DataSource dataSource = new SimpleDataSource();
    private static SqlTicketsDao ticketsDao = null;
    private static MatchService matchService = null;
    private static final String removeAllMatches = "DELETE FROM GameMatch";
    private static final String removeAllTickets = "DELETE FROM Ticket";

    @BeforeAll
    public static void init() {
        DataSourceLocator.addDataSource(ModelConstants.APP_DATA_SOURCE, dataSource);

        ticketsDao = SqlTicketsDaoFactory.getDao();
        matchService = MatchServiceFactory.getService();

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
    void listTicketsByMatch() {
        DataSource dataSource = DataSourceLocator.getDataSource(ModelConstants.APP_DATA_SOURCE);

        LocalDateTime timeMatch = LocalDateTime.now().plusMonths(3);
        Match match1 = new Match(1, "Barça FC", timeMatch, 5, 100);
        Match match2 = new Match(2, "Barça FC", timeMatch, 5, 100);
        Match match3 = new Match(3, "Barça FC", timeMatch, 5, 100);


        Ticket ticket1 = new Ticket( "0000000000000000", "a", 5);
        Ticket ticket2 = new Ticket( "0000000000000000", "a", 5);
        List<Ticket> ticketsM1 = new ArrayList<>();
        ticketsM1.add(ticket1);
        List<Ticket> ticketsM2 = new ArrayList<>();
        ticketsM2.add(ticket2);

        try (Connection connection = dataSource.getConnection()){
            try{
                //creation
                match1 = matchService.addMatch(match1);
                match2 = matchService.addMatch(match2);
                match3 = matchService.addMatch(match3);
                try {
                    ticketsDao.create(connection, ticket1, match1.getMatchID());
                    ticketsDao.create(connection, ticket2, match2.getMatchID());
                } catch (InstanceNotFoundException e) {
                    throw new RuntimeException(e);
                }

                List<Ticket> ticketsMS1 = ticketsDao.listTicketsByMatch(connection, match1);
                assertEquals(ticketsMS1, ticketsM1);
                List<Ticket> ticketsMS2 = ticketsDao.listTicketsByMatch(connection, match2);
                assertEquals(ticketsMS2, ticketsM2);

                List<Ticket> empty = ticketsDao.listTicketsByMatch(connection, match3);
                assertEquals(empty.size(), 0);

            } catch (InputValidationException e) {
                throw new RuntimeException(e);
            } finally {
                afterAll();
            }

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Test
    void listTicketsByUser() {
        DataSource dataSource = DataSourceLocator.getDataSource(ModelConstants.APP_DATA_SOURCE);

        LocalDateTime timeMatch = LocalDateTime.now().plusMonths(3);
        Ticket ticket1 = new Ticket( "1010101010101010", "a", 5);
        Ticket ticket2 = new Ticket( "1010101010101010", "a", 5);
        List<Ticket> ticketsU1 = new ArrayList<>();
        ticketsU1.add(ticket1);
        ticketsU1.add(ticket2);
        Match match = new Match(1, "Barça FC", timeMatch, 5, 100);

        try (Connection connection = dataSource.getConnection()){
            try{
                //creation
                match = matchService.addMatch(match);
                try {
                    ticketsDao.create(connection, ticket1, match.getMatchID());
                    ticketsDao.create(connection, ticket2, match.getMatchID());
                } catch (InstanceNotFoundException e) {
                    throw new RuntimeException(e);
                }

                List<Ticket> ticketsUS = ticketsDao.listTicketsByUser(connection, "a");
                assertEquals(ticketsUS, ticketsU1);

                List<Ticket> empty = ticketsDao.listTicketsByUser(connection, "b");
                assertEquals(empty.size(), 0);

            } catch (InputValidationException e) {
                throw new RuntimeException(e);
            } finally {
                afterAll();
            }


        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Test
    void find() {
        DataSource dataSource = DataSourceLocator.getDataSource(ModelConstants.APP_DATA_SOURCE);

        LocalDateTime timeMatch = LocalDateTime.now().plusMonths(3);
        Ticket ticket1 = new Ticket( "1010101010101010", "dr.strange@gmail.com", 5);
        Ticket ticket2 = new Ticket( "1010101010101010", "dr.strange@gmail.com", 5);
        Match match = new Match(-1, "Barça FC", timeMatch, 5, 100);

        try (Connection connection = dataSource.getConnection()){
            try{
                //creation
                match = matchService.addMatch(match);
                try {
                    ticket1 = ticketsDao.create(connection, ticket1, match.getMatchID());
                    ticket2 = ticketsDao.create(connection, ticket2, match.getMatchID());
                } catch (InstanceNotFoundException e) {
                    throw new RuntimeException(e);
                }

                Ticket ticket1F = ticketsDao.find(connection, ticket1.getTicketId());
                assertEquals(ticket1F, ticket1);

                Ticket ticket2F = ticketsDao.find(connection, ticket2.getTicketId());
                assertEquals(ticket2F, ticket2);

                assertNotEquals(ticket2F, ticket1F);

            } catch (InstanceNotFoundException | InputValidationException e) {
                throw new RuntimeException(e);
            } finally {
                afterAll();
            }


        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Test
    void update() {
        DataSource dataSource = DataSourceLocator.getDataSource(ModelConstants.APP_DATA_SOURCE);

        LocalDateTime timeMatch = LocalDateTime.now().plusMonths(3);
        Ticket ticket = new Ticket( "0000000000000000", "dr.strange@gmail.com", 5);
        Match match = new Match(1, "Barça FC", timeMatch, 5, 100);

        try (Connection connection = dataSource.getConnection()){
            try{
                //creation
                match = matchService.addMatch(match);
                try {
                    ticket = ticketsDao.create(connection, ticket, match.getMatchID());
                } catch (InstanceNotFoundException e) {
                    throw new RuntimeException(e);
                }

                //change ticket
                ticket.setCreditCard("0000000000000001");
                ticket.setEmail("dr.strange@gmail.com");
                ticket.setNumberEntries(3);
                ticket.setClaimed(true);

                //update
                ticketsDao.update(connection, ticket);

                //tests
                Ticket ticketUpdated = ticketsDao.find(connection, ticket.getTicketId());
                assertEquals(ticketUpdated, ticket);

            } catch (InstanceNotFoundException | InputValidationException e) {
                throw new RuntimeException(e);
            } finally {
                afterAll();
            }


        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Test
    void removeTest() {
        DataSource dataSource = DataSourceLocator.getDataSource(ModelConstants.APP_DATA_SOURCE);
        //creacción de un ticket
        LocalDateTime timeMatch = LocalDateTime.now().plusMonths(3);
        Ticket ticket = new Ticket( "1010101010101010", "dr.strange@gmail.com", 5);
        Match match = new Match(1, "Barça FC", timeMatch, 5, 100);
        try(Connection connection = dataSource.getConnection()) {
            try {
                match = matchService.addMatch(match);
                try {
                    ticket = ticketsDao.create(connection, ticket, match.getMatchID());
                } catch (InstanceNotFoundException e) {
                    throw new RuntimeException(e);
                }

                ticketsDao.remove(connection, ticket.getTicketId());

            } catch (InstanceNotFoundException | InputValidationException e) {
                throw new RuntimeException(e);
            } finally {
                afterAll();
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}