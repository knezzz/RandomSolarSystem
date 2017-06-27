package hr.knezzz.randomsolarsystem;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by knezzz on 07/04/16.
 */
public class SunHolder extends RecyclerView.ViewHolder{
    public View rootView;
    public TextView sunInfo;
    public CardView sun;

    public SunHolder(View itemView) {
        super(itemView);

        this.rootView = itemView;
        this.sunInfo = (TextView) rootView.findViewById(R.id.sunInfo);
        this.sun = (CardView) rootView.findViewById(R.id.sun);
    }
}