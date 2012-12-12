package com.djforest.lapinlink.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Base64;
import android.util.Log;

public class BlowFishChiper implements Runnable {
    private final String LOG_TAG = "com.djforest.lapinlink.util.AesChiper";
    private final String ALGORITHM = "Blowfish";
    private final String CHIPER_TYPE = "Blowfish/CBC/PKCS5Padding"; // 受け渡す文字列は アルゴリズム/モード/パディング の形式で
    private final String mCryptSeed;
    private AudioTrack mAudioTrack;
    private Thread mThread;
    private byte[] mByteData;

    public BlowFishChiper(String _CryptSeed) {
        this.mCryptSeed = _CryptSeed;
    }
    /**
     * 文字列を暗号化し、Byteを返します。
     * 
     * @param plainText(String)
     * @return byte[]
     * @throws Exception
     */
    public byte[] encryptToBytes(String plainText) throws Exception {
        byte[] rawKey = getRawKey(this.mCryptSeed.getBytes());
        byte[] encrypted = encrypt(rawKey, plainText.getBytes());
        return encrypted;
    }

    /**
     * 文字列を暗号化し、Stringを返します。
     * 
     * @param plainText(String)
     * @return String
     * @throws Exception
     */
    public String encryptToString(String plainText) throws Exception {
        byte[] rawKey = getRawKey(this.mCryptSeed.getBytes());
        byte[] encrypted = encrypt(rawKey, plainText.getBytes());
        return Base64.encodeToString(encrypted, Base64.DEFAULT);
    }

    /**
     * Bitmapイメージを暗号化します。
     * 
     * @param bitmap(Bitmap)
     * @return byte[]
     * @throws Exception
     */
    public byte[] encryptToBytes(Bitmap bitmap) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, 100, baos);
        byte[] bytesImage = baos.toByteArray();
        byte[] rawKey = getRawKey(this.mCryptSeed.getBytes());
        byte[] encrypted = null;
        try {
            encrypted = encrypt(rawKey, bytesImage);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "message", e);
        }
        return encrypted;
    }

    /**
     * Fileを暗号化し、Byteを返します。
     * 
     * @param file(File)
     * @return byte[]
     * @throws Exception
     */
    public byte[] encryptToBytes(File file) throws Exception {
        byte[] rawKey = getRawKey(this.mCryptSeed.getBytes());
        byte [] byteArray = new byte[(int) file.length()];
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            in.read(byteArray);
            in.close();
        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, "message", e);
        } catch (IOException e) {
            Log.e(LOG_TAG, "message", e);
        }
        byte[] encrypted = encrypt(rawKey, byteArray);
        return encrypted;
    }
    
    
    /**
     * 暗号化された文字列を複合化ます。
     * 
     * @param encryptedText(String)
     * @return String
     * @throws Exception
     */
    public String decryptToString(String encryptedText) throws Exception {
        byte[] rawKey = getRawKey(this.mCryptSeed.getBytes());
        byte[] enc = Base64.decode(encryptedText.getBytes(), Base64.DEFAULT);
        byte[] decrypted = decrypt(rawKey, enc);
        return new String(decrypted);
    }

    /**
     * 暗号化されたbyte配列を複合化ます。
     * 
     * @param encryptedByte(byte[])
     * @return String
     * @throws Exception
     */
    public String decryptToString(byte[] encryptedBytes) throws Exception {
        byte[] rawKey = getRawKey(this.mCryptSeed.getBytes());
        byte[] decrypted = decrypt(rawKey, encryptedBytes);
        return new String(decrypted);
    }


    /**
     * 暗号化された文字列を複合化し、Bitmapを返します。
     * 
     * @param encryptedText(String)
     * @return Bitmap
     * @throws Exception
     */
    public Bitmap decryptToBitmap(String encryptedText) throws Exception {
        byte[] rawKey = getRawKey(this.mCryptSeed.getBytes());
        byte[] enc = Base64.decode(encryptedText.getBytes(), Base64.DEFAULT);
        byte[] result = decrypt(rawKey, enc);
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap decrypted = null;
        try {
            decrypted = BitmapFactory.decodeByteArray(result, 0, result.length, options);
        } catch (Exception e) {
            Log.e(LOG_TAG, "message", e);
        }
        return decrypted;
    }

    /**
     * 暗号化されたbyte配列を複合化ます。
     * 
     * @param encryptedByte(byte[])
     * @return byte[]
     * @throws Exception
     */
    public byte[] decryptToBytes(byte[] encryptedBytes) throws Exception {
        byte[] rawKey = getRawKey(this.mCryptSeed.getBytes());
        byte[] decrypted = decrypt(rawKey, encryptedBytes);
        return decrypted;
    }


    /**
     * 暗号化されたbyte配列を複合化し、Bitmapを返します。
     * 
     * @param encrypted
     * @return Bitamp
     * @throws Exception
     */
    public Bitmap decryptToBitmap(byte[] encryptedBytes) throws Exception {
        byte[] rawKey = getRawKey(this.mCryptSeed.getBytes());
        byte[] result = decrypt(rawKey, encryptedBytes);
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap decrypted = null;
        try {
            decrypted = BitmapFactory.decodeByteArray(result, 0, result.length, options);
        } catch (Exception e) {
            Log.e(LOG_TAG, "message", e);
        }
        return decrypted;
    }

    /**
     * Fileを複合化し、Byteを返します。
     * 
     * @param file(File)
     * @return byte[]
     * @throws Exception
     */
    public byte[] decryptToBytes(File file) throws Exception {
        byte[] rawKey = getRawKey(this.mCryptSeed.getBytes());
        byte [] encryptedByteArray = new byte[(int) file.length()];
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            in.read(encryptedByteArray);
            in.close();
        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, "message", e);
        } catch (IOException e) {
            Log.e(LOG_TAG, "message", e);
        }
        byte[] decrypted = decrypt(rawKey, encryptedByteArray);
        return decrypted;
    }

    /**
     * Fileを複合化し、Bitmapを返します。
     * 
     * @param file(File)
     * @return Bitmap
     * @throws Exception
     */
    public Bitmap decryptToBitmap(File file) throws Exception {
        byte[] rawKey = getRawKey(this.mCryptSeed.getBytes());
        byte [] encryptedByteArray = new byte[(int) file.length()];
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            in.read(encryptedByteArray);
            in.close();
        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, "message", e);
        } catch (IOException e) {
            Log.e(LOG_TAG, "message", e);
        }
        byte[] decrypted = decrypt(rawKey, encryptedByteArray);
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeByteArray(decrypted, 0, decrypted.length, options);
        } catch (Exception e) {
            Log.e(LOG_TAG, "message", e);
        }
        return bitmap;
    }

    
    /**
     * 暗号化されたオーディオファイルを再生します。データはlpcm形式(wav,pcm)、サンプリングレート44100
     * であること。MONO/16bitで再生します。
     * 
     * @param plainText(String)
     * @return byte[]
     * @throws Exception
     */
    public void playAudioEncryptedFile(File file) throws Exception {
        byte[] rawKey = getRawKey(this.mCryptSeed.getBytes());
        byte [] encryptedByteArray = new byte[(int) file.length()];
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            in.read(encryptedByteArray);
            in.close();
        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, "message", e);
        } catch (IOException e) {
            Log.e(LOG_TAG, "message", e);
        }
        mByteData = decrypt(rawKey, encryptedByteArray);
        // 再生用初期化処理
        initializeAudio();
        this.mThread.start();
    }

    // 再生用初期化処理
    private void initializeAudio() {
        // 必要となるバッファサイズを計算
        int bufSize = android.media.AudioTrack.getMinBufferSize(44100,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        // AudioTrackインスタンス作成
        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                44100, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT, bufSize,
                AudioTrack.MODE_STREAM);
        // スレッドインスタンス作成
        this.mThread = new Thread(this);
    }
    
    // 再生用スレッド処理
    public void run() {
        if (mAudioTrack != null) {
            mAudioTrack.play();
            mAudioTrack.write(mByteData, 0, mByteData.length);
            mAudioTrack.stop();
            mThread = null;
            initializeAudio();
        }
    }
    
    private byte[] getRawKey(byte[] seed) throws Exception {
        KeyGenerator keygen = KeyGenerator.getInstance(ALGORITHM);
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(seed);
        keygen.init(128, random); // 192 and 256 bits may not be available 
        SecretKey key = keygen.generateKey();
        byte[] raw = key.getEncoded();
        return raw;
    }

    private byte[] encrypt(byte[] raw, byte[] plain) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(raw, ALGORITHM);
        Cipher cipher = Cipher.getInstance(CHIPER_TYPE);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encrypted = cipher.doFinal(plain);
        return encrypted;
    }

    private byte[] decrypt(byte[] raw, byte[] encrypted)
            throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(raw, ALGORITHM);
        Cipher cipher = Cipher.getInstance(CHIPER_TYPE);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }
    
    public String getSeed(){
        return this.mCryptSeed;
    }
}
