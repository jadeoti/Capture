package com.jadeoti.capture;

/**
 * Created by Morph-Deji on 23-Mar-17.
 */

import android.content.Context;

import com.jadeoti.capture.data.database.person_repo.PersonRepository;
import com.jadeoti.capture.data.database.person_repo.PersonRepositoryImpl;
import com.jadeoti.capture.util.ImageFile;
import com.jadeoti.capture.util.ImageFileImpl;

/**
 * Enables injection of mock implementations for {@link com.jadeoti.capture.util.ImageFile} and
 * {@link com.jadeoti.capture.data.database.person_repo.PersonRepository} at compile time.
 * This is useful for testing, since it allows us to use
 * a fake instance of the class to isolate the dependencies and run a test hermetically.
 */
public class Injection {

    public static ImageFile provideImageFile(Context context) {
        //return new FakeImageFileImpl(context);
        return new ImageFileImpl(context);
    }

    public static PersonRepository providerPersonRepository(Context context) {
        //return new FakePersonRepository(context);
        return new PersonRepositoryImpl(context);
    }
}
