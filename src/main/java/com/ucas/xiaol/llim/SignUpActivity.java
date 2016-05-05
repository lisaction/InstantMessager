package com.ucas.xiaol.llim;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ButtonBarLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

/**
 * Created by xiaol on 2016/4/26.
 */
public class SignUpActivity extends AppCompatActivity {

    //pop-up box
    private ProgressDialog sDialog;

    //username text
    private EditText sUsernameEdit;

    //password1 text
    private EditText sPasswordEdit1;

    //password2 text
    private EditText sPasswordEdit2;

    //sign up button
    private Button sSignUpBtn;

    //cancel button
    private Button sCancelBtn;

    //Edit Text content;
    String username;
    String password1;
    String password2;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initView();
    }

    private void initView() {
        sUsernameEdit = (EditText) findViewById(R.id.edit_s_username);
        sPasswordEdit1 = (EditText) findViewById(R.id.edit_s_password1);
        sPasswordEdit2 = (EditText) findViewById(R.id.edit_s_password2);

        sSignUpBtn = (Button) findViewById(R.id.btn_s_sign_up);
        sSignUpBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                password1 = sPasswordEdit1.getText().toString().trim();
                password2 = sPasswordEdit2.getText().toString().trim();

                if (password1.equals(password2))
                    signUp();
                else
                    Toast.makeText(SignUpActivity.this,"Password varied. Input again.",Toast.LENGTH_LONG).show();
            }
        });

        sCancelBtn = (Button) findViewById(R.id.btn_s_cancel);
        sCancelBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                cancel();
            }
        });
    }

    private void signUp() {
        //a Dialog to indicate the process
        sDialog = new ProgressDialog(this);
        sDialog.setMessage("Sign Up...");
        sDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    username = sUsernameEdit.getText().toString().trim();
                    EMClient.getInstance().createAccount(username,password1);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!SignUpActivity.this.isFinishing()){
                                sDialog.dismiss();
                            }
                            Toast.makeText(SignUpActivity.this,"Sign Up Successfully!",Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    sDialog.dismiss();
                    Toast.makeText(SignUpActivity.this,"Fail to sign up.",Toast.LENGTH_LONG).show();
                }
            }
        }).start();
    }

    private void cancel() {
        finish();
    }
}
