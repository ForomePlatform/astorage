/*
 *  Copyright (c) 2019. Vladimir Ulitin, Partners Healthcare and members of Forome Association
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

package org.forome.astorage.test.makedatabase.packer;

import org.forome.astorage.core.batch.BatchRecord;
import org.forome.astorage.core.packer.PackInterval;
import org.forome.core.struct.Chromosome;
import org.forome.core.struct.Interval;
import org.junit.Assert;
import org.junit.Test;

public class PackIntervalTest {

	@Test
	public void test() {
		int sizePack = BatchRecord.DEFAULT_SIZE;
		PackInterval packInterval = new PackInterval(sizePack);

		Chromosome chromosome = Chromosome.CHR_1;
		for (int k = 0; k < 2489082; k += 49000) {
			Interval iExpected = Interval.of(chromosome, k * sizePack, k * sizePack + sizePack - 1);
			byte[] bytes = packInterval.toByteArray(iExpected);
			Interval iActual = packInterval.fromByteArray(bytes);

			Assert.assertEquals(iExpected, iActual);
		}
	}
}
