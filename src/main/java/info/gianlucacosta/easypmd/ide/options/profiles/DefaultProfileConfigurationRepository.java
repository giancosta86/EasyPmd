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

import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import info.gianlucacosta.easypmd.StorageAreaService;
import info.gianlucacosta.easypmd.ide.Injector;
import info.gianlucacosta.helios.io.storagearea.StorageArea;
import info.gianlucacosta.helios.io.storagearea.StorageAreaEntry;
import java.io.IOException;
import org.openide.util.lookup.ServiceProvider;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Default implementation of ProfileConfigurationRepository.
 */
@ServiceProvider(service = ProfileConfigurationRepository.class)
public class DefaultProfileConfigurationRepository implements ProfileConfigurationRepository {

    private static final String PROFILE_CONFIGURATION_ENTRY_NAME = "ProfileConfiguration.xml";
    private static final Logger logger = Logger.getLogger(DefaultProfileConfigurationFactory.class.getName());

    private final ProfileConfigurationFactory profileConfigurationFactory;
    private final EasyPmdXStream xmlStream = new EasyPmdXStream();

    private final StorageArea storageArea;

    private ProfileConfiguration profileConfiguration;

    public DefaultProfileConfigurationRepository() {
        profileConfigurationFactory = Injector.lookup(ProfileConfigurationFactory.class);

        StorageAreaService storageAreaService = Injector.lookup(StorageAreaService.class);
        storageArea = storageAreaService.getStorageArea();

        if (storageArea != null) {
            try {
                StorageAreaEntry profileConfigurationEntry = storageArea.getEntry(PROFILE_CONFIGURATION_ENTRY_NAME);
                if (profileConfigurationEntry.exists()) {
                    try (ObjectInputStream profileConfigurationInputStream = xmlStream.createObjectInputStream(profileConfigurationEntry.openInputStream())) {
                        profileConfiguration = (ProfileConfiguration) profileConfigurationInputStream.readObject();
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

                try (ObjectOutputStream profileConfigurationOutputStream = xmlStream.createObjectOutputStream(
                        new PrettyPrintWriter(new OutputStreamWriter(profileConfigurationEntry.openOutputStream()))
                )) {
                    profileConfigurationOutputStream.writeObject(profileConfiguration);
                }
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Error while saving the options", ex);
            }
        }
    }
}
