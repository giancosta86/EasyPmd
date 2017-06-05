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

import org.openide.util.lookup.ServiceProvider;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Default implementation of SystemPropertiesService
 */
@ServiceProvider(service = SystemPropertiesService.class)
public class DefaultSystemPropertiesService implements SystemPropertiesService {

    public final String javaVersion;
    public final Path userHomeDir;

    public DefaultSystemPropertiesService() {
        javaVersion = findJavaVersion();

        userHomeDir = findUserHomeDir();
    }

    @Override
    public String getJavaVersion() {
        return javaVersion;
    }

    @Override
    public Path getUserHomeDirectory() {
        return userHomeDir;
    }

    private static String findJavaVersion() {
        Pattern versionPattern = Pattern.compile("(\\d\\.\\d).*");

        String javaVersionProperty = System.getProperty("java.version");
        Matcher versionMatcher = versionPattern.matcher(javaVersionProperty);

        if (versionMatcher.matches()) {
            return versionMatcher.group(1);
        } else {
            throw new RuntimeException("Cannot detected Java version");
        }
    }

    private static Path findUserHomeDir() {
        String userHomeString = System.getProperty("user.home");

        if (userHomeString == null) {
            throw new RuntimeException("Cannot detect the user's home directory");
        }

        return Paths.get(userHomeString);
    }
}
