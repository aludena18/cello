package ProcessorLibrary;

import java.io.FileInputStream;
import java.io.InputStream;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import PDULibrary.AppProperty;

public class FtpUploadFile extends Thread 
{
	
	private AppProperty AP;
	
	private String NombreArchivo, AliasServidorFTP, ServidorFTP, UsuarioFTP,
			ContraseñaFTP;
	private int puerto;

	public void run() 
	{
		FTPClient FTP = null;
		int reply; 

		int IntentosFaltantes=3;
		InputStream input=null;
		
		
		while( IntentosFaltantes > 0 )
		{
			try {
	
				IntentosFaltantes--;
				FTP = new FTPClient();
	
				FTP.connect(ServidorFTP, puerto);
	
				System.err.println("MSG: FtpUploadFile: Run: Conectando a servidor " + ServidorFTP);
	
				reply = FTP.getReplyCode();
	
				if (!FTPReply.isPositiveCompletion(reply)) {
					FTP.disconnect();
					System.err
							.println("MSG: FtpUploadFile: Run: FTP server rechazo conexion.");
					System.exit(1);
					return;
				}
	
				if (!FTP.login(UsuarioFTP, ContraseñaFTP)) {
					FTP.logout();
					return;
	
				}
	
				FTP.enterLocalPassiveMode();
				FTP.setUseEPSVwithIPv4(true);
				FTP.noop();
				input = new FileInputStream(NombreArchivo);
				System.err.println("MSG: FtpUploadFile: Run: Se subira archivo "
						+ NombreArchivo);
				FTP.storeFile(NombreArchivo, input);
				
				FTP.noop();
				System.err.println("MSG: FtpUploadFile: Run: Archivo Subido "
						+ NombreArchivo);
	
				// System.out.println("Remote system is " + FTP.getSystemType());
	
				IntentosFaltantes = 0;
			} catch (Exception e) {
				System.err
						.println("Exception: FtpUploadFile: Run: " + e.toString());
	
			} finally {
				
				try{
					if( input != null)
						input.close();
				}catch( Exception e)
				{
				}
				
				if (FTP != null) 
				{
					try {
						System.err.println("MSG: FtpUploadFile: Run: Saliendo... " );
	
						FTP.logout();
						
					} catch (Exception ee) 
					{
					}
	
					try {
						System.err.println("MSG: FtpUploadFile: Run: Desconectando del servidor... " );
	
						FTP.disconnect();
					} catch (Exception eee) {
					}
				}
				if( IntentosFaltantes > 0 )
				{
					try{
						Thread.sleep(30000);
					}catch( Exception e )
					{
					}
				}
			}
		}	

	}

	public FtpUploadFile(AppProperty tmpAP, String tmpAliasServidorFTP,String tmpNombreArchivo) 
	{
		try {
			
			AP = tmpAP;
			AliasServidorFTP = tmpAliasServidorFTP;
			NombreArchivo = tmpNombreArchivo;

			/*
			 * HADES.INI [HadesTAURUS] AliasServidorFTP=TAURUS
			 * TAURUSServidorFTP=128.241.50.218 TAURUSUsuarioFTP=hunter
			 * TAURUSPuertoFTP=21 TAURUSContrasenaFTP=t@hunt3r
			 */

			if ((ServidorFTP = AP.getProperty(AliasServidorFTP + "ServidorFTP")) == null) {
				return;
			}

			if ((UsuarioFTP = AP.getProperty(AliasServidorFTP + "UsuarioFTP")) == null) {
				return;
			}

			if ((ContraseñaFTP = AP.getProperty(AliasServidorFTP
					+ "ContrasenaFTP")) == null) {
				return;
			}

			if (AP.getProperty(AliasServidorFTP + "PuertoFTP") == null) 
			{
				return;
			}
			else
			{
				puerto = Integer.parseInt(AP.getProperty(AliasServidorFTP
						+ "PuertoFTP"));
			}

			this.start();

		} catch (Exception e) 
		{
			System.err.println("Exception: FtpUploadFile: " + e.toString());
			System.err.println(AliasServidorFTP + " " + ServidorFTP + " " + puerto + " " + UsuarioFTP + " " + ContraseñaFTP);
		}
	}
}
