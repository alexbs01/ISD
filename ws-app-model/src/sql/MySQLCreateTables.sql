DROP TABLE Ticket;

DROP TABLE GameMatch;

-- --------------------------------- Match ------------------------------------
CREATE TABLE GameMatch (
    matchId BIGINT AUTO_INCREMENT PRIMARY KEY,
    matchDate DATETIME NOT NULL,
    visitorName VARCHAR(255) COLLATE latin1_bin NOT NULL,
    maxTicketCount INT NOT NULL CHECK (maxTicketCount > 0),
    ticketPrice FLOAT NOT NULL CHECK (ticketPrice > 0),
    registrationDate DATETIME NOT NULL,
    ticketsSold INT NOT NULL,
    CONSTRAINT ticketsSold_must_be_greater_than_0 CHECK (ticketsSold >= 0),
    CONSTRAINT ticketsSold_must_be_lower_than_maxTicketCount CHECK  (ticketsSold <= maxTicketCount),
    CONSTRAINT matchDate_must_be_after_registrationDate CHECK (matchDate > registrationDate)
) ENGINE = InnoDB;

CREATE TABLE Ticket (
    ticketId BIGINT AUTO_INCREMENT PRIMARY KEY,
    creditCard VARCHAR(16) NOT NULL,
    email VARCHAR(255) COLLATE latin1_bin NOT NULL,
    numberEntries INT NOT NULL,
    saleTime DATETIME NOT NULL,
    claimed BOOL DEFAULT FALSE,
    matchId BIGINT NOT NULL,
    CONSTRAINT matchId_must_exist_on_GameMatch FOREIGN KEY (matchId) REFERENCES GameMatch(matchId),
    CONSTRAINT numberEntries_must_be_greater_than_0 CHECK (numberEntries > 0),
    CONSTRAINT creditCard_must_have_16_digits CHECK (LENGTH(creditCard) = 16)
) ENGINE = InnoDB;