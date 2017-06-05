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
package info.gianlucacosta.easypmd.ide.options.profiles;

import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import info.gianlucacosta.easypmd.ide.Injector;
import org.openide.util.lookup.ServiceProvider;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.nio.file.Path;
import info.gianlucacosta.easypmd.PathService;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.nio.file.Files;

/**
 * Default implementation of ProfileConfigurationRepository.
 */
@ServiceProvider(service = ProfileConfigurationRepository.class)
public class DefaultProfileConfigurationRepository implements ProfileConfigurationRepository {

    private static final Logger logger = Logger.getLogger(DefaultProfileConfigurationFactory.class.getName());

    private static final String PROFILES_FILE_NAME = "Profiles.xml";

    private final ProfileConfigurationFactory profileConfigurationFactory;
    private final EasyPmdXStream xmlStream = new EasyPmdXStream();

    private final Path profilesPath;

    private ProfileConfiguration profileConfiguration;

    public DefaultProfileConfigurationRepository() {
        profileConfigurationFactory = Injector.lookup(ProfileConfigurationFactory.class);

        PathService pathService = Injector.lookup(PathService.class);
        profilesPath = pathService.getRootPath().resolve(PROFILES_FILE_NAME);

        try (ObjectInputStream profileConfigurationInputStream = xmlStream.createObjectInputStream(
                new BufferedInputStream(
                        Files.newInputStream(profilesPath)
                )
        )) {
            profileConfiguration = (ProfileConfiguration) profileConfigurationInputStream.readObject();
        } catch (Exception ex) {
            logger.warning(
                    String.format("Exception while loading the profiles: %s", ex)
            );
            profileConfiguration = profileConfigurationFactory.createDefaultProfileConfiguration();
        }
    }

    @Override
    public synchronized ProfileConfiguration getProfileConfiguration() {
        return profileConfiguration;
    }

    @Override
    public synchronized void saveProfileConfiguration(ProfileConfiguration profileConfiguration) {
        this.profileConfiguration = profileConfiguration;

        try (ObjectOutputStream profileConfigurationOutputStream = xmlStream.createObjectOutputStream(
                new PrettyPrintWriter(
                        new OutputStreamWriter(
                                new BufferedOutputStream(
                                        Files.newOutputStream(profilesPath)
                                )
                        )
                )
        )) {
            profileConfigurationOutputStream.writeObject(profileConfiguration);
        } catch (Exception ex) {
            logger.severe(
                    String.format(
                            "Error while saving the options: %s",
                            ex
                    )
            );
        }
    }
}
