package es.udc.ws.app.client.service.dto;

import java.time.LocalDateTime;

public class ClientTicketDto {
    private long ticketId;
    private String creditCard;
    private String email;
    private int numberEntries;
    private LocalDateTime saleTime;
    private boolean claimed;
    private long matchId;

    public ClientTicketDto(){
    }

    public ClientTicketDto(long ticketId, String creditCard, String email, int numberEntries, LocalDateTime saleTime, boolean claimed, long matchId) {
        this.ticketId = ticketId;
        this.creditCard = creditCard;
        this.email = email;
        this.numberEntries = numberEntries;
        this.saleTime = saleTime;
        this.claimed = claimed;
        this.matchId = matchId;
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
