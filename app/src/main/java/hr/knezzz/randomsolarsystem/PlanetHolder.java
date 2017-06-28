package hr.knezzz.randomsolarsystem;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Holder class for Planet in recyclerView;
 *
 * Created by knezzz on 07/04/16.
 */
public class PlanetHolder extends RecyclerView.ViewHolder{
    private final View rootView;
    public final TextView planetName;
    public final TextView planetInfo;
    public final CardView planet;
    public final CardView waterSwitch;
    public final CardView simpleSwitch;
    public final CardView complexSwitch;
    public final ImageView planetImageView;
    public final RelativeLayout touchListener;

    public PlanetHolder(View itemView) {
        super(itemView);

        this.rootView = itemView;
        this.touchListener = (RelativeLayout) rootView.findViewById(R.id.planet_listener);
        this.planetName = (TextView) rootView.findViewById(R.id.planet_name);
        this.planetInfo = (TextView) rootView.findViewById(R.id.planet_info);

        this.planet = (CardView) rootView.findViewById(R.id.planet_view);
        this.planetImageView = (ImageView) rootView.findViewById(R.id.planet_view_terrain);
        this.waterSwitch = (CardView) rootView.findViewById(R.id.planet_water_switch);
        this.simpleSwitch = (CardView) rootView.findViewById(R.id.planet_simple_life_switch);
        this.complexSwitch = (CardView) rootView.findViewById(R.id.planet_complex_life_switch);
    }
}
