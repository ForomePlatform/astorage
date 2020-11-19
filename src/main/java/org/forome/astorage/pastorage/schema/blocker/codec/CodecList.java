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

public class CodecList extends Codec {

	private final Codec mItemCodec;

	public CodecList(CodecData codecData, JSONObject schema_instr) {
		super(codecData, schema_instr);
		mItemCodec = codecData.create((JSONObject) schema_instr.get("item"));
	}

	public Object decode(Object int_obj, ADataDecodeEnv dataDecodeEnv) {
		if (int_obj == null) {
			return null;
		}

		JSONArray result = new JSONArray();
		for (Object oItem : (JSONArray) int_obj) {
			result.add(mItemCodec.decode(oItem, dataDecodeEnv));
		}
		return result;
	}
}
