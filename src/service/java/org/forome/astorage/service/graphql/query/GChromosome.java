package org.forome.astorage.service.graphql.query;

import graphql.annotations.annotationTypes.GraphQLName;
import org.forome.core.struct.Chromosome;

@GraphQLName("chromosome")
public enum GChromosome {

    CHR_1("1"),
    CHR_2("2"),
    CHR_3("3"),
    CHR_4("4"),
    CHR_5("5"),
    CHR_6("6"),
    CHR_7("7"),
    CHR_8("8"),
    CHR_9("9"),
    CHR_10("10"),
    CHR_11("11"),
    CHR_12("12"),
    CHR_13("13"),
    CHR_14("14"),
    CHR_15("15"),
    CHR_16("16"),
    CHR_17("17"),
    CHR_18("18"),
    CHR_19("19"),
    CHR_20("20"),
    CHR_21("21"),
    CHR_22("22"),
    CHR_X("X"),
    CHR_Y("Y");

    private final String value;

    private GChromosome(String value) {
        this.value = value;
    }

    public Chromosome convert() {
        return Chromosome.of(value);
    }

    public static GChromosome convert(Chromosome chromosome) {
        for (GChromosome item: GChromosome.values()) {
            if (item.value.equals(chromosome.getChar())) {
                return item;
            }
        }
        throw new RuntimeException();
    }
}
