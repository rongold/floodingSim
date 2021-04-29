package simulator;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.Camera;
import com.jme3.system.AppSettings;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.water.WaterFilter;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Enviroment extends SimpleApplication implements ActionListener {

	private BulletAppState bulletAppState;
	private TerrainQuad terrain;
	private Material mat_terrain;
	private WaterFilter water = null;
	/**
	 * @author Ronil Goldenwalla
	 * This class is the simulator section of the project
	 */
	
	public void runSimulator() {
		AppSettings settings = new AppSettings(true);
		settings.setResolution(1024, 728);
		settings.setHeight(1024);
		settings.setWidth(728);
		settings.setTitle("Simulator");

		Enviroment app = new Enviroment();
		app.setDisplayStatView(false);

		app.setShowSettings(false);

		app.start();

	}

	@Override
	public void simpleInitApp() {
		bulletAppState = new BulletAppState();
		stateManager.attach(bulletAppState);
		// Uncomment for debugging.
		// bulletAppState.setDebugEnabled(true);

		flyCam.setMoveSpeed(100);
		setUpKeys();

		mat_terrain = new Material(assetManager, "Common/MatDefs/Terrain/Terrain.j3md");

		mat_terrain.setTexture("Alpha", assetManager.loadTexture("Textures/Terrain/splat/alphamap.png"));

		Texture grass = assetManager.loadTexture("Textures/Terrain/splat/grass.jpg");
		grass.setWrap(WrapMode.Repeat);
		mat_terrain.setTexture("Tex1", grass);
		mat_terrain.setFloat("Tex1Scale", 64f);

		Texture dirt = assetManager.loadTexture("Textures/Terrain/splat/dirt.jpg");
		dirt.setWrap(WrapMode.Repeat);
		mat_terrain.setTexture("Tex2", dirt);
		mat_terrain.setFloat("Tex2Scale", 32f);

		Texture rock = assetManager.loadTexture("Textures/Terrain/splat/road.jpg");
		rock.setWrap(WrapMode.Repeat);
		mat_terrain.setTexture("Tex3", rock);
		mat_terrain.setFloat("Tex3Scale", 128f);

		AbstractHeightMap heightmap = null;

		Texture heightMapImage = assetManager.loadTexture("simulator/Scarborough2.png");
		heightmap = new ImageBasedHeightMap(heightMapImage.getImage());
		heightmap.load();

		DirectionalLight sun = new DirectionalLight();
		Vector3f lightDir = new Vector3f(-0.37352666f, -0.50444174f, -0.7784704f);
		sun.setDirection(lightDir);
		sun.setColor(ColorRGBA.White.clone().multLocal(2));

		//Sets up water
		FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
		water = new WaterFilter(rootNode, lightDir);
		water.setWaterHeight(-80);
		water.setWaterTransparency(0.2f);
		water.setMaxAmplitude(0.3f);
		water.setWaveScale(0.008f);
		water.setSpeed(0.7f);
		water.setShoreHardness(1.0f);
		water.setRefractionConstant(0.2f);
		water.setShininess(0.3f);
		water.setSunScale(1.0f);
		water.setColorExtinction(new Vector3f(10.0f, 20.0f, 30.0f));
		water.setFoamExistence(new Vector3f(1f, 4, 0.5f));
		water.setFoamTexture((Texture2D) assetManager.loadTexture("Common/MatDefs/Water/Textures/foam2.jpg"));
		water.setRefractionStrength(0.2f);
		fpp.addFilter(water);
		viewPort.addProcessor(fpp);

		terrain = new TerrainQuad("my terrain", 65, 513, heightmap.getHeightMap());

		terrain.setMaterial(mat_terrain);
		terrain.setLocalTranslation(0, -100, 0);
		terrain.setLocalScale(2f, 1f, 2f);
		rootNode.attachChild(terrain);

		List<Camera> cameras = new ArrayList<Camera>();
		cameras.add(getCamera());
		TerrainLodControl control = new TerrainLodControl(terrain, cameras);
		terrain.addControl(control);

	}
	//Sets up keybindings
	private void setUpKeys() {
		inputManager.addMapping("Water Up", new KeyTrigger(KeyInput.KEY_R));
		inputManager.addMapping("Water Down", new KeyTrigger(KeyInput.KEY_F));
		inputManager.addMapping("Water Simulate", new KeyTrigger(KeyInput.KEY_G));
		inputManager.addMapping("Main Menu", new KeyTrigger(KeyInput.KEY_ESCAPE));

		
		inputManager.addListener(this, "Water Up");
		inputManager.addListener(this, "Water Down");
		inputManager.addListener(this, "Water Simulate");
		inputManager.addListener(this, "Main Menu");


	}
	//When Keys are pressed
	public void onAction(String binding, boolean value, float tpf) {
		if (binding.equals("Water Up")) {
			if (water.getWaterHeight() == -80f) {
				water.setWaterHeight(-60f);
			}
		}
		if (binding.equals("Water Down")) {
			water.setWaterHeight(-80f);
		}
		
		if (binding.equals("Water Simulate")) {
			try {
				float waterLevel=getWaterLevel();
				if(waterLevel>5f) {
					water.setWaterHeight(-80f);
				}
				if(waterLevel>3f && waterLevel<4.9f) {
					water.setWaterHeight(-70f);
				}
				if(waterLevel>1f && waterLevel<2.9f) {
					water.setWaterHeight(-65f);
				}
				if(waterLevel>0f && waterLevel<1.9f) {
					water.setWaterHeight(-60f);
				}
				if(waterLevel<0f) {
					water.setWaterHeight(-50f);
				}
			} catch (CsvValidationException | IOException e) {
				e.printStackTrace();
			}
		}
		if (binding.equals("Main Menu")) {
			new MainMenu().setVisible(true);
		}

	}
	//Reads CSV file
	private float getWaterLevel() throws CsvValidationException, IOException {
		String[] waterData;
		float level;
		CSVReader floodsReader = new CSVReader(
				new FileReader("assets/waterLevel.csv"));
		floodsReader.readNext();
		while ((waterData = floodsReader.readNext()) != null) {
			level=Float.parseFloat(waterData[2]);
			return level;
		}
		return 0;
	}
	
	

}