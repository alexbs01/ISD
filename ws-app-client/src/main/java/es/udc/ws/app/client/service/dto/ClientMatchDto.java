package es.udc.ws.app.client.service.dto;

import java.time.LocalDateTime;

public class ClientMatchDto {

    public int getMaxTicketCount() {
        return maxTicketCount;
    }

    public String getVisitorName() {
        return visitorName;
    }

    public LocalDateTime getMatchDate() {
        return matchDate;
    }

    public float getTicketPrice() {
        return ticketPrice;
    }

    public Long getMatchID() {
        return matchID;
    }

    public void setMatchID(long matchID) {
        this.matchID = matchID;
    }

    public int getTicketsSold() {
        return ticketsSold;
    }

    public void setTicketsSold(int ticketsSold) {
        this.ticketsSold = ticketsSold;
    }

    private  int maxTicketCount;
    private final String visitorName;

    private final LocalDateTime matchDate;
    private final float ticketPrice;
    private Long matchID;
    private int ticketsSold;


    public ClientMatchDto(int maxTicketCount, String visitorName, LocalDateTime matchDate, float ticketPrice , Long matchID, int ticketsSold) {
        this.maxTicketCount = maxTicketCount;
        this.visitorName = visitorName;
        this.matchDate = matchDate;
        this.ticketPrice = ticketPrice;
        this.matchID = matchID;
        this.ticketsSold = ticketsSold;
    }

    @Override
    public String toString() {
        return "RestMatchDto{" +
                "maxTicketCount=" + maxTicketCount +
                ", visitorName='" + visitorName + '\'' +
                ", matchDate=" + matchDate +
                ", ticketPrice=" + ticketPrice +
                ", matchID=" + matchID +
                ", ticketsSold=" + ticketsSold +
                '}';
    }


}
