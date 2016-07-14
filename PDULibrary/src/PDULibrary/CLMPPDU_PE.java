//V1.0.0

package PDULibrary;

import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class CLMPPDU_PE implements PDU
{	
	//PROPIEDADES
	private byte[] B_PDU,ACK;
	String Bin="",Hex="",S_PDU="";
	String[] SA_PDU,SA0;
	
	int Type=0,SubType=0;
	public String IP=null,IPDestino="/0.0.0.0";
	public int Puerto,PuertoDestino=0;
	
	long Time,DateTimeReceived;
	
	boolean DebugERROR,DebugTRAN;
	
	String CCalle;
	
	public String getCalle()
	{
		return CCalle;
	}
	
	public void setCalle( String Calle )
	{
		CCalle = Calle; 
	}
	
	public int getIdProtocolo()
	{
		return 99;
	}
	
	public synchronized String getReportDateTime( int gmt )
	{
		try{
			String dt,d,t;
			Calendar C;
			
			
			C = new GregorianCalendar();
			
			
			if( getGPSStatus().compareTo("1") == 0 && getType() == 1 )
			{
				d = this.getGPSDate();
				t = this.getGPSTime();
				C.set( Integer.parseInt( "20" + d.substring( 4,6) ), Integer.parseInt( d.substring(2,4))-1, Integer.parseInt( d.substring(0,2)), Integer.parseInt( t.substring(0,2)), Integer.parseInt( t.substring(2,4)), Integer.parseInt( t.substring(4,6)));
				
			}
			else 
			{
				d = this.getRTC();
				t = this.getRTC();
				C.set( Integer.parseInt( d.substring( 0,4) ), Integer.parseInt( d.substring(4,6))-1, Integer.parseInt( d.substring(6,8)), Integer.parseInt( t.substring(8,10)), Integer.parseInt( t.substring(10,12)), Integer.parseInt( t.substring(12,14)));
				
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			C.add(Calendar.HOUR_OF_DAY, gmt );
			dt = sdf.format( C.getTime());
			
			return dt;
		}catch( Exception e)
		{
			System.err.println("ERROR: CLMPPDU_PE: ReportDateTime:" + e.toString());
			
			return null;
		}
	}
	
	public synchronized String getReportDateTime()
	{
		try{
			if( getGPSStatus().compareTo("1") == 0 && getType() == 1 )
			{
				return "20" + getGPSDate().substring(4,6) + getGPSDate().substring(2, 4) + getGPSDate().substring(0,2) +
					" " + getGPSTime().substring(0,2) + ":" + getGPSTime().substring(2,4) + ":" + getGPSTime().substring(4,6); 
			}
			else 
			{
				return getRTC().substring(0,4) + getRTC().substring( 4,6 ) + getRTC().substring( 6,8 ) +
					" " + getRTC().substring(8, 10) + ":" + getRTC().substring(10, 12) + ":" + getRTC().substring(12,14); 
			}
			
		}catch( Exception e)
		{
			System.err.println("ERROR: TTPDU: ReportDateTime:" + e.toString());
			
			return null;
		}
	}
	
	public synchronized String getReportDateTimeRTC()
	{
		try{
			long Segundos; GregorianCalendar C;SimpleDateFormat sdf;
			
			if( Type == 1 && (SubType == 117 || SubType == 121 || SubType == 189) )
			{ 
				Segundos = this.BinToLong( Bin.substring( 104 , 136 ) );
				C = new GregorianCalendar();
				C.set( 1970 , Calendar.JANUARY , 1 , 0, 0 , 0 );
				C.add(Calendar.SECOND, (int)Segundos);
				sdf = new SimpleDateFormat("yyyMMdd HH:mm:ss");
				return sdf.format( C.getTime());
			}
			return null;
		}catch( Exception e)
		{
			System.err.println("ERROR: CLMPPDU_PE: ReportDateTimeRTC: " + e.toString());
			return null;
		}
		
	}

	public synchronized String getReportDateTimeGPS()
	{
		try{
			if( Type == 1  )
			{
				return "20" + getGPSDate().substring(4,6) + getGPSDate().substring(2,4) + getGPSDate().substring(0, 2) + 
			
				" " + getGPSTime().substring(0,2) + ":" + getGPSTime().substring(2,4) + ":" + getGPSTime().substring(4,6);
			}
			else
				return null;
			
		}catch( Exception e)
		{
			System.err.println("ERROR: CLMPPDU_PE: ReportDateTimeGPS: " + e.toString());
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
			return null;
		}catch( Exception e)
		{
			System.err.println( "ERROR: CMLPAPI: obtenerTemperatura1: " + e.toString() );
			return null;
		}
	}
	
	public synchronized String obtenerTemperatura2()
	{
		try{
			
			return null;
		}catch( Exception e)
		{
			System.err.println( "ERROR: CLMPAPI: obtenerTemperatura2: " + e.toString() );
			return null;
		}
	}
	
	private void IdentifyType()
	{
		/*TIPO DE CLMP
		 * 1: GPS DATE IN BINARY FORMAT WITH RTC
		 
		*/ 
		
		try{
			if( ( B_PDU.length == 117 || B_PDU.length == 121 || B_PDU.length == 189 ) )
			{
				Type = 1;
				SubType = B_PDU.length;
			}
			else 
			{
				Type = 12;
			}
			
		}catch( Exception e)
		{
			System.err.println( "ERROR: CLMPAPI: IdentifyType: " + e.toString() );
			Type = 0;
		}
	}
	
	//CONSTRUCTORES
	public CLMPPDU_PE( DatagramPacket tmpPDU, DatagramSocket UDPServer, String IPServidor )
	{
		DateTimeReceived = System.currentTimeMillis();
		
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
	
	public CLMPPDU_PE( DatagramPacket tmpPDU )
	{
		DateTimeReceived = System.currentTimeMillis();
		InitPDU( tmpPDU );
		
	}
		
	public CLMPPDU_PE( byte[] tmpPDU )
	{
		try{
			Time = System.currentTimeMillis(); 
			DateTimeReceived = System.currentTimeMillis();
			
			B_PDU = tmpPDU;
			Hex = this.toHex();
			Bin = this.toBin();
		
			this.IdentifyType();
			
		}catch( Exception e )
		{
			System.err.println("ERROR: CLMPPDU_PE: CLMPPDU_PE: " + e.toString() );
		}
	}
	
	private void InitPDU( DatagramPacket tmpPDU )
	{
		try{
			
			Time = System.currentTimeMillis(); 
			
			byte[] tmpB_PDU;
			
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
			
			try{
				if( Type == 1 && (SubType == 117 || SubType == 121 || SubType == 189 ) )
				{
					ACK = new byte[10];
				
					ACK[0]=0x2;
					ACK[1]=0x1;
					ACK[2]=B_PDU[11];
					ACK[3]=B_PDU[12];
					ACK[4]=0x2;
					ACK[5]=0x0;
					ACK[6]=0x0;
					ACK[7]=0x0;
					ACK[8]=0x0;
					ACK[9]=0x0;
				}
			}catch( Exception e2 )
			{
				ACK=null;
			}
			
			
		}catch( Exception e)
		{
			System.err.println("ERROR: CLMPPDU_PE: InitPDU: " + e.toString() );
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
			System.err.println("ERROR: CLMPPDU_PE: ToHex: " + e.toString() );
		}
		
		return Hex;
	}

	public synchronized String  getIgnitionState()
	{
		try{
			String IO = this.getIO_STATE();
			
			if( IO != null && IO.length() == 8 )
				return "" + IO.charAt(7);
			else if( IO != null && IO.length() > 0 )
			{
				return "" + IO.charAt( IO.length() - 1 );
			}
			else
				return "null";
			
			
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
			System.err.println("ERROR: CLMPPDU_PE: toBIN: " + e.toString() );
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
		/*
		 * EQUIVALENTE A TIPO DE MENSAJE
		 * 
		 */
		try{
			if( Type == 1 && (SubType == 117 || SubType == 121 || SubType == 189 ) )
				return "" + (int)B_PDU[10];
			return null;
			
		}catch( Exception e)
		{
			System.err.println("ERROR: CLMPPDU_PE: getUDPAPINumber: " + e.toString() );
			return null;
			
		}
	}
	
	public synchronized String getCommandType()
	{
		/*
		 * EQUIVALENTE A TIPO DE SERVICIO
		 * 
		 */
		try{
			if( Type == 1 && (SubType == 117 || SubType == 121 || SubType == 189 ) )
				return "" + B_PDU[9];
			return null; 
		}catch( Exception e)
		{			
			System.err.println("ERROR: CLMPPDU_PE: getCommandType: " + e.toString() );
			return null;
		}
		
	}
	
	public synchronized String getMessageHeaderReserved()
	{
		try{
			return "";
		}catch( Exception e)
		{
			System.err.println( "ERROR: CLMPAPI: getMessageHeaderReserved: " + e.toString() );
			return "";
		}
	}
		
	public synchronized String getUserSpecifiedNumber()
	{
		try{
			if(  Type == 1 && (SubType == 117 || SubType == 121 || SubType == 189 ) )
			{ 
				try{
					return "" + BinToLong(Bin.substring(400,408));
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
			System.err.println( "ERROR: CLMPPDU_PE: getUserSpecifiedNumber: " + e.toString() );
			return "";
		}
	}	
	
	public synchronized String getModemID()
	{
		try{
			String ModemID="";
						
			if( Type == 1 && (SubType == 117 || SubType == 121 || SubType == 189 ))
			{ 
				try{
					if( B_PDU[0] == -125 )//0x83
					{
						ModemID = Hex.substring(4, 4+(B_PDU[1]*2) );
					}
					return ModemID.trim( );
				}catch( Exception e)
				{
					System.err.println( e.toString());
					return null;
				}
			}
			else
			{
				return null;
			}
		}catch( Exception e)
		{
			System.err.println("ERROR: CLMPPDU_PE: getModemID: " + e.toString() );
			return null;
		}
			
	}
	
	public synchronized String getIO_CFG()
	{
		try{
			return "";
		}catch( Exception e)
		{
			System.err.println("ERROR: CLMPPDU_PE: getIO_CFG: " + e.toString() );
			return "";
		}
	}
		
	public synchronized String getIO_STATE()
	{
		try{
			if( Type == 1 && (SubType == 117 || SubType == 121 ) )
			{
				//return Bin.substring( 376 , 384); 
				return Bin.substring( 840 , 872);
				
			}
			else if ( Type == 1 && SubType == 189 )
				return Bin.substring( 904 , 936);
			return "";
		}catch( Exception e)
		{
			System.err.println("ERROR: CLMPPDU_PE: getIO_STATE: " + e.toString() );
			return "";
		}
		
	}
		
	public synchronized String getInputEvent()
	{
		try{
			
			if( Type == 1 && (SubType == 117 || SubType == 121 || SubType == 189 ) )
			{ 
				return this.getUserSpecifiedNumber();
			}
			return null;
			
		}catch( Exception e)
		{
			System.err.println("ERROR: CLMPPDU_PE: getInputEvent: " + e.toString() );
			return null;
		}
			
	}
		
	public synchronized String getGPSDate()
	{
		try{
			long Segundos; GregorianCalendar C;SimpleDateFormat sdf;
						
			if( Type == 1 && (SubType == 117 || SubType == 121 || SubType == 189  ) )
			{ 
				try{
					Segundos = this.BinToLong( Bin.substring( 136 , 168 ) );
					C = new GregorianCalendar();
					C.set( 1970 , Calendar.JANUARY , 1 , 0, 0 , 0 );
					C.add(Calendar.SECOND, (int)Segundos);
					sdf = new SimpleDateFormat("ddMMyy");
					return sdf.format( C.getTime());
								
				}catch( Exception e)
				{
					System.err.println( "ERROR: CLMPPDU_PE: getGPSDate: " + e.toString() );
					return "";
				}
			}
			return "";
		}catch( Exception e)
		{
			System.err.println("ERROR: CLMPPDU_PE: getGPSDate: " + e.toString() );
			return null;
		}		
	}
		
	public synchronized String getGPSTime()
	{
		try{
			long Segundos; GregorianCalendar C;SimpleDateFormat sdf;
			
			if( Type == 1 && (SubType == 117 || SubType == 121 || SubType == 189 ) )
			{ 
				Segundos = this.BinToLong( Bin.substring( 136 , 168 ) );
				C = new GregorianCalendar();
				C.set( 1970 , Calendar.JANUARY , 1 , 0, 0 , 0 );
				C.add(Calendar.SECOND, (int)Segundos);
				sdf = new SimpleDateFormat("HHmmss");
				return sdf.format( C.getTime());
			}
			return "";
				
		}catch( Exception e)
		{
			System.err.println( "ERROR: CLMPPDU_PE: getGPSTime: " + e.toString() );
			return "";
		}
			
	}
		
	public synchronized String getGPSStatus()
	{
		try{
			if( Type == 1 && (SubType == 117 || SubType == 121 || SubType == 189 ) )
			{ 
				if( Bin.charAt(325) == '0' )
					return "1";
				else 
					return "9";
							
				//return "" + (int)B_PDU[40];
			}
			else
			{
				return "";
			}
		
		}catch( Exception e)
		{
			System.err.println( "ERROR: CLMPPDU_PE: getGPSStatus: " + e.toString() );
			return "";
		}
	}
	
	public synchronized String getLatitude()
	{
		try{
			long LatitudeLONG=0;
			long f= Long.valueOf( "4294967296" );
		    float cordenada=0;
			
			if( Type == 1 && (SubType == 117 || SubType == 121 || SubType == 189 ) )
			{ 
				try{

					LatitudeLONG = this.BinToLong( Bin.substring( 168 , 200 ) );
												
					if( LatitudeLONG > Long.valueOf( "2147483647" ) )
					{
						LatitudeLONG = f - LatitudeLONG;
						LatitudeLONG = LatitudeLONG * -1;
						
					}
					cordenada = (float)LatitudeLONG/(float)10000000;
					
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
			System.err.println( "ERROR: CLMPAPI: getLatitude: " + e.toString() );
			return "";
		}
	}
		
	public synchronized String getLongitude()
	{
		try{
			long LongitudeLONG=0;
			long f = Long.valueOf("4294967296");
			
			float cordenada=0;
			
			if( Type == 1 && (SubType == 117 || SubType == 121 || SubType == 189 ) )
			{ 
					LongitudeLONG = this.BinToLong( Bin.substring( 200 , 232 ) );
									
					if( LongitudeLONG > Long.valueOf( "2147483647" ) )
					{
						LongitudeLONG = f - LongitudeLONG;
						LongitudeLONG = LongitudeLONG * -1;
					}
					
					cordenada = (float)LongitudeLONG/(float)10000000;
					
					/*
					decimal = (int) (LongitudeLONG /10000000);
					cordenada = (float)LongitudeLONG/10000000;
					cordenada -= (float)decimal;
					cordenada *= 100;
					cordenada /=  60;
					cordenada += (float) decimal;
					*/
					
					return "" + cordenada;
			}
			else
			{
				return "";
			}
		}catch( Exception e)
		{
			System.err.println( "ERROR: CLMPAPI: getLongitude: " + e.toString() );
			return "";
		}
			
	}
		
	public synchronized String getSpeed()
	{
		//SE CONVIERTE DE NUDOS A MILLAS
		//MILLAS
		try{
			if( Type == 1 && (SubType == 117 || SubType == 121 || SubType == 189 ) )
			{ 
				long SpeedLONG=0;
				long f = Long.valueOf("4294967296");
				
				SpeedLONG = this.BinToLong( Bin.substring( 264 , 296 ) );
										
				if( SpeedLONG > Long.valueOf( "2147483647" ) )
				{
					SpeedLONG = f - SpeedLONG;
					SpeedLONG = SpeedLONG * -1;
				}						
			    return "" + ( SpeedLONG * 0.0223693596 );				
			}
			else
			{
				return "";
			}
		}catch( Exception e)
		{
			System.err.println("ERROR: CLMPPDU_PE: getAltitude: " + e.toString() );
			return null;
		}
	}
		
	public synchronized String getHeading()
	{
		try{
			if( Type == 1 && (SubType == 117 || SubType == 121 || SubType == 189 ) )
			{ 
				try{
					
					return "" + (int)( (this.BinToInt( Bin.substring( 296 , 312) )));
					
						
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
			System.err.println("ERROR: CLMPPDU_PE: getHeading: " + e.toString() );
			return null;
		}
			
	}
		
	public synchronized String getAltitude()
	{
		try{
			if( Type == 1 && (SubType == 117 || SubType == 121 || SubType == 189 ) )
			{ 
				long AltitudeLONG=0;
				long f = Long.valueOf("4294967296");
				
				AltitudeLONG = this.BinToLong( Bin.substring( 232 , 264 ) );
										
				if( AltitudeLONG > Long.valueOf( "2147483647" ) )
				{
					AltitudeLONG = f - AltitudeLONG;
					AltitudeLONG = AltitudeLONG * -1;
				}						
				return "" + (AltitudeLONG/100);
				
			}
			else
			{
				return "";
			}
		}catch( Exception e)
		{
			System.err.println("ERROR: CLMPPDU_PE: getAltitude: " + e.toString() );
			return null;
		}	
	}
		
	public synchronized String getNumberOfSatellites()
	{
		try{	
			if( Type == 1 && (SubType == 117 || SubType == 121 || SubType == 189 ) )
			{ 
				try{
					
					return "" + (int)B_PDU[39];
					
						
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
			System.err.println( "ERROR: CLMPAPI: getNumeroOfSatellites: " + e.toString() );
			return "";
		}
			
	}
		
	public synchronized String getOdometer()
	{
		try{
			if( Type == 1 )
			{ 
				if( SubType == 117 )
				{
					long OdometroLONG=0;
					OdometroLONG = this.BinToLong( Bin.substring( 520 , 552 ) );
					return "" + (OdometroLONG);
				}
				else if( SubType == 121 )
				{
					long OdometroLONG=0;
					OdometroLONG = this.BinToLong( Bin.substring( 776 , 808  ) );
					return "" + (OdometroLONG);
				}
				
			}
			return null;			
		}catch( Exception e )
		{
			System.err.println( "ERROR: CLMPPDU_PE: getOdometer: " + e.toString() );
			return null;
		}
	}
	
	public synchronized String getHorometer() 
	{
		try{
			if( Type == 1 )
			{ 
				if( SubType == 117 )
				{
					long HorometroLONG=0;
					HorometroLONG = this.BinToLong( Bin.substring( 872,904 ) );
					return "" + (HorometroLONG);
					
				}
				else if( SubType == 121 )
				{
					long HorometroLONG=0;
					HorometroLONG = this.BinToLong( Bin.substring( 936 ) );
					return "" + (HorometroLONG);
				}
				
			}
			return null;			
		}catch( Exception e )
		{
			System.err.println( "ERROR: CLMPPDU_PE: getOdometer: " + e.toString() );
			return null;
		}
	}
		
	public synchronized String getRTC()
	{	
		try{	
			long Segundos; GregorianCalendar C;SimpleDateFormat sdf;
				
			if( Type == 1 && (SubType == 117 || SubType == 121 || SubType == 189 ) )
			{ 
				try{
					Segundos = this.BinToLong( Bin.substring( 104 , 136 ) );
					C = new GregorianCalendar();
					C.set( 1970 , Calendar.JANUARY , 1 , 0, 0 , 0 );
					C.add(Calendar.SECOND, (int)Segundos);
					sdf = new SimpleDateFormat("yyyyMMddHHmmss");
					return sdf.format( C.getTime());
								
				}catch( Exception e)
				{
					System.err.println( "ERROR: CLMPPDU_PE: getRTC: " + e.toString() );
					return "";
				}
			}
			return "";
			/*
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
			}*/
						
		}catch( Exception e)
		{
			System.err.println("ERROR: CLMPPDU_PE: getRTC: " + e.toString() );
			return null;
		}
		
	}
		
	public synchronized String getDateTimeSQL()
	{	
		String dt,d,t;
		Calendar C;
				
		try{
			if( Type == 1 )
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
			else if( this.isCMDResponse() )
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
			System.err.println( "ERROR: CLMPPDU_PE: getDateTimeSQL: " + e.toString() );
			return "";
		}
			
	}
		
	public synchronized String getCMDResponse()
	{
		try{
			if( this.isCMDResponse() )
			{
				return Hex;
			}
			else
				return null;
		}catch(Exception e )
		{
			System.err.println("ERROR: CLMPPDU_PE: getCMDResponse: " + e.toString() );
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
			System.err.println("ERROR: CLMPPDU_PE: BinToInt: " + e.toString() );
			return 0;
		}
	}
		
	public synchronized String ToString()
	{
		try{
			String C="";
			if ( Type == 1)
	        {	
				C += "SOURCE: " + IP;
				C += ":" + Puerto;
		        C += "; PDU LENGTH: " + B_PDU.length + "\n";
		        C += "; UDP API Number: " + this.getUDPAPINumber();
		        C += "; Command Type: " + this.getCommandType();
		        C += "; Reserved: " + this.getMessageHeaderReserved() + "\n";
		        C += "; GPSDate: " + this.getGPSDate();
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
			System.err.println("ERROR: CLMPPDU_PE: toString: " + e.toString() );
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
			if( Type == 1 && (SubType == 117 || SubType == 121 || SubType == 189 ) )
				return true;
			else 
				return false;
		}catch( Exception e)
		{
			System.err.println("ERROR: CLMPPDU_PE: isPDUofGPSDATA: " + e.toString() );
			return false;
		}
		
	}
		
	public synchronized boolean isCMDResponse()
	{
		if( Type == 12 )
			return true;
		else
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
		if( ACK != null)
			return new String(ACK);
		return null;
	}
	
	public byte[] getBACK() 
	{
		return ACK;
	}
	
	public String DateTimeReceveid()
	{
		try{
			GregorianCalendar C;String dt;
			C = new GregorianCalendar();
			C.setTimeInMillis( DateTimeReceived );
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS");
			dt = sdf.format( C.getTime());
			return dt;
		}catch( Exception e)
		{
			return "20000101 00:00:00.000";
		}
	}

	public float getNivelBateria() 
	{
		try{
			return -1;
		}catch( Exception e)
		{
			System.err.println("Exception: TTPDU: getNivelBateria: " + e.toString() );
			return -1;
		}
	}
	
	public float getVoltajeBateria() 
	{
		try{
			if( Type == 1 )
			{
				if( SubType == 117 )
					return (float) (this.BinToInt( Bin.substring( 456 , 488 ) ) / 1000.0);
				else if( SubType == 121)
					return (float) (this.BinToInt( Bin.substring( 712 , 744 ) ) / 1000.0);
					
			}
			
			
			return -1;
		}catch( Exception e)
		{
			System.err.println("Exception: TTPDU: getVoltajeBateria: " + e.toString() );
			return -1;
		}
	}

	@Override
	public float getEA1() 
	{
		try{
			int v=0;
			if( Type == 1 )
			{
				if( SubType == 117 || SubType == 189 )
				{
					v = this.BinToInt( Bin.substring( 488 , 520 ) );

					if ( v == 0)
					{
						return -1;
					}
				}
				
					
			}
			return -1;
			
		}catch( Exception e)
		{
			System.err.println("Exception: TTAPI: getEA1: "  + e.toString());
			return -1;
			
		}
	}

	@Override
	public float getEA2() 
	{
		try{
			int v=0;
			if( Type == 1 )
			{
				if( SubType == 189 )
				{
					v = this.BinToInt( Bin.substring( 552 , 584 ) );

					if ( v == 0)
					{
						return -1;
					}
				}
				
					
			}
			
			return -1;
		}catch( Exception e)
		{
			System.err.println("Exception: TTAPI: getEA2: "  + e.toString());
			return -1;
		}
	}

	@Override
	public float getEA3() 
	{
		try{
						
			int v=0;
			if( Type == 1 )
			{
				if( SubType == 189 )
				{
					v = this.BinToInt( Bin.substring( 520 , 552 ) );

					if ( v == 0)
					{
						return -1;
					}
				}
				
					
			}
			return -1;
		}catch( Exception e)
		{
			System.err.println( "Exception: TTAPI: EA3: " + e.toString() );
			return -1;
		}
	}

	@Override
	public float getSA1() {
		// TODO Auto-generated method stub
		return -1;
	}

	@Override
	public float getSA2() {
		// TODO Auto-generated method stub
		return -1;
	}

	@Override
	public float getSA3() {
		// TODO Auto-generated method stub
		return -1;
	}

	@Override
	public float getVoltajeAlimentacion()
	{
		try{
			if( Type == 1 )
			{
				if( SubType == 117 )
					return (float) (this.BinToInt( Bin.substring( 424 , 456 ) )/1000.0);
			}
			return -1;
		}catch( Exception e)
		{
			System.err.println("Exception: TTPDU: getVoltajeAlimentacion: " + e.toString() );
			return -1;
		}
	}
	
	@Override
	public int getPuntoVisita() {
		// TODO Auto-generated method stub
		return -1;
	}

	@Override
	public int getPuntoVisitaEstado() {
		// TODO Auto-generated method stub
		return -1;
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
	
	public String getDriverID()
	{
		
		return null;
	}
	
	//OBDII
	
	public float VelocidadOBD() 
	{
		try{
			if( Type == 1 && SubType == 189)
			{

				long vLONG=0;
				
				
				vLONG = this.BinToLong( Bin.substring( 936 , 968 ) );
										
				if( vLONG > Long.valueOf( "2147483647" ) )
				{
					long f = Long.valueOf("4294967296");
					vLONG = f - vLONG;
					vLONG = vLONG * -1;
				}						
			    return (float) ( vLONG * 0.0223693629 );		
			
			}
			return -1;
		}catch( Exception e)
		{
			System.err.println("CLMPPDU_PE: VelocidadOBD: " + e.toString() );
			return -1;
			
		}finally
		{
			
		}
		
	}

	@Override
	public int rpmOBD()
	{
		try{
			if( Type == 1 && SubType == 189)
			{
				long vLONG=0;
								
				vLONG = this.BinToLong( Bin.substring( 968 , 1000 ) );
										
				if( vLONG > Long.valueOf( "2147483647" ) )
				{
					long f = Long.valueOf("4294967296");
					vLONG = f - vLONG;
					vLONG = vLONG * -1;
				}						
			    return  (int)( vLONG / 10 );		
			
			}
			return -1;
		}catch( Exception e)
		{
			System.err.println("CLMPPDU_PE: rpmOBD: " + e.toString() );
			return -1;
			
		}finally
		{
			
		}
	}

	@Override
	public int PosicionAcceleradorOBD() 
	{
		try{
			if( Type == 1 && SubType == 189)
			{
				long vLONG=0;
								
				vLONG = this.BinToLong( Bin.substring( 1000 , 1032 ) );
										
				if( vLONG > Long.valueOf( "2147483647" ) )
				{
					long f = Long.valueOf("4294967296");
					vLONG = f - vLONG;
					vLONG = vLONG * -1;
				}						
			    return  (int)( vLONG / 100 );		
			
			}
			return -1;
		}catch( Exception e)
		{
			System.err.println("CLMPPDU_PE: PosicionAceleradorOBD: " + e.toString() );
			return -1;
			
		}finally
		{
			
		}
	}

	@Override
	public int OdometroOBD() 
	{
		try{
			if( Type == 1 && SubType == 189)
			{
				long vLONG=0;
								
				vLONG = this.BinToLong( Bin.substring( 1184 , 1224 ) );
										
				if( vLONG > Long.valueOf( "2147483647" ) )
				{
					long f = Long.valueOf("4294967296");
					vLONG = f - vLONG;
					vLONG = vLONG * -1;
				}						
			    return  (int)vLONG;		
			
			}
			return -1;
		}catch( Exception e)
		{
			System.err.println("CLMPPDU_PE: OdometroOBD: " + e.toString() );
			return -1;
			
		}finally
		{
			
		}
	}

	@Override
	public int OdometroViajeOBD() 
	{
		try{
			if( Type == 1 && SubType == 189)
			{
				long vLONG=0;
								
				vLONG = this.BinToLong( Bin.substring( 1288 , 1320 ) );
										
				if( vLONG > Long.valueOf( "2147483647" ) )
				{
					long f = Long.valueOf("4294967296");
					vLONG = f - vLONG;
					vLONG = vLONG * -1;
				}						
			    return  (int)vLONG;		
			
			}
			return -1;
		}catch( Exception e)
		{
			System.err.println("CLMPPDU_PE: OdometroViajeOBD: " + e.toString() );
			return -1;
			
		}finally
		{
			
		}
	}

	@Override
	public float NivelGasolinaOBD() 
	{
		try{
			if( Type == 1 && SubType == 189)
			{
				long vLONG=0;
								
				vLONG = this.BinToLong( Bin.substring( 1064 , 1096 ) );
										
				if( vLONG > Long.valueOf( "2147483647" ) )
				{
					long f = Long.valueOf("4294967296");
					vLONG = f - vLONG;
					vLONG = vLONG * -1;
				}						
			    return  (float)(vLONG/100);		
			
			}
			return -1;
		}catch( Exception e)
		{
			System.err.println("CLMPPDU_PE: NivelGasolinaOBD: " + e.toString() );
			return -1;
			
		}finally
		{
			
		}
	}

	@Override
	public float CombustibleRestanteOBD()
	{
		try{
			if( Type == 1 && SubType == 189)
			{
				long vLONG=0;
								
				vLONG = this.BinToLong( Bin.substring( 1096 , 1128 ) );
										
				if( vLONG > Long.valueOf( "2147483647" ) )
				{
					long f = Long.valueOf("4294967296");
					vLONG = f - vLONG;
					vLONG = vLONG * -1;
				}						
			    return  (float)(vLONG/1000);		
			
			}
			return -1;
		}catch( Exception e)
		{
			System.err.println("CLMPPDU_PE: CombustibleRestanteOBD: " + e.toString() );
			return -1;
			
		}finally
		{
			
		}
	}

	@Override
	public int EngraneTransmisionOBD()
	{
		try{
			if( Type == 1 && SubType == 189)
			{
				long vLONG=0;
								
				vLONG = this.BinToLong( Bin.substring( 1128 , 1160 ) );
										
				if( vLONG > Long.valueOf( "2147483647" ) )
				{
					long f = Long.valueOf("4294967296");
					vLONG = f - vLONG;
					vLONG = vLONG * -1;
				}						
			    return  (int)(vLONG);		
			
			}
			return -1;
		}catch( Exception e)
		{
			System.err.println("CLMPPDU_PE: EngraneTransmisionOBD: " + e.toString() );
			return -1;
			
		}finally
		{
			
		}
	}

	@Override
	public float TemperaturaRefrigeranteOBD() 
	{
		try{
			if( Type == 1 && SubType == 189)
			{
				long vLONG=0;
								
				vLONG = this.BinToLong( Bin.substring( 1128 , 1160 ) );
										
				if( vLONG > Long.valueOf( "2147483647" ) )
				{
					long f = Long.valueOf("4294967296");
					vLONG = f - vLONG;
					vLONG = vLONG * -1;
				}						
			    return  (int)(vLONG*16);		
			
			}
			return -1;
		}catch( Exception e)
		{
			System.err.println("CLMPPDU_PE: EngraneTransmisionOBD: " + e.toString() );
			return -1;
			
		}finally
		{
			
		}
	}

	@Override
	public float IndiceGasolinaOBD()
	{
		try{
			if( Type == 1 && SubType == 189)
			{
				long vLONG=0;
								
				vLONG = this.BinToLong( Bin.substring( 1192 , 1224 ) );
										
				if( vLONG > Long.valueOf( "2147483647" ) )
				{
					long f = Long.valueOf("4294967296");
					vLONG = f - vLONG;
					vLONG = vLONG * -1;
				}						
			    return  (float)(vLONG/1000);		
			
			}
			return -1;
		}catch( Exception e)
		{
			System.err.println("CLMPPDU_PE: IndiceGasolinaOBD: " + e.toString() );
			return -1;
			
		}finally
		{
			
		}
	}

	@Override
	public float VoltajeAlimentacionOBD()
	{
		try{
			if( Type == 1 && SubType == 189)
			{
				long vLONG=0;
								
				vLONG = this.BinToLong( Bin.substring( 1224 , 1256 ) );
										
				if( vLONG > Long.valueOf( "2147483647" ) )
				{
					long f = Long.valueOf("4294967296");
					vLONG = f - vLONG;
					vLONG = vLONG * -1;
				}						
			    return  (float)(vLONG/1000);		
			
			}
			return -1;
		}catch( Exception e)
		{
			System.err.println("CLMPPDU_PE: VoltajeBateriaOBD: " + e.toString() );
			return -1;
			
		}finally
		{
			
		}
	}

	@Override
	public int EstadoSeñalesGiroOBD() 
	{
		try{
			if( Type == 1 && SubType == 189)
			{
				long vLONG=0;
								
				vLONG = this.BinToLong( Bin.substring( 1256 , 1288 ) );
										
				if( vLONG > Long.valueOf( "2147483647" ) )
				{
					long f = Long.valueOf("4294967296");
					vLONG = f - vLONG;
					vLONG = vLONG * -1;
				}						
			    return  (int)(vLONG);		
			
			}
			return -1;
		}catch( Exception e)
		{
			System.err.println("CLMPPDU_PE: EstadoSeñalesGiroOBD: " + e.toString() );
			return -1;
			
		}finally
		{
			
		}
	}

	@Override
	public float GasolinaConsumidaPorViaje() 
	{
		try{
			if( Type == 1 && SubType == 189)
			{
				long vLONG=0;
								
				vLONG = this.BinToLong( Bin.substring( 1320 , 1352 ) );
										
				if( vLONG > Long.valueOf( "2147483647" ) )
				{
					long f = Long.valueOf("4294967296");
					vLONG = f - vLONG;
					vLONG = vLONG * -1;
				}						
			    return  (float)(vLONG/1000);		
			
			}
			return -1;
		}catch( Exception e)
		{
			System.err.println("CLMPPDU_PE: GasolinaConsumidaPorViajeOBD: " + e.toString() );
			return -1;
			
		}finally
		{
			
		}
	}

	@Override
	public String IndicadoresOBD() 
	{
		try{
			if( Type == 1 && SubType == 189)
			{
				return Bin.substring( 1368 , 1384 ) + Bin.substring( 1400 , 1416 );
			}
			return null;
		}catch( Exception e)
		{
			System.err.println("CLMPPDU_PE: IndicadoresOBD: " + e.toString() );
			return null;
			
		}finally
		{
			
		}
	}

	@Override
	public float getADC1() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getADC2() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getBatteryLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getTemperatura1() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTemperatura2() {
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
