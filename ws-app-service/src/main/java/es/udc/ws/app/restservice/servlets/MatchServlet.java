package es.udc.ws.app.restservice.servlets;


import es.udc.ws.app.model.match.Match;
import es.udc.ws.app.model.matchService.MatchServiceFactory;
import es.udc.ws.app.model.matchService.exceptions.MatchNotRemovableException;
import es.udc.ws.app.restservice.dto.MatchToRestMatchDtoConversor;
import es.udc.ws.app.restservice.dto.RestMatchDto;
import es.udc.ws.app.restservice.json.AppExceptionToJsonConversor;
import es.udc.ws.app.restservice.json.JsonToRestMatchDtoConversor;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.servlet.RestHttpServletTemplate;
import es.udc.ws.util.servlet.ServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class MatchServlet extends RestHttpServletTemplate {

    @Override

    protected void processPost(HttpServletRequest req, HttpServletResponse resp) throws IOException,
            InputValidationException {

        ServletUtils.checkEmptyPath(req);

        RestMatchDto matchDto = JsonToRestMatchDtoConversor.toRestMatchDto(req.getInputStream());

        Match match = MatchToRestMatchDtoConversor.toMatch(matchDto);


        match = MatchServiceFactory.getService().addMatch(match);


        matchDto = MatchToRestMatchDtoConversor.toRestMatchDto(match);

        ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_CREATED, JsonToRestMatchDtoConversor.toObjectNode(matchDto), null);

    }


    @Override
    protected void processPut(HttpServletRequest req, HttpServletResponse resp) throws IOException,
            InputValidationException, InstanceNotFoundException {
        Long matchId = ServletUtils.getIdFromPath(req, "match");

        RestMatchDto matchDto = JsonToRestMatchDtoConversor.toRestMatchDto(req.getInputStream());
        if (!matchId.equals(matchDto.getMatchID())) {
            throw new InputValidationException("Invalid Request: invalid matchId");
        }
        Match match = MatchToRestMatchDtoConversor.toMatch(matchDto);

        MatchServiceFactory.getService().updateMatch(match);

        ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_NO_CONTENT, null, null);
    }

    @Override
    protected void processDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException,
            InputValidationException, InstanceNotFoundException {
        Long matchId = ServletUtils.getIdFromPath(req, "match");

        try {
            MatchServiceFactory.getService().removeMatch(matchId);
        } catch (MatchNotRemovableException ex) {
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_FORBIDDEN,
                    AppExceptionToJsonConversor.tomatchNotRemovableException(ex),
                    null);
            return;
        }

        ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_NO_CONTENT, null, null);
    }

    @Override
    protected void processGet(HttpServletRequest req, HttpServletResponse resp) throws IOException,
            InstanceNotFoundException, InputValidationException {

        List<Match> matches = new ArrayList<>();
        try {
            ServletUtils.checkEmptyPath(req);
            String dateParameter = req.getParameter("matchDate");

            if(dateParameter == null ||  dateParameter.isEmpty()) throw new InputValidationException("No date parameter");
            //Separar dates en dos strings separadas por | y transformar a date time


            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

            // Convertir el String a LocalDateTime utilizando el formato definido
            LocalDateTime dateStart = LocalDateTime.now();
            LocalDateTime dateEnd = LocalDateTime.parse(dateParameter, formatter);

            if (dateStart.isBefore(dateEnd)) {


                matches = MatchServiceFactory.getService().findMatchByDates(dateStart, dateEnd);

            } else {
                throw new InputValidationException("Invalid Request: invalid dates");
            }


        } catch (InputValidationException e) {

            if(e.getMessage().equals("Invalid Request: invalid dates") ){
                throw new InputValidationException("Invalid Request: invalid dates");
            }


            String[] url = req.getPathInfo().split("/");

            long id;

            try {
                try {
                    id = Long.parseLong(url[url.length - 1]);
                } catch(NumberFormatException | NullPointerException ex) {
                    ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                            null, null);
                    return;
                }


                matches.add(MatchServiceFactory.getService().findMatchById(id));

            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }


        List<RestMatchDto> matchesDto = MatchToRestMatchDtoConversor.toRestMatchDtos(matches);

        ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_OK,
                JsonToRestMatchDtoConversor.toArrayNode(matchesDto), null);
    }

}
