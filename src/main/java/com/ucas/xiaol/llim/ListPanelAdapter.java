package com.ucas.xiaol.llim;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;

import java.util.Date;
import java.util.List;

/**
 * Created by lilith on 2016/4/29.
 */
public class ListPanelAdapter extends BaseAdapter {
    private Context context;
    private List<String> friend_list;

    private LayoutInflater inflater;
    //private int[] id;

    public ListPanelAdapter (Context context, List<String>friend_list){
        this.context = context;
        this.friend_list = friend_list;
        //this.id=id;
        //inflater=(LayoutInflater)getSystemService(context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        if (friend_list.isEmpty())
            return 0;
        else
            return this.friend_list.size();
    }

    @Override
    public Object getItem(int position) {
        return this.friend_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // A ViewHolder keeps references to children views to avoid unneccessary calls
        // to findViewById() on each row.
        ViewHolder viewHolder;

        // convertView: The old view to reuse, if possible.
        // When convertView is not null, we can reuse it directly, there is no need
        // to reinflate it. We only inflate a new View when the convertView supplied
        // by ListView is null.
        if (convertView == null) {
            // get container
            LayoutInflater inflater=LayoutInflater.from(context);
            // trans layout_friend(xml) into view
            convertView=inflater.inflate(R.layout.layout_friend,null);
            // Creates a ViewHolder and store references to the children views we want to bind data to.
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else {
            // Get the ViewHolder back to get fast access to the View
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv_friendName.setText(friend_list.get(position));
        //viewHolder.rl_friendLayout.setOnClickListener(new FriendListener(position));
        return convertView;
    }

    public void addView(String new_friend){
        friend_list.add(new_friend);
        notifyDataSetChanged();
    }

    class ViewHolder {
        //public RelativeLayout rl_friendLayout;
        public TextView tv_friendName;

        public ViewHolder (View convertView){
            //rl_friendLayout=(RelativeLayout)convertView.findViewById(id[0]);
            tv_friendName=(TextView)convertView.findViewById(R.id.friend_name);
        }
    }
    /*class FriendListener implements View.OnClickListener {
        private int position;
        public FriendListener(int position){
            this.position=position;
        }

        @Override
        public void onClick(View v) {
            String chatID=friend_list.get(position).toString().trim();
            if (!TextUtils.isEmpty(chatID)){
                String curUsername = EMClient.getInstance().getCurrentUser();
                if (chatID.equals(curUsername)){
                    Toast.makeText(context,"Choose another people.",Toast.LENGTH_LONG).show();
                    return;
                }

                //jump to the chat activity
                Intent intent = new Intent(context,ChatActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("chat_id",chatID);
                context.startActivity(intent);
            }
            else{
                Toast.makeText(context,"Empty ID!",Toast.LENGTH_LONG).show();
            }

        }
    }*/
}
