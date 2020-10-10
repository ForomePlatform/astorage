/*
 *  Copyright (c) 2020. Vladimir Ulitin, Partners Healthcare and members of Forome Association
 *
 *  Developed by Vladimir Ulitin and Michael Bouzinier
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 * 	 http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.forome.astorage.service.graphql.query.source.dataposition;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import org.forome.astorage.AStorage;
import org.forome.astorage.core.data.Conservation;
import org.forome.astorage.core.record.Record;
import org.forome.astorage.core.source.Source;
import org.forome.astorage.service.Service;
import org.forome.astorage.service.graphql.query.source.dataposition.conservation.GConservation;
import org.forome.astorage.service.graphql.scalar.GAssembly;
import org.forome.astorage.service.graphql.scalar.GChromosome;
import org.forome.astorage.service.graphql.scalar.GNucleotide;
import org.forome.core.struct.Assembly;
import org.forome.core.struct.Position;

@GraphQLName("data_position")
public class GDataPosition {

    public final Assembly assembly;
    public final Position position;

    public GDataPosition(Assembly assembly, Position position) {
        this.assembly = assembly;
        this.position = position;
    }

    @GraphQLField
    @GraphQLName("assembly")
    public GAssembly getAssembly() {
        return GAssembly.convert(assembly);
    }

    @GraphQLField
    @GraphQLName("chromosome")
    public GChromosome getChromosome() {
        return GChromosome.convert(position.chromosome);
    }

    @GraphQLField
    @GraphQLName("position")
    public int getPosition() {
        return position.value;
    }

    @GraphQLField
    @GraphQLName("conservation")
    public GConservation getConservation() {
        return new GConservation(this);
    }

    @GraphQLField
    @GraphQLName("nucleotide")
    public GNucleotide getNucleotide() {
        AStorage aStorage = Service.getInstance().getAStorageService().aStorage;
        Source source = aStorage.getSource(assembly);
        Record record = source.getRecord(position);
        return GNucleotide.convert(record.getNucleotide());
    }
}
