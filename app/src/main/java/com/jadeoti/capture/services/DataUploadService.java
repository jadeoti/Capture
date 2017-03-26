package com.jadeoti.capture.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.jadeoti.capture.BuildConfig;
import com.jadeoti.capture.data.model.Person;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.File;
import java.io.FileInputStream;

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
        if(person == null) return;





        String filePath = person.getImageUri(); //person.getFirstName() + "_" + person.getSurname() + ".txt";// get file path

        Timber.d("pushing file %s", filePath);


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
            File f = new File(filePath);
            channelSftp.put(new FileInputStream(f), f.getName());
        }catch(Exception ex){
            ex.printStackTrace();
        }

    }
}
