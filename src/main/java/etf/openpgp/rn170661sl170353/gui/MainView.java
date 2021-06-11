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
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JPanel;
import java.awt.GridLayout;
import java.security.Security;
import java.util.Iterator;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollPane;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;

import etf.openpgp.rn170661sl170353.keylogic.KeyManager;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;



public class MainView{

	private JFrame frame;
	private JTable publicKeyRingTable;
	private JTable secretKeyRingTable;
	
//	private KeyPairWizardDialog keyPairWizardDialog;
	
 

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
		mnNewMenu_2.add(mntmNewMenuItem_2);
		
		JMenuItem mntmNewMenuItem_3 = new JMenuItem("Export Secret Key");
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



	
	
}
