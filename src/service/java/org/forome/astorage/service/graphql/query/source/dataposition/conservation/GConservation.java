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

package org.forome.astorage.service.graphql.query.source.dataposition.conservation;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import org.forome.astorage.AStorage;
import org.forome.astorage.core.data.Conservation;
import org.forome.astorage.core.record.Record;
import org.forome.astorage.core.source.Source;
import org.forome.astorage.service.Service;
import org.forome.astorage.service.graphql.query.source.dataposition.GDataPosition;
import org.forome.astorage.service.graphql.utils.ConvertType;

@GraphQLName("conservation")
public class GConservation {

    public final GDataPosition gDataPosition;

    private Conservation _lazyConservation;

    public GConservation(GDataPosition gDataPosition) {
        this.gDataPosition = gDataPosition;
    }

    @GraphQLField
    @GraphQLName("gerp_rs")
    public Double getGerpRS() {
        return ConvertType.toDouble(getConservation().gerpRS);
    }

    @GraphQLField
    @GraphQLName("gerp_n")
    public Double getGerpN() {
        return ConvertType.toDouble(getConservation().gerpN);
    }

    private Conservation getConservation() {
        if (_lazyConservation == null) {
            AStorage aStorage = Service.getInstance().getAStorageService().aStorage;
            Source source = aStorage.getSource(gDataPosition.assembly);
            Record record = source.getRecord(gDataPosition.position);
            _lazyConservation = record.getConservation();
        }
        return _lazyConservation;
    }
}
