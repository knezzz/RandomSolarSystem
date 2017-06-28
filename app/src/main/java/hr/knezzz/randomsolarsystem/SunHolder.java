package hr.knezzz.randomsolarsystem;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by knezzz on 07/04/16.
 */
class SunHolder extends RecyclerView.ViewHolder{
    final TextView sunInfo;
    final CardView sun;

    SunHolder(View itemView) {
        super(itemView);

        this.sunInfo = (TextView) itemView.findViewById(R.id.sunInfo);
        this.sun = (CardView) itemView.findViewById(R.id.sun);
    }
}