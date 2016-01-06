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
package info.gianlucacosta.easypmd.ide.options;

import info.gianlucacosta.helios.beans.events.TriggerListener;

/**
 * Provides methods to get/set options, to verify them and to monitor their
 * changes
 */
public interface OptionsService {

    void addOptionsChangedListener(TriggerListener listener);

    void removeOptionsChangedListener(TriggerListener listener);

    Options getOptions();

    void setOptions(Options options);

    void setOptionsEnforcingChange(Options options);

    void verifyOptions(Options options) throws InvalidOptionsException;

    void addOptionsVerifier(OptionsVerifier optionsVerifier);

    void removeOptionsVerifier(OptionsVerifier optionsVerifier);
}
