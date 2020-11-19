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

import org.forome.astorage.pastorage.codec.HGKey;
import org.forome.core.struct.Assembly;
import org.forome.core.struct.Position;

public class ABlockerIOPosPager {

	private final ABlockerPageCluster blockerPageCluster;

	public ABlockerIOPosPager(ABlockerPageCluster blockerPageCluster) {
		this.blockerPageCluster = blockerPageCluster;
	}

	public ReadBlockPosPager.Result seekPos(Assembly assembly, Position position) {
		byte[] pageXKey = getPageXKey(assembly, position);
		ReadBlockPosPager readBlockPosPager = new ReadBlockPosPager(blockerPageCluster, assembly, pageXKey, blockerPageCluster.getData(pageXKey));
		return readBlockPosPager.seekPos(position);
	}

	public static byte[] getPageXKey(Assembly assembly, Position position) {
		byte[] keyEncode = HGKey.encode(assembly, position);
		byte[] key = new byte[2];
		System.arraycopy(keyEncode, 0, key, 0, 2);
		return key;
	}
}
