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

package org.forome.astorage.pastorage.schema;

import com.infomaximum.database.exception.DatabaseException;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.forome.astorage.core.rocksdb.RocksDBDatabase;
import org.forome.astorage.pastorage.record.Record;
import org.forome.core.struct.Assembly;
import org.forome.core.struct.Interval;
import org.forome.core.struct.Position;
import org.rocksdb.ColumnFamilyHandle;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public abstract class Schema {

    public final String name;

	public final RocksDBDatabase rocksDBDatabase;

    protected Schema(String name, Path schemaFile, Path schemaDatabase) {
        this.name = name;

        JSONObject jSchemaFile = parseSchemaFile(schemaFile);
        if (!jSchemaFile.getAsString("name").equals(name)) {
            throw new RuntimeException("Discrepancy schema: " + name + ", file: " + schemaFile.toString());
        }

        try {
            this.rocksDBDatabase = new RocksDBDatabase(schemaDatabase);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public abstract Record getRecord(Assembly assembly, Position position);

    protected ColumnFamilyHandle getColumnFamily(Assembly assembly) {
        if (assembly == Assembly.GRCh37) {
            return getColumnFamily("hg19");
        } else if (assembly == Assembly.GRCh38) {
            return getColumnFamily("hg38");
        } else {
            throw new RuntimeException();
        }
    }

	public ColumnFamilyHandle getColumnFamily(String name) {
		return rocksDBDatabase.getColumnFamily(name);
	}

    public static Schema build(String name, Path schemaFile, Path schemaDatabase) {
        if (SchemaFasta.SCHEMA_FASTA_NAME.equals(name)) {
            return new SchemaFasta(schemaFile, schemaDatabase);
        } else {
            return new SchemaCommon(name, schemaFile, schemaDatabase);
        }
    }

    protected static JSONObject parseSchemaFile(Path path) {
        try (InputStream is = Files.newInputStream(path, StandardOpenOption.READ)) {
            return (JSONObject) new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
