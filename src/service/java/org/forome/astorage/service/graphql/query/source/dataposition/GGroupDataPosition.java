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
import org.forome.astorage.core.record.Record;
import org.forome.astorage.core.source.Source;
import org.forome.astorage.service.Service;
import org.forome.astorage.service.graphql.scalar.GNucleotide;
import org.forome.core.struct.Assembly;
import org.forome.core.struct.Interval;
import org.forome.core.struct.Position;
import org.forome.core.struct.nucleotide.Nucleotide;
import org.forome.core.struct.sequence.Sequence;

@GraphQLName("group_data_position")
public class GGroupDataPosition {

    public final Assembly assembly;
    public final Interval interval;

    public GGroupDataPosition(Assembly assembly, Interval interval) {
        this.assembly = assembly;
        this.interval = interval;
    }

    @GraphQLField
    @GraphQLName("items")
    public GDataPosition[] getItems() {
        GDataPosition[] items = new GDataPosition[interval.end - interval.start + 1];
        for (int i = 0; i < items.length; i++) {
            items[i] = new GDataPosition(assembly, new Position(interval.chromosome, interval.start + i));
        }
        return items;
    }

    @GraphQLField
    @GraphQLName("fasta")
    public String getFasta() {
        AStorage aStorage = Service.getInstance().getAStorageService().aStorage;
        Source source = aStorage.getSource(assembly);

        Nucleotide[] nucleotides = new Nucleotide[interval.end - interval.start + 1];
        for (int i = 0; i < nucleotides.length; i++) {
            Position position = new Position(interval.chromosome, interval.start + i);
            Record record = source.getRecord(position);
            if (record == null) {
                nucleotides[i] = Nucleotide.NONE;
            } else {
                nucleotides[i] = record.getNucleotide();
            }
        }

        Sequence sequence = new Sequence(interval, nucleotides);
        return sequence.getValue();
    }

}
