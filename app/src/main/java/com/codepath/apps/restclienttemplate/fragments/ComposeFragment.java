package com.codepath.apps.restclienttemplate.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.databinding.FragmentComposeBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ComposeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComposeFragment extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    FragmentComposeBinding binding;

    // TODO: Rename and change types of parameters
    private String mParam1;

    public ComposeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ComposeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ComposeFragment newInstance(String param1, String param2) {
        ComposeFragment fragment = new ComposeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // View binding
        binding = FragmentComposeBinding.inflate(getLayoutInflater(), container, false);

        //layout of fragment stored in view's root
        View view = binding.getRoot();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Show soft keyboard automatically and request focus to field
        binding.composeTweet.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        binding.tweetButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                String tweetContent = binding.composeTweet.getText().toString();
                if(tweetContent.isEmpty()){
                    Toast.makeText(getActivity(), "Tweet can't be empty", Toast.LENGTH_SHORT).show();
                }
                if(tweetContent.length() > 140){
                    Toast.makeText(getActivity(), "Tweet can't exceed 140 characters", Toast.LENGTH_SHORT).show();

                }
                Toast.makeText(getActivity(), "Tweet is good!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}