package com.jadeoti.capture.persons;

import android.support.annotation.NonNull;

import com.jadeoti.capture.data.model.Person;

import java.util.List;

/**
 * Created by Morph-Deji on 23-Mar-17.
 */

public interface PersonsContract {
    interface View {

        void setProgressIndicator(boolean active);

        void showPersons(List<Person> persons);

        void showAddPerson();

        void showPersonDetailUi(long personId);

        void showDataNotAvailable();

        void setPresenter(PersonsPresenter presenter);

        void showServerStatus(boolean available);
    }

    interface UserActionsListener {

        void loadPersons(boolean forceUpdate);

        void addNewUser();

        void openPersonDetails(@NonNull Person requestedPerson);

        void checkServer();

        void runSync();
    }
}
