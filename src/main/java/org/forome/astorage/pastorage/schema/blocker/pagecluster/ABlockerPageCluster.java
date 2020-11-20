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

import net.minidev.json.JSONObject;
import org.forome.astorage.core.exception.ExceptionBuilder;
import org.forome.astorage.core.utils.RandomUtils;
import org.forome.astorage.pastorage.codec.HGKey;
import org.forome.astorage.pastorage.schema.SchemaCommon;
import org.forome.astorage.pastorage.schema.blocker.ABlockerIO;
import org.forome.core.struct.Assembly;
import org.forome.core.struct.Position;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDBException;

import java.util.List;
import java.util.stream.Collectors;

public class ABlockerPageCluster extends ABlockerIO {

	private final ABlockerIOPosPager mPagerIO;

	public ABlockerPageCluster(SchemaCommon schemaCommon, JSONObject jSchemaIO) {
		super(schemaCommon);
		this.mPagerIO = new ABlockerIOPosPager(this);
	}

	public byte[] getData(byte[] key) {
		String columnFamilyName = schemaCommon.name + "_pager";
		ColumnFamilyHandle columnFamily = schemaCommon.getColumnFamily(columnFamilyName);

		byte[] value;
		try {
			value = schemaCommon.rocksDBDatabase.rocksDB.get(columnFamily, key);
		} catch (RocksDBException ex) {
			throw ExceptionBuilder.buildDatabaseException(ex);
		}

//		schemaCommon.getRecord()
		return value;
	}

	@Override
	public ReadBlockPageCluster openReadBlock(Assembly assembly, Position position) {
		ReadBlockPosPager.Result result = mPagerIO.seekPos(assembly, position);
		if (result.position != null) {
			ABlockerIOComplex aBlockerIOComplex = new ABlockerIOComplex(this);
			List<Object> seq_data = aBlockerIOComplex.getBlock(assembly, result.position);

			List<Position> pos_seq = ((List<Integer>) seq_data.get(0)).stream()
					.map(iItem -> new Position(result.position.chromosome, result.position.value + iItem))
					.collect(Collectors.toList());

			return new ReadBlockPageCluster(
					schemaCommon,
					result.pos1, result.pos2,
					pos_seq,
					seq_data
			);
		} else {
			return new ReadBlockPageCluster(
					schemaCommon,
					result.pos1, result.pos2
			);
		}

//		if key_base is not None:
//		seq_data = self._getBlock(key_base)
//		pos_seq = [pos + key_base[1] for pos in seq_data[0]]
//		return _ReadBlock_PageCluster(self, chrom, pos_diap,
//				pos_seq, seq_data[1:])
//		return _ReadBlock_PageCluster(self, chrom, pos_diap)
	}

	public Position getStartKey(Assembly assembly, byte[] pageXKey) {
		byte[] startPageXKey = new byte[4];
		System.arraycopy(pageXKey, 0, startPageXKey, 0, 2);
		return HGKey.decode(assembly, startPageXKey);
	}
}
