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
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.forome.astorage.pastorage.schema.SchemaCommon;
import org.forome.core.struct.Position;

import java.util.List;

public class ADataDecodeEnv {

	private final SchemaCommon schemaCommon;

	private final int pos_diap1;
	private final int pos_diap2;

	private final List<Position> pos_seq;

	private final String[] mObjSeq;
	private final String[] mStrSeq;

	public ADataDecodeEnv(
			SchemaCommon schemaCommon,
			int pos_diap1, int pos_diap2,
			List<Position> pos_seq,
			String data_seq1, String data_seq2
	) {
		this.schemaCommon = schemaCommon;

		this.pos_diap1 = pos_diap1;
		this.pos_diap2 = pos_diap2;

		this.pos_seq = pos_seq;

		mObjSeq = data_seq1.split("\0");
		mStrSeq = data_seq2.split("\0");
	}

	public Object get(int idx) {
		String xdata = mObjSeq[idx];
		if (xdata == null) {
			return null;
		}

		Object int_obj;
		try {
			int_obj = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(xdata);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}

		return schemaCommon.codecList.decode(int_obj, this);
	}

	public Object getStr(int v_idx) {
		return mStrSeq[v_idx];
	}
}
