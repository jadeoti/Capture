package com.jadeoti.capture.util;

import java.io.IOException;

/**
 * A wrapper for handling image files.
 */
public interface ImageFile {
    void create(String name, String extension) throws IOException;

    boolean exists();

    void delete();

    String getPath();
}
