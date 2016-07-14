//V3.0.0
package HadesCELLOAPI;

import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import PDULibrary.AppProperty;
import PDULibrary.JENR;

public class ProcessorX_CELLOCMD extends Thread 
{
	AppProperty AP;
	DatagramSocket UDPServidor=null;
	
    String DBParameters;
	
    Connection ConexionBase = null,ConexionBase2;
    
	Statement stm,stm3,stm2;
	
	ResultSet rs,rs2;
	
	String IPServidor;
    	
	boolean DebugERROR,DebugTRAN;
			
	private void AbrirConexionDB()
	{
		try{
				
			ConexionBase = DriverManager.getConnection( DBParameters );
			
			
		}catch( Exception ee )
		{
			if( DebugERROR )
				System.err.println( "ERROR: ProcessorX_CLMPAPICMD " + UDPServidor.getLocalPort() + ": AbrirConexionDB: " + ee.toString() );
			CerrarConexionDB();
		}
		
		
	}
	
	private void CerrarConexionDB()
	{
		
		try{
			if( rs!= null)
				rs.close();
			rs=null;
		}catch( Exception e)
		{
			rs=null;
		}
		
		try{
			if( stm!=null)
				stm.close();
			stm=null;
		}catch( Exception e)
		{
			stm=null;
		}
		
		
		try{
			if( stm3 != null )
				stm3.close();
			stm3=null;
		}catch( Exception e)
		{
			stm3=null;
		}
		try{
			if( ConexionBase != null)
				ConexionBase.close();
			ConexionBase = null;
		
		}catch( Exception ee )
		{
			ConexionBase = null;
		}
		
		
	}
		
	public void run()
	{//HexVID
		String VID, Comando, IP, Puerto,DireccionPuerto,Valor; 
		
		//byte[] cmd = {(byte)0x07,0x00,0x00,0x03,0x08,0x00,0x00,0x00,0x00,0x00,0x00};
		byte[] cmd;//  = {(byte)0x83,0x05,0x00,0x00,0x00,0x00,0x00,0x01,0x04,0x01,0x07,0x00,0x00,0x03,0x08,0x00,0x00,0x00,0x00,0x00,0x00};
		
		
		Date FechaHoraEscritura; Calendar C;
		Time T;
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS");
		
		DatagramPacket packet2;
		
		while( true )
		{
			try{
				if( UDPServidor != null && ConexionBase == null )
					AbrirConexionDB();
				
				if( ConexionBase != null )
				{
					stm = ConexionBase.createStatement();
					
					rs = stm.executeQuery(  "SELECT C.FECHAHORAESCRITURA, C.VID, C.CMD,G.SOURCE " +
											"FROM CMD as C, REPORTEPOSICION_LAST as G WITH (NOLOCK) " +
											"WHERE C.FECHAHORAENVIADO IS NULL AND " +
											"C.FECHAHORAESCRITURA >= ( SELECT DATEADD(minute, -3, getdate() ) ) AND " + 
											"C.VID = G.ID AND " +
											"G.IDPROTOCOLO = 99 " +
											//"G.IDPROTOCOLO = 99 AND " +
											//"G.SOURCE LIKE '%" + IPServidor + "::" + UDPServidor.getLocalPort() + "' " +
											"ORDER BY FECHAHORAESCRITURA"
										);
					
					
					
					while( rs.next() == true )
					{
						cmd = null;
						FechaHoraEscritura = rs.getDate(1);
						T = rs.getTime(1);						
						VID =  rs.getString(2);
						Comando = rs.getString(3);
						
						DireccionPuerto = rs.getString(4);
						
						C =  new GregorianCalendar();
						
						C.setTime( FechaHoraEscritura );
						C.add( Calendar.MILLISECOND, (int)T.getTime());
						C.add(Calendar.HOUR_OF_DAY, -5);
						
						boolean EnviarComando = false;
						
						try{
											
							stm2 = ConexionBase.createStatement();
							
							rs2 = stm2.executeQuery( "SELECT TOP 1 * FROM CMD WITH (NOLOCK) " + 
													 "WHERE VID = '" + VID + "' AND " +
													 "FechaHoraEnviado IS NOT NULL AND " +
													 "FechaHoraEscritura <= '" + sdf.format( C.getTime() ) + "' AND " + 
													 "FechaHoraEscritura > DATEADD( second , -10 , '" + sdf.format( C.getTime()) + "' ) " +
													 "ORDER BY FechaHoraEscritura DESC");
							if( rs2.next() )
								EnviarComando = false;
							else
								EnviarComando = true;
							
						}catch( Exception e )
						{
							System.err.println(" ERROR: ProcessorX_CLMPAPICMD: " + e.toString() );
						}finally
						{
							try{
								if( rs2 != null)
								{
									rs2.close();
								}
								
							}catch( Exception ee)
							{
								
							}finally
							{
								rs2=null;
							}
							
							try{
								if( stm2 != null)
								{
									stm2.close();
								}
								
							}catch( Exception ee)
							{
								
							}finally
							{
								stm2=null;
							}
							
						}
							
						if( EnviarComando )
						{								
							IP = DireccionPuerto.substring( DireccionPuerto.indexOf('/')+1, DireccionPuerto.indexOf(':'));
							
							if( DireccionPuerto.indexOf("//") > 0 )
								Puerto = DireccionPuerto.substring( DireccionPuerto.indexOf(':')+1,DireccionPuerto.indexOf("//") );
							else
								Puerto = DireccionPuerto.substring( DireccionPuerto.indexOf(':')+1 );
													
							Comando = Comando.trim();
							
							String parametros;
				        	String[] p;
				        	
				        	Comando = Comando.toLowerCase();
				        	
				        	if( Comando.indexOf( "at$app=") >= 0)
				        	{
				        		cmd = new byte[]{(byte)0x83,0x05,0x00,0x00,0x00,0x00,0x00,0x01,0x04,0x01,0x07,0x00,0x00,0x03,0x08,0x00,0x00,0x00,0x00,0x00,0x00};
				        		
				        		parametros = Comando.substring(Comando.indexOf( "at$app=" ) + 7);
				        		
				        		p = parametros.split(",");
				        						        		
				        		if( VID.length() == 10 )
				        		{
				        			for( int j = 9; j >= 0;j=j-2)
				        			{
				        				cmd[1+((j+1)/2)] = (byte) (Integer.parseInt( "" + VID.charAt(j-1) + VID.charAt( j ) , 16)&0xff);
				        				
				        			}
				        			
				        			
				        		}
				        		
				        		for( int i=0; i < p.length; i++ )
				        		{
				        			System.err.println( p[i]);
				        			if( i == 0 )
				        			{
				        				cmd[10] = Byte.valueOf( p[i] );
				        			}
				        			else if( i == 1 )
				        			{
				        				cmd[13] = Byte.valueOf( p[i] );
				        			}
				        			else if( i == 2 )
				        			{
				        				cmd[14] = Byte.valueOf( p[i] );
				        			}
				        			else if( i == 3 )
				        			{
				        				cmd[16] = Byte.valueOf( p[i] );
				        			}        			
				        		}
				        		
				        		
				        		
							}
				        	else if( Comando.indexOf( "at$app peg action ") >= 0)
				        	{
				        		cmd = new byte[]{(byte)0x83,0x05,0x00,0x00,0x00,0x00,0x00,0x01,0x04,0x01,0x07,0x00,0x00,0x03,0x09,0x00,0x00,0x00,0x00,0x00,0x00};
				        		
				        		parametros = Comando.substring( Comando.indexOf("at$app peg action ") + "at$app peg action ".length() );
				        		
				        		p = parametros.split( " " );
				        		
				        		if( p != null && p.length == 2)
				        		{
				        			if( VID.length() == 10 )
					        		{
					        			for( int j = 9; j >= 0;j=j-2)
					        			{
					        				cmd[1+((j+1)/2)] = (byte) (Integer.parseInt( "" + VID.charAt(j-1) + VID.charAt( j ) , 16)&0xff);
					        				
					        			}
					        		}
				        						        			
				        			cmd[14] = Byte.valueOf( p[0] );
				        			cmd[16] = Byte.valueOf( p[1] );
				        			     			
				        		}
				        	}
				        	else if( Comando.indexOf( "at$app acc set ") >= 0)
				        	{
				        		cmd = new byte[]{(byte)0x83,0x05,0x00,0x00,0x00,0x00,0x00,0x01,0x04,0x01,0x06,0x00,0x00,0x01,0x0a,0x00,0x00,0x05,0x03,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
				        		
				        		parametros = Comando.substring( Comando.indexOf("at$app acc set ") + "at$app acc set ".length() );
				        		
				        		p = parametros.split( " " );
				        		
				        		if( p != null && p.length == 2)
				        		{
				        			if( VID.length() == 10 )
					        		{
					        			for( int j = 9; j >= 0;j=j-2)
					        			{
					        				cmd[1+((j+1)/2)] = (byte) (Integer.parseInt( "" + VID.charAt(j-1) + VID.charAt( j ) , 16)&0xff);
					        				
					        			}
					        		}
				        						        			
				        			cmd[18] = Byte.valueOf( p[0] );
				        			
				        			Valor = Integer.toHexString( Integer.parseInt( p[1] ) );
				                	
				                	System.err.println( Valor );
				                	
				                	while( Valor.length() < 8 )
				                	{
				                		Valor = "0" + Valor; 
				                	}
				                	
				                	System.err.println( Valor );
				        			
				        			
				        			cmd[19] = (byte) (Integer.parseInt( "" + Valor.charAt(0) + Valor.charAt( 1 ) , 16)&0xff);
				        			cmd[20] = (byte) (Integer.parseInt( "" + Valor.charAt(2) + Valor.charAt( 3 ) , 16)&0xff);
				        			cmd[21] = (byte) (Integer.parseInt( "" + Valor.charAt(4) + Valor.charAt( 5 ) , 16)&0xff);
				        			cmd[22] = (byte) (Integer.parseInt( "" + Valor.charAt(6) + Valor.charAt( 7 ) , 16)&0xff);
				        			
				        			     			
				        		}
				        	}
				        	
				        	
				        	else if( Comando.indexOf("ati4") >= 0 )
				        	{
				        		cmd = new byte[]{(byte)0x83,0x05,0x00,0x00,0x00,0x00,0x00,0x01,0x04,0x01,0x07,0x00,0x00,0x05,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
				        		
				        		if( VID.length() == 10 )
				        		{
				        			for( int j = 9; j >= 0;j=j-2)
				        			{
				        				cmd[1+((j+1)/2)] = (byte) (Integer.parseInt( "" + VID.charAt(j-1) + VID.charAt( j ) , 16)&0xff);
				        				
				        			}
				        		}
				        	}
				        	else if( Comando.indexOf("atreset") >= 0 )
				        	{
				        		cmd = new byte[]{(byte)0x83,0x05,0x00,0x00,0x00,0x00,0x00,0x01,0x04,0x01,0x07,0x00,0x00,0x01,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
				        		
				        		if( VID.length() == 10 )
				        		{
				        			for( int j = 9; j >= 0;j=j-2)
				        			{
				        				cmd[1+((j+1)/2)] = (byte) (Integer.parseInt( "" + VID.charAt(j-1) + VID.charAt( j ) , 16)&0xff);
				        				
				        			}
				        		}
				        	}
				        	else if( Comando.startsWith("0x") )
				        	{
				        		
				        		
				        		parametros = Comando.substring(2);
				        		
				        		if( parametros.length() % 2 != 0)
				        		{
				        			parametros = "0" + parametros;
				        		}
				        		
				        		cmd = new byte[parametros.length() / 2];
				        		for( int j = parametros.length() - 1; j > 0;j=j-2)
			        			{
			        				cmd[((j+1)/2) - 1] = (byte) (Integer.parseInt( "" + parametros.charAt(j-1) + parametros.charAt( j ) , 16) & 0xff);
			        				
			        			}
				        	}
				        
				        	
				        	
				        	//ENVIAR COMANDO VIA AIRE
				        	packet2 = new DatagramPacket( cmd , cmd.length , InetAddress.getByName(  IP ) , Integer.parseInt( Puerto ) );
							
							C =  new GregorianCalendar();
							
							C.setTime( FechaHoraEscritura );
							C.add( Calendar.MILLISECOND, (int)T.getTime());
							C.add(Calendar.HOUR_OF_DAY, -5);
								
							stm3 = ConexionBase.createStatement();
							stm3.executeUpdate( "UPDATE CMD set FechaHoraEnviado = getdate() , CMD = CMD + ' (" + JENR.ByteArrayToHexString(cmd) + ")' where VID = '" + VID + "' and FechaHoraEscritura = '" + sdf.format( C.getTime()) + "' and FechaHoraEnviado is null");
									
							try{
								UDPServidor.send( packet2 );
								System.err.println( "MSG: ProcessorX_CLMPAPICMD: Run: Se envio comando " + Comando + " a " + VID + "/" + IP + ":" + Puerto);
							}catch( Exception e6)
							{
							}
							try{
								stm3.close();
								stm3=null;
							}catch( Exception e)
							{
								stm3=null;
							}
				        	
				        		
				        }
							
							
							
							
							
							
							
							
							
					}
					
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
				}
				try {
					sleep( 20 );
				}catch( Exception e2)
				{
				}
			}catch( Exception e1)
			{
				CerrarConexionDB();
				System.err.println( "ERROR: ProcessorX_CLMPAPICMD: Run: " + e1.toString() );
			}
			CerrarConexionDB();
			try {
				sleep( 1000 );
			}catch( Exception e2)
			{
			}
		}
	}
	
	public ProcessorX_CELLOCMD( AppProperty tmpAP, DatagramSocket tmpUDPServidor  )
	{
		try{
			this.setName( this.getName() + ": ProcessorUDDAPICMD: " );
			
			AP = tmpAP;
			UDPServidor = tmpUDPServidor;
			
			DBParameters = AP.getProperty("JdbcUrl") + "://"+ AP.getProperty("DBGpsUnitX_Address") + ":"
			  + AP.getProperty("DBGpsUnitX_Port") + ";Database=" + AP.getProperty("DBGpsUnitX_Name") 
			  + ";User=" + AP.getProperty("DBGpsUnitX_User") + ";Password=" + AP.getProperty("DBGpsUnitX_PWD") ;
			
			if( AP.getProperty("IPServidor") != null )
				IPServidor = AP.getProperty("IPServidor");
		    
		    if( IPServidor != null )
		    	System.err.println("IP SERVIDOR:  " + IPServidor );
			
			if( AP.getProperty("DebugUDPAPItoDBError").compareTo("true") == 0)
				DebugERROR = true;
			else
				DebugERROR = false;
				
			if( AP.getProperty("DebugUDPAPItoDBTran").compareTo("true") == 0)
				DebugTRAN = true;
			else
				DebugTRAN = false;
			
			this.start();
			
		}catch( Exception e)
		{
	    	System.err.println( "ERROR: ProcessorX_CLMPAPICMD: " + e.toString());
	    	DBParameters="";
	    }
	}
	
}