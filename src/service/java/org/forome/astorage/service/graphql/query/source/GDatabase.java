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

package org.forome.astorage.service.graphql.query.source;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import org.forome.astorage.service.graphql.query.source.dataposition.GDataPosition;
import org.forome.astorage.service.graphql.query.source.dataposition.GGroupDataPosition;
import org.forome.astorage.service.graphql.scalar.GAssembly;
import org.forome.astorage.service.graphql.scalar.GChromosome;
import org.forome.core.struct.Assembly;
import org.forome.core.struct.Chromosome;
import org.forome.core.struct.Interval;
import org.forome.core.struct.Position;

@GraphQLName("source")
public class GDatabase {

    private final Assembly assembly;

    public GDatabase(Assembly assembly) {
        this.assembly = assembly;
    }

    @GraphQLField
    @GraphQLName("by_position")
    public GDataPosition getDataByPosition(
            @GraphQLNonNull
            @GraphQLName("chromosome") GChromosome gChromosome,
            @GraphQLNonNull
            @GraphQLName("position") int position
    ) {
        return new GDataPosition(
                assembly,
                new Position(gChromosome.convert(), position)
        );
    }

    @GraphQLField
    @GraphQLName("by_positions")
    public GGroupDataPosition getDataByPosition(
            @GraphQLNonNull
            @GraphQLName("chromosome") GChromosome gChromosome,
            @GraphQLNonNull
            @GraphQLName("start") int start,
            @GraphQLNonNull
            @GraphQLName("end") int end
    ) {
        return new GGroupDataPosition(
                assembly,
                Interval.of(gChromosome.convert(), start, end)
        );
    }
}
