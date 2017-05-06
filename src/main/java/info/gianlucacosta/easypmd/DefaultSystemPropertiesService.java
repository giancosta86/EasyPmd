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
package info.gianlucacosta.easypmd;

import info.gianlucacosta.helios.io.Directory;
import org.openide.util.lookup.ServiceProvider;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Default implementation of SystemPropertiesService
 */
@ServiceProvider(service = SystemPropertiesService.class)
public class DefaultSystemPropertiesService implements SystemPropertiesService {

    public String javaVersion;
    public Directory userHomeDir;

    private static String findJavaVersion() {
        Pattern versionPattern = Pattern.compile("(\\d\\.\\d).*");

        String javaVersionProperty = System.getProperty("java.version");
        Matcher versionMatcher = versionPattern.matcher(javaVersionProperty);

        if (versionMatcher.matches()) {
            return versionMatcher.group(1);
        } else {
            return null;
        }
    }

    private static Directory findUserHomeDir() {
        String userHomeString = System.getProperty("user.home");

        if (userHomeString == null) {
            return null;
        }

        File result = new File(userHomeString);

        if (result.exists() && result.isDirectory()) {
            return new Directory(result);
        }

        return null;
    }

    @Override
    public String getJavaVersion() {
        if (javaVersion == null) {
            javaVersion = findJavaVersion();
        }

        return javaVersion;
    }

    @Override
    public Directory getUserHomeDir() {
        if (userHomeDir == null) {
            userHomeDir = findUserHomeDir();
        }

        return userHomeDir;
    }
}
