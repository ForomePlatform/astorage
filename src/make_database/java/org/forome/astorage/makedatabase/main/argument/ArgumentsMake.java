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

package org.forome.astorage.makedatabase.main.argument;

import com.google.common.base.Strings;
import org.apache.commons.cli.CommandLine;
import org.forome.core.struct.Assembly;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ArgumentsMake extends Arguments {

	public final Path database;

	public final Assembly assembly;

	public final Path gerpHg19;

	public final Path fastaHg19;

	public final Path fastaHg38;

	public ArgumentsMake(CommandLine cmd) {
		super(cmd);

		String strPathDatabase = cmd.getOptionValue(ParserArgument.OPTION_PATH_DATABASE);
		if (Strings.isNullOrEmpty(strPathDatabase)) {
			throw new IllegalArgumentException("Missing path database");
		}
		database = Paths.get(strPathDatabase).toAbsolutePath();
		if (Files.exists(database) && !Files.isDirectory(database)) {
			throw new IllegalArgumentException("path database does file: " + database);
		}

		String strAssembly = cmd.getOptionValue(ParserArgument.OPTION_ASSEMBLY);
		if (Strings.isNullOrEmpty(strPathDatabase)) {
			throw new IllegalArgumentException("Missing assembly");
		}
		assembly = Assembly.valueOf(strAssembly);


		String strSourceGerpHg19 = cmd.getOptionValue(ParserArgument.OPTION_SOURCE_GERP_HG19);
		if (!Strings.isNullOrEmpty(strSourceGerpHg19)) {
			gerpHg19 = Paths.get(strSourceGerpHg19).toAbsolutePath();
			if (!Files.exists(gerpHg19)) {
				throw new IllegalArgumentException("Gerp(hg19) file does not exists: " + gerpHg19);
			}
		} else {
			gerpHg19 = null;
		}

		String strSourceFastaHg19 = cmd.getOptionValue(ParserArgument.OPTION_SOURCE_FASTA_HG19);
		if (!Strings.isNullOrEmpty(strSourceFastaHg19)) {
			fastaHg19 = Paths.get(strSourceFastaHg19).toAbsolutePath();
			if (!Files.exists(fastaHg19)) {
				throw new IllegalArgumentException("Fasta (hg19) file does not exists: " + fastaHg19);
			}
		} else {
			fastaHg19 = null;
		}

		String strSourceFastaHg38 = cmd.getOptionValue(ParserArgument.OPTION_SOURCE_FASTA_HG38);
		if (!Strings.isNullOrEmpty(strSourceFastaHg38)) {
			fastaHg38 = Paths.get(strSourceFastaHg38).toAbsolutePath();
			if (!Files.exists(fastaHg38)) {
				throw new IllegalArgumentException("Fasta (hg38) file does not exists: " + fastaHg38);
			}
		} else {
			fastaHg38 = null;
		}

	}
}
