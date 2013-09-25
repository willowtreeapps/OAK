package oak.demo.nfc;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import oak.demo.OakDemoActivity;
import oak.demo.R;
import oak.nfc.TagWriter;

/**
 * Created with IntelliJ IDEA.
 * User: ericrichardson
 * Date: 11/8/12
 * Time: 1:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class TagWriterActivity extends OakDemoActivity{
    private boolean mResumed = false;
    private TagWriter mTagWriter;
    private NfcAdapter mNfcAdapter;
    private EditText mNote;

    PendingIntent mNfcPendingIntent;
    IntentFilter[] mWriteTagFilters;
    IntentFilter[] mNdefExchangeFilters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        }catch(Exception e){
            TextView t = new TextView(this);
            t.setText("This Device doesn't support NFC");
            setContentView(t);
            return;
        }
        if(mNfcAdapter == null){
            TextView t = new TextView(this);
            t.setText("This Device doesn't support NFC");
            setContentView(t);
        }else if(!mNfcAdapter.isEnabled()){
            TextView t = new TextView(this);
            t.setText("Please turn on NFC");
            setContentView(t);
        }else{
            setContentView(R.layout.nfc_layout);
            findViewById(R.id.write_tag).setOnClickListener(mClickListener);
            mNote = ((EditText) findViewById(R.id.note));
            mNote.addTextChangedListener(mTextWatcher);
            // Handle all of our received NFC intents in this activity.
            mNfcPendingIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

            // Intent filters for reading a note from a tag or exchanging over p2p.
            IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
            try {
                ndefDetected.addDataType("application/OAKTagWriter");
            } catch (IntentFilter.MalformedMimeTypeException e) { }
            mNdefExchangeFilters = new IntentFilter[] { ndefDetected };

            // Intent filters for writing to a tag
            IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
            mWriteTagFilters = new IntentFilter[] { tagDetected };

            mTagWriter = new TagWriter(this, "OAKTagWriter", this, mNfcPendingIntent, mNdefExchangeFilters);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mResumed = true;
        if(mNfcAdapter != null && mNfcAdapter.isEnabled()){
            // Sticky notes received from Android
            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
                mTagWriter.getNdefMessages(getIntent());
                setNoteBody(new String(mTagWriter.getBytePayload()));
                setIntent(new Intent()); // Consume this intent.
            }
            mTagWriter.enableNdefExchangeMode();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mResumed = false;
        if(mNfcAdapter != null && mNfcAdapter.isEnabled())
            mTagWriter.disableNdefExchangeMode();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // NDEF exchange mode
        if (!mTagWriter.isWriteMode() && mTagWriter.isNdefDiscovered(intent)) {
            NdefMessage[] msgs = mTagWriter.getNdefMessages(intent);
            promptForContent(msgs[0]);
        }

        // Tag writing mode
        if (mTagWriter.isWriteMode() && mTagWriter.isTagDiscovered(intent)) {
            mTagWriter.writeToTag(mTagWriter.getTextViewAsNdef(mNote), mTagWriter.getDetectedTag(intent));
        }
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            // Write to a tag for as long as the dialog is shown.
            mTagWriter.disableNdefExchangeMode();
            mTagWriter.enableTagWriteMode();

            new AlertDialog.Builder(TagWriterActivity.this).setTitle("Touch tag to write")
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            mTagWriter.disableTagWriteMode();
                            mTagWriter.enableNdefExchangeMode();
                        }
                    }).create().show();
        }
    };

    private TextWatcher mTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

        }

        @Override
        public void afterTextChanged(Editable arg0) {
            if (mResumed) {
                mTagWriter.enableNdefExchangeMode();
            }
        }
    };

    private void setNoteBody(String body) {
        Editable text = mNote.getText();
        text.clear();
        text.append(body);
    }

    private void promptForContent(final NdefMessage msg) {
        new AlertDialog.Builder(this).setTitle("Replace current content?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        String body = new String(msg.getRecords()[0].getPayload());
                        setNoteBody(body);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                }).show();
    }

}
