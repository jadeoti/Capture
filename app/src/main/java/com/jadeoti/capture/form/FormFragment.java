package com.jadeoti.capture.form;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.jadeoti.capture.BuildConfig;
import com.jadeoti.capture.Injection;
import com.jadeoti.capture.R;
import com.jadeoti.capture.data.model.Person;

import java.io.File;
import java.io.IOException;

import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class FormFragment extends Fragment implements FormContract.View {

    public static final int REQUEST_CODE_IMAGE_CAPTURE = 0x1001;
    private static final int REQUEST_CODE_WRITE_PERMISSION = 0x1002;


    private FormContract.UserActionListener mActionListener;

    // views
    private EditText mFirstname;
    private EditText mSurname;
    private EditText mEmailAddress;
    private ImageView mImageThumbnail;
    private Spinner mGenderView;
    private Button mSubmit;
    private EditText mOtherNames;


    // ui state
    private Person mPerson;

    private Uri mImageUri;


    public static FormFragment newInstance() {


        FormFragment fragment = new FormFragment();
        return fragment;
    }

    public FormFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            mPerson = (Person) savedInstanceState.getSerializable("person");
        }else {
           mPerson = new Person();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_form, container, false);
        mFirstname = (EditText) root.findViewById(R.id.firstname);
        mSurname = (EditText) root.findViewById(R.id.surname);
        mEmailAddress = (EditText) root.findViewById(R.id.email_address);
        mGenderView = (Spinner) root.findViewById(R.id.gender);
        mOtherNames = (EditText) root.findViewById(R.id.other_names);
        mImageThumbnail = (ImageView) root.findViewById(R.id.user_image);
        mSubmit = (Button) root.findViewById(R.id.btnSubmit);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //params should be injected
        mActionListener = new FormPresenter(this,
                Injection.providerPersonRepository(getContext()),
                Injection.provideImageFile());

        mImageThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
            }
        });
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidEntry()) {
                    mActionListener.commit(mPerson);
                }

            }
        });


    }

    private void captureImage() {
        if(ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(getActivity(),permissions, REQUEST_CODE_WRITE_PERMISSION );

        }else {
            try {

                mActionListener.takePicture();
            } catch (IOException ioe) {
                ioe.printStackTrace();
                Timber.d("picture error: %s, reason: %s", ioe.getMessage(), ioe.getCause());
                if (getView() != null) {
                    Snackbar.make(getView(), getString(R.string.take_picture_error),
                            Snackbar.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void showProgress(boolean show) {

    }

    @Override
    public void showError(String message) {
        Snackbar.make(mFirstname, message, Snackbar.LENGTH_LONG).show();

    }

    @Override
    public void showSuccess(String message) {
        Snackbar.make(mFirstname, message, Snackbar.LENGTH_LONG).show();

    }


    @Override
    public void openCamera(String saveTo) {
        // Open the camera to take a picture.
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Check if there is a camera app installed to handle our Intent
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {

            try {
                File photoFile = new File(saveTo);
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        BuildConfig.APPLICATION_ID + ".fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_CODE_IMAGE_CAPTURE);
            }catch (Exception e){
                Snackbar.make(mFirstname, getString(R.string.cannot_connect_to_camera_message),
                        Snackbar.LENGTH_SHORT).show();
            }
        } else {
            Snackbar.make(mFirstname, getString(R.string.cannot_connect_to_camera_message),
                    Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showImagePreview(@NonNull String imageUrl) {
        if (TextUtils.isEmpty(imageUrl)) {
            throw new IllegalStateException("imageUrl cannot be null or empty!");
        }
        mImageThumbnail.setVisibility(View.VISIBLE);


        // This app uses Glide for image loading
        Glide.with(this)
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(new GlideDrawableImageViewTarget(mImageThumbnail) {
                    @Override
                    public void onResourceReady(GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable> animation) {
                        super.onResourceReady(resource, animation);
                    }
                });
    }

    @Override
    public void showImageError() {
        Snackbar.make(mFirstname, getString(R.string.cannot_connect_to_camera_message),
                Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If an image is received, display it on the ImageView.
        if (REQUEST_CODE_IMAGE_CAPTURE == requestCode && Activity.RESULT_OK == resultCode) {
            mActionListener.imageAvailable();
        } else {
            mActionListener.imageCaptureFailed();
        }
    }

    public boolean isValidEntry() {
        String firstname = mFirstname.getText().toString();
        String surname = mSurname.getText().toString();
        String email = mEmailAddress.getText().toString();
        String othernames = mOtherNames.getText().toString();
        String gender = mGenderView.getSelectedItem().toString();

        // hydrate
        mPerson.setFirstName(firstname);
        mPerson.setSurname(surname);
        mPerson.setEmailAddress(email);
        mPerson.setOtherNames(othernames);
        mPerson.setGender(gender);

        //TODO: fix image capture and redo
        mPerson.setImageUri(Uri.EMPTY.toString());

        //String imageUrl = mImageThumbnail.getText().toString();

        if (TextUtils.isEmpty(firstname)) {
            mFirstname.setError(getString(R.string.invalid));
            return false;
        } else if (TextUtils.isEmpty(surname)) {
            mSurname.setError(getString(R.string.invalid));
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmailAddress.setError(getString(R.string.invalid));
            return false;
        }

        return true; // if we made it here we are good
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_WRITE_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    captureImage();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
        }
    }

    @Override
    public void showPersonsList() {
        getActivity().finish();
        getActivity().setResult(Activity.RESULT_OK);
    }
}
