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

package org.forome.astorage.makedatabase.make.fasta.iterator;

import org.forome.core.struct.Chromosome;
import org.forome.core.struct.Position;
import org.forome.core.struct.nucleotide.Nucleotide;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.zip.GZIPInputStream;

public class SourceFastaIterator implements Iterator, AutoCloseable {

    private final static Logger log = LoggerFactory.getLogger(SourceFastaIterator.class);

    private final BufferedReader bufferedReader;

    private Item _nextItem;

    private Chromosome currentChromosome;
    private int currentCountPosition;

    private String currentLine;
    private int currentLineIndex;

    public SourceFastaIterator(Path sourcePathFasta) throws IOException {
        this.bufferedReader = new BufferedReader(new InputStreamReader(new GZIPInputStream(Files.newInputStream(sourcePathFasta))));
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
        if (currentLine == null) {
            readNextLine();
        } else if (currentLineIndex == currentLine.length()) {
            currentCountPosition += currentLine.length();
            readNextLine();
        }

        if (currentLine != null) {
            _nextItem = new Item(
                    new Position(currentChromosome, currentCountPosition + currentLineIndex + 1),
                    Nucleotide.of(currentLine.charAt(currentLineIndex))
            );

            currentLineIndex++;
        } else {
            //Элементы закончились
            _nextItem = null;
        }
    }

    private void readNextLine() throws IOException {
        String readline;
        while ((readline = bufferedReader.readLine()) != null) {
            if (readline.charAt(0) == '>') {
                Chromosome iChromosome = Chromosome.of(readline.substring(1));
                if (iChromosome.isSupport()) {
                    currentChromosome = iChromosome;
                    log.debug("Chromosome: " + iChromosome.toString());
                } else {
                    currentChromosome = null;
                    log.debug("Chromosome ignore: " + iChromosome.toString());
                }
                currentCountPosition = 0;
                continue;
            }
            if (currentChromosome == null) {
                continue;
            }
            break;
        }

        currentLine = readline;
        currentLineIndex = 0;
    }

    @Override
    public void close() throws IOException {
        bufferedReader.close();
    }
}
