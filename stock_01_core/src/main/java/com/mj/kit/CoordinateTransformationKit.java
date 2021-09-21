package com.mj.kit;

import java.math.BigDecimal;
/**
 * 经纬度转换的方法类
 * 提供了百度坐标（BD09）、国测局坐标（火星坐标，GCJ02）、和WGS84坐标系之间的转换
 * @author Administrator
 *
 */
public class CoordinateTransformationKit {
	
	public static double PI = 3.1415926535897932384626;
	public static double x_PI = 3.14159265358979324 * 3000.0 / 180.0;
	public static double ee = 0.00669342162296594323;
	public static double a = 6378245.0;
	
	public static double transFormLat(double lng,double lat){
		double ret = -100.0 + 2.0 * lng + 3.0 * lat + 0.2 * lat * lat + 0.1 * lng * lat + 0.2 * Math.sqrt(Math.abs(lng));
        ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lat * PI) + 40.0 * Math.sin(lat / 3.0 * PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(lat / 12.0 * PI) + 320 * Math.sin(lat * PI / 30.0)) * 2.0 / 3.0;
        return ret;
	}
	
	public static double transFormLng(double lng,double lat){
		double ret = 300.0 + lng + 2.0 * lat + 0.1 * lng * lng + 0.1 * lng * lat + 0.1 * Math.sqrt(Math.abs(lng));
        ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lng * PI) + 40.0 * Math.sin(lng / 3.0 * PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(lng / 12.0 * PI) + 300.0 * Math.sin(lng / 30.0 * PI)) * 2.0 / 3.0;
        return ret;
	}
	
	//判断经纬度是否在国内
	public static boolean out_of_china(double lng,double lat){
		return (lng < 72.004 || lng > 137.8347) || ((lat < 0.8293 || lat > 55.8271) || false);
	}
	
	
	//GCJ02 转换为 WGS84(小数点后6位)
	public static String gcj02towgs84(double lng,double lat){
		if (out_of_china(lng, lat)) {
            return lng+","+lat;
        } else {
            double dlat = transFormLat(lng - 105.0, lat - 35.0);
            double dlng = transFormLng(lng - 105.0, lat - 35.0);
            double radlat = lat / 180.0 * PI;
            double magic = Math.sin(radlat);
            magic = 1 - ee * magic * magic;
            double sqrtmagic = Math.sqrt(magic);
            dlat = (dlat * 180.0) / ((a * (1 - ee)) / (magic * sqrtmagic) * PI);
            dlng = (dlng * 180.0) / (a / sqrtmagic * Math.cos(radlat) * PI);
            double mglat = lat + dlat;
            double mglng = lng + dlng;
            lng=lng*2-mglng;
            lat=lat*2-mglat;
            BigDecimal lngStr = new BigDecimal(lng);
            BigDecimal latStr = new BigDecimal(lat);  
            return lngStr.setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue()+","+
            		latStr.setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
	}
	
	//WGS84转GCj02(小数点后6位)
	public static String wgs84togcj02(double lng,double lat){
		if (out_of_china(lng, lat)) {
            return lng+","+lat;
        } else {
            double dlat = transFormLat(lng - 105.0, lat - 35.0);
            double dlng = transFormLng(lng - 105.0, lat - 35.0);
            double radlat = lat / 180.0 * PI;
            double magic = Math.sin(radlat);
            magic = 1 - ee * magic * magic;
            double sqrtmagic = Math.sqrt(magic);
            dlat = (dlat * 180.0) / ((a * (1 - ee)) / (magic * sqrtmagic) * PI);
            dlng = (dlng * 180.0) / (a / sqrtmagic * Math.cos(radlat) * PI);
            double mglat = lat + dlat;
            double mglng = lng + dlng;
            lng=lng*2-mglng;
            lat=lat*2-mglat;
            BigDecimal lngStr = new BigDecimal(lng);
            BigDecimal latStr = new BigDecimal(lat);  
            return lngStr.setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue()+","+
            		latStr.setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
	}
	
	//百度坐标系 (BD-09) 与 火星坐标系 (GCJ-02)的转换
	public static String bd09togcj02(double lng,double lat){
		double x_pi = 3.14159265358979324 * 3000.0 / 180.0;
        double x = lng - 0.0065;
        double y = lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
        lng = z * Math.cos(theta);
        lat = z * Math.sin(theta);
        BigDecimal lngStr = new BigDecimal(lng);
        BigDecimal latStr = new BigDecimal(lat);  
        return lngStr.setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue()+","+
        		latStr.setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	//火星坐标系 (GCJ-02) 与百度坐标系 (BD-09) 的转换
	public static String gcj02tobd09(double lng,double lat){
		double z = Math.sqrt(lng * lng + lat * lat) + 0.00002 * Math.sin(lat * x_PI);
        double theta = Math.atan2(lat, lng) + 0.000003 * Math.cos(lng * x_PI);
        lng = z * Math.cos(theta) + 0.0065;
        lat = z * Math.sin(theta) + 0.006;
        BigDecimal lngStr = new BigDecimal(lng);
        BigDecimal latStr = new BigDecimal(lat);  
        return lngStr.setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue()+","+
        		latStr.setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
}
