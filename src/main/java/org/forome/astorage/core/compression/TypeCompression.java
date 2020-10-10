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

package org.forome.astorage.core.compression;

import org.forome.astorage.core.compression.impl.*;

public enum TypeCompression {

	//Данных нет
	EMPTY((byte) 0, new CompressionEmpty()),

	//Простое последовательное укладывание значений
	ORDERS((byte) 1, new CompressionOrder()),

	//Простое последовательное укладывание значений
	ORDERS_OVER_GZIP((byte) 2, new CompressionOrder(true)),

	//Простое последовательное укладывание значений со словарем
	ORDERS_WITH_DICTIONARY((byte) 3, new CompressionOrderWithDictionary()),

	//Простое последовательное укладывание значений со словарем
	ORDERS_WITH_DICTIONARY_OVER_GZIP((byte) 4, new CompressionOrderWithDictionary(true)),

	//Укладка не пустых записей(значение с индексом)
	SELECTIVE((byte) 5, new CompressionSelective()),

	//Укладка не пустых записей(значение с индексом)
	SELECTIVE_OVER_GZIP((byte) 6, new CompressionSelective(true)),

	//Укладка не пустых записей(значение с индексом) со словарем
	SELECTIVE_WITH_DICTIONARY((byte) 7, new CompressionSelectiveWithDictionary()),

	//Укладка не пустых записей(значение с индексом) со словарем
	SELECTIVE_WITH_DICTIONARY_OVER_GZIP((byte) 8, new CompressionSelectiveWithDictionary(true)),

	//Дефолтная укладка нуклиотидной последовательности, в одном байте - 2 нуклиотида
	FASTA_DEFAULT((byte) 9, new CompressionFastaDefault());

	public final byte value;
	public final AbstractCompression compression;

	TypeCompression(byte value, AbstractCompression compression) {
		this.value = value;
		this.compression = compression;
	}

	public static TypeCompression get(byte value) {
		for (TypeCompression item: TypeCompression.values()) {
			if (value == item.value) {
				return item;
			}
		}
		throw new RuntimeException("Not support type compression: " + value);
	}
}
