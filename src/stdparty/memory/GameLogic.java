package stdparty.memory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class GameLogic {
	private boolean stopUpdating = true;
	private long startTime = 0;
	private long timeBeforePause = 0;
	private int mistake = 0;
	private int remain;
	private int rowNum;
	private int colNum;
	private Block flipedBlock = null;
	private Block blockData[][] = null;
	
	public interface GraphicsInterface {
		public void updateTimer(long time);
		public void updateBlock();
		public void notifyGameOver();
	}
	private GraphicsInterface graphics;
	
	private class TimerThread extends Thread {
		@Override
		public void run() {
			if(!stopUpdating)
				graphics.updateTimer(System.currentTimeMillis() + timeBeforePause - startTime);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private TimerThread timer;
	private static GameLogic instance;
	
	public static void initLogic(GraphicsInterface g, int r, int c) {
		if(instance != null)
			throw new IllegalStateException("The game instance has been initialized");
		instance = new GameLogic(g, r, c);
	}
	
	public static GameLogic getInstance() {
		return instance;
	}
	
	private GameLogic(GraphicsInterface g, int r, int c) {
		blockData = new Block[r][c];
		graphics = g;
		rowNum = r;
		colNum = c;
		if((r * c / 2) * 2 != r * c) // odd
			throw new IllegalArgumentException("The number of total blocks cannot be odd");
		timer = new TimerThread();
		timer.start();
	}
	
	public void startGame() {
		mistake = 0;
		stopUpdating = false;
		remain = rowNum * colNum / 2;
		blockData = generateBlock(new Block[rowNum][colNum]);
		startTime = System.currentTimeMillis();
	}

	public void clickObject(int r, int c) {
		if(stopUpdating)
			return;
		Block target = blockData[r][c];
		if(flipedBlock == null)
			flipedBlock = target.flip();
		else if(flipedBlock.equals(target))
			flipedBlock = clear(flipedBlock, target);
		else
			flipedBlock = recordMistake();
	}
	
	public void pause() {
		timeBeforePause += System.currentTimeMillis() - startTime;
		startTime = 0;
		stopUpdating = true;
	}
	
	public void resume() {
		startTime = timeBeforePause;
		stopUpdating = false;
	}
	
	private Block recordMistake() {
		mistake++;
		flipedBlock.reset();
		return null;
	}
	
	private Block clear(Block b1, Block b2) {
		if(--remain == 0) {
			stopUpdating = true;
			graphics.notifyGameOver();
		}
		return b1.clear(b2);
	}
	
	private Block[][] generateBlock(Block[][] buf) {
		ArrayList<Integer> shuffleID = new ArrayList<>();
		for(int i = 0; i < rowNum * colNum; i++)
			shuffleID.add(i / 2);
		Collections.shuffle(shuffleID);
		for(int i = 0; i < rowNum; i++)
			for(int j = 0; j < colNum; j++)
				buf[i][j] = new Block(i, j, (Integer) shuffleID.toArray()[i * colNum + j], graphics);
		return buf;
	}

	public int getRowNum() {
		return rowNum;
	}

	public int getColNum() {
		return colNum;
	}
	
	public Block getBlock(int r, int c) {
		return blockData[r][c];
	}
	
}
