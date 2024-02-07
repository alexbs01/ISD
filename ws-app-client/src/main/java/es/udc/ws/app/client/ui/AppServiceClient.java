package es.udc.ws.app.client.ui;

import es.udc.ws.app.client.service.ClientMatchService;
import es.udc.ws.app.client.service.ClientMatchServiceFactory;
import es.udc.ws.app.client.service.dto.ClientMatchDto;
import es.udc.ws.app.client.service.dto.ClientTicketDto;

import java.time.LocalDateTime;
import java.util.List;

public class AppServiceClient {
    public static void main(String[] args) {

        if(args.length == 0) {
            printUsageAndExit();
        }
        ClientMatchService clientMatchService = ClientMatchServiceFactory.getService();
        if("-addMatch".equalsIgnoreCase(args[0])) {
            validateArgs(args, 5, new int[] {3,4});

            try {
                Long matchId = clientMatchService.addMatch(new ClientMatchDto(Integer.parseInt(args[4]), args[1], LocalDateTime.parse(args[2]), Float.parseFloat(args[3]),null,0));

                System.out.println("Match with ID " + matchId + " created sucessfully");

            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }

        } else if("-findMatches".equalsIgnoreCase(args[0])) {
            validateArgs(args, 2, new int[] {});

            try {
                String date = args[1].replace('\'', '\0').trim()+"";
                date = date.replace('\'', '\0').trim()+"T00:00:00.000000";
                List<ClientMatchDto> matches = clientMatchService.findMatchByDates(LocalDateTime.parse(date));
                System.out.println("Found " + matches.size() +
                        " match(es) between now and '" + args[1] + "'");
                for (ClientMatchDto matchDto : matches) {
                    System.out.println(matchDto.toString());
                }
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }

        } else if("-findMatch".equalsIgnoreCase(args[0])) {
            validateArgs(args, 2, new int[] {1});

            try {
                System.out.println(args[0] + " " + args[1]);
                List<ClientMatchDto> matchDto = clientMatchService.findMatchById(Long.parseLong(args[1]));

                System.out.println(matchDto.toString());

            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }

        }  else if("-buy".equalsIgnoreCase(args[0])) {
            validateArgs(args, 5, new int[] {1});

            // [buy]    MatchServiceClient -b <matchId> <creditCard> <mail> <numberEntries>

            Long ticketId;
            try {
                ticketId = clientMatchService.buyTicket(
                        Long.parseLong(args[1]),
                        args[4],
                        args[2],
                        Integer.parseInt(args[3]));

                System.out.println("Ticket for match " + args[1] +
                        " purchased sucessfully with ticket number " +
                        ticketId);

            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }

        } else if("-collect".equalsIgnoreCase(args[0])) {
            validateArgs(args, 3, new int[] {1});

            // [calim]  -collect <purchaseId> <cardNumber>

            try {
                ClientTicketDto ticketDto = clientMatchService.ticketClaim(Long.parseLong(args[1]), args[2]);

                System.out.println("Ticket "+ ticketDto.getTicketId() +" claimed");
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        } else if("-findPurchases".equalsIgnoreCase(args[0])) {
            validateArgs(args, 2, new int[] {});

            try {
                List<ClientTicketDto> ticketDtos = clientMatchService.listUserTickets(args[1]);

                System.out.println("User "+ args[1] +" tickets are:");
                for (ClientTicketDto ticket : ticketDtos) {
                    System.out.println("\t id: " + ticket.getTicketId());
                    System.out.println("\t match id: " + ticket.getMatchId());
                    System.out.println("\t credit card: " + "************" +ticket.getCreditCard());
                    System.out.println("\t number of entries: " + ticket.getNumberEntries());
                    System.out.println("\t sale time: " + ticket.getSaleTime());
                    System.out.println("\t state: " + (ticket.isClaimed()?"claimed":"pending") + "\n");
                }
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        }
    }

    public static void validateArgs(String[] args, int expectedArgs,
                                    int[] numericArguments) {
        if(expectedArgs != args.length) {
            printUsageAndExit();
        }
        for (int position : numericArguments) {
            try {
                Double.parseDouble(args[position]);
            } catch (NumberFormatException n) {
                printUsageAndExit();
            }
        }
    }

    public static void printUsageAndExit() {
        printUsage();
        System.exit(-1);
    }

    public static void printUsage() {
        System.err.println("""
                Usage:
                   [add]        MatchServiceClient -addMatch '<visitor>' '<date>' <ticketPrice> <maxTicketCount>
                   [findMatch]  MatchServiceClient -findMatchById <matchId>
                   [findByDate] MatchServiceClient -findByDate '<date>'
                   [buy]        MatchServiceClient -buy <matchId> '<cardNumber>' '<userEmail>' <numTickets>
                   [collect]    MatchServiceClient -collect <ticketId> '<cardNumber>'
                   [list]       MatchServiceClient -findPurchases <userEmail>
                """);
    }
}