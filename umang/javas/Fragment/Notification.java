package com.umangSRTC.thesohankathait.umang.javas.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.umangSRTC.thesohankathait.umang.R;
import com.umangSRTC.thesohankathait.umang.javas.Utill.Admin;
import com.umangSRTC.thesohankathait.umang.javas.ViewHolders.NoticesViewHolder;
import com.umangSRTC.thesohankathait.umang.javas.model.Notices;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Notification extends Fragment {
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<Notices,NoticesViewHolder> firebaseRecyclerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_all_notification,container,false);
        recyclerView=view.findViewById(R.id.allNotificationRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        fetchDataFromFirebase("Notification");
        return view;
    }
    private void fetchDataFromFirebase(final String schoolName) {
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Notices, NoticesViewHolder>(Notices.class, R.layout.all_notification_row, NoticesViewHolder.class, FirebaseDatabase.getInstance().getReference("Category").child(schoolName)) {
            @Override
            protected void populateViewHolder(NoticesViewHolder noticesViewHolder, final Notices notices, final int postion) {
                noticesViewHolder.allNoticeTitleTextView.setText(notices.getTitle());
                noticesViewHolder.allNoticeDescriptionTextView.setText(notices.getDescription());
                Picasso.get().load(notices.getImageUrl()).into(noticesViewHolder.allNoticeImageView);
                noticesViewHolder.allNoticeSenderTextview.setText(notices.getSender());

                noticesViewHolder.allNoticeImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showFullImage(postion, notices);
                    }
                });

                if (Admin.CheckAdmin(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                    noticesViewHolder.allNotificationlinearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            deleteNotificationWarning(schoolName, notices);
                            return true;
                        }
                    });
                }
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    private void deleteNotificationWarning(final String schoolName, final Notices notices) {


        AlertDialog builder = new AlertDialog.Builder(getContext())
                .setCancelable(false)
                .setIcon(R.drawable.ic_launcher)
                .setMessage("Do you really want to delete this notification?")
                .setTitle("Delete Notification")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        deleteNotificaitonFromFirebase(schoolName, notices);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void deleteNotificaitonFromFirebase(String schoolName, final Notices notices) {

        FirebaseDatabase.getInstance().getReference("Category").child(schoolName).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (BothEquals(notices, dataSnapshot.getValue(Notices.class))) {
                    dataSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showFullImage(int postion, Notices notices) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.full_screen_notification, null, false);
        ImageView imageView = view.findViewById(R.id.allNoticeImageView);
        Picasso.get().load(notices.getImageUrl()).into(imageView);
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setView(view)
                .show();

    }

    public static Notification newInstance() {

        Bundle args = new Bundle();

        Notification fragment = new Notification();
        fragment.setArguments(args);
        return fragment;
    }
    private boolean BothEquals(Notices notices, Notices currentNotice) {


        return notices.getTitle().equals(currentNotice.getTitle()) &&
                notices.getSender().equals(currentNotice.getSender())&&
                notices.getImageUrl().equals(currentNotice.getImageUrl())&&
                notices.getDescription().equals(currentNotice.getDescription());
    }



}
