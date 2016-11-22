package stdparty.memory;

import java.util.LinkedList;

import stdparty.memory.GameLogic.GraphicsInterface;

public class Block {
	private static class DelayedCoverThread extends Thread {
		private DelayedCoverThread() {
			a = new LinkedList<>();
		}
		
		private static DelayedCoverThread instance;
		
		public static DelayedCoverThread getInstance() {
			return instance;
		}
		
		static {
			instance = new DelayedCoverThread();
			instance.start();
		}
		
		private LinkedList<Block> a;
		
		@Override
		public void run() {
			// Flipped the card that the player failed to match back every 2s
			while(true) {
				synchronized(a) {
					while(!a.isEmpty()) {
						a.getFirst().cover();
						a.removeFirst();
					}
				}
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		public void addBlock(Block b) {
			synchronized(a) {
				a.addFirst(b);
			}
		}
	}
	
	public enum Status {Covered, Fliped, Cleared};
	
	public int r;
	public int c;
	private int pictureID;
	private Status status;
	private GraphicsInterface graphics;
	private boolean delayedCover;
	
	public Block(int row, int col, int pictureID, GraphicsInterface g) {
		this.pictureID = pictureID;
		status = Status.Covered;
		graphics = g;
		r = row;
		c = col;
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Block? pictureID == ((Block)obj).pictureID: false);
	}
	
	// Called when a card is covered and clicked by player.
	public synchronized Block flip() {
		// TODO ILLEGAL STATUS: THROW AN EXCEPTION HERE
		if(status == Status.Fliped || status == Status.Cleared)
			return null;
		else
			status = Status.Fliped;
		// when it is clicked when it should be flipped back after a short interval, the flag should be 
		// reset to avoid being covered at which the card should be shown
		delayedCover = false;
		graphics.updateBlock();
		return this;
	}
	
	// Called when a card is flipped and clicked by player, and the player fails to match a pair of card.
	public synchronized Block reset() {
		if(status == Status.Cleared)
			throw new IllegalStateException("The block cannot be reseted when it has been cleared");
		delayedCover = true;
		DelayedCoverThread.getInstance().addBlock(this);
		return this;
	}
	
	// Called by the delay thread to flipped the card back. 
	public synchronized Block cover() {
		// In this situation, the block is clicked again so it won't be covered
		if(!delayedCover)
			return this;
		status = Status.Covered;
		delayedCover = false;
		graphics.updateBlock();
		return this;
	}
	
	// Called when a card is flipped and clicked by player, and the player fails to match a pair of card. 
	public synchronized Block clear(Block another) {
		if(status == Status.Covered)
			throw new IllegalStateException("The block cannot be cleared when covered");
		else if(status == Status.Fliped) {
			status = Status.Cleared;
			graphics.updateBlock();
		}
		return another == null ? null : another.clear(null);
	}
	
	public Status getStatus() {
		return status;
	}

	public int getPictureID() {
		return pictureID;
	}
	
	@Override
	public String toString() {
		return "Block(row: " + r + ", col: " + c + ", pic: " + pictureID + ")";
	}
}
