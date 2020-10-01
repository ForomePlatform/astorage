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

package org.forome.astorage.service.graphql.scalar;

import graphql.annotations.annotationTypes.GraphQLName;
import org.forome.core.struct.Assembly;
import org.forome.core.struct.Chromosome;

@GraphQLName("assembly")
public enum GAssembly {

    /**
     * HG19
     */
    GRCh37(Assembly.GRCh37),

    /**
     * HG38
     */
    GRCh38(Assembly.GRCh38);


    public final Assembly assembly;

    GAssembly(Assembly assembly) {
        this.assembly = assembly;
    }

    public static GAssembly convert(Assembly assembly) {
        for (GAssembly item: GAssembly.values()) {
            if (item.assembly == assembly) {
                return item;
            }
        }
        throw new RuntimeException();
    }
}
