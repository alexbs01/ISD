package es.udc.ws.app.client.service.thrift;

import es.udc.ws.app.client.service.ClientMatchService;
import es.udc.ws.app.client.service.dto.ClientMatchDto;
import es.udc.ws.app.client.service.dto.ClientTicketDto;
import es.udc.ws.app.client.service.exceptions.ClaimingException;
import es.udc.ws.app.client.service.exceptions.ClientMatchNotRemovableException;
import es.udc.ws.app.client.service.exceptions.MatchPlayedException;
import es.udc.ws.app.client.service.exceptions.MatchSoldOutException;
import es.udc.ws.match.thrift.*;
import es.udc.ws.util.configuration.ConfigurationParametersManager;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class ThriftClientMatchService implements ClientMatchService {

    private final static String ENDPOINT_ADDRESS_PARAMETER =
            "ThriftClientMatchService.endpointAddress";

    private final static String endpointAddress =
            ConfigurationParametersManager.getParameter(ENDPOINT_ADDRESS_PARAMETER);
    @Override
    public Long addMatch(ClientMatchDto match) throws InputValidationException {
        ThriftMatchService.Client client = getClient();


        try (TTransport transport = client.getInputProtocol().getTransport()) {

            transport.open();

            return client.addMatch(ClientMatchDtoToThriftMatchDtoConversor.toThriftMatchDto(match)).getMatchId();

        } catch (ThriftInputValidationException e) {
            throw new InputValidationException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateMatch(ClientMatchDto match) throws InputValidationException, InstanceNotFoundException {
        ThriftMatchService.Client client = getClient();

        try (TTransport transport = client.getInputProtocol().getTransport()) {

            transport.open();
            client.updateMatch(ClientMatchDtoToThriftMatchDtoConversor.toThriftMatchDto(match));

        } catch (ThriftInputValidationException e) {
            throw new InputValidationException(e.getMessage());
        } catch (ThriftInstanceNotFoundException e) {
            throw new InstanceNotFoundException(e.getInstanceId(), e.getInstanceType());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeMatch(Long matchId) throws InstanceNotFoundException, ClientMatchNotRemovableException {

        ThriftMatchService.Client client = getClient();

        try (TTransport transport = client.getInputProtocol().getTransport()) {

            transport.open();
            client.removeMatch(matchId);

        } catch (ThriftInstanceNotFoundException e) {
            throw new InstanceNotFoundException(e.getInstanceId(), e.getInstanceType());
        } catch (ThriftMatchNotRemovableException e) {
            throw new ClientMatchNotRemovableException(e.getMatchId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ClientMatchDto> findMatchById(Long matchId) throws InstanceNotFoundException, SQLException {
        ThriftMatchService.Client client = getClient();

        try(TTransport transport = client.getInputProtocol().getTransport()) {
            transport.open();

            return ClientMatchDtoToThriftMatchDtoConversor.toClientMatchDtos(client.findMatchById(matchId));
        } catch (TException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ClientMatchDto> findMatchByDates(LocalDateTime date) {
        ThriftMatchService.Client client = getClient();

        try (TTransport transport = client.getInputProtocol().getTransport()) {

            transport.open();

            System.out.println(date.toString());


            return ClientMatchDtoToThriftMatchDtoConversor.toClientMatchDtos(client.findByDate(date.toString()));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Long buyTicket(Long matchId, String creditCard, String mail, Integer numberEntries) throws InstanceNotFoundException, MatchSoldOutException, InputValidationException, MatchPlayedException {
        ThriftMatchService.Client client = getClient();
        try (TTransport transport = client.getInputProtocol().getTransport()){
            transport.open();
            return client.buyTicket(matchId, creditCard, mail, numberEntries);
        } catch (ThriftSoldOutException e) {
            throw new MatchSoldOutException(e.matchId);
        } catch (ThriftMatchPlayedException e) {
            throw new MatchPlayedException(e.matchId);
        } catch (ThriftInputValidationException e) {
            throw new InputValidationException(e.message);
        } catch (ThriftInstanceNotFoundException e) {
            throw new InstanceNotFoundException(e.getInstanceId(), e.getInstanceType());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ClientTicketDto ticketClaim(Long ticketId, String creditCard) throws InstanceNotFoundException, ClaimingException, InputValidationException {
        ThriftMatchService.Client client = getClient();
        try (TTransport transport = client.getInputProtocol().getTransport()){
            transport.open();
            ThriftTicketDto ticketDto = client.ticketClaim(ticketId, creditCard);
            return ClientTicketDtoToThriftTicketDtoConversor.toClientTicketDto(ticketDto);

        } catch (ThriftInputValidationException e) {
            throw new InputValidationException(e.message);
        } catch (ThriftClaimingException e) {
            throw new ClaimingException(e.ticketId);
        } catch (ThriftInstanceNotFoundException e) {
            throw new InstanceNotFoundException(e.getInstanceId(), e.getInstanceType());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ClientTicketDto> listUserTickets(String mail) {
        ThriftMatchService.Client client = getClient();

        try(TTransport transport = client.getInputProtocol().getTransport()) {
            transport.open();
            List<ThriftTicketDto> ticketDto = client.listUserTickets(mail);

            return ClientTicketDtoToThriftTicketDtoConversor.toClientTicketDto(ticketDto);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ThriftMatchService.Client getClient() {

        try {

            TTransport transport = new THttpClient(endpointAddress);
            TProtocol protocol = new TBinaryProtocol(transport);

            return new ThriftMatchService.Client(protocol);

        } catch (TTransportException e) {
            throw new RuntimeException(e);
        }

    }

}
