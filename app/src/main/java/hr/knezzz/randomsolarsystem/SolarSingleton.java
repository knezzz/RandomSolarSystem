package hr.knezzz.randomsolarsystem;

/**
 * Created by knezzz on 07/04/16.
 * Singleton class that holds seed for current run
 */
public class SolarSingleton {
    private String solarSeed = null;
    private static SolarSingleton ourInstance = new SolarSingleton();

    public static SolarSingleton getInstance() {
        return ourInstance;
    }

    private SolarSingleton(){
    }

    private SolarSingleton(String solarSeed){
        this.solarSeed = solarSeed;
    }

    public void setInstance(String solarSeed){
        ourInstance = new SolarSingleton(solarSeed);
    }

    public String getSolarSeed(){
        return solarSeed;
    }
}
