package game_snake;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {

	// Variables
	final static int WIDTH = 30;
	final static int HEIGHT = 25;
	
	static int speed = 7;
	static int foodColor = 0;
	static int foodX = 0;
	static int foodY = 0;
	static int cornerSize = 25;
	
	static List<Corner> snake = new ArrayList<Corner>();
	
	static Dir direction = Dir.left;
	
	static boolean isGameOver = false;
	
	static Random rand = new Random();
	
	public enum Dir {left, right, up, down }
	
	// Corner.
	public static class Corner {
		private int x;
		private int y;
		
		public Corner(int otherX, int otherY) {
			this.x = otherX;
			this.y = otherY;
		}
	}
	
	// Let's start playing Snake!
	@Override
	public void start(Stage primaryStage) throws Exception {

		primaryStage.setResizable(false);

		try {
			newFood();
			VBox root = new VBox();
			Canvas canvas = new Canvas(WIDTH * cornerSize, HEIGHT * cornerSize);
			GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
			root.getChildren().add(canvas);
		
			// The AnimationTimer is out flip book.
			new AnimationTimer() {
				long lastTick = 0;
				
				// 1 minute of ticks = we get a new frame every 'speed' second.
				// More speed = more frames = faster snake!
				@Override
				public void handle(long now) {
						if(lastTick == 0 ) {
							lastTick = now;
							tick(graphicsContext);
							return;
						}
						
						if(now - lastTick > 1000000000 / speed) {
							lastTick = now;
							tick(graphicsContext);
						}
				}
			}.start();
			
			Scene scene = new Scene(root,WIDTH * cornerSize, HEIGHT * cornerSize);
			
			// Controllers.
			// Set the keyboard controls to W A S D.
			scene.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
				if((key.getCode() == KeyCode.UP) && (direction != Dir.down)) { direction = Dir.up; }
				if((key.getCode() == KeyCode.DOWN) && (direction != Dir.up)) { direction = Dir.down; }
				if((key.getCode() == KeyCode.LEFT) && (direction != Dir.right)) { direction = Dir.left; }
				if((key.getCode() == KeyCode.RIGHT) && (direction != Dir.left)) { direction = Dir.right; }
				
			});
			
			// adds start snake parts.
			// sets the snake to 3 parts (corners) at the beginning.
			snake.add(new Corner(WIDTH/2, HEIGHT/2));
			snake.add(new Corner(WIDTH/2, HEIGHT/2));
			snake.add(new Corner(WIDTH/2, HEIGHT/2));
			
			//scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("S N A K E");
			
			primaryStage.show();
		} catch (Exception e) { e.printStackTrace(); } 
	}
	
	// tick
	public static void tick(GraphicsContext graphicsContext) {
		if(isGameOver == true) {
			// Clears the board.
			graphicsContext.clearRect(0, 0, (WIDTH * cornerSize), (HEIGHT * cornerSize));
			graphicsContext.setFill(Color.BLACK);
			graphicsContext.fillRect(0, 0, (WIDTH * cornerSize), (HEIGHT * cornerSize));
			
			// Writes 'Game Over'.
			graphicsContext.setFill(Color.RED);
			graphicsContext.setFont(new Font("", 50));
			graphicsContext.fillText("Game over", 100, 250);
			return;
		}
		
		for(int i = snake.size() - 1; i >= 1; i--) {
			snake.get(i).x = snake.get(i-1).x;
			snake.get(i).y = snake.get(i-1).y;
		}
		
		// Choose the direction of the snake.
		// Game over if the snake touches a border.
		switch(direction) {
		case up:
			snake.get(0).y--;
			if(snake.get(0).y < 0 ) { isGameOver = true;}
			break;
		case down:
			snake.get(0).y++;
			if(snake.get(0).y > HEIGHT ) { isGameOver = true;}
			break;
		case left:
			snake.get(0).x--;
			if(snake.get(0).x < 0 ) { isGameOver = true;}
			break;
		case right:
			snake.get(0).x++;
			if(snake.get(0).x > WIDTH ) { isGameOver = true;}
			break;
		}
		
		// eat food.
		if((foodX == snake.get(0).x) && (foodY == snake.get(0).y)) {
			// Let the snake grow.
			snake.add(new Corner(-1,-1));
			newFood();
		}
		
		// self destroy.
		// Game over if the snake hit itself.
		for(int i = 1; i< snake.size(); i++) {
			if((snake.get(0).x == snake.get(i).x) && (snake.get(0).y == snake.get(i).y)) { isGameOver = true; }
		}
		
		// Fill the background in black.
		graphicsContext.setFill(Color.BLACK);
		graphicsContext.fillRect(0, 0, (WIDTH * cornerSize), (HEIGHT * cornerSize));
		
		// Fill the score in white with standard font on position 10,3.
		graphicsContext.setFill(Color.WHITE);
		graphicsContext.setFont(new Font("", 30));
		graphicsContext.fillText("Score: " +(speed-6), 10, 30);
		
		// Random food color
		Color randomFoodColor = Color.WHITE;
		
		switch(foodColor) {
		case 0: randomFoodColor = Color.PURPLE; break;
		case 1:	randomFoodColor = Color.LIGHTBLUE; break;
		case 2: randomFoodColor = Color.YELLOW; break;
		case 3: randomFoodColor = Color.PINK; break;
		case 4: randomFoodColor = Color.ORANGE; break;
		}
		
		// Fruit (food).
		graphicsContext.setFill(randomFoodColor);
		graphicsContext.fillOval((foodX * cornerSize), (foodY * cornerSize), cornerSize, cornerSize);
	
		// Snake
		// Paints the snake in 2 colors and sizes.
		// Light green as shadow and green as foreground.
		for (Corner corner : snake) {
			graphicsContext.setFill(Color.LIGHTGREEN);
			graphicsContext.fillRect((corner.x * cornerSize), (corner.y * cornerSize), (cornerSize - 1), (cornerSize - 1));
			graphicsContext.setFill(Color.GREEN);
			graphicsContext.fillRect((corner.x * cornerSize), (corner.y * cornerSize), (cornerSize - 2), (cornerSize - 2));
		}	
	}
	
	
	// Food.
	// We place new food on random location. foodX * foodY on the canvas (if there is no snake).
	public static void newFood() {
		start: while (true) {
			foodX = rand.nextInt(WIDTH);
			foodY = rand.nextInt(HEIGHT);

			for (Corner c : snake) {
				if (c.x == foodX && c.y == foodY) {
					continue start;
				}
			}
			
			foodColor = rand.nextInt(5);
			speed++;
			break;
		}
	}
	
	// Main.
	public static void main(String[] args) {
		launch(args);
	}
}

