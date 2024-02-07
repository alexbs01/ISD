package es.udc.ws.app.thrift;

import es.udc.ws.app.model.match.Match;
import es.udc.ws.app.model.matchService.MatchServiceFactory;
import es.udc.ws.app.model.matchService.exceptions.ClaimingException;
import es.udc.ws.app.model.matchService.exceptions.MatchNotRemovableException;
import es.udc.ws.app.model.matchService.exceptions.MatchPlayedException;
import es.udc.ws.app.model.matchService.exceptions.MatchSoldOutException;
import es.udc.ws.app.model.tickets.Ticket;
import es.udc.ws.match.thrift.*;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import org.apache.thrift.TException;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ThriftMatchServiceImpl implements ThriftMatchService.Iface{
    @Override
    public ThriftMatchDto addMatch(ThriftMatchDto matchDto) throws ThriftInputValidationException {
        Match match = MatchToThriftMatchDtoConversor.toMatch(matchDto);

        try {
            Match addedMatch = MatchServiceFactory.getService().addMatch(match);
            return MatchToThriftMatchDtoConversor.toThriftMatchDto(addedMatch);
        } catch (InputValidationException e) {
            throw new ThriftInputValidationException(e.getMessage());
        }
    }

    @Override
    public void updateMatch(ThriftMatchDto matchDto) throws ThriftInputValidationException, ThriftInstanceNotFoundException {
        Match match = MatchToThriftMatchDtoConversor.toMatch(matchDto);

        try {
            MatchServiceFactory.getService().updateMatch(match);
        } catch (InputValidationException e) {
            throw new ThriftInputValidationException(e.getMessage());
        } catch (InstanceNotFoundException e) {
            throw new ThriftInstanceNotFoundException(e.getInstanceId().toString(),
                    e.getInstanceType().substring(e.getInstanceType().lastIndexOf('.') + 1));
        }
    }

    @Override
    public void removeMatch(long matchId) throws ThriftInstanceNotFoundException, ThriftMatchNotRemovableException {
        try {
            MatchServiceFactory.getService().removeMatch(matchId);
        } catch (InstanceNotFoundException e) {
            throw new ThriftInstanceNotFoundException(e.getInstanceId().toString(),
                    e.getInstanceType().substring(e.getInstanceType().lastIndexOf('.') + 1));
        } catch (MatchNotRemovableException e) {
            throw new ThriftMatchNotRemovableException(e.getMatchId());
        }
    }

    @Override
    public List<ThriftMatchDto> findMatchById(long matchId) {
        try {
            Match match = MatchServiceFactory.getService().findMatchById(matchId);
            List<ThriftMatchDto> matchs = new ArrayList<>();

            matchs.add(MatchToThriftMatchDtoConversor.toThriftMatchDto(match));
            return matchs;
        } catch (InstanceNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ThriftMatchDto> findByDate(String endDate) throws TException {


        LocalDateTime date = LocalDateTime.parse(endDate, DateTimeFormatter.ISO_DATE_TIME);

        List<Match> matchs = MatchServiceFactory.getService().findMatchByDates(LocalDateTime.now(),date);

        return MatchToThriftMatchDtoConversor.toThriftMatchDtos(matchs);
    }

    @Override
    public List<ThriftTicketDto> listUserTickets(String mail) throws TException {

        List<Ticket> matches = MatchServiceFactory.getService().listUserTickets(mail);

        return TicketToThriftTicketDtoConversor.toThriftTicketDto(matches);
    }

    @Override
    public long buyTicket(long matchId, String creditCard, String mail, int numberEntries) throws ThriftInstanceNotFoundException, ThriftSoldOutException, ThriftMatchPlayedException, TException {
        try {
            return MatchServiceFactory.getService().buyTicket(matchId, creditCard, mail, numberEntries);

        }catch (InstanceNotFoundException e){
            throw new ThriftInstanceNotFoundException(e.getInstanceId().toString(),
                    e.getInstanceType().substring(e.getInstanceType().lastIndexOf('.')+1));
        }catch (InputValidationException e){
            throw new ThriftInputValidationException(e.getMessage());
        }catch (MatchSoldOutException e){
            throw new ThriftSoldOutException(e.getMatchId());
        }catch (MatchPlayedException e){
            throw new ThriftMatchPlayedException(e.getMatchId());
        }
    }

    @Override
    public ThriftTicketDto ticketClaim(long ticketId, String creditCard) throws ThriftInstanceNotFoundException, ThriftInputValidationException, ThriftClaimingException, TException {
        try {
            Ticket ticket = MatchServiceFactory.getService().ticketClaim(ticketId, creditCard);
            return TicketToThriftTicketDtoConversor.toThriftTicketDto(ticket);
        }catch (InstanceNotFoundException e){
            throw new ThriftInstanceNotFoundException(e.getInstanceId().toString(),
                    e.getInstanceType().substring(e.getInstanceType().lastIndexOf('.')+1));
        }catch (InputValidationException e){
            throw new ThriftInputValidationException(e.getMessage());
        }catch (ClaimingException e) {
            throw new ThriftClaimingException(e.getTicketId());
        }
    }
}
