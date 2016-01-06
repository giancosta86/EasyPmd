/*
 * ==========================================================================%%#
 * EasyPmd
 * ===========================================================================%%
 * Copyright (C) 2009 - 2016 Gianluca Costa
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
package info.gianlucacosta.easypmd.pmdscanner;

import info.gianlucacosta.easypmd.ide.Injector;
import info.gianlucacosta.easypmd.ide.options.Options;
import info.gianlucacosta.easypmd.pmdscanner.messagescache.ScanMessagesCache;

import java.io.File;

/**
 * Scanning strategy looking for cached scan messages: if they are missing, a
 * default PMD scan is performed.
 */
class CacheBasedLinkedPmdScanningStrategy extends LinkedPmdScanningStrategy {

    private final ScanMessagesCache scanMessagesCache;

    public CacheBasedLinkedPmdScanningStrategy(Options options) {
        super(options);

        scanMessagesCache = Injector.lookup(ScanMessagesCache.class);
    }

    @Override
    public ScanMessageList scanFile(File file) {
        final ScanMessageList cachedScanMessages = scanMessagesCache.getScanMessagesFor(file);

        if (cachedScanMessages != null) {
            return cachedScanMessages;
        }

        final ScanMessageList scanMessages = super.scanFile(file);

        scanMessagesCache.putScanMessagesFor(file, scanMessages);

        return scanMessages;
    }
}
