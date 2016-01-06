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

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import org.openide.util.lookup.ServiceProvider;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Default implementation of LanguageVersionParser. It currently only supports
 * Java.
 */
@ServiceProvider(service = LanguageVersionParser.class)
public class DefaultLanguageVersionParser implements LanguageVersionParser {

    private static final Pattern javaVersionPattern = Pattern.compile("^(\\d)\\.(\\d)$");

    @Override
    public LanguageVersion parse(String languageVersionString) {
        Matcher javaVersionMatcher = javaVersionPattern.matcher(languageVersionString);

        if (!javaVersionMatcher.matches()) {
            throw new IllegalArgumentException("Unsupported target Java version");
        }

        String majorVersion = javaVersionMatcher.group(1);
        String minorVersion = javaVersionMatcher.group(2);
        String languageHandlerClassName = String.format("net.sourceforge.pmd.lang.java.Java%s%sHandler", majorVersion, minorVersion);

        try {
            Class<? extends LanguageVersionHandler> languageHandlerClass = (Class<? extends LanguageVersionHandler>) Class.forName(languageHandlerClassName);

            LanguageVersionHandler languageHandler = languageHandlerClass.newInstance();

            Language javaLanguage = LanguageRegistry.getLanguage("Java");

            if (javaLanguage == null) {
                throw new IllegalStateException("Cannot find Java in PMD's language registry");
            }

            return new LanguageVersion(javaLanguage, languageVersionString, languageHandler);
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unsupported target Java version");
        }
    }
}
