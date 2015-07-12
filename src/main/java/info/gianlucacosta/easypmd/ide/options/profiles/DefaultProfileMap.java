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

import info.gianlucacosta.helios.beans.events.TriggerEvent;
import info.gianlucacosta.helios.beans.events.TriggerListener;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation of ProfileMap.
 */
public class DefaultProfileMap implements ProfileMap, Serializable {

    private final TriggerEvent profileNamesChangedEvent = new TriggerEvent();
    private final Map<String, Profile> profiles = new HashMap<>();

    @Override
    public void setProfile(String profileName, Profile profile) throws ProfileException {
        if (profile == null) {
            throw new IllegalArgumentException();
        }

        boolean profileNamesChanged = !profiles.containsKey(profileName);

        profiles.put(profileName, profile);

        if (profileNamesChanged) {
            profileNamesChangedEvent.fire();
        }
    }

    @Override
    public Collection<String> getProfileNames() {
        return profiles.keySet();
    }

    @Override
    public Profile getProfile(String profileName) {
        return profiles.get(profileName);
    }

    @Override
    public void duplicateProfile(String sourceName, String targetName) throws ProfileException {
        if (profiles.containsKey(targetName)) {
            throw new ProfileException(String.format("A profile named '%s' already exists", targetName));
        }

        Profile sourceProfile = profiles.get(sourceName);
        if (sourceProfile == null) {
            throw new IllegalArgumentException();
        }

        Profile targetProfile = sourceProfile.clone();
        profiles.put(targetName, targetProfile);
        profileNamesChangedEvent.fire();
    }

    @Override
    public void renameProfile(String oldName, String newName) throws ProfileException {
        if (oldName.equals(newName)) {
            return;
        }

        if (profiles.containsKey(newName)) {
            throw new ProfileException(String.format("A profile named '%s' already exists", newName));
        }

        Profile profile = profiles.get(oldName);
        if (profile == null) {
            throw new IllegalArgumentException();
        }

        profiles.put(newName, profile);
        profiles.remove(oldName);
        profileNamesChangedEvent.fire();
    }

    @Override
    public void removeProfile(String name) throws ProfileException {
        if (!profiles.containsKey(name)) {
            return;
        }

        profiles.remove(name);
        profileNamesChangedEvent.fire();
    }

    @Override
    public void addProfileNamesChangedListener(TriggerListener listener) {
        profileNamesChangedEvent.addListener(listener);
    }

    @Override
    public void removeProfileNamesChangedListener(TriggerListener listener) {
        profileNamesChangedEvent.removeListener(listener);
    }

    @Override
    public boolean profileNameExists(String profileName) {
        return profiles.containsKey(profileName);
    }
}
