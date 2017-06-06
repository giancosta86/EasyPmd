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
package info.gianlucacosta.easypmd.pmdscanner.messages;

import info.gianlucacosta.easypmd.ide.annotations.BasicAnnotation;
import info.gianlucacosta.easypmd.ide.options.Options;
import info.gianlucacosta.easypmd.pmdscanner.ScanMessage;
import info.gianlucacosta.easypmd.util.Throwables;
import org.netbeans.spi.tasklist.Task;
import org.openide.filesystems.FileObject;
import org.openide.text.Annotation;

public class ScanError implements ScanMessage {

    private static final int ERROR_LINE_NUMBER = 1;
    private static final int MAX_STACK_TRACE_STRING_LENGTH = 2000;
    private static final String ELLIPSIS_STRING = "\n<...>";

    private final String exceptionMessage;
    private final String stackTraceString;

    public ScanError(Exception exception) {
        this.exceptionMessage = Throwables.getNonEmptyMessage(exception);

        String fullStackTraceString = Throwables.getStackTraceString(exception);

        if (fullStackTraceString.length() <= MAX_STACK_TRACE_STRING_LENGTH) {
            this.stackTraceString = fullStackTraceString;
        } else {
            this.stackTraceString = fullStackTraceString.substring(0, MAX_STACK_TRACE_STRING_LENGTH - ELLIPSIS_STRING.length() - 1) + ELLIPSIS_STRING;
        }
    }

    @Override
    public boolean isShowableInGuardedSections() {
        return true;
    }

    @Override
    public int getLineNumber() {
        return ERROR_LINE_NUMBER;
    }

    @Override
    public Task createTask(Options options, FileObject fileObject) {
        return Task.create(
                fileObject,
                "info.gianlucacosta.easypmd.ide.tasklist.ScanError",
                exceptionMessage,
                ERROR_LINE_NUMBER
        );
    }

    @Override
    public Annotation createAnnotation(Options options) {
        return new BasicAnnotation(
                "info.gianlucacosta.easypmd.ide.annotations.ScanError",
                stackTraceString
        );
    }
}
