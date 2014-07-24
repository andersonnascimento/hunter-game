package com.game.actor;

import java.awt.Color;
import java.awt.Graphics2D;

import com.game.main.GamePanel;

public class Bullet extends Sprite {
	public int heightMap;
	public int widthMap;
	public int damage;
	
	public Bullet(int x, int y) {
		super(null, x, y, 0);
		damage = 1;
	}

	public Bullet(int x, int y, int hMap, int wMap) {
		super(null, x, y, 0);
		heightMap = hMap << 4;
		widthMap = wMap << 4;
		damage = 1;
	}
	
	@Override
	public void update(long diftime) {
		if (!this.dead){
			time += diftime;
			frame = ((int) time / FrameTime) % 3;

			X += (diftime * VelX) / 1000.0f;
			Y += (diftime * VelY) / 1000.0f;
			
			if ((this.X >= widthMap) || (this.Y >= heightMap) || (this.X <= 0) || (this.Y <= 0)||
				(GamePanel.instance.MAP.map[1][((int) Y) >> 4][((int) X) >> 4] != 0)) {
				this.dead = true;
			} else {
				for (int i = 0; i < GamePanel.instance.objectList.size(); i++) {
					MyObject monster = (MyObject) GamePanel.instance.objectList.get(i);
					
					if ((!monster.getClassName().equals("Itens")) && (monster != null) && (!monster.equals(this)) && (!monster.dead)){
						if ((monster.X+5 < X+5) && (monster.X+24 > X)&& (monster.Y+7 < Y+5) && (monster.Y+32 > Y)){
							if (monster.getClassName().equals("Monster")) {
								((Monster) monster).wasShot(this.damage);
							}
							this.dead = true;
							break;
						}
					}
				}
			}
		}
	}

	@Override
	public void drawItSelf(Graphics2D dbg, int xmapa, int ymapa) {
		if (!this.dead){
			dbg.setColor(Color.black);
			dbg.fillOval((int) X - xmapa - 2, (int) Y - ymapa - 2, 4, 4);
		}
	}
}
