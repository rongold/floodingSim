package simulator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import javax.swing.JTextArea;

public class AboutInfo extends JFrame implements ActionListener {
	/**
	 * @author Ronil Goldenwalla
	 * This class is the about page that user goes to for instructions and what the application is about
	 */
	private static final long serialVersionUID = 1L;

	public AboutInfo(){
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(749, 571);
		setVisible(true);
		getContentPane().setLayout(null);
		setResizable(false);
		setLocationRelativeTo(null);
		setTitle("About & Instructions");

		
		JLabel Title = new JLabel("About and Instructions");
		Title.setHorizontalAlignment(SwingConstants.CENTER);
		Title.setFont(new Font("Tahoma", Font.BOLD, 25));
		
		JButton back = new JButton("Back");
		back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new MainMenu().setVisible(true);
				dispose();
			}
		});
		back.setFont(new Font("Tahoma", Font.PLAIN, 15));
		
		JLabel aboutInfo = new JLabel("<html>This program has 2 functions. The first of which is a map which shows potential areas of flood risk in the North Yorkshire Area. "
										+ "The second function is a simulator which for now is focused on the far most coast at Scarborough "
										+ "and how the floods can affect the area </html>");
		aboutInfo.setFont(new Font("Tahoma", Font.PLAIN, 20));
		
		JLabel instructions = new JLabel("<html>The controls for the Map are the following: <br> &ensp -Mouse Drag to move around in the map "
										+ "<br> &ensp -Mouse scroll to scroll in and out <br> &ensp -Buttons for 1,2 and 3 to show Flood warnings "
										+ "of that nature <br> &ensp -Main menu button to go back to main menu <br> The controls for the Simulator "
										+ "are the following: <br> &ensp -WASD for standard movement <br> &ensp -R for Auto-Flood <br> "
										+ "&ensp -F for reset water <br> &ensp -G for Simulate Water Level Increase "
										+ "<br> &ensp -ESC to return to the main menu </html>");
		instructions.setFont(new Font("Tahoma", Font.PLAIN, 20));
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(230)
					.addComponent(Title, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addGap(517))
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(aboutInfo, GroupLayout.PREFERRED_SIZE, 731, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(294, Short.MAX_VALUE))
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(336)
					.addComponent(back, GroupLayout.PREFERRED_SIZE, 105, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(594, Short.MAX_VALUE))
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(instructions, GroupLayout.PREFERRED_SIZE, 619, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(116, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(Title, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(aboutInfo, GroupLayout.PREFERRED_SIZE, 114, GroupLayout.PREFERRED_SIZE)
					.addGap(15)
					.addComponent(instructions, GroupLayout.PREFERRED_SIZE, 271, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(back, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)
					.addGap(94))
		);
		getContentPane().setLayout(groupLayout);
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
