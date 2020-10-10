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
import org.forome.astorage.core.packer.PacketNucleotide;
import org.forome.astorage.core.utils.bits.IntegerDynamicLengthBits;
import org.forome.astorage.core.utils.bits.ShortBits;
import org.forome.core.struct.nucleotide.Nucleotide;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Простое последовательное укладывание значений
 */
public class CompressionOrder extends AbstractCompression {

    private final boolean overGzip;

    public CompressionOrder() {
        this(false);
    }

    public CompressionOrder(boolean overGzip) {
        this.overGzip = overGzip;
    }

    public byte[] pack(Class[] types, List<Object[]> items) throws NotSupportCompression {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        for (Object[] values : items) {
            if (values.length != types.length) {
                throw new IllegalStateException();
            }
            for (int i = 0; i < values.length; i++) {
                Object value = values[i];
                if (value != null && value.getClass() != types[i]) {
                    throw new IllegalStateException();
                }
                try {
                    os.write(pack(types[i], value));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        byte[] bytes = os.toByteArray();

        //Архивируем
        if (overGzip) {
            bytes = gzipCompress(bytes);
        }
        return bytes;
    }

    @Override
    public int unpackSize(Class[] types, int sizeInterval, byte[] bytes, int offsetBytes) {
        if (overGzip) {
            IntegerDynamicLengthBits.Value value = IntegerDynamicLengthBits.fromByteArray(bytes, offsetBytes);
            return value.byteSize + value.value;
        } else {
            return sizeRecord(types) * sizeInterval;
        }
    }

    public static int sizeRecord(Class[] types) {
        int size = 0;
        for (Class type : types) {
            if (type == Short.class || type == short.class) {
                size += ShortBits.BYTE_SIZE;
            } else if (type == Nucleotide.class) {
                size += PacketNucleotide.BYTE_SIZE;
            } else {
                throw new RuntimeException("Not support type: " + type);
            }
        }
        return size;
    }

    @Override
    public Object[] unpackValues(Class[] types, byte[] bytes, int offsetBytes, int index) {
        if (overGzip) {
            return _unpackValues(types, gzipDecompress(bytes, offsetBytes), 0, index);
        } else {
            return _unpackValues(types, bytes, offsetBytes, index);
        }
    }

    private static Object[] _unpackValues(Class[] types, byte[] bytes, int offsetBytes, int index) {
        int offset = offsetBytes + sizeRecord(types) * index;

        Object[] value = new Object[types.length];
        for (int i = 0; i < types.length; i++) {
            Class type = types[i];

            UnpackObject unpackObject = unpackValue(type, bytes, offset);
            value[i] = unpackObject.value;
            offset += unpackObject.byteSize;
        }
        return value;
    }

}
