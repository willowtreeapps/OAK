package ${package};

import android.content.Context;
import android.content.SharedPreferences;

import oak.ObscuredSharedPreferences;

/**
 * Generated from archetype
 */

public final class EncryptedSharedPreferences extends ObscuredSharedPreferences{

    public EncryptedSharedPreferences(Context context, SharedPreferences delegate) {
        super(context, delegate);
    }

    @Override
    protected char[] getSpecialCode() {
        return "y0urPa$$w0rdH3r3".toCharArray();
    }
}
