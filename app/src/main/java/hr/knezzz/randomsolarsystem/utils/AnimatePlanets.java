package hr.knezzz.randomsolarsystem.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.CardView;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Iterator;

import hr.knezzz.randomsolarsystem.Planet;

/**
 * Created by knezzz on 25/04/16.
 *
 * Util class for animating planets.
 */
public class AnimatePlanets extends Thread{
    private RelativeLayout solarCanvas;
    private Context context;
    private ArrayList<PlanetView> planets;
    private int positionX;
    private int positionY;
    private ArrayList<CardView> allViews = new ArrayList<>();

    public AnimatePlanets(){
    }

    public void prepareForAnimation(RelativeLayout solarCanvas, Context context, ArrayList<PlanetView> planets, int x, int y){
        this.solarCanvas = solarCanvas;
        this.context = context;
        this.planets = planets;
        this.positionX = x;
        this.positionY = y;
        allViews = new ArrayList<>();

        solarCanvas.setClipChildren(false);
    }

    /**
     *
     The points on a circle can be defined by the functions:

     x = a + r cos(θ)
     y = b + r sin(θ)
     Where (a,b) is the center of the circle.

     Depending on the speed you desire, you can say that you want a full circle to happen every T seconds. And if t is the time since the animation began:

     θ = (360 / T) * (t % T)
     You can use these functions to create your own ViewAnimation, OpenGL function, or if you're using canvas, to set the position of the paddle during the onDraw() event.
     */
    public void startAnimation() {
        for (PlanetView pv : planets) {
            Planet p = pv.getPlanet();
            int y = positionY - 100 - (p.getModelSize(true) / 2);
            int x = positionX - (p.getModelSize(true) / 2);
            double timePosition = ((double)360 / (double)p.getPlanetYear()) * (p.getRotationLocation() % p.getPlanetYear());
            final float calcX = (float)(x + (float) p.getSunDistance() * Math.cos(timePosition));
            final float calcY = (float)(y + (float) p.getSunDistance() * Math.sin(timePosition));


            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//sunModel.getLayoutParams());
            params.height = p.getModelSize(true);
            params.width = p.getModelSize(true);

          //  final ImageView planetView;
            final CardView simplePlanetView;
            Handler handler = new Handler(Looper.getMainLooper());

//            if(pv.getPlanetView() == null) {
//                planetView = new ImageView(context);
//                planetView.setImageBitmap(pv.getPlanetImage());
//                planetView.setX(calcX);
//                planetView.setY(calcY);
//                planetView.setLayoutParams(params);
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        solarCanvas.addView(planetView);
//                    }
//                });
//                allViews.add(planetView);
//                pv.setPlanetView(planetView);
//            }else{
//                planetView = pv.getPlanetView();
//            }

            if(pv.getSimplePlanetView() == null){
                simplePlanetView = new CardView(context);
                simplePlanetView.setCardBackgroundColor(p.getPlanetColor());
                simplePlanetView.setX(calcX);
                simplePlanetView.setY(calcY);
                simplePlanetView.setRadius(p.getModelSize(true)/2);
                simplePlanetView.setLayoutParams(params);
                handler.post(new Runnable(){
                    @Override
                    public void run(){
                        solarCanvas.addView(simplePlanetView);
                    }
                });
                allViews.add(simplePlanetView);
                pv.setSimplePlanetView(simplePlanetView);
            }else{
                simplePlanetView = pv.getSimplePlanetView();
            }

//            pv.generateTerrain(new PlanetView.TerrainGenerationDone(){
//                @Override
//                public void imageUpdate(Bitmap image){
//                    planetView.setImageBitmap(image);
//                }
//
//                @Override
//                public void imageDone(Bitmap image){
//                    planetView.setImageBitmap(image);
//                }
//            }, p.getModelSize(false), 2);

            handler.post(new Runnable() {
                @Override
                public void run() {
                    simplePlanetView.setX(calcX);
                    simplePlanetView.setY(calcY);
                }
            });
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();

        for(PlanetView p : planets) {
            p.setPlanetView(null);
        }

        Iterator<CardView> viewIterator = allViews.iterator();
        if(viewIterator.hasNext()){
            do{
                solarCanvas.removeView(viewIterator.next());
            }while(viewIterator.hasNext());
        }
    }

    public void resetAnimation(){
        solarCanvas = null;
        context = null;
        planets.clear();
        planets = new ArrayList<>();
        allViews.clear();
        allViews = new ArrayList<>();

        positionX = 0;
        positionY = 0;
    }

    public void hidePlanets() {
        for(CardView view : allViews){
            view.animate()
                .alpha(0)
                .setDuration(800)
                .start();
        }
    }
}
