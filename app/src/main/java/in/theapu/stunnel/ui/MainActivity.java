package in.theapu.stunnel.ui;

/**
 * Created by APU V on 16-01-2019.
 */

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;


import java.util.List;

import in.theapu.stunnel.R;
import in.theapu.stunnel.service.ForegroundService;
import in.theapu.stunnel.util.Utility;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static in.theapu.stunnel.util.Constants.*;

public class MainActivity extends Activity implements CompoundButton.OnCheckedChangeListener, EasyPermissions.PermissionCallbacks {

    private static final String TAG = "MainActivity";
    private static final int STUNNEL_PERMISSIONS = 123;

    public EditText mConfig;
    public Switch mSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Utility.checkAndExtract(this);

        mConfig = (EditText) findViewById(R.id.config);

        mConfig.setText(Utility.readConfig());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
        public void onPermissionsGranted(int requestCode, List<String> list) {
        // Some permissions have been granted
        Utility.start();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Some permissions have been denied
        Toast.makeText(this, "Permissions Denied.", Toast.LENGTH_LONG).show();
    }

    @AfterPermissionGranted(STUNNEL_PERMISSIONS)
    private void reqPermission() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            Utility.start();
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.write_external_storage_rationale),
                    STUNNEL_PERMISSIONS, perms);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem i = menu.findItem(R.id.switch_action);
        mSwitch = (Switch) i.getActionView().findViewById(R.id.switch_action_button);
        mSwitch.setOnCheckedChangeListener(this);
        reloadState();

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mSwitch != null)
            reloadState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_config:
                save();
                return true;
            case R.id.revert_to_default:
                mConfig.setText(DEF_CONFIG);
                return true;
            case R.id.log:
                startActivity(new Intent(this, LogActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton button, boolean checked) {
        button.setEnabled(false);
        save();
        if (!Utility.isRunning()) {
            reqPermission();
        } else {
            Utility.stop();
        }

        mSwitch.postDelayed(new Runnable() {
            @Override
            public void run() {
                reloadState();
            }
        }, 1000);
    }

    private void save() {
        Utility.writeConfig(mConfig.getText().toString());
    }

    private void reloadState() {
        // Release the listener first
        mSwitch.setOnCheckedChangeListener(null);
        mSwitch.setChecked(Utility.isRunning());
        mSwitch.setOnCheckedChangeListener(this);
        mSwitch.setEnabled(true);

        if (Utility.isRunning()) {
            startService(new Intent(this, ForegroundService.class));
        } else {
            stopService(new Intent(this, ForegroundService.class));
        }
    }
}
