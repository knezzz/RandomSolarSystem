package hr.knezzz.randomsolarsystem;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import hr.knezzz.randomsolarsystem.utils.PlanetView;
import hr.knezzz.randomsolarsystem.utils.Resources;
import hr.knezzz.randomsolarsystem.utils.Utils;


public class PlanetActivity extends AppCompatActivity {
    private static final String TAG = "PlanetActivity";
    private static int screenWidth;
    private TextView planetSize, planetTemperature, planetSatellites, goldilocksText, waterText, simpleLifeText, complexLifeText, planetDistanceText;
    private CardView planetModel;
    private ImageView planetImage;

    private PlanetView myPlanet;
    private Toolbar toolbar;
    private int planetColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planet);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent extra = getIntent();

        if(extra.hasExtra(Resources.PLANET_INTENT_WHOLE_PLANET)){
            myPlanet = (PlanetView) extra.getExtras().get(Resources.PLANET_INTENT_WHOLE_PLANET);
        }

        if(myPlanet == null){
            Log.e("PlanetActivity", "Error planet not specified");
            finish();
        }

        myPlanet.resetView(this);
        planetColor = myPlanet.getPlanet().getPlanetColor();
        setUpViews();
        setPlanetColor();
        showPlanetInfo();

        //noinspection ConstantConditions
        getSupportActionBar().setTitle(myPlanet.getPlanet().getName());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        myPlanet.stopRender();
    }

    private void showPlanetInfo() {
        planetSize.setText(String.format(Locale.getDefault(), "%d", myPlanet.getPlanet().getPlanetSize()));
        planetTemperature.setText(String.format(Locale.getDefault(), "%.2f", myPlanet.getPlanet().getTemperature()));
        planetSatellites.setText(String.format(Locale.getDefault(), "%d", myPlanet.getPlanet().getSatellites()));
        planetDistanceText.setText(String.format(Locale.getDefault(), "%.0f", myPlanet.getPlanet().getPlanetDistanceInKm()));

        colorBooleanTextField(goldilocksText, myPlanet.getPlanet().getGoldilocksZone());
        colorBooleanTextField(waterText, myPlanet.getPlanet().getWater());
        colorBooleanTextField(simpleLifeText, myPlanet.getPlanet().getSimpleLife());
        colorBooleanTextField(complexLifeText, myPlanet.getPlanet().getComplexLife());

        SolarMath.setUpPlanet(myPlanet.getPlanet(), planetModel);
        if(Resources.GENERATE_TERRAIN){
            drawPlanet(32);
        }
    }

    private void drawPlanet(final double quality){
        Thread planetThread = myPlanet.generateTerrain(new PlanetView.TerrainGenerationDone(){
            @Override
            public void imageUpdate(final Bitmap image){
                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        planetImage.setImageBitmap(image);
                    }
                });
            }

            @Override
            public void imageDone(final Bitmap image){
                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        planetImage.setImageBitmap(image);
                        if(quality >= .2){
                            drawPlanet(quality / 2);
                        }
                    }
                });
            }
        }, myPlanet.getPlanet().getModelSize(true)*2, quality);

        if(planetThread != null && !planetThread.isAlive()){
            planetThread.start();
        }
    }

    private void colorBooleanTextField(TextView tv, boolean bool){
        tv.setText(Boolean.toString(bool).toUpperCase());

        if(bool){
            tv.setTextColor(Color.GREEN);
        }else{
            tv.setTextColor(Color.RED);
        }
    }

    private void setUpViews() {
        planetSize = (TextView) findViewById(R.id.planet_info_size_text);
        planetTemperature = (TextView) findViewById(R.id.planet_info_temperature_text);
        planetSatellites = (TextView) findViewById(R.id.planet_info_satellites_text);
        goldilocksText = (TextView) findViewById(R.id.planet_info_goldilocks_text);
        waterText = (TextView) findViewById(R.id.planet_info_water_text);
        simpleLifeText = (TextView) findViewById(R.id.planet_info_life_text);
        complexLifeText = (TextView) findViewById(R.id.planet_info_complexlife_text);
        planetDistanceText = (TextView) findViewById(R.id.planet_info_distance_text);

        planetImage = (ImageView) findViewById(R.id.planet_image_view);
        planetModel = (CardView)findViewById(R.id.planet_model);

        if(Resources.GENERATE_TERRAIN){
            planetModel.setVisibility(View.GONE);
            planetImage.setMinimumHeight(getScreenWidth(this));
            planetImage.setMinimumWidth(getScreenWidth(this));
            planetImage.setScaleType(ImageView.ScaleType.FIT_XY);
            planetImage.setTransitionName("planetView" + myPlanet.getPlanet().getName());
        }else{
            planetImage.setVisibility(View.GONE);
            planetModel.setTransitionName("planetView" + myPlanet.getPlanet().getName());
            planetModel.setCardBackgroundColor(myPlanet.getPlanet().getPlanetColor());
        }
    }

    public static int getScreenWidth(Context c) {
        if (screenWidth == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenWidth = size.x;
        }

        return screenWidth;
    }

    private void setPlanetColor() {
        toolbar.setBackgroundColor(planetColor);

        if(Utils.isLollipop()) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            //Make color darker for status bar.
            float[] hsv = new float[3];
            Color.colorToHSV(planetColor, hsv);
            hsv[2] *= 0.75f;

            window.setStatusBarColor(Color.HSVToColor(hsv));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
