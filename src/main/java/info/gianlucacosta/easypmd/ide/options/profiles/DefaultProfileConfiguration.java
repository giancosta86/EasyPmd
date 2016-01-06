/*
 * ==========================================================================%%#
 * EasyPmd
 * ===========================================================================%%
 * Copyright (C) 2009 - 2016 Gianluca Costa
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

import info.gianlucacosta.easypmd.ide.options.Options;

import java.io.Serializable;

/**
 * Default implementation of ProfileConfiguration.
 */
public class DefaultProfileConfiguration implements ProfileConfiguration, Serializable {

    private final ProfileMap profileMap;
    private final String activeProfileName;

    public DefaultProfileConfiguration(ProfileMap profileMap, String activeProfileName) {
        this.profileMap = profileMap;
        this.activeProfileName = activeProfileName;
    }

    @Override
    public ProfileMap getProfiles() {
        return profileMap;
    }

    @Override
    public String getActiveProfileName() {
        return activeProfileName;
    }

    @Override
    public Options getActiveOptions() {
        return profileMap.getProfile(activeProfileName).getOptions();
    }
}
