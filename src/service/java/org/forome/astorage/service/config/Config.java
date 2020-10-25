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

package org.forome.astorage.service.config;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.forome.astorage.core.utils.RandomUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Config {

	public final int port;

	public final Path databaseHg37;

	public final Path databaseHg38;

	public final Path sourcePAStorage;

	public Config(Path configFile){
		if (!Files.exists(configFile)) {
			throw new RuntimeException("File: " + configFile.toString() + " not found");
		}
		JSONObject configFileJson;
		try (InputStream is = Files.newInputStream(configFile, StandardOpenOption.READ)) {
			configFileJson = (JSONObject) new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(is);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}

		JSONObject jFrontend = (JSONObject) configFileJson.get("frontend");
		port = jFrontend.getAsNumber("port").intValue();


		JSONObject jDatabase = (JSONObject) configFileJson.get("database");
		if (jDatabase.containsKey("hg37")) {
			databaseHg37 = Paths.get(jDatabase.getAsString("hg37")).toAbsolutePath();
			if (!Files.exists(databaseHg37) || !Files.isDirectory(databaseHg37)) {
				throw new RuntimeException("Database does not exists: " + databaseHg37);
			}
		} else {
			databaseHg37 = null;
		}
		if (jDatabase.containsKey("hg38")) {
			databaseHg38 = Paths.get(jDatabase.getAsString("hg38")).toAbsolutePath();
			if (!Files.exists(databaseHg38) || !Files.isDirectory(databaseHg38)) {
				throw new RuntimeException("Database does not exists: " + databaseHg38);
			}
		} else {
			databaseHg38 = null;
		}


		sourcePAStorage = Paths.get(configFileJson.getAsString("pastorage"));
		if (!Files.exists(sourcePAStorage) || !Files.isDirectory(sourcePAStorage)) {
			throw new RuntimeException("Source paStorage does not exists: " + sourcePAStorage);
		}
	}

}
