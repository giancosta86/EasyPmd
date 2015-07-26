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
package info.gianlucacosta.easypmd.ide.options;

import info.gianlucacosta.easypmd.ide.options.profiles.ProfileConfiguration;

/**
 * Configuration object returned by EasyPmdPanel
 */
public class ProfileConfigurationDTO {

    private ProfileConfiguration profileConfiguration;
    private boolean enforceChange;

    public ProfileConfiguration getProfileConfiguration() {
        return profileConfiguration;
    }

    public void setProfileConfiguration(ProfileConfiguration profileConfiguration) {
        this.profileConfiguration = profileConfiguration;
    }

    public boolean isEnforceChange() {
        return enforceChange;
    }

    public void setEnforceChange(boolean enforceChange) {
        this.enforceChange = enforceChange;
    }
}
