package DataGateway;
import java.net.*;

import PDULibrary.AppProperty;
import PDULibrary.GestorCola;

public class DataGatewayAdministradorCanales extends Thread
{
	GestorCola BufferSalidaSwitch;
	
	AppProperty APP;
	
	public DataGatewayProcesadorCanal Canales[];
	
	public synchronized int AgregarCanal( Socket tmpSocket , boolean Autenticar)
	{
		try{
			for( int i=0; i < Canales.length; i++)
			{
				if( Canales[i] == null )
				{
					synchronized( Canales )
					{
						Canales[i] = new DataGatewayProcesadorCanal( tmpSocket , BufferSalidaSwitch , Autenticar, APP );
					}
					System.err.println("MSG: DataGatewayAdministradorCanales: Se Agrego canal en indice " + i);
					return 0;
				}
			}
			
			return -2;
		}catch( Exception e)
		{
			System.err.println("ERROR: DataGatewayAdministradorCanales: " + e.toString() );
			return -1;
		}
	}
	
	public void run()
	{	
		int cont;
		while( true )
		{
			try{
				Thread.sleep( 30000 );
			}catch( Exception e)
			{
				
			}
			cont=0;
			
			for( int i=0; i < Canales.length; i++ )
			{
				try{
					if( Canales[i]!= null )
					{	
						synchronized( Canales[i] )
						{
							if( Canales[i].CanalActivo == false )
							{
								Canales[i] = null;
							}
							else
								cont++;
						}
					}
				}catch( Exception e )
				{
					System.err.println("ERROR: DataGatewayAdministradorCanales: Run: " + e.toString() );
					Canales[i] = null;
				}
			}
			
			System.err.println("MSG: DataGatewayAdministradorCanales: Run: Canales abiertos " + cont );
			
		}
	}
	
	public DataGatewayAdministradorCanales( GestorCola tmpBufferSalidaSwitch , AppProperty tmpAPP )
	{
		try{
			APP = tmpAPP;
			Canales = new DataGatewayProcesadorCanal[1000];
			
			BufferSalidaSwitch = tmpBufferSalidaSwitch;
			start();
			
		}catch( Exception e)
		{
			
		}
		
	}

}
