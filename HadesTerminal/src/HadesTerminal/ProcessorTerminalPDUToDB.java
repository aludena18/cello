//V3.0.0
package HadesTerminal;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import PDULibrary.PDU;
import PDULibrary.AppProperty;

public class ProcessorTerminalPDUToDB extends Thread 
{
	
	AppProperty AP;
	DatagramSocket UDPServidor;
	String CADENA,  ipOrigen, strPDUToSend;
	int puertoOrigen;
	DatagramPacket packet,packet2;
	byte[] pdu,btPDUToSend;
	InetAddress iaIPDestino;
	
	PDU PDU=null;
	String DBParameters;
	
	Connection ConexionBase = null;
	Statement stmINS,stmSEL;
	
	ResultSet rs;
	
	boolean DebugERROR,DebugTRAN;

	private byte[] StringToByte( String Cadena)
	{	
		char charString[];
		byte byteString[];
		int i;
		
		charString = Cadena.toCharArray();
		byteString = new byte[ charString.length ];
		
		for( i=0; i < charString.length ; i++ )
			byteString[i] = (byte)charString[i];
		
		return byteString;
	}
	
	void abrirDB()
	{
		try{
			ConexionBase = DriverManager.getConnection( DBParameters );
			
		}catch( Exception e)
		{
			ConexionBase=null;
			System.err.println("ERROR: ProccesorTerminalPDUToDB: abrirDB: " + e.toString() );
			
		}
		
	}
	
	void cerrarDB()
	{
		try{
			if( rs!=null)
				rs.close();
			rs=null;
		}catch( Exception e)
		{
			rs=null;
		}
		
		try{
			if( stmSEL != null )
				stmSEL.close();
			stmSEL=null;
		}catch( Exception e)
		{
			stmSEL=null;
		}
		
		try{
			if( stmINS != null )
				stmINS.close();
			stmINS=null;
		}catch( Exception e)
		{
			stmINS=null;
		}
		
		try{
			if( ConexionBase != null )
				ConexionBase.close();
			ConexionBase=null;
		}catch( Exception e)
		{
			ConexionBase=null;
		}
		
	}
	
	public void run()
	{
		int TipoTerminal=0;
		String[] Palabra=null;String Query="";int i;float valor;
		
		if( CADENA != null )
			Palabra = CADENA.split(":");
	
		try{
			//ENVIAR ACK
			if( PDU == null && ( Palabra!= null && Palabra[0].charAt(0) == 't' ) )
			{	strPDUToSend = "r" + Palabra[0].substring( 1 );// + (char)0x0A + (char)0x0D;
				btPDUToSend = StringToByte( strPDUToSend );
				packet2 = new DatagramPacket( btPDUToSend , btPDUToSend.length , iaIPDestino ,  puertoOrigen );
				UDPServidor.send( packet2 );
			}
		}catch( Exception e)
		{
		}
		try{
			if( PDU != null)
			{
				Query="INSERT INTO ACTIVIDAD VALUES('" + PDU.getModemID() + "',DATEADD(hour, -5, '" + PDU.getReportDateTime() + "'),1";
				
				for( int ii=0; ii < 12; ii++)
				{
					if( PDU.getIO_STATE() != null && ii < PDU.getIO_STATE().length() )
					{
						if( ii==0 || ii == 1)
						{
							if( PDU.getIO_STATE().charAt(ii) == '1' )
							{
								Query+=",0";
							}
							else
								Query+=",1";
								
						}
						else
							Query+="," + PDU.getIO_STATE().charAt(ii);
					}
					else
					{
							Query+=",null";
					}
				}
				if( PDU.getADC1() >= 0 )
					Query+=","+PDU.getADC1()+",null)";
				else	
					Query+=",null,null)";
			}
			else if( Palabra.length >= 2 )
			{	
				if( Palabra[0].length() >= 2 )
				{
					if( Palabra[Palabra.length - 1].length() == 1  && Palabra[Palabra.length - 1].charAt(0)==0x0D ) 
						Palabra[Palabra.length - 1] = "0";
					
					i=0;
			
					
					//PARA ESTABLECER EL ID
					if( Palabra[2].charAt(0) != 'v' && Palabra[2].length() != 3 && Palabra[1].length() != 4  )
					{						
						Query="INSERT INTO ACTIVIDAD VALUES('" + Palabra[2] + "',getdate()";
					}
					else
					{	
						abrirDB();
						
						if( ConexionBase != null )
						{
							stmSEL = ConexionBase.createStatement();
						
							rs = stmSEL.executeQuery("SELECT VID,TIPOTERMINAL FROM TERMINAL WHERE IP = '" + packet.getAddress().toString().substring(1) + "'" );
						}
						
						if( rs!=null && rs.next() )
						{
							System.err.println("SE OBTUVO ID DESDE LA BASE");
							Query="INSERT INTO ACTIVIDAD VALUES('" + rs.getString(1) + "',getdate()";
							TipoTerminal = rs.getInt(2);
						}
						else
						{
							System.err.println("NO SE OBTUVO ID DESDE LA BASE");
							Query="INSERT INTO ACTIVIDAD VALUES('" + packet.getAddress().toString() + "',getdate()";
						}
						cerrarDB();
					}
					
					//PARA LLENAR LOS INDICADORES y VOLTAJE
					if( ( Palabra[1].charAt(0) == '1' || Palabra[1].charAt(0) == '0' ) )
					{
					    Query+=",1";
						while( i < 12 )
						{
							if( i < Palabra[1].length() )
							{
								if( Palabra[1].charAt(i)=='1')
								{
									Query += ",1";
								}
								else if( Palabra[1].charAt(i)=='0')
								{
									Query += ",0";
								}
								else 
								{
									Query += ",NULL";
								}
							}
							else
								Query += ",NULL";
						
							i++;
						}
						if( Palabra.length >= 4  && Palabra[2].charAt(0) == 'v'  )
						{
							
							//VOLTAJE
							valor = (float)this.BinToInt( Palabra[2].substring( 1));
							
							if( TipoTerminal == 12 || TipoTerminal == 13)
							{	
								valor = valor * 15;
								valor = valor /255;
							}
							else
							{
								valor = valor * 15;
								valor = valor /1023;
							}
							Query += ",'" + valor + "',";
							
							//TEMPERATURA
							valor = (float)this.BinToInt( Palabra[3].substring( 1));
							
							if( TipoTerminal == 12 || TipoTerminal == 13 )
							{
								valor = valor * 500;
								valor = valor /255;
							}
							else
							{
								valor = valor * 500;
								valor = valor /1023;
							}
							Query += "'" + valor + "')";
						}
						else if( ( Palabra.length == 4 || Palabra.length == 3 )&& Palabra[2].length() < 5)
						{
							
							//VOLTAJE
							valor = (float)( Float.parseFloat(Palabra[2]) /10 );
							
							Query += ",'" + valor + "',NULL)";
							
						}
						else
						{
							Query += ",0,0)";
							
						}
						
					}
					//PARA DATOS SIN INDICADORES CON VOLTAJE Y TEMPERATURA
					else if( Palabra[1].charAt(0) == 't' || Palabra[1].charAt(0) == 'v' )
					{
						Query += ",2,null,null,null,null,null,null,null,null,null,null,null,null,";
						if( Palabra[1].charAt(0)=='t' )
						{	        
							valor = (float)this.BinToInt( Palabra[1].substring( 1));
							valor = valor * 500;
							valor = valor /1023;
							Query += "0,'" + valor + "')";
						}
						else if( Palabra[1].charAt(0)=='v' )
						{
							valor = (float)this.BinToInt( Palabra[1].substring( 1));
							valor = valor * 15;
							valor = valor /1023;
							Query += "'" + valor + "',0)";
						}
					}
				}
				else
					return;
			}
			else
				return;
				
				
			if( Query.length() > 1)
			{
				int c=0;
			
			
				while( c < 5)
				{	
					abrirDB();
					
					if( ConexionBase != null)
					{
						try{
						
							stmINS = ConexionBase.createStatement();
							stmINS.execute(Query);
							
							System.err.println(Query);
							cerrarDB();
							return;
						}catch( Exception eee )
						{
							cerrarDB();
							System.err.println("ERROR: ProcessorTerminalPDUToDB: Run: Insert: " + Query);
							System.err.println("ERROR: ProcessorTerminalPDUToDB: Run: Insert: " + eee.toString());
						}
					}
					
					c++;
					
					try{
						Thread.sleep(1000);
					}catch( Exception e )
					{
						
					}
				}
			}
		}catch( Exception e)
		{
			System.err.println("ERROR: ProcessorTerminalPDUToDB: Run: " + e.toString());
			return;
		}
					
	}
		
	private byte[] subbyte( byte[] bytes , int i , int j )
	{
		
		
		try{
			
			if( bytes == null  )
				return new byte[1];
			
			if( i > j )
				i=0;
			
			if( j >= bytes.length )
				j = bytes.length - 1;
			
			int ii=0;
			byte[] subbytes = new byte[ (j-i)+1 ];
			
			for( ii = 0; ii < bytes.length && ii <= j; ii++ )
			{
				subbytes[ii] = bytes[ii];
			}
				
			return subbytes;
			
		}catch( Exception e )
		{
			return null;
		}
		
	}
	
	public int BinToInt( String tmpBin )
	{
		int valor=0,i=0;
		
		try{
			for( i=0; i < tmpBin.length(); i ++ )
			{
				if( tmpBin.charAt(i) == '1' )
				{
					valor+=Math.pow( 2 ,  tmpBin.length()- ( i + 1 ));
				}
				
			}
		}catch( Exception e )
		{
			valor=0;
		}
		return valor;
	}

	public ProcessorTerminalPDUToDB( DatagramPacket tmppacket , DatagramSocket tmpUDPServidor, AppProperty tmpAP )
	{
		try{
			
			this.setName( this.getName() + ": ProcessorTerminalPDUToDB");
			AP = tmpAP;
			packet = tmppacket;
			pdu = new byte [ tmppacket.getLength()];
	        pdu = this.subbyte( packet.getData() , 0 , packet.getLength()-1);
	        CADENA = new String(pdu);
	        iaIPDestino = tmppacket.getAddress();
	        puertoOrigen = tmppacket.getPort();	
	        UDPServidor = tmpUDPServidor;
	        
	        DBParameters = AP.getProperty("JdbcUrl") + "://"+ AP.getProperty("DBTerminalAddress") + ":"
						  + AP.getProperty("DBTerminalPort") + ";Database=" + AP.getProperty("DBTerminalName") 
						  + ";User=" + AP.getProperty("DBTerminalUser") + ";Password=" + AP.getProperty("DBTerminalPWD") ;
		    this.start();
		}catch( Exception e)
		{
			System.err.println("ERROR: ProcessorTerminalPDUToDB: " + e.toString() );
			DBParameters="";
		}
	}

	public ProcessorTerminalPDUToDB( PDU tmpPDU, AppProperty tmpAP )
	{
		try{
			
			this.setName( this.getName() + ": ProcessorTerminalPDUToDB");
			AP = tmpAP;
			PDU = tmpPDU;
			
			/*
			packet = tmppacket;
			pdu = new byte [ tmppacket.getLength()];
	        pdu = this.subbyte( packet.getData() , 0 , packet.getLength()-1);
	        CADENA = new String(pdu);
	        iaIPDestino = tmppacket.getAddress();
	        puertoOrigen = tmppacket.getPort();	
	        UDPServidor = tmpUDPServidor;
	        
	        */
	        DBParameters = AP.getProperty("JdbcUrl") + "://"+ AP.getProperty("DBTerminalAddress") + ":"
						  + AP.getProperty("DBTerminalPort") + ";Database=" + AP.getProperty("DBTerminalName") 
						  + ";User=" + AP.getProperty("DBTerminalUser") + ";Password=" + AP.getProperty("DBTerminalPWD") ;
		    this.start();
		}catch( Exception e)
		{
			System.err.println("ERROR: ProcessorTerminalPDUToDB: " + e.toString() );
			DBParameters="";
		}
	}
}