
package DataGateway;

import java.net.*;
import PDULibrary.AppProperty;
import PDULibrary.GestorCola;


public class DataGateway extends Thread
{	
	public GestorCola BufferEntradaSwitch, BufferSalidaSwitch;
	
	DataGatewayProcesadorBufferSalida ProcesadorBufferSalida;
	
	DataGatewayProcesadorBufferEntrada ProcesadorBufferEntrada;
	
	DataGatewayAdministradorCanales AdministradorCanales;
	
	ServerSocket Switch=null;
	
	AppProperty APP;
	
	Socket Canal;

	boolean Autenticar;
	
	int PuertoServidor;
	
	private void abrirGateway()
	{
		try{
			
			Switch = new ServerSocket( PuertoServidor );
			
			System.err.println("MSG: DataGateway inicializado en puerto " + PuertoServidor );
			
		}catch( Exception e)
		{
			Switch=null;
			System.err.println("ERROR: DataGateway: abrirServidor: " + e.toString() );
		}
	}
	
	/*private void cerrarSwitch()
	{
		try
		{
			
		}catch( Exception e )
		{
			Switch=null;
		}
		
	}*/
	
	public void run()
	{
		while( Switch == null )
		{
			abrirGateway();
			
			if( Switch == null )
			{
				try{
					Thread.sleep( 30000 );
				}catch( Exception e)
				{
				}
			}
		}
		
		while( true )
		{
			try{
				
				Canal = Switch.accept();
				
				AdministradorCanales.AgregarCanal( Canal , Autenticar );
				
				System.err.println("MSG: DataGateway: Se recibio conexion desde " + Canal.getInetAddress().toString() );
			
			}catch( Exception e)
			{
				System.err.println( "ERROR: DataGateway: Run: " + e.toString() );
			}
		}
			
	}
	
	public DataGateway( int tmpPuertoServidor , boolean tmpAutenticar, AppProperty tmpAPP)
	{
		try{
			
			Autenticar = tmpAutenticar;
			APP = tmpAPP;
			BufferEntradaSwitch = new GestorCola();
			BufferSalidaSwitch = new GestorCola();
			
			PuertoServidor = tmpPuertoServidor;
			
			ProcesadorBufferSalida = new DataGatewayProcesadorBufferSalida( BufferSalidaSwitch , APP  );
			AdministradorCanales = new DataGatewayAdministradorCanales( BufferSalidaSwitch ,  APP );
			ProcesadorBufferEntrada = new DataGatewayProcesadorBufferEntrada( AdministradorCanales , BufferEntradaSwitch );
			
			start();
			
		}catch( Exception e)
		{
			System.err.println( "ERROR: DataGateway: DataGateway: " + e.toString() );
		}
	}

}
