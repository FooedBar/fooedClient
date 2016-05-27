package com.finder.fooedbar.client;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.finder.fooedbar.FooedBarApplication;
import com.finder.fooedbar.R;
import com.finder.fooedbar.client.api.Restaurant;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

/**
 * Created by Daniel on 25/5/16.
 */

public class QrActivity extends AppCompatActivity {

    private SurfaceView cameraView;
    private BarcodeDetector qrDetector;
    private CameraSource cameraSource;
    private Handler toastHandler;
    private int restaurantId;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);


        setContentView(R.layout.qr_reader);
        initCamera();
    }

    private void initCamera() {
        qrDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();
        qrDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (detections.detectorIsOperational() && barcodes.size() != 0) {
                    Log.d("qr value", barcodes.valueAt(0).rawValue);
                    if (Restaurant.isValidId(barcodes.valueAt(0).rawValue)) {
                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        v.vibrate(500);
                        restaurantId = Integer.parseInt(barcodes.valueAt(0).rawValue);
                        new FetchRestaurantTask().execute();
                    } else {
                        QrActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast toast = Toast.makeText(QrActivity.this, "Invalid QR code", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });
                    }
                }

            }
        });
        IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
        boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

        if (hasLowStorage) {
            Toast.makeText(this, "Low Storage", Toast.LENGTH_LONG).show();
        } else if (!qrDetector.isOperational()) {
            Toast toast = Toast.makeText(this, "QR Code detector not operational", Toast.LENGTH_LONG);
            toast.show();
        } else {
            cameraSource = new CameraSource.Builder(this, qrDetector)
                    .build();
            cameraView = (SurfaceView) findViewById(R.id.camera_view);
            cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {
                        cameraSource.start(cameraView.getHolder());
                    } catch (SecurityException ie) {
                        Log.e("SECURITY EXCEPTION", ie.getMessage());
                    } catch (IOException e) {
                        Log.e("IO EXCEPTION", e.getMessage());
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    cameraSource.stop();
                }
            });
        }
    }

    class FetchRestaurantTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                Restaurant restaurant = new Restaurant(restaurantId, ((FooedBarApplication)getApplication()).getSessionID());
                return restaurant.getName();
            } catch (Exception e) {
                Log.e("error", e.getMessage());
                return "";
            }
        }

        @Override
        protected void onPostExecute(final String name) {
            if (name == "") {
                QrActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast toast = Toast.makeText(QrActivity.this, "Could not get restaurant", Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
            } else {
                Intent intent = new Intent(QrActivity.this, MenuSuggestionsActivity.class);
                intent.putExtra("restaurant_id", restaurantId);
                intent.putExtra("restaurant_name", name);
                startActivity(intent);
                finish();
            }
        }
    }

}
