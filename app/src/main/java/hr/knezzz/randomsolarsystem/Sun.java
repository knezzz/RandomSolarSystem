package hr.knezzz.randomsolarsystem;


import android.graphics.Color;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Random;

import hr.knezzz.randomsolarsystem.utils.Resources;

/**
 * Sun class
 */
class Sun{
    private final BigInteger sunSeed;
    private BigInteger sunMass;
    private int sunColor;
    private String name;
    private double size;
    private int sun_type;
    private final Random rand;
    private int numberOfPlanets;

    public Sun(BigInteger sunSeed){
        this.sunSeed = sunSeed;
        rand = new Random(sunSeed.intValue());
        findSun();
        findPlanets();
    }

    private void findPlanets(){
        int maxNumberOfPlanets = 3*(9-getIntType());
        int minNumberOfPlanets = (8-getIntType()) > 3 ? (8-getIntType()) : 3;
        numberOfPlanets = rand.nextInt(maxNumberOfPlanets) + minNumberOfPlanets;
    }

    public int getPlanetsCount(){
        return numberOfPlanets;
    }

    public static int getSunType(BigInteger sunId){
        return findType(new Random(sunId.intValue()).nextInt(10000000));
    }

    private void findSun(){
        sun_type = findType(rand.nextInt(10000000));

        switch(sun_type){
            case Resources.STAR_TYPE_O:
                //color : blue
                //weight : > 16M (solar mass) (16 - 24)
                //size : > 6.6 (solar radius)
                sunMass = new BigInteger("16000000").add(BigInteger.valueOf(rand.nextInt(8000000)));
                size = 6.6 + rand.nextInt(420)/100;
                int whiteColor_typeO = rand.nextInt(75);
                sunColor = Color.rgb(whiteColor_typeO+150, whiteColor_typeO+150, (rand.nextInt(55)+200));
                break;
            case Resources.STAR_TYPE_B:
                //color : blue white
                //weight : 2.1 - 16M (solar mass)
                //size : 1.8 - 6.6 (solar radius)
                sunMass = new BigInteger("2100000").add(BigInteger.valueOf(rand.nextInt(13900000)));
                size = rand.nextInt(480)/100 + 1.8;
                int whiteColor_typeB = rand.nextInt(200);

                sunColor = Color.rgb(whiteColor_typeB, whiteColor_typeB, (rand.nextInt(75)+130));
                break;
            case Resources.STAR_TYPE_A:
                //color : white
                //weight : 1.4 - 2.1 (solar mass)
                //size : 1.4 - 1.8 (solar radius)
                sunMass = new BigInteger("1400000").add(BigInteger.valueOf(rand.nextInt(700000)));
                size = rand.nextInt(40)/100 + 1.4;
                int whiteColor_typeA = rand.nextInt(75)+180;
                sunColor = Color.rgb(whiteColor_typeA, whiteColor_typeA, whiteColor_typeA);
                break;
            case Resources.STAR_TYPE_F:
                //color : yellow white
                //weight : 1.04 - 1.4 (solar mass)
                //size : 1.15 - 1.4 (solar radius)
                sunMass = new BigInteger("1040000").add(BigInteger.valueOf(rand.nextInt(360000)));
                size = rand.nextInt(25)/100 + 1.15;
                int yellowColor_typeF = rand.nextInt(156) + 100;
                int whiteColor_typeF = 0;

                if(yellowColor_typeF > 150) {
                    int whiteShade_typeF = rand.nextInt(6);
                    whiteColor_typeF = (whiteShade_typeF-1)*50;
                }

                sunColor = Color.rgb(yellowColor_typeF, yellowColor_typeF, whiteColor_typeF);
                break;
            case Resources.STAR_TYPE_G:
                //color : yellow
                //weight : 0.8 - 1.04 (solar mass)
                //size : 0.96 - 1.15 (solar radius)
                sunMass = new BigInteger("800000").add(BigInteger.valueOf(rand.nextInt(204000)));
                size = rand.nextInt(19)/100 + 0.96;
                int yellowColor_typeG = rand.nextInt(156) + 100;
                sunColor = Color.rgb(yellowColor_typeG, yellowColor_typeG, 0);
                break;
            case Resources.STAR_TYPE_K:
                //color : orange
                //weight : 0.45 - 0.8 (solar mass)
                //size : 0.7 - 0.96 (solar radius)
                sunMass = new BigInteger("450000").add(BigInteger.valueOf(rand.nextInt(350000)));
                size = rand.nextInt(26)/100 + 0.7;
                int orangeColor_typeK = rand.nextInt(106) + 150;
                sunColor = Color.rgb(orangeColor_typeK, orangeColor_typeK/2, 0);
                break;
            case Resources.STAR_TYPE_M:
                //color : red
                //weight : 0.08 - 0.45 (solar mass)
                //size : < 0.7 (solar radius)
                sunMass = new BigInteger("80000").add(BigInteger.valueOf(rand.nextInt(370000)));
                size = rand.nextInt(30)/100 + 0.4;
                sunColor = Color.rgb(rand.nextInt(156)+100, 0, 0);
                break;
        }
    }


    private static int findType(int typeSeed){
        int type;

        if(typeSeed < 3){ // Chances : 0.00003%  // There is still about 13,510,798,882,111,488,000,000 of them...
            type = Resources.STAR_TYPE_O;
        }else if(typeSeed < 13000){ // Chances : 0.13%
            type = Resources.STAR_TYPE_B;
        }else if(typeSeed < 60000){ // Chances : 0.6%
            type = Resources.STAR_TYPE_A;
        }else if(typeSeed < 300000){ // Chances : 3%
            type = Resources.STAR_TYPE_F;
        }else if(typeSeed < 760000){ // Chances : 7.6%
            type = Resources.STAR_TYPE_G;
        }else if(typeSeed < 1210000){ // Chances : 12.1%
            type = Resources.STAR_TYPE_K;
        }else{ // Chances : 76.45%
            type = Resources.STAR_TYPE_M;
        }

        return type;
    }

    public String getName(){
        return name;
    }

    /**
     * Get sun type (O, B, A, F, G, K, M)
     * @return - String type
     */
    public String getType(){
        String type;

        switch(sun_type){
            case Resources.STAR_TYPE_O:
                type = "O";
                break;
            case Resources.STAR_TYPE_B:
                type = "B";
                break;
            case Resources.STAR_TYPE_A:
                type = "A";
                break;
            case Resources.STAR_TYPE_F:
                type = "F";
                break;
            case Resources.STAR_TYPE_G:
                type = "G";
                break;
            case Resources.STAR_TYPE_K:
                type = "K";
                break;
            default:// Resources.STAR_TYPE_M:
                type = "M";
                break;
        }

        return type;
    }

    public int getIntType(){
        return sun_type;
    }

    /**
     * Get sun mass in solar mass
     * @return - sun mass
     */
    public BigInteger getSunMass(){
        return sunMass;
    }

    /**
     * 1 Solar Mass = 1.989 x 10^30 kg = 1989000000000000000000000000000 kg
     *
     * @return Mass of sun in KG.
     */
    public BigInteger getSunMassInKg(){
        BigInteger massInKg = new BigInteger("1989000000000000000000000000000").multiply(sunMass);
        return massInKg;
    }

    /**
     * Get sun size in solar radius
     * @return - size of sun
     */
    public double getSunSize(){
        return size;
    }
    /**
     * 1 Solar Radius = 695,700km
     *
     * @return Size of sun in Kilometers.
     */
    public BigDecimal getSunSizeInKm(){
        BigDecimal sizeInKm = new BigDecimal(size).multiply(new BigDecimal("695700"));
        return sizeInKm.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public int getSunColor(){
        return sunColor;
    }

    public int getModelSize(boolean smallModel){
        int minSunSize;
        int maxSunSize;

        if(smallModel){
            minSunSize = 120;
            maxSunSize = 200;
        }else{
            minSunSize = 400;
            maxSunSize = 1500;
        }

        double sunRatio = getSunSize() / Resources.BIGGEST_SUN;

        return (int)(((maxSunSize-minSunSize) * sunRatio) + minSunSize);
    }

    @Override
    public String toString(){
        return String.format("Sun type [%s]\nSun size (Solar Radius) [%.2f]\nSun mass (Solar Mass) [%s]\nNumber of planets [%d]", getType(), getSunSize(), getSunMass(), getPlanetsCount());
    }
}
