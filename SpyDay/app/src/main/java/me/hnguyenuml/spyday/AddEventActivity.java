package me.hnguyenuml.spyday;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import me.hnguyenuml.spyday.BasicApp.SpyDayApplication;
import me.hnguyenuml.spyday.Static.Endpoint;
import me.hnguyenuml.spyday.UserContent.Event;

public class AddEventActivity extends BaseActivity {
    private Toolbar mToolbar;
    private ImageButton mAddImageBtn;
    private EditText mTitle;
    private EditText mDesc;
    private Uri imageUri = null;
    private SpyDayApplication mInstance;
    private ProgressDialog mProgress;

    private static final int GALLERY_REQUEST = 1;
    private static final int ZERO = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        mInstance = SpyDayApplication.getInstance();
        mProgress = new ProgressDialog(this);

        mToolbar = (Toolbar) findViewById(R.id.addev_toolbar);
        setSupportActionBar(mToolbar);
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);

        mAddImageBtn = (ImageButton) findViewById(R.id.addev_addimagebtn);
        mAddImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(gallery, GALLERY_REQUEST);
            }
        });

        mTitle = (EditText) findViewById(R.id.addev_title);
        mDesc = (EditText) findViewById(R.id.addev_desc);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_event_menu, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            imageUri = data.getData();
            mAddImageBtn.setImageURI(imageUri);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        getSupportFragmentManager().popBackStack();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.addev_menu_post) {
            Toast.makeText(getBaseContext(), "POST", Toast.LENGTH_SHORT).show();
            attempPostingEvent();
            return true;
        }

        if (id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void attempPostingEvent() {
        if (imageUri == null) {
            postEventWithoutImage();
        } else {
            postEventWithImages();
        }
    }

    private void postEventWithoutImage() {
        String title = mTitle.getText().toString();
        String desc = mDesc.getText().toString();
        mProgress.setMessage("Posting your event....");

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(desc)) {
            try {
                mProgress.show();
                DatabaseReference tRef = mInstance.getPrefManager().getFirebaseDatabase();
                tRef = tRef.child(Event.EVENT_DATABASE).push();
                tRef.child(Event.EVENT_ID).setValue(tRef.getKey());
                tRef.child(Event.EVENT_IMAGE).setValue(null);
                tRef.child(Event.EVENT_NAME).setValue("Hung Nguyen");
                tRef.child(Event.EVENT_PROFILEPIC).setValue("https://firebasestorage.googleapis.com/v0/b/spyday-fc1dc.appspot.com/o/ProfileImages%2FdxXvI8kX1wbSGofmwJHttoqZz313?alt=media&token=d29f96f0-4aff-49da-8807-33cf49e0131c");
                tRef.child(Event.EVENT_STATUS).setValue(desc);
                tRef.child(Event.EVENT_TIMESTAMP).setValue(mInstance.getNow());
                tRef.child(Event.EVENT_URL).setValue(title);
                tRef.child(Event.EVENT_LIKECOUNT).setValue(ZERO);
                tRef.child(Event.EVENT_COMMENTCOUNT).setValue(ZERO);

                onFinishPosting();
            } catch (Exception e){
                Toast.makeText(getBaseContext(), "failed posting: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            showRequiredFields();
        }
    }

    private void onFinishPosting() {
        mProgress.hide();
        NavUtils.navigateUpFromSameTask(this);
        Toast.makeText(getBaseContext(), "Posted", Toast.LENGTH_LONG).show();
        finish();
    }

    private void postEventWithImages() {
        StorageReference sRef = mInstance.getPrefManager().getFirebaseStorage();
        sRef = sRef.child(Event.EVENT_STORAGE);
        sRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String title = mTitle.getText().toString();
                String desc = mDesc.getText().toString();
                mProgress.setMessage("Posting your event....");
                if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(desc)) {
                    try {
                        mProgress.show();
                        DatabaseReference tRef = mInstance.getPrefManager().getFirebaseDatabase();
                        tRef = tRef.child(Event.EVENT_DATABASE).push();
                        tRef.child(Event.EVENT_ID).setValue(tRef.getKey());
                        tRef.child(Event.EVENT_IMAGE).setValue(taskSnapshot.getDownloadUrl().toString());
                        tRef.child(Event.EVENT_NAME).setValue("Hung Nguyen");
                        tRef.child(Event.EVENT_PROFILEPIC).setValue("https://firebasestorage.googleapis.com/v0/b/spyday-fc1dc.appspot.com/o/ProfileImages%2FdxXvI8kX1wbSGofmwJHttoqZz313?alt=media&token=d29f96f0-4aff-49da-8807-33cf49e0131c");
                        tRef.child(Event.EVENT_STATUS).setValue(desc);
                        tRef.child(Event.EVENT_TIMESTAMP).setValue(mInstance.getNow());
                        tRef.child(Event.EVENT_URL).setValue(title);
                        tRef.child(Event.EVENT_LIKECOUNT).setValue(ZERO);
                        tRef.child(Event.EVENT_COMMENTCOUNT).setValue(ZERO);
                    } catch (Exception e){
                        Toast.makeText(getBaseContext(), "failed posting: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    showRequiredFields();
                }
                onFinishPosting();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getBaseContext(), "failed to post: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showRequiredFields() {
        Toast.makeText(getBaseContext(), "Failed", Toast.LENGTH_LONG);
    }
}
