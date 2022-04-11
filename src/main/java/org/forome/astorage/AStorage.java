package org.forome.astorage;

import org.forome.database.exception.DatabaseException;
import org.forome.astorage.core.source.Source;
import org.forome.astorage.core.source.SourceDatabase;
import org.forome.astorage.pastorage.PAStorage;
import org.forome.astorage.pastorage.record.Record;
import org.forome.astorage.pastorage.record.RecordFasta;
import org.forome.astorage.pastorage.schema.Schema;
import org.forome.astorage.pastorage.schema.SchemaFasta;
import org.forome.core.struct.Assembly;
import org.forome.core.struct.Chromosome;
import org.forome.core.struct.Interval;
import org.forome.core.struct.Position;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class AStorage {

    public final Source sourceDatabase37;
    public final Source sourceDatabase38;

    private final PAStorage paStorage;

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

        this.paStorage = new PAStorage(builder.sourcePAStorage);
    }

    public Source getSource(Assembly assembly) {
        switch (assembly) {
            case GRCh37:
                return sourceDatabase37;
            case GRCh38:
                return sourceDatabase38;
            default:
                throw new RuntimeException("Not support assemple: " + assembly);
        }
    }

    public PAStorage getPaStorage() {
        return paStorage;
    }

    public static class Builder {

        private Map<Assembly, Path> sources;

        private Path sourcePAStorage;

        public Builder() {
            sources = new HashMap<>();
        }

        public Builder withSource(Assembly assembly, Path path) {
            sources.put(assembly, path);
            return this;
        }

        public Builder withSourcePAStorage(Path path) {
            this.sourcePAStorage = path;
            return this;
        }

        public AStorage build() throws DatabaseException {
            return new AStorage(this);
        }
    }
}
