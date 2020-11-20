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

import com.google.common.primitives.Ints;
import org.forome.core.struct.Assembly;
import org.forome.core.struct.Position;

public class ReadBlockPosPager {

	public class Result {
		public final Position position;
		public final int pos1;
		public final int pos2;

		public Result(Position position, int pos1, int pos2) {
			this.position = position;
			this.pos1 = pos1;
			this.pos2 = pos2;
		}
	}

	private final ABlockerPageCluster mBlocker;
	private final byte[] mPageXKey;
	private final Position startBlockPosition;
	private final Position endBlockPosition;
	private final Integer[] mArray;
	private final int mLen;

	public ReadBlockPosPager(ABlockerPageCluster mBlocker, Assembly assembly, byte[] pageXKey, byte[] posArrayBytes) {
		this.mBlocker = mBlocker;
		this.mPageXKey = pageXKey;
		this.startBlockPosition = mBlocker.getStartKey(assembly, pageXKey);
		this.endBlockPosition = new Position(
				startBlockPosition.chromosome,
				startBlockPosition.value + 0x10000
		);
		if (posArrayBytes == null) {
			mArray = new Integer[0];
		} else {
			mArray = new Integer[posArrayBytes.length / 2];
			for (int i = 0; i < mArray.length; i++) {
				mArray[i] = Ints.fromBytes((byte) 0, (byte) 0, posArrayBytes[i * 2 + 1], posArrayBytes[i * 2]);
			}
		}
		mLen = mArray.length;
	}

	public Result seekPos(Position position) {
		int idx = bisectLeft(mArray, position.value - startBlockPosition.value);
		if (idx < mLen) {
			int ret = mArray[idx] + startBlockPosition.value;
			return new Result(
					new Position(position.chromosome, ret),
					(idx == 0) ? mArray[mArray.length - 1] : mArray[idx - 1] + startBlockPosition.value + 1,
					(idx > 0) ? ret + 1 : startBlockPosition.value
			);
		}
		return new Result(
				null,
				(mArray.length > 0) ? mArray[mArray.length - 1] : 0 + startBlockPosition.value,
				(mLen > 0) ? endBlockPosition.value : startBlockPosition.value
		);
	}

	private static int bisectLeft(Integer[] mArray, int value) {
		for (int i = 0; i < mArray.length; i++) {
			if (mArray[i] >= value) {
				return i;
			}
		}
		return mArray.length;
	}
}
