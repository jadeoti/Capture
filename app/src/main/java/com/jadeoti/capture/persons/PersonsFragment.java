package com.jadeoti.capture.persons;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jadeoti.capture.R;
import com.jadeoti.capture.data.adapters.PersonRecyclerViewAdapter;
import com.jadeoti.capture.data.model.Person;
import com.jadeoti.capture.form.FormActivity;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonsFragment extends Fragment implements PersonsContract.View,
        PersonRecyclerViewAdapter.PersonInteractionListener {

    private static final int RC_ADD_USER = 0x1003;
    private PersonsContract.UserActionsListener mActionListener;

    private ArrayList<Person> mPersons;

    private PersonRecyclerViewAdapter mAdapter;

    private ImageView mServerStatusView;
    private TextView mServerStatusMessageView;


    public static PersonsFragment newInstance() {
        Bundle args = new Bundle();

        PersonsFragment fragment = new PersonsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public PersonsFragment() {
        // Required empty public constructor
    }

    @Override
    public void setPresenter(PersonsPresenter presenter) {
        mActionListener = presenter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPersons = new ArrayList<>();

        mAdapter = new PersonRecyclerViewAdapter(getContext(), mPersons, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_persons, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        Context context = view.getContext();

        mServerStatusView = (ImageView) view.findViewById(R.id.syncStatus);
        mServerStatusMessageView = (TextView) view.findViewById(R.id.statusMessage);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(mAdapter);

        // Pull-to-refresh
        SwipeRefreshLayout swipeRefreshLayout =
                (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestContentRefresh();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null) {
            requestContentRefresh();
        }
        if (mPersons.isEmpty()) {
            showDataNotAvailable();
        }
    }

    private void requestContentRefresh() {
        Timber.d("start loading developers");
        if (mActionListener != null) {
            mActionListener.loadPersons(true);
            mActionListener.checkServer();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        requestContentRefresh();
    }

    @Override
    public void setProgressIndicator(boolean active) {

    }

    @Override
    public void showPersons(List<Person> persons) {
        mAdapter.setData(persons);
    }

    @Override
    public void showAddPerson() {
        Intent addPersonIntent = new Intent(getContext(), FormActivity.class);
        startActivityForResult(addPersonIntent, RC_ADD_USER);
    }

    @Override
    public void showPersonDetailUi(long personId) {

    }

    @Override
    public void onPersonClick(View view, int position, Person order) {

    }

    @Override
    public void showDataNotAvailable() {
        //Toast.makeText(getContext(), "Data not available", Toast.LENGTH_LONG).show();
    }

    @Override
    public void showServerStatus(boolean available) {

        // Pull-to-refresh hide
        final SwipeRefreshLayout srl =
                (SwipeRefreshLayout) getView().findViewById(R.id.refresh_layout);
        srl.post(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(false);
            }
        });

        Timber.d("available");
        mServerStatusMessageView.setText("");

        if (available) {
            mServerStatusView.setColorFilter(Color.rgb(0, 255, 0), PorterDuff.Mode.MULTIPLY);
            mServerStatusMessageView.setText(R.string.connected);

            Toast.makeText(getContext(), "Connection to server successful", Toast.LENGTH_LONG).show();
        } else {
            mServerStatusView.setColorFilter(Color.rgb(255, 0, 0), PorterDuff.Mode.MULTIPLY);
            mServerStatusMessageView.setText(R.string.not_connected);
            Toast.makeText(getContext(), "Connection to server failed", Toast.LENGTH_LONG).show();
        }

        if(available){
            mActionListener.runSync();
        }
    }
}
