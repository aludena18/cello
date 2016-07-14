package DataGateway;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import PDULibrary.XMLPDU;
import PDULibrary.AppProperty;
import PDULibrary.GestorCola;


public class DataGatewayProcesadorBufferSalida extends Thread 
{
	GestorCola BufferSalidaSwitch;
	AppProperty APP;

	Connection DBConectionX = null;
	Statement stmXINS;
	
	String DBParametersX;
	
	private void AbrirDB()
	{
		try{
			DBConectionX = DriverManager.getConnection( DBParametersX );
		}catch( Exception e)
		{	DBConectionX = null;
		}
		
	}
	
	private void CerrarDB()
	{
		try{
			DBConectionX.close();
		}catch( Exception e)
		{
			
		}
		DBConectionX = null;
		
	}
		
	public void run()
	{
		XMLPDU PDU;
		String Query;
		
		while( true)
		{
			try{
				if( BufferSalidaSwitch.tamano() > 0 )
				{
					AbrirDB();
					
					while( BufferSalidaSwitch.tamano() > 0 && DBConectionX != null )
					{
						PDU = (XMLPDU)BufferSalidaSwitch.getElemento();
					
						Query = null;
						if( PDU.getElement("TIPO").compareTo("SMS") == 0 )
						{
							Query = "INSERT INTO BufferSmsSaliente (FechaHoraEscritura,Usuario,MIN,SMS) VALUES (GETDATE(),'" + PDU.getElement("USUARIO") + "','" + PDU.getElement("MIN") + "','" + PDU.getElement("MENSAJE") + "')";
						}
						else if( PDU.getElement("TIPO").compareTo("CMD") == 0 )
						{
							Query = "INSERT INTO CMD (FechaHoraEscritura,vid,cmd,SubRuta) VALUES (GETDATE(),'" + PDU.getElement("VID") + "','" + PDU.getElement("CMD") + "','" + PDU.getElement("SUBRUTA") + "')";
						}
						
						try{
							
							if( Query !=null)
							{
								stmXINS = DBConectionX.createStatement();
								stmXINS.execute(Query);
								
								try{
									stmXINS.close();
								}catch( Exception e)
								{
									
								}
								
							}
						}catch( Exception e)
						{
							BufferSalidaSwitch.putElemento(PDU);
							CerrarDB();
						}
							
					}
					CerrarDB();
				}
				try{
					Thread.sleep(50);
				}catch(Exception e)
				{
					
				}
			}catch( Exception e )
			{
				
			}
		}
		
	}
	
	public DataGatewayProcesadorBufferSalida( GestorCola tmpBufferSalidaSwitch, AppProperty tmpAPP )
	{
		try{
			
			APP = tmpAPP;
			DBParametersX = APP.getProperty("JdbcUrl") + "://"+ APP.getProperty("DBGpsUnitX_Address") + ":"
  			+ APP.getProperty("DBGpsUnitX_Port") + ";Database=" + APP.getProperty("DBGpsUnitX_Name") 
  			+ ";User=" + APP.getProperty("DBGpsUnitX_User") + ";Password=" + APP.getProperty("DBGpsUnitX_PWD") ;

			
			BufferSalidaSwitch = tmpBufferSalidaSwitch;
			
			start();
		}catch( Exception e)
		{	
			
		}
		
	}
	
}
