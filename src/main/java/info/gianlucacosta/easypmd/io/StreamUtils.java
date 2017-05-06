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
package info.gianlucacosta.easypmd.io;

import java.io.*;

/**
 * Stream-related utilities
 */
public class StreamUtils {

    private StreamUtils() {
    }

    public static Object readSingleObjectFromStream(InputStream sourceStream) throws IOException, ClassNotFoundException {
        try (ObjectInputStream inputStream = new ObjectInputStream(new BufferedInputStream(sourceStream))) {

            return inputStream.readObject();
        }
    }

    public static void writeSingleObjectToStream(OutputStream targetStream, Object obj) throws IOException {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new BufferedOutputStream(targetStream))) {
            outputStream.writeObject(obj);
        }
    }
}
