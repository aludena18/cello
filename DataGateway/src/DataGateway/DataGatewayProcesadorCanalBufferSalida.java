package DataGateway;

import java.io.OutputStream;

import PDULibrary.GestorCola;
import PDULibrary.PDU;


public class DataGatewayProcesadorCanalBufferSalida extends Thread 
{
	
	OutputStream FlujoSalidaCanal;
	
	GestorCola BufferSalida;
	
	boolean Ejecutar = true;
	
	DataGatewayProcesadorCanal ProcesadorCanal;
	
	
public void run()
{
		
		PDU P=null;
		int c=0;
		while( Ejecutar )
		{
			try{
				
				while( BufferSalida.tamano() > 0 )
				{   
					P=null;
					c=0;
					
					P = (PDU)BufferSalida.getElemento();
					c++;
					
					if( ProcesadorCanal.VIDconPermiso( P.getModemID() ) == true )
					{
						c++;
						FlujoSalidaCanal.write( P.getB_PDU() );
						c++;
					}
				}
				try{
					Thread.sleep(25);
				}catch( Exception e)
				{
					
				}
			}catch( Exception e )
			{
				System.err.println("ERROR: DataGatewayAdministradorCanalesBufferSalida: Run: " + c + " " + e.toString());
				System.err.println("ERROR: DataGatewayAdministradorCanalesBufferSalida: Run: " + c + " " + BufferSalida );
				try{
					System.err.println("ERROR: DataGatewayAdministradorCanalesBufferSalida: Run: " + c + " " + BufferSalida.tamano() );
					System.err.println("ERROR: DataGatewayAdministradorCanalesBufferSalida: Run: " + c + " " + P );
				}catch( Exception ee)
				{
					System.err.println("ERROR: DataGatewayAdministradorCanalesBufferSalida: Run: " + c + " " + ee.toString() );
					
				}
				ProcesadorCanal.CerrarCanal();
				Ejecutar = false;
			}
			
		}
	}

public DataGatewayProcesadorCanalBufferSalida( GestorCola tmpBufferSalida , OutputStream tmpFlujoSalidaCanal, DataGatewayProcesadorCanal tmpProcesadorCanal ) 
	{
		try{
			
			ProcesadorCanal = tmpProcesadorCanal;
			FlujoSalidaCanal = tmpFlujoSalidaCanal;
			BufferSalida = tmpBufferSalida;
			start();
		}catch( Exception e)
		{
			
		}
	}

}
