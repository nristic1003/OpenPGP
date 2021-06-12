package etf.openpgp.rn170661sl170353.keylogic;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchProviderException;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

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
			
			
			if(o instanceof PGPOnePassSignature)
			{
				verifySignature(fileToBeDecrypted);
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
			
			PublicKeyDataDecryptorFactory b = new JcePublicKeyDataDecryptorFactoryBuilder().setProvider("BC").setContentProvider("BC").build(sKey);
			InputStream clear = pbe.getDataStream(b);
			PGPObjectFactory plainFact = new PGPObjectFactory(clear, new JcaKeyFingerprintCalculator());

			Object message = plainFact.nextObject();
			
			if (message instanceof  PGPCompressedData) {
				PGPCompressedData cData = (PGPCompressedData) message;
				PGPObjectFactory pgpFact = new PGPObjectFactory(cData.getDataStream(), new JcaKeyFingerprintCalculator());

				message = pgpFact.nextObject();
			}
			
			OutputStream out = new FileOutputStream("decryptedMessage.txt");
			PGPOnePassSignature calculatedSignature = null;
			PGPPublicKeyRingCollection pc = KeyManager.getInstance().getPublicKeyRingCollection();
			  
			if (message instanceof  PGPLiteralData) {
				PGPLiteralData ld = (PGPLiteralData) message;
				InputStream unc = ld.getInputStream();
				int ch;
				while ((ch = unc.read()) >= 0) {
					out.write(ch);
				}
			} else if (message instanceof  PGPOnePassSignatureList) {
				calculatedSignature = ((PGPOnePassSignatureList) message).get(0);
//				   PGPPublicKeyRingCollection publicKeyRingCollection = new PGPPublicKeyRingCollection(
//				     PGPUtil.getDecoderStream(signPublicKeyInputStream));
				
				
				
				   PGPPublicKey signPublicKey = pc
				     .getPublicKey(calculatedSignature.getKeyID());
				   
				   calculatedSignature.init(signPublicKey, "BC");
				   message = objectFactory.nextObject();
			} else {
				throw new PGPException("Message is not a simple encrypted file - type unknown.");
			}

			if (pbe.isIntegrityProtected()) {
				if (!pbe.verify()) {
					throw new PGPException("Message failed integrity check");
				}
			}
			
	}
	
	 public void verifySignature( 
		        File  file
		       ) 
		        throws Exception 
		    { 
		        //in = PGPUtil.getDecoderStream(in); 
		 
		 InputStream dataForDecryption = new FileInputStream(file);
			
//			PGPObjectFactory objectFactory = new PGPObjectFactory(PGPUtil.getDecoderStream(dataForDecryption), 
//					  new JcaKeyFingerprintCalculator());
//			
			PGPEncryptedDataList enc;
		
		 
		         
		        PGPObjectFactory    pgpFact =  new PGPObjectFactory(PGPUtil.getDecoderStream(dataForDecryption), 
						  new JcaKeyFingerprintCalculator());
		        PGPSignatureList    p3 = null; 
		 
		        Object    o = pgpFact.nextObject(); 
		        if (o instanceof PGPCompressedData) 
		        { 
		            PGPCompressedData  c1 = (PGPCompressedData)o; 
		 
		            pgpFact = new PGPObjectFactory(c1.getDataStream(),  new JcaKeyFingerprintCalculator()); 
		             
		            p3 = (PGPSignatureList)pgpFact.nextObject(); 
		        } 
		        else 
		        { 
		            p3 = (PGPSignatureList)o; 
		        } 
		             
		      
		 
		 
		        InputStream                 dIn = new BufferedInputStream(new FileInputStream(file)); 
		 
		        PGPSignature                sig = p3.get(0); 
		        PGPPublicKey                key = KeyManager.getInstance().getPublicKeyRingCollection().getPublicKey(sig.getKeyID()); 
		        

		 
		        sig.init(new JcaPGPContentVerifierBuilderProvider().setProvider(new BouncyCastleProvider()), key);
		 
		        int ch; 
		        while ((ch = dIn.read()) >= 0) 
		        { 
		            sig.update((byte)ch); 
		        } 
		 
		        dIn.close(); 
		 
		        if (sig.verify()) 
		        { 
		            System.out.println("signature verified."); 
		        } 
		        else 
		        { 
		            System.out.println("signature verification failed."); 
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
