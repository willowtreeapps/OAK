package oak;

import android.content.Context;
import android.content.SharedPreferences;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Map;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by robcook on 4/2/14.
 */

/**
 * Warning, this gives a false sense of security.  If an attacker has enough access to acquire your
 * password store, then he almost certainly has enough access to acquire your source binary and
 * figure out your encryption key.  However, it will prevent casual investigators from acquiring
 * passwords, and thereby may prevent undesired negative publicity.
 *
 */

/**
 * This code originally posted by Michael Burton on StackOverflow
 * http://stackoverflow.com/questions/785973/what-is-the-most-appropriate-way-to-store-user-settings-in-android-application/6393502#6393502
 *
 * This class was created to replace the original ObscurredSharedPreferences.  It includes DES and AES
 * encryption options, and uses a randomly generate initialization vector to ensure each encrypted string
 * is unique.  If no crypto type is specified the default is AES.
 */
public abstract class CryptoSharedPreferences implements SharedPreferences {

    private static final String CRYPTO_TYPE_KEY = "CryptoSharedPrefs_Type_Key";
    protected static final String UTF8 = "utf-8";
    protected static final int SECRET_KEY_ITERATIONS = 100;
    protected static final int IV_LENGTH = 16;
    protected static final String RANDOM_ALGORITHM = "SHA1PRNG";

    // AES
    protected static final String CIPHER_ALGORITHM_AES = "AES/CBC/PKCS5Padding";
    protected static final String PBE_ALGORITHM_AES = "PBKDF2WithHmacSHA1";
    private static final String SECRET_KEY_ALGORITHM_AES = "AES";

    // DES
    protected static final String CIPHER_ALGORITHM_DES = "PBEWithMD5AndDES";
    protected static final String PBE_ALGORITHM_DES = "PBEWithMD5AndDES";

    protected SharedPreferences delegate;
    protected Context context;
    private int cryptoToUse;

    public static final int CRYPTO_AES = 0x0000;
    public static final int CRYPTO_DES = 0x0001;

    /**
     * A salt is required to ensure the AES key is the correct length.  It
     * is not necessary to have a random salt because we're using an initialization
     * vector to ensure the encrypted data is effectively random each time it is
     * generated.
     */
    private static byte[] SALT = new byte[] { (byte)0x162, 0x48, (byte)0x1c9, (byte)0x2d8, (byte)0x283,
            (byte)0xc8, (byte)0xeb, (byte)0x148, (byte)0x1bb, (byte)0x2c7,
            (byte)0x246, (byte)0x114, (byte)0x2e0, (byte)0x140, (byte)0x1b8,
            (byte)0x114, (byte)0xbd, (byte)0x321, (byte)0x1d7, (byte)0x1dd};

    public CryptoSharedPreferences(Context context, SharedPreferences delegate) {
        this.delegate = delegate;
        this.context = context;

        initCryptoType();
    }

    /**
     * AES is preferred but there is some indication AES is not available on
     * all phones (http://www.unwesen.de/2011/06/12/encryption-on-android-bouncycastle/).
     * To mitigate this the phone is checked for supporting the AES encryption
     * standard and either AES or DES is saved in shared preferences.  This
     * ensures the same type of encryption is used even if the phone gains
     * AES support from an upgrade.
     */
    private void initCryptoType() {
        int cryptoType = delegate.getInt(CRYPTO_TYPE_KEY, -1);

        if (cryptoType == CRYPTO_AES || cryptoType == CRYPTO_DES) {
            this.cryptoToUse = cryptoType;
            return;
        }

        this.cryptoToUse = CRYPTO_AES;
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_AES);
        } catch (NoSuchAlgorithmException ae) {
            ae.printStackTrace();
            this.cryptoToUse = CRYPTO_DES;
        } catch (NoSuchPaddingException pe) {
            pe.printStackTrace();
            this.cryptoToUse = CRYPTO_DES;
        }
        delegate.edit().putInt(CRYPTO_TYPE_KEY, this.cryptoToUse).commit();
    }

    /**
     * Implement this method to supply your char array with your password.
     * Ideally this is from user input or an external api and not a hard-coded
     * string.
     *
     * @return
     */
    protected abstract char[] getSpecialCode();

    public class Editor implements SharedPreferences.Editor {

        protected SharedPreferences.Editor delegate;

        public Editor() {
            this.delegate = CryptoSharedPreferences.this.delegate.edit();
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
            SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        delegate.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(
            SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        delegate.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    protected String encrypt(String value) {

        if (CRYPTO_DES == cryptoToUse) {
            return encrypt_DES(value);
        }
        return encrypt_AES(value);
    }

    protected String decrypt(String value) {

        if (CRYPTO_DES == cryptoToUse) {
            return decrypt_DES(value);
        }
        return decrypt_AES(value);
    }

    protected String encrypt_AES(String value) {

        try {
            final byte[] bytes = value != null ? value.getBytes(UTF8) : new byte[0];

            byte[] ivBytes = getInitVector();
            final IvParameterSpec iv = new IvParameterSpec(ivBytes);

            final SecretKey symKey = getSecretKey_AES(SALT);

            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_AES);

            cipher.init(Cipher.ENCRYPT_MODE, symKey, iv);

            byte[] encryptedBytes = cipher.doFinal(bytes);
            byte[] encryptedAndIv = new byte[encryptedBytes.length + ivBytes.length];
            System.arraycopy(encryptedBytes, 0, encryptedAndIv, 0, encryptedBytes.length);
            System.arraycopy(ivBytes, 0, encryptedAndIv, encryptedBytes.length, ivBytes.length);
            return new String(Base64.encode(encryptedAndIv, Base64.NO_WRAP), UTF8);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    protected String decrypt_AES(String value) {
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

            final IvParameterSpec iv = new IvParameterSpec(ivBytes);

            final SecretKey symKey = getSecretKey_AES(SALT);

            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_AES);

            cipher.init(Cipher.DECRYPT_MODE, symKey, iv);

            return new String(cipher.doFinal(encryptedBytes), UTF8);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected String encrypt_DES(String value) {

        try {
            final byte[] bytes = value != null ? value.getBytes(UTF8) : new byte[0];

            byte[] ivBytes = getInitVector();
            PBEParameterSpec iv = new PBEParameterSpec(ivBytes, 20);

            SecretKey key = getSecretKey_DES();
            Cipher pbeCipher = Cipher.getInstance(CIPHER_ALGORITHM_DES);

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

    protected String decrypt_DES(String value) {
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

            SecretKey key = getSecretKey_DES();
            Cipher pbeCipher = Cipher.getInstance(CIPHER_ALGORITHM_DES);

            pbeCipher.init(Cipher.DECRYPT_MODE, key, iv);
            return new String(pbeCipher.doFinal(encryptedBytes), UTF8);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] getInitVector() throws NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstance(RANDOM_ALGORITHM);
        byte[] iv = new byte[IV_LENGTH];
        random.nextBytes(iv);
        return iv;
    }


    private SecretKey getSecretKey_DES() throws NoSuchAlgorithmException,
            InvalidKeySpecException
    {
        PBEKeySpec keySpec = new PBEKeySpec(getSpecialCode());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(PBE_ALGORITHM_DES);
        SecretKey key = keyFactory.generateSecret(keySpec);
        return key;
    }

    private SecretKey getSecretKey_AES(byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(PBE_ALGORITHM_AES);
        KeySpec spec = new PBEKeySpec(getSpecialCode(), salt, SECRET_KEY_ITERATIONS, 256);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey secret = new SecretKeySpec(tmp.getEncoded(), SECRET_KEY_ALGORITHM_AES);
        return secret;
    }

    private byte[] copyOfRange(byte[] from, int start, int end) {
        int length = end - start;
        byte[] result = new byte[length];
        System.arraycopy(from, start, result, 0, length);
        return result;
    }
}
