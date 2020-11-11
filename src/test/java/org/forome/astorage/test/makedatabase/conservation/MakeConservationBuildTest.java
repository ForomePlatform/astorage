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

package org.forome.astorage.test.makedatabase.conservation;

import org.apache.commons.lang3.RandomUtils;
import org.forome.astorage.core.batch.BatchRecord;
import org.forome.astorage.core.batch.BatchRecordConservation;
import org.forome.astorage.core.data.Conservation;
import org.forome.astorage.core.liftover.LiftoverConnector;
import org.forome.astorage.makedatabase.make.batchrecord.WriteBatchRecordConservation;
import org.forome.core.struct.Chromosome;
import org.forome.core.struct.Interval;
import org.forome.core.struct.Position;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Objects;

public class MakeConservationBuildTest {

    @Test
    public void test() throws IOException {
        for (int k = 0; k < 100; k += 23) {

            Interval interval = Interval.of(
                    Chromosome.CHR_1,
                    k * BatchRecord.DEFAULT_SIZE, (k + 1) * BatchRecord.DEFAULT_SIZE - 1
            );

            for (int t = 0; t < 10000; t++) {

                WriteBatchRecordConservation writeBatchRecordConservation = new WriteBatchRecordConservation(interval);
                for (int i = 0; i < BatchRecord.DEFAULT_SIZE; i++) {
                    Position position = new Position(interval.chromosome, interval.start + i);
                    Conservation conservation = new Conservation(
                            (RandomUtils.nextBoolean()) ? null : RandomUtils.nextFloat(0.0f, 62.0f) - 31.0f,
                            (RandomUtils.nextBoolean()) ? null : RandomUtils.nextFloat(0.0f, 62.0f) - 31.0f
                    );
                    writeBatchRecordConservation.set(position, conservation);
                }

                byte[] bytes = writeBatchRecordConservation.build();

                //restore
                BatchRecordConservation batchRecordConservation = new BatchRecordConservation(interval, bytes, 0);

                //assert
                for (int p = interval.start; p < interval.end; p++) {
                    Position position = new Position(interval.chromosome, p);

                    Conservation restoreConservation = batchRecordConservation.getConservation(position);

                    assertFloat(writeBatchRecordConservation.getConservation(position).gerpRS, restoreConservation.gerpRS);
                    assertFloat(writeBatchRecordConservation.getConservation(position).gerpN, restoreConservation.gerpN);
                }
            }
        }
    }

    private void assertFloat(Float expected, Float actual) {
        if (Objects.equals(expected, actual)) return;
        Assert.assertEquals(expected, actual, 0.001d);
    }
}
