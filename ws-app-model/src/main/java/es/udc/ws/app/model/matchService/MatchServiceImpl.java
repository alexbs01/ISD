package es.udc.ws.app.model.matchService;


import es.udc.ws.app.model.match.Match;
import es.udc.ws.app.model.match.SqlMatchDao;
import es.udc.ws.app.model.match.SqlMatchDaoFactory;
import es.udc.ws.app.model.matchService.exceptions.ClaimingException;
import es.udc.ws.app.model.matchService.exceptions.MatchNotRemovableException;
import es.udc.ws.app.model.matchService.exceptions.MatchPlayedException;
import es.udc.ws.app.model.matchService.exceptions.MatchSoldOutException;
import es.udc.ws.app.model.tickets.SqlTicketsDao;
import es.udc.ws.app.model.tickets.SqlTicketsDaoFactory;
import es.udc.ws.app.model.tickets.Ticket;
import es.udc.ws.app.model.util.ModelConstants;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.sql.DataSourceLocator;
import es.udc.ws.util.validation.PropertyValidator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class MatchServiceImpl implements MatchService {

    private SqlMatchDao matchDao = null;

    private SqlTicketsDao ticketsDao = null;

    private final DataSource dataSource;


    public MatchServiceImpl(){

        dataSource = DataSourceLocator.getDataSource(ModelConstants.APP_DATA_SOURCE);
        matchDao = SqlMatchDaoFactory.getDao();
        ticketsDao = SqlTicketsDaoFactory.getDao();

    }

    private void validateMatch(Match match) throws InputValidationException {

        PropertyValidator.validateMandatoryString("visitorName", match.getVisitorName());
        PropertyValidator.validateDouble("ticketPrice", match.getTicketPrice(),0,ModelConstants.MAX_PRICE);
        PropertyValidator.validateLong("maxTicketCount",match.getMaxTicketCount(),1,ModelConstants.MAX_TICKETS);

        if(match.getMatchDate().isBefore(LocalDateTime.now())) throw new InputValidationException("Invalid date");


    }

    @Override
    public Match addMatch(Match match) throws InputValidationException {


        validateMatch(match);
        match.setCreationDate(LocalDateTime.now());

        try (Connection connection = dataSource.getConnection()) {

            try {

                /* Prepare connection. */
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                /* Do work. */
                Match createdMatch = matchDao.create(connection, match);

                /* Commit. */
                connection.commit();

                return createdMatch;

            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);
            } catch (RuntimeException | Error e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }    }

    @Override
    public void updateMatch(Match match) throws InputValidationException, InstanceNotFoundException {
        validateMatch(match);

        try (Connection connection = dataSource.getConnection()) {

            try {

                /* Prepare connection. */
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                /* Do work. */
                matchDao.update(connection, match);

                /* Commit. */
                connection.commit();

            } catch (InstanceNotFoundException e) {
                connection.commit();
                throw e;
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);
            } catch (RuntimeException | Error e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<Match> findMatchByDates(LocalDateTime startDate, LocalDateTime endDate)  {

        try (Connection connection = dataSource.getConnection()) {
            return matchDao.findByDate(connection, startDate, endDate);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public Match findMatchById(Long matchID) throws InstanceNotFoundException{

        try(Connection connection = dataSource.getConnection()) {
            return matchDao.find(connection, matchID);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    @Override
    public List<Ticket> listUserTickets(String mail) {
        try (Connection connection = dataSource.getConnection()){
            return ticketsDao.listTicketsByUser(connection, mail);
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Ticket ticketClaim(Long idTicket, String creditCard) throws InstanceNotFoundException, ClaimingException, InputValidationException {
        if(creditCard.length()!=16) throw new InputValidationException("Not valid credit card");
        try (Connection connection = dataSource.getConnection()){
            try {
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                Ticket ticket = ticketsDao.find(connection, idTicket);
                if (!Objects.equals(ticket.getCreditCard(), creditCard)){
                    throw new InputValidationException("Incorrect credit card number");
                }
                if (ticket.isClaimed()) {
                    throw new ClaimingException(ticket.getTicketId());
                }
                ticket.setClaimed(true);
                ticketsDao.update(connection, ticket);

                connection.commit();

                return ticket;

            } catch (InstanceNotFoundException | ClaimingException | InputValidationException e) {
                connection.commit();
                throw e;
            } catch (RuntimeException | Error e) {
                connection.rollback();
                throw e;
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeMatch(Long matchId) throws InstanceNotFoundException, MatchNotRemovableException {

        try (Connection connection = dataSource.getConnection()) {

            try {

                /* Prepare connection. */
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                /* Do work. */
                if (ticketsDao.existsByMatchId(connection, matchId)) {
                    throw new MatchNotRemovableException(matchId);
                }


                matchDao.remove(connection, matchId);

                /* Commit. */
                connection.commit();

            } catch (InstanceNotFoundException | MatchNotRemovableException e) {
                connection.commit();
                throw e;
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);
            } catch (RuntimeException | Error e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Long buyTicket(Long matchId, String creditCard, String mail, Integer numberEntries) throws InstanceNotFoundException, MatchSoldOutException, InputValidationException, MatchPlayedException {
        LocalDateTime saleTime = LocalDateTime.now();
        if(creditCard.length()!=16) throw new InputValidationException("Not valid credit card");
        if(numberEntries<=0) throw new InputValidationException("Not valid number of tickets");
        if(mail.isEmpty() || mail.contains(" "))  throw new InputValidationException("Email cant be empty");
        try (Connection connection = dataSource.getConnection()){
            try {
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                Ticket ticket = new Ticket(creditCard, mail, numberEntries, saleTime);
                Match match = matchDao.find(connection, matchId);
                if (match.getMatchDate().isBefore(saleTime)){
                    throw new MatchPlayedException(matchId,match.getMatchDate());
                }
                match.setTicketsSold(match.getTicketsSold() + numberEntries);
                if (match.getTicketsSold() > match.getMaxTicketCount()){
                    throw new MatchSoldOutException(matchId);
                }
                if (LocalDateTime.now().isAfter(match.getMatchDate())) {
                    throw new MatchPlayedException(matchId,match.getMatchDate());
                }


                try {
                    matchDao.update(connection, match);
                    ticket = ticketsDao.create(connection, ticket, matchId);
                }catch (InstanceNotFoundException e){
                    connection.rollback();
                    throw e;
                }

                connection.commit();

                return ticket.getTicketId();
            } catch (InstanceNotFoundException | MatchSoldOutException | MatchPlayedException e) {
                connection.commit();
                throw e;
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);
            } catch (RuntimeException | Error e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
