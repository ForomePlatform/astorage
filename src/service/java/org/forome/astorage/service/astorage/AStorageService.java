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

package org.forome.astorage.service.astorage;

import com.infomaximum.database.exception.DatabaseException;
import org.forome.astorage.AStorage;
import org.forome.astorage.service.Service;
import org.forome.astorage.service.config.Config;
import org.forome.core.struct.Assembly;

public class AStorageService {

    private final Service service;

    public final AStorage aStorage;

    public AStorageService(Service service) throws DatabaseException {
        this.service = service;

        Config config = service.getConfig();

        AStorage.Builder builder = new AStorage.Builder();
        if (config.databaseHg37 != null) {
            builder.withSource(Assembly.GRCh37, config.databaseHg37);
        }
        if (config.databaseHg38 != null) {
            builder.withSource(Assembly.GRCh38, config.databaseHg38);
        }

        builder.withSourcePAStorage(config.sourcePAStorage);

        aStorage = builder.build();
    }


}
