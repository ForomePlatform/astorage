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
import org.forome.astorage.makedatabase.statistics.StatisticsCompression;
import org.forome.core.struct.Interval;

public class WriteBatchRecord {

	public final Interval interval;

	private final WriteBatchRecordConservation writeBatchRecordConservation;

	private final StatisticsCompression statistics;

	public WriteBatchRecord(Interval interval, StatisticsCompression statistics) {
		this.interval = interval;

		this.writeBatchRecordConservation = new WriteBatchRecordConservation(interval);

		this.statistics = statistics;
	}

	public WriteBatchRecord(BatchRecord batchRecord, StatisticsCompression statistics) {
		this.interval = batchRecord.interval;

		this.writeBatchRecordConservation = new WriteBatchRecordConservation(
				batchRecord.batchRecordConservation
		);

		this.statistics = statistics;
	}

	public WriteBatchRecordConservation getBatchRecordConservation() {
		return writeBatchRecordConservation;
	}

	public boolean isEmpty() {
		return writeBatchRecordConservation.isEmpty();
	}

	public byte[] build() {
		byte[] conservationBytes = writeBatchRecordConservation.build();

		statistics.add("conservation", conservationBytes);

		return conservationBytes;
	}
}
