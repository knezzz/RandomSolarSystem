package hr.knezzz.randomsolarsystem.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Environment;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import hr.knezzz.randomsolarsystem.Planet;
import hr.knezzz.randomsolarsystem.PlanetActivity;
import hr.knezzz.randomsolarsystem.PlanetHolder;
import hr.knezzz.randomsolarsystem.R;
import hr.knezzz.randomsolarsystem.SolarMath;
import hr.knezzz.randomsolarsystem.noises.NoiseSource;
import hr.knezzz.randomsolarsystem.noises.Perlin;
import hr.knezzz.randomsolarsystem.noises.Simplex;
import hr.knezzz.randomsolarsystem.noises.Worley;

/**
 * Created by knezzz on 31/07/16.
 */
public class PlanetView implements Serializable{
	private static final String TAG = "PlanetView";
	private Planet planet;
	private boolean isRendering = false;
	private boolean isTerrainGenerated = false;
	private long mySeed;
	private double waterLevel = 0.0;
	private static final String TEMP_FOLDER = "/solarSystemTempFolder";

	transient private Activity activity;
	transient private Bitmap planetImage;
	transient private ImageView planetView;
	transient private CardView simplePlanetView;
//	transient private PlanetHolder planetHolder;
	transient private ArrayList<NoiseSource> noiseSources;

	transient private Paint p;
	transient private Paint eraser;

	transient Thread renderEngine = null;

	private int currentNoise = 0; // 0-Billow, 1-Perlin, 2-Ridged, 3-Voronoi
	private Biome myBiome;

	private int time = 0;
	private double bestRenderQuality = 500;

	public PlanetView(Planet planet, Activity activity){
		this.planet = planet;
		this.activity = activity;
		mySeed = planet.getPlanetSeed().longValue();
		myBiome = planet.getBiome();

		p = new Paint();
		eraser = new Paint();
		eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		addAllToArray();
		planetImage = null;
	//	makePlanetView();
	//	generateTerrain();
	}

	public void resetView(Activity activity){
		this.activity = activity;
		mySeed = planet.getPlanetSeed().longValue();
		myBiome = planet.getBiome();

		p = new Paint();
		eraser = new Paint();
		eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		addAllToArray();
		restoreImage();
	}

	public Thread generateTerrain(final TerrainGenerationDone tgd, int fSize, double squareSize){
		final double featureSize = fSize;
		final int size = planet.getPlanetSize()/80;

		Log.d(TAG, String.format("Wants quality [%f] Have quality [%f]", squareSize, bestRenderQuality));
		if(squareSize >= bestRenderQuality){
			tgd.imageDone(planetImage);
			return renderEngine;
		}

		if(isRendering)
			return null;
		else
			isRendering = true;

		final double finalSquareSize = squareSize;
		final long startTime = System.currentTimeMillis();
		final int WIDTH = size;
		final int HEIGHT = size;
		final int RADIUS = size/2;
		Log.d(TAG, "Generating terrain for: " + planet.getName() + " radius : " + RADIUS + " squareSize: " + squareSize + " feature size: " + featureSize);

		renderEngine = new Thread(new Runnable(){
			@Override
			public void run(){
				Random random = new Random();
				random.setSeed(mySeed);
				changeFiler(noiseSources.get(currentNoise));
				waterLevel = myBiome.getWaterLevel();

				if(planetImage == null){
					planetImage = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
				}else if(!planetImage.isMutable()){
					Log.d(TAG, String.format("Existing image found. Trying to turn it mutable..... %b", planetImage.isMutable()));
					planetImage = planetImage.copy(Bitmap.Config.ARGB_8888, true);
					Log.d(TAG, String.format("Existing image found. Trying to turn it mutable..... %b", planetImage.isMutable()));
				}

				boolean resetWaterLevel = false;
				if(planet.getWater()){
					waterLevel += planet.getWaterLevel();
				}else{
					resetWaterLevel = true;
				}

				switch(myBiome){
					case EARTH:
					case DESERT:
					case ICE:
					case OCEAN:
						if(resetWaterLevel) waterLevel = -1;
						currentNoise = 1; // Perlin
						break;
					case RAINFOREST:
					case LAVA:
						if(resetWaterLevel) waterLevel = 0;
						currentNoise = 0; // Simplex
						break;
					case LAVA2:
						if(resetWaterLevel) waterLevel = 0;
						currentNoise = 1; // Simplex
						break;
					case BLUE_GIANT:
						if(resetWaterLevel) waterLevel = 0;
						currentNoise = 1;
						break;
					default:
						if(resetWaterLevel) waterLevel = 0;
						currentNoise = 0; // Simplex
						break;
				}

				Canvas canvas = new Canvas(planetImage);
				canvas.save();


				for(int x = 0; x < WIDTH; x += finalSquareSize){
					for(int y = 0; y < HEIGHT; y += finalSquareSize){
                        double distanceFormCenter = Math.sqrt(Math.pow(RADIUS - x, 2) + Math.pow(RADIUS - y, 2));
                        if(distanceFormCenter < RADIUS){
							double noise = noiseSources.get(currentNoise).noise(x / featureSize, y / featureSize, time/featureSize);
							int terrainColor, waterColor = Color.TRANSPARENT;

							switch(myBiome){
								case EARTH:
									if(noise < waterLevel + 0.08){
										double sandNoise = 1 - Math.abs(noise);
										terrainColor = Color.rgb( (int) (100 * sandNoise) + 55, (int) (200 * sandNoise) + 55, 0);
									}else{
										double earthNoise = 1 - Math.abs(noise);
										earthNoise = earthNoise < 0.2 ? 0.2 : earthNoise;
										terrainColor = Color.rgb((int) (10 * earthNoise), (int) (200 * earthNoise) + 55, 0);
									}

									if(noise < waterLevel){
										double waterNoise = 1 - Math.abs(noise);
										waterNoise = waterNoise < 0.2 ? 0.2 : waterNoise;
										waterColor = Color.argb(200, 0, 0, (int) (200 * waterNoise) + 55);
									}else if(noise > 0.7){
										double iceNoise = 1-Math.abs(noise);
										waterColor = Color.argb((int) (155 * iceNoise) + 100, (int) (55 * iceNoise) + 200, (int) (55 * iceNoise) + 200, (int) (55 * iceNoise) + 200);
									}
									break;
								case DESERT:
									double sandNoise = 1 - Math.abs(noise);
									sandNoise = sandNoise < 0.2 ? 0.2 : sandNoise;
									terrainColor = Color.rgb((int) (255 * sandNoise), (int) (200 * sandNoise), 0);

									if(noise < waterLevel){
										double waterNoise = 1 - Math.abs(noise);
										waterNoise = waterNoise < 0.2 ? 0.2 : waterNoise;
										waterColor = Color.argb((int) (160 + (50 * waterNoise)), 0, 0, (int) (200 * waterNoise) + 55);
									}
									break;
								case ICE:
									double iceNoise = 1 - noise;
									int iceColor = (int) ((Math.abs(iceNoise)) * 55) + 200;
									terrainColor = Color.rgb(iceColor, iceColor, iceColor);

									if(noise < waterLevel){
										double waterNoise = Math.abs(noise);
										waterColor = Color.argb(200, 0, 0, (int) (200 * waterNoise) + 55);
									}
									break;
								case OCEAN:
									double islandNoise = noise - 2;
									terrainColor = Color.rgb(0, (int) (255 * Math.abs(noise)), (int) (20 * Math.abs(1 / islandNoise)));

									if(noise < waterLevel){
										double waterNoise = noise - 2;
										waterColor = Color.argb(200, 0, 0, (int) (255 * Math.abs(1 / waterNoise)));
									}
									break;
								case RAINFOREST:
									double rainforestNoise = 1 - Math.abs(noise);
									rainforestNoise = rainforestNoise < 0.15 ? 0.15 : rainforestNoise;
									terrainColor = Color.rgb(0, (int) (200 * rainforestNoise) + 55, 0);

									if(Math.abs(noise) < waterLevel){
										double color = 1 - Math.abs(noise);
										waterColor = Color.argb(200, 0, (int) (150 * Math.abs(noise)), (int) (255 * color));
									}
									break;
								case BLUE_GIANT:
									double blueGiantNoise = 1 - Math.abs(noise);
									blueGiantNoise = blueGiantNoise < 0.15 ? 0.15 : blueGiantNoise;
									terrainColor = Color.rgb(0, 0, (int) (55 * blueGiantNoise) + 200);

									if(Math.abs(noise) < waterLevel){
										double color = 1 - Math.abs(noise);
										waterColor = Color.argb((int) (200 * Math.abs(color)), 255, 255, 255);
									}
									break;
								case LAVA:
								case LAVA2:
									//    double color = 1 - Math.abs(noise);
									terrainColor = Color.rgb((int) (55 * Math.abs(noise)), (int) (55 * Math.abs(noise)), (int) (55 * Math.abs(noise)));

									if(Math.abs(noise) < Math.abs(waterLevel)){
										noise = Math.abs(noise) + 0.6;
										waterColor = Color.argb((int)(205 * noise),(int) (255 * noise), 10, 10);
									}
									break;
								default:
									terrainColor = Color.RED;
							}
							p.setColor(terrainColor);
							canvas.drawRect(x, y, (float)(x + finalSquareSize), (float)(y + finalSquareSize), p);
							p.setColor(waterColor);
							canvas.drawRect(x, y, (float)(x + finalSquareSize), (float)(y + finalSquareSize), p);
                        }else{
                            canvas.drawRect(x, y, (float)(x + finalSquareSize), (float)(y + finalSquareSize), eraser);
                        }
					}
					final int finalX = x;
					//							timerText.setText(String.format("Rendering Terrain [%.1f%%]", ((double) finalX / (double) (WIDTH)) * 100));
					tgd.imageUpdate(planetImage);
				}

				bestRenderQuality = finalSquareSize;
				saveImage();
				tgd.imageDone(planetImage);
				isRendering = false;
				isTerrainGenerated = true;
			}
		});

		return renderEngine;
	}

	public void stopRender(){
		if(renderEngine != null && renderEngine.isAlive()){
			renderEngine.interrupt();
		}
	}

	public void changeFiler(NoiseSource module){
		if(module instanceof Worley){
			Worley b = (Worley) module;
			b.setSeed((int) mySeed);
		}else if(module instanceof Perlin){
			Perlin b = (Perlin) module;
			b.setSeed((int) mySeed);
		}else if(module instanceof Simplex){
			Simplex b = (Simplex) module;
			b.setSeed((int) mySeed);
		}
	}

	private void addAllToArray(){
		noiseSources = new ArrayList<>();

		noiseSources.add(new Simplex());
		noiseSources.add(new Perlin());
		noiseSources.add(new Worley());
	}

	//TODO
	private void makePlanetView(){

	}

	public boolean isTerrainGenerated(){
		return isTerrainGenerated;
	}

	public Bitmap getPlanetImage(){
		return planetImage;
	}

	public CardView getSimplePlanetView(){
		return simplePlanetView;
	}

	public double getBestRenderQuality(){
		return bestRenderQuality;
	}

	public void setPlanetView(ImageView planetView){
		this.planetView = planetView;
	}

	public void setSimplePlanetView(CardView simplePlanetView){
		this.simplePlanetView = simplePlanetView;
	}

	public ImageView getPlanetView(){
		return planetView;
	}

//	public void setPlanetHolder(PlanetHolder holder){
//		if(planetHolder == null){
//			this.planetHolder = holder;
//			setUpView();
//		}
//
//		setUpView();
//	}

	public void setUpView(PlanetHolder planetHolder){
		planetHolder.planet.setCardBackgroundColor(planet.getPlanetColor());

		planetHolder.planetName.setText(planet.getName());

		int colorTrue = ContextCompat.getColor(activity, R.color.planet_info_true);
		int colorFalse = ContextCompat.getColor(activity, R.color.planet_info_false);

		planetHolder.waterSwitch.setCardBackgroundColor(planet.getGoldilocksZone() ? colorTrue : colorFalse);
		planetHolder.simpleSwitch.setCardBackgroundColor(planet.getSimpleLife() ? colorTrue : colorFalse);
		planetHolder.complexSwitch.setCardBackgroundColor(planet.getComplexLife() ? colorTrue : colorFalse);

		planetHolder.planetInfo.setText(String.format("Day duration: %s\nYear duration: %s\nDistance in AU: %.2f\nSize: %d", planet.getPlanetDay(), planet.getPlanetYear(), planet.getDistanceFromSunInAU(), planet.getPlanetSize()));

		SolarMath.setUpPlanetList(planet.getListSize(), planet.getPlanetColor(), planetHolder.planet);
		if(Resources.GENERATE_TERRAIN){
			planetHolder.planetImageView.setTransitionName("planetView" + planet.getName());
		}else{
			planetHolder.planet.setTransitionName("planetView" + planet.getName());
		}

		planetHolder.touchListener.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				if(!activity.isFinishing()){
					Intent i = new Intent(activity, PlanetActivity.class);
					i.putExtra(Resources.PLANET_INTENT_WHOLE_PLANET, PlanetView.this);
					ActivityOptionsCompat options;

					if(Resources.GENERATE_TERRAIN){
						ImageView image = (ImageView)v.findViewById(R.id.planet_view_terrain);
						options = ActivityOptionsCompat.
							makeSceneTransitionAnimation(activity, image, "planetView" + planet.getName());
					}else{
						CardView card = (CardView)v.findViewById(R.id.planet_view);
						options = ActivityOptionsCompat.
							makeSceneTransitionAnimation(activity, card, "planetView" + planet.getName());
					}

					activity.startActivity(i, options.toBundle());
				}
			}
		});
	}

	private void makeFolder(){
		File f = new File(Environment.getExternalStorageDirectory().toString() + TEMP_FOLDER);
		if(!f.exists()){
			f.mkdirs();
		}
	}

	private void saveImage(){
		makeFolder();
		String path = Environment.getExternalStorageDirectory().toString();
		FileOutputStream out = null;
		File f = new File(path + TEMP_FOLDER, planet.getName() + ".png");
		Log.i(TAG, String.format("Saving image to %s", f.getAbsolutePath()));
		try {
			out = new FileOutputStream(f);
			planetImage.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
			// PNG is a lossless format, the compression factor (100) is ignored
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void restoreImage(){
		String path = Environment.getExternalStorageDirectory().toString();
		File f = new File(path + TEMP_FOLDER, planet.getName() + ".png");
		if(f.exists()){
			Log.d(TAG, String.format("Existing image found for %s", planet.getName()));
			planetImage = BitmapFactory.decodeFile(f.getAbsolutePath());
		}
	}

	public Planet getPlanet(){
		return planet;
	}

	public interface TerrainGenerationDone{
		void imageUpdate(Bitmap image);
		void imageDone(Bitmap image);
	}
}
