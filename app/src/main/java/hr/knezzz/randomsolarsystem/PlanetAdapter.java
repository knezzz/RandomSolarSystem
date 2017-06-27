package hr.knezzz.randomsolarsystem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import hr.knezzz.randomsolarsystem.utils.AnimatePlanets;
import hr.knezzz.randomsolarsystem.utils.Resources;

/**
 * Created by knezzz on 07/04/16.
 */
public class PlanetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<Planet> planets = new ArrayList<>();
    private Context context;
    private Sun sun;
    private CardView sunView;
    private static final int SUN = 0;
    private static final int PLANET = 1;
    private int headerLayout;
    private RelativeLayout solarCanvas;
    private Activity activity;
    private AnimatePlanets animatePlanets;
    ScheduledExecutorService scheduledAnimationExecutor;


    public PlanetAdapter(Activity activity, Context context, int headerLayout, ArrayList<Planet> planets, Sun sun, RelativeLayout solarCanvas){
        this.activity = activity;
        this.context = context;
        this.planets = planets;
        this.sun = sun;
        this.solarCanvas = solarCanvas;
        this.headerLayout = headerLayout;

        animatePlanets = new AnimatePlanets();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == SUN){
            View layoutView = LayoutInflater.from(context).inflate(R.layout.solarsystem_header, parent, false);
            return new SunHolder(layoutView);
        }else if(viewType == PLANET){
            View layoutView = LayoutInflater.from(context).inflate(R.layout.planet_item, parent, false);
            return new PlanetHolder(layoutView);
        }else
            throw new RuntimeException("Could not inflate layout");
    }

    boolean toggle = true;

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        //Header
        if(position == SUN){
            SunHolder sunHolder = (SunHolder) holder;

            sunHolder.sunInfo.setText(String.format(Locale.getDefault(), "Type [%s]\nColor [%d]\nSize : [%s]\nMass : [%s]\nPlanets : [%s]", sun.getType(), sun.getSunColor(), sun.getSunSizeInKm(), sun.getSunMassInKg(), planets.size()));
            if(sunView == null) {
                Log.e("Sun", "Sun view is null....");
                sunView = new CardView(context);

                solarCanvas.addView(sunView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                solarCanvas.bringChildToFront(sunView);

                SolarMath.setUpListSun(sunView, sun);
                sunHolder.sun.setLayoutParams(sunView.getLayoutParams());
                sunHolder.sun.setRadius(sun.getModelSize(false)/2);
                sunHolder.sun.setCardBackgroundColor(sun.getSunColor());

                sunView.setVisibility(View.INVISIBLE);
            }else{
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);//sunModel.getLayoutParams());
                params.height = sun.getModelSize(false);
                params.width = sun.getModelSize(false);
                params.topMargin = 100;
                params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);

                sunHolder.sun.setLayoutParams(params);
                sunHolder.sun.setRadius(sun.getModelSize(false)/2);
                sunHolder.sun.setCardBackgroundColor(sun.getSunColor());
            }

        }else {
            final int currPosition = position-1;
            final Planet currentPlanet = planets.get(currPosition);

            final PlanetHolder planetHolder = (PlanetHolder) holder;

            planetHolder.planet.setCardBackgroundColor(currentPlanet.getPlanetColor());
            planetHolder.planetName.setText(currentPlanet.getName());

            Drawable colorTrue = ContextCompat.getDrawable(context, R.drawable.shadow_true);
            Drawable colorFalse = ContextCompat.getDrawable(context, R.drawable.shadow_false);

            planetHolder.waterSwitch.setBackground(currentPlanet.getWater() ? colorTrue : colorFalse);
            planetHolder.simpleSwitch.setBackground(currentPlanet.getSimpleLife() ? colorTrue : colorFalse);
            planetHolder.complexSwitch.setBackground(currentPlanet.getComplexLife() ? colorTrue : colorFalse);

            planetHolder.planetInfo.setText(String.format("Distance from sun: %s", currentPlanet.getSunDistance()));
            planetHolder.planetMoreInfo.setText(String.format("Day duration: %s Year duration: %s", currentPlanet.getPlanetDay(), currentPlanet.getPlanetyear()));

            SolarMath.setUpPlanetList(currentPlanet.getListSize(), currentPlanet.getPlanetColor(), planetHolder.planet);

            planetHolder.touchListener.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    CardView card = (CardView)v.findViewById(R.id.planet_view);
                    Intent i = new Intent(context, PlanetActivity.class);

                    i.putExtra(Resources.PLANET_INTENT_WHOLE_PLANET, currentPlanet);
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(activity, card, "planetView");
                    context.startActivity(i, options.toBundle());
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == SUN)
            return SUN;
        else
            return PLANET;
    }

    public void toggleViews(){
        int height;
        final RelativeLayout canvasBackground;

        if(toggle) {
            height = solarCanvas.getBottom();
            sunView.setVisibility(View.VISIBLE);
            startPlanetAnimation(solarCanvas, context, planets, solarCanvas.getWidth()/2, solarCanvas.getHeight()/2);
            canvasBackground = (RelativeLayout) solarCanvas.findViewById(R.id.canvasBackground);
            canvasBackground.animate()
                    .alpha(1)
                    .setStartDelay(0)
                    .setDuration(200)
                    .start();
        }else{
            height = 100;
            animatePlanets.hidePlanets();
            canvasBackground = (RelativeLayout) solarCanvas.findViewById(R.id.canvasBackground);
            canvasBackground.animate()
                    .alpha(0)
                    .setDuration(200)
                    .setStartDelay(800)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            sunView.setVisibility(View.INVISIBLE);
                            stopPlanetAnimation();
                        }
                    })
                    .start();
        }

        SolarMath.animateSun(toggle, height, sun, sunView);
        notifyDataSetChanged();

        toggle = !toggle;
    }

    public void startPlanetAnimation(RelativeLayout solarCanvas, Context context, ArrayList<Planet> planets, int x, int y){
        animatePlanets.prepareForAnimation(solarCanvas, context, planets, x, y);
        scheduledAnimationExecutor  = Executors.newScheduledThreadPool(1);
        scheduledAnimationExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                animatePlanets.startAnimation();
            }
        }, 0, Resources.PLANET_ANIMATION_TIME, TimeUnit.MILLISECONDS);
    }

    public void stopPlanetAnimation(){
        scheduledAnimationExecutor.shutdown();
        animatePlanets.interrupt();
        animatePlanets = new AnimatePlanets();
    }

    @Override
    public int getItemCount() {
        return planets.size() + 1;
    }

    public void changeSolarSystem(ArrayList<Planet> planets, Sun sun){
        this.planets = planets;
        this.sun = sun;

        notifyDataSetChanged();
    }
}
