/*
 * Copyright (c) 2011. WillowTree Apps, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package oak.demo.other;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import oak.demo.EncryptedPreferences;
import oak.demo.OakDemoActivity;
import oak.demo.R;
import roboguice.inject.InjectView;

/**
 * User: mlake Date: 12/19/11 Time: 9:17 AM
 */

// START SNIPPET: obscured_prefs
public class ObscuredPrefsActivity extends OakDemoActivity {

    private static final String MY_APP_PREFENCES_NAME = "my_app_prefences_name";

    private SharedPreferences mNormalSharedPreferences;
    private EncryptedPreferences mEncryptedPreferences;
    @InjectView(R.id.my_edittext) private EditText mEditText;
    @InjectView(R.id.saved_content) private TextView mTextView;
    @InjectView(R.id.saved_content_decrypted) private TextView mDecryptedText;
    @InjectView(R.id.saved_content_decrypted_desc) private TextView mDecryptedDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.obscured_prefs_demo);

        mNormalSharedPreferences = getSharedPreferences(MY_APP_PREFENCES_NAME, MODE_PRIVATE);

        //EncryptedPreferences is defined by you and extends CryptoSharedPreferences
        mEncryptedPreferences = new EncryptedPreferences(this, mNormalSharedPreferences);
    }

    public void saveEncryptedClicked(View view) {
        //save input as encrypted
        mEncryptedPreferences.edit()
                .putString("first_name", mEditText.getText().toString())
                .commit();

        showRawContents(true);
    }

    public void saveNormallyClicked(View view) {
        //save unencrypted
        mNormalSharedPreferences.edit()
                .putString("first_name", mEditText.getText().toString())
                .commit();

        showRawContents(false);
    }

    private void showRawContents(boolean isEncrypted) {
        //demonstrate what could be viewed by a light-weight hacker
        mTextView.setText(mNormalSharedPreferences.getString("first_name", ""));

        //note: to read encrypted data, you would just use mEncryptedPreferences.getString(...
        if (isEncrypted) {
            mDecryptedDesc.setVisibility(View.VISIBLE);
            mDecryptedText.setText(mEncryptedPreferences.getString("first_name", ""));
            mDecryptedText.setVisibility(View.VISIBLE);
        } else {
            mDecryptedDesc.setVisibility(View.GONE);
            mDecryptedText.setVisibility(View.GONE);
        }
    }
}
// END SNIPPET: obscured_prefs

