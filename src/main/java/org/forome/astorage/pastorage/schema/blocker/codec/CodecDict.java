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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CodecDict extends Codec {

	private final List<Codec> mItemCodecs;

	public CodecDict(CodecData codecData, JSONObject schema_instr) {
		super(codecData, schema_instr);

		this.mItemCodecs = new ArrayList<>();
		for (Object o : (JSONArray)schema_instr.get("items")) {
			mItemCodecs.add(codecData.create((JSONObject) o));
		}
	}

	@Override
	public Object decode(Object int_obj, ADataDecodeEnv dataDecodeEnv) {
		if (int_obj == null) {
			return null;
		}

		JSONArray jIntObj = (JSONArray) int_obj;
		assert (jIntObj.size() <= mItemCodecs.size());

		JSONObject ret = new JSONObject();
		for (int idx = 0; idx < mItemCodecs.size(); idx++) {
			Codec it = mItemCodecs.get(idx);

			Object it_obj = null;
			if (idx < jIntObj.size()) {
				it_obj = jIntObj.get(idx);
			}

			if (int_obj != null) {
				ret.put(it.getName(), it.decode(it_obj, dataDecodeEnv));
			} else {
				ret.put(it.getName(), null);
			}
		}
		return ret;
	}


}
