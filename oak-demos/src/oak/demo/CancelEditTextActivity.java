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

package oak.demo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
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

        RelativeLayout attr = (RelativeLayout)findViewById(R.id.attribution);
        attr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("http://www.willowtreeapps.com"));
                startActivity(i);
            }
        });
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
