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
package info.gianlucacosta.easypmd.ide.options;

import info.gianlucacosta.helios.regex.CompositeRegex;

import java.io.Serializable;

/**
 * Options related to path filtering
 */
public class PathFilteringOptions implements Serializable {

    private final CompositeRegex includedPathCompositeRegex;
    private final CompositeRegex excludedPathCompositeRegex;

    public PathFilteringOptions(CompositeRegex includedPathCompositeRegex, CompositeRegex excludedPathCompositeRegex) {
        this.includedPathCompositeRegex = includedPathCompositeRegex;
        this.excludedPathCompositeRegex = excludedPathCompositeRegex;
    }

    public CompositeRegex getExcludedPathCompositeRegex() {
        return excludedPathCompositeRegex;
    }

    public CompositeRegex getIncludedPathCompositeRegex() {
        return includedPathCompositeRegex;
    }

    private boolean isScanOnlySomePaths() {
        return !includedPathCompositeRegex.getSubRegexes().isEmpty();
    }

    private boolean isExcludeSomePaths() {
        return !excludedPathCompositeRegex.getSubRegexes().isEmpty();
    }

    public boolean isPathValid(String path) {
        if (isScanOnlySomePaths()) {
            if (!includedPathCompositeRegex.matches(path)) {
                return false;
            }
        }

        if (isExcludeSomePaths()) {
            if (excludedPathCompositeRegex.matches(path)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PathFilteringOptions)) {
            return false;
        }

        PathFilteringOptions other = (PathFilteringOptions) obj;

        return includedPathCompositeRegex.equals(other.includedPathCompositeRegex)
                && excludedPathCompositeRegex.equals(other.excludedPathCompositeRegex);
    }

    @Override
    public int hashCode() {
        return includedPathCompositeRegex.hashCode() + excludedPathCompositeRegex.hashCode();
    }
}
