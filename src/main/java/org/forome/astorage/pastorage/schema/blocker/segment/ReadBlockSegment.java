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

import net.minidev.json.JSONArray;
import org.forome.astorage.pastorage.schema.blocker.ADataDecodeEnv;
import org.forome.astorage.pastorage.schema.blocker.ReadBlock;
import org.forome.core.struct.Assembly;
import org.forome.core.struct.Position;

import java.nio.charset.StandardCharsets;

public class ReadBlockSegment implements ReadBlock {

	private final ABlockerIOSegment mBlocker;
	private final Position position;

	private final Position mBasePos;

	private final ADataDecodeEnv mDataSeq;

	public ReadBlockSegment(ABlockerIOSegment mBlocker, Assembly assembly, Position position) {
		this.mBlocker = mBlocker;
		this.position = position;

		mBasePos = mBlocker.getBasePos(position);
		byte[] data_seq = mBlocker.getBlock(assembly, mBasePos);

		if (data_seq != null) {
			mDataSeq = new ADataDecodeEnv(
					mBlocker.schemaCommon,
					0, 0,
					null,
					new String(data_seq, StandardCharsets.UTF_8), null
			);
		} else {
			mDataSeq = null;
		}
	}

	@Override
	public JSONArray getRecord(Position position) {
		if (!this.position.equals(position)) {
			throw new RuntimeException();
		}
		if (mDataSeq == null) {
			return new JSONArray();
		}

		int idx = position.value - mBasePos.value;
		assert idx >= 0;

		if (idx < mDataSeq.getLength()) {
			JSONArray result = new JSONArray();
			result.add(mDataSeq.get(idx));
			return result;
		} else {
			return new JSONArray();
		}
	}
}
