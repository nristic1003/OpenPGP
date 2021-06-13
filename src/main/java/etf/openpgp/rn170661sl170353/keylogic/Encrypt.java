package etf.openpgp.rn170661sl170353.keylogic;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileSystemView;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.CompressionAlgorithmTags;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.bouncycastle.its.asn1.SymmAlgorithm;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPLiteralDataGenerator;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureGenerator;
import org.bouncycastle.openpgp.operator.bc.BcPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPublicKeyKeyEncryptionMethodGenerator;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;

public class Encrypt {

	private static Encrypt instance;
	
	
	private Encrypt() {
		
	}
	
	
	public static Encrypt getInstance() {
		if(instance==null) instance = new Encrypt();
		return instance;
	}
	
	
	public void ecnryptData(List<String> publicKeys, 
			boolean radix64, boolean isZipped, String encryptAlg, File selectedFile, String privateKeyID, char[] passphrase ) {
		
		PGPSecretKey pgpSecretKey = null;
		PGPPrivateKey privateKey = null;
		if(!privateKeyID.equals("NO SIGNATURE"))
		{
			pgpSecretKey = KeyManager.getInstance().readSecretKeyFromFile(privateKeyID.split("/")[1]);
	
			 try {
				privateKey = pgpSecretKey
				         .extractPrivateKey(new JcePBESecretKeyDecryptorBuilder().setProvider("BC").build(passphrase));
			} catch (PGPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		List<PGPPublicKey> pgpPublicKeys = new ArrayList<>();
		for(String pc : publicKeys)
		{
			pgpPublicKeys.add(KeyManager.getInstance().readPublicKeyFromFile(pc.split("/")[1]));
		}
		
		BouncyCastleProvider bcProvider = new BouncyCastleProvider();
		
		try {
			InputStream fileToEncryptStream = new FileInputStream(selectedFile.getAbsolutePath());
			JFileChooser chooser = new JFileChooser();
			chooser.setCurrentDirectory(new java.io.File("."));
			chooser.setDialogTitle("Save as");
			int userSelection = chooser.showSaveDialog(null);
			chooser.setAcceptAllFileFilterUsed(false);

			String finalFilePath;
			
			if (userSelection == JFileChooser.APPROVE_OPTION) {
			  System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
			  
			  finalFilePath = chooser.getSelectedFile().toString();
			  System.out.println(finalFilePath);
			} else {
			  System.out.println("No Selection ");
			  return;
			}
			OutputStream encryptedFileStream = new BufferedOutputStream(new FileOutputStream(finalFilePath  + ".gpg"));
			
			
			if(radix64)
			{
				encryptedFileStream = new ArmoredOutputStream(encryptedFileStream);
			}
			
			int alg = Integer.parseInt(encryptAlg);
			
			OutputStream compressedOS;
			
			 PGPEncryptedDataGenerator encryptedDataGenerator = null;
			
			if(alg!=SymmetricKeyAlgorithmTags.NULL)
			{
				encryptedDataGenerator = new PGPEncryptedDataGenerator(new BcPGPDataEncryptorBuilder(alg).
						 setWithIntegrityPacket(true).
						 setSecureRandom(new SecureRandom()));
				 
				 for(PGPPublicKey pc : pgpPublicKeys)
				 {
					 encryptedDataGenerator.addMethod(new BcPublicKeyKeyEncryptionMethodGenerator(pc));
				 } 
				 
				 if(isZipped)
				 {
					 compressedOS = new PGPCompressedDataGenerator(PGPCompressedData.ZIP).open(encryptedDataGenerator.open(encryptedFileStream, 
							 new byte[8192]), new byte[8192]);
				 }else {
					 compressedOS = encryptedDataGenerator.open(encryptedFileStream, new byte[8192]);
				 }
				 
				
				 
			}else {
	            if (isZipped) {
	            	compressedOS = new PGPCompressedDataGenerator(PGPCompressedData.ZIP).open(encryptedFileStream, new byte[8192]);

	            } else {
	            	compressedOS = encryptedFileStream;
	            }

	        }
			
			 PGPSignatureGenerator signGen = null;
			
		
			 
			 
			if(pgpSecretKey!=null)
			{
				signGen = new PGPSignatureGenerator(new BcPGPContentSignerBuilder(privateKey.getPublicKeyPacket().getAlgorithm(), HashAlgorithmTags.SHA1));
				signGen.init(PGPSignature.BINARY_DOCUMENT, privateKey);
				signGen.generateOnePassVersion(true).encode(compressedOS);
			}
			
			OutputStream literalOut = new PGPLiteralDataGenerator().open(compressedOS, PGPLiteralData.BINARY,
					selectedFile.getName(), new Date(), new byte[8192]);
					  
					  
					  // read input file and write to target file using a buffer
					  byte[] buf = new byte[8192];
					  int len;
					  while ((len = fileToEncryptStream.read(buf, 0, buf.length)) > 0) {
					   literalOut.write(buf, 0, len);
					   if(pgpSecretKey!=null)
						   signGen.update(buf, 0, len);
					  }
			
					  
					  literalOut.close();
					  fileToEncryptStream.close();
					  
				      if (pgpSecretKey!=null) {
				    	  signGen.generate().encode(compressedOS);
				        }
				      compressedOS.close();
				        if (alg!=SymmetricKeyAlgorithmTags.NULL) {
				        	encryptedDataGenerator.close();
				        }
				        
				        encryptedFileStream.close();
					
				        JOptionPane.showMessageDialog(null, "File is saved!");
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		 
		  
		
	}
	
	
	
	
}
