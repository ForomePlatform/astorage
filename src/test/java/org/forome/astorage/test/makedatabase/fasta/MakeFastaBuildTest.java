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

package org.forome.astorage.test.makedatabase.fasta;

import org.forome.astorage.core.batch.BatchRecord;
import org.forome.astorage.core.batch.BatchRecordConservation;
import org.forome.astorage.core.batch.BatchRecordFasta;
import org.forome.astorage.core.data.Conservation;
import org.forome.astorage.core.utils.RandomUtils;
import org.forome.astorage.makedatabase.make.batchrecord.WriteBatchRecordConservation;
import org.forome.astorage.makedatabase.make.batchrecord.WriteBatchRecordFasta;
import org.forome.core.struct.Chromosome;
import org.forome.core.struct.Interval;
import org.forome.core.struct.Position;
import org.forome.core.struct.nucleotide.Nucleotide;
import org.junit.Assert;
import org.junit.Test;

public class MakeFastaBuildTest {

    @Test
    public void test() {
        for (int k = 0; k < 100; k += 23) {

            Interval interval = Interval.of(
                    Chromosome.CHR_1,
                    k * BatchRecord.DEFAULT_SIZE, (k + 1) * BatchRecord.DEFAULT_SIZE - 1
            );

            for (int t = 0; t < 10000; t++) {

                WriteBatchRecordFasta writeBatchRecordFasta = new WriteBatchRecordFasta(interval);
                for (int i = 0; i < BatchRecord.DEFAULT_SIZE; i++) {
                    Position position = new Position(interval.chromosome, interval.start + i);

                    Nucleotide nucleotide = Nucleotide.class.getEnumConstants()[
                            RandomUtils.RANDOM.nextInt(Nucleotide.class.getEnumConstants().length)
                            ];
                    writeBatchRecordFasta.set(position, nucleotide);
                }

                byte[] bytes = writeBatchRecordFasta.build();

                //restore
                BatchRecordFasta batchRecordFasta = new BatchRecordFasta(interval, bytes, 0);

                //assert
                for (int p = interval.start; p < interval.end; p++) {
                    Position position = new Position(interval.chromosome, p);

                    Nucleotide restoreNucleotide = batchRecordFasta.getNucleotide(position);

                    Assert.assertEquals(writeBatchRecordFasta.getNucleotide(position), restoreNucleotide);
                }
            }
        }
    }
}
