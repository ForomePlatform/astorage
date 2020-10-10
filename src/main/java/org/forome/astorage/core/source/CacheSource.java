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

package org.forome.astorage.core.source;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.forome.astorage.core.Metadata;
import org.forome.astorage.core.record.Record;
import org.forome.core.struct.Position;
import java.util.Optional;

public class CacheSource implements Source {

    private Source source;

    private final Cache<Position, Optional<Record>> cacheRecords;

    public CacheSource(Source source) {
        this.source = source;

        this.cacheRecords = CacheBuilder.newBuilder()
                .maximumSize(100)
                .build();
    }

    @Override
    public Metadata getMetadata() {
        return source.getMetadata();
    }

    @Override
    public Record getRecord(Position position) {
        try {
            Optional<Record> oRecord = cacheRecords.get(position, () -> Optional.ofNullable(source.getRecord(position)));
            return oRecord.orElse(null);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
