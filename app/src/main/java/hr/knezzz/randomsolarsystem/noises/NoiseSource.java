package hr.knezzz.randomsolarsystem.noises;

/**
 * Created by knezzz on 09/07/16.
 */
public class NoiseSource{
	private long seed;

	public void setSeed(long seed){
		this.seed = seed;
	}

	public double noise(double x, double y){
		return 0.2;
	}

	public double noise(double x, double y, double z){
		return 0.2;
	}
}
