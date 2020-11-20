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

package org.forome.astorage.pastorage.schema.blocker.pagecluster;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.forome.astorage.pastorage.schema.SchemaCommon;
import org.forome.astorage.pastorage.schema.blocker.ADataDecodeEnv;
import org.forome.core.struct.Position;

import java.util.List;

public class ReadBlockPageCluster {

	private final List<Position> pos_seq;
	private final ADataDecodeEnv aDataDecodeEnv;

	public ReadBlockPageCluster(
			SchemaCommon schemaCommon,
			int pos_diap1, int pos_diap2
	) {
		this(
				schemaCommon,
				pos_diap1, pos_diap2,
				null, null
		);
	}

	public ReadBlockPageCluster(
			SchemaCommon schemaCommon,
			int pos_diap1, int pos_diap2,
			List<Position> pos_seq,
			List<Object> seq_data
	) {
		this.pos_seq = pos_seq;
		if (pos_seq != null) {
			this.aDataDecodeEnv = new ADataDecodeEnv(
					schemaCommon,
					pos_diap1, pos_diap2,
					pos_seq,
					(String) seq_data.get(1), (String) seq_data.get(2)
			);
		} else {
			this.aDataDecodeEnv = null;
		}
	}

	public JSONArray getRecord(Position position) {
		if (aDataDecodeEnv == null) {
			return null;
		}

		int idx = -1;
		for (int i = 0; i < pos_seq.size(); i++) {
			if (pos_seq.get(i).equals(position)) {
				idx = i;
				break;
			}
		}

		if (idx == -1) {
			return null;
		} else {
			return (JSONArray) aDataDecodeEnv.get(idx);
		}
	}
}
