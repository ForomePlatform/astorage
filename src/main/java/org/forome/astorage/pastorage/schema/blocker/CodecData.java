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

package org.forome.astorage.pastorage.schema.blocker;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.forome.astorage.pastorage.schema.blocker.codec.Codec;
import org.forome.astorage.pastorage.schema.blocker.codec.CodecDict;
import org.forome.astorage.pastorage.schema.blocker.codec.CodecList;
import org.forome.astorage.pastorage.schema.blocker.codec.CodecStr;

public class CodecData {

//	private final JSONObject schema_instr;

//	public final CodecList codecList;
//	public final CodecDict codecDict;

	public CodecData(JSONObject schema_instr) {
//		this.schema_instr = schema_instr;

//		this.codecDict = new CodecDict(this);
//		this.codecList = new CodecList(this);
	}

	public Codec create(JSONObject schema_instr) {
		String type = schema_instr.getAsString("tp");
		switch (type) {
			case "list":
				return new CodecList(this, schema_instr);
			case "dict":
				return new CodecDict(this, schema_instr);
			case "str":
				return new CodecStr(this, schema_instr);
			default:
				throw new RuntimeException();
		}

	}
}
