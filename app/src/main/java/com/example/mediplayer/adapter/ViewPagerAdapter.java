package com.example.mediplayer.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private ArrayList<Fragment> fragments;
    private ArrayList<String> titles;
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        this.fragments=new ArrayList<>();
        this.titles=new ArrayList<>();
    }
    public void addFragments(Fragment fragment, String title){
        fragments.add(fragment);
        titles.add(title);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }
    public CharSequence getPageTitle(int position){
        return titles.get(position);
    }
}