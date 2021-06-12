package etf.openpgp.rn170661sl170353.gui;

import java.awt.EventQueue;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;

import java.awt.GridLayout;
import java.security.Security;
import java.util.Iterator;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollPane;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;

import etf.openpgp.rn170661sl170353.keylogic.Decrypt;
import etf.openpgp.rn170661sl170353.keylogic.KeyManager;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;



public class MainView{

	private JFrame frame;
	private JTable publicKeyRingTable;
	private JTable secretKeyRingTable;
	
//	private KeyPairWizardDialog keyPairWizardDialog;
	
	private JPopupMenu deletePublicKeyPopupMenu;
	private JPopupMenu deleteSecretKeyPopupMenu;

 

	/**
	 * Launch the application.
	 */
	
	static
	{
		
	}
	
	public JFrame getFrame() {
		return frame;
	}


	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainView window = new MainView();
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
	public MainView() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnNewMenu = new JMenu("New Key Pair Wizard");
		mnNewMenu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				KeyPairWizardDialog keyPairWizardDialog = new KeyPairWizardDialog(MainView.this);
				keyPairWizardDialog.setVisible(true);
			}
		});
		menuBar.add(mnNewMenu);
		
		JMenu mnNewMenu_1 = new JMenu("Import Key ");
		menuBar.add(mnNewMenu_1);
		
		JMenuItem mntmNewMenuItem = new JMenuItem("Import Public Key");
		mntmNewMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				importPublicKey(); 
				
			}
		});
		mnNewMenu_1.add(mntmNewMenuItem);
		
		JMenuItem mntmNewMenuItem_1 = new JMenuItem("Import Secret Key");
		mntmNewMenuItem_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				importSecretKey();
			}
		});
		mnNewMenu_1.add(mntmNewMenuItem_1);
		
		JMenu mnNewMenu_2 = new JMenu("Export Key");
		menuBar.add(mnNewMenu_2);
		
		JMenuItem mntmNewMenuItem_2 = new JMenuItem("Export Public Key");
		mntmNewMenuItem_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exportPublicKey();
			}
		});
		mnNewMenu_2.add(mntmNewMenuItem_2);
		
		JMenuItem mntmNewMenuItem_3 = new JMenuItem("Export Secret Key");
		mntmNewMenuItem_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exportSecretKey();
			}
		});
		mnNewMenu_2.add(mntmNewMenuItem_3);
		
		JMenu mnNewMenu_3 = new JMenu("Sign/Encrypt");
		mnNewMenu_3.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				EncrycptionWizard encWizz = new EncrycptionWizard();
				encWizz.setVisible(true);
				
			}
		});
		menuBar.add(mnNewMenu_3);
		
		JMenu mnNewMenu_4 = new JMenu("Decrypt/Verify");
		mnNewMenu_4.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				JFileChooser fileChooser = new JFileChooser();
				FileNameExtensionFilter gpgFilter = new FileNameExtensionFilter("GPG File", "gpg");
				
				fileChooser.setFileFilter(gpgFilter);
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				if(fileChooser.showOpenDialog(MainView.this.frame)==JFileChooser.APPROVE_OPTION) {
					try {
						Decrypt.getInstance().decryptFile(fileChooser.getSelectedFile());
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				
				
			}
		});
		
		menuBar.add(mnNewMenu_4);
		frame.getContentPane().setLayout(new GridLayout(2, 1, 0, 0));
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel);
		panel.setLayout(new BorderLayout(0, 0));
		
		JLabel lblNewLabel = new JLabel("Public Key Ring");
		panel.add(lblNewLabel, BorderLayout.NORTH);
		
		JScrollPane scrollPane = new JScrollPane();
		panel.add(scrollPane, BorderLayout.CENTER);
		
		publicKeyRingTable = new JTable();
		publicKeyRingTable.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
					"Name", "Email", "KeyID", "Timestamp", "Public Key"
			}
		) {
			@Override
		    public boolean isCellEditable(int row, int column) {
	        return false;
	    }}
		);
		publicKeyRingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(publicKeyRingTable);
		
		JPanel panel_1 = new JPanel();
		frame.getContentPane().add(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		JLabel lblNewLabel_1 = new JLabel("Secret Key Ring");
		panel_1.add(lblNewLabel_1, BorderLayout.NORTH);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		panel_1.add(scrollPane_1, BorderLayout.CENTER);
		
		secretKeyRingTable = new JTable();
		secretKeyRingTable.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
					"Name", "Email", "KeyID", "Timestamp", "Public Key", "Secret Key" 
			}
		)
		{
			@Override
		    public boolean isCellEditable(int row, int column) {
	        return false;
			}	
			
		}
		);
		secretKeyRingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane_1.setViewportView(secretKeyRingTable);
		
		
		/* SystemInit  */
		
		//Bouncy Castle Provider
        Security.addProvider(new BouncyCastleProvider());
       // KeyManager.getInstance().generateDSAElgamalKeyPair("test", "test@gmail.com", "123", 1024, 1024);
        KeyManager.getInstance().initPublicKeyRingCollection();
        KeyManager.getInstance().initSecretKeyRingCollection();
        initPublicKeyTable(KeyManager.getInstance().getPublicKeyRingCollection());
        initSecretKeyTable(KeyManager.getInstance().getSecretKeyRingCollection());
        
		
		/* END_SystemInit */
        
        /* DeletePopupMenuInit */
        
        this.deletePublicKeyPopupMenu = new JPopupMenu();
        this.deleteSecretKeyPopupMenu = new JPopupMenu();
        
        //Delete Public Key Menu
        JMenuItem deletePublicKeyMenuItem = new JMenuItem("Delete Public Key");
        deletePublicKeyMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deletePublicKey();
            }
        });
        this.deletePublicKeyPopupMenu.add(deletePublicKeyMenuItem);
        this.publicKeyRingTable.setComponentPopupMenu(deletePublicKeyPopupMenu);
        
        //Delete Secret Key Menu
        JMenuItem deleteSecretKeyMenuItem = new JMenuItem("Delete Secret Key");
        deleteSecretKeyMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSecretKey();
            }
        });
        this.deleteSecretKeyPopupMenu.add(deleteSecretKeyMenuItem);
        this.secretKeyRingTable.setComponentPopupMenu(deleteSecretKeyPopupMenu);
        

        
        /* END_DeletePopupMenuInit */
		
		
	}
	
	public void initPublicKeyTable(PGPPublicKeyRingCollection pgpPublicKeyRingCollection)
	{
		DefaultTableModel model = (DefaultTableModel) publicKeyRingTable.getModel();
		model.setRowCount(0);
		
		Iterator<PGPPublicKeyRing> pgpPublicKeyRingIterator = pgpPublicKeyRingCollection.getKeyRings();
		while(pgpPublicKeyRingIterator.hasNext())
		{
			PGPPublicKeyRing pgpPublicKeyRing = pgpPublicKeyRingIterator.next();
			Iterator<PGPPublicKey> pgpPublicKeyIterator = pgpPublicKeyRing.getPublicKeys();
			while(pgpPublicKeyIterator.hasNext())
			{
				this.addPGPPublicKeyRow(pgpPublicKeyIterator.next());
			}
			
		}
	}
	
	public void initSecretKeyTable(PGPSecretKeyRingCollection pgpSecretKeyRingCollection)
	{
		DefaultTableModel model = (DefaultTableModel) secretKeyRingTable.getModel();
		model.setRowCount(0);
		
		Iterator<PGPSecretKeyRing> pgpSecretKeyRingIterator = pgpSecretKeyRingCollection.getKeyRings();
		while(pgpSecretKeyRingIterator.hasNext())
		{
			PGPSecretKeyRing pgpSecretKeyRing = pgpSecretKeyRingIterator.next();
			Iterator<PGPSecretKey> pgpSecretKeyIterator = pgpSecretKeyRing.getSecretKeys();
			while(pgpSecretKeyIterator.hasNext())
			{
				this.addPGPSecretKeyRow(pgpSecretKeyIterator.next());
			}
			
		}
	}
	
	public void addPGPPublicKeyRow(PGPPublicKey publicKey)
	{
		try 
		{
			if(!publicKey.getUserIDs().hasNext())
				return;
			String[] userIdentity = publicKey.getUserIDs().next().split(" ");
			Object[] row = 
			{
					userIdentity[0],
					userIdentity[1].subSequence(1, userIdentity[1].length() - 1),
					Long.toHexString(publicKey.getKeyID()).toUpperCase(),
					publicKey.getCreationTime(),
					KeyManager.getInstance().getPublicKeyRingCollection().getPublicKey(publicKey.getKeyID())
						
			};
			DefaultTableModel model = (DefaultTableModel) publicKeyRingTable.getModel();
		    model.addRow(row);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void addPGPSecretKeyRow(PGPSecretKey secretKey)
	{
		try 
		{
			if(!secretKey.getUserIDs().hasNext())
				return;
			String[] userIdentity = secretKey.getUserIDs().next().split(" ");
			Object[] row = 
			{
					userIdentity[0],
					userIdentity[1].subSequence(1, userIdentity[1].length() - 1),
					Long.toHexString(secretKey.getKeyID()).toUpperCase(),
					secretKey.getPublicKey().getCreationTime(),
					KeyManager.getInstance().getPublicKeyRingCollection().getPublicKey(secretKey.getKeyID()),
					KeyManager.getInstance().getSecretKeyRingCollection().getSecretKey(secretKey.getKeyID())
						
			};
			DefaultTableModel model = (DefaultTableModel) secretKeyRingTable.getModel();
		    model.addRow(row);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void importPublicKey()
	{
		JFileChooser publicKeyFileChooser = new JFileChooser();
		FileNameExtensionFilter ascFilter = new FileNameExtensionFilter("ASC File", "asc");
		
		publicKeyFileChooser.setFileFilter(ascFilter);
		publicKeyFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if(publicKeyFileChooser.showOpenDialog(this.frame)==JFileChooser.APPROVE_OPTION)
		{
			File publicKeyFile = publicKeyFileChooser.getSelectedFile();
			try 
			{
				KeyManager.getInstance().importPublicKeyFromFile(publicKeyFile);
				this.initPublicKeyTable(KeyManager.getInstance().getPublicKeyRingCollection());
				
			}
			catch(Exception e)
			{
				JOptionPane.showMessageDialog(this.frame, "Invalid input file!", "Invalid input", JOptionPane.ERROR_MESSAGE);
			}
			
		}
	}
	
	private void importSecretKey()
	{
		JFileChooser secretKeyFileChooser = new JFileChooser();
		FileNameExtensionFilter ascFilter = new FileNameExtensionFilter("ASC File", "asc");
		
		secretKeyFileChooser.setFileFilter(ascFilter);
		secretKeyFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if(secretKeyFileChooser.showOpenDialog(this.frame)==JFileChooser.APPROVE_OPTION)
		{
			File secretKeyFile = secretKeyFileChooser.getSelectedFile();
			try 
			{
				KeyManager.getInstance().importSecretKeyFromFile(secretKeyFile);
				this.initSecretKeyTable(KeyManager.getInstance().getSecretKeyRingCollection());
				
			}
			catch(Exception e)
			{
				JOptionPane.showMessageDialog(this.frame, "Invalid input file!", "Invalid input", JOptionPane.ERROR_MESSAGE);
			}
			
		}	
	}
	
	
	private void exportPublicKey()
	{
		if(this.publicKeyRingTable.getSelectedRowCount() > 1 || this.publicKeyRingTable.getSelectedRowCount()<=0)
		{
			JOptionPane.showMessageDialog(
					this.frame, 
					"Please select row in Public Key Table to export Public Key!",
					"Invalid input",
					JOptionPane.ERROR_MESSAGE
			);
		}
		else
		{
			JFileChooser publicKeyChooser = new JFileChooser();
			publicKeyChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if(publicKeyChooser.showSaveDialog(this.frame) == JFileChooser.APPROVE_OPTION)
			{
				String publicKeyFileName = (String)this.publicKeyRingTable.getValueAt(
						this.publicKeyRingTable.getSelectedRow(),
						2
				);
				
				try(InputStream inputStream = new BufferedInputStream(
						new FileInputStream(
								"./public-keys/" + publicKeyFileName + ".asc"
						)	
					);
					OutputStream outputStream = new BufferedOutputStream(
							new FileOutputStream(
								publicKeyChooser.getSelectedFile().getAbsolutePath() + "/" + publicKeyFileName + ".asc"
							)
					);
				)
				{
					byte[] buffer = new byte[1024];
			        int lengthRead;
			        while ((lengthRead = inputStream.read(buffer)) > 0) {
			        	outputStream.write(buffer, 0, lengthRead);
			        	outputStream.flush();
			        }	
				}
				catch (Exception e) {
					JOptionPane.showMessageDialog(
							this.frame, 
							"Error while exporting Public Key!",
							"Export error",
							JOptionPane.ERROR_MESSAGE
					);
				}
				
				JOptionPane.showMessageDialog(
						this.frame, 
						"You have exported public key successfully!\n" +
						"KeyID: " + publicKeyFileName,
						"Export successfull",
						JOptionPane.INFORMATION_MESSAGE
				);
				
				
//				System.out.println(publicKeyChooser.getSelectedFile().getAbsolutePath() + "/" + publicKeyFileName + ".asc");
				
				
			}

		}
			
	}

	private void exportSecretKey()
	{
		if(this.secretKeyRingTable.getSelectedRowCount() > 1 || this.secretKeyRingTable.getSelectedRowCount()<=0)
		{
			JOptionPane.showMessageDialog(
					this.frame, 
					"Please select row in Secret Key Table to export Secret Key!",
					"Invalid input",
					JOptionPane.ERROR_MESSAGE
			);
		}
		else
		{
			  char[] password;
			  JPanel panel = new JPanel(); JLabel label = new JLabel("Enter a password:");
			  JPasswordField pass = new JPasswordField(10); panel.add(label);
			  panel.add(pass); String[] options = new String[]{"OK", "Cancel"}; 
			  int option = JOptionPane.showOptionDialog(null, panel, "Secret Key Password Input",
			  JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[1]);
			  if(option == 0) // pressing OK button 
			  { 
				  password = pass.getPassword();
				  String secretKeyFileName = (String)this.secretKeyRingTable.getValueAt(
							this.secretKeyRingTable.getSelectedRow(),
							2
				  );
				  PGPSecretKey pgpSecretKey = KeyManager.getInstance().readSecretKeyFromFile(secretKeyFileName);
				  PGPPrivateKey privateKey = null;

					
					
				 try {
					 privateKey = pgpSecretKey.extractPrivateKey(
							 new JcePBESecretKeyDecryptorBuilder().setProvider("BC").build(password)
					 );
					 JFileChooser secretKeyChooser = new JFileChooser();
					 secretKeyChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
						if(secretKeyChooser.showSaveDialog(this.frame) == JFileChooser.APPROVE_OPTION)
						{
							 InputStream inputStream = new BufferedInputStream(
										new FileInputStream(
												"./secret-keys/" + secretKeyFileName + ".asc"
										)	
									);
							 OutputStream outputStream = new BufferedOutputStream(
									new FileOutputStream(
											secretKeyChooser.getSelectedFile().getAbsolutePath() + "/" + secretKeyFileName + ".asc"
									)
							 );
							 
							 byte[] buffer = new byte[1024];
							 int lengthRead;
							 while ((lengthRead = inputStream.read(buffer)) > 0) {
								 outputStream.write(buffer, 0, lengthRead);
								 outputStream.flush();
							 }
							 
							 inputStream.close();
							 outputStream.close();
							 
							 JOptionPane.showMessageDialog(
									 this.frame, 
									 "You have exported secret key successfully!\n" +
									 "KeyID: " + secretKeyFileName,
									 "Export successfull",
									 JOptionPane.INFORMATION_MESSAGE
							);
							 
							 
	
						}

					 
				} catch (Exception e) {
					JOptionPane.showMessageDialog(
							this.frame, 
							"Wrong secret key password, try again!",
							"Wrong password!",
							JOptionPane.ERROR_MESSAGE
					);					
				}
  
			  }		
		}
		
		
		
		
	}

	private void deletePublicKey()
	{
		if(this.publicKeyRingTable.getSelectedRowCount() > 1 || this.publicKeyRingTable.getSelectedRowCount()<=0)
		{
			JOptionPane.showMessageDialog(
					this.frame, 
					"Please select row in Public Key Table to delete Public Key!",
					"Invalid input",
					JOptionPane.ERROR_MESSAGE
			);
			
		}
		else
		{
			String name = (String)this.publicKeyRingTable.getValueAt(
					this.publicKeyRingTable.getSelectedRow(),
					0
			);
			
			String email = (String)this.publicKeyRingTable.getValueAt(
					this.publicKeyRingTable.getSelectedRow(),
					1
			);
			
			String publicKeyFileName = (String)this.publicKeyRingTable.getValueAt(
					this.publicKeyRingTable.getSelectedRow(),
					2
			);
			
			if(JOptionPane.showConfirmDialog(
					this.frame,
					"Are you shure you want delete\n" + 
					name + " <"+ email +"> KeyID: " + publicKeyFileName,
					"Delete Public Key",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE
					
				) == 0
			)
			{
				try {
					KeyManager.getInstance().removePublicKey(publicKeyFileName);
					DefaultTableModel model = (DefaultTableModel)publicKeyRingTable.getModel();
					model.removeRow(this.publicKeyRingTable.getSelectedRow());
					if(Files.deleteIfExists(Paths.get("./public-keys/" + publicKeyFileName + ".asc")))
						JOptionPane.showMessageDialog(
								this.frame, 
								"Public Key successfully deleted!\n" +
								name + " <"+ email +"> KeyID: " + publicKeyFileName,
								"Public Key Deleted",
								JOptionPane.INFORMATION_MESSAGE		
						);
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(
							this.frame, 
							"Error while deleting Public Key!",
							"Export error",
							JOptionPane.ERROR_MESSAGE
					);
				}
				
			}
			
			
		}
		
		
	}
	
	private void deleteSecretKey()
	{
		if(this.secretKeyRingTable.getSelectedRowCount() > 1 || this.secretKeyRingTable.getSelectedRowCount()<=0)
		{
			JOptionPane.showMessageDialog(
					this.frame, 
					"Please select row in Secret Key Table to delete Secret Key!",
					"Invalid input",
					JOptionPane.ERROR_MESSAGE
			);
			
		}
		else
		{
			String name = (String)this.secretKeyRingTable.getValueAt(
					this.secretKeyRingTable.getSelectedRow(),
					0
			);
			
			String email = (String)this.secretKeyRingTable.getValueAt(
					this.secretKeyRingTable.getSelectedRow(),
					1
			);
			
			String secretKeyFileName = (String)this.secretKeyRingTable.getValueAt(
					this.secretKeyRingTable.getSelectedRow(),
					2
			);
			
			
			
			
			 char[] password;
			  JPanel panel = new JPanel(); JLabel label = new JLabel("Enter a password:");
			  JPasswordField pass = new JPasswordField(10); panel.add(label);
			  panel.add(pass); String[] options = new String[]{"OK", "Cancel"}; 
			  int option = JOptionPane.showOptionDialog(null, panel, "Secret Key Password Input",
			  JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[1]);
			  if(option == 0) // pressing OK button 
			  { 
				  password = pass.getPassword();
	
				  PGPSecretKey pgpSecretKey = KeyManager.getInstance().readSecretKeyFromFile(secretKeyFileName);
				  PGPPrivateKey privateKey = null;
				
				 try
				 {
				 	privateKey = pgpSecretKey.extractPrivateKey(
						 new JcePBESecretKeyDecryptorBuilder().setProvider("BC").build(password));
			
			
			 	 	if(JOptionPane.showConfirmDialog(
			 	 			this.frame,
							"Are you shure you want delete\n" + 
							name + " <"+ email +"> KeyID: " + secretKeyFileName,
							"Delete Public Key",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE
							
						) == 0
					)
			 	 	{
						if(Files.deleteIfExists(Paths.get("./secret-keys/" + secretKeyFileName + ".asc")))
						{
							JOptionPane.showMessageDialog(
									this.frame, 
									"Secret Key successfully deleted!\n" +
									name + " <"+ email +"> KeyID: " + secretKeyFileName,
									"Secret Key Deleted",
									JOptionPane.INFORMATION_MESSAGE		
							);
							
							KeyManager.getInstance().removeSecretKey(secretKeyFileName);
							DefaultTableModel model = (DefaultTableModel)secretKeyRingTable.getModel();
							model.removeRow(this.secretKeyRingTable.getSelectedRow());
							
						}
			 	 		
			 	 	}
			 	 	
				} catch (Exception e) {
//					e.printStackTrace();
					JOptionPane.showMessageDialog(
							this.frame, 
							"Error while deleting Public Key!",
							"Export error",
							JOptionPane.ERROR_MESSAGE
					);
				}
				
			}
			
			
		}
	
	}
	


	
	
}
