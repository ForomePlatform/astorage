package org.forome.astorage;

import com.infomaximum.database.exception.DatabaseException;
import org.forome.astorage.core.source.Source;
import org.forome.astorage.core.source.SourceDatabase;
import org.forome.core.struct.Assembly;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class AStorage {

	public final Source sourceDatabase37;
	public final Source sourceDatabase38;

	private AStorage(Builder builder) throws DatabaseException {
		Map<Assembly, Path> sources = builder.sources;
		if (sources.containsKey(Assembly.GRCh37)) {
			sourceDatabase37 = new SourceDatabase(Assembly.GRCh37, sources.get(Assembly.GRCh37));
		} else {
			sourceDatabase37 = null;
		}
		if (sources.containsKey(Assembly.GRCh38)) {
			sourceDatabase38 = new SourceDatabase(Assembly.GRCh38, sources.get(Assembly.GRCh38));
		} else {
			sourceDatabase38 = null;
		}
	}


	public static class Builder {

		private Map<Assembly, Path> sources;

		public Builder() {
			sources = new HashMap<>();
		}

		public Builder withSource(Assembly assembly, Path path) {
			sources.put(assembly, path);
			return this;
		}

		public AStorage build() throws DatabaseException {
			return new AStorage(this);
		}
	}
}
