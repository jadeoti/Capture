package com.jadeoti.capture.form;

import android.net.Uri;

import com.jadeoti.capture.data.database.person_repo.PersonRepository;
import com.jadeoti.capture.data.model.Person;
import com.jadeoti.capture.util.ImageFile;

import java.io.IOException;

/**
 * Created by Morph-Deji on 22-Mar-17.
 */

public class FormPresenter implements FormContract.UserActionListener {

    private FormContract.View mFormView;

    private PersonRepository mPersonRepostory;

    private ImageFile mImageFile;


    public FormPresenter(FormContract.View formView, PersonRepository personRepository, ImageFile imageFile) {
        this.mFormView = formView;
        this.mPersonRepostory = personRepository;
        this.mImageFile = imageFile;
    }

    @Override
    public void commit(Person person) {
        mFormView.showProgress(true);
        // save to local db
        Uri result = mPersonRepostory.commit(person);

        // push to sftp server
        mPersonRepostory.push(person);

        mFormView.showProgress(false);
        if(result != Uri.EMPTY){
            mFormView.showSuccess("User saved successfully");
            mFormView.showPersonsList();
        }else {
            mFormView.showError("Error while saving user information");

        }

    }

    @Override
    public void push(Person person) {
        //start a service and listen for response on ui thread
    }

    @Override
    public void takePicture() throws IOException {
        String imageFileName = "firstname_surname";
        mImageFile.create(imageFileName, ".jpg");
        mFormView.openCamera(mImageFile.getPath());
    }

    @Override
    public void imageAvailable() {
        if (mImageFile.exists()) {
            mFormView.showImagePreview(mImageFile.getPath());
        } else {
            imageCaptureFailed();
        }
    }

    @Override
    public void imageCaptureFailed() {
        captureFailed();
    }

    private void captureFailed() {
        mImageFile.delete();
        mFormView.showImageError();
    }
}
