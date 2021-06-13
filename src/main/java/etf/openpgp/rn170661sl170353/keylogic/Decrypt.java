package etf.openpgp.rn170661sl170353.keylogic;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchProviderException;
import java.util.Iterator;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import org.bouncycastle.asn1.cms.CompressedData;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPOnePassSignature;
import org.bouncycastle.openpgp.PGPOnePassSignatureList;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyEncryptedData;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureList;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.operator.PublicKeyDataDecryptorFactory;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentVerifierBuilderProvider;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyDataDecryptorFactoryBuilder;

public class Decrypt {

	private static Decrypt instance;
	
	
	private Decrypt() {
		
	}
	
	
	
	public static Decrypt getInstance() {
		if(instance==null)
			instance = new Decrypt();	
		return instance;
	}
	
	
	public void decryptFile(File fileToBeDecrypted) throws Exception
	{
			
			InputStream dataForDecryption = new FileInputStream(fileToBeDecrypted);
			
			PGPObjectFactory objectFactory = new PGPObjectFactory(PGPUtil.getDecoderStream(dataForDecryption), 
					  new JcaKeyFingerprintCalculator());
			
			PGPEncryptedDataList enc;
			Object o = objectFactory.nextObject();
			
			System.out.println("PGP Object :  " + o);
			
			
			if(o instanceof PGPCompressedData || o instanceof PGPSignatureList || o instanceof PGPLiteralData || o instanceof PGPOnePassSignatureList)
			{
				noEncrypt(o, objectFactory);
				return;
				
			}
			
		
			//marker
			if(o instanceof PGPEncryptedDataList)
			{
				enc = (PGPEncryptedDataList) o;
			}else 
			{
				enc = (PGPEncryptedDataList) objectFactory.nextObject();
			}
			
			Iterator<PGPEncryptedData> it = enc.getEncryptedDataObjects();
			PGPPrivateKey sKey = null;
			PGPPublicKeyEncryptedData pbe = null;

			
			char[] password;
			JPanel panel = new JPanel(); JLabel label = new JLabel("Enter a password:");
			JPasswordField pass = new JPasswordField(10); panel.add(label);
			panel.add(pass); String[] options = new String[]{"OK", "Cancel"}; 
			int option = JOptionPane.showOptionDialog(null, panel, "Secret Key Password Input",
			JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[1]);
			if(option == 0) // pressing OK button 
			{ 
				  password = pass.getPassword();
				  while (sKey == null && it.hasNext()) {
						pbe = (PGPPublicKeyEncryptedData)it.next();
						sKey = getPrivateKey(pbe.getKeyID(), password);
					}
					if (sKey == null) {
						throw new IllegalArgumentException("Secret key for message not found.");
					}
			}
			
			PublicKeyDataDecryptorFactory b = new JcePublicKeyDataDecryptorFactoryBuilder().setProvider("BC").build(sKey);
			InputStream clear = pbe.getDataStream(b);
			PGPObjectFactory plainFact = new PGPObjectFactory(clear, new JcaKeyFingerprintCalculator());

			Object message = plainFact.nextObject();
			
			noEncrypt(message, plainFact);
			
				if (pbe.isIntegrityProtected()) {
					if (!pbe.verify()) {
						throw new PGPException("Message failed integrity check");
					}else {
						  JOptionPane.showMessageDialog(null,"Integrity confirmed"  , "Integrity" , JOptionPane.INFORMATION_MESSAGE);
					}
				}else
				{
					  JOptionPane.showMessageDialog(null,"Integrity not confirmed"  , "Integrity" , JOptionPane.INFORMATION_MESSAGE);
				}
			}
		
	
	
	public void noEncrypt(Object o, PGPObjectFactory fac) throws PGPException, IOException
	{
		
		PGPOnePassSignature calculatedSignature = null;
		
		if(o instanceof PGPCompressedData)
		{
			
			fac = new PGPObjectFactory(((PGPCompressedData) o).getDataStream() , new JcaKeyFingerprintCalculator());
			o = fac.nextObject();
			
		}
		
		PGPPublicKey signPublicKey =null;
		if(o instanceof PGPOnePassSignatureList)
		{
			 calculatedSignature = ((PGPOnePassSignatureList) o).get(0);
			 System.out.println("calculated sig: " + calculatedSignature);
			 PGPPublicKeyRingCollection pkc = KeyManager.getInstance().getPublicKeyRingCollection();
			  signPublicKey = pkc.getPublicKey(calculatedSignature.getKeyID());
			 calculatedSignature.init(new JcaPGPContentVerifierBuilderProvider().setProvider("BC"), signPublicKey);
			 o = fac.nextObject();
			 System.out.println("PGP Object :  " + o);
		}
		
		JFileChooser publicKeyChooser = new JFileChooser();
		publicKeyChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		String desPath=".";
		if(publicKeyChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
		{
			 desPath =publicKeyChooser.getSelectedFile().getAbsolutePath();
		}
		
		OutputStream out = new FileOutputStream(desPath + "/" + "decryptedMessage.txt");
		if (o instanceof  PGPLiteralData) {
			System.out.println("Literal");
			PGPLiteralData ld = (PGPLiteralData) o;
			InputStream unc = ld.getInputStream();
			int ch;
			while ((ch = unc.read()) >= 0) {
				if(calculatedSignature!=null)
					calculatedSignature.update((byte)ch);
				out.write(ch);
				
			}
		}else {
			System.out.println("Nemam nista");
		}
		
		 if (calculatedSignature != null) {
			 PGPSignatureList signatureList = (PGPSignatureList) fac.nextObject();
			 System.out.println("PGP Object :  " + o);
			   System.out.println("signature list (" + signatureList.size() + " sigs) is " + signatureList);
			   PGPSignature messageSignature = (PGPSignature) signatureList.get(0);
			   System.out.println("verification signature is " + messageSignature);
			 
			   if (!calculatedSignature.verify(messageSignature)) {
				   JOptionPane.showMessageDialog(null,"Verification not confirmed"  , "Verification" , JOptionPane.INFORMATION_MESSAGE);
				   }else
				   {
					   
					  Iterator<String> users = signPublicKey.getUserIDs();
					  while(users.hasNext())
					  {
						  JOptionPane.showMessageDialog(null,"Message is signed by : " + users.next() , "Verification" , JOptionPane.INFORMATION_MESSAGE);
					  }
					   
					   
					   System.out.println("Verification");
				   }
			 	 
		 }
		
		
		
	}
	
	
	public PGPPrivateKey getPrivateKey(long keyID , char[] passphrase) throws PGPException , NoSuchProviderException
	{
		 PGPSecretKeyRingCollection sck = KeyManager.getInstance().getSecretKeyRingCollection();
		 PGPSecretKey pgpSecretKey = sck.getSecretKey(keyID);
		 PGPPrivateKey privateKey = null;
		
		
		 privateKey = pgpSecretKey.extractPrivateKey(
				 new JcePBESecretKeyDecryptorBuilder().setProvider("BC").build(passphrase));
		 return privateKey;
	}
	
	
}
