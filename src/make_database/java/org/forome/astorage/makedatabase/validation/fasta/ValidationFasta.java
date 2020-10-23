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

package org.forome.astorage.makedatabase.validation.fasta;

import org.forome.astorage.AStorage;
import org.forome.astorage.core.record.Record;
import org.forome.astorage.core.source.CacheSource;
import org.forome.astorage.core.source.Source;
import org.forome.astorage.makedatabase.make.fasta.iterator.Item;
import org.forome.astorage.makedatabase.make.fasta.iterator.SourceFastaIterator;
import org.forome.core.struct.Assembly;
import org.forome.core.struct.nucleotide.Nucleotide;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

public class ValidationFasta {

    private final static Logger log = LoggerFactory.getLogger(ValidationFasta.class);

    private final AStorage aStorage;

    private final Assembly validationAssembly;
    private final Source validationSource;

    private final Path sourceFasta;

    public ValidationFasta(AStorage aStorage, Assembly validationAssembly, Path sourceFasta) {
        this.aStorage = aStorage;

        this.validationAssembly = validationAssembly;
        this.validationSource = new CacheSource(aStorage.getSource(validationAssembly));

        this.sourceFasta = sourceFasta;
    }

    public void validation() throws IOException {
        log.debug("Validation fasta...");
        try (SourceFastaIterator sourceFastaIterator = new SourceFastaIterator(sourceFasta)) {
            while (sourceFastaIterator.hasNext()) {
                Item item = sourceFastaIterator.next();

                Record record = validationSource.getRecord(item.position);
                if (record == null) {
                    if (item.nucleotide == null || item.nucleotide == Nucleotide.NONE) {
                        continue;
                    } else {
                        log.debug("Fail validation conservation: {}, record is null", item.position);
                    }
                }

                if (!validation(item.nucleotide, record.getNucleotide())) {
                    log.debug("Fail validation fasta: {}, expected: {}, actual: {}",
                            item.position, item.nucleotide, record.getNucleotide());
                    throw new RuntimeException("Exception validation");
                }
            }
        }
    }

    private boolean validation(Nucleotide expected, Nucleotide actual) {
        if (expected == actual) return true;
        if (expected == null && actual == Nucleotide.NONE) return true;
        if (expected == Nucleotide.NONE && actual == null) return true;
        return false;
    }
}
