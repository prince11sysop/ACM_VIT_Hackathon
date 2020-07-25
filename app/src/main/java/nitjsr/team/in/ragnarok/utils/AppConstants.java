package nitjsr.team.in.ragnarok.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import nitjsr.team.in.ragnarok.Activity.SearchItemActivity;
import nitjsr.team.in.ragnarok.Activity.SearchResultsActivity;
import nitjsr.team.in.ragnarok.Fragments.ShoppingListFragment;
import nitjsr.team.in.ragnarok.Modals.ItemModal;
import nitjsr.team.in.ragnarok.Modals.ListItem;
import nitjsr.team.in.ragnarok.R;

public class AppConstants {
    public static ArrayList<ItemModal> mItemList = new ArrayList<>();
    public static SearchItemActivity mSearchProductActivity;
    public static SearchResultsActivity mSearchResultsActivity;
    public static FragmentActivity mCreateShoppingListActivity;
    public static boolean isCreateShoppingListActivityOpen = false;
    public static String searchKeyWord = "";
    public static String listFromScan = "";
    public static String curFloor = "0", curShelf = "0";

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void fetchGoodsItemList(Context context) {

        if (AppConstants.mItemList.size() == 0) {
            if (!AppConstants.isNetworkAvailable(context)) {
                Toast.makeText(context, "Please make sure you have a secure internet connection.", Toast.LENGTH_LONG).show();
                //progressDialog.dismiss();
                return;
            }
        }

        final DatabaseReference dref;
        dref = FirebaseDatabase.getInstance().getReference("shopName").child("items");

        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Initialising App Data...");
        progressDialog.setCancelable(false);

        progressDialog.show();

        if (AppConstants.mItemList.size() > 0) {
            AppConstants.mItemList.clear();
        }

        dref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    ItemModal itemModal = new ItemModal(
                            ds.child("category").getValue().toString(),
                            ds.child("subCategory").getValue().toString(),
                            ds.child("price").getValue().toString(),
                            ds.child("floor").getValue().toString(),
                            ds.child("shelf").getValue().toString(),
                            ds.child("description").getValue().toString(),
                            ds.child("name").getValue().toString(),
                            ds.child("imageUrl").getValue().toString()
                    );

                    Log.e("onDataChange: ", ds.child("name").getValue().toString());

                    AppConstants.mItemList.add(itemModal);
                }
                Log.e("onDataChange: ", "" + AppConstants.mItemList);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
        if (AppConstants.mItemList.size() > 0) {
            for (ItemModal im : AppConstants.mItemList) {
                Log.e("fetchGoodsItemList: ", im.getName() + " " + im.getSubCategory());
            }
        }
    }

    public static ArrayList<ListItem> sortItemList(ArrayList<ListItem> dataList) {
        Collections.sort(dataList, new Comparator<ListItem>() {
            @Override
            public int compare(ListItem o1, ListItem o2) {
                if (o1.getFloor().equalsIgnoreCase("gf")) {
                    o1.setFloor("0");
                }
                if (o2.getFloor().equalsIgnoreCase("gf")) {
                    o2.setFloor("0");
                }

                try {
                    if (o1.getFloor().equalsIgnoreCase(o2.getFloor())) {
                        return (int) o1.getShelf().toLowerCase().compareTo(o2.getShelf().toLowerCase());
                    } else {
                        return Integer.parseInt(o1.getFloor()) - Integer.parseInt(o2.getFloor());
                    }
                } catch (Exception e) {
                }

                return 0;
            }
        });
        return dataList;
    }

    public static void openAddItemDialog(final Context context, final ItemModal itemModal, final int type) {

        Activity callingActivity;
        //type 1 is for search activity

        //type 2 is for my shopping list activity

        Rect displayRectangle = new Rect();
        ViewGroup viewGroup;

        //comment it later
        viewGroup = AppConstants.mSearchResultsActivity.findViewById(android.R.id.content);

        if (type == 1) {
            Window window = AppConstants.mSearchResultsActivity.getWindow();
            window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
            viewGroup = AppConstants.mSearchResultsActivity.findViewById(android.R.id.content);
        }
//        else {
//            Window window = AppConstants.mCreateShoppingListActivity.getWindow();
//            window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
//            viewGroup = AppConstants.mCreateShoppingListActivity.findViewById(android.R.id.content);
//        }

        View dialogView = LayoutInflater.from(context).inflate(R.layout.item_details_dialog, viewGroup, false);

        TextView upper_text = dialogView.findViewById(R.id.upper_text);

        TextView itemName, itemCat, itemSubCat, itemPrice, itemFloor, itemDesc;
        itemCat = dialogView.findViewById(R.id.details_item_category);
        itemSubCat = dialogView.findViewById(R.id.details_item_subcat);
        itemPrice = dialogView.findViewById(R.id.details_item_price);
        itemFloor = dialogView.findViewById(R.id.details_item_floor);
        itemDesc = dialogView.findViewById(R.id.details_item_desc);
        itemName = dialogView.findViewById(R.id.details_item_name);

        itemName.setText(itemModal.getName());
        itemCat.setText(itemModal.getCategory());
        itemSubCat.setText(itemModal.getSubCategory());
        itemPrice.setText("Rs." + itemModal.getPrice());
        itemFloor.setText("Floor-" + itemModal.getFloor());
        itemDesc.setText(itemModal.getDescription());

        TextView addButton = dialogView.findViewById(R.id.add_item_button);
        if (type == 2) {
            addButton.setText("Got It");
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);
        builder.setView(dialogView);
        final AlertDialog alertDialogOtp;
        alertDialogOtp = builder.create();
        alertDialogOtp.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialogOtp.setCancelable(true);
        alertDialogOtp.show();

//        addButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                //do-things
//
//                //if type ==2
//                if (type == 2) {
//                    alertDialogOtp.dismiss();
//                    return;
//                }
//
//                SharedPreferences sharedPreferences = AppConstants.mSearchResultsActivity
//                        .getSharedPreferences("SharedPref", Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//
//
//                ArrayList<ListItem> myList = new ArrayList<>();
//
//
//                //fetch older item list
//                Gson gson = new Gson();
//                String response = sharedPreferences.getString("ItemList", "");
//
//                if (gson.fromJson(response, new TypeToken<List<ListItem>>() {
//                }.getType()) != null)
//                    myList = gson.fromJson(response, new TypeToken<List<ListItem>>() {
//                    }.getType());
//                else
//                    myList = new ArrayList<>();
//
//
//                boolean ifAlreadyInList = false;
//                //check if this item is already in the list
//                for (ListItem li : myList) {
//                    if (li.getName().equalsIgnoreCase(itemModal.getName())
//                            && li.getDescription().equalsIgnoreCase(itemModal.getDescription())) {
//                        li.setItemCount(li.getItemCount() + 1);
//                        ifAlreadyInList = true;
//                    }
//                }
//
//                //add to item list
//                if (!ifAlreadyInList) {
//                    myList.add(new ListItem(
//                            itemModal.getCategory(),
//                            itemModal.getSubCategory(),
//                            itemModal.getPrice(),
//                            itemModal.getFloor(),
//                            itemModal.getShelf(),
//                            itemModal.getDescription(),
//                            itemModal.getName(),
//                            1, false
//                    ));
//                }
//                Collections.reverse(myList);
//                myList = AppConstants.sortItemList(myList);
//                itemList = myList;
//
//                Gson gson2 = new Gson();
//                String json = gson2.toJson(myList);
//
//                editor = sharedPreferences.edit();
//                editor.remove("ItemList").commit();
//                editor.putString("ItemList", json);
//                editor.commit();
//
//                alertDialogOtp.dismiss();
//
//                mSearchResultsActivity.finish();
//
//                if (AppConstants.isCreateShoppingListActivityOpen) {
//                    //AppConstants.mCreateShoppingListActivity.adapter.notifyDataSetChanged();
//
//                    AppConstants.mCreateShoppingListActivity.recyclerView
//                            .setAdapter(new MyShoppingListAdapter(
//                                    context, myList
//                            ));
//                } else {
//                    context.startActivity(new Intent(context, CreateShoppingListActivity.class));
//                }
//
//            }
//        });
    }
}
