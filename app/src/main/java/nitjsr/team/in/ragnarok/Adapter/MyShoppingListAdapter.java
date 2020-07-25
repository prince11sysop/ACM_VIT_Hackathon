package nitjsr.team.in.ragnarok.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;

import nitjsr.team.in.ragnarok.Modals.ItemModal;
import nitjsr.team.in.ragnarok.Modals.ListItem;
import nitjsr.team.in.ragnarok.R;
import nitjsr.team.in.ragnarok.utils.AppConstants;

import static nitjsr.team.in.ragnarok.Fragments.ShoppingListFragment.itemList;
import static nitjsr.team.in.ragnarok.Fragments.ShoppingListFragment.recyclerView;

public class MyShoppingListAdapter extends RecyclerView.Adapter<MyShoppingListAdapter.MyShoppingListViewHolder> {

    private Context myContext;
    private ArrayList<ListItem> myItemList = new ArrayList<>();

    String key = "ItemList";
    private static final String SHARED_PREF = "SharedPref";
    SharedPreferences shref;
    SharedPreferences.Editor editor;
    Gson gson;
    String response;


    public MyShoppingListAdapter(Context myContext, ArrayList<ListItem> itemList) {
        this.myContext = myContext;
        this.myItemList = itemList;

        //Adding list in local storage
        shref = myContext.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        gson = new Gson();
        response = shref.getString(key, "");
    }

    @NonNull
    @Override
    public MyShoppingListAdapter.MyShoppingListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        LayoutInflater layoutInflater = LayoutInflater.from(myContext);
        View view = layoutInflater.inflate(R.layout.card_format, null);

        return new MyShoppingListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyShoppingListViewHolder myShoppingListViewHolder, final int position) {

        final ListItem listItem = myItemList.get(position);

        if (!listItem.isStatus()) {
            myShoppingListViewHolder.checkBox.setButtonDrawable(R.drawable.ic_check_box_outline_blank_black_24dp);
        }

        myShoppingListViewHolder.mItemName.setText(listItem.getName());

        myShoppingListViewHolder.itemPrice.setText("Rs." + listItem.getPrice());

        myShoppingListViewHolder.itemLocation.setText("Floor-" + listItem.getFloor() +
                ", Shelf-" + listItem.getShelf());

        myShoppingListViewHolder.mItemCount.setText(listItem.getItemCount() + "");

        if (listItem.getItemCount() == 1) {
            myShoppingListViewHolder.removeItemButton.setAlpha(0.5f);
        }

        myShoppingListViewHolder.addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemList.get(position).setItemCount(listItem.getItemCount() + 1);
                Gson gson = new Gson();
                String json = gson.toJson(itemList);

                editor = shref.edit();
                //editor.remove(key).apply();
                editor.putString(key, json);
                editor.apply();
                //adapter.notifyDataSetChanged();
                MyShoppingListAdapter adapter = new MyShoppingListAdapter(myContext, itemList);
                recyclerView.setAdapter(adapter);
            }
        });

        myShoppingListViewHolder.removeItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listItem.getItemCount() == 1) {
                    return;
                }
                itemList.get(position).setItemCount(listItem.getItemCount() - 1);
                Gson gson = new Gson();
                String json = gson.toJson(itemList);

                editor = shref.edit();
                editor.remove(key).commit();
                editor.putString(key, json);
                editor.commit();
                //adapter.notifyDataSetChanged();
                MyShoppingListAdapter adapter = new MyShoppingListAdapter(myContext, itemList);
                recyclerView.setAdapter(adapter);
            }
        });

        myShoppingListViewHolder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                itemList.remove(position);
                Gson gson = new Gson();
                String json = gson.toJson(itemList);

                editor = shref.edit();
                editor.remove(key).commit();
                editor.putString(key, json);
                editor.commit();
                //adapter.notifyDataSetChanged();
                MyShoppingListAdapter adapter = new MyShoppingListAdapter(myContext, itemList);
                recyclerView.setAdapter(adapter);
            }
        });

        if (listItem.isStatus()) {
            myShoppingListViewHolder.checkBox.setChecked(true);
        }

        myShoppingListViewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ListItem listItem1 = itemList.get(position);
                    itemList.remove(position);
                    listItem1.setStatus(true);
                    itemList.add(listItem1);

                } else {
                    ListItem listItem1 = itemList.get(position);
                    itemList.remove(position);

                    listItem1.setStatus(false);
                    itemList.add(0, listItem1);
                }

                Gson gson = new Gson();
                String json = gson.toJson(itemList);

                editor = shref.edit();
                editor.remove(key).commit();
                editor.putString(key, json);
                editor.commit();

                MyShoppingListAdapter adapter = new MyShoppingListAdapter(myContext, itemList);
                recyclerView.setAdapter(adapter);
            }
        });

        myShoppingListViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListItem temp = myItemList.get(position);
                ItemModal tempToOpen = AppConstants.mItemList.get(0);
                for (ItemModal im : AppConstants.mItemList) {
                    if (im.getName().equals(temp.getName()) &&
                            im.getDescription().equals(temp.getDescription())) {
                        tempToOpen = im;
                        break;
                    }
                }
//                ItemModal tempToOpen = new ItemModal(temp.getCategory(),
//                        temp.getSubCategory(),
//                        temp.getPrice(),
//                        temp.getFloor(),
//                        temp.getShelf(),
//                        temp.getDescription(),
//                        temp.getName(),"");
                AppConstants.openAddItemDialog(myContext, tempToOpen, 2);
            }
        });

    }


    @Override
    public int getItemCount() {
        return myItemList.size();
    }


    //    public static class MyReportsViewHolder extends RecyclerView.ViewHolder {
    public class MyShoppingListViewHolder extends RecyclerView.ViewHolder {

        TextView mItemName, mItemCount, itemPrice, itemLocation;
        ImageView deleteItem, addItemButton, removeItemButton;
        CheckBox checkBox;


        public MyShoppingListViewHolder(@NonNull final View itemView) {
            super(itemView);
            mItemName = itemView.findViewById(R.id.itemName);
            mItemCount = itemView.findViewById(R.id.count_text_cf);
            deleteItem = itemView.findViewById(R.id.deleteBtn);
            checkBox = itemView.findViewById(R.id.checkBox);
            itemPrice = itemView.findViewById(R.id.item_price_cf);
            itemLocation = itemView.findViewById(R.id.shelf_no_cf);
            addItemButton = itemView.findViewById(R.id.add_item_cf);
            removeItemButton = itemView.findViewById(R.id.remove_item_cf);

            itemView.setTag(getAdapterPosition());
        }

    }

}

