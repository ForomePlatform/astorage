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

package org.forome.astorage.makedatabase.make.fasta;

import org.forome.astorage.core.data.Conservation;
import org.forome.astorage.makedatabase.make.MakeDatabase;
import org.forome.astorage.makedatabase.make.conservation.accumulation.AccumulationConservation;
import org.forome.astorage.makedatabase.make.fasta.accumulation.AccumulationFasta;
import org.forome.astorage.makedatabase.make.fasta.iterator.Item;
import org.forome.astorage.makedatabase.make.fasta.iterator.SourceFastaIterator;
import org.forome.astorage.makedatabase.statistics.StatisticsCompression;
import org.forome.core.struct.Assembly;
import org.forome.core.struct.Chromosome;
import org.forome.core.struct.Position;
import org.forome.core.struct.nucleotide.Nucleotide;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.OptimisticTransactionDB;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;

public class MakeFasta {

    private final static Logger log = LoggerFactory.getLogger(MakeFasta.class);

    private final MakeDatabase makeDatabase;

    private final Assembly assembly;

    private final Path sourcePathFasta;

    //В класс необходимо передать файл нужной сборке
    public MakeFasta(
            MakeDatabase makeDatabase,
            Assembly assembly,
            Path sourcePathFasta
    ) {
        this.makeDatabase = makeDatabase;
        this.assembly = assembly;

        this.sourcePathFasta = sourcePathFasta;
    }

    public void build(OptimisticTransactionDB rocksDB, ColumnFamilyHandle columnFamilyRecord, StatisticsCompression statistics) throws IOException, RocksDBException {
        log.debug("Write fasta...");
        try (SourceFastaIterator sourceFastaIterator = new SourceFastaIterator(sourcePathFasta)) {
            try (AccumulationFasta accumulation = new AccumulationFasta(rocksDB, columnFamilyRecord, statistics)) {
                while (sourceFastaIterator.hasNext()) {
                    Item item = sourceFastaIterator.next();

                    accumulation.add(item.position, item.nucleotide);

                    if (item.position.value % 1_000_000 == 0) {
                        log.debug("Write: {}", item.position);
                    }
                }
            }
        }
        log.debug("Write fasta... complete");
    }

}
