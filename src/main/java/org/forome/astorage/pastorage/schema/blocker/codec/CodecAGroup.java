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
import java.util.List;
import java.util.stream.Collectors;

public class CodecAGroup extends Codec {

	private final List<String> mGroup;
	private String mGroupName;
	private final List<Codec> mItemCodecs;

	public CodecAGroup(CodecData codecData, JSONObject schema_instr) {
		super(codecData, schema_instr);

		this.mGroup = ((JSONArray) schema_instr.get("group")).stream().map(o -> (String) o).collect(Collectors.toList());

		this.mGroupName = schema_instr.getAsString("group-name");

		this.mItemCodecs = new ArrayList<>();
		for (Object o : (JSONArray) schema_instr.get("items")) {
			this.mItemCodecs.add(codecData.create((JSONObject) o));
		}

	}

	@Override
	public boolean isAggregate() {
		return true;
	}

	@Override
	public Object decode(Object group_obj, ADataDecodeEnv dataDecodeEnv) {
		JSONObject ret = new JSONObject();
		for (Object oint_obj : ((JSONArray) group_obj)) {
			JSONArray int_obj = (JSONArray) oint_obj;

			String name = mGroup.get((Integer) int_obj.get(0));
			JSONObject grp_obj = new JSONObject();

			for (int idx = 0; idx < mItemCodecs.size(); idx++) {
				Codec it = mItemCodecs.get(idx);

				Number it_obj = null;
				if (idx + 1 < int_obj.size()) {
					it_obj = (Number) int_obj.get(idx + 1);
				}

				if (it.isAggregate()) {
					throw new RuntimeException("Not implemented");
				} else {
					if (it_obj != null) {
						grp_obj.put(it.getName(), it.decode(it_obj, dataDecodeEnv));
					} else {
						grp_obj.put(it.getName(), null);
					}
				}
			}
			ret.put(name, grp_obj);
		}
		return ret;
	}
}
