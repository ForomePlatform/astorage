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

package org.forome.astorage.test.core.compression;

import org.forome.astorage.core.batch.BatchRecord;
import org.forome.astorage.core.compression.TypeCompression;
import org.forome.astorage.core.compression.exception.NotSupportCompression;
import org.forome.astorage.core.utils.RandomUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CompressionTest {

	public static void checkCompression(TypeCompression type, Class[] types, List<Object[]> expected) throws NotSupportCompression {
		byte[] sourceBytes = type.compression.pack(types, expected);

		//Добавляем произвольный байтовый префикс(что бы проверить работу offset)
		int offsetBytes = RandomUtils.RANDOM.nextInt(10);
		byte[] bytes = new byte[offsetBytes + sourceBytes.length];
		System.arraycopy(sourceBytes, 0, bytes, offsetBytes, sourceBytes.length);

		//Assert size
		int unpackSize = type.compression.unpackSize(types, expected.size(), bytes, offsetBytes);
		Assert.assertEquals(sourceBytes.length, unpackSize);

		//Assert values
		for (int index = 0; index < BatchRecord.DEFAULT_SIZE; index++) {
			Object[] values = type.compression.unpackValues(types, bytes, offsetBytes, index);
			Assert.assertArrayEquals(expected.get(index), values);
		}
	}

	public static short getRandomShort() {
		return (short) (RandomUtils.RANDOM.nextInt(Short.MAX_VALUE - Short.MIN_VALUE) - Short.MIN_VALUE + 1);
	}
}
