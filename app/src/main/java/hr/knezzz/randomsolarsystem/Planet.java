package hr.knezzz.randomsolarsystem;

import android.support.v7.widget.CardView;
import android.util.Log;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Random;

import hr.knezzz.randomsolarsystem.utils.Resources;
import hr.knezzz.randomsolarsystem.utils.Utils;

/**
 * Created by knezzz on 30/03/16.
 */
public class Planet implements Serializable{
    private BigInteger planetSeed;
    private int smallSeed;
    private int ID_LENGTH;
    private int location;
    private transient CardView planetView;
    private long rotationLocation;

    private String name;

    Random randSeed;

    private boolean goldilocksZone; // 10% of all planets
    private boolean hasWater; //10% of planets in goldilocks zone
    private boolean hasLife; //All planets that have water and are in goldilocks zone
    private boolean hasComplexLife; //10% of planets that have simple life

    private int size;
    private long sunDistance;
    private int planetDay;
    private int planetYear;
    private int temperature;
    private int satellites;

    private int rotation;

    private static final String TAG = "Planet";

    public Planet(BigInteger seed){
        planetSeed = seed;
        ID_LENGTH = planetSeed.toString().length();

        smallSeed = (planetSeed.intValue());

        makePlanet();
    }

    private void makePlanet(){
        randSeed = new Random(smallSeed);

        findLife();
        findSize();
        findName();
        findTemperature();
        findSatellites();
        findPlanetYear();
        findRotationLocation();
    }

    private void findLife() {
        goldilocksZone = randSeed.nextInt(100) < 10;//Replace this later when sun is added
        hasWater = randSeed.nextInt(100) < 10; //10% of planets will have atmosphere

        hasLife = goldilocksZone && hasWater; //Planet can develop simple life if in goldilocks zone and it has atmosphere

        hasComplexLife = hasLife && randSeed.nextInt(100) < 10; // Planet with simple life has 10% chance of developing complex life
    }

    private void findSize(){
        if(randSeed.nextInt(10)<4) {
            size = randSeed.nextInt(Resources.BIGGEST_BIG_PLANET - Resources.SMALLEST_BIG_PLANET) + Resources.SMALLEST_BIG_PLANET;
        }else{
            size = randSeed.nextInt(Resources.BIGGEST_SMALL_PLANET - Resources.SMALLEST_SMALL_PLANET) + Resources.SMALLEST_SMALL_PLANET;
        }
    }

    private void findName(){
        int nameSize = randSeed.nextInt(12)+4;
        String _name = "";

        for(int i = 0; i < nameSize; i++){
            _name += Resources.PLANET_NAME_LETTERS.charAt(
                    randSeed.nextInt(Resources.PLANET_NAME_LETTERS.length()-1)+1);
        }

        name = _name;
    }

    // 2300 - (-270)
    private void findTemperature(){
        int tempDifference = Resources.PLANET_MAX_TEMPERATURE - Resources.PLANET_MIN_TEMPERATURE;

        if(goldilocksZone || hasWater){
            tempDifference = 200 - Resources.PLANET_MIN_TEMPERATURE;
        }

        temperature = randSeed.nextInt(tempDifference) + Resources.PLANET_MIN_TEMPERATURE;
    }

    private void findSatellites(){
        if(size < 50000){ //Small planets can have 10 satellites
            satellites = randSeed.nextInt(10);
        }else if(size >= 50000 && size < 250000){ //Planets in 50,000 - 300,000 have up to 40 satellites with minimum of 2.
            satellites = randSeed.nextInt(38) + 2;
        }else{
            satellites = randSeed.nextInt(96) + 4;//Planets bigger than 300,000 have up to 100 satellites with minimum of 4.
        }
    }

    private void findPlanetYear(){
        sunDistance = (long)(randSeed.nextDouble()*(Resources.BIGGEST_SUN_DISTANCE - Resources.SHORTEST_SUN_DISTANCE)) + Resources.SHORTEST_SUN_DISTANCE;
        planetYear = randSeed.nextInt(400) + 40;

        planetDay = randSeed.nextInt(planetYear);
    }

    private void findRotationLocation(){
        rotationLocation = randSeed.nextInt(planetYear);
    }

    public void setDistanceFromSun(long distance){
        sunDistance = distance;
    }

    public int getPlanetyear() {
        return planetYear;
    }

    public int getPlanetDay(){
        return planetDay;
    }

    public BigInteger getPlanetSeed(){
        return planetSeed;
    }

    public boolean getGoldilocksZone(){
        return goldilocksZone;
    }

    public boolean getWater(){
        return hasWater;
    }

    public boolean getSimpleLife(){
        return hasLife;
    }

    public boolean getComplexLife(){
        return hasComplexLife;
    }

    public int getPlanetSize(){
        return size;
    }

    public int getTemperature(){
        return temperature;
    }

    public int getSatellites(){
        return satellites;
    }

    public boolean hasLife(){
        return hasLife;
    }

    public int getIdLength(){
        return ID_LENGTH;
    }

    public String getName(){
        return name;
    }

    public int getPlanetColor(){
        return Utils.getPlanetColor(planetSeed);
    }

    public int getModelSize(boolean smallModel){
        int minPlanetSize;
        int maxPlanetSize;

        if(smallModel){
            minPlanetSize = 2;
            maxPlanetSize = 20;
        }else{
            minPlanetSize = 40;
            maxPlanetSize = 900;
        }

        double planetRatio = (double)getPlanetSize() / (double)Resources.BIGGEST_BIG_PLANET;

        return (int)(((maxPlanetSize-minPlanetSize) * planetRatio) + minPlanetSize);
    }

    public int getListSize(){
        int minPlanetSize = 10;
        int maxPlanetSize = 120;

        double planetRatio;

        planetRatio = (double)getPlanetSize() / (double)Resources.BIGGEST_BIG_PLANET;

        return (int)(((maxPlanetSize-minPlanetSize) * planetRatio) + minPlanetSize);
    }

    public long getSunDistance() {
        return sunDistance;
    }

    public CardView getPlanetView() {
        return planetView;
    }

    public void setPlanetView(CardView planetView) {
        this.planetView = planetView;
    }
    long animationStarted;

    public void startAnimation(){
        animationStarted = System.currentTimeMillis();
    }

    public double getRotationLocation(){
        return rotationLocation + ((double)(System.currentTimeMillis())/50000);
    }
}
