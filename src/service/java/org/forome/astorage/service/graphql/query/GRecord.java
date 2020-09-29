package org.forome.astorage.service.graphql.query;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import org.forome.core.struct.Chromosome;

@GraphQLName("record")
public class GRecord {

    private final Chromosome chromosome;

    public GRecord(Chromosome chromosome) {
        this.chromosome = chromosome;
    }

    @GraphQLField
    @GraphQLName("chromosome")
    public GChromosome getChromosome() {
        return GChromosome.convert(chromosome);
    }
}
