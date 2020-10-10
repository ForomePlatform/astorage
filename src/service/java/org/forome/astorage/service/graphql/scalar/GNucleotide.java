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

import org.forome.core.struct.nucleotide.Nucleotide;

public enum GNucleotide {

    NONE(Nucleotide.NONE),

    A(Nucleotide.A),//Аденин
    G(Nucleotide.G),//Гуанин
    C(Nucleotide.C),//Цитозин
    T(Nucleotide.T),//Тимин

    a(Nucleotide.a),//Аденин
    g(Nucleotide.g),//Гуанин
    c(Nucleotide.c),//Цитозин
    t(Nucleotide.t);//Тимин

    public final Nucleotide nucleotide;

    GNucleotide(Nucleotide nucleotide) {
        this.nucleotide = nucleotide;
    }

    public static GNucleotide convert(Nucleotide nucleotide) {
        for (GNucleotide item: GNucleotide.values()) {
            if (item.nucleotide == nucleotide) {
                return item;
            }
        }
        throw new RuntimeException();
    }
}
