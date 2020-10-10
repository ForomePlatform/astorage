package org.forome.astorage.service.graphql;

import graphql.*;
import graphql.annotations.AnnotationsSchemaCreator;
import graphql.language.SourceLocation;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLType;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.forome.astorage.core.exception.AStorageException;
import org.forome.astorage.service.exception.GraphQLWrapperAStorageException;
import org.forome.astorage.service.exception.ServiceExceptionBuilder;
import org.forome.astorage.service.graphql.query.GQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphQLService {

    private final GraphQL graphQL;

    public GraphQLService() {
        GraphQLSchema graphQLSchema = AnnotationsSchemaCreator.newAnnotationsSchema()
                .query(GQuery.class)
                .build();

        graphQL = GraphQL
                .newGraphQL(graphQLSchema)
                .build();
    }

    public GraphQLResponse<JSONObject> execute(ExecutionInput executionInput) {
        ExecutionResult executionResult = graphQL.execute(executionInput);

        if (!executionResult.getErrors().isEmpty()) {
            GraphQLWrapperAStorageException storageException = coercionGraphQLSubsystemException(executionResult.getErrors().get(0));
            return buildResponse(storageException);
        } else {
            return new GraphQLResponse<>(new JSONObject(executionResult.getData()), false);
        }
    }

    private static GraphQLResponse<JSONObject> buildResponse(GraphQLWrapperAStorageException storageException) {
        AStorageException e = storageException.getAStorageException();
        List<SourceLocation> sourceLocations = storageException.getSourceLocations();

        JSONObject error = new JSONObject();

        error.put("code", e.getCode());

        if (e.getParameters() != null && !e.getParameters().isEmpty()) {
            JSONObject outParameters = new JSONObject();
            for (Map.Entry<String, Object> entry : e.getParameters().entrySet()) {
                outParameters.put(entry.getKey(), entry.getValue());
            }
            error.put("parameters", outParameters);
        }

        if (e.getComment() != null) {
            error.put("message", e.getComment());
        }

        if (sourceLocations != null) {
            JSONArray locations = new JSONArray();
            for (SourceLocation sourceLocation : sourceLocations) {
                locations.add(new JSONObject()
                        .appendField("line", sourceLocation.getLine())
                        .appendField("column", sourceLocation.getColumn())
                );
            }
            error.put("source_location", locations);
        }

        return new GraphQLResponse<>(error, true);
    }

    private static GraphQLWrapperAStorageException coercionGraphQLSubsystemException(GraphQLError graphQLError) {
        ErrorClassification errorType = graphQLError.getErrorType();

        AStorageException astorageException;
        if (errorType == ErrorType.InvalidSyntax) {
            astorageException = ServiceExceptionBuilder.buildGraphQLInvalidSyntaxException();
        } else if (errorType == ErrorType.ValidationError) {
            astorageException = ServiceExceptionBuilder.buildGraphQLValidationException();
        } else if (errorType == ErrorType.DataFetchingException) {
            ExceptionWhileDataFetching exceptionWhileDataFetching = (ExceptionWhileDataFetching) graphQLError;
            Throwable dataFetchingThrowable = getAStorageException(exceptionWhileDataFetching.getException());
            if (dataFetchingThrowable instanceof AStorageException) {
                astorageException = (AStorageException) dataFetchingThrowable;
            } else {
                throw new RuntimeException("Not support throwable", dataFetchingThrowable);
            }
        } else {
            throw new RuntimeException("Not support error type: " + graphQLError.getErrorType());
        }
        return new GraphQLWrapperAStorageException(astorageException, graphQLError.getLocations());
    }


    public static class GraphQLResponse<T> {
        public final T data;
        public final boolean error;

        public GraphQLResponse(T data, boolean error) {
            this.data = data;
            this.error = error;
        }
    }

    private static Throwable getAStorageException(Throwable throwable) {
        for (Throwable chainThrowable: ExceptionUtils.getThrowableList(throwable)) {
            if (chainThrowable instanceof AStorageException) {
                return chainThrowable;
            }
        }
        return throwable;
    }
}
