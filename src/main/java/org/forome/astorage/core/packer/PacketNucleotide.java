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

package org.forome.astorage.core.packer;

import org.forome.core.struct.nucleotide.Nucleotide;

public class PacketNucleotide {

    public static int BYTE_SIZE = 1;

    public static Nucleotide fromByte(byte value) {
        for (Nucleotide item : Nucleotide.values()) {
            if ((byte)(item.character) == value) {
                return item;
            }
        }
        throw new RuntimeException("Unknown byte value: " + value);
    }


    public static byte toByte(Nucleotide value) {
        if (value == null) {
            return (byte) Nucleotide.NONE.character;
        } else {
            return (byte) value.character;
        }
    }

    public static byte[] toBytes(Nucleotide value) {
        return new byte[]{
                toByte(value)
        };
    }
}
