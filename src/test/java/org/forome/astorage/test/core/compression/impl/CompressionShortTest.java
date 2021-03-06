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

package org.forome.astorage.test.core.compression.impl;

import org.forome.astorage.core.batch.BatchRecord;
import org.forome.astorage.core.compression.TypeCompression;
import org.forome.astorage.core.compression.exception.NotSupportCompression;
import org.forome.astorage.core.utils.RandomUtils;
import org.forome.astorage.test.core.compression.CompressionTest;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CompressionShortTest extends CompressionTest {

    @Test
    public void testCompressionFromShortAndShort() throws NotSupportCompression {
        Class[] types = { Short.class, Short.class };

        for (int test = 0; test < BatchRecord.DEFAULT_SIZE; test++) {
            //Generate
            List<Object[]> expected = new ArrayList<>();
            expected.add(new Object[]{ null, null });//Первую запись гарантированна добавляем пустую
            for (int index = 1; index < BatchRecord.DEFAULT_SIZE; index++) {
                Short value1 = (RandomUtils.RANDOM.nextBoolean()) ? null : CompressionTest.getRandomShort();
                Short value2 = (RandomUtils.RANDOM.nextBoolean()) ? null : CompressionTest.getRandomShort();
                expected.add(new Object[]{ value1, value2 });
            }

            //Этот тип компрессии не поддерживает эти данные
            try {
                checkCompression(TypeCompression.EMPTY, types, expected);
                Assert.fail();
            } catch (NotSupportCompression e) {
            }

            checkCompression(TypeCompression.ORDERS, types, expected);
            checkCompression(TypeCompression.ORDERS_OVER_GZIP, types, expected);

            checkCompression(TypeCompression.ORDERS_WITH_DICTIONARY, types, expected);
            checkCompression(TypeCompression.ORDERS_WITH_DICTIONARY_OVER_GZIP, types, expected);

            checkCompression(TypeCompression.SELECTIVE, types, expected);
            checkCompression(TypeCompression.SELECTIVE_OVER_GZIP, types, expected);

            checkCompression(TypeCompression.SELECTIVE_WITH_DICTIONARY, types, expected);
            checkCompression(TypeCompression.SELECTIVE_WITH_DICTIONARY_OVER_GZIP, types, expected);

            //Этот тип компрессии не поддерживает эти данные
            try {
                checkCompression(TypeCompression.FASTA_DEFAULT, types, expected);
                Assert.fail();
            } catch (NotSupportCompression e) {
            }
        }
    }

}
