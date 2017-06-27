package hr.knezzz.randomsolarsystem;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Locale;

import hr.knezzz.randomsolarsystem.utils.Resources;
import hr.knezzz.randomsolarsystem.utils.Utils;


public class PlanetActivity extends AppCompatActivity {
    private TextView planetSize, planetTemperature, planetSatellites, goldilocksText, waterText, simpleLifeText, complexLifeText;
    private CardView planetModel;

    private Planet myPlanet;
    private Toolbar toolbar;
    private int planetColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        LeakCanary.install(getApplication());
        setContentView(R.layout.activity_planet);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent extra = getIntent();

        if(extra.hasExtra(Resources.PLANET_INTENT_WHOLE_PLANET)){
            myPlanet = (Planet) extra.getExtras().get(Resources.PLANET_INTENT_WHOLE_PLANET);
        }

        if(myPlanet == null){
            Log.e("PlanetActivity", "Error planet not specified");
            finish();
        }

        planetColor = myPlanet.getPlanetColor();
        setUpViews();
        setPlanetColor();
        showPlanetInfo();

        getSupportActionBar().setTitle(myPlanet.getName());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void showPlanetInfo() {
        planetSize.setText(String.format(Locale.getDefault(), "%d", myPlanet.getPlanetSize()));
        planetTemperature.setText(String.format(Locale.getDefault(), "%d", myPlanet.getTemperature()));
        planetSatellites.setText(String.format(Locale.getDefault(), "%d", myPlanet.getSatellites()));

        colorBooleanTextfield(goldilocksText, myPlanet.getGoldilocksZone());
        colorBooleanTextfield(waterText, myPlanet.getWater());
        colorBooleanTextfield(simpleLifeText, myPlanet.getSimpleLife());
        colorBooleanTextfield(complexLifeText, myPlanet.getComplexLife());

        SolarMath.setUpPlanet(myPlanet, planetModel);
       // setUpModel();
    }

    private void colorBooleanTextfield(TextView tv, boolean bool){
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

        planetModel = (CardView) findViewById(R.id.planet_model);
    }

    public void setPlanetColor() {
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
