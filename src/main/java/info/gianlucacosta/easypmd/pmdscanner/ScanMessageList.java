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

import java.util.ArrayList;
import java.util.Set;

/**
 * A list of scan messages
 */
public class ScanMessageList extends ArrayList<ScanMessage> {

    public ScanMessageList filterOutGuardedSections(Set<Integer> guardedSectionLines) {
        ScanMessageList result = new ScanMessageList();

        for (ScanMessage scanMessage : this) {
            int violationLineNumber = scanMessage.getLineNumber();

            if (scanMessage.isShowableInGuardedSections() || !guardedSectionLines.contains(violationLineNumber)) {
                result.add(scanMessage);
            }
        }

        return result;
    }
}
