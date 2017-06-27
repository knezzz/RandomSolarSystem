package hr.knezzz.randomsolarsystem.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.CardView;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import hr.knezzz.randomsolarsystem.Planet;

/**
 * Created by knezzz on 25/04/16.
 *
 * Util class for animating planets.
 */
public class AnimatePlanets extends Thread{
    boolean isAnimating = false;
    RelativeLayout solarCanvas;
    Context context;
    ArrayList<Planet> planets;
    int positionX;
    int positionY;
    ArrayList<CardView> allViews = new ArrayList<>();

    public AnimatePlanets(){
    }

    public void prepareForAnimation(RelativeLayout solarCanvas, Context context, ArrayList<Planet> planets, int x, int y){
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
        isAnimating = true;

        for (Planet p : planets){
            p.startAnimation();
        }

        //while(canAnimate()){
        for (Planet p : planets) {
            int y = positionY - 100 - (p.getModelSize(true) / 2);
            int x = positionX - (p.getModelSize(true) / 2);
            double timePosition = ((double)360 / (double)p.getPlanetyear()) * (p.getRotationLocation() % p.getPlanetyear());
            final float calcX = (float)(x + (float) p.getSunDistance() * Math.cos(timePosition));
            final float calcY = (float)(y + (float) p.getSunDistance() * Math.sin(timePosition));


            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//sunModel.getLayoutParams());
            params.height = p.getModelSize(true);
            params.width = p.getModelSize(true);

            final CardView planetView;
            Handler handler = new Handler(Looper.getMainLooper());

            if(p.getPlanetView() == null) {
                planetView = new CardView(context);
                planetView.setRadius(p.getModelSize(true) / 2);
                planetView.setCardBackgroundColor(p.getPlanetColor());
                planetView.setX(calcX);
                planetView.setY(calcY);
                planetView.setLayoutParams(params);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        solarCanvas.addView(planetView);
                    }
                });
                allViews.add(planetView);
                p.setPlanetView(planetView);
            }else{
                planetView = p.getPlanetView();
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    planetView.setX(calcX);
                    planetView.setY(calcY);
                }
            });
        }
    }

   /* public boolean canAnimate(){
        return isAnimating;
    }*/

    @Override
    public void interrupt() {
        super.interrupt();

        isAnimating = false;
        for(Planet p : planets) {
            p.setPlanetView(null);
        }

        for(CardView view : allViews){
            solarCanvas.removeView(view);
        }
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
