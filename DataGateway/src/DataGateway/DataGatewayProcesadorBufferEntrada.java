package DataGateway;

import PDULibrary.GestorCola;

public class DataGatewayProcesadorBufferEntrada extends Thread 
{
	GestorCola BufferEntradaSwitch;
	
	DataGatewayAdministradorCanales AdministradorCanales;

	public void run()
	{
		Object elemento=null;
		while( true)
		{
			try{
				while( BufferEntradaSwitch.tamano() > 0 )
				{
					elemento = (Object)BufferEntradaSwitch.getElemento();
					
					for( int i=0; i < AdministradorCanales.Canales.length; i ++ )
					{
						if( AdministradorCanales.Canales[i] != null )
						{
							AdministradorCanales.Canales[i].CanalBufferSaliente.putElemento( elemento );
						}
					}
					
				}
				try{
					Thread.sleep(50);
				}catch( Exception e)
				{
					
				}
			}catch( Exception e )
			{
				System.err.println("ERROR: ServerSwitchProcesadorBufferEntranda: Run: " + e.toString());
			}
		}
		
	}
	
	public DataGatewayProcesadorBufferEntrada( DataGatewayAdministradorCanales tmpAdministradorCanales , GestorCola tmpBufferEntradaSwitch )
	{
		try{
			BufferEntradaSwitch = tmpBufferEntradaSwitch;
			AdministradorCanales = tmpAdministradorCanales;
			start();
		}catch( Exception e)
		{	
			
		}
		
	}
	
}
