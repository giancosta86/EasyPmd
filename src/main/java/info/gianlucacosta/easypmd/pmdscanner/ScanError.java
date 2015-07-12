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
package info.gianlucacosta.easypmd.pmdscanner;

import info.gianlucacosta.easypmd.ThrowableExtensions;

class ScanError implements ScanMessage {

    private static final int MAX_STACK_TRACE_STRING_LENGTH = 2000;
    private static final String ELLIPSIS_STRING = "\n<...>";
    private final String stackTraceString;
    private final String message;

    public ScanError(Exception exception) {
        String fullStackTraceString = ThrowableExtensions.getStackTraceString(exception);

        if (fullStackTraceString.length() < MAX_STACK_TRACE_STRING_LENGTH) {
            this.stackTraceString = fullStackTraceString;
        } else {
            this.stackTraceString = fullStackTraceString.substring(0, MAX_STACK_TRACE_STRING_LENGTH - ELLIPSIS_STRING.length() - 1) + ELLIPSIS_STRING;
        }

        this.message = ThrowableExtensions.getNonEmptyMessage(exception);
    }

    @Override
    public String getAnnotationText() {
        return String.format(stackTraceString);
    }

    @Override
    public int getLineNumber() {
        return 1;
    }

    @Override
    public String getTaskText() {
        return message;
    }

    @Override
    public String getTaskType() {
        return "info.gianlucacosta.easypmd.ide.tasklist.ScanError";
    }

    @Override
    public String getAnnotationType() {
        return "info.gianlucacosta.easypmd.ide.annotations.ScanError";
    }

    @Override
    public boolean isShowableInGuardedSections() {
        return true;
    }
}
