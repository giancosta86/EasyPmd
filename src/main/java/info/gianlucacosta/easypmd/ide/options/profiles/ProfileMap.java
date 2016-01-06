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

import info.gianlucacosta.helios.beans.events.TriggerListener;

import java.util.Collection;

/**
 * A dedicated map-like object for enumerating profiles.
 */
public interface ProfileMap {

    Collection<String> getProfileNames();

    void setProfile(String profileName, Profile profile) throws ProfileException;

    boolean profileNameExists(String profileName);

    Profile getProfile(String profileName);

    void duplicateProfile(String sourceName, String targetName) throws ProfileException;

    void renameProfile(String oldName, String newName) throws ProfileException;

    void removeProfile(String name) throws ProfileException;

    void addProfileNamesChangedListener(TriggerListener listener);

    void removeProfileNamesChangedListener(TriggerListener listener);
}
