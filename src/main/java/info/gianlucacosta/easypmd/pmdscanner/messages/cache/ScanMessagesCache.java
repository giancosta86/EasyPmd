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

import java.util.Optional;
import java.util.Set;

/**
 * Cache of scan messages
 */
public interface ScanMessagesCache extends AutoCloseable {

    Optional<Set<ScanMessage>> getScanMessagesFor(String pathString, long pathLastModificationMillis);

    void putScanMessagesFor(String pathString, long lastModificationMillis, Set<ScanMessage> scanMessages);

    boolean clear();
}
