package com.jadeoti.capture.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;

import com.google.gson.Gson;
import com.jadeoti.capture.BuildConfig;
import com.jadeoti.capture.data.model.Person;
import com.jadeoti.capture.provider.person.PersonColumns;
import com.jadeoti.capture.provider.person.PersonContentValues;
import com.jadeoti.capture.provider.person.PersonSelection;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

/**
 * An {@code IntentService} that performs upload to SFTP server. It reads bank data from local db,
 * writes to a file then upload.
 *  Created by Morph-Deji on 23-Mar-17.
 */
public class DataUploadService extends IntentService{
    private static final String TAG = DataUploadService.class.getSimpleName();


    public static void upload(Context context, Person person) {
        Intent intent = new Intent(context, DataUploadService.class);
        Bundle extras = new Bundle();
        extras.putSerializable("profile", person);
        intent.putExtras(extras);
        context.startService(intent);
    }

    /**
     * Creates a DataUploadService.
     */
    public DataUploadService() {
        super(TAG);}

    @Override
    protected void onHandleIntent(Intent intent) {

        Bundle extras = intent.getExtras();

        if(extras == null) return;

        Person person = (Person) extras.getSerializable("profile");
        if(person != null){
            uploadProfile(person);
        }else {
            sync();
        }
    }

    private void uploadProfile(Person person) {
        File textFile = new File("");
        try {
            File root = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Profiles");
            // if directory exists and folder with name Profiles
            if (!root.exists()) {
                root.mkdirs(); // this will create folder.
            }
            String filename = String.format(Locale.getDefault(), "%s_%s.txt", person.getFirstName(), person.getSurname());
            textFile = new File(root, filename);  // file path to save
            FileWriter writer = new FileWriter(textFile);
            String data = new Gson().toJson(person, Person.class);
            writer.append(data);
            writer.flush();
            writer.close();
            Timber.d("commit: %s successfully created", filename);
        }catch (IOException ioe){
            ioe.printStackTrace();
            Timber.d("commit: failed to create document file");

        }


        //TODO:
        // write data to file
        // then send

        String SFTPHOST = BuildConfig.SFTP_IP;
        int    SFTPPORT = 22;
        String SFTPUSER = BuildConfig.SFTP_USER;
        String SFTPPASS = BuildConfig.SFTP_PASS;
        String SFTPWORKINGDIR = "/deji";


        Session session;
        Channel channel;
        ChannelSftp channelSftp;

        try{
            JSch jsch = new JSch();
            session = jsch.getSession(SFTPUSER,SFTPHOST,SFTPPORT);
            session.setPassword(SFTPPASS);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            channel = session.openChannel("sftp");
            channel.connect();

            channelSftp = (ChannelSftp)channel;
            channelSftp.cd(SFTPWORKINGDIR);
            // upload image
            String dest = String.format(Locale.getDefault(), "%s_%s.jpg", person.getFirstName(), person.getSurname());
            File imageFile = new File(person.getImageUri());
            channelSftp.put(new FileInputStream(imageFile), dest);

            // upload text file
            channelSftp.put(new FileInputStream(textFile), textFile.getName());

            // if we came this far mark dta as synced
            markAsSynced(person);

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private void sync(){
        // query content provider

        String[] args;
        String where = PersonColumns.SYNC_FAILED + " = ? ";
        args = new String[]{"1"};
        Cursor cursor = getContentResolver().query(PersonColumns.CONTENT_URI, PersonColumns.ALL_COLUMNS, where, args, null);
        List<Person> profiles = Person.from(cursor);

        if(!profiles.isEmpty()){
            for (Person profile :
                    profiles) {
                uploadProfile(profile);
            }
        }
    }

    private void markAsSynced(Person person){
        try {
            //upload
            // load image from uri into file
            PersonContentValues values = new PersonContentValues();
            values.putDateSynced(System.currentTimeMillis());
            values.putSyncFailed(false);
            PersonSelection selection = new PersonSelection();
            selection.id(person.getId());
            values.update(this, selection);
            Timber.d("mark as synced %s", person.toString());
        }catch (Exception e){

        }
    }

    private void markAsSynced(Person person, boolean synced){
        try {
            //upload
            // load image from uri into file
            PersonContentValues values = new PersonContentValues();
            values.putDateSynced(System.currentTimeMillis());
            values.putSyncFailed(!synced);
            PersonSelection selection = new PersonSelection();
            selection.id(person.getId());
            values.update(this, selection);
            Timber.d("mark as synced %s", person.toString());
        }catch (Exception e){

        }
    }


}
