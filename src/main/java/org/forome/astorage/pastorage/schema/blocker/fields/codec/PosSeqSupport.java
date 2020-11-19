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

package org.forome.astorage.pastorage.schema.blocker.fields.codec;

import org.apache.commons.compress.utils.Lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class PosSeqSupport extends Codec {

	@Override
	public List<Integer> fromBytes(byte[] xbytes) {
		List<Integer> pseq = new ArrayList<>();
		pseq.add(0);
		for (int i = 0; i < xbytes.length; i += 2) {
			int cnt = Byte.toUnsignedInt(xbytes[i]);
			int delta = Byte.toUnsignedInt(xbytes[i + 1]);
			for (int j = 0; j < cnt; j++) {
				int value = pseq.get(pseq.size() - 1) - delta - 1;
				pseq.add(value);
			}
		}

		Collections.reverse(pseq);
		return pseq;
	}
}
