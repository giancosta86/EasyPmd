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
package info.gianlucacosta.easypmd.pmdscanner.messagescache;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * In-memory scan messages cache
 */
class InMemoryScanMessagesCache extends AbstractScanMessagesCache {

    private final Map<Path, ScanMessagesCacheItem> cacheItems = new HashMap<>();

    @Override
    protected ScanMessagesCacheItem getItem(Path scannedPath) {
        return cacheItems.get(scannedPath.toAbsolutePath());
    }

    @Override
    protected void putItem(Path scannedPath, ScanMessagesCacheItem cacheItem) {
        cacheItems.put(scannedPath.toAbsolutePath(), cacheItem);
    }

    @Override
    protected boolean doClear() {
        cacheItems.clear();

        return true;
    }
}
