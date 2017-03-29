package com.jadeoti.capture.data.database.person_repo;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import com.jadeoti.capture.data.model.Person;
import com.jadeoti.capture.provider.person.PersonContentValues;
import com.jadeoti.capture.provider.person.PersonSelection;
import com.jadeoti.capture.services.DataUploadService;

import java.util.ArrayList;
import java.util.Locale;

import timber.log.Timber;

/**
 * Created by Morph-Deji on 22-Mar-17.
 */

public class PersonRepositoryImpl implements PersonRepository {

    private Context mContext;
    private ContentResolver mContentResolver;
    public PersonRepositoryImpl(Context context) {
        this.mContext = context;
        mContentResolver = mContext.getContentResolver();
    }

    @Override
    public Uri commit(Person person) {
        Timber.d("saving person %s, %s", person.getFirstName(), person.getSurname());
        PersonContentValues values = new PersonContentValues();
        values.putDateCreated(System.currentTimeMillis());
        values.putDateModified(System.currentTimeMillis());
        values.putDateSynced(-1L);
        values.putSyncFailed(true);
        values.putFirstName(person.getFirstName());
        values.putSurname(person.getSurname());
        values.putOtherNames(person.getOtherNames());
        values.putEmailAddress(person.getEmailAddress());
        values.putImageUri(person.getImageUri());
        values.putGender(person.getGender());
        Uri uri = values.insert(mContext);
        Timber.d("person saved to %s", uri.toString());
        return  uri;
    }

    @Override
    public int push(Person person) {
        Timber.d("pushing person %s, %s", person.getFirstName(), person.getSurname());
        //Implement sftp here
        DataUploadService.upload(mContext, person);
        return 0;
    }

    private void uploadImage(Person person){
        try {
            //upload
            String filename = createFilename(person.getFirstName(), person.getSurname());
            // load image from uri into file
            PersonContentValues values = new PersonContentValues();
            values.putDateSynced(System.currentTimeMillis());
            values.putSyncFailed(false);
            PersonSelection selection = new PersonSelection();
            selection.id(person.getId());
            values.update(mContext, selection);
            Timber.d("uploading image for %s", filename);
        }catch (Exception e){

        }
    }

    private String createFilename(String firstname, String lastname){
        return String.format(Locale.getDefault(), "%s_%s.jpg", firstname, lastname);
    }

    @Override
    public void loadProfiles(GetProfilesCallback callback) {
        callback.onProfilesLoaded(new ArrayList<Person>());
    }

    @Override
    public void loadProfile(Person person, GetProfileCallback callback) {
        callback.onProfileLoaded(new Person());

    }

    @Override
    public void syncAll() {
        DataUploadService.upload(mContext, null);
    }
}
