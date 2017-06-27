package hr.knezzz.randomsolarsystem;

import android.support.v7.widget.CardView;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by knezzz on 07/04/16.
 *
 * Util class that calculates sun animations
 */
public class SolarMath {
    public SolarMath(){
    }

    public static void animateSun(boolean expand, int marginTop, Sun sun, final CardView sunView) {
        int sunSizeList = sun.getModelSize(true);
        int sunSizeModel = sun.getModelSize(false);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//sunModel.getLayoutParams());
        params.height = sunSizeModel;
        params.width = sunSizeModel;
        params.topMargin = 100;
        params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);

        sunView.setLayoutParams(params);
        sunView.setRadius(sunSizeModel/2);
        sunView.setCardBackgroundColor(sun.getSunColor());

        double scale = (double) sunSizeList / (double) sunSizeModel;

        if(!expand){
            sunView.animate()
                    .y(marginTop)
                    .scaleX((float)(1))
                    .scaleY((float)(1))
                    .setDuration(600)
                    .start();
        }else{
            sunView.animate()
                    .y(((marginTop - sunSizeModel) / 2) - 100)
                    .scaleX((float) (scale))
                    .scaleY((float) (scale))
                    .setStartDelay(200)
                    .setDuration(600)
                    .start();
        }
    }

    public static void setUpPlanet(Planet planet, CardView planetView){
        int planetSize = planet.getModelSize(false);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(planetView.getLayoutParams());
        params.height = planetSize;
        params.width = planetSize;
        params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);

        planetView.setLayoutParams(params);
        planetView.setRadius(planetSize/2);
        //   planetModel.setZ(measuredSize/100);
        planetView.setCardBackgroundColor(planet.getPlanetColor());
    }

    public static void setUpPlanetList(int size,int color, CardView planetView){
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(planetView.getLayoutParams());
        params.height = size;
        params.width = size;
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        planetView.setLayoutParams(params);
        planetView.setRadius(size/2);
        //   planetModel.setZ(measuredSize/100);
        planetView.setCardBackgroundColor(color);
    }

    public static void setUpListSun(CardView sunView, Sun sun){
        int sunSize = sun.getModelSize(false);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);//sunModel.getLayoutParams());
        params.height = sunSize;
        params.width = sunSize;
        params.topMargin = 100;
        params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);

        sunView.setLayoutParams(params);
        sunView.setRadius(sunSize/2);
        sunView.setCardBackgroundColor(sun.getSunColor());

        sunView.animate()
                .alpha(0)
                .alphaBy(1)
                .scaleX((float)(1))
                .scaleY((float)(1))
                .setDuration(1000)
                .start();
    }
}
