/*
 * ==========================================================================%%#
 * EasyPmd
 * ===========================================================================%%
 * Copyright (C) 2009 - 2017 Gianluca Costa
 * ===========================================================================%%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * ==========================================================================%##
 */
package info.gianlucacosta.easypmd.pmdscanner.strategies;

import info.gianlucacosta.easypmd.ide.PathService;
import info.gianlucacosta.easypmd.ide.Injector;
import info.gianlucacosta.easypmd.ide.options.Options;
import info.gianlucacosta.easypmd.pmdscanner.ScanMessage;
import info.gianlucacosta.easypmd.pmdscanner.messages.ScanError;
import info.gianlucacosta.easypmd.pmdscanner.messages.cache.ScanMessagesCache;
import java.io.IOException;
import java.nio.file.Files;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Scanning strategy looking for cached scan messages: if they are missing, a
 * default PMD scan is performed.
 */
public class CacheBasedLinkedPmdScanningStrategy extends LinkedPmdScanningStrategy {

    private static final Logger logger = Logger.getLogger(CacheBasedLinkedPmdScanningStrategy.class.getName());

    private final ScanMessagesCache scanMessagesCache = Injector.lookup(ScanMessagesCache.class);

    public CacheBasedLinkedPmdScanningStrategy(Options options) {
        super(options);
    }

    @Override
    public Set<ScanMessage> scan(Path path) {
        try {
            String pathString = path.toString();
            long lastModificationMillis = Files.getLastModifiedTime(path).toMillis();

            final Optional<Set<ScanMessage>> cachedScanMessagesOption = scanMessagesCache.getScanMessagesFor(
                    pathString,
                    lastModificationMillis
            );

            return cachedScanMessagesOption.orElseGet(() -> {
                logger.info(() -> String.format("No valid cache entry found for path: %s", pathString));

                Set<ScanMessage> scanMessages = super.scan(path);

                scanMessagesCache.putScanMessagesFor(pathString, lastModificationMillis, scanMessages);

                return scanMessages;
            });
        } catch (IOException ex) {
            return Collections.singleton(
                    new ScanError(ex)
            );
        }
    }
}
