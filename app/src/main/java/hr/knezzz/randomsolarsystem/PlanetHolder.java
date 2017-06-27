package hr.knezzz.randomsolarsystem;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by knezzz on 07/04/16.
 */
public class PlanetHolder extends RecyclerView.ViewHolder{
    public View rootView;
    public TextView planetName, planetInfo, planetMoreInfo;
    public CardView planet;
    public View waterSwitch, simpleSwitch, complexSwitch;
    public RelativeLayout touchListener;

    public PlanetHolder(View itemView) {
        super(itemView);

        this.rootView = itemView;
        this.touchListener = (RelativeLayout) rootView.findViewById(R.id.planet_listener);
        this.planetName = (TextView) rootView.findViewById(R.id.planet_name);
        this.planetInfo = (TextView) rootView.findViewById(R.id.planet_info);
        this.planetMoreInfo = (TextView) rootView.findViewById(R.id.planet_more_info);

        this.planet = (CardView) rootView.findViewById(R.id.planet_view);
        this.waterSwitch = (View) rootView.findViewById(R.id.planet_water_shadow);
        this.simpleSwitch = (View) rootView.findViewById(R.id.planet_simple_life_shadow);
        this.complexSwitch = (View) rootView.findViewById(R.id.planet_complex_life_shadow);
    }
}
