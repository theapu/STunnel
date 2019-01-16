package in.theapu.stunnel.ui;

/**
 * Created by APU V on 16-01-2019.
 */

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;

import in.theapu.stunnel.R;
import in.theapu.stunnel.util.Utility;

public class LogActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        ((EditText) findViewById(R.id.config)).setText(Utility.readLog());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
