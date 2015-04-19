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
package info.gianlucacosta.easypmd7.pmdscanner.messagescache;

import info.gianlucacosta.easypmd7.pmdscanner.ScanMessageList;

import java.io.File;
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
    public ScanMessageList getScanMessagesFor(File file) {
        readLock.lock();

        try {
            ScanMessagesCacheItem cacheItem = getItem(file);

            if (cacheItem == null) {
                return null;
            }

            if (!cacheItem.isSynchronizedWith(file)) {
                return null;
            }

            return cacheItem.getScanMessages();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void putScanMessagesFor(File file, ScanMessageList scanMessages) {
        writeLock.lock();
        try {
            ScanMessagesCacheItem cacheItem = new ScanMessagesCacheItem(file, scanMessages);

            putItem(file, cacheItem);
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

    protected abstract ScanMessagesCacheItem getItem(File scannedFile);

    protected abstract void putItem(File scannedFile, ScanMessagesCacheItem cacheItem);

    protected abstract boolean doClear();
}
