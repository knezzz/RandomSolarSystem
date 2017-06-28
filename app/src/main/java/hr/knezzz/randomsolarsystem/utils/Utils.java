package hr.knezzz.randomsolarsystem.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

import java.math.BigInteger;
import java.util.Random;

/**
 * Utils class
 * Class for all the thing that are needed across the app.
 * Created by knezzz on 28/03/16.
 */
public class Utils {
    private static int screenWidth = 0;
    private static int screenHeight = 0;
    private static final int SCREEN_MARGIN = 120;

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int getPlanetColor(BigInteger planetLocation){
        return Color.rgb(getPlanetColorRed(planetLocation), getPlanetColorGreen(planetLocation), getPlanetColorBlue(planetLocation));
    }

    public static int getPlanetColorRed(BigInteger planetLocation){
        String colorThem = planetLocation.toString();
        int redColor;

        // Find better way to handle numbers that are too large for integer. (java.lang.NumberFormatException: Invalid int: "9999999901")
        if (colorThem.length() > 3) {
            int stringLength;
            stringLength = (int) Math.floor(colorThem.length() / 3);

            long red = Long.parseLong(colorThem.substring(0, stringLength));
            long green = Long.parseLong(colorThem.substring(stringLength, stringLength*2));
            long blue = Long.parseLong(colorThem.substring(stringLength*2));

            Random redSeed = new Random(red);
            Random greenSeed = new Random(green);
            Random blueSeed = new Random(blue);

            //255 /2 = 129 /2 63 /2 32
            redColor = redSeed.nextInt(129) + (redSeed.nextBoolean()?blueSeed.nextInt(129):greenSeed.nextInt(129));
        } else {
            Random singleSeed = new Random(Integer.parseInt(colorThem));
            redColor = singleSeed.nextInt(255);
        }

        return redColor;
    }

    public static int getPlanetColorGreen(BigInteger planetLocation){
        String colorThem = planetLocation.toString();

        int greenColor;

        // Find better way to handle numbers that are too large for integer. (java.lang.NumberFormatException: Invalid int: "9999999901")
        if (colorThem.length() > 3) {
            int stringLength;

            stringLength = (int) Math.floor(colorThem.length() / 3);

            long red = Long.parseLong(colorThem.substring(0, stringLength));
            long green = Long.parseLong(colorThem.substring(stringLength, stringLength*2));
            long blue = Long.parseLong(colorThem.substring(stringLength*2));

            Random redSeed = new Random(red);
            Random greenSeed = new Random(green);
            Random blueSeed = new Random(blue);

            //255 /2 = 129 /2 63 /2 32
            greenColor = greenSeed.nextInt(129) + (greenSeed.nextBoolean()?redSeed.nextInt(129):blueSeed.nextInt(129));
        } else {
            Random singleSeed = new Random(Integer.parseInt(colorThem));
            greenColor = singleSeed.nextInt(255);
        }

        return greenColor;
    }

    public static int getPlanetColorBlue(BigInteger planetLocation){
        String colorThem = planetLocation.toString();

        int blueColor;

        // Find better way to handle numbers that are too large for integer. (java.lang.NumberFormatException: Invalid int: "9999999901")
        if (colorThem.length() > 3) {
            int stringLength;

            stringLength = (int) Math.floor(colorThem.length() / 3);

            long red = Long.parseLong(colorThem.substring(0, stringLength));
            long green = Long.parseLong(colorThem.substring(stringLength, stringLength*2));
            long blue = Long.parseLong(colorThem.substring(stringLength*2));

            Random redSeed = new Random(red);
            Random greenSeed = new Random(green);
            Random blueSeed = new Random(blue);

            //255 /2 = 129 /2 63 /2 32
            blueColor = blueSeed.nextInt(129) + (blueSeed.nextBoolean()?greenSeed.nextInt(129):redSeed.nextInt(129));
        } else {
            Random singleSeed = new Random(Integer.parseInt(colorThem));
            blueColor = singleSeed.nextInt(255);
        }

        return blueColor;
    }

    public static int getScreenHeight(Context c) {
        if (screenHeight == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenHeight = size.y - SCREEN_MARGIN;
        }

        return screenHeight;
    }

    public static int getScreenWidth(Context c) {
        if (screenWidth == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenWidth = size.x - SCREEN_MARGIN;
        }

        return screenWidth;
    }

    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }
}
