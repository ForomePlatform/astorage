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

package org.forome.astorage.makedatabase.make.accumulation;

import org.forome.astorage.core.batch.BatchRecord;
import org.forome.astorage.core.packer.PackInterval;
import org.forome.astorage.core.source.SourceDatabase;
import org.forome.astorage.makedatabase.main.MainMakeDatabase;
import org.forome.astorage.makedatabase.make.batchrecord.WriteBatchRecord;
import org.forome.astorage.makedatabase.statistics.StatisticsCompression;
import org.forome.core.struct.Position;
import org.rocksdb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Accumulation implements AutoCloseable {

	private final static Logger log = LoggerFactory.getLogger(Accumulation.class);

	protected final OptimisticTransactionDB rocksDB;
	protected final ColumnFamilyHandle columnFamily;
	protected final StatisticsCompression statistics;

	private WriteBatchRecord activeWriteBatchRecord;

	public Accumulation(OptimisticTransactionDB rocksDB, ColumnFamilyHandle columnFamily, StatisticsCompression statistics) {
		this.rocksDB = rocksDB;
		this.columnFamily = columnFamily;
		this.statistics = statistics;
	}

	protected WriteBatchRecord getBatchRecord(Position position) throws RocksDBException {
		if (activeWriteBatchRecord == null) {
			activeWriteBatchRecord = buildWriteBatchRecord(position);
		} else if (!activeWriteBatchRecord.interval.contains(position)) {
			flush();
			activeWriteBatchRecord = buildWriteBatchRecord(position);
		}
		return activeWriteBatchRecord;
	}

	private WriteBatchRecord buildWriteBatchRecord(Position position) {
		BatchRecord batchRecord = SourceDatabase.getBatchRecord(rocksDB, columnFamily, position);
		if (batchRecord == null) {
			return new WriteBatchRecord(SourceDatabase.getIntervalBatchRecord(position), statistics);
		} else {
			return new WriteBatchRecord(batchRecord, statistics);
		}
	}

	private void flush() throws RocksDBException {
		if (activeWriteBatchRecord == null) return;

		try (Transaction transaction = rocksDB.beginTransaction(new WriteOptions())) {
			byte[] key = new PackInterval(BatchRecord.DEFAULT_SIZE).toByteArray(activeWriteBatchRecord.interval);
			if (activeWriteBatchRecord.isEmpty()) {
				transaction.delete(columnFamily, key);
			} else {
				byte[] value = activeWriteBatchRecord.build();
				transaction.put(columnFamily, key, value);
			}
			transaction.commit();
		}
	}

	@Override
	public void close() throws RocksDBException {
		flush();
	}
}
