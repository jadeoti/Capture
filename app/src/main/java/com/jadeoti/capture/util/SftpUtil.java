package com.jadeoti.capture.util;

import com.jadeoti.capture.BuildConfig;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * Created by Morph-Deji on 23-Mar-17.
 */

public class SftpUtil {
    public static boolean isConnected(){
        boolean isConnected = false;
        JSch jsch = new JSch();
        Session session = null;
        try {
            session = jsch.getSession(BuildConfig.SFTP_USER,BuildConfig.SFTP_IP,22);
            session.setPassword(BuildConfig.SFTP_PASS);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect(5000);
            Channel channel = session.openChannel("sftp");
            channel.connect(5000);
            return channel.isConnected();
        } catch (JSchException e) {
            e.printStackTrace();
            return isConnected;
        }

    }
}
