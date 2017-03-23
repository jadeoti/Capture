package com.jadeoti.capture.form;

import android.support.annotation.NonNull;

import com.jadeoti.capture.data.model.Person;

import java.io.IOException;

/**
 * Created by Morph-Deji on 22-Mar-17.
 */

public interface FormContract {
    interface View{
        void showProgress(boolean show);
        void showError(String message);
        void showSuccess(String message);
        void openCamera(String saveTo);

        void showPersonsList();

        void showImagePreview(@NonNull String uri);

        void showImageError();
    }

    interface UserActionListener {
        void commit(Person person);
        void push(Person person);

        void takePicture() throws IOException;

        void imageAvailable();

        void imageCaptureFailed();
    }
}
