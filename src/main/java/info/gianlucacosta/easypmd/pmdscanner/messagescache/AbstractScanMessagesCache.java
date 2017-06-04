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

import info.gianlucacosta.easypmd.pmdscanner.ScanMessage;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Abstract implementation of the scan messages cache
 */
abstract class AbstractScanMessagesCache implements ScanMessagesCache {

    private final Lock readLock;
    private final Lock writeLock;

    public AbstractScanMessagesCache() {
        ReadWriteLock cacheLock = new ReentrantReadWriteLock();

        readLock = cacheLock.readLock();
        writeLock = cacheLock.writeLock();
    }

    @Override
    public List<ScanMessage> getScanMessagesFor(Path path) {
        readLock.lock();

        try {
            ScanMessagesCacheItem cacheItem = getItem(path);

            if (cacheItem == null) {
                return null;
            }

            if (!cacheItem.isSynchronizedWith(path)) {
                return null;
            }

            return cacheItem.getScanMessages();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void putScanMessagesFor(Path path, List<ScanMessage> scanMessages) {
        writeLock.lock();
        try {
            ScanMessagesCacheItem cacheItem = new ScanMessagesCacheItem(path, scanMessages);

            putItem(path, cacheItem);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean clear() {
        writeLock.lock();

        try {
            return doClear();
        } finally {
            writeLock.unlock();
        }
    }

    protected abstract ScanMessagesCacheItem getItem(Path scannedPath);

    protected abstract void putItem(Path scannedPath, ScanMessagesCacheItem cacheItem);

    protected abstract boolean doClear();
}
