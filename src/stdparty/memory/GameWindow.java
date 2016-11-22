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
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
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
	private JPanel body;

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
				// Use shorter side to calculate size of canvas
				// Subtract 55 ( = 30 + 25) from height to remove the header height and button height
				Dimension d = arg0.getComponent().getSize();
				double xConst, yConst, lengthOfSquare;
				if(d.getHeight() - 55 > d.getWidth()) {
					xConst = 50;
					lengthOfSquare = d.getWidth() - 100;
					yConst = ((d.getHeight() - 55) - lengthOfSquare) / 2;
				} else {
					yConst = 50;
					lengthOfSquare = (d.getHeight() - 55) - 100;
					xConst = (d.getWidth() - lengthOfSquare) / 2;
				}
				SpringLayout springLayout = (SpringLayout) body.getLayout();
				springLayout.putConstraint(SpringLayout.NORTH, panel, (int)yConst, SpringLayout.NORTH, body);
				springLayout.putConstraint(SpringLayout.SOUTH, panel, (int)-yConst, SpringLayout.SOUTH, body);
				springLayout.putConstraint(SpringLayout.EAST, panel, (int)-xConst, SpringLayout.EAST, body);
				springLayout.putConstraint(SpringLayout.WEST, panel, (int)xConst, SpringLayout.WEST, body);
				Dimension debugSize = panel.getSize();
				
				frame.repaint();
				panel.repaint();
				panel.revalidate();
			}
		});
		frame.setBounds(100, 100, 500, 555);
		frame.setMinimumSize(new Dimension(500, 555));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		BorderLayout frameLayout = new BorderLayout(0, 0);
		frame.getContentPane().setLayout(frameLayout);
		
		body = new JPanel();
		frame.getContentPane().add(body, BorderLayout.CENTER);
		SpringLayout springLayout = new SpringLayout();
		body.setLayout(springLayout);
		
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
		
		panel = GameCanvas.createJPanel();
		springLayout.putConstraint(SpringLayout.NORTH, panel, 50, SpringLayout.NORTH, body);
		springLayout.putConstraint(SpringLayout.SOUTH, panel, -50, SpringLayout.SOUTH, body);
		springLayout.putConstraint(SpringLayout.EAST, panel, -50, SpringLayout.EAST, body);
		springLayout.putConstraint(SpringLayout.WEST, panel, 50, SpringLayout.WEST, body);
		panel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				int x = (int) (arg0.getX() / ((float) panel.getWidth() /(float) GameLogic.getInstance().getColNum()));
				int y = (int) (arg0.getY() / ((float) panel.getHeight() /(float) GameLogic.getInstance().getRowNum()));
				GameLogic.getInstance().clickObject(x, y);
			}
		});
		body.add(panel);
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
	public void updateTimer(Duration time) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault());
		lblTime.setText("Time: " + 
				sdf.format(new Date(time.toMillis() - TimeZone.getDefault().getRawOffset())));
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
}
