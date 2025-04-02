package com.example.myapplication.Ui.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.myapplication.R;


public class SearchFragment extends Fragment {
    EditText editText;
    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        editText = view.findViewById(R.id.searchBar_SearchFragment);
        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String searchText = v.getText().toString();
                if(!searchText.isEmpty())
                {
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    RecyclerForSearchFragment recyclerForSearchFragment = new RecyclerForSearchFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("Value", v.getText().toString());
                    recyclerForSearchFragment.setArguments(bundle);
                    fragmentTransaction.replace(R.id.linearLayout_SearchFragment, recyclerForSearchFragment);
                    fragmentTransaction.commit();
                    editText.setText("");
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    editText.clearFocus();
                    return true;
                }else {
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    editText.clearFocus();

                   return false;
                }
            }
            return false;
        });
        return view;
    }
}