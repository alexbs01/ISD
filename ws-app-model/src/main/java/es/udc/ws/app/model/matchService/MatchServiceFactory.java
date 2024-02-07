package es.udc.ws.app.model.matchService;

import es.udc.ws.util.configuration.ConfigurationParametersManager;

public class MatchServiceFactory {
    private final static String CLASS_NAME_PARAMETER = "MatchServiceFactory.className";
    private static es.udc.ws.app.model.matchService.MatchService service = null;

    private MatchServiceFactory() {
    }

    @SuppressWarnings("rawtypes")
    private static MatchService getInstance() {
        try {
            String serviceClassName = ConfigurationParametersManager
                    .getParameter(CLASS_NAME_PARAMETER);
            Class serviceClass = Class.forName(serviceClassName);
            return (MatchService) serviceClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public synchronized static MatchService getService() {

        if (service == null) {
            service = getInstance();
        }
        return service;

    }
}
