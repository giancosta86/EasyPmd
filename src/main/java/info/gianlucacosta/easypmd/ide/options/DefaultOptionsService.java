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

import info.gianlucacosta.easypmd.ide.Injector;
import info.gianlucacosta.easypmd.ide.options.profiles.ProfileConfiguration;
import info.gianlucacosta.easypmd.ide.options.profiles.ProfileConfigurationRepository;
import info.gianlucacosta.helios.beans.events.TriggerEvent;
import info.gianlucacosta.helios.beans.events.TriggerListener;
import org.openide.util.lookup.ServiceProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Default implementation of OptionsService
 */
@ServiceProvider(service = OptionsService.class)
public class DefaultOptionsService implements OptionsService {

    private static final Logger logger = Logger.getLogger(DefaultOptionsService.class.getName());
    private final TriggerEvent optionsChangedEvent = new TriggerEvent();
    private final Lock readLock;
    private final Lock writeLock;
    private final Collection<OptionsVerifier> optionsVerifiers = new ArrayList<>();
    private Options options;

    public DefaultOptionsService() {
        ReadWriteLock optionsLock = new ReentrantReadWriteLock();
        readLock = optionsLock.readLock();
        writeLock = optionsLock.writeLock();

        ProfileConfigurationRepository profileConfigurationRepository = Injector.lookup(ProfileConfigurationRepository.class);
        ProfileConfiguration profileConfiguration = profileConfigurationRepository.getProfileConfiguration();
        options = profileConfiguration.getActiveOptions();
    }

    @Override
    public void addOptionsChangedListener(TriggerListener listener) {
        writeLock.lock();
        try {
            optionsChangedEvent.addListener(listener);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void removeOptionsChangedListener(TriggerListener listener) {
        writeLock.lock();
        try {
            optionsChangedEvent.removeListener(listener);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public Options getOptions() {
        readLock.lock();

        try {
            return options;
        } finally {
            readLock.unlock();
        }

    }

    @Override
    public void setOptions(Options options) {
        writeLock.lock();
        try {
            Options oldOptions = this.options;

            this.options = options;

            if (!options.equals(oldOptions)) {
                logger.log(Level.INFO, "Options changed!");

                optionsChangedEvent.fire();
            }
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void setOptionsEnforcingChange(Options options) {
        writeLock.lock();
        try {
            this.options = options;

            logger.log(Level.INFO, "Options with enforced change!");

            optionsChangedEvent.fire();
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void addOptionsVerifier(OptionsVerifier optionsVerifier) {
        optionsVerifiers.add(optionsVerifier);
    }

    @Override
    public void removeOptionsVerifier(OptionsVerifier optionsVerifier) {
        optionsVerifiers.remove(optionsVerifier);
    }

    @Override
    public void verifyOptions(Options options) throws InvalidOptionsException {
        writeLock.lock();

        try {
            for (OptionsVerifier optionsVerifier : optionsVerifiers) {
                optionsVerifier.verifyOptions(options);
            }
        } finally {
            writeLock.unlock();
        }
    }
}
