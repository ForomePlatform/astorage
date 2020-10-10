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

package org.forome.astorage.makedatabase.validation.conservation;

import org.forome.astorage.AStorage;
import org.forome.astorage.core.data.Conservation;
import org.forome.astorage.core.liftover.LiftoverConnector;
import org.forome.astorage.core.record.Record;
import org.forome.astorage.core.source.CacheSource;
import org.forome.astorage.core.source.Source;
import org.forome.astorage.makedatabase.make.conservation.iterator.Item;
import org.forome.astorage.makedatabase.make.conservation.iterator.SourceConservationIterator;
import org.forome.core.struct.Assembly;
import org.forome.core.struct.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

public class ValidationConservation {

    private final static Logger log = LoggerFactory.getLogger(ValidationConservation.class);

    private final AStorage aStorage;

    private final Assembly validationAssembly;
    private final Source validationSource;

    private final Assembly sourceAssembly;
    private final Path sourceGerpPath;

    private final LiftoverConnector liftoverConnector;

    public ValidationConservation(AStorage aStorage, Assembly validationAssembly, Assembly sourceAssembly, Path sourceGerpPath, LiftoverConnector liftoverConnector) {
        this.aStorage = aStorage;

        this.validationAssembly = validationAssembly;
        this.validationSource = new CacheSource(aStorage.getSource(validationAssembly));

        this.sourceAssembly = sourceAssembly;
        this.sourceGerpPath = sourceGerpPath;

        this.liftoverConnector = liftoverConnector;
    }

    public void validation() throws IOException {
        log.debug("Validation conservation...");
        try (SourceConservationIterator sourceConservationIterator = new SourceConservationIterator(sourceGerpPath)) {
            while (sourceConservationIterator.hasNext()) {
                Item item = sourceConservationIterator.next();

                Position validationPosition = liftoverConnector.convertPosition(
                        validationAssembly, sourceAssembly, item.position
                );

                Record record = validationSource.getRecord(validationPosition);
                if (record == null) {
                    if (item.gerpN == null && item.gerpRS == null) {
                        continue;
                    } else {
                        log.debug("Fail validation conservation, validationPosition: {}, record is null", validationPosition);
                    }
                }

                Conservation validationConservation = record.getConservation();
                boolean resGerpN = validationFloat(item.gerpN, validationConservation.gerpN);
                if (!resGerpN) {
                    log.debug("Fail validation conservation (resGerpN), validationPosition: {}, expected: {}, actual: {}",
                            validationPosition, item.gerpN, validationConservation.gerpN);
                    throw new RuntimeException("Exception validation");
                }
                boolean resGerpRS = validationFloat(item.gerpRS, validationConservation.gerpRS);
                if (!resGerpRS) {
                    log.debug("Fail validation conservation (gerpRS), validationPosition: {}, expected: {}, actual: {}",
                            validationPosition, item.gerpRS, validationConservation.gerpRS);
                    throw new RuntimeException("Exception validation");
                }
            }
        }
        log.debug("Validation conservation... complete");
    }

    private static boolean validationFloat(Float expected, Float actual) {
        if (Objects.equals(expected, actual)) return true;
        return (Math.abs(expected - actual) < 0.001d);
    }
}
