package com.ucas.xiaol.llim;

import android.app.Application;

import com.hyphenate.chat.EMChatManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMEncryptUtils;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chat.adapter.EMABase;
import com.hyphenate.chat.adapter.EMAChatManager;
import com.hyphenate.chat.adapter.EMAEncryptProviderInterface;
import com.hyphenate.util.EasyUtils;

/**
 * Created by xiaol on 2016/4/28.
 */
public class CustomApplication extends Application{
    @Override
    public void onCreate(){
        super.onCreate();

        EMOptions options = new EMOptions();
        //Get invitation inform from friends
        options.setAcceptInvitationAlways(false);
        //Auto Login is close
        options.setAutoLogin(false);

        //init EM
        EMClient.getInstance().init(getApplicationContext(),options);
        //enable debug
        EMClient.getInstance().setDebugMode(true);
        //EMEncryptUtils.decryptFile();
        //EMAEncryptProviderInterface

    }

    public class Encrypt extends EMAEncryptProviderInterface{

    }

}
