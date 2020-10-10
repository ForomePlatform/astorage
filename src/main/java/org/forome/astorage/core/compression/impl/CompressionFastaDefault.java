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

package org.forome.astorage.core.compression.impl;

import org.forome.astorage.core.compression.AbstractCompression;
import org.forome.astorage.core.compression.exception.NotSupportCompression;
import org.forome.core.struct.nucleotide.Nucleotide;

import java.util.List;

public class CompressionFastaDefault extends AbstractCompression {

    @Override
    public byte[] pack(Class[] types, List<Object[]> items) throws NotSupportCompression {
        if (types.length != 1 && types[0] != Nucleotide.class) {
            throw new NotSupportCompression();
        }

        byte[] bytes = new byte[getSizeBytes(items.size())];
        for (int i = 0; i < bytes.length; i++) {
            Nucleotide nucleotide1 = (Nucleotide) items.get(i * 2)[0];
            Nucleotide nucleotide2 = (Nucleotide) items.get(i * 2 + 1)[0];
            int b = (getCodeNucleotide(nucleotide1) << 4) | getCodeNucleotide(nucleotide2);
            bytes[i] = (byte) b;
        }
        return bytes;
    }

    @Override
    public int unpackSize(Class[] types, int sizeInterval, byte[] bytes, int offsetBytes) {
        return getSizeBytes(sizeInterval);
    }

    @Override
    public Object[] unpackValues(Class[] types, byte[] bytes, int offsetBytes, int index) {
        byte b = bytes[offsetBytes + index / 2];

        byte codeNucleotide;
        if ((index & 1) == 0) {
            //четный индекс, он находится на начали байта, необходимо смещение на 4 бита
            b = (byte) (b >>> 4);
        }
        codeNucleotide = (byte) (b & 15);//00001111

        return new Object[]{
                getNucleotide(codeNucleotide)
        };
    }

    private static int getSizeBytes(int sizeInterval) {
        int sizeBytes = sizeInterval / 2;
        if (sizeInterval % 2 != 0) {
            sizeBytes++;
        }
        return sizeBytes;
    }

    //Приравниваем нуклиотиду NONE к null
    private static byte getCodeNucleotide(Nucleotide nucleotide) {
        if (nucleotide == null) {
            return (byte) 0;
        }
        switch (nucleotide) {
            case NONE:
                return (byte) 0;
            case A:
                return (byte) 1;
            case G:
                return (byte) 2;
            case C:
                return (byte) 3;
            case T:
                return (byte) 4;
            case a:
                return (byte) 5;
            case g:
                return (byte) 6;
            case c:
                return (byte) 7;
            case t:
                return (byte) 8;
            default:
                throw new RuntimeException("Not support nucleotide: " + nucleotide);
        }
    }

    //Приравниваем null нуклиотиду к NONE
    private static Nucleotide getNucleotide(byte codeNucleotide) {
        switch (codeNucleotide) {
            case 0:
                return Nucleotide.NONE;
            case 1:
                return Nucleotide.A;
            case 2:
                return Nucleotide.G;
            case 3:
                return Nucleotide.C;
            case 4:
                return Nucleotide.T;
            case 5:
                return Nucleotide.a;
            case 6:
                return Nucleotide.g;
            case 7:
                return Nucleotide.c;
            case 8:
                return Nucleotide.t;
            default:
                throw new RuntimeException("Not support code nucleotide: " + codeNucleotide);
        }
    }
}
