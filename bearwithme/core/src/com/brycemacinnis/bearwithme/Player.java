package com.brycemacinnis.bearwithme;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Player {
	
	//The texture of the player.
	public Sprite sprite;
	
	//The position the player begins at.
	public Vector2 startPosition;
	
	//The position the player is at, sprite is drawn here.
	public Vector2 position;
	
	//The initial speed of the player before any debuffs.
	public float initialSpeed;
	
	//The speed of the player
	public float speed;
	
	public final int maxHealth = 10;
	public int health; 

	//For switching screens to the game over screen.
	BearWithMe game;
	
	boolean isAlive;
	
	//The time as a zombie before the game over screen is shown.
	float deathDelay = 25.0f;
	
	Player(BearWithMe game) {
		//Get the player's texture from /ass
		sprite = new Sprite(new Texture(Gdx.files.internal("Prototype.png")), 46, 72);
		
		//Bears are brown, and the color should be brown.
		sprite.setColor(Color.BROWN);
		
		//Set the speed of the player
		speed = 1f;
		
		//Center the player
		startPosition = new Vector2(100,100);
		position = startPosition;
		
		sprite.setPosition(position.x, position.y);
		
		health = maxHealth;
		isAlive = true;
		
		this.game = game;
		
		//Set at the beginning, to allow for resetting later on.
		initialSpeed = speed;
	}
	
	
	//Timer is used to delay game over screen.
	float timer = 0.0f;
	
	//Update function
	public void render(SpriteBatch batch) {
  		
  		//Draw our friendly little bear
		batch.begin();
		sprite.draw(batch);
		batch.end();

		//Move the player
		handleMovement();
		
		//Switch screens if the player is dead after timer
		if (!isAlive) {
			
			//Add time to the timer.
			timer += Gdx.graphics.getDeltaTime();
			
			//If dead for time delay show the game over screen.
			if (timer > deathDelay) {
				game.setScreen(new EndScreen(game));
				
			}
		}
		

	}
	
	//Move the player, if the player moves to a collision tile,reverse the movement
	private void handleMovement() {
		float movement = 100 * speed * Gdx.graphics.getDeltaTime();
		
		if (Gdx.input.isKeyPressed(Input.Keys.W)) {
			position.y += movement;
			
			if (playerCollides())
				position.y -= movement;
		}
		
		else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
			position.y -= movement;
			
			if (playerCollides())
				position.y += movement;
		}
		
		else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			position.x -= movement;
			
			if (playerCollides())
				position.x += movement;
		}
		
		else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			position.x += movement;
			
			if (playerCollides())
				position.x -= movement;
		}
		
		
		//Adjust the sprite to the position
		sprite.setPosition(position.x, position.y);
	}

	//Get the tile position of a value
	private int convertFloatToWorld(float position) {
		return (int)Math.floor(position / Map.tileWidth);
	}

	//Get the tile of a position
	private Tile getTile(float x, float y) {
		return Map.tiles[convertFloatToWorld(x)][convertFloatToWorld(y)];
	}
	
	///If any of the vertices are in a collision tile return true.
	private boolean playerCollides() {
		//Bottom left corner collision
		if (getTile(position.x, position.y).isCollider)
			return true;
		//Top left corner collision
		else if (getTile(position.x, position.y + sprite.getHeight()).isCollider)
			return true;
		//Bottom right corner collision
		else if (getTile(position.x + sprite.getWidth(), position.y).isCollider)
			return true;
		//Top right corner collision
		else if (getTile(position.x + sprite.getWidth(), position.y + sprite.getHeight()).isCollider)
			return true;
		else
			return false;
	}
	
	//This should be called by other entities.
	//Takes as much damage as possible before death, never reaches below 0.
	public void takeDamage(int amount) {
		if (health - amount > 0)
			health -= amount;
		else if (health - amount == 0) {
			health -= amount;
			death();
		}
	}
	
	//Heals as much as possible until it reaches maxHealth
	public void heal(int amount) {
		
		//Once you are dead you can no longer heal.
		if (health + amount <= maxHealth && isAlive)
			health += amount;
	}
	
	//Turns the player into a zombie for 30 seconds uwwntil a game over screen appears.
	private void death() {
		sprite.setColor(Color.OLIVE);
		
		//Used to prevent healing while dead and displaying game over screen.
		isAlive = false;
		
		//Slow the zombie down
		speed /= 2;
	}
	
	public void reset() { 
		position = startPosition;
		speed = initialSpeed;
		health = maxHealth;
		
		//Because all healthy bears should be brown.
		sprite.setColor(Color.BROWN);
	}

}	