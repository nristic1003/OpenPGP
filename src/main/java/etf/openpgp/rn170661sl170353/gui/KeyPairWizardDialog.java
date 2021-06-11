package etf.openpgp.rn170661sl170353.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import etf.openpgp.rn170661sl170353.keylogic.KeyManager;

import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPasswordField;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class KeyPairWizardDialog extends JDialog implements ActionListener{

	private final JPanel contentPanel = new JPanel();
	private MainView mainView;
	
	private JTextField emailField;
	private JTextField nameField;
	private JPasswordField passphraseField;
	private JComboBox dsaKeySizeComboBox;
	private JComboBox elgamalKeySizeComboBox;
	
	
	private JButton cancelButton;
	private JButton createButton;

	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		try {
//			KeyPairWizardDialog dialog = new KeyPairWizardDialog();
//			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//			dialog.setVisible(true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	/**
	 * Create the dialog.
	 */
	public KeyPairWizardDialog(MainView mainView) {
		super(mainView.getFrame(), true);
		this.mainView = mainView;
		setFont(new Font("Dialog", Font.PLAIN, 11));
		setTitle("Key Pair Wizard");
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new GridLayout(5, 2, 0, 0));
		{
			JLabel nameLabel = new JLabel("Name");
			nameLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
			contentPanel.add(nameLabel);
		}
		{
			nameField = new JTextField();
			nameField.setFont(new Font("Tahoma", Font.PLAIN, 15));
			contentPanel.add(nameField);
			nameField.setColumns(10);
		}
		{
			JLabel emailLabel = new JLabel("Email");
			emailLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
			contentPanel.add(emailLabel);
		}
		{
			emailField = new JTextField();
			emailField.setFont(new Font("Tahoma", Font.PLAIN, 15));
			contentPanel.add(emailField);
			emailField.setColumns(10);
		}
		{
			JLabel passphraseLabel = new JLabel("Passphrase");
			passphraseLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
			contentPanel.add(passphraseLabel);
		}
		{
			passphraseField = new JPasswordField();
			passphraseField.setFont(new Font("Tahoma", Font.PLAIN, 15));
			contentPanel.add(passphraseField);
		}
		{
			JLabel dsaKeySizeLabel = new JLabel("DSA Key Size");
			dsaKeySizeLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
			contentPanel.add(dsaKeySizeLabel);
		}
		{
			dsaKeySizeComboBox = new JComboBox();
			dsaKeySizeComboBox.setFont(new Font("Tahoma", Font.PLAIN, 15));
			dsaKeySizeComboBox.setModel(new DefaultComboBoxModel(new String[] {"1024", "2048"}));
			contentPanel.add(dsaKeySizeComboBox);
		}
		{
			JLabel elgamalKeySizeLabel = new JLabel("ElGamal Key Size");
			elgamalKeySizeLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
			contentPanel.add(elgamalKeySizeLabel);
		}
		{
			elgamalKeySizeComboBox = new JComboBox();
			elgamalKeySizeComboBox.setFont(new Font("Tahoma", Font.PLAIN, 15));
			elgamalKeySizeComboBox.setModel(new DefaultComboBoxModel(new String[] {"1024", "2048", "4096"}));
			contentPanel.add(elgamalKeySizeComboBox);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				createButton = new JButton("Create");
				createButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
				createButton.setActionCommand("OK");
				createButton.addActionListener(this);
				buttonPane.add(createButton);
				getRootPane().setDefaultButton(createButton);
			}
			{
				cancelButton = new JButton("Cancel");
				cancelButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
				cancelButton.setActionCommand("Cancel");
				cancelButton.addActionListener(this);
				buttonPane.add(cancelButton);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == cancelButton)
		{
			this.dispose();
		}
		else if(e.getSource() == createButton)
		{
			if(nameField.getText().equals("") || emailField.getText().equals("") || passphraseField.getPassword().length==0)
			{
				JOptionPane.showMessageDialog(this, "You must enter all fields!", "Invalid input!", JOptionPane.ERROR_MESSAGE);
			}
			else
			{
				
				KeyManager.getInstance().generateDSAElgamalKeyPair(
						nameField.getText(), 
						emailField.getText(), 
						passphraseField.getPassword(), 
						Integer.parseInt(dsaKeySizeComboBox.getSelectedItem().toString()), 
						Integer.parseInt(elgamalKeySizeComboBox.getSelectedItem().toString()) 
				);
				
				this.mainView.initSecretKeyTable(KeyManager.getInstance().getSecretKeyRingCollection());
				this.mainView.initPublicKeyTable(KeyManager.getInstance().getPublicKeyRingCollection());
				
				JOptionPane.showMessageDialog(
						this, 
						"You have generated new key pair: " + nameField.getText() + " <" + emailField.getText() + ">\n" +
						"DSA Key Size: " + dsaKeySizeComboBox.getSelectedItem() + "\n" +
						"ElGamal Key Size: " + elgamalKeySizeComboBox.getSelectedItem(),
						"Key generated successfully!", 
						JOptionPane.INFORMATION_MESSAGE
				);
				
				this.dispose();

			}	
		}
		
	}

}
