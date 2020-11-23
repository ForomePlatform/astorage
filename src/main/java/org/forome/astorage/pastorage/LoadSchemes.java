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

package org.forome.astorage.pastorage;

import org.forome.astorage.pastorage.schema.Schema;
import org.forome.astorage.pastorage.schema.SchemaCommon;
import org.forome.astorage.pastorage.schema.SchemaFasta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class LoadSchemes {

	private final static Logger log = LoggerFactory.getLogger(LoadSchemes.class);

	private final Map<String, Schema> schemes;

	public LoadSchemes(Path source) throws IOException {
		Path pathSchemes = source.resolve("schema").toAbsolutePath();
		schemes = Files.walk(pathSchemes)
				.filter(path -> !path.equals(pathSchemes))
				.filter(Files::isDirectory)
				.map(path -> {
					String schemaName = path.getFileName().toString();
					Path schemaFile = path.resolve(schemaName + ".json").toAbsolutePath();
					if (!Files.exists(schemaFile)) {
						log.warn("File schema is not exists(ignored): {}", schemaFile);
						return null;
					}

					Path schemaDatabase = source.resolve("rdbs").resolve(schemaName).toAbsolutePath();
					if (!Files.exists(schemaDatabase)) {
						log.warn("Database schema is not exists(ignored): {}", schemaDatabase);
						return null;
					}

					//TODO Необходимо удалить - этот фильтр
					if (!schemaName.equals(SchemaFasta.SCHEMA_FASTA_NAME)
							&& !schemaName.equals(SchemaCommon.SCHEMA_DBSNP_NAME)
							&& !schemaName.equals(SchemaCommon.SCHEMA_DBNSFP_NAME)
							&& !schemaName.equals(SchemaCommon.SCHEMA_GNOMAD_NAME)
							&& !schemaName.equals(SchemaCommon.SCHEMA_GERP_NAME)
					) {
						log.warn("Database schema is ignored: {}", schemaDatabase);
						return null;
					}

					return Schema.build(schemaName, schemaFile, schemaDatabase);
				})
				.filter(Objects::nonNull)
				.collect(Collectors.toMap(schema -> schema.name, schema -> schema));
	}

	public Map<String, Schema> getSchemes() {
		return schemes;
	}
}
