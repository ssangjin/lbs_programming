package com.lbs.programming.lbs_2_0;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import android.util.Base64;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    final String key = "key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        EditText input = findViewById(R.id.inputEditText);
        EditText output = findViewById(R.id.outputEditText);

        switch (view.getId()) {
            case R.id.buttonEncode:
                output.setText(encode(input.getText().toString(), key));
                input.setText("");
                break;
            case R.id.buttonDecode:
                input.setText(decode(output.getText().toString(), key));
                output.setText("");
                break;
            case R.id.buttonHash:
                // TODO: show hash code.
                int hash = input.getText().hashCode();
                output.setText(Integer.toString(hash));
                break;
        }
    }

    private String encode(String originalText, String key) {

        try {
            Cipher cipher = getCipher();
            byte[] keyBytes = getKeyBytes(key.getBytes());

            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

            try {
                cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }


            byte[] encrypted = cipher.doFinal(originalText.getBytes());

            return Base64.encodeToString(encrypted, Base64.DEFAULT);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private Cipher getCipher() throws NoSuchAlgorithmException, NoSuchPaddingException {
        Cipher cipher;
        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        return cipher;
    }

    private byte[] getKeyBytes(byte[] bytes) {
        byte[] keyBytes = new byte[16];
        byte[] bkey = bytes;

        int length = bkey.length;
        if (length > keyBytes.length) {
            length = keyBytes.length;
        }

        System.arraycopy(bkey, 0, keyBytes, 0, length);
        return keyBytes;
    }

    private String decode(String encryptedText, String key) {
        try {
            byte[] keyBytes = getKeyBytes(key.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = getCipher();
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);

            byte[] original = cipher.doFinal(Base64.decode(encryptedText, Base64.DEFAULT));
            return new String(original);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
