//V3.0.0
package HadesTerminal;

import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import PDULibrary.AppProperty;

public class ProcessorTerminalCMD extends Thread 
{
	AppProperty AP;
	
	DatagramSocket UDPServidor;
	
    String DBParameters;
	
    Connection ConexionBase = null, ConexionBase2 = null;
	Statement stm,stm2;
	ResultSet rs;
	
	boolean DebugERROR,DebugTRAN;
    

	private byte[] StringToByte( String Cadena)
	{	
		try{
			char charString[];
			byte byteString[];
			int i;
			
			charString = Cadena.toCharArray();
			byteString = new byte[ charString.length ];
			
			for( i=0; i < charString.length ; i++ )
				byteString[i] = (byte)charString[i];
			
			return byteString;
		}catch( Exception e)
		{
			System.err.println( "ERROR: ProcessorTerminalCMD: StringToByte: " + e.toString() );
			return null;
		}
	}
		
	private void AbrirConexionDB()
	{
		try{
				
			ConexionBase = DriverManager.getConnection( DBParameters );
			
			
		}catch( Exception ee )
		{
			System.err.println( "ERROR: ProcessorTerminalCMD: AbrirConexionDB:" + ee.toString() );
			CerrarConexionDB();
		}
		
		
	}
		
	private void CerrarConexionDB()
	{
		try{
			rs.close();
			rs=null;
		}catch( Exception e)
		{
			rs=null;
		}
		
		try{
			stm.close();
			stm=null;
		}catch( Exception e)
		{
			stm=null;
		}
		
		try{
			stm2.close();
			stm2=null;
		}catch( Exception e)
		{
			stm2=null;
		}
			
		try{
			ConexionBase.close();
			ConexionBase = null;
		}catch( Exception ee )
		{
			ConexionBase = null;
		}
		
		try{
			ConexionBase2.close();
			ConexionBase2=null;
		}catch( Exception e)
		{
			ConexionBase2=null;
		}
	}
	
	public void run()
	{
		String VID, IdComando, Comando, IP, Puerto;
		byte[] btPDUToSend;
		DatagramPacket packet2;
		
		while( true )
		{
			try{
				
				AbrirConexionDB();
				
				if( ConexionBase != null )
				{
					stm = ConexionBase.createStatement();
					
					rs = stm.executeQuery( "SELECT LC.FECHAHORA, LC.VID, LC.IDCOMANDO, LC.IDCLIENTE, LC.USUARIO, LC.ENVIADO, LC.CONFIRMADO, C.CMD, T.IP, T.PUERTO " +
							   "FROM LOGCOMANDO AS LC, COMANDO AS C, TERMINAL AS T " +
							   "WHERE LC.VID = T.VID AND " + 
							   "LC.IDCOMANDO = C.IDCOMANDO AND " +
							   "C.TIPOTERMINAL = T.TIPOTERMINAL AND " +
							   "LC.ENVIADO IS NULL " + 
							   "ORDER BY FECHAHORA DESC" );
					

					while( rs.next() == true )
					{
						if( ConexionBase2 == null )
						{
							try{
								ConexionBase2 = DriverManager.getConnection( DBParameters );
								stm2 = ConexionBase2.createStatement();
							}catch( Exception e2)
							{
								ConexionBase2 = null;
							}
						}
						VID =  rs.getString(2);
						IdComando = rs.getString(3);
						Comando = rs.getString(8);
						IP = rs.getString(9);
						Puerto = rs.getString(10);			
						
						if( ConexionBase2 != null )
						{	
							btPDUToSend = StringToByte( Comando /* + (char)0x0A + (char)0x0D */);
							packet2 = new DatagramPacket( btPDUToSend , btPDUToSend.length , InetAddress.getByName(  IP ) , Integer.parseInt( Puerto ) );
							
							try{
								stm2.executeUpdate( "UPDATE LOGCOMANDO set Enviado = getdate() where VID = " + VID + " and IdComando = " + IdComando + " and Enviado is null" );
								UDPServidor.send( packet2 );
							}catch( Exception e2)
							{
								try{
									ConexionBase2.close();
									ConexionBase2 = null;
								}catch(Exception e3)
								{
									ConexionBase2 = null;
								}
							}
						}
					}
					CerrarConexionDB();
					try{
						ConexionBase2.close();
						ConexionBase2 = null;
					}catch(Exception e3)
					{
						ConexionBase2 = null;
					}
				}
			}catch( Exception e1)
			{
				System.err.println( "ERROR: ProcessorTerminalCMD: Run: " + e1.toString() );
				CerrarConexionDB();
				try{
					ConexionBase2.close();
					ConexionBase2 = null;
				}catch(Exception e3)
				{
					ConexionBase2 = null;
				}
			}
			try {
				Thread.sleep( 10000 );
			}catch( Exception e2)
			{
			}
		}
	}
	
	public ProcessorTerminalCMD( DatagramSocket tmpUDPServidor, AppProperty tmpAP )
	{
		try{
			this.setName( this.getName() + ": ProcessorTerminalCMD");
			
			AP=tmpAP;
			UDPServidor = tmpUDPServidor; 
						
		  	DBParameters = AP.getProperty("JdbcUrl") + "://"+ AP.getProperty("DBTerminalAddress") + ":"
						  + AP.getProperty("DBTerminalPort") + ";Database=" + AP.getProperty("DBTerminalName") 
						  + ";User=" + AP.getProperty("DBTerminalUser") + ";Password=" + AP.getProperty("DBTerminalPWD") ;
		    this.start();
		}catch( Exception e)
		{
			System.err.println("ERROR: ProcessorTerminalPDUToDB: " + e.toString() );
		}
	}
	
}
