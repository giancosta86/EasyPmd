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
package info.gianlucacosta.easypmd.pmdscanner.strategies;

import net.sourceforge.pmd.PMD;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;

/**
 * ClassLoader that firstly tries to load classes and resources using PMD's
 * class loader as its parent
 */
class PmdBasedClassLoader extends URLClassLoader {

    public static PmdBasedClassLoader create(Collection<URL> additionalUrls) {
        ClassLoader pmdClassLoader = PMD.class.getClassLoader();

        return new PmdBasedClassLoader(
                additionalUrls
                        .stream()
                        .toArray(URL[]::new),
                pmdClassLoader
        );
    }

    private PmdBasedClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }
}
