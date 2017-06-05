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
package info.gianlucacosta.easypmd.pmdscanner.messages.cache;

import info.gianlucacosta.easypmd.pmdscanner.ScanMessage;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Dual-layered (in-memory and in-storage) cache
 */
public class DualLayerCache implements ScanMessagesCache {

    private final Map<String, CacheEntry> inMemoryEntries = new ConcurrentHashMap<>();

    private final CacheStorage storage;

    public DualLayerCache(CacheStorage storage) {
        this.storage = storage;
    }

    @Override
    public Optional<Set<ScanMessage>> getScanMessagesFor(String pathString, long pathLastModificationMillis) {
        CacheEntry inMemoryEntry = inMemoryEntries.get(pathString);

        if (inMemoryEntry != null) {
            if (inMemoryEntry.getLastModificationMillis() == pathLastModificationMillis) {
                return Optional.of(inMemoryEntry.getScanMessages());
            } else {
                return Optional.empty();
            }
        } else {
            Optional<CacheEntry> storageEntryOption = storage.getEntry(pathString);

            return storageEntryOption.map(storageEntry -> {
                if (storageEntry.getLastModificationMillis() == pathLastModificationMillis) {
                    inMemoryEntries.put(pathString, storageEntry);

                    return storageEntry.getScanMessages();
                } else {
                    return Collections.emptySet();
                }
            });
        }
    }

    @Override
    public void putScanMessagesFor(String pathString, long lastModificationMillis, Set<ScanMessage> scanMessages) {
        CacheEntry cacheEntry = new CacheEntry(lastModificationMillis, scanMessages);

        inMemoryEntries.put(pathString, cacheEntry);
        storage.putEntry(pathString, cacheEntry);
    }

    @Override
    public boolean clear() {
        inMemoryEntries.clear();

        return storage.clearEntries();
    }

    @Override
    public void close() throws Exception {
        inMemoryEntries.clear();

        storage.close();
    }
}
