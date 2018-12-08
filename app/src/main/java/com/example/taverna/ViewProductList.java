package com.example.taverna;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewProductList extends Activity implements View.OnClickListener {

    public EditText searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        ImageButton searchButton = this.findViewById(R.id.search_panel_button);
        searchButton.setOnClickListener(this);

        EditText searchText = this.findViewById(R.id.search_panel_text);
        searchText.setOnKeyListener(new OnKeyPress());
        //Подгрузка списка товаров первой группы
        showProductListByGroup(1);

    }

    public void onClick(View v) {

        if (v.getId() == R.id.search_panel_button) {
            EditText searchText = findViewById(R.id.search_panel_text);

            showProductDetailByEAN(searchText.getText().toString());

        } else {
            //Клик по группе
            //Подгрузка списка товаров
            int idGroup = (int) v.getTag();
            showProductListByGroup(idGroup);
        }

    }

    public void onClickProductList(int idProduct) {

        showProductDetailById(idProduct);

    }

    private void showProductDetailById(int id){

        DataApi mDataApi = SingletonRetrofit.getInstance().getDataApi();
        Call<ModelProductFull> serviceCall = mDataApi.getProductFullById(id);
        serviceCall.enqueue(new Callback<ModelProductFull>() {
            @Override
            public void onResponse(Call<ModelProductFull> call, Response<ModelProductFull> response) {
                ModelProductFull ss = response.body();
                showProductDetail(ss);
            }

            @Override
            public void onFailure(Call<ModelProductFull> call, Throwable t) {
                showErrorSearch();
            }
        });

    }

    private void showProductDetailByEAN(String EAN){

        DataApi mDataApi = SingletonRetrofit.getInstance().getDataApi();
        Call<ModelProductFull> serviceCall = mDataApi.getProductFullByEAN(EAN);
        serviceCall.enqueue(new Callback<ModelProductFull>() {
            @Override
            public void onResponse(Call<ModelProductFull> call, Response<ModelProductFull> response) {
                ModelProductFull ss = response.body();
                showProductDetail(ss);
            }

            @Override
            public void onFailure(Call<ModelProductFull> call, Throwable t) {
                showErrorSearch();
            }
        });

    }

    private void showProductDetail(ModelProductFull prod){

        Intent intent = new Intent(this, ViewProduct.class);
        intent.putExtra("object", prod);
        startActivity(intent);

    }


    private void serchByName(String name) {

        //Подгрузка списка товаров
        /*
        AsyncListModelProductByName asyncGetProductList = new AsyncListModelProductByName();
        asyncGetProductList.setMyView(this);
        asyncGetProductList.execute(name);
        */
        DataApi mDataApi = SingletonRetrofit.getInstance().getDataApi();
        Call<ModelSearchResult> serviceCall = mDataApi.getProductGroupListByName(name);
        serviceCall.enqueue(new Callback<ModelSearchResult>() {
            @Override
            public void onResponse(Call<ModelSearchResult> call, Response<ModelSearchResult> response) {
                ModelSearchResult ss = response.body();
                showGroupList(ss.getGroups());
                showProductList(ss.getProducts());
            }

            @Override
            public void onFailure(Call<ModelSearchResult> call, Throwable t) {
                showErrorSearch();
            }
        });


    }

    private void showProductListByGroup(int idGroup){

        DataApi mDataApi = SingletonRetrofit.getInstance().getDataApi();
        Call<List<ModelProduct>> serviceCall = mDataApi.getProductListByGroup(idGroup);
        serviceCall.enqueue(new Callback<List<ModelProduct>>() {
            @Override
            public void onResponse(Call<List<ModelProduct>> call, Response<List<ModelProduct>> response) {
                List<ModelProduct> ss = response.body();
                showProductList(ss);
            }

            @Override
            public void onFailure(Call<List<ModelProduct>> call, Throwable t) {
                showErrorSearch();
            }
        });

    }

    public void showErrorSearch() {
        Toast mt = Toast.makeText(this,"Ничего не найдено", Toast.LENGTH_LONG);
        mt.show();
    }

    private class OnKeyPress implements View.OnKeyListener {
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // сохраняем текст, введенный до нажатия Enter в переменную
                EditText editText = v.findViewById(R.id.search_panel_text);
                String strCatName = editText.getText().toString();
                if (!strCatName.equals(""))
                    serchByName(strCatName);

                return true;
            }
            return false;
        }
    }

    public void showGroupList(List<ModelGroup> groupList){
        FlowLayout resultGroup = findViewById(R.id.search_result_group);
        //Подчищу старые теги групп
        resultGroup.removeAllViews();
        for (ModelGroup strGr : groupList) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics()));

            Button nButton = new Button(this, null, R.style.Widget_AppCompat_Button_Borderless);
            nButton.setHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 28, getResources().getDisplayMetrics()));
            nButton.setLayoutParams(layoutParams);
            nButton.setTextColor(getResources().getColor(R.color.colorTextGroupResultSearch));
            nButton.setGravity(Gravity.CENTER);
            nButton.setAllCaps(false);
            nButton.setBackgroundResource(R.drawable.search_result_group);
            nButton.setText(strGr.name);
            nButton.setOnClickListener(this);
            nButton.setTag(strGr.id);
            resultGroup.addView(nButton);
        }
    }

    public void showProductList(List<ModelProduct> productList){
        // создаем адаптер
        ProductListAdapter productListAdapter;
        productListAdapter = new ProductListAdapter(this.getApplicationContext(), productList);

        // настраиваем список
        ListView myList = (ListView) this.findViewById(R.id.product_list);
        myList.setAdapter(productListAdapter);

        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {

                onClickProductList((int) itemClicked.findViewById(R.id.productName).getTag());

            }
        });
    }

    //Класс адаптера для списка товара
    private class ProductListAdapter extends BaseAdapter {
        Context ctx;
        LayoutInflater lInflater;
        List<ModelProduct> objects;

        ProductListAdapter(Context context, List<ModelProduct> products) {
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
                view = lInflater.inflate(R.layout.fragment_list_product, parent, false);
            }

            ModelProduct p = getProduct(position);

            TextView view_midPrice = view.findViewById(R.id.midPrice);//Средняя цена
            TextView view_productName = view.findViewById(R.id.productName);//Наименование
            TextView view_lowPrice = view.findViewById(R.id.lowPrice);//Разброс цен
            TextView view_textRaiting = view.findViewById(R.id.textRaiting);//Количество отзывов
            RatingBar view_productRaiting = view.findViewById(R.id.productRaiting);//Рейтинг

            // заполняем View в пункте списка данными из товаров: наименование, цена
            // и картинка
            view_productName.setText(p.name);
            view_productName.setTag(p.id);
            view_midPrice.setText(p.price + "\u20BD");

            view_lowPrice.setText("от " + p.price_min + " до " + p.price_max + "\u20BD");
            view_textRaiting.setText(p.comment_count + " отзывов");
            view_productRaiting.setRating(p.raiting);

            ImageView pictire = view.findViewById(R.id.imageView);
            if (p.imageSmall_link == null) {
                pictire.setImageResource(R.drawable.noimage_small);
            } else {

                Picasso.with(ctx)
                        .load(Constants.SERVICE_GET_IMAGE + p.imageSmall_link)
                        .placeholder(R.drawable.noimage_small)
                        .error(R.drawable.noimage_small)
                        .into(pictire);

            }
            return view;
        }

        // товар по позиции
        ModelProduct getProduct(int position) {
            return ((ModelProduct) getItem(position));
        }


    }

}