package hr.knezzz.randomsolarsystem;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Random;

import hr.knezzz.randomsolarsystem.utils.Biome;
import hr.knezzz.randomsolarsystem.utils.Resources;
import hr.knezzz.randomsolarsystem.utils.Utils;

/**
 * Planet class;
 * This class builds the planet and gives planet it's properties.
 *
 * Created by knezzz on 30/03/16.
 */
public class Planet implements Serializable{
    private final BigInteger planetSeed;
    private final int smallSeed;
    private final int ID_LENGTH;
    private long rotationLocation;
    private double planetDistanceAU;
    private int planetColor;
    private Biome planetBiome;
    private long waterLevel;

    private String name;
    private Random randSeed;
    transient private Bitmap planetImage;

    private boolean goldilocksZone; //Goldilocks zone depends on parent star.
    private boolean hasWater; //10% of planets in goldilocks zone
    private boolean hasLife; //All planets that have water and are in goldilocks zone
    private boolean hasComplexLife; //10% of planets that have simple life

    public final static int PLANET_SMALL = 1;
    public final static int PLANET_BIG = 2;
    public final static int PLANET_GIANT = 3;

    private int planetType;

    private int size;
    private long sunDistance;
    private int parentStarType;
    private int planetDay;
    private int planetYear;
    private double temperature;
    private int satellites;

    // Image Stuff
    int featureSize;

    private static final String TAG = "Planet";

    public Planet(BigInteger seed, long sunDistance, int parentStarType){
        planetSeed = seed;

        this.parentStarType = parentStarType;
        this.sunDistance = sunDistance;
        convertDistanceToAU();

        ID_LENGTH = planetSeed.toString().length();

        smallSeed = (planetSeed.intValue());

        makePlanet();
    }

    private void convertDistanceToAU(){
        double biggestDistance;
        double smallestDistance;

        switch(parentStarType){
            case Resources.STAR_TYPE_O:
                smallestDistance = 3;
                biggestDistance = 2300;
                break;
            case Resources.STAR_TYPE_B:
                smallestDistance = 1;
                biggestDistance = 1800;
                break;
            case Resources.STAR_TYPE_A:
                smallestDistance = 0.8;
                biggestDistance = 1200;
                break;
            case Resources.STAR_TYPE_F:
                smallestDistance = 0.4;
                biggestDistance = 900;
                break;
            case Resources.STAR_TYPE_G:
                smallestDistance = 0.2;
                biggestDistance = 600;
                break;
            case Resources.STAR_TYPE_K:
                smallestDistance = 0.1;
                biggestDistance = 350;
                break;
            case Resources.STAR_TYPE_M:
                smallestDistance = 0.05;
                biggestDistance = 200;
                break;
            default:
                Log.e(TAG, "Sun type not found.. using default values...");
                smallestDistance = Resources.SHORTEST_SUN_DISTANCE;
                biggestDistance = Resources.BIGGEST_SUN_DISTANCE;
                break;
        }

      //  biggestDistance = biggestDistance * 24;

        planetDistanceAU = ((((double)sunDistance/300)/(double)Resources.MODEL_BIGGEST_DISTANCE) * (biggestDistance - smallestDistance)) + smallestDistance;
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
        planetColor = Utils.getPlanetColor(planetSeed);
    }

    private void findLife() {
      //  goldilocksZone = randSeed.nextInt(100) < 10;//Replace this later when sun is added
        isInGoldilocksZone();
        hasWater = randSeed.nextInt(100) < 10; //10% of planets will have atmosphere


        hasLife = goldilocksZone && hasWater; //Planet can develop simple life if in goldilocks zone and it has atmosphere

        hasComplexLife = hasLife && randSeed.nextInt(100) < 10; // Planet with simple life has 10% chance of developing complex life

        if(hasWater) findWaterLevel();
    }

    private void findWaterLevel(){
        if(hasComplexLife){
            waterLevel = (long)(randSeed.nextInt(60)/100 + 0.4);
        }else if(hasLife){
            waterLevel = (long)(randSeed.nextInt(70)/100 + 0.3);
        }else{
            waterLevel = (long)(randSeed.nextInt(90)/100 + 0.1);
        }
    }

    private void isInGoldilocksZone(){
        switch(parentStarType){
            case Resources.STAR_TYPE_O:
                getZoneAndBiome(4.4, 6.8);
                break;
            case Resources.STAR_TYPE_B:
                getZoneAndBiome(3.6, 4.6);
                break;
            case Resources.STAR_TYPE_A:
                getZoneAndBiome(2.6, 3.8);
                break;
            case Resources.STAR_TYPE_F:
                getZoneAndBiome(1.8, 2.8);
                break;
            case Resources.STAR_TYPE_G:
                getZoneAndBiome(1, 2);
                break;
            case Resources.STAR_TYPE_K:
                getZoneAndBiome(0.8, 1.8);
                break;
            case Resources.STAR_TYPE_M:
                getZoneAndBiome(0.4, 0.8);
                break;
        }
    }

	/**
     * Get if planet is in goldilocks zone by specifying min and max distances from sun.
     * sets boolean goldilocksZone and assigns Biome to planetBiome
     * @param min minimum distance from star to be inside goldilocks zone
     * @param max maximum distance from star to be inside goldilocks zone
     */
    private void getZoneAndBiome(double min, double max){
        goldilocksZone = planetDistanceAU > min && planetDistanceAU < max;

        if(planetDistanceAU > max){
            planetBiome = Biome.ICE;
        }else if(planetDistanceAU < min){
            planetBiome = randSeed.nextBoolean() ? Biome.LAVA : Biome.LAVA2;
        }else{
            switch(randSeed.nextInt(3)){
                case 0:
                    planetBiome = Biome.DESERT;
                    break;
                case 1:
                    planetBiome = Biome.OCEAN;
                    break;
                case 2:
                    planetBiome = Biome.EARTH;
            }
        }
    }

    private void findSize(){//Giant planets are created when they are far away from sun with little luck, or just by insane luck.
        if((planetDistanceAU > 1.2 && randSeed.nextInt(10)<4) || (randSeed.nextInt(50000)<2 && randSeed.nextBoolean())) {
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
        int lowestTemp = -273;
        int highestTemp = 2500;

        temperature = (sunDistance / Resources.BIGGEST_SUN_DISTANCE) * (highestTemp - lowestTemp);
    }

    private void findSatellites(){
        if(size < 50000){ //Small planets can have 10 satellites
            planetType = PLANET_SMALL;
            featureSize = randSeed.nextInt(10) + 25;
            satellites = randSeed.nextInt(4);
        }else if(size >= 50000 && size < 250000){ //Planets in 50,000 - 300,000 have up to 30 satellites with minimum of 2.
            planetType = PLANET_BIG;
            featureSize = randSeed.nextInt(10) + 20;
            satellites = randSeed.nextInt(28) + 2;
        }else{//Planets bigger than 300,000 have up to 80 satellites with minimum of 4.
            planetType = PLANET_GIANT;
            featureSize = randSeed.nextInt(30) + 5;
            satellites = randSeed.nextInt(76) + 4;
        }
    }

    private void findPlanetYear(){
        //sunDistance = (long)(randSeed.nextDouble()*(Resources.BIGGEST_SUN_DISTANCE - Resources.SHORTEST_SUN_DISTANCE)) + Resources.SHORTEST_SUN_DISTANCE;
        planetYear = randSeed.nextInt(400) + 40;

        planetDay = randSeed.nextInt(planetYear);
    }

    private void findRotationLocation(){
        rotationLocation = randSeed.nextInt(planetYear);
    }

    public int getPlanetYear() {
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

    public double getTemperature(){
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
        return planetColor;
    }

    public int getPlanetColorRed(){
        return Utils.getPlanetColorRed(planetSeed);
    }

    public int getPlanetColorGreen(){
        return Utils.getPlanetColorGreen(planetSeed);
    }

    public int getPlanetColorBlue(){
        return Utils.getPlanetColorBlue(planetSeed);
    }

    public int getFeatureSize(){
        return featureSize;
    }

    public int getModelSize(boolean smallModel){
        int minPlanetSize;
        int maxPlanetSize;

        if(smallModel){
            minPlanetSize = 10;
            maxPlanetSize = 60;
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

    public double getDistanceFromSunInAU(){
        return planetDistanceAU;
    }

    public double getPlanetDistanceInKm(){
        return planetDistanceAU * 149597871;
    }

    public double getRotationLocation(){
        return rotationLocation + ((double)(System.currentTimeMillis())/50000);
    }

    public Biome getBiome(){
        return planetBiome;
    }

    public int getPlanetType(){
        return planetType;
    }

    public long getWaterLevel(){
        return waterLevel;
    }
}
