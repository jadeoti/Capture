package com.jadeoti.capture.persons;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.jadeoti.capture.Injection;
import com.jadeoti.capture.R;
import com.jadeoti.capture.data.database.person_repo.LoaderProvider;

import static com.jadeoti.capture.data.database.person_repo.LoaderProvider.ALL;

public class ProfilesActivity extends AppCompatActivity {

    private static final String CURRENT_FILTERING_KEY = "filtering_key";

    private PersonsPresenter mPersonsPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiles);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the presenter
        LoaderProvider loaderProvider = new LoaderProvider(this);

        // Load previously saved state, if available.
        String typeFilter = ALL;
        if (savedInstanceState != null) {
            typeFilter = savedInstanceState.getString(CURRENT_FILTERING_KEY, ALL);
        }


        PersonsFragment personsFragment =
                (PersonsFragment) getSupportFragmentManager().findFragmentById(R.id.main_content);
        if (personsFragment == null) {
            // Create the fragment
            personsFragment = PersonsFragment.newInstance();
        }

        if(savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.main_content, personsFragment);
            transaction.commit();
        }

        mPersonsPresenter = new PersonsPresenter(
                personsFragment,
                Injection.providerPersonRepository(this),
                loaderProvider,
                getSupportLoaderManager(),
                typeFilter
        );
        personsFragment.setPresenter(mPersonsPresenter);

        // Set up floating action button
        FloatingActionButton fab =
                (FloatingActionButton) findViewById(R.id.fab);

        //fab.setImageResource(R.drawable.ic_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPersonsPresenter.addNewUser();
            }
        });


    }

}
