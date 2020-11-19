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

package org.forome.astorage.pastorage.schema;

import net.minidev.json.JSONObject;
import org.forome.astorage.core.exception.ExceptionBuilder;
import org.forome.astorage.pastorage.codec.HGKey;
import org.forome.astorage.pastorage.record.RecordFasta;
import org.forome.astorage.pastorage.utils.CompressorUtils;
import org.forome.core.struct.Assembly;
import org.forome.core.struct.Chromosome;
import org.forome.core.struct.Interval;
import org.forome.core.struct.Position;
import org.forome.core.struct.sequence.Sequence;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDBException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class SchemaFasta extends Schema {

	public static final String SCHEMA_FASTA_NAME = "fasta";

	public int mBlockSize;

	protected SchemaFasta(Path schemaFile, Path schemaDatabase) {
		super(SCHEMA_FASTA_NAME, schemaFile, schemaDatabase);

		JSONObject jSchemaFile = parseSchemaFile(schemaFile);
		this.mBlockSize = jSchemaFile.getAsNumber("block-size").intValue();
	}

	@Override
	public RecordFasta getRecord(Assembly assembly, Position position) {
		Chromosome chromosome = position.chromosome;
		int pos = position.value;

		Position basePosition = new Position(
				position.chromosome,
				(pos - 1) - ((pos - 1) % mBlockSize)
		);

		byte[] key = HGKey.encode(assembly, basePosition);

		ColumnFamilyHandle columnFamily = getColumnFamily(assembly);
		byte[] value;
		try {
			value = rocksDBDatabase.rocksDB.get(columnFamily, key);
		} catch (RocksDBException ex) {
			throw ExceptionBuilder.buildDatabaseException(ex);
		}

		byte[] bytes = CompressorUtils.uncompress(value);
		String sSequence = new String(bytes, StandardCharsets.UTF_8);

		Interval interval = Interval.of(chromosome, basePosition.value + 1, basePosition.value + sSequence.length());

		Sequence sequence = Sequence.build(interval, sSequence);

		return new RecordFasta(
				sequence.getNucleotide(position)
		);
	}
}
