package stdparty.memory;

import stdparty.memory.GameLogic.GraphicsInterface;

public class Block {
	public enum Status {Covered, Fliped, Cleared};
	
	public int r;
	public int c;
	private int pictureID;
	private Status status;
	private GraphicsInterface graphics;
	
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
	
	public Block flip() {
		if(status == Status.Fliped || status == Status.Cleared)
			return null;
		else
			status = Status.Fliped;
		graphics.updateBlock();
		return this;
	}
	
	public Block reset() {
		if(status == Status.Cleared)
			throw new IllegalStateException("The block cannot be reseted when it has been cleared");
		status = Status.Covered;
		graphics.updateBlock();
		return this;
	}
	
	public Block clear(Block another) {
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
