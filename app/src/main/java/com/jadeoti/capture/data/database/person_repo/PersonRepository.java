package com.jadeoti.capture.data.database.person_repo;

import android.net.Uri;

import com.jadeoti.capture.data.model.Person;

import java.util.List;

/**
 * Created by Morph-Deji on 22-Mar-17.
 */

public interface PersonRepository {
    Uri commit(Person person);
    int push(Person person);

    void loadProfiles(GetProfilesCallback callback);
    void loadProfile(Person person, GetProfileCallback callback);

    interface GetProfilesCallback {

        void onProfilesLoaded(List<Person> persons);

        void onDataNotAvailable();
    }

    interface GetProfileCallback {

        void onProfileLoaded(Person person);

        void onDataNotAvailable();
    }}
