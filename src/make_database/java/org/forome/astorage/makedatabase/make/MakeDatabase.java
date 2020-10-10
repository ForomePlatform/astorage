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

package org.forome.astorage.makedatabase.make;

import org.forome.astorage.core.Metadata;
import org.forome.astorage.core.liftover.LiftoverConnector;
import org.forome.astorage.core.source.SourceDatabase;
import org.forome.astorage.core.utils.bits.ShortBits;
import org.forome.astorage.core.utils.bits.StringBits;
import org.forome.astorage.makedatabase.main.argument.ArgumentsMake;
import org.forome.astorage.makedatabase.make.conservation.MakeConservation;
import org.forome.astorage.makedatabase.make.fasta.MakeFasta;
import org.forome.astorage.makedatabase.statistics.StatisticsCompression;
import org.forome.core.struct.Assembly;
import org.rocksdb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;


public class MakeDatabase implements AutoCloseable {

    private final static Logger log = LoggerFactory.getLogger(MakeDatabase.class);

    public final Assembly assembly;

    private final RocksDBConnector rocksDBConnector;
    private final OptimisticTransactionDB rocksDB;

    public final LiftoverConnector liftoverConnector;

    public final MakeConservation makeConservation;
    public final MakeFasta makeFasta;

    public MakeDatabase(ArgumentsMake argumentsMake) throws Exception {
        this.assembly = argumentsMake.assembly;

        this.rocksDBConnector = new RocksDBConnector(argumentsMake.database.toAbsolutePath());
        this.rocksDB = rocksDBConnector.rocksDB;

        this.liftoverConnector = new LiftoverConnector();

        if (argumentsMake.gerpHg19 != null) {
            this.makeConservation = new MakeConservation(this, assembly, Assembly.GRCh37, argumentsMake.gerpHg19);
        } else {
            this.makeConservation = null;
        }

        if (assembly == Assembly.GRCh37 && argumentsMake.fastaHg19 != null) {
            this.makeFasta = new MakeFasta(this, assembly, argumentsMake.fastaHg19);
        } else if (assembly == Assembly.GRCh38 && argumentsMake.fastaHg38 != null) {
            this.makeFasta = new MakeFasta(this, assembly, argumentsMake.fastaHg38);
        } else {
            this.makeFasta = null;
        }

		/*
		ColumnFamilyHandle columnFamilyRecord = rocksDBConnector.getColumnFamily(RocksDBDatabase.COLUMN_FAMILY_RECORD);
		if (columnFamilyRecord == null) {
			columnFamilyRecord = rocksDBConnector.createColumnFamily(RocksDBDatabase.COLUMN_FAMILY_RECORD);
		}
		int count = 0;
		try (RocksIterator rocksIterator = rocksDBConnector.rocksDB.newIterator(columnFamilyRecord)) {
			rocksIterator.seekToFirst();
			while (rocksIterator.isValid() && count++ < 10) {
				byte[] key = rocksIterator.key();
				byte[] value = rocksIterator.value();

				log.debug("key: {}, value: {}", key, value);
				rocksIterator.next();
			}
		}
		log.debug("read complete");
		*/
    }

    public void buildInfo() throws RocksDBException {
        if (rocksDBConnector.getColumnFamily(SourceDatabase.COLUMN_FAMILY_INFO) != null) {
            rocksDBConnector.dropColumnFamily(SourceDatabase.COLUMN_FAMILY_INFO);
        }
        ColumnFamilyHandle columnFamilyInfo = rocksDBConnector.createColumnFamily(SourceDatabase.COLUMN_FAMILY_INFO);

        try (Transaction transaction = rocksDB.beginTransaction(new WriteOptions())) {

            //Версия формата
            transaction.put(
                    columnFamilyInfo,
                    StringBits.toByteArray(Metadata.KEY_FORMAT_VERSION),
                    ShortBits.toByteArray(SourceDatabase.VERSION_FORMAT)
            );

            //Assembly
            transaction.put(
                    columnFamilyInfo,
                    StringBits.toByteArray(Metadata.KEY_ASSEMBLY),
                    StringBits.toByteArray(assembly.name())
            );

            transaction.commit();
        }

        rocksDBConnector.rocksDB.compactRange(columnFamilyInfo);
    }

    public void buildRecords() throws RocksDBException, SQLException, IOException {
        ColumnFamilyHandle columnFamilyRecord = rocksDBConnector.getColumnFamily(SourceDatabase.COLUMN_FAMILY_RECORD);
        if (columnFamilyRecord == null) {
            columnFamilyRecord = rocksDBConnector.createColumnFamily(SourceDatabase.COLUMN_FAMILY_RECORD);
        }

        StatisticsCompression statistics = new StatisticsCompression();

        if (makeConservation != null) {
            makeConservation.build(rocksDB, columnFamilyRecord, statistics);
        }
        if (makeFasta != null) {
            makeFasta.build(rocksDB, columnFamilyRecord, statistics);
        }

        rocksDBConnector.rocksDB.compactRange();

        statistics.println();
    }

    @Override
    public void close() {
        rocksDBConnector.close();
    }

}
