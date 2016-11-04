package me.hnguyenuml.spyday.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import me.hnguyenuml.spyday.BasicApp.SpyDayApplication;
import me.hnguyenuml.spyday.MapsActivity;
import me.hnguyenuml.spyday.R;
import me.hnguyenuml.spyday.UserContent.User;

public class GetProfilePictureFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ImageView profile;
    private View rootView;
    private static final int GALLERY_REQUEST = 1;
    private Button mFinishBtn;
    private Uri mImageURI = null;

    public GetProfilePictureFragment() {
        // Required empty public constructor
    }

    public static GetProfilePictureFragment newInstance(String param1, String param2) {
        GetProfilePictureFragment fragment = new GetProfilePictureFragment();
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
        rootView = inflater.inflate(R.layout.fragment_get_profile_picture, container, false);

        profile = (ImageView) rootView.findViewById(R.id.profile_image);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23 && getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // request permission until user accept it
                    getActivity().requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 4);
                    return;
                }
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });

        mFinishBtn = (Button) rootView.findViewById(R.id.getprofile_finish);
        mFinishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mImageURI != null) {
                    pushFullProfile();
                } else {
                    pushProfile();
                }
                getActivity().finish();
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_down_out, R.anim.slide_down_out);
            }
        });

        return rootView;
    }

    private void pushProfile() {
        User temp = SpyDayApplication.getInstance().getPrefManager().getUser();
        User user = new User(temp.getUserUID(), temp.getUserName(),
                temp.getUserNickName(), null);
        SpyDayApplication.getInstance()
                .getPrefManager()
                .getUserDatabase()
                .child(user.getUserUID()).setValue(user);
    }

    private void pushFullProfile() {
        final User temp = SpyDayApplication.getInstance().getPrefManager().getUser();
        StorageReference newPath = SpyDayApplication.getInstance()
                .getPrefManager()
                .getFirebaseStorage().child(getString(R.string.SR_profileImage))
                .child(temp.getUserUID());
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), mImageURI);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = newPath.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri dbURI = taskSnapshot.getDownloadUrl();

                    assert dbURI != null;
                    User user = new User(temp.getUserUID(), temp.getUserName(),
                            temp.getUserNickName(), dbURI.toString());

                    SpyDayApplication.getInstance()
                            .getPrefManager().storeUser(user);

                    SpyDayApplication.getInstance()
                            .getPrefManager()
                            .getUserDatabase()
                            .child(user.getUserUID()).setValue(user);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK) {
            mImageURI = data.getData();
            profile.setImageURI(mImageURI);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
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
