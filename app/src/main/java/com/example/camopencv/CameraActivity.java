package com.example.camopencv;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.qrcode.QRCodeMultiReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CameraActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String TAG = "MainActivity";

    private Mat mRgba;
    private Integer flag = 0;

    List<JSONObject> listaFinal;
    JavaCameraView javaCameraView;
    List<StatusQr> lstDados;
    List<Qr> listsProducts;
    List<Qr> listaEndereco;


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface
                        .SUCCESS: {
                    Log.i(TAG, "OpenCv Is loaded");
                    javaCameraView.enableView();
                }
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    public CameraActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        lstDados = new ArrayList<>();

        int MY_PERMISSIONS_REQUEST_CAMERA = 0;

        if (ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(CameraActivity.this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
        }
        setContentView(R.layout.activity_camera);
        javaCameraView = (JavaCameraView) findViewById(R.id.frame_Surface);
        javaCameraView.setCameraIndex(0);
        javaCameraView.setVisibility(SurfaceView.VISIBLE);
        javaCameraView.setCvCameraViewListener(CameraActivity.this);
        javaCameraView.enableFpsMeter();


        QrAdapter qrAdapter = new QrAdapter(this, lstDados);
        RecyclerView recyclerView = findViewById(R.id.RecyView);
        recyclerView.setLayoutManager(new LinearLayoutManager(CameraActivity.this));
        recyclerView.setAdapter(qrAdapter);
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (OpenCVLoader.initDebug()) {
            Log.d(TAG, "Opencv initialization is done");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        } else {
            Log.d(TAG, "Opencv is not loaded. try again");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (javaCameraView != null) {
            javaCameraView.disableView();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (javaCameraView != null) {
            javaCameraView.disableView();
        }
    }

    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
    }

    public void onCameraViewStopped() {
        mRgba.release();
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();

        if (flag == 0) {
            Bitmap bMap = Bitmap.createBitmap(mRgba.width(), mRgba.height(), Bitmap.Config.ARGB_4444);
            Utils.matToBitmap(mRgba, bMap);
            int[] intArray = new int[bMap.getWidth() * bMap.getHeight()];
            bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());
            LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            List<Qr> listaProduto = new ArrayList<>();
            List<Qr> listaEndereco = new ArrayList<>();
            List<JSONObject> listaFinal = new ArrayList<>();

            try {
                Map<DecodeHintType, String> hints = new HashMap<>();
                hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
                Result[] result = new QRCodeMultiReader().decodeMultiple(bitmap, hints);
                if (result.length > 0) {
                    flag = 1;
                }

                for (Result kp : result) {
                    ResultPoint[] points = kp.getResultPoints();
                    Imgproc.putText(mRgba, kp.getText(), new Point(points[1].getX(), points[1].getY() - 50), Core.FONT_HERSHEY_COMPLEX, 1.0, new Scalar(0, 255, 0), 3, Imgproc.LINE_AA, false);
                    Imgproc.rectangle(mRgba, new Point(points[0].getX(), points[0].getY()), new Point(points[2].getX(), points[2].getY()), new Scalar(0, 0, 0, 0), 15);
                    if (kp.getText().contains("QR01")) {
                        Float x = points[0].getX();
                        Float y = points[0].getY();
                        listaProduto.add(new Qr(x, y, kp.getText()));
                    } else if (kp.getText().contains("QR02")) {
                        Float x = points[0].getX();
                        Float y = points[0].getY();
                        listaEndereco.add(new Qr(x, y, kp.getText()));
                    }
                }

                for (int i = 0; i < listaProduto.size(); i++) {

                    Map<String, String> map = new HashMap<>();
                    map.put("endereco", "");
                    map.put("produto", listaProduto.get(i).getResultado());
                    map.put("status", "");
                    Double xp = listaProduto.get(i).getX();
                    Double yp = listaProduto.get(i).getY();
                    Double distanciaTemp = null;
                    String enderecoTemp = null;
                    String produtoTemp = listaProduto.get(i).getResultado();
                    int indexTemp = 0;

                    for (int j = 0; j < listaEndereco.size(); j++) {
                        if (j == 0) {
                            Double xe = listaEndereco.get(j).getX();
                            Double ye = listaEndereco.get(j).getY();
                            enderecoTemp = listaEndereco.get(j).getResultado();
                            distanciaTemp = Math.pow(((xe - xp) * (xe - xp) + (ye - yp) * (ye - yp)), 0.5);
                            indexTemp = j;

                        } else {
                            Double xe = listaEndereco.get(j).getX();
                            Double ye = listaEndereco.get(j).getY();
                            double pow = Math.pow(((xe - xp) * (xe - xp) + (ye - yp) * (ye - yp)), 0.5);
                            if (pow < distanciaTemp) {
                                enderecoTemp = listaEndereco.get(j).getResultado();
                                distanciaTemp = pow;
                                indexTemp = j;
                            }
                        }
                    }

                    if (enderecoTemp != null) {
                        if (produtoTemp.contains(enderecoTemp.replaceFirst("QR02", ""))) {
                            map.put("status", "ok");
                        } else {
                            map.put("status", "erro");

                        }
                        map.put("endereco", enderecoTemp);
                        listaEndereco.remove(indexTemp);
                        JSONObject jo = new JSONObject(map);
                        listaFinal.add(jo);
                    }
                }
                if (listaEndereco.size() > 0) {
                    for (int j = 0; j < listaEndereco.size(); j++) {
                        Map<String, String> map = new HashMap<>();
                        map.put("endereco", listaEndereco.get(j).getResultado());
                        map.put("produto", "");
                        map.put("status", "Nao Leu");
                        JSONObject jo = new JSONObject(map);
                        listaFinal.add(jo);
                    }
                }

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {



                    }
                });


                JSONArray jsonArray = new JSONArray(listaFinal);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    StatusQr statusQr = new StatusQr();
                    statusQr.setEndereco(jsonObject.get("endereco").toString());
                    statusQr.setProduto(jsonObject.get("produto").toString());
                    statusQr.setStatus(jsonObject.get("status").toString());
                    lstDados.add(statusQr);
                }

                Thread thread = new Thread(){
                    @Override
                    public void run() {
                        try {
                            synchronized (this) {
                                wait(5000);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        QrAdapter qrAdapter = new QrAdapter(CameraActivity.this, lstDados);
                                        RecyclerView recyclerView = findViewById(R.id.RecyView);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(CameraActivity.this));
                                        recyclerView.setAdapter(qrAdapter);
                                        recyclerView.scrollToPosition(lstDados.size() - 1);
                                    }
                                });
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    };
                };
                flag = 0;
                thread.start();

            } catch (NotFoundException | JSONException e) {
                e.printStackTrace();
            }
        }

        return mRgba;
    }

}