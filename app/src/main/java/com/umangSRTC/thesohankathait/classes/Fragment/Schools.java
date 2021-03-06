package com.umangSRTC.thesohankathait.classes.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.umangSRTC.thesohankathait.umang.R;
import com.umangSRTC.thesohankathait.classes.Activity.AllNotification;
import com.umangSRTC.thesohankathait.classes.Utill.Initialisation;
import com.umangSRTC.thesohankathait.classes.Adapter.SchoolsArrayAdapter;
import com.umangSRTC.thesohankathait.classes.Utill.Admin;
import com.umangSRTC.thesohankathait.classes.model.User;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Schools extends Fragment {

    private ListView schoolsListView;
    private Button addSchoolsFloatingActionButton;
    public SchoolsArrayAdapter schoolsArrayAdapter;
    private TextView hintTextView;

    public static Schools schoolsFragmentInstance;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.schools_fragment,container,false);

        // requied for refreshing school list from outside
        schoolsFragmentInstance = this;


        schoolsListView=view.findViewById(R.id.schoolsListView);
        addSchoolsFloatingActionButton=view.findViewById(R.id.addSchoolsFloatingActionButton);
        hintTextView=view.findViewById(R.id.hintTextview);

        //fon movable textview
        hintTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        hintTextView.setSingleLine(true);
        hintTextView.setSelected(true);

        if(!Admin.CheckAdmin(User.getCurrentUser().email))
            addSchoolsFloatingActionButton.setVisibility(View.GONE);
//        for(int i=1;i<Initialisation.schools.size();i++){
//            schoolArrayList.add(Initialisation.schools.get(i));
//        }

        schoolsArrayAdapter=new SchoolsArrayAdapter(getContext(),Initialisation.schoolArrayList);
        schoolsListView.setAdapter(schoolsArrayAdapter);
        schoolsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(getContext(), "itemClicked", Toast.LENGTH_SHORT).show();

                Intent allNotificationActivityIntent=new Intent(getContext(),AllNotification.class);
                allNotificationActivityIntent.putExtra("SCHOOL",Initialisation.schoolArrayList.get(position));
                startActivity(allNotificationActivityIntent);
            }
        });

        schoolsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(Admin.CheckAdmin(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
                    deleteWarning(Initialisation.schoolArrayList.get(position),position);
                return true;
            }
        });

        addSchoolsFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    addNewSchool();

            }
        });

        return view;
    }

    private void deleteWarning(final String school, final int position) {

        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setMessage("Do you really want to delete "+school+"?")
                .setIcon(R.drawable.ic_warning_black_24dp)
                .setTitle("Delete")
                .setPositiveButton("Continue Anyway", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteSchoolFromFirebase(school);
                        Initialisation.schoolArrayList.remove(position);
                        schoolsArrayAdapter.notifyDataSetChanged();
                       // Upload.spinnerArrayAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Back", null)
                .show();

    }

    private void deleteSchoolFromFirebase(final String school) {

        FirebaseDatabase.getInstance().getReference("Schools").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot finalDataSnapshot:dataSnapshot.getChildren()){
                    if(finalDataSnapshot.getValue().toString().equals(school)){
                        finalDataSnapshot.getRef().removeValue();

                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        deleteFromValue(school,"Category");
        deleteFromValue(school,"Requests");

    }

    private void deleteFromValue(final String school, String category) {

        FirebaseDatabase.getInstance().getReference(category).child(school).removeValue();

    }

    private void addNewSchool() {

        LayoutInflater inflater=getLayoutInflater();
        final View view=inflater.inflate(R.layout.add_school_view,null,false);

        final EditText addSchoolEditText=view.findViewById(R.id.newSchoolEditText);

         AlertDialog builder=new AlertDialog.Builder(getContext())
                .setCancelable(false)
                 .setView(view)
                .setIcon(R.drawable.ic_launcher_background)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!addSchoolEditText.getText().toString().trim().equals("")&&addSchoolIntoFirebase(addSchoolEditText.getText().toString())) {
                            InputMethodManager inputMethodManager=(InputMethodManager)getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);

                            dialog.dismiss();
                        }
                        else{
                            Toast.makeText(getContext(), "Try again!", Toast.LENGTH_SHORT).show();
                        }
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

    private boolean addSchoolIntoFirebase(String schoolName) {
        if(Admin.isSchoolCorrect(schoolName)){
            FirebaseDatabase.getInstance().getReference("Schools").push().setValue(schoolName);
            return true;
        }
        else{
            return false;
        }

    }

    public static Schools newInstance() {
        
        Bundle args = new Bundle();
        
        Schools fragment = new Schools();
        fragment.setArguments(args);
        return fragment;
    }
}
