package com.game.main;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.game.actor.Bullet;
import com.game.actor.Item;
import com.game.actor.Monster;
import com.game.actor.MyObject;
import com.game.actor.Sprite;
import com.game.map.TileMap;

public class GamePanel extends JPanel implements Runnable {

	private static final int PWIDTH = 640; // size of panel
	private static final int PHEIGHT = 480;
	private Thread animator; // for the animation
	private boolean running = false; // stops the animation
//	public boolean gameOver = false; // for game termination

	public Random rnd = new Random();
	
	private static boolean gamePause; 
	public static GamePanel instance;

	private Graphics2D dbg;
	private Image dbImage = null;

	private Image heroImage;
	private Image monstersImage;

	public static TileMap MAP;

	public static  int framesPerSecond, framesCount;
	public static long currentSecond = 0;
	public static long newSecond = 0;


	private long timeShoting = 0;
	public Sprite player;
	
	public ArrayList<MyObject> objectList;
	public ArrayList<Bullet> bulletList;

	
	public boolean keyUPPressed, keyDownPressed, keyLeftPressed, keyRightPressed, keySpacePressed;

	public double cro;
	
	public GamePanel() {
		/*
		 * Setting up Variables
		 */
		instance = this;
		gamePause = false;

		cro = 0;
		keyUPPressed = false;
		keyDownPressed = false;
		keyLeftPressed = false;
		keyRightPressed = false;
		keySpacePressed = false;

		
		/*
		 * Setting up screen configuration
		 */
		setBackground(Color.white); // white background
		setPreferredSize(new Dimension(PWIDTH, PHEIGHT));
		setFocusable(true);
		requestFocus(); // JPanel now receives key events


		/*
		 * Setting up Events
		 */
		
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				keyboardListnerPressed(e);
			}
			public void keyReleased(KeyEvent e) {
				keyboardListnerReleased(e);
			}
		});

		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				mouseEventListner(e.getX(), e.getY());
			}
		});


		

		/*
		 * Loading Resources
		 * 
		 */
		try {
			Image tileset = ImageIO.read(getClass().getResource("/mapObjects.png"));
			MAP = new TileMap(tileset, 40, 30);
			MAP.OpenMap("/mapa.map");

		} catch (IOException e) {
			System.out.println("Load Image error:");
		}
		
		
		try {
			monstersImage = ImageIO.read(getClass().getResource("/monster.png"));
		} catch (IOException e) {
			System.out.println("Load Image error:");
		}

		try {
			heroImage = ImageIO.read(getClass().getResource("/chara1O.png"));
		} catch (IOException e) {
			System.out.println("Load Image error:");
		}
		
		/*
		 * Creating creatures and objects into the game  
		 */
		
		player = new Sprite(heroImage, 20, 20, 0);
		player.FrameTime = 200;
		player.velocity = 150;

		objectList = new ArrayList<MyObject>();
		bulletList = new ArrayList<Bullet>();

		for (int i = 0; i < 10; i++) {
			Monster enemy = new Monster(monstersImage, rnd.nextInt(640), rnd.nextInt(480), rnd.nextInt(8));
			enemy.FrameTime = 100 + rnd.nextInt(400);
			enemy.VelX = -100 + rnd.nextInt(200);
			enemy.VelY = -100 + rnd.nextInt(200);
			enemy.direction = rnd.nextInt(4);

			enemy.velocity = 50 + rnd.nextInt(100);
			objectList.add(enemy);
		}
	} 

	private void mouseEventListner(int x, int y) {
		x +=MAP.MapX;
		y +=MAP.MapY;
		
		Bullet bullet = new Bullet((int) player.X  + 12, (int) player.Y + 16, MAP.height, MAP.width);

		int difx = (int) (x - player.X-12);
		int dify = (int) (y - player.Y-16);

		double ang = Math.atan2(dify, difx);
		
		bullet.VelX = (int) (Math.cos(ang) * 500);
		bullet.VelY = (int) (Math.sin(ang) * 500);

		bulletList.add(bullet);
	}

	public void keyboardListnerPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();

		if ((keyCode == KeyEvent.VK_ESCAPE) || (keyCode == KeyEvent.VK_Q) || (keyCode == KeyEvent.VK_END) || ((keyCode == KeyEvent.VK_C) && e.isControlDown())) {
			running = false;
		}

		keyLeftPressed |= (keyCode == KeyEvent.VK_LEFT);
		keyRightPressed |= keyCode == KeyEvent.VK_RIGHT;
		keyUPPressed |= keyCode == KeyEvent.VK_UP;
		keyDownPressed |= keyCode == KeyEvent.VK_DOWN;
		keySpacePressed |= keyCode == KeyEvent.VK_SPACE;

		if (keyCode == KeyEvent.VK_F4) {
			gamePause = !gamePause;
		}
	}

	public void keyboardListnerReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();

		switch (keyCode) {
			case KeyEvent.VK_LEFT: keyLeftPressed = false; break;
			case KeyEvent.VK_RIGHT: keyRightPressed = false; break;
			case KeyEvent.VK_UP: keyUPPressed = false; break;
			case KeyEvent.VK_DOWN: keyDownPressed = false; break;
			case KeyEvent.VK_SPACE: keySpacePressed = false; break;
		}
	}

	public void addNotify() {
		super.addNotify(); // creates the peer
		startGame(); // start the thread
	}

	private void startGame() {
		if (animator == null || !running) {
			animator = new Thread(this);
			animator.start();
		}
	} 

	
	public void stopGame()	{
		running = false;
	}

	public void run() {
		running = true;

		long difTime, previousTime;

		difTime = 0;
		previousTime = System.currentTimeMillis();
		currentSecond = (long) (previousTime / 1000);

		while (running) {
			gameUpdate(difTime); // game state is updated
			gameRender(); // render to a buffer
			repaint(); // paint with the buffer

			try {
				Thread.sleep(20); // sleep a bit
			} catch (InterruptedException ex) {
			}

			difTime = System.currentTimeMillis() - previousTime;
			previousTime = System.currentTimeMillis();
			newSecond = (long) (previousTime / 1000);

			if (newSecond != currentSecond) {
				framesPerSecond = framesCount;
				currentSecond = newSecond;
				framesCount = 1;
			} else {
				framesCount++;
			}
		}
		System.exit(0); // so enclosing JFrame/JApplet exits
	} // end of run()

	
	private void gameUpdate(long diffTime) {
		if ((!gamePause) && (!player.dead)) {
			float oldx = player.X;
			float oldy = player.Y;
			cro += diffTime;

			if (keyLeftPressed) {
				player.X += -((player.velocity * diffTime) / 1000.0f);
				player.direction = 3;
				player.frame = (((int) cro / 100) % 3);
			}
			if (keyRightPressed) {
				player.X += ((player.velocity * diffTime) / 1000.0f);
				player.direction = 1;
				player.frame = (((int) cro / 100) % 3);
			}
			if (keyUPPressed) {
				player.Y += -((player.velocity * diffTime) / 1000.0f);
				player.direction = 0;
				player.frame= (((int) cro / 100) % 3);
			}
			if (keyDownPressed) {
				player.Y += ((player.velocity * diffTime) / 1000.0f);
				player.direction = 2;
				player.frame = (((int) cro / 100) % 3);
			}

			if ((player.X < 0) || ((player.X + 24) > MAP.width << 4)) {
				player.X = oldx;
			}
			if ((player.Y < 0) || ((player.Y + 32) > MAP.height << 4)) {
				player.Y = oldy;
			}
			if ((MAP.map[1][((int) (player.Y + 30)) >> 4][((int) (player.X + 12)) >> 4] != 0)||
				(player.verifyColision())) {
				player.X = oldx;
				player.Y = oldy;
			}

			MAP.setPosition((int) player.X - 320, (int) player.Y - 240);

			if (keySpacePressed) {
				if ((timeShoting/diffTime) > 4) {
					bulletList.addAll(player.shot(MAP));
					
					timeShoting = 0;
				}
			}

			for (int i = 0; i < objectList.size(); i++) {
				objectList.get(i).update(diffTime);
			}
			for (int i = 0; i < bulletList.size(); i++) {
				bulletList.get(i).update(diffTime);
			}

			timeShoting += diffTime;
		}
	}

	private void gameRender() {
		if (dbImage == null) { 
			dbImage = createImage(PWIDTH, PHEIGHT);

			if (dbImage == null) {
				System.out.println("dbImage is null");
				return;
			} else {
				dbg = (Graphics2D) dbImage.getGraphics();
			}
		}

		// clear the background
		MAP.drawItSelf(dbg);
		// draw game elements
		dbg.setColor(Color.WHITE);
		dbg.drawString("FPS: " + framesPerSecond, 0, 10);
		
		for (int i = 0; i < objectList.size(); i++) {
			MyObject o = objectList.get(i);
			if (o != null){
				if (o.dead){
					if (o.getClass().getName() == "Sprite"){
						objectList.remove(i);
						o = new Item((int)((Sprite) o).X, (int)((Sprite) o).Y, MAP.MapX,MAP.MapY);

						objectList.add(i, o);
					}
					else {
						objectList.remove(i);
					}
				}
				else {
					objectList.get(i).drawItSelf(dbg, MAP.MapX,MAP.MapY);
				}
			}
		}

		for (int i = 0; i < bulletList.size(); i++) {
			Sprite s = bulletList.get(i);
			if (s != null) {
				if (s.dead) {
					bulletList.remove(i);
				}
				else {
					s.drawItSelf(dbg, MAP.MapX,MAP.MapY);
				}
			}
		}
		
		player.drawItSelf(dbg, MAP.MapX, MAP.MapY);
		if (gamePause){
			dbg.setColor(Color.black);
			dbg.drawRect((int)player.X - MAP.MapX + 25, (int)player.Y - MAP.MapY + 4, 72, 102);
			dbg.setColor(new Color(10,10,10,200));
			dbg.fillRect((int)player.X - MAP.MapX + 26, (int)player.Y - MAP.MapY + 5, 71, 101);
		}
		
		if (player.dead){
			dbg.setColor(new Color(20,20,20,100));
			dbg.fillRect(0,0, PWIDTH, PHEIGHT);
		}
		
	} 


	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (dbImage != null) {
			g.drawImage(dbImage, 0, 0, null);
		}
	}

	/**
	 * Main responsible to create a screen for rendering the game
	 */
	public static void main(String args[]) {
		GamePanel ttPanel = new GamePanel();

		JFrame app = new JFrame("Swing Timer Test");

		app.getContentPane().add(ttPanel, BorderLayout.CENTER);
		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		app.pack();
		app.setResizable(false);
		app.setVisible(true);
	} 
}

