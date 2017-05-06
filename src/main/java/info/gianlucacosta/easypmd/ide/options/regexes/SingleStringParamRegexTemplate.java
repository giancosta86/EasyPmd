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
package info.gianlucacosta.easypmd.ide.options.regexes;

import info.gianlucacosta.easypmd.ide.DialogService;
import info.gianlucacosta.easypmd.ide.Injector;

/**
 * Regex template requiring just a single string parameter
 */
public abstract class SingleStringParamRegexTemplate extends RegexTemplate {

    private final DialogService dialogService;

    public SingleStringParamRegexTemplate() {
        dialogService = Injector.lookup(DialogService.class);
    }

    protected abstract String getPromptMessage();

    protected abstract String getDefaultValue();

    protected abstract String getRegex(String stringParam);

    @Override
    public String getRegex() {
        while (true) {
            String stringParam = dialogService.askForString(getPromptMessage(), getDefaultValue());

            if (stringParam == null) {
                return null;
            }

            stringParam = stringParam.trim();
            if (stringParam.isEmpty()) {
                continue;
            }

            try {
                return getRegex(stringParam);
            } catch (RuntimeException ex) {
                dialogService.showWarning(ex.getMessage());
            }
        }
    }
}
