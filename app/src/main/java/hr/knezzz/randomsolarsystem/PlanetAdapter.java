package hr.knezzz.randomsolarsystem;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import hr.knezzz.randomsolarsystem.utils.AnimatePlanets;
import hr.knezzz.randomsolarsystem.utils.PlanetView;
import hr.knezzz.randomsolarsystem.utils.Resources;

/**
 * Created by knezzz on 07/04/16.
 */
class PlanetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<PlanetView> planetViews = new ArrayList<>();
    private final Context context;
    private Sun sun;
    private CardView sunView;
    private static final int SUN = 0;
    private static final int PLANET = 1;
    private final RelativeLayout solarCanvas;
    private final Activity activity;
    private AnimatePlanets animatePlanets;
    private ScheduledExecutorService scheduledAnimationExecutor;
    private final RelativeLayout canvasBackground;
    private ThreadPoolExecutor threadPoolExecutor;

    /*
     * Gets the number of available cores
     * (not always the same as the maximum number of cores)
     */
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    // Sets the amount of time an idle thread waits before terminating
    private static final int KEEP_ALIVE_TIME = 1;
    // Sets the Time Unit to seconds
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

    private final BlockingQueue<Runnable> decodeWorkQueue;


    public PlanetAdapter(Activity activity, Context context, ArrayList<PlanetView> planets, Sun sun, RelativeLayout solarCanvas){
        this.activity = activity;
        this.context = context;
        this.sun = sun;
        this.solarCanvas = solarCanvas;
        this.planetViews = planets;

        decodeWorkQueue = new LinkedBlockingQueue<Runnable>();
        threadPoolExecutor = new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, decodeWorkQueue);
        animatePlanets = new AnimatePlanets();
        canvasBackground = (RelativeLayout) solarCanvas.findViewById(R.id.canvasBackground);
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

    private boolean toggle = true;

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        //Header
        if(position == SUN){
            SunHolder sunHolder = (SunHolder) holder;

            sunHolder.sunInfo.setText(String.format(Locale.getDefault(), "Type [%s]\nColor [%d]\nSize (Solar Radius): [%s]\nMass (Solar Mass): [%s]\nPlanets : [%s]", sun.getType(), sun.getSunColor(), sun.getSunSize(), sun.getSunMass(), planetViews.size()));
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

        }else{
            final int currPosition = position-1;
            final PlanetView currentPlanet = planetViews.get(currPosition);
            final PlanetHolder planetHolder = (PlanetHolder) holder;
            currentPlanet.resetView(activity);
            currentPlanet.setUpView(planetHolder);

            if(Resources.GENERATE_TERRAIN){
                Thread planetGeneration = null;

                if(currentPlanet.getPlanetImage() != null){
                    planetHolder.planetImageView.setImageBitmap(currentPlanet.getPlanetImage());
                }

                if(currentPlanet.getBestRenderQuality() == 500){
                    planetGeneration = currentPlanet.generateTerrain(new PlanetView.TerrainGenerationDone(){
                        @Override
                        public void imageUpdate(final Bitmap image){
                            activity.runOnUiThread(new Runnable(){
                                @Override
                                public void run(){
                                    planetHolder.planetImageView.setImageBitmap(image);
                                }
                            });
                        }

                        @Override
                        public void imageDone(final Bitmap image){
                            activity.runOnUiThread(new Runnable(){
                                @Override
                                public void run(){
                                    planetHolder.planetImageView.setImageBitmap(image);
                                    notifyItemChanged(holder.getAdapterPosition());
                                }
                            });
                        }
                    }, currentPlanet.getPlanet().getModelSize(true) * 2, 32);
                }else if(currentPlanet.getBestRenderQuality() > 4){
                    planetGeneration = currentPlanet.generateTerrain(new PlanetView.TerrainGenerationDone(){
                        @Override
                        public void imageUpdate(final Bitmap image){
                            activity.runOnUiThread(new Runnable(){
                                @Override
                                public void run(){
                                    planetHolder.planetImageView.setImageBitmap(image);
                                }
                            });
                        }

                        @Override
                        public void imageDone(final Bitmap image){
                            activity.runOnUiThread(new Runnable(){
                                @Override
                                public void run(){
                                    planetHolder.planetImageView.setImageBitmap(image);
                                    notifyItemChanged(holder.getAdapterPosition());
                                }
                            });
                        }
                    }, currentPlanet.getPlanet().getModelSize(true) * 2, currentPlanet.getBestRenderQuality() / 2);
                }
                
                if(planetGeneration != null){
                    threadPoolExecutor.execute(planetGeneration);
                }
            }else{
                planetHolder.planetImageView.setVisibility(View.GONE);
                planetHolder.planet.setVisibility(View.VISIBLE);
            }
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

        if(toggle) {
            height = solarCanvas.getBottom();
            sunView.setVisibility(View.VISIBLE);
            startPlanetAnimation(solarCanvas, context, planetViews, solarCanvas.getWidth()/2, solarCanvas.getHeight()/2);
            canvasBackground.animate()
                    .alpha(1)
                    .setStartDelay(0)
                    .setDuration(200)
                    .start();
        }else{
            height = 100;
            animatePlanets.hidePlanets();
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


    @Deprecated
    public void showModel(){
        animatePlanets.interrupt();
        animatePlanets.resetAnimation();
        solarCanvas.removeAllViews();
        sunView = new CardView(context);

        int height = solarCanvas.getBottom();
        SolarMath.setUpListSun(sunView, sun);
        solarCanvas.addView(sunView);

        startPlanetAnimation(solarCanvas, context, planetViews, solarCanvas.getWidth()/2, solarCanvas.getHeight()/2);
        SolarMath.animateSun(toggle, height, sun, sunView);

        notifyDataSetChanged();
    }

    private void startPlanetAnimation(RelativeLayout solarCanvas, Context context, ArrayList<PlanetView> planets, int x, int y){
        animatePlanets.prepareForAnimation(solarCanvas, context, planets, x, y);
        scheduledAnimationExecutor  = Executors.newScheduledThreadPool(1);
        scheduledAnimationExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                animatePlanets.startAnimation();
            }
        }, 0, Resources.PLANET_ANIMATION_TIME, TimeUnit.MILLISECONDS);
    }

    private void stopPlanetAnimation(){
        scheduledAnimationExecutor.shutdown();
        animatePlanets.interrupt();
        animatePlanets = new AnimatePlanets();
    }

    @Override
    public int getItemCount() {
        return planetViews.size() + 1;
    }

    public void changeSolarSystem(ArrayList<PlanetView> planets, Sun sun){
        this.planetViews = planets;
        this.sun = sun;

        notifyDataSetChanged();
    }
}
