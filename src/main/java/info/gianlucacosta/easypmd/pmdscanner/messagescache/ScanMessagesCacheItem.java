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
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * An item in the scan messages cache
 */
public class ScanMessagesCacheItem implements Serializable {

    private final long lastModifiedMillis;
    private final List<ScanMessage> scanMessages;

    ScanMessagesCacheItem(Path path, List<ScanMessage> scanMessages) {
        if (scanMessages == null) {
            throw new IllegalArgumentException();
        }

        long actualLastModifiedMillis;

        try {
            actualLastModifiedMillis = Files.getLastModifiedTime(path).toMillis();
        } catch (IOException ex) {
            actualLastModifiedMillis = -1;
        }

        this.lastModifiedMillis = actualLastModifiedMillis;
        this.scanMessages = scanMessages;
    }

    public boolean isSynchronizedWith(Path path) {
        try {
            return lastModifiedMillis == Files.getLastModifiedTime(path).toMillis();
        } catch (IOException ex) {
            return false;
        }
    }

    public List<ScanMessage> getScanMessages() {
        return scanMessages;
    }
}
