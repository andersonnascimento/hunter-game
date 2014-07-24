package com.game.actor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

public class Item extends MyObject {
	
	public final byte HEALTH_SUPLY = 1;
	public final byte INCREASE_DAMAGE = 2;
	public final byte POISON = 3;
	public final byte GUN_SHOT = 4;
	
	protected byte type;
	public int endurance;
	public int life;
	public int velocity;
	public byte damage;
	public byte numberOfShots;
	public Random rnd;
	
	public Item(int x, int y) {
		super(x,y);
		rnd = new Random();
		type = (byte) rnd.nextInt(5);
		dead = false;
		BonificacaoDoItem();
	}

	public Item(int x, int y, int hMapa, int wMapa) {
		super(x, y, hMapa, wMapa);
		rnd = new Random();

		type = (byte) rnd.nextInt(5);
		BonificacaoDoItem();
	}

	public void BonificacaoDoItem(){
		switch (type) {
			case 0: life = (1 * rnd.nextInt(6)) * 10; break; 
			case 1: numberOfShots = (byte) rnd.nextInt(10); break;
			case 2: damage = (byte) (1+rnd.nextInt(6)); break;
			case 3: velocity = (byte) rnd.nextInt(3) * 3; break;
			case 4: endurance = (1 +rnd.nextInt(5)) * 10; break;
		}
	}

	//@Override
	public void DesenhaSe(Graphics2D dbg, int xmapa, int ymapa) {
		
	}
	public String getClassName(){
		return "Itens";
	}

	@Override
	public boolean verifyColision() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void drawItSelf(Graphics2D dbg, int Xmundo, int Ymundo) {
		if (!this.dead){
			switch (type) {
				case 0: dbg.setColor(Color.RED); break; 
				case 1: dbg.setColor(Color.BLACK); break;
				case 2: dbg.setColor(Color.BLUE); break;
				case 3: dbg.setColor(Color.YELLOW); break;
				case 4: dbg.setColor(Color.GRAY); break;
			}
				
			dbg.fillRect((int) (X - Xmundo), ((int) Y - Ymundo),20,20);
		}
	}

	@Override
	public void update(long diftime) {
		// TODO Auto-generated method stub
	}
}
