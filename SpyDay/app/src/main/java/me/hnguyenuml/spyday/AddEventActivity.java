package me.hnguyenuml.spyday;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
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

import com.google.firebase.database.DatabaseReference;

import me.hnguyenuml.spyday.BasicApp.SpyDayApplication;
import me.hnguyenuml.spyday.Static.Endpoint;

public class AddEventActivity extends BaseActivity {
    private Toolbar mToolbar;
    private ImageButton mAddImageBtn;
    private EditText mTitle;
    private EditText mDesc;
    private Uri imageUri = null;
    private SpyDayApplication mInstance;
    private ProgressDialog mProgress;

    private static final int GALLERY_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        mInstance = SpyDayApplication.getInstance();

        mToolbar = (Toolbar) findViewById(R.id.addev_toolbar);
        setSupportActionBar(mToolbar);
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);

        mAddImageBtn = (ImageButton) findViewById(R.id.addev_addimage);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.addev_post) {
            attempPostingEvent();
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
                tRef = tRef.child(Endpoint.DB_EVENT).push();
                tRef.child(Endpoint.EVENT_ID).setValue(mInstance.getPrefManager().getUser().getUserUID());
                tRef.child(Endpoint.EVENT_TITLE).setValue(title);
                tRef.child(Endpoint.EVENT_DESC).setValue(desc);
                tRef.child(Endpoint.EVENT_CREATED_AT).setValue(mInstance.getNow());
                onFinishPosting();
            } catch (Exception e){
                Toast.makeText(getBaseContext(), "failed posting: " + e.getMessage(), Toast.LENGTH_LONG);
            }
        } else {
            showRequiredFields();
        }
    }

    private void onFinishPosting() {
        mProgress.hide();
        getFragmentManager().popBackStackImmediate();
        Toast.makeText(getBaseContext(), "Posted", Toast.LENGTH_LONG);
        finish();
    }

    private void postEventWithImages() {

    }

    private void showRequiredFields() {
        Toast.makeText(getBaseContext(), "Failed", Toast.LENGTH_LONG);
    }
}
