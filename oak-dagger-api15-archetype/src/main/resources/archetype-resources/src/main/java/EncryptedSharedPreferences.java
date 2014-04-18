package ${package};

import android.content.Context;
import android.content.SharedPreferences;
import java.util.Set;

import oak.CryptoSharedPreferences;

/**
 * Generated from archetypezz
 */

public final class EncryptedSharedPreferences extends CryptoSharedPreferences {

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
