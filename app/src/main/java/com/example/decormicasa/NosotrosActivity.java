package com.example.decormicasa;

import androidx.viewpager2.widget.ViewPager2;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;

import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class NosotrosActivity extends AppCompatActivity {

    private ViewPager2 viewPager2;
    private int[] sampleImages = {R.drawable.banner1, R.drawable.carrusel_img1, R.drawable.carrusel_img2};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nosotros);

        viewPager2 = findViewById(R.id.viewPager2);

        // Establece un adaptador
        CarouselAdapter carouselAdapter = new CarouselAdapter(sampleImages);
        viewPager2.setAdapter(carouselAdapter);

        // Desliza automáticamente cada 3 segundos
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int currentItem = viewPager2.getCurrentItem();
                int nextItem = (currentItem + 1) % sampleImages.length;
                viewPager2.setCurrentItem(nextItem, true);
                new Handler().postDelayed(this, 3000);  // Repite cada 3 segundos
            }
        }, 3000);  // Primer retraso de 3 segundos
    }

    private class ImageAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return sampleImages.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(NosotrosActivity.this);
            imageView.setImageResource(sampleImages[position]);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
