package in.theapu.stunnel.ui;

/**
 * Created by APU V on 16-01-2019.
 */

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.kishan.askpermission.AskPermission;
import com.kishan.askpermission.ErrorCallback;
import com.kishan.askpermission.PermissionCallback;
import com.kishan.askpermission.PermissionInterface;

import in.theapu.stunnel.R;
import in.theapu.stunnel.service.ForegroundService;
import in.theapu.stunnel.util.Utility;
import static in.theapu.stunnel.util.Constants.*;

public class MainActivity extends Activity implements CompoundButton.OnCheckedChangeListener, PermissionCallback, ErrorCallback {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_PERMISSIONS = 20;

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

    private void reqPermission() {
        new AskPermission.Builder(this).setPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .setCallback(this)
                .setErrorCallback(this)
                .request(REQUEST_PERMISSIONS);
    }


    @Override
    public void onPermissionsGranted(int requestCode) {
        //Toast.makeText(this, "STunnel started", Toast.LENGTH_LONG).show();
        Utility.start();
    }


    @Override
    public void onPermissionsDenied(int requestCode) {
        Toast.makeText(this, "Permissions Denied.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onShowRationalDialog(final PermissionInterface permissionInterface, int requestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("STunnel need permission to read SSL certiticates from device storage.");
        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                permissionInterface.onDialogShown();
            }
        });
        builder.setNegativeButton(R.string.btn_cancel, null);
        builder.show();
    }

    @Override
    public void onShowSettings(final PermissionInterface permissionInterface, int requestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("We need permissions for this app. Open setting screen?");
        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                permissionInterface.onSettingsShown();
            }
        });
        builder.setNegativeButton(R.string.btn_cancel, null);
        builder.show();
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
            //Utility.start();
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
