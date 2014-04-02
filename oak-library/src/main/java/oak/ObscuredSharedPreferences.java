package oak;

/**
 * User: mlake Date: 12/16/11 Time: 4:05 PM
 */


import android.content.Context;
import android.content.SharedPreferences;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 * Warning, this gives a false sense of security.  If an attacker has enough access to acquire your
 * password store, then he almost certainly has enough access to acquire your source binary and
 * figure out your encryption key.  However, it will prevent casual investigators from acquiring
 * passwords, and thereby may prevent undesired negative publicity.
 */

/**
 * This code originally posted by Michael Burton on StackOverflow
 * http://stackoverflow.com/questions/785973/what-is-the-most-appropriate-way-to-store-user-settings-in-android-application/6393502#6393502
 */


public abstract class ObscuredSharedPreferences implements SharedPreferences {

    protected static final String UTF8 = "utf-8";
    protected static final int SECRET_KEY_ITERATIONS = 20;
    protected static final int SALT_LENGTH = 20;
    protected static final int IV_LENGTH = 20;
    protected static final String RANDOM_ALGORITHM = "SHA1PRNG";

    // AES
//    protected static final String CIPHER_ALGORITHM = "AES/CBC/NoPadding";
//    protected static final String PBE_ALGORITHM = "PBEWithSHA256And256BitAES-CBC-BC";
//    private static final String SECRET_KEY_ALGORITHM = "AES";


    // DES
  protected static final String CIPHER_ALGORITHM = "PBEWithMD5AndDES";
  protected static final String PBE_ALGORITHM = "PBEWithMD5AndDES";
  private static final String SECRET_KEY_ALGORITHM = "AES";

    protected SharedPreferences delegate;
    protected Context context;

    public ObscuredSharedPreferences(Context context, SharedPreferences delegate) {
        this.delegate = delegate;
        this.context = context;
    }

    /**
     * Implement this method to supply your char array with your password
     *
     * @return
     */

    protected abstract char[] getSpecialCode();

    public class Editor implements SharedPreferences.Editor {

        protected SharedPreferences.Editor delegate;

        public Editor() {
            this.delegate = ObscuredSharedPreferences.this.delegate.edit();
        }

        @Override
        public Editor putBoolean(String key, boolean value) {
            String eValue = encrypt(Boolean.toString(value));

            delegate.putString(key, eValue);
            return this;
        }

        @Override
        public Editor putFloat(String key, float value) {
            delegate.putString(key, encrypt(Float.toString(value)));
            return this;
        }

        @Override
        public Editor putInt(String key, int value) {
            delegate.putString(key, encrypt(Integer.toString(value)));
            return this;
        }

        @Override
        public Editor putLong(String key, long value) {
            delegate.putString(key, encrypt(Long.toString(value)));
            return this;
        }

        @Override
        public Editor putString(String key, String value) {
            delegate.putString(key, encrypt(value));
            return this;
        }

//        @Override
        public SharedPreferences.Editor putStringSet(String s, Set<String> strings) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

//      commented out for 2.1 compatibility
//        @Override
//        public void apply() {
//            delegate.apply();
//        }

        @Override
        public Editor clear() {
            delegate.clear();
            return this;
        }

        @Override
        public boolean commit() {
            return delegate.commit();
        }

//        @Override
        public void apply() {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public Editor remove(String s) {
            delegate.remove(s);
            return this;
        }
    }

    public Editor edit() {
        return new Editor();
    }


    @Override
    public Map<String, ?> getAll() {
        throw new UnsupportedOperationException(); // left as an exercise to the reader
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        final String v = delegate.getString(key, null);
        return v != null ? Boolean.parseBoolean(decrypt(v)) : defValue;
    }

    @Override
    public float getFloat(String key, float defValue) {
        final String v = delegate.getString(key, null);
        return v != null ? Float.parseFloat(decrypt(v)) : defValue;
    }

    @Override
    public int getInt(String key, int defValue) {
        final String v = delegate.getString(key, null);
        return v != null ? Integer.parseInt(decrypt(v)) : defValue;
    }

    @Override
    public long getLong(String key, long defValue) {
        final String v = delegate.getString(key, null);
        return v != null ? Long.parseLong(decrypt(v)) : defValue;
    }

    @Override
    public String getString(String key, String defValue) {
        final String v = delegate.getString(key, null);
        return v != null ? decrypt(v) : defValue;
    }

    @Override
    public boolean contains(String s) {
        return delegate.contains(s);
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(
            OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        delegate.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(
            OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        delegate.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }


    protected String encrypt(String value) {

        try {
            final byte[] bytes = value != null ? value.getBytes(UTF8) : new byte[0];

            byte[] ivBytes = getInitVector();
            PBEParameterSpec iv = new PBEParameterSpec(ivBytes, 20);

            SecretKey key = getSecretKey();
            Cipher pbeCipher = Cipher.getInstance(CIPHER_ALGORITHM);

            pbeCipher.init(Cipher.ENCRYPT_MODE, key, iv);

            byte[] encryptedBytes = pbeCipher.doFinal(bytes);
            byte[] encryptedAndIv = new byte[encryptedBytes.length + ivBytes.length];
            System.arraycopy(encryptedBytes, 0, encryptedAndIv, 0, encryptedBytes.length);
            System.arraycopy(ivBytes, 0, encryptedAndIv, encryptedBytes.length, ivBytes.length);
            return new String(Base64.encode(encryptedAndIv, Base64.NO_WRAP), UTF8);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    protected String decrypt(String value) {
        try {
            final byte[] bytes = value != null ? Base64.decode(value, Base64.DEFAULT) : new byte[0];

            byte[] ivBytes;
            byte[] encryptedBytes;

            if (bytes.length > IV_LENGTH) {
                ivBytes = copyOfRange(bytes, bytes.length - IV_LENGTH, bytes.length);
                encryptedBytes = copyOfRange(bytes, 0, bytes.length - IV_LENGTH);
            } else {
                return "";
            }

            PBEParameterSpec iv = new PBEParameterSpec(ivBytes, 20);

            SecretKey key = getSecretKey();
            Cipher pbeCipher = Cipher.getInstance(CIPHER_ALGORITHM);

            pbeCipher.init(Cipher.DECRYPT_MODE, key, iv);
            return new String(pbeCipher.doFinal(encryptedBytes), UTF8);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstance(RANDOM_ALGORITHM);
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }

    private byte[] getInitVector() throws NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstance(RANDOM_ALGORITHM);
        byte[] iv = new byte[IV_LENGTH];
        random.nextBytes(iv);
        return iv;
    }


    private SecretKey getSecretKey() throws NoSuchAlgorithmException,
            InvalidKeySpecException
    {
        PBEKeySpec keySpec = new PBEKeySpec(getSpecialCode());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(PBE_ALGORITHM);
        SecretKey key = keyFactory.generateSecret(keySpec);
        return key;
    }

    private byte[] copyOfRange(byte[] from, int start, int end) {
        int length = end - start;
        byte[] result = new byte[length];
        System.arraycopy(from, start, result, 0, length);
        return result;
    }

}
