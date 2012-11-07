package oak.demo;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

import oak.ObscuredSharedPreferences;

public final class EncryptedSharedPreferences extends ObscuredSharedPreferences {

    public EncryptedSharedPreferences(Context context, SharedPreferences delegate) {
        super(context, delegate);
    }

    @Override
    protected char[] getSpecialCode() {
        return "y0urPa$$w0rdH3r3".toCharArray();
    }

    @Override
    public Set<String> getStringSet(String s, Set<String> strings) {
        return null;
    }
}
