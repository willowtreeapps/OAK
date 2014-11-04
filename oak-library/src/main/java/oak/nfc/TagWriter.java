package oak.nfc;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Parcelable;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: ericrichardson
 * Date: 11/8/12
 * Time: 1:25 PM
 * To change this template use File | Settings | File Templates.
 */
@TargetApi(14)
@Deprecated
public class TagWriter {
    private static final String TAG = "OakTagWriter";
    NfcAdapter adapter;
    PendingIntent nfcPendingIntent;

    private NdefMessage message;
    IntentFilter[] writeTagFilters;
    IntentFilter[] ndefExchangeFilters;

    private boolean writeMode = false;
    private Tag tag;
    private Activity activity;

    private String mimeType;

    private NdefMessage[] messages;
    private byte[] mPayload;
    private String payloadString;


    /**
     * The constructor for a write tag object
     *
     * @param context       the context needed to create the NfcAdapter. (The activity)
     * @param mimeType      the mimetype for the application
     * @param activity      the activity that will be using this object
     * @param pendingIntent the pending intent that handles all nfc intents
     * @param intentFilters the filters needed for writing to a tag
     */
    public TagWriter(Context context, String mimeType, Activity activity, PendingIntent pendingIntent, IntentFilter[] intentFilters) {
        ndefExchangeFilters = intentFilters;
        this.activity = activity;
        nfcPendingIntent = pendingIntent;
        adapter = NfcAdapter.getDefaultAdapter(context);
        createMimeType(mimeType);
    }

    /**
     * Call to enable an exchange between NDEF devices during the activity with the message to be written.
     * Gives priority to the foreground activity when dispatching a discovered tag to an application.
     * <p/>
     * If this needs to be changed in the future, change what the message is, or list a different activity in
     * setNdefPushMessage.
     */
    public void enableNdefExchangeMode() {
        adapter.setNdefPushMessage(message, activity);
        adapter.enableForegroundDispatch(activity, nfcPendingIntent, ndefExchangeFilters, null);
    }

    /**
     * Call to disable any sort of exchange between NDEF devices.
     */
    public void disableNdefExchangeMode() {
//        passing a null value disables foreground NDEF push in the specified activity.
        adapter.setNdefPushMessage(null, activity);
        adapter.disableForegroundDispatch(activity);
    }

    /**
     * Gets the message from a text view and turns it into an NDEF Message and returns it as well as setting the message
     * field.
     *
     * @param view The textView that contains the text to be turned into a message.
     * @return Returns a new NdefMessage from the text.
     */
    public NdefMessage getTextViewAsNdef(TextView view) {
        byte[] textBytes = view.getText().toString().getBytes();
        NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeType.getBytes(),
                new byte[]{}, textBytes);
        message = new NdefMessage(new NdefRecord[]{
                textRecord
        });
        return message;
    }

    /**
     * a helper method to set a string from the payload of a single message.
     */
    public void setPayloadString() {
        payloadString = new String(message.getRecords()[0].getPayload());
    }

    /**
     * A helper method to get the string version of whatever the current ndef message is.
     *
     * @return payloadString the string version of the payload of message if it has been set.
     */
    public String getPayloadString() {
        return payloadString;
    }

    /**
     * A method to turn clear out the previous message to be pushed. This allows the NFC device to listen for incoming
     * ndef messages.
     */
    public void clearPushMessage() {
        adapter.setNdefPushMessage(null, activity);
    }

    /**
     * Gets the payload for a message from the array of NDEFmessages and then sets mPayload to that value and returns it.
     *
     * @return mPayload the payload for the ndefMessage in the first position in the messages array.
     */
    public byte[] getBytePayload() {
        mPayload = messages[0].getRecords()[0].getPayload();
        return mPayload;
    }

    /**
     * A getter for the current NdefMessage.
     *
     * @return message the current NdefMessage.
     */
    public NdefMessage getNdefMessage() {
        return message;
    }

    /**
     * A getter for the NdefMessages array.
     *
     * @return Returns messages which is an array of NdefMessages.
     */
    public NdefMessage[] getNdefMessages() {
        return messages;
    }

    /**
     * Gets the first message in the array of messages and sets the current message field to that value.
     *
     * @return message the current message stored in the WriteTag object.
     */
    public NdefMessage getMessageFromArray() {
        message = messages[0];
        return message;
    }

    /**
     * Gets the NdefMessage array from a tag when it's discovered or from an NDEF device. Then it sets the field messages to that array.
     *
     * @param intent The intent that contains the action that is hopefully either ACTION_TAG_DISCOVERED or ACTION_NDEF_DISCOVERED
     * @return messages Returns the messages array.
     */
    public NdefMessage[] getNdefMessages(Intent intent) {
        // Parse the intent
        NdefMessage[] msgs = null;
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
                messages = msgs;
            } else {
                // Unknown tag type
                byte[] empty = new byte[]{};
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
                NdefMessage msg = new NdefMessage(new NdefRecord[]{
                        record
                });
                msgs = new NdefMessage[]{
                        msg
                };
                messages = msgs;
            }
        } else {
            Log.d(TAG, "Unknown intent.");
            activity.finish();
        }
        return messages;
    }

    /**
     * a getter for a detected tag from an intent.
     *
     * @param intent the intent that contains the tag
     * @return the Tag that is in the intent.
     */
    public Tag getDetectedTag(Intent intent) {
        return intent.getParcelableExtra(adapter.EXTRA_TAG);
    }

    /**
     * A getter for the boolean writeMode.
     *
     * @return writeMode returns the boolean value that determines if it is currently in writeMode according to the boolean.
     */
    public boolean isWriteMode() {
        return writeMode;
    }

    /**
     * A method that determines if there's an NDEF action discovered from an intent.
     *
     * @param intent the intent that might contain the NDEF Action.
     * @return true if there exists an NDEF action in the intent.
     */
    public boolean isNdefDiscovered(Intent intent) {
        return adapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction());
    }

    /**
     * A method that determines if there's an NFC tag discovered from an intent.
     *
     * @param intent the intent that might contain a Tag Discovered action
     * @return true if the intent contains a Tag Discovered action.
     */
    public boolean isTagDiscovered(Intent intent) {
        return adapter.ACTION_TAG_DISCOVERED.equals(intent.getAction());
    }

    /**
     * Enables the tag writing mode so that the application knows that it is ready to write something to a tag.
     */
    public void enableTagWriteMode() {
        writeMode = true;
        IntentFilter tagDetected = new IntentFilter(adapter.ACTION_TAG_DISCOVERED);
        writeTagFilters = new IntentFilter[]{
                tagDetected
        };
        adapter.enableForegroundDispatch(activity, nfcPendingIntent, writeTagFilters, null);
    }

    /**
     * Disables the tag writing mode so that there is no confusion for the application as to whether it's about to write
     * to a tag or not.
     */
    public void disableTagWriteMode() {
        writeMode = false;
        adapter.disableForegroundDispatch(activity);
    }

    /**
     * a method to create a MimeType that the application will use when writing to a tag so that it knows which
     * application / activity to bring up when the tag is touched.
     *
     * @param name the name of the application that will be tacked on to the mimeType.
     */
    public void createMimeType(String name) {
        mimeType = "application/" + name;
    }

    /**
     * A method that will create an NDEF message from a string and set the current message to that value.
     *
     * @param str The String that will be used to create an NDEF message.
     */
    public void setMessage(String str) {
        byte[] textBytes = str.getBytes();
        NdefRecord stringRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeType.getBytes(),
                new byte[]{}, textBytes);
        message = new NdefMessage(new NdefRecord[]{
                stringRecord
        });
    }

    /**
     * A method to create an NDEF message from a byte array and sets the current message field to that value.
     *
     * @param bytes the byte array that will be used to create the NDEF message.
     */
    public void setMessage(byte[] bytes) {
        NdefRecord byteRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeType.getBytes(), new byte[]{}, bytes);
        message = new NdefMessage(new NdefRecord[]{
                byteRecord
        });
    }

    /**
     * A big method that writes an NDEF message to a tag or displays a toast message as to why it failed.
     *
     * @param aMessage the message to be written to the tag.
     * @param tag      the tag that the message is being written to.
     * @return True if it succeeded in writing the message to the tag. False otherwise.
     */
    public boolean writeToTag(NdefMessage aMessage, Tag tag) {
        if (aMessage != null) {
            message = aMessage;
        }

        int size = message.toByteArray().length;

        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();

                if (!ndef.isWritable()) {
                    toast("Tag is read only");
                    return false;
                }
                if (ndef.getMaxSize() < size) {
                    toast("Tag capacity is " + ndef.getMaxSize() + " bytes, message is " + size + " bytes.");
                    return false;
                }

                ndef.writeNdefMessage(message);
                toast("Wrote message to pre-formatted tag.");
                return true;
            } else {
                NdefFormatable format = NdefFormatable.get(tag);
                if (format != null) {
                    try {
                        format.connect();
                        format.format(message);
                        toast("Formatted tag and wrote message");
                        return true;
                    } catch (IOException e) {
                        toast("Failed to format tag");
                        return false;
                    }
                } else {
                    toast("Tag doesn't support NDEF.");
                    return false;
                }
            }
        } catch (Exception e) {
            toast("Failed to write tag");
            return false;
        }
    }


    private void toast(String text) {
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
    }

}