package org.forome.astorage.service.graphql.query;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import org.forome.astorage.service.graphql.query.source.GDatabase;
import org.forome.astorage.service.graphql.scalar.GAssembly;

@GraphQLName("query")
public class GQuery {

    @GraphQLField
    @GraphQLName("database")
    public static GDatabase getDatabase(
            @GraphQLNonNull
            @GraphQLName("assembly") GAssembly gAssembly
    ) {
        return new GDatabase(gAssembly.assembly);
    }
}
