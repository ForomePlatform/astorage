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

package org.forome.astorage.pastorage.schema.blocker.codec;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.forome.astorage.pastorage.schema.blocker.ADataDecodeEnv;
import org.forome.astorage.pastorage.schema.blocker.CodecData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CodecStr extends Codec {

	private final static Map<String, String> sGeneLetters = new HashMap<String, String>() {{
		put("A", "0");
		put("C", "1");
		put("G", "2");
		put("T", "3");
		put(null, "4");
	}};

	private Map<String, String> mPreDict;
	private int mPreShift;
	private Map<Integer, String> mPreDecode;

	private List<String> mDictList;
	private Map<String, Integer> mDict;

	private Boolean mRepeatable;

	public CodecStr(CodecData codecData, JSONObject schema_instr) {
		super(codecData, schema_instr);

		String opt = schema_instr.getAsString("opt");
		if ("dict".equals(opt)) {
			mDictList = ((JSONArray) schema_instr.get("dictlist")).stream()
					.map(o -> (String) o).collect(Collectors.toList());

			mDict = new HashMap<>();
			for (int i = 0; i < mDictList.size(); i++) {
				mDict.put(mDictList.get(i), i);
			}
		} else {
			if ("repeat".equals(opt)) {
				mRepeatable = true;
			} else if ("gene".equals(opt)) {
				mPreDict = sGeneLetters;
				mPreShift = mPreDict.size();

				mPreDecode = new HashMap<>();
				for (Map.Entry<String, String> entry : mPreDict.entrySet()) {
					mPreDecode.put(Integer.parseInt(entry.getValue()), entry.getKey());
				}
			} else {
				mRepeatable = false;
			}
		}


	}

	@Override
	public Object decode(Object int_obj, ADataDecodeEnv dataDecodeEnv) {
		if (int_obj == null) {
			return null;
		}
		int v_idx = (int) int_obj;

		if (mPreShift > 0) {
			if (v_idx < mPreShift) {
				return mPreDecode.get(v_idx);
			}
			v_idx -= mPreShift;
		}

		if (mDict != null) {
			return mDictList.get(v_idx);
		}
		return dataDecodeEnv.getStr(v_idx);
	}
}
