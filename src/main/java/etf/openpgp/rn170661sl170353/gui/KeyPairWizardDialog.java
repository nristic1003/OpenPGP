package etf.openpgp.rn170661sl170353.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPasswordField;
import java.awt.Font;

public class KeyPairWizardDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField emailField;
	private JTextField nameField;
	private JPasswordField passphraseField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			KeyPairWizardDialog dialog = new KeyPairWizardDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public KeyPairWizardDialog() {
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
			JComboBox dsaKeySizeComboBox = new JComboBox();
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
			JComboBox elgamalKeySizeComboBox = new JComboBox();
			elgamalKeySizeComboBox.setFont(new Font("Tahoma", Font.PLAIN, 15));
			elgamalKeySizeComboBox.setModel(new DefaultComboBoxModel(new String[] {"1024", "2048", "4096"}));
			contentPanel.add(elgamalKeySizeComboBox);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton createButton = new JButton("Create");
				createButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
				createButton.setActionCommand("OK");
				buttonPane.add(createButton);
				getRootPane().setDefaultButton(createButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

}
