package org.forome.astorage.service;

import org.forome.astorage.service.graphql.GraphQLService;
import org.forome.astorage.service.network.NetworkService;
import org.forome.astorage.service.network.exception.NetworkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Service {

    private final static Logger log = LoggerFactory.getLogger(Service.class);

    private static Service instance = null;

    private final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    private final NetworkService networkService;
    private final GraphQLService graphQLService;

    public Service(int port, Thread.UncaughtExceptionHandler uncaughtExceptionHandler) throws NetworkException {
        instance = this;

        this.uncaughtExceptionHandler = uncaughtExceptionHandler;

        this.networkService = new NetworkService(port, uncaughtExceptionHandler);
        this.graphQLService = new GraphQLService();
    }

    public Thread.UncaughtExceptionHandler getUncaughtExceptionHandler() {
        return uncaughtExceptionHandler;
    }

    public GraphQLService getGraphQLService() {
        return graphQLService;
    }

    public void stop() throws NetworkException {
        networkService.stop();
        instance = null;
    }

    public static Service getInstance() {
        return instance;
    }
}
