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
package info.gianlucacosta.easypmd.ide.options.profiles;

import info.gianlucacosta.easypmd.StorageAreaService;
import info.gianlucacosta.easypmd.ide.Injector;
import info.gianlucacosta.easypmd.io.StreamUtils;
import info.gianlucacosta.helios.io.storagearea.StorageArea;
import info.gianlucacosta.helios.io.storagearea.StorageAreaEntry;
import org.openide.util.lookup.ServiceProvider;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Default implementation of ProfileConfigurationRepository.
 */
@ServiceProvider(service = ProfileConfigurationRepository.class)
public class DefaultProfileConfigurationRepository implements ProfileConfigurationRepository {

    private static final String PROFILE_CONFIGURATION_ENTRY_NAME = "ProfileConfiguration";
    private static final Logger logger = Logger.getLogger(DefaultProfileConfigurationFactory.class.getName());

    private final StorageArea storageArea;
    private final ProfileConfigurationFactory profileConfigurationFactory;

    private ProfileConfiguration profileConfiguration;

    public DefaultProfileConfigurationRepository() {
        profileConfigurationFactory = Injector.lookup(ProfileConfigurationFactory.class);

        StorageAreaService storageAreaService = Injector.lookup(StorageAreaService.class);
        storageArea = storageAreaService.getStorageArea();

        if (storageArea != null) {
            try {
                StorageAreaEntry profileConfigurationEntry = storageArea.getEntry(PROFILE_CONFIGURATION_ENTRY_NAME);
                if (profileConfigurationEntry.exists()) {
                    try (InputStream profileConfigurationInputStream = profileConfigurationEntry.openInputStream()) {
                        profileConfiguration = (ProfileConfiguration) StreamUtils.readSingleObjectFromStream(profileConfigurationInputStream);
                    }
                }
            } catch (IOException | ClassNotFoundException ex) {
                logger.log(Level.SEVERE, "Exception while loading the options", ex);
            }

            if (profileConfiguration == null) {
                profileConfiguration = profileConfigurationFactory.createDefaultProfileConfiguration();
            }
        }
    }

    @Override
    public synchronized ProfileConfiguration getProfileConfiguration() {
        return profileConfiguration;
    }

    @Override
    public synchronized void saveProfileConfiguration(ProfileConfiguration profileConfiguration) {
        this.profileConfiguration = profileConfiguration;

        if (storageArea != null) {
            try {
                StorageAreaEntry profileConfigurationEntry = storageArea.getEntry(PROFILE_CONFIGURATION_ENTRY_NAME);

                try (OutputStream profileConfigurationOutputStream = profileConfigurationEntry.openOutputStream()) {
                    StreamUtils.writeSingleObjectToStream(profileConfigurationOutputStream, profileConfiguration);
                }
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Error while saving the options", ex);
            }
        }
    }
}
