package com.jadeoti.capture.data.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jadeoti.capture.R;
import com.jadeoti.capture.data.model.Person;

import java.util.List;

/**
 * Created by Morph-Deji on 23-Mar-2016.
 */

public class PersonRecyclerViewAdapter extends RecyclerView.Adapter<PersonRecyclerViewAdapter.ViewHolder> {

    private List<Person> mPersons;
    private Context context;
    private PersonInteractionListener mPersonInteractionListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public Person mBoundPerson;
        public final View mView;
        public final TextView mPersonName, mPersonGender, mPersonEmail;
        public final ImageView mUserAvatar;
        public final ImageView mSyncIcon;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mPersonName = (TextView) view.findViewById(R.id.dev_name);
            mSyncIcon = (ImageView) view.findViewById(R.id.syncStatus);
            mPersonEmail = (TextView) view.findViewById(R.id.email);
            mPersonGender = (TextView) view.findViewById(R.id.gender);
            mUserAvatar = (ImageView) view.findViewById(R.id.dev_image);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mPersonName.getText();
        }
    }

    public Person getValueAt(int position) {
        return mPersons.get(position);
    }

    public PersonRecyclerViewAdapter(Context context, List<Person> items, PersonInteractionListener listener) {
        mPersons = items;
        this.context = context;
        mPersonInteractionListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_profiles, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mBoundPerson = mPersons.get(position);
        holder.mPersonName.setText(String.format("%s, %s %s ",
                holder.mBoundPerson.getSurname(),
                holder.mBoundPerson.getFirstName(), holder.mBoundPerson.getOtherNames()));

        holder.mPersonGender.setText(holder.mBoundPerson.getGender());
        holder.mPersonEmail.setText(holder.mBoundPerson.getEmailAddress());

        int syncStatus = 0;
        if(holder.mBoundPerson.isSyncFailed()){
            syncStatus = R.drawable.not_synced;
        }else {
            syncStatus = R.drawable.cloud_done;
        }

        Glide.with(context)
                .load(syncStatus).into(holder.mSyncIcon);

        int placeholder = 0;
        if(holder.mBoundPerson.getGender().equals("Male")){
            placeholder = R.drawable.avatar_4_raster;
        }else {
            placeholder = R.drawable.avatar_7_raster;
        }
        Glide.with(context)
                .load(holder.mBoundPerson.getImageUri())
                .error(placeholder)
                .into(holder.mUserAvatar);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = holder.getAdapterPosition();
                Person person = getValueAt(adapterPosition);
                mPersonInteractionListener.onPersonClick(view, adapterPosition, person);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mPersons.size();
    }

    public void setData(List<Person> orders) {
        mPersons = orders;
        notifyDataSetChanged();
    }

    public interface PersonInteractionListener {
        void onPersonClick(View view, int position, Person order);
    }
}