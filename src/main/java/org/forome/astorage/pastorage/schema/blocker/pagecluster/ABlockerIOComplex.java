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

import org.forome.astorage.core.exception.ExceptionBuilder;
import org.forome.astorage.pastorage.schema.SchemaCommon;
import org.forome.astorage.pastorage.schema.blocker.ABlockerIO;
import org.forome.astorage.pastorage.schema.blocker.fields.BytesFieldsSupport;
import org.forome.core.struct.Assembly;
import org.forome.core.struct.Position;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDBException;

import java.util.List;

public class ABlockerIOComplex {

	protected final SchemaCommon schemaCommon;
	protected final BytesFieldsSupport bytesFieldsSupport;

	public ABlockerIOComplex(ABlockerIO aBlockerIO) {
		this.schemaCommon = aBlockerIO.schemaCommon;
		this.bytesFieldsSupport = aBlockerIO.bytesFieldsSupport;
	}

	public List<Object> getBlock(Assembly assembly, Position position) {
		String columnFamilyName = schemaCommon.name + "_base";
		ColumnFamilyHandle columnFamily = schemaCommon.getColumnFamily(columnFamilyName);

		byte[] xdata;
		try {
			xdata = schemaCommon.rocksDBDatabase.rocksDB.get(columnFamily, ABlockerIO.getXKey(assembly, position));
		} catch (RocksDBException ex) {
			throw ExceptionBuilder.buildDatabaseException(ex);
		}

		if (xdata == null) {
			return null;
		}

		return unpack(xdata);
	}


	private List<Object> unpack(byte[] xdata) {
		return bytesFieldsSupport.unpack(xdata);
	}
}
