package DataGateway;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import PDULibrary.AppProperty;
import PDULibrary.XMLPDU;
import PDULibrary.GestorCola;

public class DataGatewayProcesadorCanal extends Thread 
{
	boolean CanalActivo = false, Ejecutar= true;
	Socket Canal;
	InputStream FlujoEntradaCanal;
	OutputStream FlujoSalidaCanal;
	DataGatewayProcesadorCanalBufferSalida ProcesadorCanalBufferSaliente;
	
	String VIDS[];
	int nv;
	AppProperty AP;
	
	String DBParameters;
	
	boolean Autenticado = false;
	
	Connection ConexionBase = null;
	
	Statement stmSEL=null;
	
	ResultSet rs;
	
	
	boolean Autenticar;
	
	public synchronized boolean VIDconPermiso( String VID )
	{
		try{
			if( Autenticar == true )
			{	
				if( VIDS != null )
				{
					for( int i=0 ; i < VIDS.length ; i++ )
					{
						if ( VIDS[i].compareTo(VID) == 0 )
							return true;
					}
				}
			}
			else 
				return true;
			return false;
		}catch( Exception e)
		{
			return false;
		}
		
		
	}
	
	private void AbrirConexionDB()
	{
		try{
				
			ConexionBase = DriverManager.getConnection( DBParameters );
					
		}catch( Exception e )
		{
			System.err.println( "ERROR: DataGatewayProcesadorCanal: AbrirConexionDB: " + e.toString() );
			CerrarConexionDB();
		}
	}
	
	private void CerrarConexionDB()
	{
		try{
			rs.close();
			rs=null;
		}catch( Exception e1)
		{
			rs=null;
		}
		
		try{
			stmSEL.close();
			stmSEL=null;
			
		}catch( Exception e1)
		{
			stmSEL=null;
		}
		
		try{
			ConexionBase.close();
			ConexionBase= null;
		}catch( Exception e2 )
		{
			ConexionBase = null;
		}
		
	}
	
	public GestorCola CanalBufferSaliente, BufferSalidaSwitch;
	
	public synchronized boolean esCanalActivo()
	{
		return CanalActivo;
	}
	
	public synchronized void CerrarCanal()
	{
		
		
		try{
			FlujoEntradaCanal.close();
		}catch(Exception e)
		{
		}
		try{
			FlujoSalidaCanal.close();
		}catch(Exception e)
		{
		}
		try{
			Canal.close();
		}catch(Exception e)
		{
		}
		Ejecutar= false;
		CanalActivo = false;
	}
	
	public void run()
	{
		byte tmpBufferIn[];
		
		XMLPDU PDU;
		
		if( Autenticar == false )
			Autenticado = true;
		
		while( Ejecutar )
		{
			try{
				if( CanalActivo == true  )
				{
					if( FlujoEntradaCanal.available() > 0 )
					{
						tmpBufferIn = new byte[ FlujoEntradaCanal.available()];
						
						FlujoEntradaCanal.read( tmpBufferIn );
					
						PDU = new XMLPDU( "" + new String(tmpBufferIn) );
						
						//AUTENTICAR CONEXION
						if( Autenticado == false )
						{	
							System.err.println( "" + new String( tmpBufferIn )  );
							
							if( PDU.getElement("TIPO").compareTo( "AUTENTICACION" ) == 0 )
							{
								AbrirConexionDB();
								
								if( ConexionBase != null )
								{
									try{
								
										stmSEL = ConexionBase.createStatement();
									
										rs = stmSEL.executeQuery( "SELECT COUNT(*) FROM Usuario U,UsuarioEntidad UE,EntidadActivo EA,Activo A,ActivoDispositivo AD, Dispositivo D, Aplicacion APP, UsuarioAplicacion UAPP WITH (NOLOCK) " +
																  "WHERE  U.Usuario = '" + PDU.getElement("USUARIO") + "' AND " +
																  "U.Clave = '" + PDU.getElement("CONTRASENA") + "'	AND " +
																  "U.IdUsuario = UE.IdUsuario AND " +
																  "UE.IdEntidad = EA.IdEntidad AND " +
																  "EA.IdActivo = A.IdActivo AND " +
																  "A.IdActivo = AD.IdActivo AND " +
																  "AD.IdDispositivo = D.IdDispositivo AND " + 
																  "U.IdUsuario = UAPP.IdUsuario AND " +
																  "UAPP.IdAplicacion = APP.IdAplicacion AND " +
																  "APP.Nombre = 'DATAGATEWAY' " 
																);	
																		
										if( rs.next() )
											nv =  rs.getInt( 1 );
																			
										if( nv > 0 )
										{
											VIDS = new String[nv];
										
											System.err.println("MSG: DataGatewayProcesadorCanal: Run: Usuario " + PDU.getElement("USUARIO") + " con permiso y acceso a " + nv + " vehiculos" );
											
											try{
												rs.close();
												stmSEL.close();
											}catch( Exception eee )
											{
											
											}
									
											stmSEL = ConexionBase.createStatement();
									
									
											rs = stmSEL.executeQuery( "SELECT U.IdUsuario , D.IdDispositivo FROM Usuario U,UsuarioEntidad UE,EntidadActivo EA,Activo A,ActivoDispositivo AD, Dispositivo D, Aplicacion APP, UsuarioAplicacion UAPP WITH (NOLOCK) " +
																	  "WHERE  U.Usuario = '" + PDU.getElement("USUARIO") + "' AND " +
																	  "U.Clave = '" + PDU.getElement("CONTRASENA") + "'	AND " +
																	  "U.IdUsuario = UE.IdUsuario AND " +
																	  "UE.IdEntidad = EA.IdEntidad AND " +
																	  "EA.IdActivo = A.IdActivo AND " +
																	  "A.IdActivo = AD.IdActivo AND " +
																	  "AD.IdDispositivo = D.IdDispositivo AND " + 
																	  "U.IdUsuario = UAPP.IdUsuario AND " +
																	  "UAPP.IdAplicacion = APP.IdAplicacion AND " +
																	  "APP.Nombre = 'DATAGATEWAY' and U.Estado = 'A'" + 
																	  "ORDER BY 2"
																	);
										
										
											int i=0;
										
											while( rs.next() )
											{
												VIDS[i] = rs.getString(2);
												i++;
											}
										
											Autenticado = true;
											FlujoSalidaCanal.write( ("<SAT><TIPO>AUTENTICACION_RSP</TIPO><MSG>OK</MSG><NV>" + nv + "</NV><TD>1" + "</TD></SAT>").getBytes() );
											FlujoSalidaCanal.flush();
									
										}
										else
										{
											System.err.println("MSG: DataGatewayProcesadorCanal: Run: Usuario sin permisos: " + PDU.getElement("USUARIO") + " " + PDU.getElement("CONTRASENA"));
											FlujoSalidaCanal.write( "<SAT><TIPO>AUTENTICACION_RSP</TIPO><MSG>ERROR</MSG></SAT>".getBytes() );
											FlujoSalidaCanal.flush();
											CerrarCanal();
										}
									
										CerrarConexionDB();
																	
									}catch( Exception ee )
									{
										System.err.println("ERROR:DateGatewayProcesadorCanal: Run: DB: " + ee.toString() );
									}
								
								}
								else //CERRAR CANAL EN VISTA DE QUE NO HAY BASE DE DATOS
								{
									FlujoSalidaCanal.write( "<SAT><TIPO>AUTENTICACION_RSP</TIPO><MSG>ERROR</MSG></SAT>".getBytes() );
									FlujoSalidaCanal.flush();
									CerrarCanal();
								}
								
							}
							else // SE CIERRA CANAL EN VISTA DE QUE NO CUMPLIO CON SU PRIMER PASO DE AUTENTICACION
							{
								FlujoSalidaCanal.write( "<SAT><TIPO>AUTENTICACION_RSP</TIPO><MSG>ERROR</MSG></SAT>".getBytes() );
								FlujoSalidaCanal.flush();
								CerrarCanal();
							}
						}
						else
						{	
							if( PDU.getElement("TIPO")!=null && PDU.getElement("TIPO").compareTo("CMD")== 0 )
							{
								if( PDU.getElement("VID")!=null && VIDconPermiso( PDU.getElement("VID") ) == true )  
								{
									BufferSalidaSwitch.putElemento( PDU );
								}
							}
							else
								BufferSalidaSwitch.putElemento( PDU );
							
						}
					}
				}
								
				try{
					Thread.sleep( 50 );
				}catch( Exception e)
				{
				}				
			}catch( Exception e )
			{
				System.err.println("ERROR: DataGatewayAdministradorCanales: Run: " + e.toString());
				CanalActivo = false;
			}
			
		}
	}
	
	public DataGatewayProcesadorCanal( Socket tmpCanal, GestorCola tmpBufferSalidaSwitch , boolean tmpAutenticar, AppProperty tmpAPP ) 
	{
		try{
			if( tmpCanal != null )
			{	
				Autenticar = tmpAutenticar;
				
				AP = tmpAPP;
				
				DBParameters = AP.getProperty("JdbcUrl") + "://"+ AP.getProperty("DBGpsUnitX_Address") + ":"
	  			   			 + AP.getProperty("DBGpsUnitX_Port") + ";Database=" + AP.getProperty("DBGpsUnitX_Name") 
	  			             + ";User=" + AP.getProperty("DBGpsUnitX_User") + ";Password=" + AP.getProperty("DBGpsUnitX_PWD") ;
				
				Canal = tmpCanal;
				FlujoEntradaCanal = Canal.getInputStream();
				FlujoSalidaCanal = Canal.getOutputStream();
				BufferSalidaSwitch = tmpBufferSalidaSwitch;
				CanalBufferSaliente = new GestorCola();
				ProcesadorCanalBufferSaliente = new DataGatewayProcesadorCanalBufferSalida( CanalBufferSaliente , FlujoSalidaCanal , this );
				CanalActivo = true;
				start();
			}
			else
			{
				try{
					this.finalize();
				}catch (Throwable e)
				{
				}
			}
		}catch( Exception e)
		{
			
		}
	}

}
