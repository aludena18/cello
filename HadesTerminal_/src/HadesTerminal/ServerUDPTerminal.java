//V3.0.0

package HadesTerminal;

import java.net.*;
import PDULibrary.AppProperty;

public class ServerUDPTerminal extends Thread 
{
	AppProperty AP;
	int Puerto;
	DatagramSocket UDPServidor;
	
	boolean Running = false, ACKNOWLEDGE = false;
	
	byte[] buffer;
	DatagramPacket packet, packet2;
	String NombreServidor = null;

	boolean DebugERROR,DebugTRAN;
	
	public DatagramSocket getUDPServer()
	{
		return UDPServidor;
	}

	private int InicializarServer() 
	{
		try {
			UDPServidor = new DatagramSocket(Puerto);
			System.out.println("MSG: ServerUDPTerminal: " + NombreServidor + ": Servidor inicializado en puerto " + Puerto);
			return 0;
		}catch (Exception e)
		{
			CerrarServer();
			System.err.println("ERR: ServerUDPTerminal: " + NombreServidor + ": No se pudo inicializar servidor en puerto: " + Puerto + ": "
								+ e.toString());
			return -1;
		}
	}

	private int CerrarServer() 
	{
		try {
			UDPServidor.close();
			UDPServidor = null;
			System.out.println("MSG: ServerUDPTerminal: " + NombreServidor + ": Servidor cerrado");
			return 0;
		}catch (Exception e)
		{
			UDPServidor = null;
			System.err.println("ERR: ServerUDPTerminal: " + NombreServidor + ": No se pudo cerrar servidor correctamente: "
							+ e.toString());
			return -1;
		}
	}
	
	public void run() 
	{
		while( Running != true )
		{
			if( InicializarServer() == -1 )
			{
				try{
					Thread.sleep(30000);
				}catch( Exception e)
				{
				}
			}
			else
				Running = true;
		}

		while (Running) 
		{
			try{
				buffer = new byte[4096];
				packet = new DatagramPacket(buffer, buffer.length);
				UDPServidor.receive(packet);
		
				if( AP.getProperty("MostrarUDPTerminalRecibido")!=null && AP.getProperty("MostrarUDPTerminalRecibido").compareTo("true") == 0)
				{
					System.err.println("MSG: ServerUDPTerminal: " + NombreServidor + ": TERMINAL PDU RECIBIDO: " + new String( packet.getData() ).substring( 0 , packet.getLength() ) + ": "
						+ packet.getAddress().toString());
				}
				
				new ProcessorTerminalPDUToDB( packet , UDPServidor, AP);
				
				

			}catch (Exception e) 
			{
				System.err.println("ERROR: ServerUDPTerminal: " + NombreServidor + ": Run: Network: "
						+ e.toString());
			}

		}

	}

	public ServerUDPTerminal(int tmpPuerto, String tmpNombreServidor, AppProperty tmpAP)
	{
		InitServer(tmpPuerto, tmpNombreServidor, tmpAP);
	}

	private void InitServer(int tmpPuerto, String tmpNombreServidor, AppProperty tmpAP)
	{	
		this.setName( this.getName() + ": ServerUDPTerminal");
		
		AP = tmpAP;
		NombreServidor = tmpNombreServidor;
		Puerto = tmpPuerto;
		
		this.start();
	}

}