package com.example.vendorapp.UI.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.vendorapp.UI.Fragments.OrderManageFragment;

import java.util.Arrays;

public class TabLayoutPagerAdapter extends FragmentPagerAdapter {
    public TabLayoutPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(position == 0)
        {
            return new OrderManageFragment(Arrays.asList("Pending"));
        } else if (position == 1) {
            return new OrderManageFragment(Arrays.asList("Confirmed","Packed","Preparing","Out-For-Delivery"));
        } else if (position == 2) {
            return new OrderManageFragment(Arrays.asList("Delivered"));
        } else{
            return new OrderManageFragment(Arrays.asList("Rejected","Cancelled","Refunded"));
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if(position == 0)
        {
            return "Pending Orders";
        } else if (position == 1) {
            return "Dispatched Orders";
        } else if (position == 2) {
            return "Delivered Orders";
        } else{
            return "Rejected Orders";
        }
    }
}
