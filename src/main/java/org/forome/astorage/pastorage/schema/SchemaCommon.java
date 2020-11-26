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

package org.forome.astorage.pastorage.schema;

import net.minidev.json.JSONObject;
import org.forome.astorage.pastorage.record.Record;
import org.forome.astorage.pastorage.schema.blocker.ABlockerIO;
import org.forome.astorage.pastorage.schema.blocker.CodecData;
import org.forome.astorage.pastorage.schema.blocker.codec.Codec;
import org.forome.core.struct.Assembly;
import org.forome.core.struct.Position;

import java.nio.file.Path;

public class SchemaCommon extends Schema {

	public static final String SCHEMA_DBSNP_NAME = "dbSNP";
	public static final String SCHEMA_DBNSFP_NAME = "dbNSFP";
	public static final String SCHEMA_GNOMAD_NAME = "gnomAD";
	public static final String SCHEMA_GERP_NAME = "Gerp";
	public static final String SCHEMA_SPLICEAI_NAME = "SpliceAI";

	public final ABlockerIO blocker;
	public final Codec codecList;

	protected SchemaCommon(String name, Path schemaFile, Path schemaDatabase) {
		super(name, schemaFile, schemaDatabase);

		JSONObject jSchemaFile = parseSchemaFile(schemaFile);

		blocker = ABlockerIO.build(this, (JSONObject) jSchemaFile.get("io"));

		CodecData codecData = new CodecData(
				(JSONObject) jSchemaFile.get("top")
		);
		codecList = codecData.create(
				(JSONObject) jSchemaFile.get("top")
		);
	}

	@Override
	public Record getRecord(Assembly assembly, Position position) {
		throw new RuntimeException();
	}
}

