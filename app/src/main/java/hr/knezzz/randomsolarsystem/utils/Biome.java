package hr.knezzz.randomsolarsystem.utils;

/**
 * Created by knezzz on 02/07/16.
 */
public enum Biome{
	EARTH(0, "Earth", 0.0),
	DESERT(1, "Desert", -0.5),
	ICE(2, "Ice", -0.02),
	OCEAN(3, "Ocean", 0.6),
	RAINFOREST(4, "Rainforest", 0.1),
	LAVA(5, "Lava", 0.12),
	LAVA2(6, "New Lava", 0.05),
	BLUE_GIANT(7, "Blue Giant", 0.26);

	private String name;
	private int value;
	private double waterLevel;

	Biome(int value, String name, double waterLevel){
		this.value = value;
		this.name = name;
		this.waterLevel = waterLevel;
	}

	@Override
	public String toString(){
		return name;
	}

	public int getValue(){
		return value;
	}

	public double getWaterLevel(){
		return waterLevel;
	}
}
