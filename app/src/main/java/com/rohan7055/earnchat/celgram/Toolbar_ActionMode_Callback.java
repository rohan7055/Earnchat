package com.rohan7055.earnchat.celgram;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.rohan7055.earnchat.Adapter.SetMessageAdapter;
import com.rohan7055.earnchat.R;
import com.rohan7055.earnchat.celgram.DataBase_Helper.MessageSQLiteHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SONU on 22/03/16.
 */
public class Toolbar_ActionMode_Callback implements ActionMode.Callback {

    private Context context;
    private SetMessageAdapter recyclerView_adapter;
    private ArrayList<Object> message_models;
    MessageSQLiteHelper db_helper;


    public Toolbar_ActionMode_Callback(Context context, SetMessageAdapter recyclerView_adapter, ArrayList<Object> message_models,MessageSQLiteHelper db_helper) {
        this.context = context;
        this.recyclerView_adapter = recyclerView_adapter;
        this.db_helper=db_helper;
        this.message_models = message_models;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.contextual_action_mode, menu);//Inflate the menu over action mode
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.action_reply);
        //Sometimes the meu will not be visible so for that we need to set their visibility manually in this method
        //So here show action menu according to SDK Levels
        menu.findItem(R.id.action_delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.findItem(R.id.action_copy).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.findItem(R.id.action_forward).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.findItem(R.id.action_reply).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        //To set the visibility of the info icon ... now invisible for every case...
        if(recyclerView_adapter.getSelectedCount()==1) {
            menuItem.setVisible(true);
        }
        else {
            menuItem.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_reply:

                SparseBooleanArray selected3 =recyclerView_adapter.getSelectedIds();
                Message model2 = (Message)message_models.get(selected3.keyAt(0));
                ((ChatWindow) context).replyTo(model2);

                mode.finish();
                break;

            case R.id.action_delete:
                Message model=null;
                SparseBooleanArray selected =recyclerView_adapter.getSelectedIds();

                ArrayList<String> msd_id_delete=new ArrayList<>();
                //Loop all selected ids
                for (int i = (selected.size()-1); i >= 0; i--) {
                    if (selected.valueAt(i)) {
                        //If current id is selected remove the item via key
                        model =(Message)message_models.get(selected.keyAt(i));
                        db_helper.deleteMessage(model.getId());
                        message_models.remove(selected.keyAt(i));
                       recyclerView_adapter.notifyDataSetChanged();//notify adapter always do it at the last
                    }
                }
                List<Message> msglist=  db_helper.getAllMessages(model.getConvo_partner());
                if (!msglist.isEmpty()) {
                    String lastmsgid = msglist.get(msglist.size() - 1).getId();
                    db_helper.addToChatList(model.getConvo_partner(),lastmsgid);
                    Log.i("last message",lastmsgid);
                }

                //Toast.makeText(context, "You selected delete menu.", Toast.LENGTH_SHORT).show();//Show toast
                msd_id_delete.clear();
                mode.finish();//Finish action mode
                /*ChatWindow chatWindow = (ChatWindow)context;
                chatWindow.deleteRows();*/

                break;
            case R.id.action_copy:

                SparseBooleanArray selectedcopy = recyclerView_adapter.getSelectedIds();
                int selectedMessageSize = selectedcopy.size();
                String toCopy = "";
                for(int i =(selectedMessageSize-1);i>=0;i--)
                {
                    if (selectedcopy.valueAt(i)) {
                        //get selected data in Model
                        Message model1 =(Message) message_models.get(selectedcopy.keyAt(i));
                        //String sender = model1.getSender_id();
                        toCopy = toCopy + model1.getData();
                        Log.i("messageCopy",model1.getData());
                        //Print the data to show if its working properly or not

                    }
                }

                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", toCopy);
                clipboard.setPrimaryClip(clip);

                Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show();//Show toast
                mode.finish();//Finish action mode

                break;
            case R.id.action_forward:

                SparseBooleanArray selected2 =recyclerView_adapter.getSelectedIds();

                /*Message model2=null;
                SparseBooleanArray selected2 = selected;

                //Loop all selected ids
                for (int i = (selected2.size()-1); i >= 0; i--) {
                    if (selected2.valueAt(i)) {
                        //If current id is selected remove the item via key
                        model2 =(Message)message_models.get(selected2.keyAt(i));

                        ChatWindow chatWindow = (ChatWindow)context;
                        chatWindow.forwardMsg(model2);
                        Toast.makeText(context, "msg forwarded", Toast.LENGTH_SHORT).show();
                    }
                }*/

                ChatWindow chatWindow = (ChatWindow)context;
                chatWindow.giveIntent(selected2, message_models);

                //Toast.makeText(context, "You selected Forward menu.", Toast.LENGTH_SHORT).show();//Show toast
                mode.finish();//Finish action mode
                break;

            /*case R.id.action_info:
                Message model_info =null;
                SparseBooleanArray selected_info = recyclerView_adapter.getSelectedIds();
                model_info =(Message)message_models.get(selected_info.keyAt(0));
                Intent i = new Intent(context,Message_info.class);
                i.putExtra("msg_info",model_info);
                context.startActivity(i);*/
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        //When action mode destroyed remove selected selections and set action mode to null
        ChatWindow chatWindow = (ChatWindow)context;
        recyclerView_adapter.removeSelection();
        SetMessageAdapter.notselected = true;
        chatWindow.setNullToActionMode();
    }
}