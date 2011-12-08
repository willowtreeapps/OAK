package oak.demo;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import oak.CancelEditText;

/**
 * User: mlake Date: 12/8/11 Time: 10:56 AM
 */
public class CancelEditTextActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cancel_edit_text_demo);

        final CancelEditText cancelEditText = (CancelEditText) findViewById(R.id.cancel_edit_one);

        
        cancelEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (cancelEditText.getText().length() == 0){
                    Toast.makeText(CancelEditTextActivity.this,
                            "The CancelEditText was cleared", 1000)
                            .show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
