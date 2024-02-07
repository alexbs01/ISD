package es.udc.ws.app.model.tickets;

import java.time.LocalDateTime;
import java.util.Objects;

public class Ticket {
    private long ticketId;
    private String creditCard;
    private String email;
    private int numberEntries;
    private LocalDateTime saleTime;
    private boolean claimed;
    private long matchId;

    public Ticket(String creditCard, String email, int numberEntries){
        this.creditCard = creditCard;
        this.email = email;
        this.numberEntries = numberEntries;
        this.saleTime = null;
        this.claimed = false;
        this.matchId = -1;
    }
    public Ticket(String creditCard, String email, int numberEntries, LocalDateTime saleTime){
        this(creditCard, email, numberEntries);
        this.saleTime = saleTime;
    }
    public Ticket(long ticketId, String creditCard, String email, int numberEntries, LocalDateTime saleTime, boolean claimed, long matchId) {
        this(creditCard, email, numberEntries, saleTime);
        this.ticketId = ticketId;
        this.claimed = claimed;
        this.matchId = matchId;
    }

        @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return ticketId == ticket.ticketId && Objects.equals(creditCard, ticket.creditCard) && numberEntries == ticket.numberEntries && claimed == ticket.claimed && Objects.equals(email, ticket.email) && Objects.equals(saleTime, ticket.saleTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticketId, creditCard, email, numberEntries, saleTime, claimed);
    }

    public long getTicketId() {
        return ticketId;
    }

    public void setTicketId(long ticketId) {
        this.ticketId = ticketId;
    }

    public String getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(String creditCard) {
        this.creditCard = creditCard;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getNumberEntries() {
        return numberEntries;
    }

    public void setNumberEntries(int numberEntries) {
        this.numberEntries = numberEntries;
    }

    public LocalDateTime getSaleTime() {
        return saleTime;
    }

    public void setSaleTime(LocalDateTime saleTime) {
        this.saleTime = saleTime;
    }

    public boolean isClaimed() {
        return claimed;
    }

    public void setClaimed(boolean claimed) {
        this.claimed = claimed;
    }

    public long getMatchId() {
        return matchId;
    }

    public void setMatchId(long matchId) {
        this.matchId = matchId;
    }
}
