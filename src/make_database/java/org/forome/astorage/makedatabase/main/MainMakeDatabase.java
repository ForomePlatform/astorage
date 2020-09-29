/*
 *  Copyright (c) 2019. Vladimir Ulitin, Partners Healthcare and members of Forome Association
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

package org.forome.astorage.makedatabase.main;

import org.forome.astorage.makedatabase.main.argument.Arguments;
import org.forome.astorage.makedatabase.main.argument.ArgumentsMake;
import org.forome.astorage.makedatabase.main.argument.ParserArgument;
import org.forome.astorage.makedatabase.make.MakeDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainMakeDatabase {

	private final static Logger log = LoggerFactory.getLogger(MainMakeDatabase.class);

	public static void main(String[] args) {
		try {
			ParserArgument argumentParser = new ParserArgument(args);
			Arguments arguments = argumentParser.arguments;

			ArgumentsMake argumentsMake = (ArgumentsMake) arguments;
			try (MakeDatabase makeDatabase = new MakeDatabase(argumentsMake)) {
				makeDatabase.buildInfo();
				makeDatabase.buildRecords();
			}

			System.exit(0);
		} catch (Throwable e) {
			log.error("Exception", e);
			System.exit(2);
			return;
		}
	}
}
