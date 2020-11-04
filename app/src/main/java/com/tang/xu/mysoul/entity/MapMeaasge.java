package com.tang.xu.mysoul.entity;

import android.content.Context;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;

public
class MapMeaasge {

    public static volatile  MapMeaasge mapMeaasge = null;

    private GeocodeSearch geocodeSearch;
    private OnGeocodeSearchpoi2address poi2address;
    private OnGeocodeSearchaddress2poi address2poi;

    public void setOnGeocodeSearch(OnGeocodeSearchpoi2address poi2address) {
        this.poi2address = poi2address;
    }

    public void setAddress2poi(OnGeocodeSearchaddress2poi address2poi) {
        this.address2poi = address2poi;
    }

    private MapMeaasge() {
    }

    public static MapMeaasge getInstance(){
        if (mapMeaasge==null){
            synchronized (MapMeaasge.class){
                if (mapMeaasge==null){
                    mapMeaasge=new MapMeaasge();
                }
            }
        }
        return mapMeaasge;
    }


    public void initMap(Context context){
        geocodeSearch = new GeocodeSearch(context);
        geocodeSearch.setOnGeocodeSearchListener(searchListener);
    }

    private GeocodeSearch.OnGeocodeSearchListener searchListener = new GeocodeSearch.OnGeocodeSearchListener() {
        @Override
        public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
            if (i== AMapException.CODE_AMAP_SUCCESS){
                if (regeocodeResult!=null){
                    if (poi2address!=null){
                        poi2address.poi2address(regeocodeResult.getRegeocodeAddress().getFormatAddress());
                    }
                }
            }
        }

        @Override
        public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
            if (i== AMapException.CODE_AMAP_SUCCESS){
                if (geocodeResult!=null){
                    if (address2poi!=null){
                        if (geocodeResult.getGeocodeAddressList()!=null&&geocodeResult.getGeocodeAddressList().size()>0){
                            GeocodeAddress geocodeAddress = geocodeResult.getGeocodeAddressList().get(0);
                            address2poi.address2poi(geocodeAddress.getLatLonPoint().getLatitude(),geocodeAddress.getLatLonPoint().getLongitude(),
                                    geocodeAddress.getFormatAddress());
                        }
                    }
                }
            }
        }
    };

    //地址转经纬度
    public MapMeaasge address2poi(String address,OnGeocodeSearchaddress2poi listener){
        this.address2poi=listener;
        GeocodeQuery geocodeQuery = new GeocodeQuery(address,"");
        geocodeSearch.getFromLocationNameAsyn(geocodeQuery);
        return mapMeaasge;
    }

    //经纬度转地址
    public MapMeaasge poi2address(double jin,double wei,OnGeocodeSearchpoi2address listener){
        this.poi2address = listener;
        RegeocodeQuery regeocodeQuery = new RegeocodeQuery(new LatLonPoint(jin, wei), 3000,GeocodeSearch.AMAP);
        geocodeSearch.getFromLocationAsyn(regeocodeQuery);
        return mapMeaasge;
    }

    public interface OnGeocodeSearchpoi2address{
        void poi2address(String address);
    }

    public interface OnGeocodeSearchaddress2poi{
        void address2poi(double jin,double wei,String address);
    }

    public String getMapUrl(double jin,double wei){
        //String url="https://restapi.amap.com/v3/staticmap?location="+jin+","+wei+"&zoom=10&size=750*300&markers=mid,,A:"+jin+","+wei+"&key="+"15afc4ea24f9823e698e0e2a03c55362";
        String url = "https://restapi.amap.com/v3/staticmap?location=" + wei + "," + jin +
                "&zoom=17&scale=2&size=150*150&markers=mid,,A:" + wei + ","
                + jin + "&key=" + "15afc4ea24f9823e698e0e2a03c55362";
        return url;
    }

}
