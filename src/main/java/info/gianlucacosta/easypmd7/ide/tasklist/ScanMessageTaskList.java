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
package info.gianlucacosta.easypmd7.ide.tasklist;

import info.gianlucacosta.easypmd7.pmdscanner.ScanMessage;
import info.gianlucacosta.easypmd7.pmdscanner.ScanMessageList;
import org.netbeans.spi.tasklist.Task;
import org.openide.filesystems.FileObject;

import java.util.ArrayList;

/**
 * A list of tasks, generated from a list of scan messages, related to a
 * FileObject
 */
public class ScanMessageTaskList extends ArrayList<Task> {

    public ScanMessageTaskList() {
        //Just do nothing
    }

    public ScanMessageTaskList(FileObject fileObject, ScanMessageList scanMessages) {
        for (ScanMessage scanMessage : scanMessages) {
            Task violationTask = Task.create(fileObject, scanMessage.getTaskType(), scanMessage.getTaskText(), scanMessage.getLineNumber());
            add(violationTask);
        }
    }
}
