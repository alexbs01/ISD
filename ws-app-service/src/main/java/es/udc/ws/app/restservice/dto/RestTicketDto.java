package es.udc.ws.app.restservice.dto;

import java.time.LocalDateTime;

public class RestTicketDto {
    private Long ticketId;
    private int numberEntries;
    private LocalDateTime saleTime;
    private boolean claimed;
    private Long matchId;
    private String creditCard;
    public RestTicketDto() {
    }

    public RestTicketDto(Long saleId, int numberEntries, LocalDateTime saleTime, boolean claimed, Long matchId, String creditCard) {
        this.matchId = matchId;
        this.ticketId = saleId;
        this.claimed = claimed;
        this.numberEntries = numberEntries;
        this.saleTime = saleTime;
        this.creditCard = creditCard;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
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

    public String getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(String creditCard) {
        this.creditCard = creditCard;
    }
}
