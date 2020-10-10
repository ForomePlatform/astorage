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
import org.forome.astorage.core.utils.ThreadPoolService;
import org.forome.core.struct.Interval;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Compression {

    private final Class[] types;
    private final int sizeInterval;

    public Compression(Class[] types, Interval interval) {
        this(types,interval.end - interval.start + 1);
    }

    public Compression(Class[] types, int sizeInterval) {
        this.types = types;
        this.sizeInterval = sizeInterval;
    }

    public byte[] pack(List<Object[]> items) {
        if (items.size() != sizeInterval) {
            throw new IllegalArgumentException();
        }

        //Упаковываем различными способами и выбираем сжатие с минимальным размером
        List<CompletableFuture<byte[]>> futures = new ArrayList<>();
        for (TypeCompression type : TypeCompression.values()) {
            TypeCompression iType = type;
            futures.add(
                    CompletableFuture.supplyAsync(
                            () -> {
                                byte[] pack = iType.compression.pack(types, items);

                                byte[] result = new byte[pack.length + 1];
                                result[0] = iType.value;
                                System.arraycopy(pack, 0, result, 1, pack.length);
                                return result;
                            },
                            ThreadPoolService.threadPool
                    ));
        }


        byte[] optimalPack = null;
        for (CompletableFuture<byte[]> future : futures) {
            byte[] pack;
            try {
                pack = future.get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                if (cause instanceof NotSupportCompression) {
                    continue;
                } else {
                    throw new RuntimeException(e);
                }
            }

            if (optimalPack == null || optimalPack.length > pack.length) {
                optimalPack = pack;
            }
        }

        return optimalPack;
    }

    public int unpackSize(byte[] bytes, int offsetBytes) {
        TypeCompression typeCompression = TypeCompression.get(bytes[offsetBytes]);
        return typeCompression.compression.unpackSize(types, sizeInterval, bytes, offsetBytes + 1) + 1;
    }

    public Object[] unpackValues(byte[] bytes, int offsetBytes, int index) {
        TypeCompression typeCompression = TypeCompression.get(bytes[offsetBytes]);
        return typeCompression.compression.unpackValues(types, bytes, offsetBytes + 1, index);
    }
}
