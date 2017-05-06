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
import info.gianlucacosta.helios.io.Directory;
import info.gianlucacosta.helios.io.storagearea.DirectoryStorageArea;
import info.gianlucacosta.helios.io.storagearea.StorageArea;
import org.openide.util.lookup.ServiceProvider;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Default implementation of StorageAreaService
 */
@ServiceProvider(service = StorageAreaService.class)
public class DefaultStorageAreaService implements StorageAreaService {

    private static final Logger logger = Logger.getLogger(DefaultStorageAreaService.class.getName());

    private final SystemPropertiesService systemPropertiesService;
    private final PropertyPluginInfoService pluginInfoService;
    private final String storageDirName;
    private final StorageArea storageArea;

    public DefaultStorageAreaService() {
        systemPropertiesService = Injector.lookup(SystemPropertiesService.class);
        pluginInfoService = Injector.lookup(PropertyPluginInfoService.class);

        String majorVersion = pluginInfoService.getVersion().split("\\.")[0];
        storageDirName = String.format(".EasyPmd_%s", majorVersion);

        StorageArea tempStorageArea;

        Directory userHomeDir = systemPropertiesService.getUserHomeDir();
        if (userHomeDir != null) {
            Directory storageDir = new Directory(userHomeDir, storageDirName);

            tempStorageArea = new DirectoryStorageArea(storageDir);
        } else {
            tempStorageArea = null;
            logger.log(Level.SEVERE, "Cannot determine the home directory: the storage area will NOT be available");
        }

        storageArea = tempStorageArea;
    }

    @Override
    public StorageArea getStorageArea() {
        return storageArea;
    }
}
