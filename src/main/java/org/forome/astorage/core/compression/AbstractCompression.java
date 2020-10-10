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

package org.forome.astorage.core.compression;

import org.forome.astorage.core.compression.exception.NotSupportCompression;
import org.forome.astorage.core.packer.PacketNucleotide;
import org.forome.astorage.core.utils.bits.IntegerDynamicLengthBits;
import org.forome.astorage.core.utils.bits.ShortBits;
import org.forome.astorage.core.utils.compression.GZIPCompression;
import org.forome.core.struct.nucleotide.Nucleotide;

import java.util.List;

public abstract class AbstractCompression {

    public static final short SHORT_NULL_VALUE = Short.MIN_VALUE;

    public static class UnpackObject {

        public final Object value;
        public final int byteSize;

        public UnpackObject(Object value, int byteSize) {
            this.value = value;
            this.byteSize = byteSize;
        }
    }

    public abstract byte[] pack(Class[] types, List<Object[]> items) throws NotSupportCompression;

    public abstract int unpackSize(Class[] types, int sizeInterval, byte[] bytes, int offsetBytes);

    public abstract Object[] unpackValues(Class[] types, byte[] bytes, int offsetBytes, int index);

    protected static UnpackObject unpackValue(Class type, byte[] bytes, int offsetBytes) {
        if (type == Short.class || type == short.class) {
            short value = ShortBits.fromByteArray(bytes, offsetBytes);
            if (value == SHORT_NULL_VALUE) {
                return new UnpackObject(null, ShortBits.BYTE_SIZE);
            } else {
                return new UnpackObject(value, ShortBits.BYTE_SIZE);
            }
        } else if (type == Nucleotide.class) {
            Nucleotide nucleotide = PacketNucleotide.fromByte(bytes[offsetBytes]);
            return new UnpackObject(nucleotide, PacketNucleotide.BYTE_SIZE);
        } else {
            throw new RuntimeException("Not support type: " + type);
        }
    }

    protected static byte[] pack(Class type, Object value) throws NotSupportCompression {
        if (type == Short.class || type == short.class) {
            return packShort((Short) value);
        } else if (type == Nucleotide.class) {
            return PacketNucleotide.toBytes((Nucleotide) value);
        } else {
            throw new NotSupportCompression();
        }
    }

    protected static byte[] packShort(Short value) {
        if (value == null) {
            return ShortBits.toByteArray(SHORT_NULL_VALUE);
        } else {
            if (value.equals(SHORT_NULL_VALUE)) {
                throw new RuntimeException("Conflict value");
            }
            return ShortBits.toByteArray(value);
        }
    }

    protected byte[] gzipCompress(byte[] value) {
        byte[] gzipValueBytes = GZIPCompression.compress(value);
        byte[] lenBytes = IntegerDynamicLengthBits.toByteArray(gzipValueBytes.length);
        byte[] result = new byte[lenBytes.length + gzipValueBytes.length];
        System.arraycopy(lenBytes, 0, result, 0, lenBytes.length);
        System.arraycopy(gzipValueBytes, 0, result, lenBytes.length, gzipValueBytes.length);
        return result;
    }

    protected byte[] gzipDecompress(byte[] bytes, int offsetBytes) {
        IntegerDynamicLengthBits.Value value = IntegerDynamicLengthBits.fromByteArray(bytes, offsetBytes);
        byte[] result = new byte[value.value];
        System.arraycopy(bytes, offsetBytes + value.byteSize, result, 0, value.value);
        return GZIPCompression.decompressToBytes(result);
    }
}
