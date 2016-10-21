package me.hnguyenuml.spyday.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import me.hnguyenuml.spyday.R;

public class GetNameFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private Button mNext;
    private EditText mNameField;
    private View rootView;

    public GetNameFragment() {
        // Required empty public constructor
    }

    public static GetNameFragment newInstance(String param1, String param2) {
        GetNameFragment fragment = new GetNameFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_get_name, container, false);
        mNameField = (EditText) rootView.findViewById(R.id.gn_put_name);

        mNext = (Button) rootView.findViewById(R.id.gn_next);
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mNameField.getText().toString();
                if (isValidName(name)) {
                    final FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.contentFragment, new GetProfilePictureFragment(), "GetProfile");
                    ft.commit();
                } else {
                    mNameField.setError(getString(R.string.error_field_required));
                }
            }
        });

        return rootView;
    }

    private boolean isValidName(String name) {
        if (name == null) {
            return false;
        }
        return true;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}