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

package org.forome.astorage.makedatabase.validation;

import org.forome.astorage.AStorage;
import org.forome.astorage.core.liftover.LiftoverConnector;
import org.forome.astorage.makedatabase.main.argument.ArgumentsMake;
import org.forome.astorage.makedatabase.validation.conservation.ValidationConservation;
import org.forome.astorage.makedatabase.validation.fasta.ValidationFasta;
import org.forome.core.struct.Assembly;

import java.io.IOException;

public class ValidationDatabase {

    public final Assembly assembly;

    public final LiftoverConnector liftoverConnector;

    public final AStorage aStorage;

    private final ValidationConservation validationConservation;
    private final ValidationFasta validationFasta;

    public ValidationDatabase(ArgumentsMake argumentsMake) throws Exception {
        this.assembly = argumentsMake.assembly;

        this.liftoverConnector = new LiftoverConnector();

        AStorage.Builder builder = new AStorage.Builder();
        if (assembly == Assembly.GRCh37) {
            builder.withSource(Assembly.GRCh37, argumentsMake.database);
        } else {
            throw new RuntimeException("Not support assembly: " + assembly);
        }
        aStorage = builder.build();

        if (argumentsMake.gerpHg19 != null) {
            validationConservation = new ValidationConservation(
                    aStorage,
                    assembly,
                    Assembly.GRCh37, argumentsMake.gerpHg19,
                    liftoverConnector
            );
        } else {
            validationConservation = null;
        }

        if (assembly == Assembly.GRCh37 && argumentsMake.fastaHg19 != null) {
            validationFasta = new ValidationFasta(
                    aStorage,
                    assembly,
                    argumentsMake.fastaHg19
            );
        } else if (assembly == Assembly.GRCh38 && argumentsMake.fastaHg38 != null) {
            validationFasta = new ValidationFasta(
                    aStorage,
                    assembly,
                    argumentsMake.fastaHg38
            );
        } else {
            validationFasta = null;
        }


    }

    public void validation() throws IOException {
        if (validationConservation != null) {
            validationConservation.validation();
        }
        if (validationFasta != null) {
            validationFasta.validation();
        }
    }
}
