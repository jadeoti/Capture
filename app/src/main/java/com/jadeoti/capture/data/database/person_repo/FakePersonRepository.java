package com.jadeoti.capture.data.database.person_repo;

import android.content.Context;
import android.net.Uri;

import com.jadeoti.capture.data.model.Person;

import timber.log.Timber;

/**
 * Created by Morph-Deji on 22-Mar-17.
 */

public class FakePersonRepository implements PersonRepository {
    Context context;
    public FakePersonRepository(Context context) {
        this.context = context;
    }

    @Override
    public Uri commit(Person person) {
        Timber.d("saving person %s, %s", person.getFirstName(), person.getSurname());
        return Uri.EMPTY;
    }

    @Override
    public int push(Person person) {
        Timber.d("pushing person %s, %s", person.getFirstName(), person.getSurname());
        return 0;
    }

    @Override
    public void loadProfiles(GetProfilesCallback callback) {

    }

    @Override
    public void loadProfile(Person person, GetProfileCallback callback) {

    }
}
