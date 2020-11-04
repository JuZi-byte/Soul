package com.tang.xu.mysoul.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Poi;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.tang.xu.formwork.base.BaseActivity;
import com.tang.xu.formwork.entity.Contants;
import com.tang.xu.formwork.utils.DilogMessage;
import com.tang.xu.formwork.utils.LoginUtils;
import com.tang.xu.formwork.view.DilogView;
import com.tang.xu.formwork.view.DilogViewRoom;
import com.tang.xu.formwork.view.LoadView;
import com.tang.xu.mysoul.R;
import com.tang.xu.mysoul.adapter.CommonViewAdapter;
import com.tang.xu.mysoul.adapter.CommonViewHolder;
import com.tang.xu.mysoul.entity.MapMeaasge;
import com.tang.xu.mysoul.entity.PoiEntity;

import java.util.ArrayList;

public class LocationActivity extends BaseActivity implements View.OnClickListener, PoiSearch.OnPoiSearchListener {

    private MapView mMapView = null;
    private AMap aMap;
    boolean booleanExtra;
    private EditText etSearch;
    private ImageView ivPoi;
    private DilogViewRoom viewRoom;
    private RecyclerView recyclerView;
    private TextView textView;
    private CommonViewAdapter<PoiItem> commonViewAdapter;
    private ArrayList<PoiItem> arrayList = new ArrayList<>();
    private LoadView loadView;

    private double Ijin;
    private double Iwei;
    private String Iaddress;

    private int ITEM = -1;


    public static void startActivity(Activity activity, boolean isShow, double jin, double wei, String adress,int requestCode){
        Intent intent = new Intent(activity,LocationActivity.class);
        intent.putExtra(Contants.INTENT_MENU_SHOW,isShow);
        intent.putExtra("jin",jin);
        intent.putExtra("wei",wei);
        intent.putExtra("adress",adress);
        activity.startActivityForResult(intent,requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        //文本控件
        etSearch = findViewById(R.id.et_search);
        //发送按钮
        ivPoi = findViewById(R.id.iv_poi);
        //发送按钮点击监听
        ivPoi.setOnClickListener(this);
        //初始化弹出加载Dialog
        loadView=new LoadView(this);
        loadView.setTextViewMsg("正在搜索......");
        //初始化弹出的列表Dialog
        viewRoom = DilogMessage.getInstance().createViewRoom(this, R.layout.dialog_select_constellation, Gravity.BOTTOM);
        viewRoom.setCancelable(false);
        //弹出列表Dialog的recycle
        recyclerView = viewRoom.findViewById(R.id.mConstellationnView);
        //返回按钮
        textView = viewRoom.findViewById(R.id.tv_cancel);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DilogMessage.getInstance().hide(viewRoom);
            }
        });
        //设置recycle
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        //弹出列表Dialog的Adapter
        commonViewAdapter = new CommonViewAdapter<>(arrayList, new CommonViewAdapter.OnBindListener<PoiItem>() {
            @Override
            public void onBindViewHolder(final PoiItem molde, CommonViewHolder viewHolder, int type, int position) {
                viewHolder.setText(R.id.tv_age_text,molde.toString());

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ITEM=position;

                        DilogMessage.getInstance().hide(viewRoom);

                        //通过地址转经纬度获得经纬度
                        MapMeaasge.getInstance().address2poi(molde.toString(), new MapMeaasge.OnGeocodeSearchaddress2poi() {
                            @Override
                            public void address2poi(double jin, double wei, String address) {
                                Ijin=jin;
                                Iwei=wei;
                                Iaddress=address;
                                updata(Ijin,Iwei,Iaddress);
                            }
                        });

                    }
                });
            }

            @Override
            public int gettype(int position) {
                return R.layout.layout_me_age_item;
            }
        });

        recyclerView.setAdapter(commonViewAdapter);

        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mMapView.getMap();
        }
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        myLocationStyle.interval(2000);
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        aMap.setMyLocationEnabled(true);

        aMap.moveCamera(CameraUpdateFactory.zoomTo(18));

        Intent intent = getIntent();
        booleanExtra = intent.getBooleanExtra(Contants.INTENT_MENU_SHOW,false);
        if (!booleanExtra){
            String adress = intent.getStringExtra("adress");
            double jin = intent.getDoubleExtra("jin", 0);
            double wei = intent.getDoubleExtra("wei", 0);
            updata(jin,wei,adress);
        }

        aMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {

            }
        });
    }

    //更新位置
    private void updata(double jin,double wei,String adress) {
        aMap.setMyLocationEnabled((true));
        supportInvalidateOptionsMenu();
        //显示位置
        LatLng latLng = new LatLng(jin,wei);
        aMap.clear();
        aMap.addMarker(new MarkerOptions().position(latLng).title("位置").snippet(adress));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    private PoiSearch.Query query;
    private PoiSearch poiSearch;

    //poi关键字搜索
    private void poiSearch(String keyword){
        loadView.show("正在搜索....");
        query = new PoiSearch.Query(keyword, "", "");
        query.setPageSize(6);
        query.setPageNum(1);
        poiSearch = new PoiSearch(this,query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (booleanExtra){
            getMenuInflater().inflate(R.menu.location_menu,menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.menu_send){
            Intent intent = new Intent();
            if (ITEM>=0){
                intent.putExtra("jin",Ijin);
                intent.putExtra("wei",Iwei);
                intent.putExtra("adress",Iaddress);
                LoginUtils.LogE("ITEM>0"+Ijin+""+Iwei+""+Iaddress);
            }else {
                intent.putExtra("jin",aMap.getMyLocation().getLatitude());
                intent.putExtra("wei",aMap.getMyLocation().getLongitude());
                intent.putExtra("adress",aMap.getMyLocation().getExtras().getString("desc"));
                LoginUtils.LogE("ITEM<0"+aMap.getMyLocation().getLatitude()+""+aMap.getMyLocation().getLongitude()+""+aMap.getMyLocation().getExtras().getString("desc"));
            }
            setResult(Activity.RESULT_OK,intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_poi:
                String trim = etSearch.getText().toString().trim();
                if (!TextUtils.isEmpty(trim)){
                    poiSearch(trim);
                }else{
                    return;
                }
                break;
        }
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        loadView.hide();
        if (arrayList.size()>0){
            arrayList.clear();
        }
        arrayList.addAll(poiResult.getPois());
        commonViewAdapter.notifyDataSetChanged();
        DilogMessage.getInstance().show(viewRoom);
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }
}