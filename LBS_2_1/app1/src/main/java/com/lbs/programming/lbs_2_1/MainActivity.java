package com.lbs.programming.lbs_2_1;

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
                break;
        }
    }

    private String encode(String originalText, String key) {
        // TODO: encode text
        return null;
    }

    private Cipher getCipher() throws NoSuchAlgorithmException, NoSuchPaddingException {
        // TODO: Cipher 가져오기
        return null;
    }

    private byte[] getKeyBytes(byte[] bytes) {
        // TODO: KEY의 byte array 가져오기
        return null;
    }

    private String decode(String encryptedText, String key) {
        // TODO: decode text.
        return null;
    }
}
