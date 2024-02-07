package es.udc.ws.app.model.tickets.exceptions;

public class TooManyEntries extends Exception{
    private Long ticketId;

    public TooManyEntries(Long ticketId) {
        super("Ticket with id = " + ticketId + " can't be created because there aren't enought entries");
        this.ticketId = ticketId;
    }
}
