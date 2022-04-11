package org.forome.astorage.service;

import org.forome.database.exception.DatabaseException;
import org.forome.astorage.service.astorage.AStorageService;
import org.forome.astorage.service.config.Config;
import org.forome.astorage.service.graphql.GraphQLService;
import org.forome.astorage.service.network.NetworkService;
import org.forome.astorage.service.network.exception.NetworkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class Service {

    private final static Logger log = LoggerFactory.getLogger(Service.class);

    private static Service instance = null;

    private final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    private final Config config;

    private final AStorageService aStorageService;

    private final NetworkService networkService;
    private final GraphQLService graphQLService;

    public Service(Path configFile, Thread.UncaughtExceptionHandler uncaughtExceptionHandler) throws NetworkException, DatabaseException {
        instance = this;

        this.uncaughtExceptionHandler = uncaughtExceptionHandler;

        this.config = new Config(configFile);

        this.aStorageService = new AStorageService(this);

        this.networkService = new NetworkService(config.port, uncaughtExceptionHandler);
        this.graphQLService = new GraphQLService();
    }

    public Thread.UncaughtExceptionHandler getUncaughtExceptionHandler() {
        return uncaughtExceptionHandler;
    }

    public Config getConfig() {
        return config;
    }

    public AStorageService getAStorageService() {
        return aStorageService;
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
