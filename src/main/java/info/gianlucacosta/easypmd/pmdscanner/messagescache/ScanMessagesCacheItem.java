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
import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * An item in the scan messages cache
 */
public class ScanMessagesCacheItem implements Serializable {

    private final long lastModified;
    private final List<ScanMessage> scanMessages;

    ScanMessagesCacheItem(File file, List<ScanMessage> scanMessages) {
        if (scanMessages == null) {
            throw new IllegalArgumentException();
        }

        this.lastModified = file.lastModified();
        this.scanMessages = scanMessages;
    }

    public boolean isSynchronizedWith(File file) {
        return lastModified == file.lastModified();
    }

    public List<ScanMessage> getScanMessages() {
        return scanMessages;
    }
}
