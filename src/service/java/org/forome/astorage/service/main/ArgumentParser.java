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

package org.forome.astorage.service.main;

import org.apache.commons.cli.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ArgumentParser {

	private static final String OPTION_PATH_CONFIG = "config";

	public final Path configFile;

	public ArgumentParser(String[] args) throws InterruptedException {
		Options options = new Options()
				.addOption(Option.builder()
						.longOpt(OPTION_PATH_CONFIG)
						.hasArg(true)
						.optionalArg(true)
						.desc("Absolute path to config file")
						.build());

		try {
			CommandLine cmd = new DefaultParser().parse(options, args);

			String sConfigFile = cmd.getOptionValue(OPTION_PATH_CONFIG, "config.json");
			configFile = Paths.get(sConfigFile).toAbsolutePath();
			if (!Files.exists(configFile)) {
				throw new RuntimeException("File: " + configFile.toString() + " not found");
			}

		} catch (ParseException | IllegalArgumentException ex) {
			System.out.println(ex.getMessage());
			new HelpFormatter().printHelp("", options);

			throw new InterruptedException();
		}
	}
}
