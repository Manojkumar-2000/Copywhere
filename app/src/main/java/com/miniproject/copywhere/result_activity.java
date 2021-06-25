package com.miniproject.copywhere;

import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
//This activity gets the the image uri from the main activity
public class result_activity extends AppCompatActivity {
    private static final int NUM_PAGES = 2;
    //The pager widget, which handles animation and allows swiping horizontally to access previous and next wizard steps.
    public static ViewPager2 viewPager;
    // Array of strings FOR TABS TITLES
    private String[] titles = new String[]{"Text", "Image"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String imguri=getIntent().getStringExtra("image_uri");//getting the uri passed by the main activity as a string
        Uri image_uri= Uri.parse(imguri);//converting the the obtained string to Uri
        setContentView(R.layout.result_activity);
        viewPager = findViewById(R.id.viewpager);
        // The pager adapter, which provides the pages to the view pager widget.
        FragmentStateAdapter pagerAdapter = new MyPagerAdapter(this, NUM_PAGES,image_uri);
        viewPager.setAdapter(pagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        new TabLayoutMediator(tabLayout, viewPager,(tab, position) -> tab.setText(titles[position])).attach();
    }
}
class MyPagerAdapter extends FragmentStateAdapter{
private int total_tabs;
public  Uri image_uri;
    public MyPagerAdapter(FragmentActivity fa,int totaltabs,Uri image_uri) {
        super(fa);
    total_tabs=totaltabs;
    this.image_uri=image_uri;
    }
    @NonNull
    @Override
    public Fragment createFragment(int pos) {
        switch (pos) {
            case 0: {
                //When text tab is selected this fragment is called
                return new Text_Fragment(image_uri);
            }
            case 1: {
                //when picture tab is selected this fragment is called
                return new Image_Fragment(image_uri);
            }
            default:
                throw new IllegalStateException("Unexpected value: " + pos);
        }
    }

    @Override
    public int getItemCount() {
        return total_tabs ;
    }
}