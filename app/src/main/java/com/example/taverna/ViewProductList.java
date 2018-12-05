package com.example.taverna;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import java.util.Map;

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
        //Подгрузка списка товаров
        AsyncListModelProduct asyncGetProductList = new AsyncListModelProduct();
        asyncGetProductList.setMyView(this);
        asyncGetProductList.execute("1");

    }

    public void onClick(View v) {

        if (v.getId() == R.id.search_panel_button) {
            EditText searchText = findViewById(R.id.search_panel_text);

            Intent intent = new Intent(this, ViewProduct.class);
            intent.putExtra("methodSearch", Constants.SEARCH_METHOD_BY_EAN);
            intent.putExtra("dataSearch", searchText.getText().toString());
            startActivity(intent);
        } else {

            //Подгрузка списка товаров
            AsyncListModelProduct asyncGetProductList = new AsyncListModelProduct();
            asyncGetProductList.setMyView(this);
            asyncGetProductList.execute(Integer.toString((int) v.getTag()));
        }

    }

    public void onClickProductList(int idProduct) {

        Intent intent = new Intent(this, ViewProduct.class);
        intent.putExtra("methodSearch", Constants.SEARCH_METHOD_BY_ID);
        intent.putExtra("dataSearch", idProduct);
        startActivity(intent);


    }

    private void serchByName(String name) {

//Подгрузка списка товаров
        AsyncListModelProductByName asyncGetProductList = new AsyncListModelProductByName();
        asyncGetProductList.setMyView(this);
        asyncGetProductList.execute(name);


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

    //Класс асинхронного получения списка
    private class AsyncListModelProduct extends AsyncTask<String, Void, ArrayList<ModelProduct>> {

        public ViewProductList myView;

        public void setMyView(ViewProductList myView) {
            this.myView = myView;
        }

        //Процедура получения данных
        @Override
        protected ArrayList<ModelProduct> doInBackground(String... idGroups) {

            return ModelProduct.GetProductListByGroup(idGroups[0]);

        }

        //Обработчик завершения
        @Override
        protected void onPostExecute(ArrayList<ModelProduct> result) {
            super.onPostExecute(result);

            // данные получены

            // создаем адаптер
            ProductListAdapter productListAdapter;
            productListAdapter = new ProductListAdapter(myView.getApplicationContext(), result);

            // настраиваем список
            ListView myList = (ListView) myView.findViewById(R.id.product_list);
            myList.setAdapter(productListAdapter);

            myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                        long id) {

                    myView.onClickProductList((int) itemClicked.findViewById(R.id.productName).getTag());

                }
            });
        }

    }

    //Асинхронный поиск по наименованию
    private class AsyncListModelProductByName extends AsyncTask<String, Void, HashMap<String, ArrayList>> {

        public ViewProductList myView;

        public void setMyView(ViewProductList myView) {
            this.myView = myView;
        }

        //Процедура получения данных
        @Override
        protected HashMap<String, ArrayList> doInBackground(String... names) {

            return ModelProduct.GetProductGroupListByName(names[0]);

        }

        //Обработчик завершения
        @Override
        protected void onPostExecute(HashMap<String, ArrayList> result) {
            super.onPostExecute(result);

            if (result == null) {
                Toast mt = Toast.makeText(myView, "Ничего не найдено", Toast.LENGTH_LONG);
                mt.show();
                return;
            }
            // создаем адаптер
            ProductListAdapter productListAdapter;
            productListAdapter = new ProductListAdapter(myView.getApplicationContext(), result.get("products"));

            // настраиваем список
            ListView myList = (ListView) myView.findViewById(R.id.product_list);
            myList.setAdapter(productListAdapter);

            myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                        long id) {
                    myView.onClickProductList((int) itemClicked.findViewById(R.id.productName).getTag());
                }
            });

            FlowLayout resultGroup = findViewById(R.id.search_result_group);
            //Подчищу старые теги групп
            resultGroup.removeAllViews();
            for (ModelGroup strGr : (ArrayList<ModelGroup>) result.get("groups")) {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics()));

                Button nButton = new Button(myView, null, R.style.Widget_AppCompat_Button_Borderless);
                nButton.setHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 28, getResources().getDisplayMetrics()));
                nButton.setLayoutParams(layoutParams);
                nButton.setTextColor(getResources().getColor(R.color.colorTextGroupResultSearch));
                nButton.setGravity(Gravity.CENTER);
                nButton.setAllCaps(false);
                nButton.setBackgroundResource(R.drawable.search_result_group);
                nButton.setText(strGr.name);
                nButton.setOnClickListener(myView);
                nButton.setTag(strGr.id);
                resultGroup.addView(nButton);
            }


        }

    }


    //Класс адаптера для списка товара
    private class ProductListAdapter extends BaseAdapter {
        Context ctx;
        LayoutInflater lInflater;
        ArrayList<ModelProduct> objects;

        ProductListAdapter(Context context, ArrayList<ModelProduct> products) {
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
                        .load(p.imageSmall_link)
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