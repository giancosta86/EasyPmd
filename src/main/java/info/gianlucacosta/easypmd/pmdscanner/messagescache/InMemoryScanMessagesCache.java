/*
 * ==========================================================================%%#
 * EasyPmd
 * ===========================================================================%%
 * Copyright (C) 2009 - 2015 Gianluca Costa
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
package info.gianlucacosta.easypmd.pmdscanner.messagescache;

import java.io.File;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * In-memory scan messages cache
 */
class InMemoryScanMessagesCache extends AbstractScanMessagesCache {

    private final Map<String, ScanMessagesCacheItem> cacheItems = new WeakHashMap<>();

    @Override
    protected ScanMessagesCacheItem getItem(File scannedFile) {
        return cacheItems.get(scannedFile.getAbsolutePath());
    }

    @Override
    protected void putItem(File scannedFile, ScanMessagesCacheItem cacheItem) {
        cacheItems.put(scannedFile.getAbsolutePath(), cacheItem);
    }

    @Override
    protected boolean doClear() {
        cacheItems.clear();

        return true;
    }
}
