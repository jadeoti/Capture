package com.jadeoti.capture.util;

import android.content.Context;

import java.io.IOException;

/**
 * Fake implementation of {@link ImageFile} to inject a fake image in a hermetic test.
 */
public class FakeImageFileImpl extends ImageFileImpl {

    public FakeImageFileImpl(Context context) {
        super(context);
    }

    @Override
    public void create(String name, String extension) throws IOException {
        // Do nothing
    }

    @Override
    public String getPath() {
        return "file:///android_asset/atsl-logo.png";
    }

    @Override
    public boolean exists() {
        return true;
    }
}
