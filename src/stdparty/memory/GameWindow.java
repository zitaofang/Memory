package stdparty.memory;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.FormSpecs;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import javax.swing.BoxLayout;
import net.miginfocom.swing.MigLayout;

public class GameWindow implements GameLogic.GraphicsInterface {

	private JFrame frame;
	private JButton btnStart;
	private JLabel lblTime;
	private JPanel panel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GameWindow window = new GameWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GameWindow() {
		GameLogic.initLogic(this, 6, 6);
		initialize();
		((GameCanvas.JCanvas)panel).setImageList(loadImage());
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent arg0) {
				Dimension frameSize = arg0.getComponent().getSize();
				boolean correctAspectRatio = frameSize.height == frameSize.width;

				if (!correctAspectRatio) {
					final int _width = (frameSize.height + frameSize.width) / 2;
					final int _height = _width;

					SwingUtilities.invokeLater(new Runnable(){
						public void run() {
							frame.setSize(_width, _height);
						}
					});
				}
				frame.repaint();
				panel.repaint();
				panel.revalidate();
			}
		});
		frame.setBounds(100, 100, 500, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel head = new JPanel();
		frame.getContentPane().add(head, BorderLayout.NORTH);
		head.setLayout(new BorderLayout(0, 0));
		
		btnStart = new JButton("Start");
		btnStart.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				switch(btnStart.getText()) {
				case "Pause":
					btnStart.setText("Resume");
					GameLogic.getInstance().pause();
					break;
				case "Start":
					btnStart.setText("Pause");
					GameLogic.getInstance().startGame();
					break;
				case "Resume":
					btnStart.setText("Resume");
					GameLogic.getInstance().resume();
					break;
				default:
					throw new IllegalStateException("Cannot determine the status of the game");	
				}
			}
		});
		head.add(btnStart, BorderLayout.EAST);
		
		lblTime = new JLabel("Time:");
		head.add(lblTime, BorderLayout.WEST);
		
		JPanel placeholder = new JPanel();
		frame.getContentPane().add(placeholder, BorderLayout.CENTER);
		placeholder.setLayout(new MigLayout("", "[20,left][grow][20,right]", "[20,top][grow][20,bottom]"));
		
		panel = GameCanvas.createJPanel();
		panel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				int x = arg0.getX() / (panel.getWidth() / GameLogic.getInstance().getColNum());
				int y = arg0.getY() / (panel.getHeight() / GameLogic.getInstance().getRowNum());
				GameLogic.getInstance().clickObject(x, y);
			}
		});
		placeholder.add(panel, "cell 1 1,grow");
	}
	protected JLabel getLblTime() {
		return lblTime;
	}
	protected JButton getBtnPause() {
		return btnStart;
	}
	protected JPanel getPanel() {
		return panel;
	}
	
	// Customized content
	@Override
	public void updateTimer(long time) {
		lblTime.setText("Time: "+ formatInterval(time, "%02d:%02d:%02d.%02d"
				, TimeUnit.HOURS, TimeUnit.MINUTES, TimeUnit.SECONDS, TimeUnit.MILLISECONDS));
		frame.repaint();
		lblTime.revalidate();
		lblTime.repaint();
	}

	@Override
	public void updateBlock() {
		frame.repaint();
		panel.repaint();
		panel.revalidate();
	}
	
	@Override
	public void notifyGameOver() {
		btnStart.setText("Start");
	}
	
	private Image[] loadImage() {
		try {
			ArrayList<Image> imageList = new ArrayList<>();
			File folder = new File(getClass().getResource("/stdparty/resource").toURI());
			for(File imageFile : folder.listFiles())
				imageList.add(ImageIO.read(imageFile));
			return imageList.toArray(new Image[imageList.size()]);
		} catch(IOException | URISyntaxException e) {
			e.printStackTrace();
			System.exit(-1);
			return null;
		}
	}
	
	private static String formatInterval(long time, String formatStr, TimeUnit... vars) {
		Long[] v = new Long[vars.length];
		int i = 0;
		for(TimeUnit u : vars) {
			v[i] = u.convert(time, TimeUnit.MILLISECONDS);
			time -= u.toMillis(v[i++]);
		}
		return String.format(formatStr, (Object[])v);
	}
	
}
