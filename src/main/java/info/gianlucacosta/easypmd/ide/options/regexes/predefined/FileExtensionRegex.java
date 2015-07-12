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
package info.gianlucacosta.easypmd.ide.options.regexes.predefined;

import info.gianlucacosta.easypmd.ide.options.regexes.RegexTemplate;
import info.gianlucacosta.easypmd.ide.options.regexes.SingleStringParamRegexTemplate;
import org.openide.util.lookup.ServiceProvider;

/**
 * Regex filtering by file extension
 */
@ServiceProvider(service = RegexTemplate.class)
public class FileExtensionRegex extends SingleStringParamRegexTemplate {

    @Override
    public String getDescription() {
        return "Filter by file extension";
    }

    @Override
    protected String getPromptMessage() {
        return "Please, digit the file extension for the filter:";
    }

    @Override
    protected String getDefaultValue() {
        return ".java";
    }

    @Override
    protected String getRegex(String stringParam) {
        if (!stringParam.startsWith(".")) {
            stringParam = "." + stringParam;
        }

        return String.format("^.*\\%s$", stringParam);
    }
}
