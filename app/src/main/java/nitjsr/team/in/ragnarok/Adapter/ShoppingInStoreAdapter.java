package nitjsr.team.in.ragnarok.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import nitjsr.team.in.ragnarok.Activity.StoreMapActivity;
import nitjsr.team.in.ragnarok.Modals.ListItem;
import nitjsr.team.in.ragnarok.R;

public class ShoppingInStoreAdapter extends RecyclerView.Adapter<ShoppingInStoreAdapter.MyShoppingListViewHolder> {

    private Context myContext;
    private ArrayList<ListItem> myItemList;

    String key = "ItemList";
    private static final String SHARED_PREF = "SharedPref";
    SharedPreferences shref;
    SharedPreferences.Editor editor;
    Gson gson;
    String response;
    StoreMapActivity parentActivity;


    public ShoppingInStoreAdapter(StoreMapActivity parentActivity, Context myContext, ArrayList<ListItem> itemList) {
        this.myContext = myContext;
        this.myItemList = itemList;
        this.parentActivity = parentActivity;

        //Adding list in local storage
        shref = myContext.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        gson = new Gson();
        response = shref.getString(key, "");
    }

    @NonNull
    @Override
    public ShoppingInStoreAdapter.MyShoppingListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        LayoutInflater layoutInflater = LayoutInflater.from(myContext);
        View view = layoutInflater.inflate(R.layout.shopping_card_format, null);
//            view.findViewById(R.id.view_background).setVisibility(View.GONE);

        return new MyShoppingListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyShoppingListViewHolder myShoppingListViewHolder, final int position) {

        final ListItem listItem = myItemList.get(position);

        myShoppingListViewHolder.mItemName.setText(listItem.getName());
        myShoppingListViewHolder.mItemCount.setText("Qty: "+listItem.getItemCount() + "");
        myShoppingListViewHolder.mItemLocation.setText(listItem.getShelf()+" Shelf, "+listItem.getFloor()+"Floor");


        if (listItem.isStatus()) {
            myShoppingListViewHolder.checkBox.setChecked(true);
        }

        myShoppingListViewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    ListItem listItem1 = myItemList.get(position);
                    ListItem li2=listItem1;
                    listItem1.setStatus(true);
                    myItemList.remove(position);
                    parentActivity.itemList.remove(li2);
                    parentActivity.itemList.add(listItem1);

                    Gson gson = new Gson();
                    String json = gson.toJson(parentActivity.itemList);

                    editor = shref.edit();
                    editor.remove(key).commit();
                    editor.putString(key, json);
                    editor.commit();

                    //notifyItemRemoved(position);
                    parentActivity.recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            parentActivity.adapter.notifyDataSetChanged();
                        }
                    });
                    parentActivity.setVoiceDirectionsAndText();

                } else {
                    ListItem listItem1= myItemList.get(position);
                    ListItem li2=listItem1;
                    listItem1.setStatus(false);
                    myItemList.remove(position);
                    parentActivity.itemList.remove(li2);
                    parentActivity.itemList.add(0, listItem1);

                    Gson gson = new Gson();
                    String json = gson.toJson(parentActivity.itemList);

                    editor = shref.edit();
                    editor.remove(key).commit();
                    editor.putString(key, json);
                    editor.commit();

                    //notifyItemRemoved(position);
                    parentActivity.recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            parentActivity.adapter.notifyDataSetChanged();
                        }
                    });
                    parentActivity.setVoiceDirectionsAndText();
                }

//                Gson gson = new Gson();
//                String json = gson.toJson(myItemList);
//
//                editor = shref.edit();
//                editor.remove(key).commit();
//                editor.putString(key, json);
//                editor.commit();
            }
        });
    }


    @Override
    public int getItemCount() {
        return myItemList.size();
    }


    public void removeItem(int position) {
//        myItemList.remove(position);
//        // notify the item removed by position
//        // to perform recycler view delete animations
//        // NOTE: don't call notifyDataSetChanged()
//        notifyItemRemoved(position);
//        parentActivity.setVoiceDirectionsAndText();

        ListItem listItem1 = myItemList.get(position);
        ListItem li2=listItem1;
        listItem1.setStatus(true);
        myItemList.remove(position);
        //notifyItemRemoved(position);
        parentActivity.recyclerView.post(new Runnable() {
            @Override
            public void run() {
                parentActivity.adapter.notifyDataSetChanged();
            }
        });
        parentActivity.itemList.remove(li2);
        parentActivity.itemList.add(listItem1);
        parentActivity.setVoiceDirectionsAndText();
    }

    public void restoreItem(ListItem item, int position) {
        myItemList.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
        parentActivity.setVoiceDirectionsAndText();
    }


    public class MyShoppingListViewHolder extends RecyclerView.ViewHolder {

        TextView mItemName, mItemLocation, mItemCount;
        CheckBox checkBox;
        public RelativeLayout viewBackground;
        public LinearLayout viewForeground;


        public MyShoppingListViewHolder(@NonNull final View itemView) {
            super(itemView);
            mItemName = itemView.findViewById(R.id.itemName);
            mItemCount = itemView.findViewById(R.id.itemCount);
            mItemLocation = itemView.findViewById(R.id.itemLocation);
            checkBox = itemView.findViewById(R.id.checkBox);
            viewBackground = itemView.findViewById(R.id.view_background);
            viewForeground = itemView.findViewById(R.id.view_foreground);
//            viewBackground1=itemView.findViewById(R.id.view_background_1);

        }

    }

}

