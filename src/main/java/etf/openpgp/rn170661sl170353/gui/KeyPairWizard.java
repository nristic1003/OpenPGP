package etf.openpgp.rn170661sl170353.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;

public class KeyPairWizard {

	private JFrame frame;
	private JTextField textField;
	private JTextField textField_1;
	private JPasswordField passwordField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					KeyPairWizard window = new KeyPairWizard();
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
	public KeyPairWizard() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 803, 518);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Name");
		lblNewLabel.setBounds(153, 98, 46, 14);
		frame.getContentPane().add(lblNewLabel);
		
		textField = new JTextField();
		textField.setBounds(267, 95, 86, 20);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Email");
		lblNewLabel_1.setBounds(153, 123, 46, 14);
		frame.getContentPane().add(lblNewLabel_1);
		
		textField_1 = new JTextField();
		textField_1.setBounds(267, 120, 86, 20);
		frame.getContentPane().add(textField_1);
		textField_1.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("Passphrase");
		lblNewLabel_2.setBounds(153, 154, 55, 14);
		frame.getContentPane().add(lblNewLabel_2);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(267, 151, 86, 20);
		frame.getContentPane().add(passwordField);
		
		JLabel lblNewLabel_3 = new JLabel("DSA Key Size");
		lblNewLabel_3.setBounds(153, 179, 75, 14);
		frame.getContentPane().add(lblNewLabel_3);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"1024", "2048"}));
		comboBox.setBounds(267, 175, 86, 22);
		frame.getContentPane().add(comboBox);
		
		JLabel lblNewLabel_4 = new JLabel("ElGamal Key Size");
		lblNewLabel_4.setBounds(153, 214, 86, 14);
		frame.getContentPane().add(lblNewLabel_4);
		
		JComboBox comboBox_1 = new JComboBox();
		comboBox_1.setModel(new DefaultComboBoxModel(new String[] {"1024", "2048\t", "4096"}));
		comboBox_1.setBounds(267, 208, 86, 22);
		frame.getContentPane().add(comboBox_1);
		
		JButton btnNewButton = new JButton("Create");
		btnNewButton.setBounds(267, 264, 89, 23);
		frame.getContentPane().add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("Cancel");
		btnNewButton_1.setBounds(267, 298, 89, 23);
		frame.getContentPane().add(btnNewButton_1);
	}
}
