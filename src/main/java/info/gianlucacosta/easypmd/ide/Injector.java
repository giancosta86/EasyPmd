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
package info.gianlucacosta.easypmd.ide;

import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

import java.util.Collection;

/**
 * Provides a simplified and checked lookup system
 */
public class Injector {

    @ServiceProvider(service = TestService.class)
    public static class TestService {
    }

    private static final Lookup lookup = Lookup.getDefault();

    private static Lookup getLookup() {
        return lookup;
    }

    public static <T> T lookup(Class<T> serviceClass) {
        T result = getLookup().lookup(serviceClass);

        validateLookupResult(serviceClass, result);

        return result;
    }

    public static <T> Collection<? extends T> lookupAll(Class<T> serviceClass) {
        Collection<? extends T> result = getLookup().lookupAll(serviceClass);

        validateLookupResult(serviceClass, result);

        return result;
    }

    private static void validateLookupResult(Class<?> serviceClass, Object lookupResult) {
        if (lookupResult == null) {
            if (shouldThrowLookupExceptions(serviceClass)) {
                throw new IllegalArgumentException(String.format("Service '%s' not registered!", serviceClass.getSimpleName()));
            }
        }
    }

    //This code prevents exceptions in the visual form editors within NetBeans
    private static boolean shouldThrowLookupExceptions(Class<?> serviceClass) {
        if (serviceClass != TestService.class) {
            TestService testService = lookup.lookup(TestService.class);
            if (testService == null) {
                return false;
            }
        }

        return true;
    }

    private Injector() {
    }
}
