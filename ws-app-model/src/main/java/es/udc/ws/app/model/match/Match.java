package es.udc.ws.app.model.match;

import java.time.LocalDateTime;
import java.util.Objects;

public class Match {

    private final int maxTicketCount;
    private final String visitorName;
    private final LocalDateTime matchDate;
    private final float ticketPrice;
    private LocalDateTime registrationDate;
    private long matchID;
    private int ticketsSold;



    public Match(String visitorName, LocalDateTime matchDate, float ticketPrice, int maxTicketCount) {

        this.visitorName = visitorName;
        this.matchDate = matchDate;
        this.ticketPrice = ticketPrice;
        this.maxTicketCount = maxTicketCount;
    }

    public Match(long matchID, String visitorName, LocalDateTime matchDate, float ticketPrice, int maxTicketCount) {

        this(visitorName, matchDate, ticketPrice, maxTicketCount);
        this.matchID = matchID;

    }

    public Match(long matchID, String visitorName, LocalDateTime matchDate, float ticketPrice, int maxTicketCount, int ticketsSold, LocalDateTime registrationDate) {
        this(matchID, visitorName, matchDate, ticketPrice, maxTicketCount);
        this.ticketsSold = ticketsSold;
        this.registrationDate = (registrationDate != null) ? registrationDate.withNano(0) : null;
    }

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

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public long getMatchID() {
        return matchID;
    }

    public int getTicketsSold() {
        return ticketsSold;
    }

    public void setCreationDate(LocalDateTime creationDate) {

        this.registrationDate = (creationDate != null) ? creationDate.withNano(0) : null;

    }

    public void setTicketsSold(int ticketsSold) {
        this.ticketsSold = ticketsSold;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Match match = (Match) o;

        int secondsDate1 = matchDate.getSecond();
        int secondsDate2 = match.matchDate.getSecond();
        int differenceSeconds;

        if(secondsDate1 <= secondsDate2) {
            differenceSeconds = secondsDate2 - secondsDate1;
        } else {
            differenceSeconds = secondsDate1 - secondsDate2;
        }


        return maxTicketCount == match.maxTicketCount &&
                Float.compare(ticketPrice, match.ticketPrice) == 0 &&
                matchID == match.matchID && ticketsSold == match.ticketsSold &&
                visitorName.equals(match.visitorName) &&
                matchDate.isEqual(match.matchDate) &&
                differenceSeconds <= 2;

    }
}
