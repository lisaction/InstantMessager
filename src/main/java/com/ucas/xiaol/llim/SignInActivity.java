package com.ucas.xiaol.llim;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;

public class SignInActivity extends AppCompatActivity {

    //pop-up box
    private ProgressDialog mDialog;
    //username text
    private EditText mUsernameEdit;
    //passwd text
    private EditText mPasswordEdit;
    //reg button
    private Button mSignupBtn;
    //sign in button
    private Button mSignInBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init View
        initView();
    }

    private void initView(){
        mUsernameEdit = (EditText) findViewById(R.id.edit_username);
        mPasswordEdit = (EditText) findViewById(R.id.edit_password);

        mSignInBtn = (Button) findViewById(R.id.btn_sign_in);
        mSignInBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        mSignupBtn = (Button) findViewById(R.id.btn_sign_up);
        mSignupBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    //sign in method
    private void signIn(){
        //a Dialog to indicate the process
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Sign in...");
        mDialog.show();

        String username = mUsernameEdit.getText().toString().trim();
        String password = mPasswordEdit.getText().toString().trim();

        EMClient.getInstance().login(username, password, new EMCallBack() {

            //call back after successfully log on
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mDialog.dismiss();
                        Toast.makeText(SignInActivity.this,"Sign in successfully!",Toast.LENGTH_LONG).show();

                        //get the conversation form local
                        EMClient.getInstance().chatManager().loadAllConversations();

                        //jump to the list panel activity and move out this activity
                        Intent intent = new Intent(SignInActivity.this, ListPanelActivity.class);
                        startActivity(intent);
                        finish();

                    }
                });
            }

            @Override
            public void onProgress(int i, String s) {

            }

            //call back if fail
            @Override
            public void onError(int i, String s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mDialog.dismiss();
                        Toast.makeText(SignInActivity.this,"Fail to sign in",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    //sign up method (zhuce)
    private void signUp(){
        //skip to the signUp activity
        Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

}
