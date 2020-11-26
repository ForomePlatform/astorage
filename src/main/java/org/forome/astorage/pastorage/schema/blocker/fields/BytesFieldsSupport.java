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

package org.forome.astorage.pastorage.schema.blocker.fields;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import org.forome.astorage.pastorage.schema.blocker.fields.codec.Codec;

import java.util.ArrayList;
import java.util.List;

public class BytesFieldsSupport {

	private final List<Codec> mConvSeq;

	public BytesFieldsSupport(Codec... codecs) {
		this.mConvSeq = Lists.newArrayList(codecs);
	}

	public List<Object> unpack(byte[] xdata) {
		List<Integer> len_header = new ArrayList<>();
		for (int i = 0; i < mConvSeq.size() - 1; i++) {
			len_header.add(Ints.fromBytes(
					xdata[i * 4], xdata[i * 4 + 1], xdata[i * 4 + 2], xdata[i * 4 + 3]
			));
		}

		List<Object> ret = new ArrayList<>();

		int pos = (mConvSeq.size() - 1) * 4;
		for (int i = 0; i < len_header.size(); i++) {
			byte[] iBytes = new byte[len_header.get(i)];
			System.arraycopy(xdata, pos, iBytes, 0, iBytes.length);

			Codec codec = mConvSeq.get(i);
			ret.add(codec.fromBytes(iBytes));

			pos += iBytes.length;
		}

		//last codec
		byte[] iBytes = new byte[xdata.length-pos];
		System.arraycopy(xdata, pos, iBytes, 0, iBytes.length);
		Codec codec = mConvSeq.get(mConvSeq.size()-1);
		ret.add(codec.fromBytes(iBytes));

		return ret;
	}

}
