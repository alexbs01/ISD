package es.udc.ws.app.test.model.appservice;

import es.udc.ws.app.model.match.Match;
import es.udc.ws.app.model.match.SqlMatchDao;
import es.udc.ws.app.model.match.SqlMatchDaoFactory;
import es.udc.ws.app.model.matchService.MatchService;
import es.udc.ws.app.model.matchService.MatchServiceFactory;
import es.udc.ws.app.model.matchService.exceptions.ClaimingException;
import es.udc.ws.app.model.matchService.exceptions.MatchPlayedException;
import es.udc.ws.app.model.matchService.exceptions.MatchSoldOutException;
import es.udc.ws.app.model.tickets.SqlTicketsDao;
import es.udc.ws.app.model.tickets.SqlTicketsDaoFactory;
import es.udc.ws.app.model.tickets.Ticket;
import es.udc.ws.app.model.util.ModelConstants;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.sql.DataSourceLocator;
import es.udc.ws.util.sql.SimpleDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AppServiceTest {

    private static final DataSource dataSource = new SimpleDataSource();
    private static MatchService matchService = null;
    private static SqlTicketsDao ticketsDao = null;
    private static SqlMatchDao matchDao = null;
    private static final String removeAllMatches = "DELETE FROM GameMatch";
    private static final String removeAllTickets = "DELETE FROM Ticket";

    @BeforeAll
    public static void init() {

        /*
         * Create a simple data source and add it to "DataSourceLocator" (this
         * is needed to test "es.udc.ws.matchs.model.matchservice.matchService"
         */
        DataSource dataSource = new SimpleDataSource();

        /* Add "dataSource" to "DataSourceLocator". */
        DataSourceLocator.addDataSource(ModelConstants.APP_DATA_SOURCE, dataSource);

        matchService = MatchServiceFactory.getService();
        matchDao = SqlMatchDaoFactory.getDao();
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

    @AfterEach
    void afterEach() {
        try {
            Connection c = dataSource.getConnection();
            Statement stMatches = c.createStatement();
            Statement stTickets = c.createStatement();

            stTickets.execute(removeAllTickets);
            stMatches.execute(removeAllMatches);

            c.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // util func
    private Match getValidMatch(String visitorName, LocalDateTime matchDate) {
        return new Match(visitorName,matchDate,10,500);
    }
    private Match getValidMatch(LocalDateTime matchDate) {
        return getValidMatch("Team A",matchDate);
    }
    private Match getValidMatch() {
        return getValidMatch("Team A",LocalDateTime.now().plusMonths(3).withNano(0));
    }

    //region FUNC 1

    //test addmatch
    @Test
    public void addMatchTest() {

        Match match = getValidMatch();

        try {

            // Create Match
            LocalDateTime beforeCreationDate = LocalDateTime.now().withNano(0);

            Match addedMatch = matchService.addMatch(match);

            LocalDateTime afterCreationDate = LocalDateTime.now().withNano(0);

            assertEquals(addedMatch.getTicketPrice(), match.getTicketPrice());
            assertEquals(addedMatch.getMaxTicketCount(), match.getMaxTicketCount());
            assertEquals(addedMatch.getTicketsSold(), match.getTicketsSold());
            assertEquals(addedMatch.getMatchDate(), match.getMatchDate());
            assertEquals(addedMatch.getVisitorName(), match.getVisitorName());
            assertTrue((!addedMatch.getRegistrationDate().isBefore(beforeCreationDate))
                    && (!addedMatch.getRegistrationDate().isAfter(afterCreationDate)));

        } catch (InputValidationException e) {
            throw new RuntimeException(e);
        }
    }


    /*@Test
    public void addMatchRuntimeException(){
        LocalDateTime matchDate = LocalDateTime.of(1492, 10, 12, 10, 10);
        Match match = new Match("Los masajitos Fisioterapia FC", matchDate, 5, 100);

        assertThrows(RuntimeException.class, () -> matchService.addMatch(match));
    }*/

    //endregion

    //region FUNC 2
    @Test
    public void findMatchByDate() {

        Match match = getValidMatch();
        Match addedMatch ;

        try {

            // Create match
            LocalDateTime beforeCreationDate = LocalDateTime.now().withNano(0);

            addedMatch = matchService.addMatch(match);

            LocalDateTime afterCreationDate = LocalDateTime.now().withNano(0);

            Match foundedMatch = matchService.findMatchByDates(match.getMatchDate().minusMinutes(1),match.getMatchDate().plusMinutes(1)).get(0);

            assertEquals(addedMatch, foundedMatch);
            assertEquals(foundedMatch.getTicketPrice(),match.getTicketPrice());
            assertEquals(foundedMatch.getMaxTicketCount(),match.getMaxTicketCount());
            assertEquals(foundedMatch.getTicketsSold(),match.getTicketsSold());
            assertEquals(foundedMatch.getMatchDate(),match.getMatchDate());
            assertEquals(foundedMatch.getVisitorName(),match.getVisitorName());
            assertTrue((!foundedMatch.getRegistrationDate().isBefore(beforeCreationDate))
                    && (!foundedMatch.getRegistrationDate().isAfter(afterCreationDate)));


        } catch ( InputValidationException e) {
            throw new RuntimeException(e);
        }
    }


    //endregion

    //region FUNC 3
    @Test
    void findMatchById() {
        try {
            Match match1 = getValidMatch();

            Ticket ticket1 = new Ticket("0000000000000000", "dr.strange@gmail.com", 5);
            Ticket ticket2 = new Ticket("0000000000000001", "monkeyD.Luffy@gmail.com", 15);

            Match matchCreated;

            try {
                matchCreated = matchService.addMatch(match1);
            } catch (InputValidationException e) {
                throw new RuntimeException(e);
            }

            Match matchFound = matchService.findMatchById(matchCreated.getMatchID());

            assertEquals(matchCreated.getMatchID(), matchFound.getMatchID());
            assertEquals(10, matchFound.getTicketPrice());
            assertEquals(match1.getVisitorName(), matchFound.getVisitorName());
            assertEquals(0, match1.getTicketsSold());

            matchService.buyTicket(matchFound.getMatchID(), ticket1.getCreditCard(), ticket1.getEmail(), ticket1.getNumberEntries());
            matchService.buyTicket(matchFound.getMatchID(), ticket2.getCreditCard(), ticket2.getEmail(), ticket2.getNumberEntries());

            matchFound = matchService.findMatchById(matchCreated.getMatchID());

            assertEquals(ticket1.getNumberEntries() + ticket2.getNumberEntries(), matchFound.getTicketsSold());

        } catch (SQLException | InstanceNotFoundException | MatchSoldOutException | InputValidationException |
                 MatchPlayedException e) {
            throw new RuntimeException(e);
        }
    }
   @Test
    void findMatchByIdExceptionInstanceNotFound() {
        assertThrows(InstanceNotFoundException.class, () -> matchService.findMatchById(1L));
        assertThrows(InstanceNotFoundException.class, () -> matchService.findMatchById(2L));
    }

    //endregion

    //region FUNC 4
    @Test
    void correctBuy(){
        DataSource dataSource = DataSourceLocator.getDataSource(ModelConstants.APP_DATA_SOURCE);
        Match match = getValidMatch();
        long ticketId;
        try{
            match = matchService.addMatch(match);
            ticketId = matchService.buyTicket(match.getMatchID(),"0000000000000000", "a", 2);

            // ticket add validation
            try(Connection connection = dataSource.getConnection()){
                Ticket ticket1 = ticketsDao.find(connection, ticketId);
                assertEquals(ticket1.getEmail(), "a");
                assertEquals(ticket1.getCreditCard(), "0000000000000000");
                assertEquals(ticket1.getNumberEntries(), 2);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            // match update assert
            match = matchService.findMatchById(match.getMatchID());
            assertEquals(match.getTicketsSold(), 2);

        } catch (InputValidationException | InstanceNotFoundException | MatchSoldOutException | SQLException |
                 MatchPlayedException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    void soldOutBuy(){
        Match match = getValidMatch();

        try {
            match = matchService.addMatch(match);
            Match finalMatch = match;
            assertThrows(MatchSoldOutException.class, () -> matchService.buyTicket(finalMatch.getMatchID(), "0000000000000000", "a", 1000));

            // match not update assert
            match = matchService.findMatchById(match.getMatchID());
            assertEquals(match.getTicketsSold(), 0);

        } catch (SQLException | InstanceNotFoundException | InputValidationException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    void instanceNotFoundBuy(){
        assertThrows(InstanceNotFoundException.class, () -> matchService.buyTicket(-1L, "1010101010101010", "a", 10));
    }
    @Test
    void matchPlayBuy(){
        //old match create
        Match oldMatch = new Match(-1, "cele", LocalDateTime.now().minusMinutes(10),
                10, 50, 0, LocalDateTime.now().minusDays(10));

        try (Connection connection = dataSource.getConnection()) {
            oldMatch = matchDao.create(connection, oldMatch);
            Match finalOldMatch = oldMatch;
            assertThrows(MatchPlayedException.class, () -> matchService.buyTicket(finalOldMatch.getMatchID(), "0000000000000000", "a", 2));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    void inputValidationBuy() {
        Match match = getValidMatch();
        try {
            matchService.addMatch(match);
            assertThrows(InputValidationException.class, () -> matchService.buyTicket(match.getMatchID(), "1", "a", 2));
            assertThrows(InputValidationException.class, () -> matchService.buyTicket(match.getMatchID(), "0000000000000000", "a", -1));
        } catch (InputValidationException e) {
            throw new RuntimeException(e);
        }
    }
    //endregion

    //region FUNC 5
    @Test
    void listUserTickets() {
        Match match = getValidMatch();

        try{
            match = matchService.addMatch(match);
            matchService.buyTicket(match.getMatchID(), "1010101010101010", "tuAndo247@gmail.com", 5);
            matchService.buyTicket(match.getMatchID(), "1010101010101010", "tuAndo247@gmail.com", 5);
            matchService.buyTicket(match.getMatchID(), "1010101010101010", "tuAndo247@gmail.com", 5);

            List<Ticket> ticketList = matchService.listUserTickets("tuAndo247@gmail.com");
            assertEquals(3, ticketList.size());

            List<Ticket> ticketList1 = matchService.listUserTickets("tuBisbi247365@gmail.com");
            assertEquals(0, ticketList1.size());

        } catch (InputValidationException | InstanceNotFoundException | MatchSoldOutException | MatchPlayedException e) {
            throw new RuntimeException(e);
        }
    }
    //endregion

    //region FUNC 6
    @Test
    void correctTicketClaim() {
        Match match = getValidMatch();
        try {
            match = matchService.addMatch(match);
            Long ticketId = matchService.buyTicket(match.getMatchID(), "1010101010101010", "tuQuique347@gmail.com", 5);

            Ticket ticket = matchService.ticketClaim(ticketId, "1010101010101010");
            assertTrue(ticket.isClaimed());
        } catch (InputValidationException | InstanceNotFoundException | ClaimingException | MatchSoldOutException |
                 MatchPlayedException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    void claimedTicketClaim(){
        DataSource dataSource = DataSourceLocator.getDataSource(ModelConstants.APP_DATA_SOURCE);
        Match match = getValidMatch();
        try{
            match = matchService.addMatch(match);
            Long ticketId = matchService.buyTicket(match.getMatchID(), "1010101010101010", "tuQuique347@gmail.com", 5);
            try(Connection connection = dataSource.getConnection()){
                Ticket ticket = ticketsDao.find(connection, ticketId);
                ticket.setClaimed(true);
                ticketsDao.update(connection, ticket);

                assertThrows(ClaimingException.class, () -> matchService.ticketClaim(ticketId, "1010101010101010"));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (InputValidationException | InstanceNotFoundException | MatchSoldOutException | MatchPlayedException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    void instanceNotFoundTicketClaim(){
        assertThrows(InstanceNotFoundException.class, () -> matchService.ticketClaim(1L, "1010101010101010"));
    }
    @Test
    void inputValidationTicketClaim(){
        assertThrows(InputValidationException.class, () -> matchService.ticketClaim(1L, "1"));
    }
    //endregion
}
