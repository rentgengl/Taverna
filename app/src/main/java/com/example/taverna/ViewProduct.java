package com.example.taverna;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class ViewProduct extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    public ModelProductFull thisProductFull;
    private SliderLayout mDemoSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_product);

        //Получение данных о товаре по ID или EAN
        AsyncModelProduct myProduct = new AsyncModelProduct();
        myProduct.setMyView(this);
        myProduct.execute(getIntent());

        final Button button = findViewById(R.id.show_price_on_map);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showPriceOnMap();
            }
        });


    }

    public void showPriceOnMap() {

        String arrName[] = new String[thisProductFull.prices.size()];
        double arrLat[] = new double[thisProductFull.prices.size()];
        double arrLng[] = new double[thisProductFull.prices.size()];
        int arrPrice[] = new int[thisProductFull.prices.size()];

        int i = 0;
        for (ModelPrice mPrice : thisProductFull.prices) {
            arrName[i] = mPrice.market.name;
            arrLat[i] = mPrice.market.latitude;
            arrLng[i] = mPrice.market.longitude;
            arrPrice[i] = mPrice.price;
            i++;

        }

        Intent intent = new Intent(this, ViewProductPriceMap.class);
        intent.putExtra("name", arrName);
        intent.putExtra("lat", arrLat);
        intent.putExtra("lng", arrLng);
        intent.putExtra("price", arrPrice);
        startActivity(intent);

    }

    public void onGetData(ModelProductFull product) {

        thisProductFull = product;
        mDemoSlider = (SliderLayout) findViewById(R.id.slider);

        if (product.images_links != null & product.images_links.size() > 0) {

            for (String link : product.images_links) {
                TextSliderView textSliderView = new TextSliderView(this);
                // initialize a SliderLayout
                textSliderView
                        .image(link)
                        .setOnSliderClickListener(this)
                        .setScaleType(BaseSliderView.ScaleType.CenterInside);

                //Данные для передачи при клике
                textSliderView.bundle(new Bundle());
                textSliderView.getBundle()
                        .putString("extra", link);

                mDemoSlider.addSlider(textSliderView);
            }


        } else {

            TextSliderView textSliderView = new TextSliderView(this);
            textSliderView
                    .image(R.drawable.noimage_small)
                    .setScaleType(BaseSliderView.ScaleType.CenterInside);

            mDemoSlider.addSlider(textSliderView);

        }

        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Default);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.stopAutoCycle();
        mDemoSlider.addOnPageChangeListener(this);

    }


    // Обработчики слайдера картинок
    @Override
    protected void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        mDemoSlider.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(this, slider.getBundle().get("extra") + "", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        Log.d("Slider Demo", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    //Асинхронная загрузка данных
    private class AsyncModelProduct extends AsyncTask<Intent, Void, ModelProductFull> {

        public ViewProduct myView;

        public void setMyView(ViewProduct myView) {
            this.myView = myView;
        }

        @Override
        protected ModelProductFull doInBackground(Intent... params) {

            Intent openIntent = params[0];
            switch (openIntent.getStringExtra("methodSearch")) {
                case Constants.SEARCH_METHOD_BY_ID:
                    return ModelProductFull.getProductByID(openIntent.getIntExtra("dataSearch", 0));

                case Constants.SEARCH_METHOD_BY_EAN:
                    return ModelProductFull.getProductByEAN(openIntent.getStringExtra("dataSearch"));

                default:
                    return null;
            }
        }


        @Override
        protected void onPostExecute(ModelProductFull result) {
            super.onPostExecute(result);

            if (result == null) {
                return;
            }
            //Накину данные продукта на форму
            TextView view_midPrice = myView.findViewById(R.id.midPrice);//Средняя цена
            TextView view_productName = myView.findViewById(R.id.productName);//Наименование
            TextView view_lowPrice = myView.findViewById(R.id.lowPrice);//Разброс цен
            TextView view_textRaiting = myView.findViewById(R.id.textRaiting);//Рейтинг текстом
            RatingBar view_productRaiting = myView.findViewById(R.id.productRaiting);//Рейтинг

            ListView view_price_list = myView.findViewById(R.id.price_list);
            ListView view_comment_list = myView.findViewById(R.id.comment_list);

            // заполняем View в пункте списка данными из товаров: наименование, цена
            // и картинка
            view_productName.setText(result.name);
            view_productName.setTag(result.id);
            view_midPrice.setText(result.price + "\u20BD");

            view_lowPrice.setText("от " + result.price_min + " до " + result.price_max + "\u20BD");
            view_textRaiting.setText(result.raiting + " из 5");
            view_productRaiting.setRating(result.raiting);


            //Вывод списков
            if (result.prices != null) {
                PriceListAdapter priceAdapter = new PriceListAdapter(myView, result.prices);
                view_price_list.setAdapter(priceAdapter);
            }
            if (result.comments != null) {
                CommentListAdapter commentAdapter = new CommentListAdapter(myView, result.comments);
                view_comment_list.setAdapter(commentAdapter);
            }
            myView.onGetData(result);


        }


        //Адаптеры списков
        private class CommentListAdapter extends BaseAdapter {
            Context ctx;
            LayoutInflater lInflater;
            ArrayList<ModelComment> objects;

            CommentListAdapter(Context context, ArrayList<ModelComment> comments) {
                ctx = context;
                objects = comments;
                lInflater = (LayoutInflater) ctx
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }

            // кол-во элементов
            @Override
            public int getCount() {
                return objects.size();
            }

            // элемент по позиции
            @Override
            public Object getItem(int position) {
                return objects.get(position);
            }

            // id по позиции
            @Override
            public long getItemId(int position) {
                return position;
            }

            // пункт списка
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // используем созданные, но не используемые view
                View view = convertView;
                if (view == null) {
                    view = lInflater.inflate(R.layout.fragment_list_comment, parent, false);
                }

                ModelComment obj = getObj(position);

                TextView view_user_comment = view.findViewById(R.id.user_comment);
                TextView view_user_name = view.findViewById(R.id.user_name);
                TextView view_user_raiting_text = view.findViewById(R.id.user_raiting_text);
                RatingBar view_user_raiting = view.findViewById(R.id.user_raiting);

                view_user_comment.setText(obj.comment);
                view_user_name.setText(obj.user.name);
                view_user_raiting_text.setText(obj.raiting + " из 5");
                view_user_raiting.setRating(obj.raiting);

                return view;
            }

            ModelComment getObj(int position) {
                return ((ModelComment) getItem(position));
            }
        }

        private class PriceListAdapter extends BaseAdapter {
            Context ctx;
            LayoutInflater lInflater;
            ArrayList<ModelPrice> objects;

            PriceListAdapter(Context context, ArrayList<ModelPrice> products) {
                ctx = context;
                objects = products;
                lInflater = (LayoutInflater) ctx
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }

            // кол-во элементов
            @Override
            public int getCount() {
                return objects.size();
            }

            // элемент по позиции
            @Override
            public Object getItem(int position) {
                return objects.get(position);
            }

            // id по позиции
            @Override
            public long getItemId(int position) {
                return position;
            }

            // пункт списка
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // используем созданные, но не используемые view
                View view = convertView;
                if (view == null) {
                    view = lInflater.inflate(R.layout.fragment_list_price, parent, false);
                }

                ModelPrice obj = getObj(position);

                TextView view_magazinName = view.findViewById(R.id.magazinName);
                TextView view_magazinAdres = view.findViewById(R.id.magazinAdres);
                TextView view_magazinPrice = view.findViewById(R.id.magazinPrice);
                ImageView view_imageLogo = view.findViewById(R.id.imageLogo);

                view_magazinName.setText(obj.market.name);
                view_magazinAdres.setText(obj.market.adress);
                view_magazinPrice.setText(obj.price + "\u20BD");

                if (obj.market.logo_link != null) {
                    Picasso.with(ctx)
                            .load(obj.market.logo_link)
                            .placeholder(R.drawable.noimage_small)
                            .error(R.drawable.noimage_small)
                            .into(view_imageLogo);
                }

                return view;
            }

            ModelPrice getObj(int position) {
                return ((ModelPrice) getItem(position));
            }

        }


    }


}
