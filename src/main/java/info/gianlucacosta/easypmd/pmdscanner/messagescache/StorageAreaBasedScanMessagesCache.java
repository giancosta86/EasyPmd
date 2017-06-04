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

import info.gianlucacosta.easypmd.io.StreamUtils;
import info.gianlucacosta.helios.io.storagearea.StorageArea;
import info.gianlucacosta.helios.io.storagearea.StorageAreaEntry;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Scan messages cache writing its items to a StorageArea
 */
class StorageAreaBasedScanMessagesCache extends AbstractScanMessagesCache {

    private static final Logger logger = Logger.getLogger(StorageAreaBasedScanMessagesCache.class.getName());
    private static final String ROOT_CACHE_ENTRY = "cache";
    private final StorageArea storageArea;

    public StorageAreaBasedScanMessagesCache(StorageArea storageArea) {
        this.storageArea = storageArea;
    }

    @Override
    protected ScanMessagesCacheItem getItem(File scannedFile) {
        if (storageArea == null) {
            logger.log(Level.FINE, "No storage area detected - reading cannot be performed");
            return null;
        }

        Map<String, ScanMessagesCacheItem> clusterMap = getClusterMap(scannedFile);

        if (clusterMap == null) {
            return null;
        }

        return clusterMap.get(scannedFile.getAbsolutePath());
    }

    private Map<String, ScanMessagesCacheItem> getClusterMap(File scannedFile) {
        try {
            StorageAreaEntry clusterMapEntry = getClusterMapEntry(scannedFile);

            if (!clusterMapEntry.exists()) {
                return null;
            }

            try (InputStream entryStream = clusterMapEntry.openInputStream()) {
                return (Map<String, ScanMessagesCacheItem>) StreamUtils.readSingleObjectFromStream(new BufferedInputStream(entryStream));
            }

        } catch (IOException | ClassNotFoundException ex) {
            logger.log(
                    Level.WARNING,
                    String.format(
                            "Error while reading cache cluster map for file: '%s'",
                            scannedFile.getAbsolutePath()),
                    ex);

        }

        return null;
    }

    private StorageAreaEntry getClusterMapEntry(File scannedFile) throws IOException {
        String absolutePath = scannedFile.getAbsolutePath();

        Integer pathHashCode = absolutePath.hashCode();
        return storageArea.getEntry(ROOT_CACHE_ENTRY, pathHashCode.toString());
    }

    @Override
    protected void putItem(File scannedFile, ScanMessagesCacheItem cacheItem) {
        if (storageArea == null) {
            logger.log(Level.FINE, "No storage area detected - bypassing writing phase...");
            return;
        }

        Map<String, ScanMessagesCacheItem> clusterMap = getClusterMap(scannedFile);

        if (clusterMap == null) {
            clusterMap = new HashMap<>();
        }

        clusterMap.put(scannedFile.getAbsolutePath(), cacheItem);

        try {
            StorageAreaEntry clusterMapEntry = getClusterMapEntry(scannedFile);

            try (OutputStream entryOutputStream = clusterMapEntry.openOutputStream()) {
                StreamUtils.writeSingleObjectToStream(
                        new BufferedOutputStream(entryOutputStream),
                        clusterMap
                );
            }
        } catch (IOException ex) {
            logger.log(
                    Level.SEVERE,
                    String.format("Error while writing the cache cluster map for file: '%s'", scannedFile.getAbsolutePath()),
                    ex);
        }
    }

    @Override
    protected boolean doClear() {
        if (storageArea == null) {
            logger.log(Level.FINE, "No storage area detected - the cache is already empty");
            return true;
        }

        try {
            StorageAreaEntry rootCacheEntry = storageArea.getEntry(ROOT_CACHE_ENTRY);
            rootCacheEntry.remove();
            return true;
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Could not clear the cache", ex);
            return false;
        }
    }
}
