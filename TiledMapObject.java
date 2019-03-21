package objects;

import static com.badlogic.gdx.graphics.g2d.Batch.C1;
import static com.badlogic.gdx.graphics.g2d.Batch.C2;
import static com.badlogic.gdx.graphics.g2d.Batch.C3;
import static com.badlogic.gdx.graphics.g2d.Batch.C4;
import static com.badlogic.gdx.graphics.g2d.Batch.U1;
import static com.badlogic.gdx.graphics.g2d.Batch.U2;
import static com.badlogic.gdx.graphics.g2d.Batch.U3;
import static com.badlogic.gdx.graphics.g2d.Batch.U4;
import static com.badlogic.gdx.graphics.g2d.Batch.V1;
import static com.badlogic.gdx.graphics.g2d.Batch.V2;
import static com.badlogic.gdx.graphics.g2d.Batch.V3;
import static com.badlogic.gdx.graphics.g2d.Batch.V4;
import static com.badlogic.gdx.graphics.g2d.Batch.X1;
import static com.badlogic.gdx.graphics.g2d.Batch.X2;
import static com.badlogic.gdx.graphics.g2d.Batch.X3;
import static com.badlogic.gdx.graphics.g2d.Batch.X4;
import static com.badlogic.gdx.graphics.g2d.Batch.Y1;
import static com.badlogic.gdx.graphics.g2d.Batch.Y2;
import static com.badlogic.gdx.graphics.g2d.Batch.Y3;
import static com.badlogic.gdx.graphics.g2d.Batch.Y4;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapImageLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Array;

import config.configGame;

public class TiledMapObject extends Actor {
	
	private TiledMap map;
	private Array<Integer> layers;
	
	private int randomMap;
	private int mapWidth, mapHeight, tileWidth, tileHeight;
	private float[] vertices = new float[20];
	private Rectangle viewBounds;
	
	public TiledMapObject(TiledMap map, int random){
		this.map = map;
		this.randomMap = random; 
		this.mapWidth = map.getProperties().get("width", Integer.class); 
		this.mapHeight = map.getProperties().get("height", Integer.class);
		this.tileWidth = map.getProperties().get("tilewidth", Integer.class);
		this.tileHeight = map.getProperties().get("tileheight", Integer.class);
		
		this.viewBounds = new Rectangle();
		this.layers = new Array<Integer>(2);
		setDrawLayers(configGame.defaultLayerStep, configGame.defaultCountLayers);
	}
	
	public TiledMapObject(TiledMap map, int random, float width){
		this(map, random);
		float mapScaleXY;
		if (width != 0.0f)
			mapScaleXY = width / (tileWidth * mapWidth);
		else mapScaleXY = 1.0f;
		setScale(mapScaleXY);
		setSize(tileWidth * mapWidth * getScaleX(), tileHeight * mapHeight * getScaleY());
	}
	
	public void replaceMap(TiledMapObject objectMap){
		this.map = objectMap.getMap();
		this.randomMap = objectMap.getRandomMap();
		if (this.layers.size != 0) this.layers.clear();
		this.layers.addAll(objectMap.getDrawLayers());
	}
	
	public void setEmptyMap(TiledMap map){
		this.map = map;
		this.randomMap = 0;
		if (this.layers.size != 0) this.layers.clear();
		this.layers.add(0);
	}
	
	public void setNewMap(TiledMap map, int random) {
		this.map = map;
		this.randomMap = random;
		setDrawLayers(configGame.defaultLayerStep, configGame.defaultCountLayers);
	}
	
	public TiledMapObject setDrawLayersAll() {
		if (this.layers.size != 0) this.layers.clear();
		int ii = 0;
		while (ii < map.getLayers().getCount())
			layers.add(ii++);
		return this;
	}
	
	public TiledMapObject setDrawLayersDefault(){
		if (this.layers.size != 0) this.layers.clear();
		int drawLayer;
		for (int ii = 0; ii < configGame.defaultCountLayers; ii++) {
			drawLayer = MathUtils.random(configGame.defaultLayerStep) + ii * (configGame.defaultLayerStep + 1);
			if (drawLayer >= map.getLayers().getCount()) return this;
			layers.add(drawLayer);
		}
		return this;
	}
	
	public void setDrawLayers(int layerStep, int countLayers) {
		if (this.layers.size != 0) this.layers.clear();
		int drawLayer;
		for (int ii = 0; ii < countLayers; ii++) {
			drawLayer = MathUtils.random(layerStep) + ii * (layerStep + 1);
			if (drawLayer >= map.getLayers().getCount()) return;
			layers.add(drawLayer);
		}
	}
	
	public int getRandomMap(){
		return this.randomMap;
	}
	
	public boolean isEmptyMap(){
		if (this.randomMap == 0)
			return true;
		else return false;
	}
	
	public TiledMap getMap(){
		return this.map;
	}
	
	public Array<Integer> getDrawLayers() {
		return this.layers;
	}
	
	@Override
	public Actor hit (float x, float y, boolean touchable) {
		if (touchable && getTouchable() != Touchable.enabled) return null;
		return x >= 0 && x < tileWidth * mapWidth && y >= 0 && y < tileHeight * mapHeight ? this : null;
	}
	
	private void mapViewUpdate(){
		OrthographicCamera cam = (OrthographicCamera) getStage().getCamera();
		float width = cam.viewportWidth * cam.zoom;
		float height = cam.viewportHeight * cam.zoom;
		viewBounds.set(cam.position.x - width / 2, cam.position.y - height / 2, width, height);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		
		//update the viewbounds
		mapViewUpdate();
			
		//draw the layers
		for (int layerIdx : layers) {
			MapLayer layer = map.getLayers().get(layerIdx);
			if (layer.isVisible()) {
				if (layer instanceof TiledMapTileLayer) {
					drawLayer((TiledMapTileLayer)layer, batch, parentAlpha);
				} else if (layer instanceof TiledMapImageLayer) {
					//renderImageLayer((TiledMapImageLayer)layer);
				} else {
					for (MapObject object : layer.getObjects()) 
						drawObject(object);
				}
			}
		}
	}
	
	protected void drawLayer(TiledMapTileLayer layer, Batch batch, float parentAlpha){
		final Color batchColor = batch.getColor();
		final float color = Color.toFloatBits(batchColor.r, batchColor.g, batchColor.b, batchColor.a * layer.getOpacity() * parentAlpha);

		final int layerWidth = layer.getWidth();
		final int layerHeight = layer.getHeight();

		final float layerTileWidth = layer.getTileWidth() * getScaleX();
		final float layerTileHeight = layer.getTileHeight() * getScaleY();

		final int col1 = Math.max(0, (int) (viewBounds.x / layerTileWidth));
		final int col2 = Math.min(layerWidth, (int) ((viewBounds.x + viewBounds.width + layerTileWidth) / layerTileWidth));

		final int row1 = Math.max(0, (int) (viewBounds.y / layerTileHeight));
		final int row2 = Math.min(layerHeight, (int) ((viewBounds.y + viewBounds.height + layerTileHeight) / layerTileHeight));				

		float y = getY() + (row1 * layerTileHeight);
		float xStart = getX() + (col1 * layerTileWidth);
		final float[] vertices = this.vertices;

		for (int row = row1; row < row2; row++) {
			float x = xStart;
			for (int col = col1; col < col2; col++) {
				final TiledMapTileLayer.Cell cell = layer.getCell(col, row);
				if(cell == null) {
					x += layerTileWidth;
					continue;
				}
				final TiledMapTile tile = cell.getTile();
				if (tile == null) continue; 
					
				final boolean flipX = cell.getFlipHorizontally();
				final boolean flipY = cell.getFlipVertically();
				final int rotations = cell.getRotation();

				TextureRegion region = tile.getTextureRegion();

				float x1 = x;
				float y1 = y;
				float x2 = x1 + region.getRegionWidth() * getScaleX();
				float y2 = y1 + region.getRegionHeight() * getScaleY();

				float u1 = region.getU();
				float v1 = region.getV2();
				float u2 = region.getU2();
				float v2 = region.getV();

				vertices[X1] = x1;
				vertices[Y1] = y1;
				vertices[C1] = color;
				vertices[U1] = u1;
				vertices[V1] = v1;

				vertices[X2] = x1;
				vertices[Y2] = y2;
				vertices[C2] = color;
				vertices[U2] = u1;
				vertices[V2] = v2;

				vertices[X3] = x2;
				vertices[Y3] = y2;
				vertices[C3] = color;
				vertices[U3] = u2;
				vertices[V3] = v2;

				vertices[X4] = x2;
				vertices[Y4] = y1;
				vertices[C4] = color;
				vertices[U4] = u2;
				vertices[V4] = v1;

				if (flipX) {
					float temp = vertices[U1];
					vertices[U1] = vertices[U3];
					vertices[U3] = temp;
					temp = vertices[U2];
					vertices[U2] = vertices[U4];
					vertices[U4] = temp;
				}
				if (flipY) {
					float temp = vertices[V1];
					vertices[V1] = vertices[V3];
					vertices[V3] = temp;
					temp = vertices[V2];
					vertices[V2] = vertices[V4];
					vertices[V4] = temp;
				}
				if (rotations != 0) {
					switch (rotations) {
						case Cell.ROTATE_90: {
							float tempV = vertices[V1];
							vertices[V1] = vertices[V2];
							vertices[V2] = vertices[V3];
							vertices[V3] = vertices[V4];
							vertices[V4] = tempV;

							float tempU = vertices[U1];
							vertices[U1] = vertices[U2];
							vertices[U2] = vertices[U3];
							vertices[U3] = vertices[U4];
							vertices[U4] = tempU;
							break;
						}
						case Cell.ROTATE_180: {
							float tempU = vertices[U1];
							vertices[U1] = vertices[U3];
							vertices[U3] = tempU;
							tempU = vertices[U2];
							vertices[U2] = vertices[U4];
							vertices[U4] = tempU;
							float tempV = vertices[V1];
							vertices[V1] = vertices[V3];
							vertices[V3] = tempV;
							tempV = vertices[V2];
							vertices[V2] = vertices[V4];
							vertices[V4] = tempV;
							break;
						}
						case Cell.ROTATE_270: {
							float tempV = vertices[V1];
							vertices[V1] = vertices[V4];
							vertices[V4] = vertices[V3];
							vertices[V3] = vertices[V2];
							vertices[V2] = tempV;

							float tempU = vertices[U1];
							vertices[U1] = vertices[U4];
							vertices[U4] = vertices[U3];
							vertices[U3] = vertices[U2];
							vertices[U2] = tempU;
							break;
						}
					}
				}
				batch.draw(region.getTexture(), vertices, 0, 20);
				x += layerTileWidth;
			}
			y += layerTileHeight;
		}
	}
	
	protected void drawObject(MapObject object){
		
	}
	
} 