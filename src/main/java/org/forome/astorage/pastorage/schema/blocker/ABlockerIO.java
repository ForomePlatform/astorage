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
import org.forome.astorage.pastorage.codec.HGKey;
import org.forome.astorage.pastorage.schema.SchemaCommon;
import org.forome.astorage.pastorage.schema.blocker.fields.BytesFieldsSupport;
import org.forome.astorage.pastorage.schema.blocker.fields.codec.BZ2Support;
import org.forome.astorage.pastorage.schema.blocker.fields.codec.PosSeqSupport;
import org.forome.astorage.pastorage.schema.blocker.pagecluster.ABlockerPageCluster;
import org.forome.astorage.pastorage.schema.blocker.segment.ABlockerIOSegment;
import org.forome.core.struct.Assembly;
import org.forome.core.struct.Position;

public abstract class ABlockerIO {

	public final SchemaCommon schemaCommon;
	public final BytesFieldsSupport bytesFieldsSupport;

	public ABlockerIO(SchemaCommon schemaCommon) {
		this.schemaCommon = schemaCommon;

		this.bytesFieldsSupport = new BytesFieldsSupport(
				new PosSeqSupport(),
				new BZ2Support(),
				new BZ2Support()
		);
	}

	public JSONArray getRecord(Assembly assembly, Position position) {
		ReadBlock res = openReadBlock(assembly, position);
		return res.getRecord(position);
	}

	public abstract ReadBlock openReadBlock(Assembly assembly, Position position);

	public static byte[] getXKey(Assembly assembly, Position position) {
		return HGKey.encode(assembly, position);
	}

	public static ABlockerIO build(SchemaCommon schemaCommon, JSONObject jSchemaIO) {
		String blockType = jSchemaIO.getAsString("block-type");
		if ("page-cluster".equals(blockType)) {
			return new ABlockerPageCluster(schemaCommon);
		} else if ("segment".equals(blockType)) {
			return new ABlockerIOSegment(schemaCommon, jSchemaIO);
		} else {
			throw new RuntimeException("Not support blockType: " + blockType);
		}
	}
}
