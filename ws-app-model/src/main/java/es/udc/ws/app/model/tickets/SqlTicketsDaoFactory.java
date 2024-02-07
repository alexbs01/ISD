package es.udc.ws.app.model.tickets;

import es.udc.ws.util.configuration.ConfigurationParametersManager;

public class SqlTicketsDaoFactory {

    private final static String CLASS_NAME_PARAMETER = "SqlTicketsDaoFactory.className";
    private static SqlTicketsDao dao = null;

    private SqlTicketsDaoFactory() {
    }

    @SuppressWarnings("rawtypes")
    private static SqlTicketsDao getInstance() {
        try {
            String daoClassName = ConfigurationParametersManager
                    .getParameter(CLASS_NAME_PARAMETER);
            Class daoClass = Class.forName(daoClassName);
            return (SqlTicketsDao) daoClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public synchronized static SqlTicketsDao getDao() {

        if (dao == null) {
            dao = getInstance();
        }
        return dao;

    }

}
