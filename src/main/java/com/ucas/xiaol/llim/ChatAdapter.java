package com.ucas.xiaol.llim;

import android.content.Context;
import android.telecom.TelecomManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by lilith on 2016/4/30.
 */
public class ChatAdapter extends BaseAdapter {
    private List<String[]> lChat;
    private Context context;
    private LayoutInflater inflater;
    private int id_tv;

    public ChatAdapter(Context context, List<String[]> lChat, int id_tv) {
        this.context = context;
        this.lChat = lChat;
        this.id_tv = id_tv;
    }

    @Override
    public int getCount() {
        if (lChat == null || lChat.isEmpty())
            return 0;
        else return lChat.size();
    }

    @Override
    public Object getItem(int position) {
        return lChat.get(position);//no practical meaning in this adapter
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        for (int i=0;i<position;i++){
            Log.d("chatmsg", "getView: " + lChat.get(i)[0] + ": " + lChat.get(i)[1]);
        }
        if (convertView == null) {
            inflater = LayoutInflater.from(context);
            if (lChat.get(position)[0] == "send") {
                convertView = inflater.inflate(R.layout.layout_message_send, null);
                viewHolder = new ViewHolder(convertView);
            } else { //else if (lChat.get(position)[0]=="receive") {
                convertView = inflater.inflate(R.layout.layout_message_recieve, null);
                viewHolder = new ViewHolder(convertView);
            }

            //timestamp can be added below
            convertView.setTag(viewHolder);
        } else viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.tv_msg.setText(lChat.get(position)[1]);
        return convertView;
    }

    public void addView(String[] new_msg) {
        Log.d("chatmsg",new_msg[0] + ": " + new_msg[1]);

        lChat.add(new_msg);
        notifyDataSetChanged();
    }

    class ViewHolder {
        public TextView tv_msg = null;

        public ViewHolder(View convertView) {
            tv_msg = (TextView) convertView.findViewById(id_tv);
        }
    }
}
