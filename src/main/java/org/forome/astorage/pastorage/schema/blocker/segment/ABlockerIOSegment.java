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

package org.forome.astorage.pastorage.schema.blocker.segment;

import com.google.common.collect.Lists;
import net.minidev.json.JSONObject;
import org.forome.astorage.core.exception.ExceptionBuilder;
import org.forome.astorage.pastorage.codec.HGKey;
import org.forome.astorage.pastorage.schema.SchemaCommon;
import org.forome.astorage.pastorage.schema.blocker.ABlockerIO;
import org.forome.astorage.pastorage.schema.blocker.ReadBlock;
import org.forome.astorage.pastorage.schema.blocker.fields.BytesFieldsSupport;
import org.forome.astorage.pastorage.schema.blocker.fields.codec.BZ2Support;
import org.forome.astorage.pastorage.utils.CompressorUtils;
import org.forome.core.struct.Assembly;
import org.forome.core.struct.Position;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDBException;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class ABlockerIOSegment extends ABlockerIO {

	private final int mPosFrame;

	private final BytesFieldsSupport mBytesSupp;

	public ABlockerIOSegment(SchemaCommon schemaCommon, JSONObject jSchemaIO) {
		super(schemaCommon);

		mPosFrame = jSchemaIO.getAsNumber("pos-frame").intValue();

		if (schemaCommon.name.equals(SchemaCommon.SCHEMA_SPLICEAI_NAME)) {
			mBytesSupp = new BytesFieldsSupport(
					new BZ2Support(),
					new BZ2Support()
			);
		} else {
			mBytesSupp = null;
		}
	}

	@Override
	public ReadBlock openReadBlock(Assembly assembly, Position position) {
		return new ReadBlockSegment(this, assembly, position);
	}

	public Position getBasePos(Position position) {
		return new Position(
				position.chromosome,
				position.value - (position.value % mPosFrame)
		);
	}


	public List<Object> getBlock(Assembly assembly, Position mBasePos) {
		byte[] xdata = getData(assembly, mBasePos);
		if (xdata == null) {
			return null;
		}
		if (mBytesSupp!=null) {
			return mBytesSupp.unpack(xdata);
		} else {
			byte[] bytes = CompressorUtils.uncompress(xdata);
			return Lists.newArrayList(new String(bytes, StandardCharsets.UTF_8));
		}
	}

	private byte[] getData(Assembly assembly, Position mBasePos) {
		byte[] key = HGKey.encode(assembly, mBasePos);

		String columnFamilyName = schemaCommon.name + "_base";
		ColumnFamilyHandle columnFamily = schemaCommon.getColumnFamily(columnFamilyName);

		byte[] value;
		try {
			value = schemaCommon.rocksDBDatabase.rocksDB.get(columnFamily, key);
		} catch (RocksDBException ex) {
			throw ExceptionBuilder.buildDatabaseException(ex);
		}
		return value;
	}
}
