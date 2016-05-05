package com.ucas.xiaol.llim;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMContactListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaol on 2016/4/27.
 */
public class ListPanelActivity extends AppCompatActivity{

    //Friend id edit text
    private EditText lFrdIdEdit;
    //reason edit text
    private EditText lReason;
    //Add Friend Button
    private ImageButton lAddFrdBtn;
    //Sign out
    private ImageButton lSignOutBtn;
    //Friend list layout
    //private LinearLayout lLinearLayout;
    //Friend list
    private List<String> lFrdname;
    //friend list view
    private ListView lFrdListView;
    //send ids into adapter
    //int[] id={R.id.friend_layout,R.id.friend_name};
    //adapt data for ListView
    ListPanelAdapter adapter;
    //contact listener
    MyContactListener contactListener = new MyContactListener();


    private class MyContactListener implements EMContactListener {
        @Override
        public void onContactAdded(final String s) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ListPanelActivity.this, "You have made friends with " + s, Toast.LENGTH_LONG).show();
                    Log.d("Frds","onContactAdded");
                    addNewFrdBtn(s);
                }
            });
        }

        @Override
        public void onContactDeleted(String s) {
            Toast.makeText(ListPanelActivity.this, s + " deleted your Contact.", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onContactInvited(final String s, final String s1) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ListPanelActivity.this)
                            //Add view here if need (P148)
                            /*
                            * */
                            .setTitle("A new Contact invite detected.")
                            .setMessage(s + '\n' + s1);
                    setAgreeButton(builder, s);
                    setRefuseButton(builder, s)
                            .create()
                            .show();
                }
            });

            //getFriendList();
        }

        @Override
        public void onContactAgreed(String s) {
            Toast.makeText(ListPanelActivity.this, s + " agreed your Contact invite.", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onContactRefused(String s) {
            Toast.makeText(ListPanelActivity.this, s + " refused your Contact invite.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listpanel);

        //if it hasn't been signed in
        if (!EMClient.getInstance().isLoggedInBefore()){
            Intent intent = new Intent(ListPanelActivity.this,SignInActivity.class);
            startActivity(intent);
            finish();
        }

        Log.d("EXEORDER","ON CREATE1");
        EMClient.getInstance().contactManager().setContactListener(contactListener);
        Log.d("EXEORDER","ON CREATE2");
        initView();
    }

    @Override
    protected void onStart(){
        super.onStart();

        Log.d("EXEORDER","ON START-");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        EMClient.getInstance().contactManager().removeContactListener(contactListener);
    }

    private void initView() {
        lFrdIdEdit = (EditText) findViewById(R.id.edit_frd_id);
        lReason = (EditText) findViewById(R.id.edit_reason);

        lAddFrdBtn = (ImageButton) findViewById(R.id.btn_add);
        lAddFrdBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                addFriend();
            }
        });

        lSignOutBtn = (ImageButton) findViewById(R.id.btn_sign_out);
        lSignOutBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        lFrdListView = (ListView)findViewById(R.id.account_listView);
        lFrdListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (lFrdname.isEmpty() || lFrdname.size() < position){
                    Log.d("Err","size: " + lFrdname.size() + "   position: " + position);
                    Toast.makeText(ListPanelActivity.this,"Can't lunch a new chat!",Toast.LENGTH_LONG).show();
                    return ;
                }
                String chatID = lFrdname.get(position).trim();
                Log.d("chat","Target: " + chatID);
                if (!TextUtils.isEmpty(chatID)){
                    String curUsername = EMClient.getInstance().getCurrentUser();
                    if (chatID.equals(curUsername)){
                        Toast.makeText(ListPanelActivity.this,"Choose another people.",Toast.LENGTH_LONG).show();
                        return;
                    }

                    //jump to the chat activity
                    Intent intent = new Intent(ListPanelActivity.this,ChatActivity.class);
                    intent.putExtra("chat_id",chatID);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(ListPanelActivity.this,"Empty ID!",Toast.LENGTH_LONG).show();
                }
            }
        });
        getFriendList();
    }

    private void addNewFrdBtn(String Frdname){
        if (lFrdListView.getCount()>0)
            adapter.addView(Frdname);
        else {
            lFrdname.add(Frdname);
            adapter = new ListPanelAdapter(getApplicationContext(),lFrdname);
            lFrdListView.setAdapter(adapter);
        }
        /*final Button bn = new Button(ListPanelActivity.this);
        bn.setText(Frdname);
        //set the view of button if need (P62)
        /*
        *
        lLinearLayout.addView(bn);
        bn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String chatID = bn.getText().toString().trim();
                Log.d("chatBtn",chatID);
                if (!TextUtils.isEmpty(chatID)){
                    String curUsername = EMClient.getInstance().getCurrentUser();
                    if (chatID.equals(curUsername)){
                        Toast.makeText(ListPanelActivity.this,"Choose another people.",Toast.LENGTH_LONG).show();
                        return;
                    }

                    //jump to the chat activity
                    Intent intent = new Intent(ListPanelActivity.this,ChatActivity.class);
                    intent.putExtra("chat_id",chatID);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(ListPanelActivity.this,"Empty ID!",Toast.LENGTH_LONG).show();
                }
            }
        }); */
        //endregion
    }

    private void getFriendList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("EXEORDER","GETfriendlist");
                    lFrdname=new ArrayList<String>();
                    lFrdname = EMClient.getInstance().contactManager().getAllContactsFromServer();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //lLinearLayout.removeAllViews();list_frdName.clear();
                            //String[] lsFrdname = new String[lFrdname.size()];
                            //lFrdname.toArray(lsFrdname);
                            /*
                            for (int i=0;i<lsFrdname.length;i++){
                                //addNewFrdBtn(lsFrdname[i]);
                                list_frdName.add(lsFrdname[i]);
                            }*/
                            if (!lFrdname.isEmpty())
                            {
                                adapter=new ListPanelAdapter(getApplicationContext(),lFrdname);
                                lFrdListView.setAdapter(adapter);
                            }
                        }
                    });

                } catch (HyphenateException e) {
                    e.printStackTrace();
                    //Toast.makeText(ListPanelActivity.this,"Fail to get the friend list!",Toast.LENGTH_LONG).show();
                    //int errorCode = e.getErrorCode();
                    //Toast.makeText(ListPanelActivity.this,errorCode,Toast.LENGTH_LONG).show();
                }
            }
        }).start();
    }

    private AlertDialog.Builder setAgreeButton(AlertDialog.Builder builder, final String username){
        return builder.setPositiveButton("Agree", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            EMClient.getInstance().contactManager().acceptInvitation(username);
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }

    private AlertDialog.Builder setRefuseButton(AlertDialog.Builder builder, final String username){
        return builder.setNegativeButton("Refuse", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    EMClient.getInstance().contactManager().declineInvitation(username);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void addFriend() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String frdID = lFrdIdEdit.getText().toString().trim();
                String reason = lReason.getText().toString().trim();
                try {
                    EMClient.getInstance().contactManager().addContact(frdID,reason);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    Log.d("Frds","AddFailed");
                    return;
                }
                //Toast.makeText(ListPanelActivity.this,"Send invite Successfully.",Toast.LENGTH_LONG).show();
            }
        }).start();
    }

    private void signOut() {
        EMClient.getInstance().logout(false, new EMCallBack() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ListPanelActivity.this,"Sign out Successfully!",Toast.LENGTH_LONG).show();
                        //jump to the sign in activity
                        Intent intent = new Intent(ListPanelActivity.this,SignInActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(ListPanelActivity.this,"Something wrong happened!",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }
 }
