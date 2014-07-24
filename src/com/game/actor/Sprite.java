package com.game.actor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Random;

import com.game.main.GamePanel;
import com.game.map.TileMap;

public class Sprite extends MyObject {

	public Image Charset = null;

	private int shotDamage;
	
	public Random rnd;
	public short endurance;
	public short life;
	//float X, Y;
	public int VelX, VelY;
	public int FrameTime;
	public long time;
	public int direction;
	public int animation;
	public int character;
	public int frame;
	public int charX, charY;
	public int velocity;

	public long timeRemoveOut;
	public int timeAtack;
	
	public Sprite(Image charset, int x, int y, int charac) {
		super(x,y);
		timeRemoveOut = 0;
		rnd = new Random();
		Charset = charset;
		character = charac;

		charX = (charac % 4) * 72;
		charY = (charac / 4) * 128;

		FrameTime = 200;
		time = 0;
		direction = 0;
		VelX = 0;
		VelY = 0;

		dead = false;
		
		life = (short)(100 + (rnd.nextInt(155)));
		endurance = life;
		timeAtack = 0;

	}

	public void drawItSelf(Graphics2D dbg, int Xmundo, int Ymundo) {
		if (timeRemoveOut < 900000) {
			int x = (int) (X - Xmundo);
			int y = (int) (Y - Ymundo);
			
			dbg.setColor(Color.black);
			dbg.drawRect(x, y, 31, 5);
			dbg.setColor(Color.red);
			dbg.fillRect(x+1, y+1, (int)(30 * life / endurance) , 4);
			dbg.drawImage(Charset, x, y, x + 24, y + 32, charX + frame* 24, charY + direction * 32, (charX + frame * 24) + 24,(charY + direction * 32) + 32, null);	
		}
	}

	public void update(long diffTime) {
		
		if (!this.dead) {
			time += diffTime;
			frame = ((int) time / FrameTime) % 3;
			float oldX = X;
			float oldY = Y;

			X += (diffTime * VelX) / 1000.0f;
			Y += (diffTime * VelY) / 1000.0f;

			if (verifyColision()){
				X = oldX;
				Y = oldY;
			}

			if ((GamePanel.instance.player.X -5 < X + 24 -5) && (GamePanel.instance.player.X + 24 - 5 > X - 5) && 
				(GamePanel.instance.player.Y -7< Y + 32-7) && (GamePanel.instance.player.Y + 32-7 > Y-7)) {
				X = oldX;
				Y = oldY;

				if (GamePanel.instance.player.wasAttacked(diffTime)) {
					dead = true;
				}
			}
			
			if (Math.abs(VelX) > Math.abs(VelY)) {
				if (VelX > 0) {
					direction = 1;
				} else {
					direction = 3;
				}
			} else {
				if (VelY > 0) {
					direction = 2;
				} else {
					direction = 0;
				}
			}

			int difx = (int) (GamePanel.instance.player.X - X);
			int dify = (int) (GamePanel.instance.player.Y - Y);

			double ang = Math.atan2(dify, difx);

			VelX = (int) (Math.cos(ang) * velocity);
			VelY = (int) (Math.sin(ang) * velocity);
		}
		else if (timeRemoveOut < 900000){
			this.timeRemoveOut += time;
		}
	}
	
	
	public boolean verifyColision(){
		boolean output = false;
		int mapX = ((int) (Y + 30)) >> 4;
		int mapY = ((int) (X + 12)) >> 4;
		
		try {
			int x = GamePanel.instance.MAP.map[1][mapX][mapX];
		} catch (Exception e) {
			System.out.println(mapX + "    " +mapX);
		} 
		
		//map colision
		if (((X >= GamePanel.instance.MAP.width << 4) || (X <= 0) || (Y >= GamePanel.instance.MAP.height << 4) || (Y <= 0)) ||
			((GamePanel.instance.MAP.map[1][mapX][mapY] != 0))){
				output = true;
		}
		//object colision
		else{
			for (int i = 0; i < GamePanel.instance.objectList.size(); i++) {
	    		MyObject spr = (MyObject) (GamePanel.instance.objectList.get(i));
	    		if (spr.getClassName().equals("Itens")) {
	    			if ((spr.X-5 < X+24-5) && (spr.X+24-5 > X-5)&& (spr.Y-7 < Y+32-7) && (spr.Y+32-7 > Y-7)){
		    			this.hasGotItem((Item)(spr));
		    			spr.dead = true;
					}
	    		}
	    		else if ((!spr.equals(this)) && (!spr.dead)){
	    			if ((spr.X-5 < X+24-5) && (spr.X+24-5 > X-5)&&
	    			    (spr.Y-7 < Y+32-7) && (spr.Y+32-7 > Y-7)){
	    				output = true;
						break;
					}
				}
			}
		}
		return output;
	}

	public boolean wasAttacked(long diftime){
		if (timeAtack > 700) {
			life -= rnd.nextInt(15);
			dead = (life < 0);	
			timeAtack = 0;
		}
		else{
			timeAtack +=diftime;
		}	
		return dead;
	}

	public void hasGotItem(Item item) {
		this.life += item.life;
		this.endurance += item.endurance;
		this.velocity += item.velocity;
		this.shotDamage = this.shotDamage > item.damage ? this.shotDamage : item.damage;

		// Verifica se o life agora é maior q sua resistencia
		this.life = this.life > this.endurance ? this.endurance : this.life;
	}
	
	public String getClassName(){
		return "Sprite";
	}

	public ArrayList<Bullet> shot(TileMap MAPA){
		ArrayList<Bullet> shotList = new ArrayList<Bullet>();
		
		Bullet proj = new Bullet((int) this.X + 12, (int) this.Y + 16, MAPA.height, MAPA.width);
		proj.damage = shotDamage;
			
		switch (this.direction) {
			case 0: proj.VelY = -500; break; 
			case 1: proj.VelX = 500; break;
			case 2: proj.VelY = 500; break;
			case 3: proj.VelX = -500; break;
		}
		shotList.add(proj);

		return shotList;
	}
}
