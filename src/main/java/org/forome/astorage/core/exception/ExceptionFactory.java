/*
 Copyright (c) 2019. Vladimir Ulitin, Partners Healthcare and members of Forome Association

 Developed by Vladimir Ulitin and Michael Bouzinier

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

	 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
*/

package org.forome.astorage.core.exception;

import java.util.Map;

public class ExceptionFactory {

	public AStorageException build(String code, String comment, Map<String, Object> parameters) {
		return build(code, comment, parameters, null);
	}

	public AStorageException build(String code, Map<String, Object> parameters) {
		return build(code, null, parameters, null);
	}

	public AStorageException build(String code, String comment) {
		return build(code, comment, null, null);
	}

	public AStorageException build(String code, Throwable e) {
		return build(code, null, null, e);
	}

	public AStorageException build(String code) {
		return build(code, null, null, null);
	}

	public AStorageException build(String code, String comment, Map<String, Object> parameters, Throwable cause) {
		return new AStorageException(code, comment, parameters, cause);
	}
}
