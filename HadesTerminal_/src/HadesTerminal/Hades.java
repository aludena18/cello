package HadesTerminal;

import PDULibrary.AppProperty;

public class Hades
{
	public static AppProperty AP;
	
	
	public static void main( String[] args )
	{
		try{
			AP = new AppProperty();
					
			System.err.println( "HadesTerminal v20.0.0");
			System.err.println( "2011-04-25 CARSEG S.A.");
			System.err.println( "Todos los derechos reservados");
			
			if( AP.getProperty("Pais").compareTo("EC") == 0 )
			{
				System.err.println( "Cargando perfil para pais Ecuador....");
				System.err.println( "Cargando controladores.....");
				System.err.println( "Tiempo aproximado para iniciar: 5 segundos....");
			
				try{
					Thread.sleep(5000);
				}catch( Exception e2)
				{
				}
				
				//PARA TERMINAL
				ServerUDPTerminal Srv22222;
				
				Srv22222 = new ServerUDPTerminal(22222, "UDP TERMINAL SERVER 22222" , AP);
				for(int i=0;i<20;i++)
				{	
					if( Srv22222.getUDPServer() != null )
					{	new ProcessorTerminalCMD( Srv22222.getUDPServer(),AP);
						break;
					}
					else
					{
						try{
							Thread.sleep( 10000 );
						}catch( Exception e2)
						{
							
						}
					}
				}
							
			}
			else if( AP.getProperty("Pais").compareTo("PE") == 0 )
			{
				System.err.println( "Cargando perfil para pais Perú....");
				System.err.println( "Cargando controladores.....");
				System.err.println( "Tiempo aproximado para iniciar: 5 segundos....");
				
				try{
					Thread.sleep( 5000 );
				}catch( Exception e2)
				{
				}
				
				try{
					Thread.sleep( 5000 );
				}catch( Exception e2)
				{
				}
								
				//PARA TERMINAL
				ServerUDPTerminal Srv22222;
				Srv22222 = new ServerUDPTerminal(22222, "UDP TERMINAL SERVER 22222" , AP);
				
				
				for(int i=0;i<20;i++)
				{	
					if( Srv22222.getUDPServer() != null )
					{	new ProcessorTerminalCMD( Srv22222.getUDPServer(),AP);
						break;
					}
					else
					{
						try{
							Thread.sleep( 10000 );
						}catch( Exception e2)
						{
							
						}
					}
				}
				
			}
		}catch( Exception e )
		{
			System.err.println("ERROR: HADES: No se ejecuto correctamente Hades" );
			System.err.println( e.toString() );
			System.exit(-1);
		}
			
	}

}
