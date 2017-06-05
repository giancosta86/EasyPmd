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
package info.gianlucacosta.easypmd.ide.options;

import info.gianlucacosta.easypmd.ide.Injector;
import org.openide.util.lookup.ServiceProvider;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import info.gianlucacosta.easypmd.ide.options.profiles.ProfileContext;
import info.gianlucacosta.easypmd.ide.options.profiles.ProfileContextRepository;

/**
 * Default implementation of OptionsService
 */
@ServiceProvider(service = OptionsService.class)
public class DefaultOptionsService implements OptionsService {

    private static final Logger logger = Logger.getLogger(DefaultOptionsService.class.getName());
    private final List<BiConsumer<Options, OptionsChanges>> optionsSetListeners = new LinkedList<>();
    private final Lock readLock;
    private final Lock writeLock;
    private final Collection<OptionsVerifier> optionsVerifiers = new LinkedList<>();
    private Options options;

    public DefaultOptionsService() {
        ReadWriteLock optionsLock = new ReentrantReadWriteLock();
        readLock = optionsLock.readLock();
        writeLock = optionsLock.writeLock();

        ProfileContextRepository profileContextRepository = Injector.lookup(ProfileContextRepository.class);
        ProfileContext profileContext = profileContextRepository.getProfileContext();
        options = profileContext.getActiveOptions();
    }

    @Override
    public void addOptionsSetListener(BiConsumer<Options, OptionsChanges> listener) {
        writeLock.lock();
        try {
            optionsSetListeners.add(listener);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void removeOptionsSetListener(BiConsumer<Options, OptionsChanges> listener) {
        writeLock.lock();
        try {
            optionsSetListeners.remove(listener);
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
            logger.info(() -> "Options set!");

            OptionsChanges optionsChanges = Options.computeChanges(oldOptions, options);

            optionsSetListeners
                    .stream()
                    .forEach(listener -> listener.accept(oldOptions, optionsChanges));
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void addOptionsVerifier(OptionsVerifier optionsVerifier) {
        writeLock.lock();
        try {
            optionsVerifiers.add(optionsVerifier);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void removeOptionsVerifier(OptionsVerifier optionsVerifier) {
        writeLock.lock();

        try {
            optionsVerifiers.remove(optionsVerifier);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void verifyOptions(Options options) throws InvalidOptionsException {
        readLock.lock();

        try {
            for (OptionsVerifier optionsVerifier : optionsVerifiers) {
                optionsVerifier.verifyOptions(options);
            }
        } finally {
            readLock.unlock();
        }
    }
}
