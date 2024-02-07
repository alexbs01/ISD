namespace java es.udc.ws.match.thrift

struct ThriftMatchDto {
    1: i64 matchId
    2: string visitorName
    3: string matchDate
    4: i16 ticketPrice
    5: i16 maxTickets
    6: i16 ticketsSold

}

struct ThriftTicketDto {
    1: i64      ticketId
    2: string   creditCard
    3: string   mail
    4: i32      numberEntries
    5: string   saleTime
    6: bool     claim
    7: i64      matchId
}

exception ThriftInputValidationException {
    1: string message
}

exception ThriftInstanceNotFoundException {
    1: string instanceId
    2: string instanceType
}

exception ThriftSoldOutException{
    1: i64 matchId
}

exception ThriftMatchPlayedException {
    1: i64 matchId
}

exception ThriftClaimingException {
    1: i64 ticketId
}

exception ThriftMatchNotRemovableException {
    1: i64 matchId
}
service ThriftMatchService {

   ThriftMatchDto addMatch(1: ThriftMatchDto matchDto) throws (1: ThriftInputValidationException e)

   void updateMatch(1: ThriftMatchDto matchDto) throws (1: ThriftInputValidationException e, 2: ThriftInstanceNotFoundException ee)

   void removeMatch(1: i64 matchId) throws (1: ThriftInstanceNotFoundException e, 2: ThriftMatchNotRemovableException ee)

   list<ThriftMatchDto> findMatchById(1: i64 matchId);

   list<ThriftMatchDto> findByDate(1: string endDate)

   list<ThriftTicketDto> listUserTickets (1: string mail)

   i64 buyTicket (1: i64 matchId, 2: string creditCard, 3: string mail, 4: i32 numberEntries) throws (1: ThriftInputValidationException e, 2: ThriftInstanceNotFoundException ee, 3: ThriftSoldOutException eee, 4: ThriftMatchPlayedException eeee)

   ThriftTicketDto ticketClaim (1: i64 ticketId, 2: string creditCard) throws (1: ThriftInstanceNotFoundException e, 2: ThriftInputValidationException ee, 3: ThriftClaimingException eee)

}
