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

package org.forome.astorage.makedatabase.make.conservation.iterator;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.forome.astorage.core.utils.RandomUtils;
import org.forome.astorage.makedatabase.make.conservation.MakeConservation;
import org.forome.core.struct.Chromosome;
import org.forome.core.struct.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.zip.GZIPInputStream;

public class SourceConservationIterator implements Iterator, AutoCloseable {

    private final static Logger log = LoggerFactory.getLogger(SourceConservationIterator.class);

    private final GZIPInputStream gzipInputStream;
    private final TarArchiveInputStream tarArchiveInputStream;

    private Item _nextItem;

    private TarArchiveEntry currentEntry;
    private Chromosome currentEntryChromosome;
    private int currentEntryCountPosition;
    private BufferedReader currentEntryBufferedReader;

    public SourceConservationIterator(Path sourceGerpPath) throws IOException {
        gzipInputStream = new GZIPInputStream(new BufferedInputStream(Files.newInputStream(sourceGerpPath)));
        tarArchiveInputStream = new TarArchiveInputStream(gzipInputStream);

        readNext();
    }

    @Override
    public boolean hasNext() {
        return (_nextItem != null);
    }

    @Override
    public Item next() {
        if (_nextItem == null) {
            throw new NoSuchElementException();
        }

        Item item = _nextItem;
        try {
            readNext();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return item;
    }

    private void readNext() throws IOException {
        if (currentEntry == null) {
            while ((currentEntry = (TarArchiveEntry) tarArchiveInputStream.getNextEntry()) != null) {
                Chromosome chromosome = getChromosome(currentEntry.getName());
                if (chromosome == null) {
                    log.debug("File entry: {} does ignored", currentEntry.getName());
                    continue;
                }
                currentEntryChromosome = chromosome;
                currentEntryCountPosition = 0;
                break;
            }
            if (currentEntry == null) {
                //Файлов в архиве больше не оказалось - все пробежали
                _nextItem = null;
                return;
            }

            currentEntryBufferedReader = new BufferedReader(new InputStreamReader(tarArchiveInputStream));
        }

        String line = currentEntryBufferedReader.readLine();
        if (line == null) {
            //файл закончился, уходим на второй круг
            currentEntry = null;
            readNext();
            return;
        }
        currentEntryCountPosition++;


        Position position = new Position(currentEntryChromosome, currentEntryCountPosition);
        String[] sLine = line.split("\\t");
        Float gerpN = null;
        if (!"0".equals(sLine[0])) {
            gerpN = Float.parseFloat(sLine[0]);
        }
        Float gerpRS = null;
        if (!"0".equals(sLine[1])) {
            gerpRS = Float.parseFloat(sLine[1]);
        }

        if (position.value % 1_000_000 == 0) {
            log.debug("Conservation: {}", position);
        }

        _nextItem = new Item(position, gerpN, gerpRS);
    }

    @Override
    public void close() throws IOException {
        if (currentEntryBufferedReader != null) {
            currentEntryBufferedReader.close();
        }
        tarArchiveInputStream.close();
        gzipInputStream.close();
    }

    private static Chromosome getChromosome(String entryName) {
        Chromosome chromosome = Chromosome.of(entryName.split("(\\.)|(_)")[0]);
        if (chromosome.isSupport()) {
            return chromosome;
        } else {
            return null;
        }
    }
}
