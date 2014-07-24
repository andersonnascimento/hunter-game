package com.game.actor;
import java.awt.Graphics2D;


public abstract class MyObject {
	public boolean dead;
	public int heightMap;
	public int widthMap;
	
	public float X, Y;

	abstract public boolean verifyColision();
	abstract public void drawItSelf(Graphics2D dbg, int Xworld, int Yworld);
	abstract public void update(long diftime);
	
	public MyObject(int x, int y){
		X = x;
		Y = y;

		dead = false;
	}
	
	public MyObject(int x, int y, int hMap, int wMap){
		X = x;
		Y = y;
		dead = false;
		heightMap = hMap << 4;
		widthMap = wMap << 4;	
	}
	
	public String getClassName(){
		return "MyObject";
	}
}
