package nitjsr.team.in.ragnarok.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import nitjsr.team.in.ragnarok.R;

public class ShoppingListFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_shopping_list, container, false);




        return root;
    }
}