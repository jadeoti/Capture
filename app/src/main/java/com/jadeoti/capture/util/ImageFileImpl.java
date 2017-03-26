package com.jadeoti.capture.util;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.VisibleForTesting;

import java.io.File;
import java.io.IOException;

/**
 * A thin wrapper around Android file APIs to make them more testable and allows the injection of a
 * fake implementation for hermetic UI tests.
 */
public class ImageFileImpl implements ImageFile {

    Context mContext;
    public ImageFileImpl(Context context) {
        this.mContext = context;
    }

    @VisibleForTesting
    File mImageFile;

    @Override
    public void create(String name, String extension) throws IOException {

        File storageDir =  mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        mImageFile = File.createTempFile(
                name,  /* prefix */
                extension,        /* suffix */
                storageDir      /* directory */
        );

    }

    @Override
    public boolean exists() {
        return null != mImageFile && mImageFile.exists();
    }

    @Override
    public void delete() {
        mImageFile = null;
    }

    @Override
    public String getPath() {
        //return Uri.fromFile(mImageFile).getPath();
        return mImageFile.getAbsolutePath();
    }

}
