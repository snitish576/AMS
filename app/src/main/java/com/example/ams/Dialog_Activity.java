package com.example.ams;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.Window;
import android.widget.TableLayout;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class Dialog_Activity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager2 viewPager2;

    ViewPageFragmentAdapter adapter;

    String[] labels = new String[]{"Range Roll Numbers","Custom Roll Numbers"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dialog_);


        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();

        }


        tabLayout = findViewById(R.id.tab);

        viewPager2 = findViewById(R.id.viewpager);

        adapter = new ViewPageFragmentAdapter(this);


        viewPager2.setAdapter(adapter);

        new TabLayoutMediator(tabLayout,viewPager2,((tab, position) -> {tab.setText(labels[position]);})).attach();



    }

    private class ViewPageFragmentAdapter extends FragmentStateAdapter{


        public ViewPageFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
           switch (position){
               case 0:
                   return new FirstFragment();
               case 1:
                   return new SecondFragment();

           }
           return new FirstFragment();
        }

        @Override
        public int getItemCount() {
            return labels.length;
        }
    }
}