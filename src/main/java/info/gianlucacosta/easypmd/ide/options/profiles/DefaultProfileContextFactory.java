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

import info.gianlucacosta.easypmd.ide.Injector;
import info.gianlucacosta.easypmd.ide.options.Options;
import info.gianlucacosta.easypmd.ide.options.OptionsFactory;
import org.openide.util.lookup.ServiceProvider;

/**
 * Default implementation of ProfileContextFactory.
 */
@ServiceProvider(service = ProfileContextFactory.class)
public class DefaultProfileContextFactory implements ProfileContextFactory {

    private static final String defaultProfileName = "Default profile";
    private final OptionsFactory optionsFactory;

    public DefaultProfileContextFactory() {
        this.optionsFactory = Injector.lookup(OptionsFactory.class);
    }

    @Override
    public ProfileContext createDefaultProfileContext() {
        Options defaultOptions = optionsFactory.createDefaultOptions();
        Profile defaultProfile = new DefaultProfile(defaultOptions);

        ProfileMap defaultProfiles = new DefaultProfileMap();

        try {
            defaultProfiles.setProfile(defaultProfileName, defaultProfile);
        } catch (ProfileException ex) {
            throw new RuntimeException(ex);
        }

        return new DefaultProfileContext(defaultProfiles, defaultProfileName);
    }

}
