package org.forome.astorage.service.graphql.query;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import org.forome.astorage.service.graphql.query.source.GSource;
import org.forome.astorage.service.graphql.scalar.GAssembly;

@GraphQLName("query")
public class GQuery {

    @GraphQLField
    @GraphQLName("source")
    public static GSource getSource(
            @GraphQLNonNull
            @GraphQLName("assembly") GAssembly gAssembly
    ) {
        return new GSource(gAssembly.assembly);
    }
}
