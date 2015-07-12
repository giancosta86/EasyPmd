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
package info.gianlucacosta.easypmd.ide.editor;

import info.gianlucacosta.easypmd.pmdscanner.ScanMessage;
import org.openide.text.Annotation;
import org.openide.text.Line;

/**
 * An editor annotation related to a scan message
 */
public class ScanMessageAnnotation extends Annotation {

    private final ScanMessage message;

    ScanMessageAnnotation(ScanMessage message) {
        this.message = message;
    }

    public void attach(Line.Set lineSet) {
        Line line = lineSet.getOriginal(getLineNumber() - 1);
        attach(line);
    }

    @Override
    public String getAnnotationType() {
        return message.getAnnotationType();
    }

    @Override
    public String getShortDescription() {
        return message.getAnnotationText();
    }

    public int getLineNumber() {
        return message.getLineNumber();
    }
}
