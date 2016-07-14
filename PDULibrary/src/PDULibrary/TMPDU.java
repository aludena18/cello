
package PDULibrary;

import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class TMPDU implements PDU
{	
	//PROPIEDADES
	//DatagramPacket DG_PDU;
	
	private byte[] B_PDU;
	String Bin="",Hex="",S_PDU="";
	String[] SA_PDU,SA0;
	
	private String Id = "";
	private String [] Parametros;
	
	int Type=0,SubType=0;
	public String IP=null,IPDestino="/0.0.0.0";
	public int Puerto,PuertoDestino=0;
	
	long Time;
	
	boolean DebugERROR,DebugTRAN;
	
	public int getIdProtocolo()
	{
		return 11;
	}
	
	public synchronized String getReportDateTime( int gmt )
	{
		try{
			String dt,d,t;
			Calendar C;
			
			
			C = new GregorianCalendar();
			
			
			if( getGPSStatus().compareTo("1") == 0 )
			{
				d = this.getGPSDate();
				t = this.getGPSTime();
				C.set( Integer.parseInt( "20" + d.substring( 4,6) ), Integer.parseInt( d.substring(2,4))-1, Integer.parseInt( d.substring(0,2)), Integer.parseInt( t.substring(0,2)), Integer.parseInt( t.substring(2,4)), Integer.parseInt( t.substring(4,6)));
				
			}
			else if( getGPSStatus().compareTo("1") != 0 && getType() == 1 )
			{
				d = this.getRTC();
				t = this.getRTC();
				C.set( Integer.parseInt( d.substring( 0,4) ), Integer.parseInt( d.substring(4,6))-1, Integer.parseInt( d.substring(6,8)), Integer.parseInt( t.substring(8,10)), Integer.parseInt( t.substring(10,12)), Integer.parseInt( t.substring(12,14)));
				
			}
			else
			{
				d = this.getGPSDate();
				t = this.getGPSTime();
				C.set( Integer.parseInt( "20" + d.substring( 4,6) ), Integer.parseInt( d.substring(2,4))-1, Integer.parseInt( d.substring(0,2)), Integer.parseInt( t.substring(0,2)), Integer.parseInt( t.substring(2,4)), Integer.parseInt( t.substring(4,6)));
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss");
			C.add(Calendar.HOUR_OF_DAY, gmt );
			dt = sdf.format( C.getTime());
			
			return dt;
		}catch( Exception e)
		{
			System.err.println("ERROR: TTPDU: ReportDateTime:" + e.toString());
			
			return null;
		}
	}
	
	public synchronized String getReportDateTime()
	{
		try{
			/*
			return "20" + getGPSDate().substring(4,6) + getGPSDate().substring(2, 4) + getGPSDate().substring(0,2) +
					" " + getGPSTime().substring(0,2) + ":" + getGPSTime().substring(2,4) + ":" + getGPSTime().substring(4,6); 
			*/
			
			if( getGPSStatus().compareTo("1") == 0 )
			{
				return "20" + getGPSDate().substring(4,6) + getGPSDate().substring(2, 4) + getGPSDate().substring(0,2) +
					" " + getGPSTime().substring(0,2) + ":" + getGPSTime().substring(2,4) + ":" + getGPSTime().substring(4,6); 
			}
			else if( getGPSStatus().compareTo("1") != 0 && getType() == 1 )
			{
				return getRTC().substring(0,4) + getRTC().substring( 4,6 ) + getRTC().substring( 6,8 ) +
					" " + getRTC().substring(8, 10) + ":" + getRTC().substring(10, 12) + ":" + getRTC().substring(12,14); 
			}
			else
				return "20" + getGPSDate().substring(4,6) + getGPSDate().substring(2, 4) + getGPSDate().substring(0,2) +
				" " + getGPSTime().substring(0,2) + ":" + getGPSTime().substring(2,4) + ":" + getGPSTime().substring(4,6);
				
		
			
			
			
		}catch( Exception e)
		{
			System.err.println("ERROR: TTPDU: ReportDateTime:" + e.toString());
			
			return null;
		}
	}
	
	public synchronized String getReportDateTimeRTC()
	{
		try{
			/*
			return "20" + getGPSDate().substring(4,6) + getGPSDate().substring(2, 4) + getGPSDate().substring(0,2) +
					" " + getGPSTime().substring(0,2) + ":" + getGPSTime().substring(2,4) + ":" + getGPSTime().substring(4,6); 
			*/
			
			if( Type == 1)
			{	
				return getRTC().substring(0,4) + "-" + getRTC().substring(6,8) + "-" + getRTC().substring( 4 , 6 ) +
				" " + getRTC().substring(8, 10) + ":" + getRTC().substring(10, 12) + ":" + getRTC().substring(12,14);
			}
			else if(Type == 2)
				return getReportDateTimeGPS();
			else 
				return null;
			
		}catch( Exception e)
		{
			System.err.println("ERROR: TTPDU: ReportDateTimeRTC: " + e.toString());
			return null;
		}
		
	}

	public synchronized String getReportDateTimeGPS()
	{
		try{
			if( Type == 1 || Type == 2 )
			{
				return "20" + getGPSDate().substring(4,6) + "-" + getGPSDate().substring(0, 2) + "-" + getGPSDate().substring(2,4) +
			
				" " + getGPSTime().substring(0,2) + ":" + getGPSTime().substring(2,4) + ":" + getGPSTime().substring(4,6);
			}
			else
				return null;
			
		}catch( Exception e)
		{
			System.err.println("ERROR: TTPDU: ReportDateTimeGPS: " + e.toString());
			return null;
		}
		
	}

	public synchronized byte[] getRAWDATA()
	{
		return B_PDU;
	}
	
	public synchronized String getIP()
	{
		return IP;
	}
	
	public synchronized int getPuerto()
	{
		return Puerto;
	}
	
	public synchronized String getIPDestino()
	{
		return IPDestino;
	}
	
	public synchronized int getPuertoDestino()
	{
		return PuertoDestino;
	}
	
	public synchronized String obtenerTemperatura1()
	{
		try{
			float valor;
			if( Type == 11 )
			{
				valor = (float)Integer.parseInt( new String( B_PDU ).substring(4,8) ); // Palabra[0].substring(4, 8) );
				valor = (float) (valor * 0.3);
				valor = valor - 273;
				return Float.toString(valor);
			}
			return null;
		}catch( Exception e)
		{
			System.err.println( "ERROR: TTAPI: obtenerTemperatura1: " + e.toString() );
			return null;
		}
	}
	
	public synchronized String obtenerTemperatura2()
	{
		try{
			float valor;
			if( Type == 11 )
			{
				valor = (float)Integer.parseInt( new String( B_PDU).substring(9));//Palabra[0].substring(9) );
				valor = (float) (valor * 0.3);
				valor = valor - 273;
				return Float.toString(valor);
			}
			return null;
		}catch( Exception e)
		{
			System.err.println( "ERROR:TTAPI: obtenerTemperatura2: " + e.toString() );
			return null;
		}
	}
	
	private void IdentifyType()
	{
		/*TIPO DE UDPAPI
		 * 0: DESCONOCIDO
		 * 1: GPS DATE IN BINARY FORMAT WITH RTC
		 * 2: GPS DATA IN BINARY FORMAT WITHOUT RTC
		 * 3: LBS DATA
		 * 4: ACK RESPONSE
		 * 11: DATO DE TEMPERATURA
		 * 12: CMD RESPONSE
		 * 13: ACK PING
		*/ 
		
		try{
			if( this.getCommandType().compareTo("2") == 0 && this.getUDPAPINumber().compareTo("5")==0  && ( B_PDU.length >= 75 ) )
			{
				Type = 1;
				SubType = B_PDU.length;
			}
			else if( this.getCommandType().compareTo("2") == 0 && this.getUDPAPINumber().compareTo("5")==0  &&  B_PDU.length == 63  )
			{
				Type = 2;
			}
			else if( this.getCommandType().compareTo("1") == 0 && this.getUDPAPINumber().compareTo("10")==0  &&  B_PDU.length == 4  )
				Type = 4;
			else if( this.getCommandType().compareTo("2") == 0 && this.getUDPAPINumber().compareTo("10")==0 )
				Type = 13;
			else if( this.getCommandType().compareTo("5") == 0 && this.getUDPAPINumber().compareTo("1")==0 )
				Type = 12;
			else if( B_PDU.length == 71 )
				Type = 3;
			else if( this.getCommandType().compareTo("2") == 0 && this.getUDPAPINumber().compareTo("8")==0  &&  B_PDU.length == 13 )
				Type = 11;
			else 
				Type = 0;
			
		}catch( Exception e)
		{
			System.err.println( "ERROR: TTAPI: IdentifyType: " + e.toString() );
			Type = 0;
		}
	}
	
	//CONSTRUCTORES
	public TMPDU( DatagramPacket tmpPDU, DatagramSocket UDPServer, String IPServidor )
	{
		try{
			
			if( IPServidor != null )
				IPDestino = IPServidor;
			else
				IPDestino = UDPServer.getLocalAddress().toString();
					
			PuertoDestino = UDPServer.getLocalPort();
			
		}catch( Exception ee)
		{
			
		}
		InitPDU( tmpPDU );
	}
	
	public TMPDU( DatagramPacket tmpPDU )
	{
		InitPDU( tmpPDU );
		
	}
		
	public TMPDU( byte[] tmpPDU )
	{
		try{
			Time = System.currentTimeMillis(); 
			
			B_PDU = tmpPDU;
			Hex = this.toHex();
			Bin = this.toBin();
		
			this.IdentifyType();
			
		}catch( Exception e )
		{
			System.err.println("ERROR: TTPDU: UDPAPIPDU: " + e.toString() );
		}
	}
	
	private void InitPDU( DatagramPacket tmpPDU )
	{
		try{
			
			Time = System.currentTimeMillis(); 
			
			byte[] tmpB_PDU;
			
			//DG_PDU = tmpPDU;
			
			tmpB_PDU = tmpPDU.getData();

			B_PDU = new byte[ tmpPDU.getLength() ];
						
			System.arraycopy(tmpB_PDU, 0, B_PDU , 0, tmpPDU.getLength() );
						
			Hex = this.toHex();
			Bin = this.toBin();
			
			try{
				
				IP = tmpPDU.getAddress().toString();
				Puerto= tmpPDU.getPort();
				
			}catch( Exception e)
			{
				
			}
			
			this.IdentifyType();
			
		}catch( Exception e)
		{
			System.err.println("ERROR: TTPDU: InitPDU: " + e.toString() );
		}
	}
	
	//FUNCIONES
	private synchronized String toHex()
	{	
		try{
			Hex = "";
			byte H1,H2;
			
			for( int i=0; i < B_PDU.length ; i++ )
			{
				H1= B_PDU[i];
				H2 = H1;
				H1 >>= 4;
				H1 = (byte) (H1 & (byte)0x0F);
				H2 = (byte) (H2 & (byte)0x0F);
				
				switch( H1 )
				{
					case 0:
						Hex+="0";
						break;		
					case 1:
						Hex+="1";
						break;
					case 2:
						Hex+="2";
						break;
					case 3:
						Hex+="3";
						break;
					case 4:
						Hex+="4";
						break;
					case 5:
						Hex+="5";
						break;
					case 6:
						Hex+="6";
						break;
					case 7:
						Hex+="7";
						break;
					case 8:
						Hex+="8";
						break;
					case 9:
						Hex+="9";
						break;
					case 10:
						Hex+="A";
						break;
					case 11:
						Hex+="B";
						break;
					case 12:
						Hex+="C";
						break;
					case 13:
						Hex+="D";
						break;
					case 14:
						Hex+="E";
						break;
					case 15:
						Hex+="F";
						break;
				}
				
				switch( H2 )
				{
					case 0:
						Hex+="0";
						break;		
					case 1:
						Hex+="1";
						break;
					case 2:
						Hex+="2";
						break;
					case 3:
						Hex+="3";
						break;
					case 4:
						Hex+="4";
						break;
					case 5:
						Hex+="5";
						break;
					case 6:
						Hex+="6";
						break;
					case 7:
						Hex+="7";
						break;
					case 8:
						Hex+="8";
						break;
					case 9:
						Hex+="9";
						break;
					case 10:
						Hex+="A";
						break;
					case 11:
						Hex+="B";
						break;
					case 12:
						Hex+="C";
						break;
					case 13:
						Hex+="D";
						break;
					case 14:
						Hex+="E";
						break;
					case 15:
						Hex+="F";
						break;
				}
				
			}
		}catch( Exception e )
		{
			System.err.println("ERROR: TTPDU: ToHex: " + e.toString() );
		}
		
		return Hex;
	}

	public synchronized String  getIgnitionState()
	{
		try{
			
			return "" + this.getIO_STATE().charAt(7);
			
			
		}catch( Exception e )
		{
			return "null";
		}
		
	}

	private synchronized String toBin()
	{
		try{
			Bin = "";
			for( int i = 0; i < Hex.length(); i++ )
			{
				switch( Hex.charAt(i ))
				{
					case '0':
						Bin+="0000";
						break;
					case '1':
						Bin+="0001";
						break;
					case '2':
						Bin+="0010";
						break;
					case '3':
						Bin+="0011";
						break;
					case '4':
						Bin+="0100";
						break;
					case '5':
						Bin+="0101";
						break;
					case '6':
						Bin+="0110";
						break;
					case '7':
						Bin+="0111";
						break;
					case '8':
						Bin+="1000";
						break;
					case '9':
						Bin+="1001";
						break;
					case 'A':
						Bin+="1010";
						break;
					case 'B':
						Bin+="1011";
						break;
					case 'C':
						Bin+="1100";
						break;
					case 'D':
						Bin+="1101";
						break;
					case 'E':
						Bin+="1110";
						break;
					case 'F':
						Bin+="1111";
						break;
				}
			}
			
			
		}catch(Exception e)
		{
			System.err.println("ERROR: TTPDU: toBIN: " + e.toString() );
			Bin = "";
		}
		return Bin;
	}
		
	public synchronized String getBin()
	{
		return Bin;
	}
		
	public synchronized String getHex()
	{
		return Hex;
	}
		
	public synchronized String getUDPAPINumber()
	{
		try{
			return "" + this.BinToInt( Bin.substring(0, 16) );			
			
		}catch( Exception e)
		{
			System.err.println("ERROR: TTPDU: getUDPAPINumber: " + e.toString() );
			return "";
			
		}
	}
	
	public synchronized String getCommandType()
	{
		try{
			return "" + this.BinToInt( Bin.substring(16, 24) ); 
		}catch( Exception e)
		{			
			System.err.println("ERROR: TTPDU: getCommandType: " + e.toString() );
			return "";
		}
		
	}
	
	public synchronized String getMessageHeaderReserved()
	{
		try{
			return "" + this.BinToInt(Bin.substring(24, 32));
		}catch( Exception e)
		{
			System.err.println( "ERROR: TTAPI: getMessageHeaderReserved: " + e.toString() );
			return "";
		}
	}
		
	public synchronized String getUserSpecifiedNumber()
	{
		try{
			if(  Type == 1 && SubType >= 75)
			{ 
				try{
					return "" + this.BinToInt( Bin.substring( 40 , 72) );
				}catch( Exception e)
				{
					return "";
				}
			}
			else
			{
				return "";
			}
		}catch( Exception e )
		{
			System.err.println( "ERROR: TTAPI: getUserSpecifiedNumber: " + e.toString() );
			return "";
		}
	}	
	
	public synchronized String getModemID()
	{
		try{
			String ModemID="";
			String MID_BIN="";
			
			if( Type == 1 && SubType >= 75)
			{ 
				try{
					
					MID_BIN = Bin.substring( 72 , 248);
					
					for ( int i =0; i < 22; i ++ )
					{
						ModemID+= (char)this.BinToInt( MID_BIN.substring( i*8, (i*8)+8) );
					}
					
					Id=ModemID.trim();
					
					return ModemID.trim( ).substring(0,5);
				}catch( Exception e)
				{
					return null;
				}
			}
			else
			{
				return null;
			}
		}catch( Exception e)
		{
			System.err.println("ERROR: TTPDU: getModemID: " + e.toString() );
			return null;
		}
			
	}
	
	public synchronized String getIO_CFG()
	{
		try{
			if( Type == 1 && SubType >= 75 )
				return "11100001";
			else
				return "";
		}catch( Exception e)
		{
			System.err.println("ERROR: TTPDU: getIO_CFG: " + e.toString() );
			return "";
		}
	}
	
	public synchronized String getIO_STATE()
	{
		try{
			if( Type == 1 && SubType >= 75 )
				
				return Bin.substring(263,264)+Bin.substring(262,263)+Bin.substring(254,255)+"0"+Bin.substring(261,262)+Bin.substring(259,260)+Bin.substring(258,259)+Bin.substring(255,256);
			else
				return "";
		}catch( Exception e)
		{
			System.err.println("ERROR: TTPDU: getIO_STATE: " + e.toString() );
			return "";
		}
	}
	
	public synchronized float getADC1()
	{
		
		try{
			
	 
			if( Type == 1 && SubType >= 75 )
			{
				
				return (float)(((float)this.BinToInt( Bin.substring( 264 , 280 ) ) * 16.00 ) / 1000.00);
			}
			else
			{
				return -1;
			}
			
			
		}catch( Exception e)
		{
			System.err.println("Exception: TTAPI: getADC1: ");
			return -1;
			
		}
		
	}
	
	public synchronized float getADC2()
	{
		try{
			if( Type == 1 && SubType >= 75 )
			{
				return (float)(((float)this.BinToInt( Bin.substring( 280, 296 ) ) * 16.00 )/1000.00);
			}
			else
				return -1;
			
		}catch( Exception e)
		{
			return -1;
		}
		
	}
	
	public synchronized String getInputEvent()
	{
		try{
			String InputEvent="";
			String InputEvent_BIN="";
			
			if( Type == 1 && SubType >= 75 )
			{ 
				try{
					
					InputEvent_BIN = Bin.substring( 296 , 304);
					InputEvent+= this.BinToInt( InputEvent_BIN );
						
					
					
					int event = Integer.parseInt( InputEvent );

					if( event >= 0 && event <= 7 )
					{
						event+=200 * Integer.parseInt( "" + this.getIO_STATE().charAt( event ) ); 
						
						return "" + event;
					}
					
					
					return InputEvent;
					
					
				}catch( Exception e)
				{
					return "";
				}
			}
			else
			{
				return "";
			}
		}catch( Exception e)
		{
			System.err.println("ERROR: TTPDU: getInputEvent: " + e.toString() );
			return null;
		}
			
	}
	
	public synchronized String getGPSDate()
	{
		try{
			String GPSDate, V;
						
			if( Type == 1 && SubType >= 75 )
			{ 
				try{
					
					V = "" + this.BinToLong( Bin.substring( 304 , 312 ) );
				
					if( V == null || V.length() == 0 )
						V = "00";
					else if( V.length() == 1 )
						V = "0" + V;
					else if ( V.length() > 1 )
						V = V.substring(V.length() - 2 );
					
					GPSDate = V;
					
					V = "" + this.BinToLong( Bin.substring( 312 , 320 ) );
					
					if( V == null || V.length() == 0 )
						V = "00";
					else if( V.length() == 1 )
						V = "0" + V;
					else if ( V.length() > 1 )
						V = V.substring(V.length() - 2 );
					
					GPSDate = GPSDate + V;
				
					V = "" + this.BinToLong( Bin.substring( 320 , 328 ) );
					
					if( V == null || V.length() == 0 )
						V = "00";
					else if( V.length() == 1 )
						V = "0" + V;
					else if ( V.length() > 1 )
						V = V.substring(V.length() - 2 );
					
					GPSDate = GPSDate + V;
					
					/*
					if( GPSDate.length() == 5 )
					{
						GPSDate = "0" + GPSDate;
					}
					else if( GPSDate.length() == 4 )
					{
						GPSDate = "00" + GPSDate;
					}
					else if( GPSDate.length() == 3 )
					{
						GPSDate = "000" + GPSDate;
					}
					else if( GPSDate.length() == 2 )
					{
						GPSDate = "0000" + GPSDate;
					}
					else if( GPSDate.length() == 1 )
					{
						GPSDate = "00000" + GPSDate;
					}
					else if( GPSDate.length() == 0 )
					{
						GPSDate = "000000" + GPSDate;
					}
					else if( GPSDate.length() > 6 )
						GPSDate = GPSDate.substring( GPSDate.length() - 6 , GPSDate.length() );*/
					
					
					return GPSDate;
						
				}catch( Exception e)
				{
					System.err.println( "ERROR: TTAPI: getGPSDate: " + e.toString() );
					return "";
				}
			}
			else
			{
				return "";
			}
		}catch( Exception e)
		{
			System.err.println("ERROR: TTPDU: getGPSSTATE: " + e.toString() );
			return null;
		}		
	}
	
	public synchronized String getGPSStatus()
	{
		try{
			String GPSStatus;
			if( Type == 1 && SubType >= 75)
			{ 
				GPSStatus = "" + this.BinToInt( Bin.substring( 328 , 336) );
				return GPSStatus;
				
			}
			else
			{
				return "";
			}
		
		}catch( Exception e)
		{
			System.err.println( "ERROR: TTAPI: getGPSStatus: " + e.toString() );
			return "";
		}
	}

	public synchronized String getLatitude()
	{
		try{
			long LatitudeLONG=0;
			long f= Long.valueOf( "4294967295" );
			int decimal=0; float cordenada=0;
			
			if( Type == 1 && SubType >= 75 )
			{ 
				try{
//					
//					if( SubType == 70 )
//						LatitudeLONG = this.BinToLong( Bin.substring( 328 , 360 ) );
//					else
						LatitudeLONG = this.BinToLong( Bin.substring( 336 , 368 ) );
												
					if( LatitudeLONG > Long.valueOf( "2147483647" ) )
					{
						LatitudeLONG = f - LatitudeLONG;
						LatitudeLONG = LatitudeLONG * -1;
						
					}
					
					decimal = (int) (LatitudeLONG /1000000);
					cordenada = (float)LatitudeLONG/1000000;
					cordenada -= (float)decimal;
					cordenada *= 100;
					cordenada /=  60;
					cordenada += (float) decimal;
					
					return "" + cordenada;
				
				}catch( Exception e)
				{
					return "";
				}
			}
			else
			{
				return "";
			}
		}catch( Exception e)
		{
			System.err.println( "ERROR: TTAPI: getLatitude: " + e.toString() );
			return "";
		}
	}
	
	public synchronized String getLongitude()
	{
		try{
			long LongitudeLONG=0;
			long f = Long.valueOf("4294967295");
			
			int decimal=0; float cordenada=0;
			
			if( Type == 1 && SubType >= 75 )
			{ 
//					if( SubType == 70 )
//						LongitudeLONG = this.BinToLong( Bin.substring( 360 , 392 ) );
//					else
						LongitudeLONG = this.BinToLong( Bin.substring( 368 , 400 ) );
									
					if( LongitudeLONG > Long.valueOf( "2147483647" ) )
					{
						LongitudeLONG = f - LongitudeLONG;
						LongitudeLONG = LongitudeLONG * -1;
					}
					
					decimal = (int) (LongitudeLONG /1000000);
					cordenada = (float)LongitudeLONG/1000000;
					cordenada -= (float)decimal;
					cordenada *= 100;
					cordenada /=  60;
					cordenada += (float) decimal;
					
					return "" + cordenada;
					
				
			}
			else
			{
				return "";
			}
		}catch( Exception e)
		{
			System.err.println( "ERROR: TTAPI: getLongitude: " + e.toString() );
			return "";
		}
			
	}
	
	public synchronized String getSpeed()
	{
		//SE CONVIERTE DE NUDOS A MILLAS
		//MILLAS
		try{
			if( Type == 1 && SubType >= 75 )
			{ 
				try{
					return "" + (int)(((float)this.BinToInt( Bin.substring( 400 , 416 ) ))*0.11507794);
				}catch( Exception e)
				{
					return "";
				}
			}
			else
			{
				return "";
			}
		}catch( Exception e )
		{
			System.err.println( "ERROR: TTAPI: getSpeed: " + e.toString() );
			return "";
		}
	}
	
	public synchronized String getHeading()
	{
		
		try{
			if( Type == 1 && SubType >= 75 )
			{ 
				try{
					
					return "" + (int)( (this.BinToInt( Bin.substring( 416 , 432) ))/10);
					
						
				}catch( Exception e)
				{
					return "";
				}
			}
			else
			{
				return "";
			}
		}catch( Exception e)
		{
			System.err.println("ERROR: TTIPDU: getHeading: " + e.toString() );
			return null;
		}
			
	}
	
	public synchronized String getGPSTime()
	{
		try{
			String GPSTime,V;
			
			if( Type == 1 && SubType >= 75 )
			{ 
				try{

					V = "" + this.BinToLong( Bin.substring( 432 , 440 ) );
						
					if( V == null || V.length() == 0 )
						V = "00";
					else if( V.length() == 1 )
						V = "0" + V;
					else if ( V.length() > 1 )
						V = V.substring(V.length() - 2 );
					
					GPSTime = V;
					
					V = "" + this.BinToLong( Bin.substring( 440 , 448 ) );
					
					if( V == null || V.length() == 0 )
						V = "00";
					else if( V.length() == 1 )
						V = "0" + V;
					else if ( V.length() > 1 )
						V = V.substring(V.length() - 2 );
					
					GPSTime = GPSTime + V;
					
					V = "" + this.BinToLong( Bin.substring( 448 , 456 ) );
					
					if( V == null || V.length() == 0 )
						V = "00";
					else if( V.length() == 1 )
						V = "0" + V;
					else if ( V.length() > 1 )
						V = V.substring(V.length() - 2 );
					
					GPSTime = GPSTime + V;	
					/*	
					if( GPSTime.length() == 5 )
					{
						GPSTime = "0" + GPSTime;
						return GPSTime;
					}
					else if( GPSTime.length() == 4 )
					{
						GPSTime = "00" + GPSTime;
						return GPSTime;
					}
					else if( GPSTime.length() == 3 )
					{
						GPSTime = "000" + GPSTime;
						return GPSTime;
					}
					else if( GPSTime.length() == 2 )
					{
						GPSTime = "0000" + GPSTime;
						return GPSTime;
					}
					else if( GPSTime.length() == 1 )
					{
						GPSTime = "00000" + GPSTime;
						return GPSTime;
					}
					else if( GPSTime.length() == 6 )
					{
						return GPSTime;
					}
					else if( GPSTime.length() > 6 )
					{
						return GPSTime.substring( GPSTime.length() - 6, GPSTime.length() );
					}*/
					return GPSTime;
				}catch( Exception e)
				{
					System.err.println( "ERROR: TTAPI: getGPSTIME: " + e.toString() );
					return "";
				}
			}
			else
			{
				return "";
			}
		}catch( Exception e)
		{
			System.err.println("ERROR: TTPDU: getGPSTIME: " + e.toString() );
			return null;
		}
			
	}
	
	public synchronized String getAltitude()
	{
		try{
			if( Type == 1 && SubType >= 75 )
			{ 
				return "" + this.BinToInt( Bin.substring( 456, 480) )/10;
			}
			else
			{
				return "";
			}
		}catch( Exception e)
		{
			System.err.println("ERROR: TTPDU: getAltitude: " + e.toString() );
			return null;
		}
	
	}
	
	public synchronized String getNumberOfSatellites()
	{
		try{	
			if( Type == 1 && SubType >= 75 )
			{ 
				try{
					
					return "" + this.BinToInt( Bin.substring( 480 , 488 ) );
					
						
				}catch( Exception e)
				{
					return "";
				}
			}
			else
			{
				return "";
			}
		}catch( Exception e)
		{
			System.err.println( "ERROR: TTAPI: getNumeroOfSatellites: " + e.toString() );
			return "";
		}
			
	}
	
	public synchronized String getOdometer()
	{
		try{
			if( Type == 1 && SubType >= 75 )
			{ 
				try{
					return "" + this.BinToLong( Bin.substring( 488 , 520 ) );
				
					
						
				}catch( Exception e)
				{
					return "";
				}
			}
			else
			{
				return "";
			}
		}catch( Exception e )
		{
			System.err.println( "ERROR: TTAPI: getOdometer: " + e.toString() );
			return "";
		}
	}
	
	public synchronized String getRTC()
	{	
		try{
			String rtc="",n;
			int i=0;
			
			if( Type == 1 && SubType >= 75 )
			{	
				//AÑO
				n = "" + ( 2000 + this.BinToInt( Bin.substring( 552 , 560) ) );
				rtc+=n;
				
				//MONTH,DAY,YEAR,HORA,MINUTO,SEGUNDO
				for( i=0; i<5; i++ )
				{	
					n = "" + this.BinToInt( Bin.substring( 560+i*8 , 568+i*8 ) );
					if ( n.length() == 1 )
						n = "0"+n;
					rtc+=n;
				}
			}
			return rtc;
		}catch( Exception e)
		{
			System.err.println("ERROR: TTPDU: getRTC: " + e.toString() );
			return null;
		}
	}
	
	public synchronized int getBatteryLevel()
	{
		try{
			return this.BinToInt( Bin.substring( 552 , 560 ) )/1000;
		}catch( Exception e)
		{
			return -1;
		}
		
	}
	
	public synchronized String getDateTimeSQL()
	{	
		String dt,d,t;
		Calendar C;
				
		try{
			if( Type == 2 || Type == 1 )
			{	
				C = new GregorianCalendar();
				d = this.getGPSDate();
				t = this.getGPSTime();
		
				C.set( Integer.parseInt( "20" + d.substring( 4,6) ), Integer.parseInt( d.substring(2,4))-1, Integer.parseInt( d.substring(0,2)), Integer.parseInt( t.substring(0,2)), Integer.parseInt( t.substring(2,4)), Integer.parseInt( t.substring(4,6)));
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
				C.add(Calendar.HOUR_OF_DAY, -5 );
				dt = sdf.format( C.getTime());
				return dt;
			}
			else if( Type == 12)
			{
				C = new GregorianCalendar();
				C.setTimeInMillis(Time);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
				dt = sdf.format( C.getTime());
				return dt;
				
			}
			else if ( Type == 3 )
			{
				C = new GregorianCalendar();
				C.setTimeInMillis(Time);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
				dt = sdf.format( C.getTime());
				return dt;
			}
			else 
				return "";
	        
		}catch( Exception e )
		{
			System.err.println( "ERROR: TTAPI: getDateTimeSQL: " + e.toString() );
			return "";
		}
			
	}
	
	public synchronized String getCMDResponse()
	{
		try{
			String S_PDU;
			
			if( Type == 12 )
			{
				S_PDU = new String( B_PDU );
				
				return S_PDU.substring(5);
			}
			else
				return null;
		}catch(Exception e )
		{
			System.err.println("ERROR: TTPDU: getCMDResponse: " + e.toString() );
			return null;
		}
	}
	
	public synchronized int BinToInt( String tmpBin )
	{
		try{	
			int valor=0,i=0;
			
			try{
				for( i=0; i < tmpBin.length(); i ++ )
				{
					if( tmpBin.charAt(i) == '1' )
					{
						valor+=Math.pow( 2 ,  tmpBin.length()- ( i + 1 ));
					}
					
				}
			}catch( Exception e )
			{
				
			}
			return valor;
		}catch( Exception e)
		{
			System.err.println("ERROR: TTPDU: BinToInt: " + e.toString() );
			return 0;
		}
	}
	
	public synchronized String ToString()
	{
		try{
			String C="";
			if ( Type == 2)
	        {	
				C += "SOURCE: " + IP;
				C += ":" + Puerto;
		        C += "; PDU LENGTH: " + B_PDU.length + "\n";
		        C += "UDP API Number: " + this.getUDPAPINumber();
		        C += "; Command Type: " + this.getCommandType();
		        C += "; Reserved: " + this.getMessageHeaderReserved() + "\n";
		        C += "GPSDate: " + this.getGPSDate();
		        C += "; GPSTime: " + this.getGPSTime();
		        C += "; ModemID: " + this.getModemID();
		        C += "; UserSpecifiedNumber: " + this.getUserSpecifiedNumber();
		        C += "; InputEvent: " + this.getInputEvent();
		        C += "; Latitude: " + this.getLatitude();
		        C += "; Longitude: " + this.getLongitude();
		        C += "; GPSAltitude: " + this.getAltitude();
		        C += "; GPSSpeed: " + this.getSpeed();
		        C += "; GPSHeading: " + this.getHeading();
		        C += "; NumberOfSatellites: " + this.getNumberOfSatellites();
		        C += "; GPSStatus: " + this.getGPSStatus();
		        C += "; Odometer: " + this.getOdometer();
		        C += "; IO_CFG: " + this.getIO_CFG();
		        C += "; IO_STATE: " + this.getIO_STATE();
		        
		        if( Type == 1 )
		        	C += "; RTC: " + this.getRTC();
	        
		        return C;
	        }
	        return "";
		}catch( Exception e)
		{
			System.err.println("ERROR: TTPDU: toString: " + e.toString() );
			return null;
		}
	}
	
	public synchronized long BinToLong( String tmpBin )
	{
		try{
			long valor=0; int i=0;
			
			try{
				for( i=0; i < tmpBin.length(); i ++ )
				{
					if( tmpBin.charAt(i) == '1' )
					{
						valor+=Math.pow( 2 ,  tmpBin.length()- ( i + 1 ));
					}
					
				}
			}catch( Exception e )
			{
				return 0;
			}
			return valor;
		}catch( Exception e)
		{
			System.err.println("ERROR: TTPDU: BinToLong: " + e.toString() );
			return 0;
		}
	}
	
	/*public synchronized boolean isPDUofGPSDATA()
	{
		try{
			if( Type == 2 || Type == 1 || Type == 11 )
				return true;
			else 
				return false;
		}catch( Exception e)
		{
			System.err.println("ERROR: UDPAPIPDU: isPDUofGPSDATA: " + e.toString() );
			return false;
		}
		
	}*/
	
	public synchronized boolean isTaxiDataPDU()
	{
		return false;
	}
	
	public synchronized boolean isTaxiDataEstadoPDU()
	{
		return false;
	}
	
	public synchronized boolean isTaxiDataMensajePDU()
	{
		return false;
	}
	
	public synchronized boolean isTaxiDataTaximetroPDU()
	{
		return false;
	}
	
	public synchronized boolean isPositionReportPDU()
	{
		try{
			if( Type == 2 || Type == 1 || Type == 11 )
				return true;
			else 
				return false;
		}catch( Exception e)
		{
			System.err.println("ERROR: TTPDU: isPDUofGPSDATA: " + e.toString() );
			return false;
		}
		
	}
	
	public synchronized boolean isCMDResponse()
	{
		if( this.getType() == 12 )
			return true;
		return false;
	}
	
	public synchronized String getSource()
	{
		return IP;
	}
	
	public synchronized long getVariable( int i )
	{
		try{
			String Variable_Bin;
			long l_Variable;
			
			Variable_Bin = Bin.substring( 248 + i*32, 280 + i*32 );
			l_Variable = this.BinToLong( Variable_Bin );
			
			return l_Variable;
		}catch( Exception e)
		{
			System.err.println("ERROR: TTPDU: getVariable: " + e.toString() );
			return 0;
		}
		
	}

	public synchronized int getType()
	{
		return Type;
	}
	
	@Override
	public int getSubType() 
	{
		// TODO Auto-generated method stub
		return SubType;
	}

	@Override
	public String getS_PDU() 
	{
		// TODO Auto-generated method stub
		return S_PDU;
	}

	@Override
	public boolean isPDUofPhoto() 
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getEstado() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumeroMensaje() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getMensaje() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getTiempoOcupado() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getImporteAPagar() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTiempoEspera() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getKilometraje() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNumeroCarrera() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNumeroVoucher() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public synchronized byte[] getB_PDU() 
	{		
		return B_PDU;
	}
	
	public synchronized  String getACK()
	{
		return null;
	}

	@Override
	public byte[] getBACK() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public synchronized String getTemperatura1()
	{
		try{
			Parametros =  Id.substring(5).split(",");
			
			//System.out.println("Cadena: "+Id);
			//System.out.println("Temp 0 :"+Parametros[0]);
			//System.out.println("Temp 1 :" +Parametros[1]);
			return Parametros[0];
			
		}catch( Exception e)
		{
			System.err.println( "No se encontraron datos de temperatura en Sensor1: " + e.toString() );
			return "0";
		}
	}
	
	public synchronized String getTemperatura2()
	{
		try{
			
			Parametros =  Id.substring(5).split(",");
			
			return Parametros[1];

		}catch( Exception e)
		{
			System.err.println( "No se encontraron datos de temperatura en Sensor2: " + e.toString() );
			return "0";
		}
	}

	@Override
	public float getEA1() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getEA2() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getEA3() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getSA1() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getSA2() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getSA3() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getVoltajeAlimentacion() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getPuntoVisita() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getPuntoVisitaEstado() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int CellID0() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int SenalCellID0() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int CellID1() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int SenalCellID1() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int CellID2() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int SenalCellID2() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int CellID3() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int SenalCellID3() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int CellID4() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int SenalCellID4() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int CellID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int SenalCellID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int MCC() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int LAC() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int rpmOBD() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int PosicionAcceleradorOBD() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int OdometroOBD() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int OdometroViajeOBD() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float NivelGasolinaOBD() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float CombustibleRestanteOBD() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int EngraneTransmisionOBD() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float TemperaturaRefrigeranteOBD() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float IndiceGasolinaOBD() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float VoltajeAlimentacionOBD() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int EstadoSeñalesGiroOBD() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float GasolinaConsumidaPorViaje() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String IndicadoresOBD() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getHorometer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDTC() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getCheckEngine() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getMaxSpeed() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTimeMaxSpeed() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSpeedSetPoint() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEventoValor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEventoValorSP() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getImpactData() {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
