package HadesCELLOAPI;

import DataGateway.DataGateway;
import PDULibrary.AppProperty;
import ProcessorLibrary.AlertEngine;
import ProcessorLibrary.CMDRSPToDBEngine;
import ProcessorLibrary.OdometerEngine;
import ProcessorLibrary.PDUFoward;
import ProcessorLibrary.PDUtoEDDIERouter;
import ProcessorLibrary.PositionReportToDBEngine;
import ProcessorLibrary.TaxiDataToDBEngine;

public class Hades
{
	public static AppProperty AP;
		
	//public ServerUDPAPICMD SERVERUDPAPICMD;

	@SuppressWarnings("unused")
	public static void main( String[] args )
	{
		try{
			AP = new AppProperty();
			DataGateway DG=null;	
			
			System.err.println( "Hades Cellocator v20.0.0 RC1");
			System.err.println( "2015-11-09 AUTOSAFE S.A.C");
			System.err.println( "Todos los derechos reservados");
			System.err.println( "Area de Investigación y Desarrollo");
			
			PositionReportToDBEngine[] MotorReportePosicionToDB;
			TaxiDataToDBEngine[] MotorDatosTaxiToDB=null;
			AlertEngine[] MotorAlertas;
			OdometerEngine[] MotorOdometro;
			PDUtoEDDIERouter[] EDDIERouter= null;
			CMDRSPToDBEngine[] MotorCMDResponse;
			
			PDUFoward[] Foward31001 = new PDUFoward[1]; 
			PDUFoward[] Foward31002 = new PDUFoward[1];
			PDUFoward[] Foward31003 = new PDUFoward[1]; 
			PDUFoward[] Foward31004 = new PDUFoward[1];
			PDUFoward[] Foward31005 = new PDUFoward[1]; 
			PDUFoward[] Foward31006 = new PDUFoward[1];
			PDUFoward[] Foward31007 = new PDUFoward[1]; 
			PDUFoward[] Foward31008 = new PDUFoward[1];
			PDUFoward[] Foward31009 = new PDUFoward[1]; 
			PDUFoward[] Foward31000 = new PDUFoward[1];
			
			
			PDUFoward[] Foward32001 = new PDUFoward[1]; 
			PDUFoward[] Foward32002 = new PDUFoward[1];
			PDUFoward[] Foward32003 = new PDUFoward[1]; 
			PDUFoward[] Foward32004 = new PDUFoward[1];
			PDUFoward[] Foward32005 = new PDUFoward[1]; 
			PDUFoward[] Foward32006 = new PDUFoward[1];
			PDUFoward[] Foward32007 = new PDUFoward[1]; 
			PDUFoward[] Foward32008 = new PDUFoward[1];
			PDUFoward[] Foward32009 = new PDUFoward[1]; 
			PDUFoward[] Foward32000 = new PDUFoward[1];
			/*
			boolean DataGatewayAutenticar=true;
			
			try{
				if( AP.getProperty( "DataGatewayUDPAPIAutenticar") != null && AP.getProperty( "DataGatewayUDPAPIAutenticar").compareTo("true") == 0 )
				{ 	
					DataGatewayAutenticar=true;
					System.err.println( "DataGatewayUDPAPI se cargará con parametro de Autenticar en TRUE" );
				}
				else
				{
					System.err.println( "DataGatewayUDPAPI se cargará con parametro de Autenticar en FALSE" );
				}
			}catch( Exception e)
			{
				System.err.println("ERROR: Carga de parametro DataGatewayXVMAutenticar: " + e.toString() );
				System.err.println( "DataGatewayUDPAPI se cargará con parametro de Autenticar en FALSE" );
			}
			
			DG = new DataGateway(62000,DataGatewayAutenticar,AP);
			
			*/
			if( AP.getProperty("Pais").compareTo("EC") == 0 )
			{
				System.err.println( "Cargando perfil para pais Ecuador....");
				System.err.println( "Cargando controladores.....");
				System.err.println( "Tiempo aproximado para iniciar: 5 segundos....");
			
				EDDIERouter = new PDUtoEDDIERouter[120];
				MotorReportePosicionToDB =  new PositionReportToDBEngine[200];
				//MotorDatosTaxiToDB = null; //new TaxiDataToDBEngine[20];
				MotorAlertas = new AlertEngine[40];
				//MotorAlertasByProduct = new AlertEngineByProduct[40];
				MotorOdometro = new OdometerEngine[10];
				MotorCMDResponse = new CMDRSPToDBEngine[10];
											
				for( int i=0; i < MotorReportePosicionToDB.length; i++)
					MotorReportePosicionToDB[i] = new PositionReportToDBEngine( AP );
				
				/*
				for( int i=0; i < MotorDatosTaxiToDB.length; i++)
					MotorDatosTaxiToDB[i] = new TaxiDataToDBEngine( AP );
				*/
				
				
				
				for( int i=0; i < MotorAlertas.length; i++)
					MotorAlertas[i] = new AlertEngine( AP );

				/*
				for( int i=0; i < MotorAlertasByProduct.length; i++)
					MotorAlertasByProduct[i] = new AlertEngineByProduct( AP );
				*/
								
				for( int i=0; i < MotorOdometro.length; i++)
					MotorOdometro[i] = new OdometerEngine( AP );
				
				for( int i=0; i < EDDIERouter.length; i++)
					EDDIERouter[i] = new PDUtoEDDIERouter( AP );
				
				for( int i=0; i < MotorCMDResponse.length; i++)
					MotorCMDResponse[i] = new CMDRSPToDBEngine( AP );
				
				
				try{
					Thread.sleep(5000);
				}catch( Exception e2)
				{
				}
															
				
				//PUERTOS GEOSYS
				new ServerX_CELLOAPI( MotorReportePosicionToDB , MotorDatosTaxiToDB , MotorAlertas , EDDIERouter , MotorOdometro , MotorCMDResponse , Foward31000 , 31000, "CLMPSERVER X 31000" , true , false, AP , DG , 1);
				new ServerX_CELLOAPI( MotorReportePosicionToDB , MotorDatosTaxiToDB , MotorAlertas , EDDIERouter , MotorOdometro , MotorCMDResponse , Foward31001 , 31001, "CLMPSERVER X 31001" , true , false, AP , DG , 1);
				new ServerX_CELLOAPI( MotorReportePosicionToDB , MotorDatosTaxiToDB , MotorAlertas , EDDIERouter , MotorOdometro , MotorCMDResponse , Foward31002 , 31002, "CLMPSERVER X 31002" , true , false, AP , DG , 1);
				new ServerX_CELLOAPI( MotorReportePosicionToDB , MotorDatosTaxiToDB , MotorAlertas , EDDIERouter , MotorOdometro , MotorCMDResponse , Foward31003 , 31003, "CLMPSERVER X 31003" , true , false, AP , DG , 1);
				new ServerX_CELLOAPI( MotorReportePosicionToDB , MotorDatosTaxiToDB , MotorAlertas , EDDIERouter , MotorOdometro , MotorCMDResponse , Foward31004 , 31004, "CLMPSERVER X 31004" , true , false, AP , DG , 1);
				new ServerX_CELLOAPI( MotorReportePosicionToDB , MotorDatosTaxiToDB , MotorAlertas , EDDIERouter , MotorOdometro , MotorCMDResponse , Foward31005 , 31005, "CLMPSERVER X 31005" , true , false, AP , DG , 1);
				new ServerX_CELLOAPI( MotorReportePosicionToDB , MotorDatosTaxiToDB , MotorAlertas , EDDIERouter , MotorOdometro , MotorCMDResponse , Foward31006 , 31006, "CLMPSERVER X 31006" , true , false, AP , DG , 1);
				new ServerX_CELLOAPI( MotorReportePosicionToDB , MotorDatosTaxiToDB , MotorAlertas , EDDIERouter , MotorOdometro , MotorCMDResponse , Foward31007 , 31007, "CLMPSERVER X 31007" , true , false, AP , DG , 1);
				new ServerX_CELLOAPI( MotorReportePosicionToDB , MotorDatosTaxiToDB , MotorAlertas , EDDIERouter , MotorOdometro , MotorCMDResponse , Foward31008 , 31008, "CLMPSERVER X 31008" , true , false, AP , DG , 1);
				new ServerX_CELLOAPI( MotorReportePosicionToDB , MotorDatosTaxiToDB , MotorAlertas , EDDIERouter , MotorOdometro , MotorCMDResponse , Foward31009 , 31009, "CLMPSERVER X 31009" , true , false, AP , DG , 1);
				
				new ServerX_CELLOAPI( MotorReportePosicionToDB , MotorDatosTaxiToDB , MotorAlertas , EDDIERouter , MotorOdometro , MotorCMDResponse , Foward32000 , 32000, "CLMPSERVER X 32000" , true , false, AP , DG , 2);
				new ServerX_CELLOAPI( MotorReportePosicionToDB , MotorDatosTaxiToDB , MotorAlertas , EDDIERouter , MotorOdometro , MotorCMDResponse , Foward32001 , 32001, "CLMPSERVER X 32001" , true , false, AP , DG , 2);
				new ServerX_CELLOAPI( MotorReportePosicionToDB , MotorDatosTaxiToDB , MotorAlertas , EDDIERouter , MotorOdometro , MotorCMDResponse , Foward32002 , 32002, "CLMPSERVER X 32002" , true , false, AP , DG , 2);
				new ServerX_CELLOAPI( MotorReportePosicionToDB , MotorDatosTaxiToDB , MotorAlertas , EDDIERouter , MotorOdometro , MotorCMDResponse , Foward32003 , 32003, "CLMPSERVER X 32003" , true , false, AP , DG , 2);
				new ServerX_CELLOAPI( MotorReportePosicionToDB , MotorDatosTaxiToDB , MotorAlertas , EDDIERouter , MotorOdometro , MotorCMDResponse , Foward32004 , 32004, "CLMPSERVER X 32004" , true , false, AP , DG , 2);
				new ServerX_CELLOAPI( MotorReportePosicionToDB , MotorDatosTaxiToDB , MotorAlertas , EDDIERouter , MotorOdometro , MotorCMDResponse , Foward32005 , 32005, "CLMPSERVER X 32005" , true , false, AP , DG , 2);
				new ServerX_CELLOAPI( MotorReportePosicionToDB , MotorDatosTaxiToDB , MotorAlertas , EDDIERouter , MotorOdometro , MotorCMDResponse , Foward32006 , 32006, "CLMPSERVER X 32006" , true , false, AP , DG , 2);
				new ServerX_CELLOAPI( MotorReportePosicionToDB , MotorDatosTaxiToDB , MotorAlertas , EDDIERouter , MotorOdometro , MotorCMDResponse , Foward32007 , 32007, "CLMPSERVER X 32007" , true , false, AP , DG , 2);
				new ServerX_CELLOAPI( MotorReportePosicionToDB , MotorDatosTaxiToDB , MotorAlertas , EDDIERouter , MotorOdometro , MotorCMDResponse , Foward32008 , 32008, "CLMPSERVER X 32008" , true , false, AP , DG , 2);
				new ServerX_CELLOAPI( MotorReportePosicionToDB , MotorDatosTaxiToDB , MotorAlertas , EDDIERouter , MotorOdometro , MotorCMDResponse , Foward32009 , 32009, "CLMPSERVER X 32009" , true , false, AP , DG , 2);
			}
			else if( AP.getProperty("Pais").compareTo("PE") == 0 )
			{
				System.err.println( "Cargando perfil para pais Peru....");
				System.err.println( "Cargando controladores.....");
				System.err.println( "Tiempo aproximado para iniciar: 5 segundos....");
			
				EDDIERouter = null;
				MotorReportePosicionToDB =  new PositionReportToDBEngine[200];
				//MotorDatosTaxiToDB = null; //new TaxiDataToDBEngine[20];
				MotorAlertas = new AlertEngine[40];
				//MotorAlertasByProduct = new AlertEngineByProduct[40];
				MotorOdometro = new OdometerEngine[10];
				MotorCMDResponse = new CMDRSPToDBEngine[10];
											
				for( int i=0; i < MotorReportePosicionToDB.length; i++)
					MotorReportePosicionToDB[i] = new PositionReportToDBEngine( AP );
				
				/*
				for( int i=0; i < MotorDatosTaxiToDB.length; i++)
					MotorDatosTaxiToDB[i] = new TaxiDataToDBEngine( AP );
				*/
				
				
				
				for( int i=0; i < MotorAlertas.length; i++)
					MotorAlertas[i] = new AlertEngine( AP );

				/*
				for( int i=0; i < MotorAlertasByProduct.length; i++)
					MotorAlertasByProduct[i] = new AlertEngineByProduct( AP );
				*/
								
				for( int i=0; i < MotorOdometro.length; i++)
					MotorOdometro[i] = new OdometerEngine( AP );
				
				if( EDDIERouter != null)
				{
					for( int i=0; i < EDDIERouter.length; i++)
						EDDIERouter[i] = new PDUtoEDDIERouter( AP );
				}
					
				
				for( int i=0; i < MotorCMDResponse.length; i++)
					MotorCMDResponse[i] = new CMDRSPToDBEngine( AP );
				
				
				try{
					Thread.sleep(5000);
				}catch( Exception e2)
				{
				}
															
				
				//PUERTOS GEOSYS
				new ServerX_CELLOAPI( MotorReportePosicionToDB , MotorDatosTaxiToDB , MotorAlertas , EDDIERouter , MotorOdometro , MotorCMDResponse , null , 41000, "CLMPSERVER X 41000" , true , false, AP , DG , 1);
				new ServerX_CELLOAPI( MotorReportePosicionToDB , MotorDatosTaxiToDB , MotorAlertas , EDDIERouter , MotorOdometro , MotorCMDResponse , null , 42000, "CLMPSERVER X 31000" , true , false, AP , DG , 1);
				/*new ServerX_CELLOAPI( MotorReportePosicionToDB , MotorDatosTaxiToDB , MotorAlertas , EDDIERouter , MotorOdometro , MotorCMDResponse , null , 31001, "CLMPSERVER X 31001" , true , false, AP , DG , 1);
				new ServerX_CELLOAPI( MotorReportePosicionToDB , MotorDatosTaxiToDB , MotorAlertas , EDDIERouter , MotorOdometro , MotorCMDResponse , null , 31002, "CLMPSERVER X 31002" , true , false, AP , DG , 1);
				new ServerX_CELLOAPI( MotorReportePosicionToDB , MotorDatosTaxiToDB , MotorAlertas , EDDIERouter , MotorOdometro , MotorCMDResponse , null , 31003, "CLMPSERVER X 31003" , true , false, AP , DG , 1);
				new ServerX_CELLOAPI( MotorReportePosicionToDB , MotorDatosTaxiToDB , MotorAlertas , EDDIERouter , MotorOdometro , MotorCMDResponse , null , 31004, "CLMPSERVER X 31004" , true , false, AP , DG , 1);
				new ServerX_CELLOAPI( MotorReportePosicionToDB , MotorDatosTaxiToDB , MotorAlertas , EDDIERouter , MotorOdometro , MotorCMDResponse , null , 31005, "CLMPSERVER X 31005" , true , false, AP , DG , 1);
				new ServerX_CELLOAPI( MotorReportePosicionToDB , MotorDatosTaxiToDB , MotorAlertas , EDDIERouter , MotorOdometro , MotorCMDResponse , null , 31006, "CLMPSERVER X 31006" , true , false, AP , DG , 1);
				new ServerX_CELLOAPI( MotorReportePosicionToDB , MotorDatosTaxiToDB , MotorAlertas , EDDIERouter , MotorOdometro , MotorCMDResponse , null , 31007, "CLMPSERVER X 31007" , true , false, AP , DG , 1);
				new ServerX_CELLOAPI( MotorReportePosicionToDB , MotorDatosTaxiToDB , MotorAlertas , EDDIERouter , MotorOdometro , MotorCMDResponse , null , 31008, "CLMPSERVER X 31008" , true , false, AP , DG , 1);
				new ServerX_CELLOAPI( MotorReportePosicionToDB , MotorDatosTaxiToDB , MotorAlertas , EDDIERouter , MotorOdometro , MotorCMDResponse , null , 31009, "CLMPSERVER X 31009" , true , false, AP , DG , 1);
				
				new ServerX_CELLOAPI( MotorReportePosicionToDB , MotorDatosTaxiToDB , MotorAlertas , EDDIERouter , MotorOdometro , MotorCMDResponse , Foward32000 , 32000, "CLMPSERVER X 32000" , true , false, AP , DG , 2);
				new ServerX_CELLOAPI( MotorReportePosicionToDB , MotorDatosTaxiToDB , MotorAlertas , EDDIERouter , MotorOdometro , MotorCMDResponse , Foward32001 , 32001, "CLMPSERVER X 32001" , true , false, AP , DG , 2);
				new ServerX_CELLOAPI( MotorReportePosicionToDB , MotorDatosTaxiToDB , MotorAlertas , EDDIERouter , MotorOdometro , MotorCMDResponse , Foward32002 , 32002, "CLMPSERVER X 32002" , true , false, AP , DG , 2);
				new ServerX_CELLOAPI( MotorReportePosicionToDB , MotorDatosTaxiToDB , MotorAlertas , EDDIERouter , MotorOdometro , MotorCMDResponse , Foward32003 , 32003, "CLMPSERVER X 32003" , true , false, AP , DG , 2);
				new ServerX_CELLOAPI( MotorReportePosicionToDB , MotorDatosTaxiToDB , MotorAlertas , EDDIERouter , MotorOdometro , MotorCMDResponse , Foward32004 , 32004, "CLMPSERVER X 32004" , true , false, AP , DG , 2);
				new ServerX_CELLOAPI( MotorReportePosicionToDB , MotorDatosTaxiToDB , MotorAlertas , EDDIERouter , MotorOdometro , MotorCMDResponse , Foward32005 , 32005, "CLMPSERVER X 32005" , true , false, AP , DG , 2);
				new ServerX_CELLOAPI( MotorReportePosicionToDB , MotorDatosTaxiToDB , MotorAlertas , EDDIERouter , MotorOdometro , MotorCMDResponse , Foward32006 , 32006, "CLMPSERVER X 32006" , true , false, AP , DG , 2);
				new ServerX_CELLOAPI( MotorReportePosicionToDB , MotorDatosTaxiToDB , MotorAlertas , EDDIERouter , MotorOdometro , MotorCMDResponse , Foward32007 , 32007, "CLMPSERVER X 32007" , true , false, AP , DG , 2);
				new ServerX_CELLOAPI( MotorReportePosicionToDB , MotorDatosTaxiToDB , MotorAlertas , EDDIERouter , MotorOdometro , MotorCMDResponse , Foward32008 , 32008, "CLMPSERVER X 32008" , true , false, AP , DG , 2);
				new ServerX_CELLOAPI( MotorReportePosicionToDB , MotorDatosTaxiToDB , MotorAlertas , EDDIERouter , MotorOdometro , MotorCMDResponse , Foward32009 , 32009, "CLMPSERVER X 32009" , true , false, AP , DG , 2);
			*/
			}
					
		}catch( Exception e )
		{
			System.err.println("ERROR: HADES: No se ejecuto correctamente Hades" );
			System.err.println( e.toString() );
			System.exit(-1);
		}
			
	}

}
