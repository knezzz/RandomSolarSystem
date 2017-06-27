package hr.knezzz.randomsolarsystem;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.Locale;

/**
 * Created by knezzz on 27/04/16.
 * Scanner for QR of solarSystem. Once scanned starts new solar system with specified coordinates.
 */
public class QRScanner extends AppCompatActivity {
    private SurfaceView cameraView;
    private TextView barcodeInfo;
    private static final int CAMERA_PREMISSION = 42;

    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_scanner);

        cameraView = (SurfaceView) findViewById(R.id.camera_view);
        barcodeInfo = (TextView) findViewById(R.id.code_info);

        new Thread(new Runnable() {
            @Override
            public void run() {
                setUpSensors();
            }
        }).start();
    }

    private void setUpSensors() {
        Log.d(TAG, "Setting up sensors.");
        barcodeDetector =
                new BarcodeDetector.Builder(this)
                        .setBarcodeFormats(Barcode.QR_CODE)
                        .build();

        cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setAutoFocusEnabled(true)
                .build();

        startCamera();
    }

    private void startCamera() {
        Log.d(TAG, "Starting the camera..");
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(QRScanner.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        askForPermission();
                        return;
                    }
                    cameraSource.start(cameraView.getHolder());
                    Log.e("CAMERA STARTED", (cameraSource.getCameraFacing()==CameraSource.CAMERA_FACING_FRONT?"Front":"Back")+" " + cameraSource.getPreviewSize().getHeight()+ "x"+cameraSource.getPreviewSize().getWidth());
                    setBarcodeDetector();
                } catch (IOException | RuntimeException ie) {
                    Log.e("CAMERA FAILED", ie.getMessage());
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

    private void askForPermission() {
        String[] permissions = new String[]{Manifest.permission.CAMERA};
        ActivityCompat.requestPermissions(this, permissions, CAMERA_PREMISSION);
    }

    private void setBarcodeDetector(){
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() != 0) {
                    String value = barcodes.valueAt(0).rawValue;
                    boolean isValid = validateConfig(value);

                    if(isValid){
                        returnSeed(value);
                    }
                }
            }
        });
    }

    private void returnSeed(String seed) {
        Intent i = new Intent();//getIntent();
        i.putExtra("SEED", seed);
        setResult(RESULT_OK, i);
        finish();
       // finishActivityFromChild(this, SolarActivity.QR_CODE_REQUEST);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpSensors();
    }

    @Override
    protected void onStop() {
        super.onStop();
        cameraSource.release();
        barcodeDetector.release();
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PREMISSION) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (permission.equals(Manifest.permission.CAMERA)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        startCamera();
                    }else{
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PREMISSION);
                    }
                }
            }
        }
    }

    /**
     for(int i = 0; i < 256; i++){
     star += position.nextInt(Integer.MAX_VALUE);
     //  star += 1;
     }

     * @param config - qr code raw value
     * @return - return true if QR code is valid, false otherwise
     */

    private boolean validateConfig(final String config) {
        Log.d(TAG, "About to validate configuration from QR kod. Scanned: " + config);
        final String[] location = config.split(":");

        if(location.length > 3) {
            try{
                boolean isValid;
                final long _partOfUniverse = Long.parseLong(location[0]);
                final long _superCluster = Long.parseLong(location[1]);
                final long _cluster = Long.parseLong(location[2]);
                final long _galaxy = Long.parseLong(location[3]);
                final long _star = Long.parseLong(location[4]);

                isValid = (_partOfUniverse <= 100) && (_superCluster <= 163840) && (_cluster <= 50000)
                        && (_galaxy <= 1000) && (_star <= 549755813888L);

                if(isValid) {
                    barcodeInfo.post(new Runnable() {    // Use the post method of the TextView
                        public void run() {
                            barcodeInfo.setText(String.format(Locale.getDefault(), "Part of universe: %d\n" +
                                    "Supercluster: %d\n" +
                                    "Cluster: %d\n" +
                                    "Galaxy: %d\n" +
                                    "Star: %d", _partOfUniverse, _superCluster, _cluster, _galaxy, _star));
                        }
                    });

                    return true;
                }else{
                    barcodeInfo.post(new Runnable() {    // Use the post method of the TextView
                        public void run() {
                            barcodeInfo.setText(String.format("%s\n" +
                                    "%s\n" +
                                    "%s\n" +
                                    "%s\n" +
                                    "e:: %s", "Nice try.", "", "max limits: 100:163840:50000:1000:549755813888", "your value: "+config, "Value out of limits"));
                        }
                    });

                    return false;
                }
            }catch(Exception e){
                barcodeInfo.post(new Runnable() {    // Use the post method of the TextView
                    public void run() {
                        barcodeInfo.setText(String.format("%s%s", getString(R.string.wrong_qr), config));
                    }
                });

                return false;
            }
        }

        barcodeInfo.post(new Runnable() {    // Use the post method of the TextView
            public void run() {
                barcodeInfo.setText(String.format("%s%s", getString(R.string.wrong_qr), config));
            }
        });

        return false;
    }

}