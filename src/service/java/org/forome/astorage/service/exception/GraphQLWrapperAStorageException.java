package org.forome.astorage.service.exception;

import graphql.language.SourceLocation;
import org.forome.astorage.core.exception.AStorageException;

import java.util.List;
import java.util.Map;

public class GraphQLWrapperAStorageException extends AStorageException {

    private List<SourceLocation> sourceLocations;

    public GraphQLWrapperAStorageException(AStorageException astorageException) {
        this(astorageException, null);
    }

    public GraphQLWrapperAStorageException(AStorageException astorageException, List<SourceLocation> sourceLocations) {
        super("wrapper", null, null, astorageException);
        this.sourceLocations = sourceLocations;
    }

    public AStorageException getAStorageException() {
        return (AStorageException) getCause();
    }

    public List<SourceLocation> getSourceLocations() {
        return sourceLocations;
    }
}
