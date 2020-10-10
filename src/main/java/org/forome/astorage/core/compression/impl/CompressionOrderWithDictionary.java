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

package org.forome.astorage.core.compression.impl;

import org.forome.astorage.core.compression.AbstractCompression;
import org.forome.astorage.core.compression.exception.NotSupportCompression;
import org.forome.astorage.core.packer.PacketNucleotide;
import org.forome.astorage.core.utils.bits.ByteBits;
import org.forome.astorage.core.utils.bits.IntegerDynamicLengthBits;
import org.forome.astorage.core.utils.bits.ShortBits;
import org.forome.core.struct.nucleotide.Nucleotide;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Простое последовательное укладывание значений со словарем
 * В настоящий момент поддерживается упаковка только однотипных значений, а так же размер карты не больше 255 элементов
 */
public class CompressionOrderWithDictionary extends AbstractCompression {

	private final boolean overGzip;

	public CompressionOrderWithDictionary() {
		this(false);
	}

	public CompressionOrderWithDictionary(boolean overGzip) {
		this.overGzip = overGzip;
	}

	@Override
	public byte[] pack(Class[] types, List<Object[]> items) throws NotSupportCompression {
		if (Arrays.stream(types).distinct().count() > 1) {
			//Проверяем, что все значения одного типа
			throw new NotSupportCompression();
		}

		//Собираем словарь
		List<Object> dictionary = getDictionary(items);

		ByteArrayOutputStream os = new ByteArrayOutputStream();

		//Сначала записываем размер словаря(без знаковый)
		os.write(ByteBits.convertFromUnsigned(dictionary.size()));

		//Последовательно записываем словарь
		for (Object iDictionary : dictionary) {
			try {
				os.write(pack(types[0], iDictionary));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		//Теперь записываем значение в виде ссылок на карту - по одному байту
		for (Object[] values : items) {
			if (values.length != types.length) {
				throw new IllegalStateException();
			}
			for (int i = 0; i < values.length; i++) {
				Object value = values[i];
				if (value != null && value.getClass() != types[i]) {
					throw new IllegalStateException();
				}

				int dictionaryIndex = Integer.MIN_VALUE;
				for (int di = 0; di < dictionary.size(); di++) {
					if (Objects.equals(dictionary.get(di), value)) {
						dictionaryIndex = di;
						break;
					}
				}
				if (dictionaryIndex == Integer.MIN_VALUE) {
					//Баг, почему-то не нашлось значение в словаре
					throw new IllegalStateException();
				}
				os.write(ByteBits.convertFromUnsigned(dictionaryIndex));
			}
		}
		byte[] bytes = os.toByteArray();

		//Архивируем
		if (overGzip) {
			bytes = gzipCompress(bytes);
		}
		return bytes;
	}

	/**
	 * Собираем словарь
	 *
	 * @param items
	 * @return
	 * @throws NotSupportCompression
	 */
	protected static List<Object> getDictionary(List<Object[]> items) throws NotSupportCompression {
		List<Object> dictionary = items.stream().flatMap(objects -> Arrays.stream(objects)).distinct().collect(Collectors.toList());
		if (dictionary.size() > ByteBits.MAX_UNSIGNED_VALUE) {
			//Проверяем, что размер словаря не больше 255 элементов
			throw new NotSupportCompression();
		}
		return dictionary;
	}

	@Override
	public int unpackSize(Class[] types, int sizeInterval, byte[] bytes, int offsetBytes) {
		if (overGzip) {
			IntegerDynamicLengthBits.Value value = IntegerDynamicLengthBits.fromByteArray(bytes, offsetBytes);
			return value.byteSize + value.value;
		} else {
			int sizeMap = ByteBits.convertByUnsigned(bytes[offsetBytes]);
			return 1 //Размер карты
					+ getByteSize(types[0]) * sizeMap //Сама карта
					+ types.length * sizeInterval; // каждое значение заниет 1 байт - ссылку на словарь
		}
	}

	@Override
	public Object[] unpackValues(Class[] types, byte[] bytes, int offsetBytes, int index) {
		if (overGzip) {
			return _unpackValues(types, gzipDecompress(bytes, offsetBytes), 0, index);
		} else {
			return _unpackValues(types, bytes, offsetBytes, index);
		}
	}

	public static Object[] _unpackValues(Class[] types, byte[] bytes, int offsetBytes, int index) {
		int sizeMap = ByteBits.convertByUnsigned(bytes[offsetBytes]);

		int offset = offsetBytes
				+ 1 //Размер карты
				+ getByteSize(types[0]) * sizeMap //Сама карта
				+ types.length * index; // каждое значение занимет 1 байт - ссылку на словарь

		Object[] value = new Object[types.length];
		for (int i = 0; i < types.length; i++) {
			Class type = types[i];

			int dictionaryIndex;
			try {
				dictionaryIndex = ByteBits.convertByUnsigned(bytes[offset]);
			} catch (Throwable ex) {
				throw new RuntimeException(ex);
			}

			value[i] = unpackValue(type, bytes, offsetBytes + 1 + dictionaryIndex * getByteSize(type)).value;
			offset += 1;
		}
		return value;
	}

	protected static int getByteSize(Class type) {
		if (type == Short.class || type == short.class) {
			return ShortBits.BYTE_SIZE;
		} else if (type == Nucleotide.class) {
			return PacketNucleotide.BYTE_SIZE;
		} else {
			throw new RuntimeException("Not support type: " + type);
		}
	}

}

