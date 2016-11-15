package stdparty.memory;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

public final class GameCanvas {
	/**
	 * @wbp.factory
	 */
	public static JPanel createJPanel() {
		JPanel panel = new JCanvas/*/JPanel/**/();
		return panel;
	}
	
	public static class JCanvas extends JPanel {
		private Image[] imageList;
		
		@Override
		protected void paintComponent(Graphics g) {
			int l = getSize().height / GameLogic.getInstance().getColNum();
			int w = getSize().width / GameLogic.getInstance().getRowNum();
			for(int i = 0; i < GameLogic.getInstance().getRowNum(); i++) {
				for(int j = 0; j < GameLogic.getInstance().getColNum(); j++) {
					Block current = GameLogic.getInstance().getBlock(i, j);
					if(current == null) {
						g.setColor(Color.GRAY);
						g.fillRect(l * i + 3, w * j + 3, l - 6, w - 6);
					} else
					switch(current.getStatus()) {
					case Covered:
						g.setColor(Color.GRAY);
						g.fillRect(l * i + 3, w * j + 3, l - 6, w - 6);
						break;
					case Fliped:
					case Cleared:
						g.drawImage(imageList[current.getPictureID()]
								, l * i + 3, w * j + 3, l - 6, w - 6, null);
						break;
					}
				}
			}
		}
		
		public void setImageList(Image[] imageList) {
			this.imageList = imageList;
		}
		
	}
}