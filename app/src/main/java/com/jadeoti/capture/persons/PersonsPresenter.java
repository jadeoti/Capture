package com.jadeoti.capture.persons;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.jadeoti.capture.data.database.person_repo.LoaderProvider;
import com.jadeoti.capture.data.database.person_repo.PersonRepository;
import com.jadeoti.capture.data.model.Person;
import com.jadeoti.capture.util.SftpUtil;

import java.util.List;

import timber.log.Timber;

/**
 * Created by Morph-Deji on 23-Mar-17.
 */

public class PersonsPresenter implements PersonsContract.UserActionsListener,
        LoaderManager.LoaderCallbacks<Cursor>,
        PersonRepository.GetProfilesCallback,
        PersonRepository.GetProfileCallback {


    private @LoaderProvider.TypeFilter String mTypFilter = LoaderProvider.ALL;

    public final static int PROFILE_LOADER = 1;


    @NonNull
    private final LoaderProvider mLoaderProvider;

    @NonNull
    private final LoaderManager mLoaderManager;

    @NonNull
    private PersonsContract.View mProfilesView;

    @NonNull
    private PersonRepository mPersonRepository;

    public PersonsPresenter(PersonsContract.View profilesView,
                            PersonRepository personRepository,
                            LoaderProvider loaderProvider,
                            LoaderManager loaderManager, String typeFilter) {
        this.mProfilesView = profilesView;
        this.mPersonRepository = personRepository;
        this.mLoaderManager = loaderManager;
        this.mLoaderProvider = loaderProvider;
        this.mTypFilter = typeFilter;
        mProfilesView.setPresenter(this);
    }

    @Override
    public void loadPersons(boolean forceUpdate) {

        mPersonRepository.loadProfiles(this);
    }

    @Override
    public void addNewUser() {
        mProfilesView.showAddPerson();
    }

    @Override
    public void openPersonDetails(@NonNull Person requestedPerson) {
        mProfilesView.showPersonDetailUi(requestedPerson.getId());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return mLoaderProvider.createFilteredProfileLoader(mTypFilter);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            List<Person> persons = Person.from(data);
            mProfilesView.showPersons(persons);
        } else {
            // NO-OP, add mode.
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    @Override
    public void onProfilesLoaded(List<Person> persons) {
        Bundle args = new Bundle();
        args.putString("query", mTypFilter);
        // we don't care about the result since the CursorLoader will load the data for us
        if (mLoaderManager.getLoader(PROFILE_LOADER) == null) {
            mLoaderManager.initLoader(PROFILE_LOADER, args, this);
        } else {
            mLoaderManager.restartLoader(PROFILE_LOADER, args, this);
        }

    }

    @Override
    public void onDataNotAvailable() {
        mProfilesView.setProgressIndicator(false);
        mProfilesView.showDataNotAvailable();
    }

    @Override
    public void onProfileLoaded(Person person) {

    }

    @Override
    public void checkServer() {
        new CheckServerTask().execute();
    }


    private class CheckServerTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean response =  SftpUtil.isConnected();
            Timber.d("server available %b", response);
            return response;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean){
            super.onPostExecute(aBoolean);
           mProfilesView.showServerStatus(aBoolean);
        }

    }

}
