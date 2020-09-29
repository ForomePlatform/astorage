package org.forome.astorage.service.graphql.query;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;

@GraphQLName("query")
public class GQuery {

    @GraphQLField
    @GraphQLName("record")
    public static GRecord getRecord(
            @GraphQLName("chromosome") GChromosome chromosome
    ) {
        return new GRecord(chromosome.convert());
    }
}
