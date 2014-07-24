package com.game.map;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TileMap {

	public Image TileSet = null;

	public int MapX;
	public int MapY;
	public int NTileX, NTileY;
	public int width = 60;
	public int height = 50;
	public int tilePLineTileset;
	public int numLayers;

	public int[][][] map;

	public TileMap(Image tileset, int tilestelaX, int tilestelaY) {
		NTileX = tilestelaX;
		NTileY = tilestelaY;
		TileSet = tileset;
		MapX = 0;
		MapY = 0;
		tilePLineTileset = TileSet.getWidth(null) >> 4;
	}

	public void OpenMap(String name) {

		try {
			InputStream In = getClass().getResourceAsStream(name);
			DataInputStream data = new DataInputStream(In);

			int version = data.readInt(); 
			width = ReadCInt(data); 
			height = ReadCInt(data);
			int ltilex = ReadCInt(data);
			int ltiley = ReadCInt(data);
			byte tileMapName[] = new byte[32];
			data.read(tileMapName, 0, 32); 
			data.read(tileMapName, 0, 32); 
			numLayers = ReadCInt(data);
			int numTiles = ReadCInt(data);
			int bytesPerTile = ReadCInt(data); 
			int empty1 = ReadCInt(data); 
			int empty2 = ReadCInt(data); 

			map = new int[numLayers][height][width];

			if (bytesPerTile == 1) {
				for (int l = 0; l < numLayers; l++) {
					for (int j = 0; j < height; j++) {
						for (int i = 0; i < width; i++) {
							int b1 = data.readByte();
							int b2 = data.readByte();

							map[l][j][i] = ((int) b1 & 0x00ff) | (((int) b2 & 0x00ff) << 8);
						}
					}
				}

			} else if (bytesPerTile == 2) {
				for (int l = 0; l < numLayers; l++) {
					for (int j = 0; j < height; j++) {
						for (int i = 0; i < width; i++) {
							int b1 = data.readByte();
							int b2 = data.readByte();
							int b3 = data.readByte();
							int b4 = data.readByte();

							map[l][j][i] = ((int) b1 & 0x00ff) | (((int) b2 & 0x00ff) << 8) | (((int) b3 & 0x00ff) << 16) | (((int) b4 & 0x00ff) << 24);
						}
					}
				}

			} else {
				for (int l = 0; l < numLayers; l++) {
					for (int j = 0; j < height; j++) {
						for (int i = 0; i < width; i++) {
							int b1 = data.readByte();

							map[l][j][i] = ((int) b1 & 0x00ff);
						}
					}
				}
			}

			data.close();
			In.close();

		}
		catch (Exception e) {
			System.out.println("Something bad, really bady,  has happend." + e.getMessage());
		}
	}

	public void drawItSelf(Graphics2D dbg) {
		int offx = MapX & 0x0f;
		int offy = MapY & 0x0f;
		int sumX, sumY;
		
		sumX = offx > 0 ? 1 : 0;  
		sumY = offy > 0 ? 1 : 0;  
		
		for (int l = 0; l < numLayers; l++) {
			if (l == 0) {
				for (int j = 0; j < NTileY + sumY; j++) {
					for (int i = 0; i < NTileX + sumX; i++) {
						int tilex = (map[l][j + (MapY >> 4)][i + (MapX >> 4)] % tilePLineTileset) << 4;
						int tiley = (map[l][j + (MapY >> 4)][i + (MapX >> 4)] / tilePLineTileset) << 4;

						dbg.drawImage(TileSet, (i << 4) - offx, (j << 4) - offy, (i << 4) + 16 - offx, (j << 4) + 16 - offy, tilex, tiley, tilex + 16, tiley + 16, null);
					}
				}
			} else {
				for (int j = 0; j < NTileY + sumY; j++) {
					for (int i = 0; i < NTileX + sumX; i++) {
						int tilex = (map[l][j + (MapY >> 4)][i + (MapX >> 4)] % tilePLineTileset) << 4;
						int tiley = (map[l][j + (MapY >> 4)][i + (MapX >> 4)] / tilePLineTileset) << 4;

						if ((tilex == 0) && (tiley == 0)) {

						} else {
							dbg.drawImage(TileSet, (i << 4) - offx, (j << 4) - offy, (i << 4) + 16 - offx, (j << 4) + 16 - offy, tilex, tiley, tilex + 16, tiley + 16, null);
						}
					}
				}
			}
		}
	}

	public void setPosition(int x, int y) {
		int X = x >> 4;
		int Y = y >> 4;

		if (X < 0) {
			MapX = 0;
		} else if (X >= (width - NTileX)) {
			MapX = ((width - NTileX)) << 4;
		} else {
			MapX = x;
		}

		if (Y < 0) {
			MapY = 0;
		} else if (Y >= (height - NTileY)) {
			MapY = ((height - NTileY)) << 4;
		} else {
			MapY = y;
		}
	}

	public int ReadCInt(DataInputStream data) throws IOException {
		int b1 = data.readByte();
		int b2 = data.readByte();
		int b3 = data.readByte();
		int b4 = data.readByte();

		return ((int) b1 & 0x00ff) | (((int) b2 & 0x00ff) << 8) | (((int) b3 & 0x00ff) << 16) | (((int) b4 & 0x00ff) << 24);
	}
}