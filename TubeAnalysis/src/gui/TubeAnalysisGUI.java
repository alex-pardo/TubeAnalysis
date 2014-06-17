package gui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import main.TubeAnalysis;

public class TubeAnalysisGUI {

	private JFrame frame;
	private static JSlider speedSlider;
	private static JSlider edgeSlider;
	
	private static JLabel travelTime;
	private static JLabel waitingTime;
	private static JLabel numTravels;
	
	private static JLabel edge_value;
	private static JLabel speed_value;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	
	private static TubeAnalysis ta;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ta = new TubeAnalysis();
					TubeAnalysisGUI window = new TubeAnalysisGUI();
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
	public TubeAnalysisGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	public static void reset(){
		edgeSlider.setValue(959);
		speedSlider.setValue(833);
	}
	
	public static void setResults(String travel, String wait, String num){
		travelTime.setText(travel);
		waitingTime.setText(wait);
		numTravels.setText(num);
	}
	
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 218, 502);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		
		JButton btnRun = new JButton("RUN");
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ta.setValues(TubeAnalysisGUI.edgeSlider.getValue(), TubeAnalysisGUI.speedSlider.getValue());
				String selected = null;
				for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
		            AbstractButton button = buttons.nextElement();

		            if (button.isSelected()) {
		                selected = button.getToolTipText();
		            }
		        }
				ta.setMode(selected);
				ta.run();
			}
		});
		
		JPanel panel_1 = new JPanel();
		
		JButton btnReset = new JButton("RESET");
		
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TubeAnalysisGUI.reset();
			}
		});
		
		
		JRadioButton rdbtnNewRadioButton = new JRadioButton("Real graph");
		rdbtnNewRadioButton.setSelected(true);
		rdbtnNewRadioButton.setToolTipText("0");
		buttonGroup.add(rdbtnNewRadioButton);
		
		JRadioButton rdbtnNewRadioButton_1 = new JRadioButton("Remove 1 node betw.");
		rdbtnNewRadioButton_1.setToolTipText("1");
		buttonGroup.add(rdbtnNewRadioButton_1);
		
		JRadioButton rdbtnNewRadioButton_2 = new JRadioButton("Remove 2 node betw.");
		rdbtnNewRadioButton_2.setToolTipText("2");
		buttonGroup.add(rdbtnNewRadioButton_2);
		
		JRadioButton rdbtnNewRadioButton_3 = new JRadioButton("Remove 1 node neigh.");
		rdbtnNewRadioButton_3.setToolTipText("3");
		buttonGroup.add(rdbtnNewRadioButton_3);
		
		JRadioButton rdbtnNewRadioButton_4 = new JRadioButton("Remove large in traffic");
		rdbtnNewRadioButton_4.setToolTipText("4");
		buttonGroup.add(rdbtnNewRadioButton_4);
		
		JRadioButton rdbtnRemoveLargeOut = new JRadioButton("Remove large out traffic");
		rdbtnRemoveLargeOut.setToolTipText("5");
		buttonGroup.add(rdbtnRemoveLargeOut);
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(14)
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 187, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(17, Short.MAX_VALUE))
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 203, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(9, Short.MAX_VALUE))
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(btnRun))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(66)
							.addComponent(btnReset, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
					.addGap(71))
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(rdbtnNewRadioButton_1)
					.addContainerGap(46, Short.MAX_VALUE))
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(rdbtnNewRadioButton_2)
					.addContainerGap(46, Short.MAX_VALUE))
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(rdbtnNewRadioButton_3)
					.addContainerGap(41, Short.MAX_VALUE))
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(rdbtnNewRadioButton_4)
					.addContainerGap(26, Short.MAX_VALUE))
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(rdbtnNewRadioButton)
					.addContainerGap(114, Short.MAX_VALUE))
				.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(rdbtnRemoveLargeOut)
					.addContainerGap(71, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(20)
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 121, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnReset)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(rdbtnNewRadioButton)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(rdbtnNewRadioButton_1)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(rdbtnNewRadioButton_2)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(rdbtnNewRadioButton_3)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(rdbtnNewRadioButton_4)
					.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(rdbtnRemoveLargeOut)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnRun)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		
		JLabel lblMeanTravelTime = new JLabel("Mean travel time:");
		
		travelTime = new JLabel("");
		
		JLabel lblMeanWaitingTime = new JLabel("Mean waiting time:");
		
		waitingTime = new JLabel("");
		
		JLabel lblNumberOfTravels = new JLabel("Number of travels:");
		
		numTravels = new JLabel("");
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(lblMeanTravelTime)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(travelTime))
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(lblMeanWaitingTime)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(waitingTime))
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(lblNumberOfTravels)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(numTravels)))
					.addContainerGap(11, Short.MAX_VALUE))
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblMeanTravelTime)
						.addComponent(travelTime))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblMeanWaitingTime)
						.addComponent(waitingTime))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNumberOfTravels)
						.addComponent(numTravels))
					.addContainerGap(101, Short.MAX_VALUE))
		);
		panel_1.setLayout(gl_panel_1);
		
		JLabel lblNewLabel = new JLabel("People/Edge: \n\n");
		panel.add(lblNewLabel);
		
		edge_value = new JLabel("");
		panel.add(edge_value);
		
		edgeSlider = new JSlider();
		edgeSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				edge_value.setText(Integer.toString(edgeSlider.getValue()));
			}
		});
		edgeSlider.setMinimum(500);
		edgeSlider.setMaximum(3000);
		edgeSlider.setValue(959);
		panel.add(edgeSlider);
		
		JLabel lblNewLabel_1 = new JLabel("m/turn: ");
		panel.add(lblNewLabel_1);
		
		speed_value = new JLabel("");
		panel.add(speed_value);
		
		speedSlider = new JSlider();
		speedSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				speed_value.setText(Integer.toString(speedSlider.getValue()));
			}
		});
		
		speedSlider.setMinimum(500);
		speedSlider.setMaximum(1500);
		speedSlider.setValue(833);
		panel.add(speedSlider);
		frame.getContentPane().setLayout(groupLayout);
	}
}
