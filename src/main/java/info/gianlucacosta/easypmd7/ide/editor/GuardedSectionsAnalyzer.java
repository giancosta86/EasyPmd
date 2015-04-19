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
package info.gianlucacosta.easypmd7.ide.editor;

import org.netbeans.api.editor.guards.GuardedSection;
import org.netbeans.api.editor.guards.GuardedSectionManager;
import org.openide.cookies.EditorCookie;
import org.openide.loaders.DataObject;

import javax.swing.text.BadLocationException;
import javax.swing.text.Position;
import javax.swing.text.StyledDocument;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Analyzes the guarded sections in a document
 */
public class GuardedSectionsAnalyzer {

    private final Set<Integer> guardedLineNumbers = new HashSet<>();

    public GuardedSectionsAnalyzer(DataObject dataObject) {
        try {
            EditorCookie documentCookie = dataObject.getLookup().lookup(EditorCookie.class);
            StyledDocument document = documentCookie.openDocument();
            initialize(document);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public GuardedSectionsAnalyzer(StyledDocument document) {
        initialize(document);
    }

    private void initialize(StyledDocument document) {
        try {
            String fileContents = document.getText(0, document.getLength());
            if (fileContents.isEmpty()) {
                return;
            }

            GuardedSectionManager guardedSectionManager = GuardedSectionManager.getInstance(document);
            if (guardedSectionManager == null) {
                return;
            }

            Iterable<GuardedSection> guardedSections = guardedSectionManager.getGuardedSections();
            if (!guardedSections.iterator().hasNext()) {
                return;
            }

            int lastLineSeparatorIndex = -1;

            for (int lineNumber = 1; ; lineNumber++) {
                int lineStartIndex = lastLineSeparatorIndex + 1;
                Position lineStartPosition = document.createPosition(lineStartIndex);

                for (GuardedSection guardedSection : guardedSections) {
                    if (guardedSection.contains(lineStartPosition, true)) {
                        guardedLineNumbers.add(lineNumber);
                        break;
                    }
                }

                lastLineSeparatorIndex = fileContents.indexOf("\n", lastLineSeparatorIndex + 1);
                if (lastLineSeparatorIndex == -1) {
                    break;
                }
            }

        } catch (BadLocationException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @return the list of guarded line numbers. Line numbers are 1-based.
     */
    public Set<Integer> getGuardedLineNumbers() {
        return Collections.unmodifiableSet(guardedLineNumbers);
    }
}
