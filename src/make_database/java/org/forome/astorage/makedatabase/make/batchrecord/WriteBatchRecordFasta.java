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

package org.forome.astorage.makedatabase.make.batchrecord;

import org.forome.astorage.core.batch.BatchRecord;
import org.forome.astorage.core.batch.BatchRecordFasta;
import org.forome.astorage.core.compression.Compression;
import org.forome.core.struct.Interval;
import org.forome.core.struct.Position;
import org.forome.core.struct.nucleotide.Nucleotide;

import java.util.ArrayList;
import java.util.List;

public class WriteBatchRecordFasta {

    private final Interval interval;
    private final Nucleotide[] nucleotides;

    public WriteBatchRecordFasta(Interval interval) {
        this.interval = interval;
        this.nucleotides = new Nucleotide[interval.end - interval.start + 1];
    }

    public WriteBatchRecordFasta(BatchRecordFasta batchRecordFasta) {
        this(batchRecordFasta.interval);

        for (int i = 0; i < nucleotides.length; i++) {
            Position position = new Position(interval.chromosome, interval.start + i);
            set(position, batchRecordFasta.getNucleotide(position));
        }
    }

    public void set(Position position, Nucleotide nucleotide) {
        nucleotides[getIndex(position)] = nucleotide;
    }

    public Nucleotide getNucleotide(Position position) {
        return nucleotides[getIndex(position)];
    }

    private int getIndex(Position position) {
        return position.value - interval.start;
    }

    public boolean isEmpty() {
        for (Nucleotide nucleotide : nucleotides) {
            if (isEmpty(nucleotide)) continue;
            return false;
        }
        return true;
    }

    public static boolean isEmpty(Nucleotide nucleotide) {
        return (nucleotide == Nucleotide.NONE);
    }

    public byte[] build() {
        List<Object[]> values = new ArrayList<>();
        for (int i = 0; i < nucleotides.length; i++) {
            Nucleotide nucleotide = nucleotides[i];
            if (nucleotide == Nucleotide.NONE) {
                values.add(new Object[]{ null });
            } else {
                values.add(new Object[]{
                        nucleotide
                });
            }
        }
        return new Compression(BatchRecordFasta.DATABASE_ORDER_TYPES, BatchRecord.DEFAULT_SIZE)
                .pack(values);
    }
}
