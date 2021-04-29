package simulator;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.swing.*;

import org.openstreetmap.gui.jmapviewer.Map;

import com.opencsv.exceptions.CsvValidationException;

import java.awt.Font;

public class MainMenu extends JFrame implements ActionListener {

	/**
	 * @author Ronil Goldenwalla
	 * This class is for the main menu that the user first opens
	 */
	private static final long serialVersionUID = 9078084560792318731L;

	public MainMenu(){
		updateData();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(436, 282);
		setVisible(true);
		getContentPane().setLayout(null);
		setResizable(false);
		setLocationRelativeTo(null);
		setTitle("Main Menu");

		
		JButton Simulator = new JButton("Simulator");
		Simulator.setFont(new Font("Tahoma", Font.PLAIN, 15));
		Simulator.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new Enviroment().runSimulator();
				dispose();
			}
		});
		Simulator.setBounds(152, 120, 122, 78);
		getContentPane().add(Simulator);
		
		JButton Map = new JButton("Map");
		Map.setFont(new Font("Tahoma", Font.PLAIN, 15));
		Map.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					new Map().setVisible(true);
					dispose();
				} catch (CsvValidationException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		Map.setBounds(20, 120, 122, 78);
		getContentPane().add(Map);
		
		JButton About = new JButton("About");
		About.setFont(new Font("Tahoma", Font.PLAIN, 15));
		About.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new AboutInfo().setVisible(true);
				dispose();
			}
		});
		About.setBounds(284, 120, 122, 78);
		getContentPane().add(About);
		
		JLabel Title = new JLabel("Flooding Simulator and Visualiser");
		Title.setHorizontalAlignment(SwingConstants.CENTER);
		Title.setFont(new Font("Tahoma", Font.BOLD, 20));
		Title.setBounds(20, 10, 395, 55);
		getContentPane().add(Title);

        
 }

	public static void main(String[] args) {
		new MainMenu();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	// Function that updates the dynamic flood data
	private void updateData() {
		try (BufferedInputStream inputStream = new BufferedInputStream(new URL(
				"https://environment.data.gov.uk/flood-monitoring/id/floods.csv?county=North%20Yorkshire&min-severity=3")
						.openStream());
				FileOutputStream fileOS = new FileOutputStream(
						"assets/floods.csv")) {
			byte data[] = new byte[1024];
			int byteContent;
			while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
				fileOS.write(data, 0, byteContent);
			}
		} catch (IOException e) {
			// handles IO exceptions
		}
		
		try (BufferedInputStream inputStream = new BufferedInputStream(new URL(
				"https://environment.data.gov.uk/flood-monitoring/id/stations/L2600/readings.csv?_sorted&_limit=1")
						.openStream());
				FileOutputStream fileOS = new FileOutputStream(
						"assets/waterLevel.csv")) {
			byte data[] = new byte[1024];
			int byteContent;
			while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
				fileOS.write(data, 0, byteContent);
			}
		} catch (IOException e) {
			// handles IO exceptions
		}
	}
}