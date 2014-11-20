package oak.demo;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

import oak.app.CryptoSharedPreferences;


public final class EncryptedSharedPreferences extends CryptoSharedPreferences {

    public EncryptedSharedPreferences(Context context, SharedPreferences delegate) {
        super(context, delegate);
    }

    /**
     * This should be replaced with a user input pass phrase or an externally
     * retrieved pass phrase if possible.
     *
     * @return
     */
    @Override
    protected char[] getSpecialCode() {
        return "y0urPa$$w0rdH3r3".toCharArray();
    }

    @Override
    public Set<String> getStringSet(String s, Set<String> strings) {
        return null;
    }
}
