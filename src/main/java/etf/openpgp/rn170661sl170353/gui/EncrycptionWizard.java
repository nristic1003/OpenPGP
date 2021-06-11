package etf.openpgp.rn170661sl170353.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;

import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;

import etf.openpgp.rn170661sl170353.keylogic.Encrypt;
import etf.openpgp.rn170661sl170353.keylogic.KeyManager;

import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.AbstractListModel;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import java.awt.event.ActionListener;
import java.io.File;
import java.awt.event.ActionEvent;

public class EncrycptionWizard extends JDialog {

	private final JPanel contentPanel = new JPanel();

	private JComboBox signatureComboBox;
	private JList<String> publicKeysList;
	private File fileToEncrypt;
	private JFileChooser fileChooser;
	private JLabel selectedFileName;
	private JCheckBox radix64, zip;
	private ButtonGroup bg;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			EncrycptionWizard dialog = new EncrycptionWizard();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public EncrycptionWizard() {
	    bg = new ButtonGroup();
		this.fileChooser = new JFileChooser();
		setTitle("Encrypt");
		setBounds(100, 100, 625, 502);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JLabel lblNewLabel = new JLabel("Signature;");
			lblNewLabel.setBounds(67, 34, 83, 14);
			contentPanel.add(lblNewLabel);
		}

		signatureComboBox = new JComboBox();
		// petljica

		signatureComboBox.setModel(new DefaultComboBoxModel(new String[] { "NO SIGNATURE" }));
		signatureComboBox.setBounds(176, 30, 400, 22);
		contentPanel.add(signatureComboBox);

		JLabel lblNewLabel_1 = new JLabel("Public Keys");
		lblNewLabel_1.setBounds(67, 115, 83, 14);
		contentPanel.add(lblNewLabel_1);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(176, 80, 283, 103);
		contentPanel.add(scrollPane);
		DefaultListModel<String> model = new DefaultListModel<>();
		publicKeysList = new JList<>(model);

		scrollPane.setViewportView(publicKeysList);

		JRadioButton trippleDES = new JRadioButton("3DES");
		trippleDES.setBounds(176, 258, 109, 23);
		contentPanel.add(trippleDES);
		trippleDES.setActionCommand(String.valueOf(SymmetricKeyAlgorithmTags.TRIPLE_DES));

		JRadioButton idea = new JRadioButton("IDEA");
		idea.setBounds(287, 258, 69, 23);
		contentPanel.add(idea);
		idea.setActionCommand(String.valueOf(SymmetricKeyAlgorithmTags.IDEA));

		JLabel lblNewLabel_2 = new JLabel("Encryption algorithm");
		lblNewLabel_2.setBounds(61, 248, 109, 43);
		contentPanel.add(lblNewLabel_2);

		zip = new JCheckBox("ZIP");
		zip.setBounds(176, 313, 58, 23);
		contentPanel.add(zip);

		radix64 = new JCheckBox("RADIX 64");
		radix64.setBounds(258, 313, 97, 23);
		contentPanel.add(radix64);

		selectedFileName = new JLabel("File to be encrypted");
		selectedFileName.setBounds(286, 352, 156, 14);
		contentPanel.add(selectedFileName);

		JButton btnNewButton = new JButton("Choose file");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fileChooser.showOpenDialog(null);
				if (fileChooser.getSelectedFile() != null) {
					fileToEncrypt = fileChooser.getSelectedFile();
					selectedFileName.setText(fileToEncrypt.getName());
				}

			}
		});
		btnNewButton.setBounds(176, 348, 89, 23);
		contentPanel.add(btnNewButton);

		JRadioButton noEnc = new JRadioButton("No Encryption");
		noEnc.setSelected(true);
		noEnc.setBounds(358, 258, 109, 23);
		noEnc.setActionCommand(String.valueOf(SymmetricKeyAlgorithmTags.NULL));

		contentPanel.add(noEnc);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Encrypt/Sign");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						encrypt();
						
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}

		initSecretKeyTable(KeyManager.getInstance().getSecretKeyRingCollection());
		initPublicKeyTable(KeyManager.getInstance().getPublicKeyRingCollection());
		bg.add(trippleDES);
		bg.add(idea);
		bg.add(noEnc);

	}
	
	
	private void encrypt() {
		//potisivanje
		
		if ((publicKeysList.isSelectionEmpty() && !bg.getSelection().getActionCommand().equals(String.valueOf(SymmetricKeyAlgorithmTags.NULL))) || fileToEncrypt == null) 
		{
			JOptionPane.showMessageDialog(null, "You must select file and public keys ",
					"Invalid Input", JOptionPane.ERROR_MESSAGE);
		}else 
		{
			char[] password = null;
			if(!signatureComboBox.getSelectedItem().toString().equals("NO SIGNATURE"))
			{
				
				  JPanel panel = new JPanel(); JLabel label = new JLabel("Enter a password:");
				  JPasswordField pass = new JPasswordField(10); panel.add(label);
				  panel.add(pass); String[] options = new String[]{"OK", "Cancel"}; int option
				  = JOptionPane.showOptionDialog(null, panel, "The title",
				  JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[1]);
				  if(option == 0) // pressing OK button 
				  { 
					  password = pass.getPassword();
				  	  System.out.println("Your password is: " + new String(password)); 
				  }
				  
				 
				  

			}
			
			  Encrypt.getInstance().ecnryptData(publicKeysList.getSelectedValuesList(),
					  radix64.isSelected(), zip.isSelected(), bg.getSelection().getActionCommand(),
					  fileToEncrypt, signatureComboBox.getSelectedItem().toString(), password);
			
			
			
	
			
		
			
		}
	
		
	}
	

	private void initSecretKeyTable(PGPSecretKeyRingCollection pgpSecretKeyRingCollection) {
		Iterator<PGPSecretKeyRing> pgpSecretKeyRingIterator = pgpSecretKeyRingCollection.getKeyRings();
		while (pgpSecretKeyRingIterator.hasNext()) {
			PGPSecretKeyRing pgpSecretKeyRing = pgpSecretKeyRingIterator.next();
			Iterator<PGPSecretKey> pgpSecretKeyIterator = pgpSecretKeyRing.getSecretKeys();
			while (pgpSecretKeyIterator.hasNext()) {
				PGPSecretKey secretKey = pgpSecretKeyIterator.next();
				if(!secretKey.getUserIDs().hasNext())
					continue;
				this.signatureComboBox.addItem(
						secretKey.getUserIDs().next() + "/" + Long.toHexString(secretKey.getKeyID()).toUpperCase());
			}

		}
	}

	private void initPublicKeyTable(PGPPublicKeyRingCollection pgpPublicKeyRingCollection) {
		DefaultListModel<String> model = (DefaultListModel<String>) publicKeysList.getModel();
		Iterator<PGPPublicKeyRing> pgpPublicKeyRingIterator = pgpPublicKeyRingCollection.getKeyRings();
		while (pgpPublicKeyRingIterator.hasNext()) {
			PGPPublicKeyRing pgpPublicKeyRing = pgpPublicKeyRingIterator.next();
			Iterator<PGPPublicKey> pgpPublicKeyIterator = pgpPublicKeyRing.getPublicKeys();
			while (pgpPublicKeyIterator.hasNext()) {
				PGPPublicKey publicKey = pgpPublicKeyIterator.next();	
				  if(!publicKey.getUserIDs().hasNext()) continue;		 
				model.addElement(
						publicKey.getUserIDs().next() + "/" + Long.toHexString(publicKey.getKeyID()).toUpperCase());
			}

		}
	}
}
