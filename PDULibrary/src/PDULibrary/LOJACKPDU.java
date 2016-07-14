package PDULibrary;

import java.net.*;




public class LOJACKPDU 
{
	public String S_PDU;
	String CodigoRespuesta,CodigoActivacion,Box;
	int IntensidadSeñal=0, Direccion=0;
	String[] Segmentos;
	
	long FechaHora;
	//int CargaBateriaInterna=0, VoltajeEntrada=0;
	String V1,V2,V3,V4,V5;

	String DireccionOrigen,PuertoOrigen;
	
	
	boolean Señal=false;
	boolean Comando=false;
	boolean RFCheck=false;
	int Tipo;
	
	public long obtenerFechaHora()
	{
		return FechaHora;
	}
	
	public synchronized String obtenerDireccionOrigen()
	{
		return DireccionOrigen;
	}
	
	public synchronized String obtenerPuertoOrigen()
	{
		return PuertoOrigen;
	}
	
	public synchronized String toString()
	{
		return S_PDU;
	}
	
	/*
	 *
	 * SEÑALES
	 * 1 - VTREPLY
	 * 2 - VTREPLY (FILTRO)
	 * 3 -
	 * 4 -
	 * 5 - RF CHECK RESPONSE 
	 *
	 * COMANDOS
	 * 
	 * 10 - ACTIVACION ACTNO
	 * 11 - DESACTIVACION (DEANO)
	 * 12 - ACTIVACION FILTRO 
	 * 13 - TRACK
	 * 14 - TRACK FILTRO
	 * 15 - RF CHECK CMD
	 * 
	 * 
	 */
		
	public synchronized int obtenerIntensidadSeñal()
	{
		return IntensidadSeñal;
	}
	
	public synchronized String obtenerV1()
	{
		return V1;
	}
	
	public synchronized String obtenerV2()
	{
		return V2;
	}
	
	public synchronized String obtenerV3()
	{
		return V3;
	}
	
	public synchronized String obtenerV4()
	{
		return V4;
	}
	
	public synchronized String obtenerV5()
	{
		return V5;
	}
	
	public synchronized int obtenerDireccion()
	{
		return Direccion;
	}
	
	public  synchronized String obtenerCodigoRespuesta()
	{
		return CodigoRespuesta;
	}
	
	public  synchronized String ObtenerCodigoActivacion()
	{
		return CodigoActivacion;
	}
	
	public  synchronized boolean esSeñal( )
	{
		return Señal;
		
	}
	
	public  synchronized boolean esComando( )
	{
		return Comando;
		
	}
	
	public  synchronized boolean esRFCHECK( )
	{
		return RFCheck;
		
	}
	
	public  synchronized int obtenerTipo()
	{
		return Tipo;
	}
	
	public  synchronized String[] obtenerSegmentos()
	{
		return Segmentos;
	}
	
	public LOJACKPDU( DatagramPacket tmpPaquete )
	{
		DireccionOrigen = tmpPaquete.getAddress().toString();
		PuertoOrigen = "" + tmpPaquete.getPort();
		byte[] tmpP = new byte[ tmpPaquete.getLength() ];
		
		System.arraycopy(  tmpPaquete.getData() , 0, tmpP , 0, tmpP.length );
		
		ConstruirClase( new String( tmpP ) );
	}
	
	public LOJACKPDU( String tmp_S_PDU)
	{
		ConstruirClase( tmp_S_PDU );
	}
	
	private  synchronized void ConstruirClase( String tmp_S_PDU )
	{
		try{
			FechaHora = System.currentTimeMillis();
			
			S_PDU = tmp_S_PDU.trim();
			
			//S_PDU = S_PDU.trim();
			
			Tipo=-1;
			
			if( S_PDU.indexOf( "VTREPLY") >= 0 )
			{
				Segmentos = ( S_PDU.substring(S_PDU.indexOf( "VTREPLY") )).split( " " );
				
				if( Segmentos.length == 11 )
				{
					if( Segmentos[2].length() >= 7)
					{	
						CodigoRespuesta = Segmentos[2].substring(1,6);
						IntensidadSeñal = Integer.parseInt( Segmentos[10].trim());
						Direccion=0;
					}
					Señal = true;
					Tipo=1;
					
				} 
				else if( S_PDU.indexOf( "VTREPLY" , S_PDU.indexOf( "VTREPLY") + 1 ) >= 0)
				{
					Segmentos = ( S_PDU.substring( S_PDU.indexOf( "VTREPLY",S_PDU.indexOf( "VTREPLY") + 1 ) )).split( " " );
					
					if( Segmentos.length == 11 )
					{
						if( Segmentos[2].length() >= 7)
						{	
							CodigoRespuesta = Segmentos[2].substring(1,6);
							IntensidadSeñal = Integer.parseInt( Segmentos[10].trim());
							Direccion=0;
						}
						Señal = true;
						Tipo=1;
						
					} 
				}
			}
			else if( S_PDU.indexOf("TRACK") >= 0 )
			{	
				Segmentos = ( S_PDU.substring(S_PDU.indexOf( "TRACK") )).split( " " );
				CodigoRespuesta = Segmentos[5].substring( 1 , Segmentos[5].length() - 1 );
				IntensidadSeñal = Integer.parseInt( Segmentos[10].trim());
				Comando=true;
				Tipo=13;
				
			}
			else if( S_PDU.indexOf("ACTIV") >= 0 )
			{	
				Segmentos = ( S_PDU.substring(S_PDU.indexOf( "ACTIV") )).split( " " );
				CodigoActivacion = Segmentos[7];
				IntensidadSeñal = Integer.parseInt( Segmentos[12].trim());
				Comando=true;
				Tipo=10;
				
			}
			else if( S_PDU.indexOf("DEACT") >= 0 )
			{	
				Segmentos = ( S_PDU.substring(S_PDU.indexOf( "DEACT") )).split( " " );
				CodigoActivacion = Segmentos[7];
				IntensidadSeñal = Integer.parseInt( Segmentos[12].trim());
				Comando=true;
				Tipo=11;
				
			}
			else if( S_PDU.indexOf( "RF CHK" ) >= 0 )
			{
				Segmentos = (S_PDU.substring( S_PDU.indexOf( "RF CHK") )).split( " " );

				if( Segmentos[3].indexOf("CMD") >= 0)
				{
					V1 = Segmentos[5];
					V2 = Segmentos[6];
					V3 = Segmentos[7];
					V4 = Segmentos[8];
					V5 = Segmentos[9];
					IntensidadSeñal = Integer.parseInt( Segmentos[11].trim());
					Tipo = 15;
				}
				else if( Segmentos[3].indexOf("RESP") >= 0)
				{	
					V1 = Segmentos[4];
					V2 = Segmentos[5];
					V3 = Segmentos[6];
					V4 = Segmentos[7];
					V5 = Segmentos[8];
			
					IntensidadSeñal = Integer.parseInt( Segmentos[10].trim());
					Tipo=5;
					
				}
								
				RFCheck = true;
			}
			
			else
			{
				Segmentos = S_PDU.trim().split( " " );
				
				if( Segmentos.length == 1 )
				{
					if( Segmentos[0].charAt(0) == 'v' && Segmentos[0].length() == 9 )
					{
						CodigoRespuesta = Segmentos[0].substring(1,6);
						IntensidadSeñal = Integer.parseInt(  Segmentos[0].substring(6,9));
						Direccion=0;
						Señal = true;
						Tipo=2;
					}
					else if( Segmentos[0].charAt(0) == 'a' && Segmentos[0].length() == 11 )
					{
						CodigoActivacion = Segmentos[0].substring(1,8);
						IntensidadSeñal = Integer.parseInt(  Segmentos[0].substring(8) );
						Direccion=0;
						Comando = true;
						Tipo=10;
					}
					else if( Segmentos[0].charAt(0) == 't' && Segmentos[0].length() == 9 )
					{
						CodigoRespuesta = Segmentos[0].substring(1,6);
						IntensidadSeñal = Integer.parseInt(  Segmentos[0].substring(6,9));
						Direccion=0;
						Comando = true;
						Tipo=14;
					}
				}
				else if( Segmentos.length == 3 )
				{
					CodigoRespuesta = Segmentos[1];
					IntensidadSeñal = Integer.parseInt(  Segmentos[2].trim());
					Direccion = 0;
					Señal = true;
					Tipo=3;
				}
				else if( Segmentos.length == 4 )
				{	
					CodigoRespuesta = Segmentos[0];
					IntensidadSeñal = Integer.parseInt(  Segmentos[2]);
					Direccion = Integer.parseInt(  Segmentos[3].trim() );
					Señal = true;
					Tipo=4;
				}
				else if( Segmentos.length == 5 )
				{	
					if( Segmentos[0] == null || Segmentos[0].length() == 0 || ( Segmentos[0].charAt(0) != 't' && Segmentos[0].charAt(0) != 'T') )
					{
						CodigoRespuesta = Segmentos[1];
						IntensidadSeñal = Integer.parseInt(  Segmentos[3] );
						Direccion = Integer.parseInt(  Segmentos[4].trim() );
						Señal = true;
						Tipo=4;
					}
					
				}
			}
		}catch( Exception e )
		{
			Señal = false;
			Tipo=-1;
			
		}
	}

}
