package com.example.xxw.testmanager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

public class MainActivity extends AppCompatActivity {
    private Button mButtonStart, mButtonStop, mButtonSearch;
    private static final int PERMISSION_REQUEST_CODE = 0;
    private TextView mTextViewLatitude, mTextViewLongitude, mTextViewCurrentSpeed, mTextViewDistance, mTextViewTime, mTextViewAspeed;
    private String provider;
    private LocationManager locationManager;
    private Location location;
    private ImageView mImageView;
    private List<Location> mLocationList;
    private float mDistance = 0;
    private boolean isGps = false;
    private boolean isShow = true;
    private AlertDialog mDialog;
    private long mTime;
    private MyLocationListener mListener = new MyLocationListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //判断是否已经打开GPS模块
        if (locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            //GPS模块打开，可以定位操作
            Log.d("tagg", "onCreate: ==" + "yes");
        } else {
            Toast.makeText(MainActivity.this, "请打开GPS模块", Toast.LENGTH_SHORT).show();
            //这里可以用一个dialog去让用户设置；
            Intent i = new Intent();
            i.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(i);
        }
        Criteria criteria = new Criteria();
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
            } else {
            }
        }
        // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, mListener);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(false);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        provider = locationManager.getBestProvider(criteria, true);
        initData();
        setData();
        setListener();
    }

    private void initView() {
        mTextViewTime = (TextView) findViewById(R.id.tv_activity_main_time);
        mTextViewAspeed = (TextView) findViewById(R.id.tv_activity_main_aspeed);
        mButtonSearch = (Button) findViewById(R.id.btn_activity_main_search);
        mButtonStop = (Button) findViewById(R.id.btn_activity_main_stop);
        mButtonStart = (Button) findViewById(R.id.btn_activity_main_start);
        mTextViewLatitude = (TextView) findViewById(R.id.tv_activity_main_latitude);
        mTextViewLongitude = (TextView) findViewById(R.id.tv_activity_main_longitude);
        mTextViewCurrentSpeed = (TextView) findViewById(R.id.tv_activity_main_currentspeed);
        mTextViewDistance = (TextView) findViewById(R.id.tv_activity_main_distance);
    }

    private void initData() {

    }

    private void setData() {

    }

    private void setListener() {
        mButtonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 23) {
                    //动态权限；
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
                    } else {
                        getDialog();
                        //绑定监听状态
                        locationManager.addGpsStatusListener(listener);
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, mListener);
                    }
                } else {
                }
            }
        });
        mButtonStart.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (view != null)
                    if (Build.VERSION.SDK_INT >= 23) {
                        isGps = true;
                    }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            // requestCode即所声明的权限获取码，在checkSelfPermission时传入
            case PERMISSION_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获取到权限，作相应处理（调用定位SDK应当确保相关权限均被授权，否则可能引起定位失败）
                    // Location location = locationManager.getLastKnownLocation(provider);
                    //                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    //                        // TODO: Consider calling
                    //                        //    ActivityCompat#requestPermissions
                    //                        // here to request the missing permissions, and then overriding
                    //                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                        //                                          int[] grantResults)
                    //                        // to handle the case where the user grants the permission. See the documentation
                    //                        // for ActivityCompat#requestPermissions for more details.
                    //                        return;
                    //                    }
                    //                    location = locationManager.getLastKnownLocation(provider);
                    //                    float currentSpeed = location.getSpeed();
                    //                    double latitude = location.getLatitude();
                    //                    double longitude = location.getLongitude();
                    //                    mTextViewLatitude.setText(latitude + "");
                    //                    mTextViewLongitude.setText(longitude + "");
                    //                    mTextViewCurrentSpeed.setText(currentSpeed + "");
                    //                    mTextViewDistance.setText(0 + "");
                } else {
                    // 没有获取到权限，做特殊处理
                }
                break;

            default:
                break;

        }
    }

    //状态监听
    GpsStatus.Listener listener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) {
            switch (event) {
                //第一次定位
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    //第一次定位，存入第一个数据，进行位置的计算；
                    Toast.makeText(MainActivity.this, "第一次定位", Toast.LENGTH_SHORT).show();
                    Location ocation = locationManager.getLastKnownLocation(provider);
                    mDialog.dismiss();
                    mLocationList = new ArrayList<>();
                    mLocationList.add(ocation);
                    //得到纬度
                    double latitude = ocation.getLatitude();
                    //得到经度
                    double longitude = ocation.getLongitude();
                    float currentSpeed = ocation.getSpeed();
                    mTextViewLatitude.setText(latitude + "==");
                    mTextViewLongitude.setText(longitude + "==");
                    mTextViewCurrentSpeed.setText(currentSpeed + "==");
                    mTime = ocation.getTime();
                    break;
                //卫星状态改变
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:

                    Toast.makeText(MainActivity.this, "卫星状态改变", Toast.LENGTH_SHORT).show();
                    //获取当前状态
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    GpsStatus gpsStatus = locationManager.getGpsStatus(null);
                    //获取卫星颗数的默认最大值
                    int maxSatellites = gpsStatus.getMaxSatellites();
                    //创建一个迭代器保存所有卫星
                    Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
                    int count = 0;
                    while (iters.hasNext() && count <= maxSatellites) {
                        GpsSatellite s = iters.next();
                        //                        count++;
                        if (s.getSnr() > 30) {
                            count++;
                            if (count >= 6) {
                                //                                mDialog.dismiss();
                                //                                Toast.makeText(MainActivity.this, "成功", Toast.LENGTH_SHORT).show();
                                //isGps = true;
                            } else {

                            }
                        }
                    }
                    Toast.makeText(MainActivity.this, "搜索到：" + count + "颗卫星", Toast.LENGTH_SHORT).show();
                    break;
                //定位启动
                case GpsStatus.GPS_EVENT_STARTED:
                    Toast.makeText(MainActivity.this, "定位启动", Toast.LENGTH_SHORT).show();
                    break;
                //定位结束
                case GpsStatus.GPS_EVENT_STOPPED:
                    Toast.makeText(MainActivity.this, "定位结束", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    //弹出dialog并且点击外部不能消失
    private void getDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);  //先得到构造器
        builder.setTitle("正在搜索GPS！");
        builder.setMessage("正在努力搜索"); //设置内容
        //        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
        //            @Override
        //            public void onClick(DialogInterface dialog, int which) {
        //                dialog.dismiss(); //关闭dialog
        //            }
        //        });
        //        builder.setNegativeButton("下次", new DialogInterface.OnClickListener() {
        //            @Override
        //            public void onClick(DialogInterface dialog, int which) {
        //                dialog.dismiss();
        //            }
        //        });
        //参数都设置完成了，创建并显示出来
        mDialog = builder.create();
        mDialog.show();
        mDialog.setCanceledOnTouchOutside(false);
    }

    private class MyLocationListener implements LocationListener {


        @Override
        public void onLocationChanged(Location ocation) {

            //        if (isShow && isGps) {
            //            mLocationList.add(ocation);
            //            //得到纬度
            //            double latitude = ocation.getLatitude();
            //            //得到经度
            //            double longitude = ocation.getLongitude();
            //            float currentSpeed = ocation.getSpeed();
            //            mTextViewLatitude.setText(latitude + "==");
            //            mTextViewLongitude.setText(longitude + "==");
            //            mTextViewCurrentSpeed.setText(currentSpeed + "==");
            //            mImageView.setVisibility(View.GONE);
            //            isShow = false;
            //        } else {
            float accuracy = ocation.getAccuracy();
            if (accuracy < 15) {
                mDialog.dismiss();
            }
            if (isGps) {
                if (isShow) {
                    mLocationList = new ArrayList<>();
                    mLocationList.add(ocation);
                    //得到纬度
                    double latitude = ocation.getLatitude();
                    //得到经度
                    double longitude = ocation.getLongitude();
                    float currentSpeed = ocation.getSpeed();
                    mTextViewLatitude.setText(latitude + "==");
                    mTextViewLongitude.setText(longitude + "==");
                    mTextViewCurrentSpeed.setText(currentSpeed + "==");
                    mTime = ocation.getTime();
                    isShow = false;
                } else {
                    float distanceTo = ocation.distanceTo(mLocationList.get(mLocationList.size() - 1));
                    if (distanceTo >= 0.1 && distanceTo < 10) {
                        mDistance += distanceTo;
                    } else if (distanceTo > 10) {
                        mDistance += 7;
                    }
                    //  得到纬度
                    double latitude = ocation.getLatitude();
                    //得到经度
                    double longitude = ocation.getLongitude();
                    float currentSpeed = ocation.getSpeed();
                    mTextViewLatitude.setText(latitude + "==" + "changed");
                    mTextViewLongitude.setText(longitude + "==" + "changed");
                    mTextViewCurrentSpeed.setText(currentSpeed + "==" + "changed");
                    mTextViewDistance.setText(mDistance + "");
                    mLocationList.add(ocation);
                    if (mDistance >= 100) {
                        mTextViewDistance.setText(mDistance + "");
                        long time = ocation.getTime();
                        long l = (time - mTime) / 1000;
                        float v = mDistance / l;
                        mTextViewAspeed.setText(v + "");
                        float v1 = (float) (100.00 / v);
                        mTextViewTime.setText(v1 + "");
                        if (mListener != null) {
                            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            locationManager.removeUpdates(mListener);
                            locationManager.removeGpsStatusListener(listener);
                            mListener = null;
                        }
                    }
                }
            }
        }

        @Override
        public void onStatusChanged(String s, int status, Bundle bundle) {
            Toast.makeText(MainActivity.this, "statusChanged" + "==" + s + "int==" + status, Toast.LENGTH_SHORT).show();
            //        if (!s.equals("gps")) {
            //            mImageView.setVisibility(View.VISIBLE);
            //        } else {
            //            isGps = true;
            //            mImageView.setVisibility(View.GONE);
            //        }
            switch (status) {
                //GPS状态为可见时
                case LocationProvider.AVAILABLE:
                    Log.i("tagg", "当前GPS状态为可见状态");
                    //Toast.makeText(MainActivity.this, "当前GPS状态为可见状态", Toast.LENGTH_SHORT).show();
                    break;
                //GPS状态为服务区外时
                case LocationProvider.OUT_OF_SERVICE:
                    Log.i("tagg", "当前GPS状态为服务区外状态");
                    //Toast.makeText(MainActivity.this, "当前GPS状态为服务区外状态", Toast.LENGTH_SHORT).show();
                    break;
                //GPS状态为暂停服务时
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.i("tagg", "当前GPS状态为暂停服务状态");
                    //  Toast.makeText(MainActivity.this, "当前GPS状态为暂停服务状态", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void onProviderEnabled(String s) {
            Toast.makeText(MainActivity.this, "enabled" + s, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(String s) {
            //        if (location != null) {
            //            //得到纬度
            //            double latitude = location.getLatitude();
            //            //得到经度
            //            double longitude = location.getLongitude();
            //            mTextViewPosition.setText(latitude + "=" + longitude + "s");
            //        }
            //   mTextViewPosition.setText(s);
            Toast.makeText(MainActivity.this, "disEnbaled" + s, Toast.LENGTH_SHORT).show();
        }
    }
}
