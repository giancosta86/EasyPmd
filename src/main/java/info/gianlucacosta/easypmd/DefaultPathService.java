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

import info.gianlucacosta.easypmd.ide.Injector;
import java.nio.file.Path;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = PathService.class)
public class DefaultPathService implements PathService {

    private final SystemPropertiesService systemPropertiesService;
    private final PropertyPluginInfoService pluginInfoService;
    private final Path rootPath;
    private final Path cachePath;

    public DefaultPathService() {
        systemPropertiesService = Injector.lookup(SystemPropertiesService.class);
        pluginInfoService = Injector.lookup(PropertyPluginInfoService.class);

        String majorVersion = pluginInfoService.getVersion().split("\\.")[0];
        String rootDirName = String.format(".EasyPmd_%s", majorVersion);

        rootPath = systemPropertiesService.getUserHomeDirectory().resolve(rootDirName);
        cachePath = rootPath.resolve("cache");
    }

    @Override
    public Path getRootPath() {
        return rootPath;
    }

    @Override
    public Path getCachePath() {
        return cachePath;
    }
}
