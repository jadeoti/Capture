package com.jadeoti.capture.data.model;

import android.database.Cursor;

import com.jadeoti.capture.provider.person.PersonColumns;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Morph-Deji on 22-Mar-17.
 */

public class Person extends BaseModel {
    private String firstName;
    private String surname;
    private String otherNames;
    private String emailAddress;
    private String gender;
    private String imageUri;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getOtherNames() {
        return otherNames;
    }

    public void setOtherNames(String orderNames) {
        this.otherNames = orderNames;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public static List<Person> from(Cursor cursor) {
        List<Person> persons = new ArrayList<>();
        do {
            Person person = new Person();
            person.setFirstName(cursor.getString(cursor.getColumnIndex(PersonColumns.FIRST_NAME)));
            person.setSurname(cursor.getString(cursor.getColumnIndex(PersonColumns.SURNAME)));
            person.setOtherNames(cursor.getString(cursor.getColumnIndex(PersonColumns.OTHER_NAMES)));
            person.setGender(cursor.getString(cursor.getColumnIndex(PersonColumns.GENDER)));
            person.setImageUri(cursor.getString(cursor.getColumnIndex(PersonColumns.IMAGE_URI)));
            person.setEmailAddress(cursor.getString(cursor.getColumnIndex(PersonColumns.EMAIL_ADDRESS)));
            person.setSyncFailed(cursor.getInt(cursor.getColumnIndex(PersonColumns.SYNC_FAILED)) == 1);
            person.setDateSynced(cursor.getLong(cursor.getColumnIndex(PersonColumns.DATE_SYNCED)));
            person.setDateCreated(cursor.getLong(cursor.getColumnIndex(PersonColumns.DATE_CREATED)));
            person.setDateModified(cursor.getLong(cursor.getColumnIndex(PersonColumns.DATE_MODIFIED)));
            persons.add(person);
            cursor.moveToNext();
        } while (!cursor.isAfterLast());
        return persons;
    }
}
