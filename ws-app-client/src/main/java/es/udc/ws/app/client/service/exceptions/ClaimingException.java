package es.udc.ws.app.client.service.exceptions;

public class ClaimingException extends Exception{
    private long ticketId;
    public ClaimingException(long ticketId){
        super("Ticket with id=\""+ticketId+" is already claimed");
        this.ticketId=ticketId;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(long ticketId) {
        this.ticketId = ticketId;
    }
}
