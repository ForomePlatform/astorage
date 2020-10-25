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

package org.forome.astorage.pastorage.codec;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import org.forome.core.struct.Assembly;
import org.forome.core.struct.Chromosome;
import org.forome.core.struct.Position;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class HGKey {

    public static class TabItem {

        public final long start;
        public final long boundLength;
        public final long realLength;

        public TabItem(long start, long boundLength, long realLength) {
            this.start = start;
            this.boundLength = boundLength;
            this.realLength = realLength;
        }
    }

    public static Map<Chromosome, TabItem> hg19Tab = new HashMap<Chromosome, TabItem>() {{
        put(Chromosome.of("chrM"), new TabItem(1048576L, 2097152, 16571));
        put(Chromosome.CHR_1, new TabItem(3145728L, 250609664, 249250621));
        put(Chromosome.CHR_2, new TabItem(253755392L, 244318208, 243199373));
        put(Chromosome.CHR_3, new TabItem(498073600L, 199229440, 198022430));
        put(Chromosome.CHR_4, new TabItem(697303040L, 192937984, 191154276));
        put(Chromosome.CHR_5, new TabItem(890241024L, 182452224, 180915260));
        put(Chromosome.CHR_6, new TabItem(1072693248L, 173015040, 171115067));
        put(Chromosome.CHR_7, new TabItem(1245708288L, 160432128, 159138663));
        put(Chromosome.CHR_8, new TabItem(1406140416L, 147849216, 146364022));
        put(Chromosome.CHR_9, new TabItem(1553989632L, 142606336, 141213431));
        put(Chromosome.CHR_10, new TabItem(1696595968L, 137363456, 135534747));
        put(Chromosome.CHR_11, new TabItem(1833959424L, 136314880, 135006516));
        put(Chromosome.CHR_12, new TabItem(1970274304L, 135266304, 133851895));
        put(Chromosome.CHR_13, new TabItem(2105540608L, 116391936, 115169878));
        put(Chromosome.CHR_14, new TabItem(2221932544L, 109051904, 107349540));
        put(Chromosome.CHR_15, new TabItem(2330984448L, 103809024, 102531392));
        put(Chromosome.CHR_16, new TabItem(2434793472L, 92274688, 90354753));
        put(Chromosome.CHR_17, new TabItem(2527068160L, 82837504, 81195210));
        put(Chromosome.CHR_18, new TabItem(2609905664L, 79691776, 78077248));
        put(Chromosome.CHR_19, new TabItem(2689597440L, 60817408, 59128983));
        put(Chromosome.CHR_20, new TabItem(2750414848L, 65011712, 63025520));
        put(Chromosome.CHR_21, new TabItem(2815426560L, 49283072, 48129895));
        put(Chromosome.CHR_22, new TabItem(2864709632L, 52428800, 51304566));
        put(Chromosome.CHR_X, new TabItem(2917138432L, 157286400, 155270560));
        put(Chromosome.CHR_Y, new TabItem(3074424832L, 60817408, 59373566));
    }};

    public static Map<Chromosome, TabItem> hg38Tab = new HashMap<Chromosome, TabItem>() {{
        put(Chromosome.of("chrM"), new TabItem(1048576, 2097152, 16569));
        put(Chromosome.CHR_1, new TabItem(3145728, 250609664, 248956422));
        put(Chromosome.CHR_2, new TabItem(253755392, 243269632, 242193529));
        put(Chromosome.CHR_3, new TabItem(497025024, 200278016, 198295559));
        put(Chromosome.CHR_4, new TabItem(697303040, 191889408, 190214555));
        put(Chromosome.CHR_5, new TabItem(889192448, 183500800, 181538259));
        put(Chromosome.CHR_6, new TabItem(1072693248, 171966464, 170805979));
        put(Chromosome.CHR_7, new TabItem(1244659712, 160432128, 159345973));
        put(Chromosome.CHR_8, new TabItem(1405091840, 146800640, 145138636));
        put(Chromosome.CHR_9, new TabItem(1551892480, 139460608, 138394717));
        put(Chromosome.CHR_10, new TabItem(1691353088, 135266304, 133797422));
        put(Chromosome.CHR_11, new TabItem(1826619392, 136314880, 135086622));
        put(Chromosome.CHR_12, new TabItem(1962934272, 135266304, 133275309));
        put(Chromosome.CHR_13, new TabItem(2098200576, 116391936, 114364328));
        put(Chromosome.CHR_14, new TabItem(2214592512L, 109051904, 107043718));
        put(Chromosome.CHR_15, new TabItem(2323644416L, 103809024, 101991189));
        put(Chromosome.CHR_16, new TabItem(2427453440L, 92274688, 90338345));
        put(Chromosome.CHR_17, new TabItem(2519728128L, 84934656, 83257441));
        put(Chromosome.CHR_18, new TabItem(2604662784L, 81788928, 80373285));
        put(Chromosome.CHR_19, new TabItem(2686451712L, 59768832, 58617616));
        put(Chromosome.CHR_20, new TabItem(2746220544L, 66060288, 64444167));
        put(Chromosome.CHR_21, new TabItem(2812280832L, 48234496, 46709983));
        put(Chromosome.CHR_22, new TabItem(2860515328L, 52428800, 50818468));
        put(Chromosome.CHR_X, new TabItem(2912944128L, 157286400, 156040895));
        put(Chromosome.CHR_Y, new TabItem(3070230528L, 58720256, 57227415));
    }};

    public static byte[] encode(Assembly assembly, Position position) {
        Map<Chromosome, TabItem> tab = getTab(assembly);
        long long_pos = tab.get(position.chromosome).start + position.value;

        //Хак приводим к массиву байтов, а потом отрезаем первые 4 байта.
        byte[] bt = Longs.toByteArray(long_pos);

        byte[] bytes = new byte[4];
        System.arraycopy(bt, 4, bytes, 0, 4);
        return bytes;
    }

    private static Map<Chromosome, TabItem> getTab(Assembly assembly) {
        if (assembly == Assembly.GRCh37) {
            return hg19Tab;
        } else if (assembly == Assembly.GRCh38) {
            return hg38Tab;
        } else {
            throw new RuntimeException();
        }
    }
}
