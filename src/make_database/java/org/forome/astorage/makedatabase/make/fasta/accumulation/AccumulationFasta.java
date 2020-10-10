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

package org.forome.astorage.makedatabase.make.fasta.accumulation;

import org.forome.astorage.makedatabase.make.accumulation.Accumulation;
import org.forome.astorage.makedatabase.make.batchrecord.WriteBatchRecord;
import org.forome.astorage.makedatabase.make.batchrecord.WriteBatchRecordConservation;
import org.forome.astorage.makedatabase.make.batchrecord.WriteBatchRecordFasta;
import org.forome.astorage.makedatabase.statistics.StatisticsCompression;
import org.forome.core.struct.Chromosome;
import org.forome.core.struct.Position;
import org.forome.core.struct.nucleotide.Nucleotide;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.OptimisticTransactionDB;
import org.rocksdb.RocksDBException;

public class AccumulationFasta extends Accumulation {

    public AccumulationFasta(OptimisticTransactionDB rocksDB, ColumnFamilyHandle columnFamily, StatisticsCompression statistics) {
        super(rocksDB, columnFamily, statistics);
    }

    public void add(Position position, Nucleotide nucleotide) throws RocksDBException {
        if (WriteBatchRecordFasta.isEmpty(nucleotide)) {
            return;
        }

        WriteBatchRecord writeBatchRecord = getBatchRecord(position);
        WriteBatchRecordFasta writeBatchRecordFasta = writeBatchRecord.getBatchRecordFasta();
        writeBatchRecordFasta.set(position, nucleotide);
    }

}
