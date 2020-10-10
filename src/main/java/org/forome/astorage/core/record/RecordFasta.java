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

package org.forome.astorage.core.record;

import org.forome.astorage.core.batch.BatchRecordConservation;
import org.forome.astorage.core.batch.BatchRecordFasta;
import org.forome.astorage.core.data.Conservation;
import org.forome.core.struct.nucleotide.Nucleotide;

public class RecordFasta {

	private final Record record;

	private final BatchRecordFasta batchRecordFasta;

	protected RecordFasta(Record record, BatchRecordFasta batchRecordFasta) {
		this.record = record;
		this.batchRecordFasta = batchRecordFasta;
	}

	public Nucleotide getNucleotide() {
		return batchRecordFasta.getNucleotide(record.position);
	}
}
