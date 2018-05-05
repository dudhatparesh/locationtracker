package com.enggdream.locationtracker;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.enggdream.locationtracker.service.LocationTrackerService;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {
    private static final int RC_LOCATION_PERMISSION = 1001;
    private static final int REQUEST_CHECK_SETTINGS = 1002;
    private static final String TAG = MainActivity.class.getName();
    FirebaseDatabase firebaseDatabase;
    @BindView(R.id.btn_track)
    Button btnTrack;
    @BindView(R.id.et_mobile_no)
    EditText etMobileNo;

    // Create a new mDispatcher using the Google Play driver.
    FirebaseJobDispatcher mDispatcher ;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseDatabase = MainApplication.getFirebaseDatabase();
        ButterKnife.bind(this);
        btnTrack.setText(PrefUtils.isTrackingOn(this,false)?
                R.string.stop_tracking:R.string.start_tracking);
        mDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        etMobileNo.setText(PrefUtils.getMobileNo(this,""));
    }
    @OnClick({R.id.btn_track,R.id.btn_share})
    void onClick(View v){
        switch (v.getId()) {
            case R.id.btn_track:
                trackClicked();
                break;
            case R.id.btn_share:
                shareClicked();
                break;
        }
    }

    private void shareClicked() {

    }
    @AfterPermissionGranted(RC_LOCATION_PERMISSION)
    public void trackClicked() {
        if(PrefUtils.isTrackingOn(this,false)){
            mDispatcher.cancelAll();
            btnTrack.setText(R.string.start_tracking);
            Bundle extras= new Bundle();
            extras.putBoolean("is_more_work_needed",false);
            Job trackingJob = mDispatcher.newJobBuilder()
                    .setExtras(extras)
                    .setTag("my_job")
                    .setService(LocationTrackerService.class)
                    .setRecurring(false)
                    .setLifetime(Lifetime.FOREVER)
                    .setTrigger(Trigger.NOW)
                    .setReplaceCurrent(true)
                    .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                    .build();
            mDispatcher.schedule(trackingJob);
//            mDispatcher.cancel("my_job");
            PrefUtils.setTracking(this,false);
        }else {
            if(TextUtils.isEmpty(etMobileNo.getText().toString())){
                Toast.makeText(this, "Please enter mobile number", Toast.LENGTH_SHORT).show();
                return;
            }
            String[] perms = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
            if (EasyPermissions.hasPermissions(this, perms)) {
                checkLocationSettings();
            } else {
                EasyPermissions.requestPermissions(this,
                        getString(R.string.rationale_location_permission)
                        , RC_LOCATION_PERMISSION, perms);
            }
        }
    }

    private void startTracking() {
        PrefUtils.setMobileNo(this,etMobileNo.getText().toString());
        //Toast.makeText(this,getString(R.string.tracking_started),Toast.LENGTH_LONG).show();

        Bundle extras= new Bundle();
        extras.putBoolean("is_more_work_needed",true);
        Job trackingJob = mDispatcher.newJobBuilder()
                .setExtras(extras)
                .setTag("my_job")
                .setService(LocationTrackerService.class)
                .setRecurring(false)
                .setLifetime(Lifetime.FOREVER)
                .setTrigger(Trigger.NOW)
                .setReplaceCurrent(true)
                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                .build();
        mDispatcher.schedule(trackingJob);
        PrefUtils.setTracking(this,true);
        btnTrack.setText(R.string.stop_tracking);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    protected void checkLocationSettings(){
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(
                CommonUtils.getLocationSettingBuilder());


        task.addOnSuccessListener( new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
                startTracking();
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MainActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == REQUEST_CHECK_SETTINGS){
                startTracking();
            }
        }else{
            Log.d(TAG, "onActivityResult: "+resultCode);
        }
    }
}
