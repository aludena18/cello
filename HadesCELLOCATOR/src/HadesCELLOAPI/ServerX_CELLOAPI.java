//V3.0.0

package HadesCELLOAPI;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import DataGateway.DataGateway;
//import HadesTerminal.ProcessorTerminalPDUToDB;
import PDULibrary.AppProperty;
import PDULibrary.CELLOPDU;
import PDULibrary.JENR;
import ProcessorLibrary.AlertEngine;
import ProcessorLibrary.CMDRSPToDBEngine;
import ProcessorLibrary.OdometerEngine;
import ProcessorLibrary.PDUFoward;
import ProcessorLibrary.PDUtoEDDIERouter;
import ProcessorLibrary.PositionReportToDBEngine;
import ProcessorLibrary.TaxiDataToDBEngine;

public class ServerX_CELLOAPI extends Thread
{
	AppProperty AP;

	int Puerto;
	DatagramSocket UDPServidor;
	boolean Running = false, ACKNOWLEDGE = false,toTerminalPDU=false;;
	byte[] buffer;
	DatagramPacket packet,packet2; 
	String NombreServidor = null;

	//MOTORES
	PositionReportToDBEngine[] MotorReportePosicionToDB;
	TaxiDataToDBEngine[] MotorDatosTaxiToDB;
	AlertEngine[] MotorAlertas;
	OdometerEngine[] MotorOdometro;
	CMDRSPToDBEngine[] MotorCMDResponse;

	PDUtoEDDIERouter[] EDDIERouter;

	String IPServidor;

	//-----------------------
	PDUFoward[] Foward;
	//-----------------------

	DataGateway DG =null;

	//-----------------------
	int FowardType=1;
	//-----------------------

	boolean DebugERROR,DebugTRAN;

	boolean MostrarUDPGpsRecibido=false,GuardarEnGeosysDatosNoRuteados=false,GuardarLocalmenteDatosNoRuteados=false;

	private int InicializarServer()
	{
		try{
			UDPServidor = new DatagramSocket( Puerto );
			System.out.println( "MSG: ServerX_CELLOAPI: Servidor inicializado en puerto " + Puerto );

			return 0;
		}catch( Exception e)
		{	
			CerrarServer();
			System.err.println( "ERR: ServerX_CELLOAPI: No se pudo inicializar servidor: " + Puerto + ": " + e.toString()  );
			return -1;
		}
	}

	private int CerrarServer()
	{
		try{
			UDPServidor.close();
			UDPServidor = null;
			System.out.println( "MSG: ServerX_CELLOAPI: Servidor cerrado" );
			return 0;

		}catch( Exception e)
		{
			UDPServidor = null;
			System.err.println( "ERROR: ServerX_CELLOAPI: No se pudo cerrar servidor correctamente: " + e.toString() );
			return -1;
		}
	}

	public void run()
	{		
		buffer = new byte[512];
		DatagramPacket RespuestaDatagrama;
		CELLOPDU PDU=null;

		//int cont=0,ma=0,mr=0,mt=0,mo=0,cr=0;

		while( Running != true )
		{
			if( InicializarServer() == -1 )
			{
				try{
					Thread.sleep( 30000 );
				}catch( Exception e)
				{
					System.err.println( "ERROR: ServerX_CELLOAPI: Run: InicializarServer: " + e.toString() );
				}
			}
			else
			{
				Running = true;
			}
		}
		//---------------------------------------------------------------------------------------------------------

		//Foward = new PDUFoward[5];


		try
		{	
			if( Foward == null )
				Foward = new PDUFoward[5];

			for( int i=0; i < Foward.length;i++)
			{
				if( FowardType == 1 )
					Foward[i] = new PDUFoward( UDPServidor , "HadesCELLO " + Puerto , AP );
				else if( FowardType == 2)
					Foward[i] = new PDUFoward( UDPServidor , "HadesCELLOP" + Puerto , AP , 2, "190.85.173.67" , 9005);
			}
		}catch( Exception e)
		{
			System.err.println("Exception: ServerX_CELLOAPI: Run: Inicializar Foward. " + e.toString());
		}
		//---------------------------------------------------------------------------------------------------------

		new ProcessorX_CELLOCMD( AP , UDPServidor );

		int contPDUS=0,contACK=0,/*ma=0,mr=0,mo=0,*/er=0,cr=0,pf=0;
		long UltimoReporte = System.currentTimeMillis();

		while( Running )
		{
			try{				
				packet = new DatagramPacket( buffer, buffer.length );
				UDPServidor.receive( packet );
				contPDUS++; 

				PDU = new CELLOPDU( packet , UDPServidor , IPServidor );

				if( ACKNOWLEDGE == true && PDU.getBACK() != null    )
				{
					if(PDU.getType() == 0 || PDU.getType()==11){
						RespuestaDatagrama = new DatagramPacket( PDU.getBACK(), PDU.getBACK().length, packet.getAddress(),packet.getPort() );
						UDPServidor.send( RespuestaDatagrama );
						contACK++;
					}
				}


				if( PDU.isPositionReportPDU() == true )
				{
					//System.err.println(PDU.ToString());


					if( MostrarUDPGpsRecibido )
						System.out.println( "MSG: " + NombreServidor + ": " + packet.getAddress() + ":" +  packet.getPort() + " : " + UDPServidor.getLocalPort() + " : UDP RECIBIDO: " + PDU.getRAWDATA() );

					if( GuardarEnGeosysDatosNoRuteados )
					{
						//GUARDAR EN BASE DE DATOS REPORTE DE POSICION
						/*if( MotorReportePosicionToDB != null )
						{
							MotorReportePosicionToDB[ mr ].BufferEntrada.putElemento( PDU );
							mr++;

							if( mr == MotorReportePosicionToDB.length )
								mr = 0;
						}*/


						//PARA PROCESAR ODOMETRO
						/*int e;
						try{
							e = Integer.parseInt(PDU.getInputEvent());
						}catch( Exception e46)
						{
							e=0;
						}

	    				if( e == 7 || e == 207 || e == 30)
	    				{
	    					if( MotorOdometro != null )
	    					{
	    						MotorOdometro[mo].BufferEntrada.putElemento( PDU );
	    						mo++;

	    						if( mo == MotorOdometro.length )
	    							mo = 0;

	    					}
	    				}

						try{
							if( toTerminalPDU == true )
								new ProcessorTerminalPDUToDB( PDU , AP );
						}catch( Throwable ee )
						{
							System.err.println( "ERROR: ServerX_CLMPAPI: Run: Lanzador de Processor " );
							ee.printStackTrace();
						}*/

						//MOTOR DE ALERTAS
						/*if( MotorAlertas != null )
						{
							MotorAlertas[ma].BufferEntrada.putElemento(PDU);
							ma++;

							if( ma == MotorAlertas.length )
								ma =0;
						}*/

					}
					/*
	    			if( DG !=null )
			   			DG.BufferEntradaSwitch.putElemento( PDU );

	    			if( EDDIERouter != null )
					{		
	    				if( PDU.getModemID().compareTo("") == 0 )
	    				{
	    					System.err.println("ServerX_CELLOAPI: Se pusó en EDDIERouter");
	    				}

	    				EDDIERouter[er].BufferEntrada.putElemento(PDU);
						er++;

						if( er == EDDIERouter.length )
							er = 0;
					}

	    			//---------------------------------------------------------------------------------------------------------
	    			if( Foward != null )
					{
						Foward[pf].BufferEntrada.putElemento(PDU);
						pf++;

						if( pf == Foward.length )
							pf = 0;
					}
	    			//---------------------------------------------------------------------------------------------------------

			   	}
	    		else if( PDU.isCMDResponse())
	    		{
	    			if( MotorCMDResponse != null )
					{
						MotorCMDResponse[ cr ].BufferEntrada.putElemento( PDU );
						cr++;

						if( cr == MotorCMDResponse.length )
							cr = 0;
					}*/		
				}

				PDU = null;

				//CONTEO DE PDUS RECIBIDOS Y ACKS ENVIADOS
				if( System.currentTimeMillis() - UltimoReporte > 60000 )
				{
					UltimoReporte = System.currentTimeMillis();
					System.err.println("MSG: ServerX_CELLOAPI " + Puerto + ": Run: PDU recibidos-> " + contPDUS + ": ACK Enviados: " + contACK );
					contPDUS=0;
					contACK=0;
				}

				try{
					sleep(50);
				}catch( Exception e)
				{
				}
			}
			catch( Exception e )
			{
				System.err.println( "EXCEPTION: ServerX_CELLOAPI: " + Puerto + ": Run: " + e.toString() );
			}
			catch( Throwable e)
			{
				System.err.println( "ERROR: ServerX_CELLOAPI: " + Puerto + ": Run: " );
				e.printStackTrace();
			}
		}	
	}

	public ServerX_CELLOAPI( PositionReportToDBEngine[] tmpMotorReportePosicionToDB , TaxiDataToDBEngine[] tmpMotorDatosTaxiToDB , AlertEngine[] tmpMotorAlertas ,  PDUtoEDDIERouter[] tmpEDDIERouter , OdometerEngine[] tmpMotorOdometro , CMDRSPToDBEngine[] tmpMotorCMDResponse ,  PDUFoward[] tmpFoward , int tmpPuerto , String tmpNombreServidor, boolean tmpACK, boolean tmptoTerminalPDU , AppProperty tmpAP , DataGateway tmpDataGateway , int tmpFowardType)
	{	

		InitServer( tmpMotorReportePosicionToDB , tmpMotorDatosTaxiToDB , tmpMotorAlertas , tmpEDDIERouter , tmpMotorOdometro , tmpMotorCMDResponse , tmpFoward , tmpPuerto , tmpNombreServidor , 100 , tmpACK,  tmptoTerminalPDU, tmpAP , tmpDataGateway ,  tmpFowardType );
	}

	private void InitServer( PositionReportToDBEngine[] tmpMotorReportePosicionToDB , TaxiDataToDBEngine[] tmpMotorDatosTaxiToDB , AlertEngine[] tmpMotorAlertas  , PDUtoEDDIERouter[] tmpEDDIERouter , OdometerEngine[] tmpMotorOdometro , CMDRSPToDBEngine[] tmpMotorCMDResponse ,  PDUFoward[] tmpFoward ,int tmpPuerto , String tmpNombreServidor , int tmpTamanoBDRegistro, boolean tmpACK, boolean tmptoTerminalPDU, AppProperty tmpAP, DataGateway tmpDataGateway ,  int tmpFowardType)
	{
		try{



			this.setName( this.getName() + ": ServerX_CELLOAPI");

			DG = tmpDataGateway;

			MotorAlertas = tmpMotorAlertas;
			MotorReportePosicionToDB = tmpMotorReportePosicionToDB;
			MotorDatosTaxiToDB = tmpMotorDatosTaxiToDB;
			MotorOdometro = tmpMotorOdometro;
			MotorCMDResponse = tmpMotorCMDResponse;


			Foward = tmpFoward;
			FowardType = tmpFowardType;





			EDDIERouter = tmpEDDIERouter;

			AP = tmpAP;
			ACKNOWLEDGE = tmpACK;
			toTerminalPDU = tmptoTerminalPDU;
			NombreServidor = tmpNombreServidor;
			Puerto = tmpPuerto;



			if( InicializarServer() == 0 )
				Running = true;

			if( AP.getProperty("IPServidor") != null )
				IPServidor = AP.getProperty("IPServidor");

			if( IPServidor != null )
				System.err.println("IP SERVIDOR:  " + IPServidor );




			if( AP.getProperty("MostrarUDPGpsRecibido")!=null && AP.getProperty("MostrarUDPGpsRecibido").compareTo("true") == 0)
				MostrarUDPGpsRecibido=true;
			else
				MostrarUDPGpsRecibido=false;




			if( AP.getProperty("GuardarEnGeosysDatosNoRuteados")!= null && AP.getProperty("GuardarEnGeosysDatosNoRuteados").compareTo("si" ) == 0 )
				GuardarEnGeosysDatosNoRuteados=true;
			else
				GuardarEnGeosysDatosNoRuteados=false;

			if( AP.getProperty("GuardarLocalmenteDatosNoRuteados")!= null && AP.getProperty("GuardarLocalmenteDatosNoRuteados").compareTo("si" ) == 0 )
				GuardarLocalmenteDatosNoRuteados=true;
			else
				GuardarLocalmenteDatosNoRuteados=false;

		}catch( Exception e )
		{
			System.err.println( "ERROR: ServerX_CLMPAPI: InitServer: " + e.toString() );
		}
		this.start();
	}

}