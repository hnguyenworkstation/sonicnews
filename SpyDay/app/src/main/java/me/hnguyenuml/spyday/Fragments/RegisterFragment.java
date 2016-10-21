package me.hnguyenuml.spyday.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import me.hnguyenuml.spyday.BasicApp.SpyDayApplication;
import me.hnguyenuml.spyday.BasicApp.SpyDayPreferenceManager;
import me.hnguyenuml.spyday.MapsActivity;
import me.hnguyenuml.spyday.R;

public class RegisterFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText mEmailField;
    private EditText mPassword;
    private EditText mConfirmPassword;
    private View rootView;
    private Button mRegButton;
    private View mRegisterView;
    private View mProgressView;
    private ImageButton mBack;
    private ImageButton mClose;
    private FragmentTransaction ft;

    private OnFragmentInteractionListener mListener;

    public RegisterFragment() {

    }

    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_register, container, false);

        mRegisterView = rootView.findViewById(R.id.email_register_form);
        mProgressView = rootView.findViewById(R.id.register_progress);

        mEmailField = (EditText) rootView.findViewById(R.id.reg_email);
        mPassword = (EditText) rootView.findViewById(R.id.reg_password);
        mConfirmPassword = (EditText) rootView.findViewById(R.id.reg_repassword);
        mRegButton = (Button) rootView.findViewById(R.id.email_reg_button);
        mRegButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attempRegister();
            }
        });

        mBack = (ImageButton)  rootView.findViewById(R.id.reg_back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.fade_out_to_right, R.anim.fade_in_from_left);
                ft.replace(R.id.contentFragment, new LoginFragment(), "LoginFragment");
                ft.commit();
            }
        });

        mClose = (ImageButton) rootView.findViewById(R.id.reg_close);
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_down_out, R.anim.slide_down_out);
                return;
            }
        });

        return rootView;
    }

    private void attempRegister() {
        // Reset errors.
        mEmailField.setError(null);
        mPassword.setError(null);
        mConfirmPassword.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailField.getText().toString();
        String password = mPassword.getText().toString();
        String confirmPass = mConfirmPassword.getText().toString();

        boolean isSatisfying = true;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError(getString(R.string.error_field_required));
            focusView = mEmailField;
            isSatisfying = false;
        } else if (TextUtils.isEmpty(password)) {
            mPassword.setError(getString(R.string.error_field_required));
            focusView = mPassword;
            isSatisfying = false;
        } else if (TextUtils.isEmpty(confirmPass)) {
            mConfirmPassword.setError(getString(R.string.error_field_required));
            focusView = mConfirmPassword;
            isSatisfying = false;
        } else if (!isEmailValid(email)) {
            mEmailField.setError(getString(R.string.error_invalid_email));
            focusView = mEmailField;
            isSatisfying = false;
        } else if (!(password.equals(confirmPass))) {
            mPassword.setError(getString(R.string.password_not_match));
            mConfirmPassword.setError(getString(R.string.password_not_match));
            focusView = mPassword;
            isSatisfying = false;
        }

        if (!isSatisfying) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            showProgress(true);
            SpyDayApplication.getInstance().getPrefManager()
                    .getFirebaseAuth().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Toast.makeText(getContext(), "createUserWithEmail:onComplete:"
                                    + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                            showProgress(false);
                            if (!task.isSuccessful()) {
                                Toast.makeText(getContext(), "Authentication failed." + task.getException(),
                                        Toast.LENGTH_SHORT).show();
                                if (task.getException()
                                        .getMessage()
                                        .contains(getString(R.string.error_duplicate_email))){
                                    // Todo: show a dialog asking for reset password
                                }
                            } else {
                                SpyDayApplication.getInstance().getPrefManager()
                                        .updateUserByFirebaseUID(SpyDayApplication.getInstance().getPrefManager()
                                                .getFirebaseAuth().getCurrentUser().getUid());
                                ft = getFragmentManager().beginTransaction();
                                ft.replace(R.id.contentFragment, new GetNameFragment(), "GetNameFragment");
                                ft.commit();
                            }
                        }
                    });
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegisterView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 8;
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
