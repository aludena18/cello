//V1.0.0

package PDULibrary;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class CELLOPDU implements PDU
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
		try
		{
			if( Calle != null )
				Calle = Calle.replace( '\'', ' ' );
		}catch( Exception e)
		{

		}
		CCalle = Calle; 
	}


	//PROPIEDADES
	private String FechaHora;
	private String ModemID;
	private String EventoEntrada;
	private String Latitud;
	private String Longitud;
	private String Odometro;
	private String Velocidad;
	private String MaxVelocidad;

	//Obtener Propiedades
	public synchronized String getReportDateTime()
	{
		return FechaHora;
	}
	public synchronized String getModemID()
	{
		return ModemID;
	}
	public synchronized String getInputEvent()
	{
		return EventoEntrada;
	}
	public synchronized String getLatitude()
	{
		return Latitud;
	}
	public synchronized String getLongitude()
	{
		return Longitud;
	}
	public synchronized String getOdometer()
	{
		return Odometro;
	}
	public synchronized String getSpeed()
	{
		return Velocidad;
	}
	public synchronized String getMaxSpeed()
	{
		return MaxVelocidad;
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


			if( getGPSStatus().compareTo("1") == 0 && ( getType() == 1 || getType() == 2 ) )
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
			System.err.println("ERROR: CLMPPDU: ReportDateTime:" + e.toString());

			return null;
		}
	}


	private synchronized String proc_ReportDateTime()
	{
		try{
			if( getGPSStatus().compareTo("1") == 0 && ( getType() == 0 || getType() == 11 ))
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
			System.err.println("ERROR: CELLOPDU: ReportDateTime:" + e.toString());

			return null;
		}
	}

	public synchronized String getReportDateTimeRTC()
	{
		try{
			long Segundos; GregorianCalendar C;SimpleDateFormat sdf;

			if( ( Type == 1 && (SubType == 117 || SubType == 121 || SubType == 189 || SubType == 81 || SubType == 137) || getType() == 2 ) )
			{ 
				Segundos = JENR.BinToLong( Bin.substring( 104 , 136 ) );
				C = new GregorianCalendar();
				C.set( 1970 , Calendar.JANUARY , 1 , 0, 0 , 0 );
				C.add(Calendar.SECOND, (int)Segundos);
				sdf = new SimpleDateFormat("yyyMMdd HH:mm:ss");
				return sdf.format( C.getTime());
			}
			return null;
		}catch( Exception e)
		{
			System.err.println("ERROR: CLMPPDU: ReportDateTimeRTC: " + e.toString());
			return null;
		}

	}

	public synchronized String getReportDateTimeGPS()
	{
		try{
			if( Type == 1  || Type == 2)
			{
				return "20" + getGPSDate().substring(4,6) + getGPSDate().substring(2,4) + getGPSDate().substring(0, 2) + 

						" " + getGPSTime().substring(0,2) + ":" + getGPSTime().substring(2,4) + ":" + getGPSTime().substring(4,6);
			}
			else
				return null;

		}catch( Exception e)
		{
			System.err.println("ERROR: CLMPPDU: ReportDateTimeGPS: " + e.toString());
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

	private void IdentifyType()	//ALG
	{
		/*TIPO DE TRAMA CELLO
		 */ 
		try {
			int tipo = B_PDU[4];
			switch (tipo) {
			case 0:
				Type = 0;
				SubType = B_PDU.length;
				break;
			case 11:
				Type = 11;
				SubType = B_PDU.length;
				break;
			default:
				break;
			}

		} catch (Exception e) {
			// TODO: handle exception
			System.err.println( "ERROR: CLMPAPI: IdentifyType: " + e.toString() );
			Type = 99;
		}
	}


	//CONSTRUCTORES
	public CELLOPDU( DatagramPacket tmpPDU, DatagramSocket UDPServer, String IPServidor )
	{
		DateTimeReceived = System.currentTimeMillis();
		//System.out.println("CLMPPDU.java");
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

	public CELLOPDU( DatagramPacket tmpPDU )
	{
		DateTimeReceived = System.currentTimeMillis();
		InitPDU( tmpPDU );

	}

	public CELLOPDU( byte[] tmpPDU )
	{
		try{
			Time = System.currentTimeMillis(); 
			DateTimeReceived = System.currentTimeMillis();

			B_PDU = tmpPDU;
			Hex = JENR.ByteArrayToHexString( B_PDU);
			Bin = JENR.HexToBin(Hex);

			this.IdentifyType();

		}catch( Exception e )
		{
			System.err.println("ERROR: CLMPPDU: CLMPPDU: " + e.toString() );
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

			Hex = JENR.ByteArrayToHexString(B_PDU);
			Bin = JENR.HexToBin(Hex);


			System.out.println("\nTrama Hexadecimal = " + Hex);
			System.out.print(" / Length=" + B_PDU.length);
			System.out.println(" / Tipo=" + (int)B_PDU[4]);

			try{

				IP = tmpPDU.getAddress().toString();
				Puerto= tmpPDU.getPort();

			}catch( Exception e)
			{

			}

			this.IdentifyType();

			try{
				int suma = 0;
				byte checkSum = 0;

				switch (Type) {
				case 0:
					suma = 4 + (int)(B_PDU[5]&0xFF) + (int)(B_PDU[6]&0xFF) + (int)B_PDU[7] +
					(int)B_PDU[8] + (int)B_PDU[11];
					checkSum = (byte)(suma-256);

					ACK = new byte[28];
					ACK[0] = B_PDU[0];
					ACK[1] = B_PDU[1];
					ACK[2] = B_PDU[2];
					ACK[3] = B_PDU[3];
					ACK[4] = 4;
					ACK[5] = B_PDU[5];
					ACK[6] = B_PDU[6];
					ACK[7] = B_PDU[7];
					ACK[8] = B_PDU[8];
					ACK[9] = 0;
					ACK[10] = 0;
					ACK[11] = 0;
					ACK[12] = 0;
					ACK[13] = 0;
					ACK[14] = 0;
					ACK[15] = B_PDU[11];
					ACK[16] = 0;
					ACK[17] = 0;
					ACK[18] = 0;
					ACK[19] = 0;
					ACK[20] = 0;
					ACK[21] = 0;
					ACK[22] = 0;
					ACK[23] = 0;
					ACK[24] = 0;
					ACK[25] = 0;
					ACK[26] = 0;
					ACK[27] = checkSum;

					break;

				case 11:
					suma = 11 + (int)(B_PDU[5]&0xFF) + (int)(B_PDU[6]&0xFF) + (int)B_PDU[7] +
					(int)B_PDU[8] + (int)B_PDU[11] + (int)0x80 + (int)0x0A + 9 + 3;
					checkSum = (byte)(suma-256);

					ACK = new byte[28];
					ACK[0] = B_PDU[0];
					ACK[1] = B_PDU[1];
					ACK[2] = B_PDU[2];
					ACK[3] = B_PDU[3];
					ACK[4] = (byte)0x0B;
					ACK[5] = B_PDU[5];
					ACK[6] = B_PDU[6];
					ACK[7] = B_PDU[7];
					ACK[8] = B_PDU[8];
					ACK[9] = B_PDU[11];
					ACK[10] = 0;
					ACK[11] = 0;
					ACK[12] = 0;
					ACK[13] = 0;
					ACK[14] = (byte)0x80;
					ACK[15] = (byte)0x0A;
					ACK[16] = 0;
					ACK[17] = 0;
					ACK[18] = 0;
					ACK[19] = 0;
					ACK[20] = 0;
					ACK[21] = 9;
					ACK[22] = 3;
					ACK[23] = 0;
					ACK[24] = 0;
					ACK[25] = 0;
					ACK[26] = 0;
					ACK[27] = checkSum;

					break;

				default:
					ACK=null;
					break;
				}

			}catch( Exception e2 )
			{
				ACK=null;
			}

			//Establecer Propiedades
			FechaHora = this.proc_ReportDateTime();
			ModemID = this.proc_ModemID();
			EventoEntrada = this.proc_InputEvent();
			Latitud = this.proc_Latitude();
			Longitud = this.proc_Longitude();
			Odometro = this.proc_Odometer();
			Velocidad = this.proc_Speed();
			MaxVelocidad = this.proc_MaxSpeed();

		}catch( Exception e)
		{
			System.err.println("ERROR: CLMPPDU: InitPDU: " + e.toString() );
		}
	}


	//FUNCIONES
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

	public synchronized String getBin()
	{
		return Bin;
	}

	public synchronized String getHex()
	{
		return Hex;
	}

	@Override
	public String getS_PDU() 
	{
		// TODO Auto-generated method stub
		return S_PDU;
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
		if( ACK != null){
			System.out.println("ACK = " + JENR.ByteArrayToHexString(ACK));
			return ACK;
		}
		return null;
	}




	public synchronized String getUDPAPINumber()
	{
		/*
		 * EQUIVALENTE A TIPO DE MENSAJE
		 * 
		 */
		try{
			if( (Type == 1 && (SubType == 117 || SubType == 121 || SubType == 189 || SubType == 81 || B_PDU.length == 137  ) ) || Type == 2)
				return "" + (int)B_PDU[10];
			return null;

		}catch( Exception e)
		{
			System.err.println("ERROR: CLMPPDU: getUDPAPINumber: " + e.toString() );
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
			if( ( Type == 1 && (SubType == 117 || SubType == 121 || SubType == 189 || SubType == 81 || B_PDU.length == 137 ) ) || Type == 2)
				return "" + B_PDU[9];
			return null; 
		}catch( Exception e)
		{			
			System.err.println("ERROR: CLMPPDU: getCommandType: " + e.toString() );
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
					//System.out.println("Input Event = " + Bin.substring(400, 408));
					return "" + JENR.BinToLong(Bin.substring(400,408));
				}catch( Exception e)
				{
					return "";
				}
			}
			else if( Type == 1 && ( SubType == 81 || B_PDU.length == 137 ) )
			{
				//PARA DAR SOPORTE A DATOS APP MSG DE TIPO JPOD CL7, CL9,CL11 : SUBTYPE = 81
				//PARA DAR SOPORTE A DATOS APP MSG DE TIPO JPOD CL6 : SUBTYPE = 137
				try{
					return "" + ( 1000 + JENR.BinToLong(Bin.substring(424,432)) );
				}catch( Exception e)
				{
					return "";
				}
			}
			else if( Type == 2)
			{
				try{
					return "" + JENR.BinToLong(Bin.substring(392,408)) + "000";
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
			System.err.println( "ERROR: CLMPPDU: getUserSpecifiedNumber: " + e.toString() );
			return "";
		}
	}	

	private synchronized String proc_ModemID()
	{
		try{
			String ModemID="";

			if( Type == 0 || Type == 11)
			{ 
				//EQUIVALENTE A MobileID
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
			System.err.println("ERROR: CLMPPDU: getModemID: " + e.toString() );
			return null;
		}

	}

	public synchronized String getIO_CFG()
	{
		try{
			return "";
		}catch( Exception e)
		{
			System.err.println("ERROR: CLMPPDU: getIO_CFG: " + e.toString() );
			return "";
		}
	}

	public synchronized String getIO_STATE()
	{
		String IO_STATE;
		try{
			if( Type == 1 && (SubType == 117 || SubType == 121 ) )
			{
				IO_STATE = Bin.substring( 840 , 872);
				return IO_STATE.substring( IO_STATE.length() - 12 );

			}
			else if ( Type == 1 && SubType == 189 )
			{
				IO_STATE = Bin.substring( 904 , 936);
				return IO_STATE.substring( IO_STATE.length() - 12 );

			}
			else if ( Type == 1 && ( SubType == 81 || B_PDU.length == 137 ))
			{
				IO_STATE = Bin.substring( 376 , 384);
				return IO_STATE;

			}
			return "";
		}catch( Exception e)
		{
			System.err.println("ERROR: CLMPPDU: getIO_STATE: " + e.toString() );
			return "";
		}

	}

	private synchronized String proc_InputEvent()
	{
		try{

			if( ( Type == 1 && (SubType == 117 || SubType == 121 || SubType == 189 || SubType == 81 || B_PDU.length == 137 ) ) || Type == 2)
			{ 
				return this.getUserSpecifiedNumber();
			}
			return null;

		}catch( Exception e)
		{
			System.err.println("ERROR: CLMPPDU: getInputEvent: " + e.toString() );
			return null;
		}

	}

	public synchronized String getGPSDate()	//ALG
	{
		try{
			//long Segundos; GregorianCalendar C;SimpleDateFormat sdf;
			
			//**Format ddMMyy**

			if(Type == 0){
				int d = B_PDU[65];
				int M = B_PDU[66];
				int y = B_PDU[68]*256 + B_PDU[67];
				
				String dd = "", MM = "", yy = "";
				
				if(d<10) dd = "" + "0" + d;
				else dd = "" + d;
				
				if(M<10) MM = "" + "0" + M;
				else MM = "" + M; 
				
				yy = "" + y;
				
				return "" + dd + MM + yy.substring(2);
			}
			else if(Type == 11 ){ 
				try{
					/*Segundos = JENR.BinToLong( Bin.substring( 136 , 168 ) );
					C = new GregorianCalendar();
					C.set( 1970 , Calendar.JANUARY , 1 , 0, 0 , 0 );
					C.add(Calendar.SECOND, (int)Segundos);
					sdf = new SimpleDateFormat("ddMMyy");
					return sdf.format( C.getTime());
					 */
					
					int d = B_PDU[57];
					int M = B_PDU[58];
					int y = B_PDU[59];
					
					String dd = "", MM = "", yy = "";
					
					if(d<10) dd = "" + "0" + d;
					else dd = "" + d;
					
					if(M<10) MM = "" + "0" + M;
					else MM = "" + M; 
					
					if(y<10) yy = "" + "0" + y;
					else yy = "" + y;
					
					return "" + dd + MM + yy;
					

				}catch( Exception e)
				{
					System.err.println( "ERROR: CELLOPDU: getGPSDate: " + e.toString() );
					return "";
				}
			}
			return "";
		}catch( Exception e)
		{
			System.err.println("ERROR: CELLOPDU: getGPSDate: " + e.toString() );
			return null;
		}		
	}

	public synchronized String getGPSTime()
	{
		try{
			long Segundos; GregorianCalendar C;SimpleDateFormat sdf;

			if( ( Type == 1 && (SubType == 117 || SubType == 121 || SubType == 189 || SubType == 81 || B_PDU.length == 137 )  ) || Type == 2)
			{ 
				Segundos = JENR.BinToLong( Bin.substring( 136 , 168 ) );
				C = new GregorianCalendar();
				C.set( 1970 , Calendar.JANUARY , 1 , 0, 0 , 0 );
				C.add(Calendar.SECOND, (int)Segundos);
				sdf = new SimpleDateFormat("HHmmss");
				return sdf.format( C.getTime());
			}
			return "";

		}catch( Exception e)
		{
			System.err.println( "ERROR: CLMPPDU: getGPSTime: " + e.toString() );
			return "";
		}

	}

	public synchronized String getGPSStatus()	//ALG
	{
		try{
			if(Type == 0){
				if( B_PDU[41] > 2 )
					return "1";
				else 
					return "9";
				//return "";
			}
			else if(Type == 11){ 
				if( B_PDU[32] > 2 )
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
			System.err.println( "ERROR: CELLOPDU: getGPSStatus: " + e.toString() );
			System.err.println( "ERROR: CELLOPDU: getGPSStatus: " + Type + " " + SubType );

			return "";
		}
	}

	private synchronized String proc_Latitude()
	{
		try{
			long LatitudeLONG=0;
			long f= Long.valueOf( "4294967296" );
			float cordenada=0;

			if( ( Type == 1 && (SubType == 117 || SubType == 121 || SubType == 189 || SubType == 81 || B_PDU.length == 137 ) ) || Type == 2 )
			{ 
				try{

					LatitudeLONG = JENR.BinToLong( Bin.substring( 168 , 200 ) );

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

	private synchronized String proc_Longitude()
	{
		try{
			long LongitudeLONG=0;
			long f = Long.valueOf("4294967296");

			float cordenada=0;

			if( ( Type == 1 && (SubType == 117 || SubType == 121 || SubType == 189 || SubType == 81 || B_PDU.length == 137 ) ) || Type == 2 )
			{ 
				LongitudeLONG = JENR.BinToLong( Bin.substring( 200 , 232 ) );

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

	private synchronized String proc_Speed()
	{
		//SE CONVIERTE DE CM/S A MILLAS
		//MILLAS
		try{
			if( ( Type == 1 && (SubType == 117 || SubType == 121 || SubType == 189 || SubType == 81 || B_PDU.length == 137 ) ) || Type == 2 )
			{ 
				long SpeedLONG=0;
				long f = Long.valueOf("4294967296");		//hex -->  1 0000 0000


				int inputEvent = Integer.parseInt(this.getInputEvent());
				if(inputEvent == 130){
					/*
					 * SI EL EVENTO ES 130 (EVENTO DE EXCESO DE VELOCIDAD CON TIEMPO DE PERMANENCIA),
					 * LA VELOCIDAD QUE REPORTARA SERA LA VELOCIDAD PICO DEL EXCESO
					 * DEBIDO A Q LAS ALERTAS Y REPORTES EN EL GEOSYS NO COINCIDEN EN NUMERO
					 * ABEL LUDEÑA
					 */
					SpeedLONG = JENR.BinToLong(Bin.substring(968, 1000));	//ACC 15
				}
				else{
					SpeedLONG = JENR.BinToLong( Bin.substring( 264 , 296 ) );
				}


				if( SpeedLONG > Long.valueOf( "2147483647" ) )	// hex --> 7FFF FFFF
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
			System.err.println("ERROR: CLMPPDU: getSpeed: " + e.toString() );
			return null;
		}
	}

	private synchronized String proc_MaxSpeed() {
		// ABEL -- EN EL ACUMULADOR 15 SE GUARDA EL PICO DEL EXCESO DE VELOCIDAD
		try {
			if( ( Type == 1 && (SubType == 117 || SubType == 121 || SubType == 189 || SubType == 81 || B_PDU.length == 137 ) ) || Type == 2 ){
				try{					
					if (SubType == 117) {
						return "-1";
					}
					if (SubType == 189) {
						long maxSpeedLONG=0;
						long f = Long.valueOf("4294967296");
						maxSpeedLONG = JENR.BinToLong(Bin.substring(968, 1000));	//ACC 15
						//System.out.println("MAX SPEED BIN TO LONG = " + maxSpeedLONG);

						if( maxSpeedLONG > Long.valueOf( "2147483647" ) )
						{
							maxSpeedLONG = f - maxSpeedLONG;
							maxSpeedLONG = maxSpeedLONG * -1;
						}
						return "" + ( maxSpeedLONG * 0.0223693596 );
					}
					else return "-1";
				}catch( Exception e)
				{ return "-1"; }
			}
			else return "-1";

		} catch (Exception e) {
			System.err.println("ERROR: CLMPAPI: get MaxSpeed: " + e.toString());
			return "-1";
		}
	}

	public synchronized String getHeading()
	{
		try{
			if( ( Type == 1 && (SubType == 117 || SubType == 121 || SubType == 189 || SubType == 81 || B_PDU.length == 137 ) ) || Type == 2)
			{ 
				try{

					return "" + (int)( (JENR.BinToInt( Bin.substring( 296 , 312) )));


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
			System.err.println("ERROR: CLMPPDU: getHeading: " + e.toString() );
			return null;
		}

	}

	public synchronized String getAltitude()
	{
		try{
			if( ( Type == 1 && (SubType == 117 || SubType == 121 || SubType == 189 || SubType == 81 || B_PDU.length == 137 ) ) || Type == 2 )
			{ 
				long AltitudeLONG=0;
				long f = Long.valueOf("4294967296");

				AltitudeLONG = JENR.BinToLong( Bin.substring( 232 , 264 ) );

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
			System.err.println("ERROR: CLMPPDU: getAltitude: " + e.toString() );
			return null;
		}	
	}

	public synchronized String getNumberOfSatellites()
	{
		try{	
			if( ( Type == 1 && (SubType == 117 || SubType == 121 || SubType == 189 || SubType == 81 || B_PDU.length == 137 ) ) || Type == 2 )
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

	private synchronized String proc_Odometer()
	{
		try{
			if( Type == 1 )
			{ 
				if( SubType == 117 )
				{
					long OdometroLONG=0;
					OdometroLONG = JENR.BinToLong( Bin.substring( 520 , 552 ) );
					return "" + (OdometroLONG);
				}
				else if( SubType == 121 )
				{
					long OdometroLONG=0;
					OdometroLONG = JENR.BinToLong( Bin.substring( 776 , 808  ) );
					return "" + (OdometroLONG);
				}
				else if( SubType == 189 )
				{
					long OdometroLONG=0;
					OdometroLONG = JENR.BinToLong( Bin.substring( 584 , 616  ) );
					return "" + (OdometroLONG);
				}


			}
			return null;			
		}catch( Exception e )
		{
			System.err.println( "ERROR: CLMPPDU: getOdometer: " + e.toString() );
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
					HorometroLONG = JENR.BinToLong( Bin.substring( 872,904 ) );
					return "" + (HorometroLONG);

				}
				else if( SubType == 121 )
				{
					long HorometroLONG=0;
					HorometroLONG = JENR.BinToLong( Bin.substring( 936 ) );
					return "" + (HorometroLONG);
				}
				if( SubType == 189 )
				{
					long HorometroLONG=0;
					HorometroLONG = JENR.BinToLong( Bin.substring( 840,872 ) );
					return "" + (HorometroLONG);

				}

			}
			return null;			
		}catch( Exception e )
		{
			System.err.println( "ERROR: CLMPPDU: getOdometer: " + e.toString() );
			return null;
		}
	}

	public synchronized String getRTC()
	{	
		try{	
			long Segundos; GregorianCalendar C;SimpleDateFormat sdf;

			if( Type == 0 ){
				return "";
			}
			else if(Type == 11){ 
				try{
					/*Segundos = JENR.BinToLong( Bin.substring( 104 , 136 ) );
					C = new GregorianCalendar();
					C.set( 1970 , Calendar.JANUARY , 1 , 0, 0 , 0 );
					C.add(Calendar.SECOND, (int)Segundos);
					sdf = new SimpleDateFormat("yyyyMMddHHmmss");
					return sdf.format( C.getTime());
					 */
					return "" + "20" + B_PDU[59] + "" + B_PDU[58] + "" + B_PDU[57] + "" + 
					B_PDU[56] + "" + B_PDU[55] + "" + B_PDU[54];

				}catch( Exception e)
				{
					System.err.println( "ERROR: CELLOPDU: getRTC: " + e.toString() );
					return "";
				}
			}
			return "";


		}catch( Exception e)
		{
			System.err.println("ERROR: CELLOPDU: getRTC: " + e.toString() );
			return null;
		}

	}

	public synchronized String getDateTimeSQL()
	{	
		String dt,d,t;
		Calendar C;

		try{
			if( Type == 1  || Type == 2)
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
			System.err.println( "ERROR: CLMPPDU: getDateTimeSQL: " + e.toString() );
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
			System.err.println("ERROR: CLMPPDU: getCMDResponse: " + e.toString() );
			return null;
		}
	}

	public synchronized String ToString()
	{
		try{
			String C="";

			if ( Type == 1 || Type == 2)
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
			System.err.println("ERROR: CLMPPDU: toString: " + e.toString() );
			return null;
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
			if( ( Type == 1 && (SubType == 117 || SubType == 121 || SubType == 189 || SubType == 81 || B_PDU.length == 137 ) ) || Type == 2  )
				return true;
			else 
				return false;
		}catch( Exception e)
		{
			System.err.println("ERROR: CLMPPDU: isPDUofGPSDATA: " + e.toString() );
			return false;
		}

	}

	public synchronized boolean isCMDResponse()
	{
		if( Type == 12 || Type == 2 || ( Type == 1 && SubType == 137 ) )
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
			l_Variable = JENR.BinToLong( Variable_Bin );

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
					return (float) (JENR.BinToInt( Bin.substring( 456 , 488 ) ) / 1000.0);
				else if( SubType == 121)
					return (float) (JENR.BinToInt( Bin.substring( 712 , 744 ) ) / 1000.0);
				else if( SubType == 189 )
					return (float) (JENR.BinToInt( Bin.substring( 520 , 552 ) ) / 1000.0);
				/*
				else if( SubType == 81 && B_PDU[53] == 45 )
					return (float) (this.BinToInt( Bin.substring( 536 , 552 ) ) * 0.05 );
				 */
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

			if( Type == 1 && SubType == 189)
			{
				String Lvalue;
				Long vLONG;

				Lvalue = "" + (char)B_PDU[177] + (char)B_PDU[178] + (char)B_PDU[179] + (char)B_PDU[180];

				try  
				{  
					vLONG = Long.parseLong( Lvalue ); 
				}catch(NumberFormatException nfe)  
				{  
					return -1;  
				}  


				return (float)vLONG;
			}
			else
				return -1;
		}catch( Exception e)
		{
			System.err.println("Exception: CLMPAPI: getEA2: "  + e.toString());
			return -1;
		}
	}

	@Override
	public float getEA3() 
	{
		try{

			float v=0;
			long vLONG=0;
			if( Type == 1 )
			{
				if( SubType == 117 || SubType == 121 )
				{					
					vLONG = JENR.BinToLong( Bin.substring( 584 , 616 ) );

					if( vLONG > Long.valueOf( "2147483647" ) )
					{
						long f = Long.valueOf("4294967296");
						vLONG = f - vLONG;
						vLONG = vLONG * -1;
					}						
					v=(float)(vLONG/16);

					/*
				    if( v== 0 )
				    	return -1;
				    else
				    	return v;
					 */	
				}
				else if( SubType == 189 )
				{
					vLONG = JENR.BinToLong( Bin.substring( 648 , 680 ) );

					if( vLONG > Long.valueOf( "2147483647" ) )
					{
						long f = Long.valueOf("4294967296");
						vLONG = f - vLONG;
						vLONG = vLONG * -1;
					}						
					v=  (float)(vLONG/16.0);


				}
				if( v == 0 )
					return -1;
				else
					return v;

			}
			return -1;
		}catch( Exception e)
		{
			System.err.println( "Exception: CLMPPAPI: EA3: " + e.toString() );
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
					return (float) (JENR.BinToInt( Bin.substring( 424 , 456 ) )/1000.0);
				else if(SubType == 189 )
					return (float) (JENR.BinToInt( Bin.substring( 488 , 520 ) )/1000.0);
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

		try{


			long vLONG=0;
			if( Type == 1 )
			{
				if( SubType == 117 || SubType == 121 )
				{



					vLONG = JENR.BinToLong( Bin.substring( 648 , 680 ) );

					if( vLONG > Long.valueOf( "2147483647" ) )
					{
						long f = Long.valueOf("4294967296");
						vLONG = f - vLONG;
						vLONG = vLONG * -1;
					}						


				}
				else if( SubType == 189 )
				{
					vLONG = JENR.BinToLong( Bin.substring( 712 , 744 ) );

					if( vLONG > Long.valueOf( "2147483647" ) )
					{
						long f = Long.valueOf("4294967296");
						vLONG = f - vLONG;
						vLONG = vLONG * -1;
					}						



				}
				if( vLONG == 0 )
					return null;
				else
					return Long.toHexString( vLONG );

			}
			return null;
		}catch( Exception e)
		{
			System.err.println( "Exception: TTAPI: DriverID: " + e.toString() );
			return null;
		}
	}

	//OBDII

	public float VelocidadOBD() 
	{
		try{
			float vLONG=0;

			if( Type == 1 && SubType == 189)
			{
				vLONG = JENR.BinToLong( Bin.substring( 936 , 968 ) );

				if( vLONG > Long.valueOf( "2147483647" ) )
				{
					long f = Long.valueOf("4294967296");
					vLONG = f - vLONG;
					vLONG = vLONG * -1;
				}						
				return (float) ( vLONG * 0.0223693629 );		

			}
			else if ( Type == 1 && SubType == 137 )
			{
				vLONG = JENR.BinToLong( Bin.substring( 656 , 664) );

				return (float) ( ( vLONG * 0.005 ) / 0.6213712 );

			}
			return -1;
		}catch( Exception e)
		{
			System.err.println("CLMPPDU: VelocidadOBD: " + e.toString() );
			return -1;

		}finally
		{

		}

	}

	@Override
	public int rpmOBD()
	{
		try{			
			long vLONG=0;

			if( Type == 1 && SubType == 189)
			{


				vLONG = JENR.BinToLong( Bin.substring( 968 , 1000 ) );

				if( vLONG > Long.valueOf( "2147483647" ) )
				{
					long f = Long.valueOf("4294967296");
					vLONG = f - vLONG;
					vLONG = vLONG * -1;
				}						
				return  (int)( vLONG / 10 );		

			}
			else if ( Type == 1 && SubType == 137 )
			{
				//vLONG = this.BinToLong( Bin.substring( 624 , 640 ) );
				vLONG = Long.parseLong( Hex.substring(158,160) + Hex.substring(156,158) , 16 );
				if( vLONG != 0 )
				{
					return (int) ( vLONG * 0.25 );					
				}
				else
				{
					//vLONG = this.BinToLong( Bin.substring( 640 , 656 ) );
					vLONG = Long.parseLong( Hex.substring(162,164) + Hex.substring(160,162), 16 );
					return (int) ( vLONG * 0.125 );
				}				
			}
			return -1;
		}catch( Exception e)
		{
			System.err.println("CLMPPDU: rpmOBD: " + e.toString() );
			return -1;

		}finally
		{

		}
	}

	@Override
	public int PosicionAcceleradorOBD() 
	{
		try{
			long vLONG=0;
			if( Type == 1 && SubType == 189)
			{


				vLONG = JENR.BinToLong( Bin.substring( 1000 , 1032 ) );

				if( vLONG > Long.valueOf( "2147483647" ) )
				{
					long f = Long.valueOf("4294967296");
					vLONG = f - vLONG;
					vLONG = vLONG * -1;
				}						
				return  (int)( vLONG / 100 );		

			}
			else if ( Type == 1 && SubType == 137 )
			{
				vLONG = JENR.BinToLong( Bin.substring( 1024 , 1032 ) );

				if( vLONG != 0 )
				{
					return (int) ( vLONG * 0.40 );					
				}
				else
				{
					vLONG = JENR.BinToLong( Bin.substring( 1032 , 1040 ) );
					return (int) ( vLONG * 0.40 );
				}				
			}
			return -1;
		}catch( Exception e)
		{
			System.err.println("CLMPPDU: PosicionAceleradorOBD: " + e.toString() );
			return -1;

		}finally
		{

		}
	}

	@Override
	public int OdometroOBD() 
	{
		try{
			long vLONG=0;

			if( Type == 1 && SubType == 189)
			{
				vLONG = JENR.BinToLong( Bin.substring( 1032 , 1064 ) );

				if( vLONG > Long.valueOf( "2147483647" ) )
				{
					long f = Long.valueOf("4294967296");
					vLONG = f - vLONG;
					vLONG = vLONG * -1;
				}						
				return  (int)vLONG;		

			}
			else if( Type == 1 && SubType == 81 && B_PDU[53] == 0x2C )
			{
				byte[] vByte;

				vByte = new byte[4];

				vByte[3] = B_PDU[62];
				vByte[2] = B_PDU[63];
				vByte[1] = B_PDU[64];
				vByte[0] = B_PDU[65];

				return (int) ( (JENR.BinToInt(JENR.HexToBin( JENR.ByteArrayToHexString( vByte ) ) ) * 5 ) );
			}
			else if ( Type == 1 && SubType == 137 )
			{

				//J1708
				vLONG = Long.parseLong( Hex.substring(122,124) + Hex.substring(120,122) + Hex.substring(118,120) + Hex.substring(116,118) , 16 );

				if( vLONG != 0 )
				{
					return (int) ( vLONG * 0.1 * 1.609334 * 1000 );					
				}

				vLONG = Long.parseLong( Hex.substring(130,132) + Hex.substring(128,130) + Hex.substring(126,128) + Hex.substring(124,126) , 16 );

				//J1939
				if( vLONG != 0)
				{
					return (int)  (vLONG  * 0.125 * 1000 );
				}

				vLONG = Long.parseLong( Hex.substring(138,140) + Hex.substring(136,138) + Hex.substring(134,136) + Hex.substring(132,134)  , 16 );

				//J1939
				if( vLONG != 0)
				{
					return (int)  (vLONG  * 0.005 * 1000 );
				}	
			}

			return -1;
		}catch( Exception e)
		{
			System.err.println("CLMPPDU: OdometroOBD: " + e.toString() );
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

				vLONG = JENR.BinToLong( Bin.substring( 1288 , 1320 ) );

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
			System.err.println("CLMPPDU: OdometroViajeOBD: " + e.toString() );
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

				vLONG = JENR.BinToLong( Bin.substring( 1064 , 1096 ) );

				if( vLONG > Long.valueOf( "2147483647" ) )
				{
					long f = Long.valueOf("4294967296");
					vLONG = f - vLONG;
					vLONG = vLONG * -1;
				}						
				return  (float)(vLONG/100);		

			}
			else if ( Type == 1 && SubType == 137 )
			{
				float vLONG1 = (float)JENR.BinToLong( Bin.substring( 880 , 888 ) );

				if( vLONG1 != 0 )
				{
					vLONG1 = (float) (vLONG1 * 0.5);
				}	
				else
				{
					vLONG1 = (float)JENR.BinToLong( Bin.substring( 888 , 896 ) );
					vLONG1 = (float) (vLONG1 * 0.4);
				}

				float vLONG2 = (float)JENR.BinToLong( Bin.substring( 896 , 904 ) );

				if( vLONG2 != 0 )
				{
					vLONG2 = (float)(vLONG2*0.5);				
				}
				else
				{
					vLONG2 = JENR.BinToLong( Bin.substring( 904 , 912 ) );
					vLONG2 = (float)(vLONG2*0.4);
				}
				return (float) (( vLONG1 + vLONG2)/2.00);
			}

			return -1;
		}catch( Exception e)
		{
			System.err.println("CLMPPDU: NivelGasolinaOBD: " + e.toString() );
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

				vLONG = JENR.BinToLong( Bin.substring( 1096 , 1128 ) );

				if( vLONG > Long.valueOf( "2147483647" ) )
				{
					long f = Long.valueOf("4294967296");
					vLONG = f - vLONG;
					vLONG = vLONG * -1;
				}						
				return  (float)(vLONG/1000);		

			}
			else if( Type == 1 && SubType == 81 && B_PDU[53] == 0x2D )
			{
				byte[] vByte;

				vByte = new byte[4];

				vByte[3] = B_PDU[58];
				vByte[2] = B_PDU[59];
				vByte[1] = B_PDU[60];
				vByte[0] = B_PDU[61];

				return (float) ( (JENR.BinToInt(JENR.HexToBin( JENR.ByteArrayToHexString( vByte ) ) ) * 0.05 )/1000 );
			}
			return -1;
		}catch( Exception e)
		{
			System.err.println("CLMPPDU: CombustibleRestanteOBD: " + e.toString() );
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

				vLONG = JENR.BinToLong( Bin.substring( 1128 , 1160 ) );

				if( vLONG > Long.valueOf( "2147483647" ) )
				{
					long f = Long.valueOf("4294967296");
					vLONG = f - vLONG;
					vLONG = vLONG * -1;
				}						
				return  (int)(vLONG);		

			}
			else if ( Type == 1 && SubType == 137 )
			{
				long vLONG = JENR.BinToLong( Bin.substring( 1016 , 1024 ) );

				if( vLONG != 0 )
				{
					return (int) ( vLONG  );					
				}


			}
			return -1;
		}catch( Exception e)
		{
			System.err.println("CLMPPDU: EngraneTransmisionOBD: " + e.toString() );
			return -1;

		}finally
		{

		}
	}

	@Override
	public float TemperaturaRefrigeranteOBD() 
	{
		try{

			long vLONG=0;
			if( Type == 1 && SubType == 189)
			{


				vLONG = JENR.BinToLong( Bin.substring( 1160 , 1192 ) );

				if( vLONG > Long.valueOf( "2147483647" ) )
				{
					long f = Long.valueOf("4294967296");
					vLONG = f - vLONG;
					vLONG = vLONG * -1;
				}						
				return  (int)(vLONG/16);		

			}
			else if ( Type == 1 && SubType == 137 )
			{
				vLONG = JENR.BinToLong( Bin.substring( 744 , 752 ) );

				if( vLONG != 0 )
				{
					return (int) ( (vLONG - 32)/1.8 );					
				}
				else
				{
					vLONG = JENR.BinToLong( Bin.substring( 752 , 760 ) );
					return (int) ( vLONG - 40 );
				}				
			}

			return -1;
		}catch( Exception e)
		{
			System.err.println("CLMPPDU: EngraneTransmisionOBD: " + e.toString() );
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

				vLONG = JENR.BinToLong( Bin.substring( 1192 , 1224 ) );

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
			System.err.println("CLMPPDU: IndiceGasolinaOBD: " + e.toString() );
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

				vLONG = JENR.BinToLong( Bin.substring( 1224 , 1256 ) );

				if( vLONG > Long.valueOf( "2147483647" ) )
				{
					long f = Long.valueOf("4294967296");
					vLONG = f - vLONG;
					vLONG = vLONG * -1;
				}						
				return  (float)(vLONG/1000);		

			}
			else if ( Type == 1 && SubType == 137 )
			{
				//vLONG = this.BinToLong( Bin.substring( 624 , 640 ) );
				long vLONG = Long.parseLong( Hex.substring(142,144) + Hex.substring(140,142) , 16 );
				if( vLONG != 0 )
				{
					return (int) ( vLONG * 0.05 );					
				}
				else
				{
					//vLONG = this.BinToLong( Bin.substring( 640 , 656 ) );
					vLONG = Long.parseLong( Hex.substring(146,148) + Hex.substring(144,146), 16 );
					return (int) ( vLONG * 0.05 );
				}				
			}
			return -1;
		}catch( Exception e)
		{
			System.err.println("CLMPPDU: VoltajeBateriaOBD: " + e.toString() );
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

				vLONG = JENR.BinToLong( Bin.substring( 1256 , 1288 ) );

				if( vLONG > Long.valueOf( "2147483647" ) )
				{
					long f = Long.valueOf("4294967296");
					vLONG = f - vLONG;
					vLONG = vLONG * -1;
				}						
				return  (int)(vLONG);		

			}
			else if ( Type == 1 && SubType == 137 )
			{
				//vLONG = this.BinToLong( Bin.substring( 624 , 640 ) );
				long vLONG = JENR.BinToLong( Bin.substring( 1048 , 1056 ) );

				return (int) ( vLONG );					


			}
			return -1;
		}catch( Exception e)
		{
			System.err.println("CLMPPDU: EstadoSeñalesGiroOBD: " + e.toString() );
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

				vLONG = JENR.BinToLong( Bin.substring( 1320 , 1352 ) );

				if( vLONG > Long.valueOf( "2147483647" ) )
				{
					long f = Long.valueOf("4294967296");
					vLONG = f - vLONG;
					vLONG = vLONG * -1;
				}						
				return  (float)((float)vLONG/1000.0);		

			}
			return -1;
		}catch( Exception e)
		{
			System.err.println("CLMPPDU: GasolinaConsumidaPorViajeOBD: " + e.toString() );
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
			System.err.println("CLMPPDU: IndicadoresOBD: " + e.toString() );
			return null;

		}finally
		{

		}
	}

	@SuppressWarnings("finally")
	@Override
	public String getDTC()
	{
		// TODO Auto-generated method stub
		String DTC=null;
		try{
			if( Type == 2)
			{
				if( B_PDU != null && B_PDU.length >= 55 )
				{
					DTC="";
					for( int i=55; i < B_PDU.length; i=i+5)
					{
						if( DTC.length() > 0 )
						{
							DTC += ",";
						}
						DTC += new String( Arrays.copyOfRange(B_PDU , i, i+5 ) );


					}


				}
			}

		}catch( Exception  e)
		{
			System.err.println( "EXCEPTION: CLMPPDU: getDTC: " + e.toString() );

		}finally
		{			
			return DTC;
		}
	}

	@Override
	public boolean getCheckEngine() 
	{
		// TODO Auto-generated method stub
		return false;
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
		//RICKY
		try{
			long Temperatura1LONG=0;
			long f= Long.valueOf( "4294967296" );
			float temperatura=0;
			if( ( Type == 1 && (SubType == 117 || SubType == 121 || SubType == 189 || SubType == 81 || B_PDU.length == 137 ) ) || Type == 2 )
			{ 
				try{					
					if (SubType == 117) {
						Temperatura1LONG = JENR.BinToLong( Bin.substring( 584 , 616 ) );
					}
					if (SubType == 189) {
						Temperatura1LONG = JENR.BinToLong( Bin.substring( 648 , 680 ) );	//ACC 5
					}
					if(  Temperatura1LONG > Long.valueOf( "2147483647" ) )
					{
						Temperatura1LONG = f -  Temperatura1LONG;
						Temperatura1LONG =  Temperatura1LONG * -1;	
					}
					temperatura = (float)( Math.rint(Temperatura1LONG*0.0625*10)/10 ) ;
					return "" + temperatura;				
				}catch( Exception e)
				{ return ""; }
			}
			else
			{ return ""; }
		}catch( Exception e)
		{
			System.err.println( "ERROR: CLMPAPI: get Temperatura1LONG: " + e.toString() );
			return "";
		}
	}

	@Override
	public String getTemperatura2() {
		// ABEL -- ACUMULADOR 25 REFERIDO A DATOS DE TEMPERATURA 2
		try{
			long Temperatura2LONG=0;
			long f= Long.valueOf( "4294967296" );
			float temperatura=0;
			if( ( Type == 1 && (SubType == 117 || SubType == 121 || SubType == 189 || SubType == 81 || B_PDU.length == 137 ) ) || Type == 2 )
			{ 
				try{					
					if (SubType == 117) {
						Temperatura2LONG = JENR.BinToLong( Bin.substring( 1224 , 1256 ) );
					}
					if (SubType == 189) {
						Temperatura2LONG = JENR.BinToLong( Bin.substring( 1288 , 1320 ) );	// ACC 25
					}
					if(  Temperatura2LONG > Long.valueOf( "2147483647" ) )
					{
						Temperatura2LONG = f -  Temperatura2LONG;
						Temperatura2LONG =  Temperatura2LONG * -1;	
					}
					temperatura = (float)( Math.rint(Temperatura2LONG*0.0625*10)/10 ) ;
					return "" + temperatura;				
				}catch( Exception e)
				{ return ""; }
			}
			else
			{ return ""; }
		}catch( Exception e)
		{
			System.err.println( "ERROR: CLMPAPI: get Temperatura2LONG: " + e.toString() );
			return "";
		}
	}

	@Override
	public String getTimeMaxSpeed() {
		// ABEL -- EN EL ACUMULADOR 14 SE GUARDA EL TIEMPO DE PERMANENCIA EN EXCESO DE VELOCIDAD
		int timeMaxVelocidadINT = 0;
		try {
			if( ( Type == 1 && (SubType == 117 || SubType == 121 || SubType == 189 || SubType == 81 || B_PDU.length == 137 ) ) || Type == 2 ){
				try{					
					if (SubType == 117) {
					}
					if (SubType == 189) {
						timeMaxVelocidadINT = JENR.BinToInt(Bin.substring(936, 968));	//ACC 14
					}
					return "" + timeMaxVelocidadINT;				
				}catch( Exception e)
				{ return null; }
			}
			else return null;

		} catch (Exception e) {
			System.err.println("ERROR: CLMPAPI: get TimeMaxSpeed: " + e.toString());
			return null;
		}
	}

	@Override
	public String getSpeedSetPoint() {
		// ABEL -- EN EL ACUMULADOR 16 SE GRABA EL SET POINT DE EXCESO DE VELOCIDAD
		try {
			if( ( Type == 1 && (SubType == 117 || SubType == 121 || SubType == 189 || SubType == 81 || B_PDU.length == 137 ) ) || Type == 2 ){
				try{					
					if (SubType == 189) {
						long SpeedSP=0;
						long f = Long.valueOf("4294967296");
						SpeedSP = JENR.BinToLong(Bin.substring(1000, 1032));	//ACC 16

						if( SpeedSP > Long.valueOf( "2147483647" ) )
						{
							SpeedSP = f - SpeedSP;
							SpeedSP = SpeedSP * -1;
						}
						return "" + ( SpeedSP * 0.0223693596 );
					}
					else return "-1";
				}catch( Exception e)
				{ return "-1"; }
			}
			else return "-1";

		} catch (Exception e) {
			System.err.println("ERROR: CLMPAPI: get SpeedSetPoint: " + e.toString());
			return "-1";
		}
	}

	@Override
	public String getEventoValor() {
		// ABEL -- EN EL ACUMULADOR 29 SE IDENTIFICA LA MAXIMA ACELERACION
		//		-- EN EL ACUMULADOR 6 SE REGISTRA EL DATO DE IMPACTO
		try {
			if( ( Type == 1 && (SubType == 117 || SubType == 121 || SubType == 189 || SubType == 81 || B_PDU.length == 137 ) ) || Type == 2 ){
				try{					
					if (SubType == 189) {
						//long inputEvent = JENR.BinToLong(Bin.substring(400,408));
						int inputEvent = Integer.parseInt(this.getInputEvent());
						switch (inputEvent) {
						case 110-113:
							//System.out.println("EVENTO COMPORTAMIENTO BRUSCO");
							long maxAcceleration=0;
							maxAcceleration = JENR.BinToLong((Bin.substring(1416, 1448)).substring(19, 32));	//ACC 29
							return "" + maxAcceleration;
						case 114:
							//System.out.println("EVENTO IMPACTO");
							//String acc6 = Bin.substring(680, 712);	//ACC 6
							//System.out.println("ACC 26 = " + acc6);
							long impactData=0;
							impactData = JENR.BinToLong((Bin.substring(680, 712)).substring(16, 32));
							return ""+impactData;

						default:
							return "-1";
						}

					}
					else return "-1";
				}catch( Exception e)
				{ return "-1"; }
			}
			else return "-1";

		} catch (Exception e) {
			System.err.println("ERROR: CLMPAPI: get EventoValor: " + e.toString());
			return "-1";
		}
	}

	@Override
	public String getEventoValorSP() {
		//ABEL
		try {
			if( ( Type == 1 && (SubType == 117 || SubType == 121 || SubType == 189 || SubType == 81 || B_PDU.length == 137 ) ) || Type == 2 ){
				try{					
					if (SubType == 189) {
						//REFERIDO AL ACUMULADOR 17 DONDE SE GUARDAN 4 DATOS DE SP
						Integer evento = 0;
						String eventoEntrada = this.getUserSpecifiedNumber();
						String acc17 = Bin.substring(1032, 1064);
						evento = Integer.parseInt(eventoEntrada);

						switch (evento) {
						case 110:	//ACELERACION BRUSCA
							long evento0 = 0; 
							evento0 = JENR.BinToLong(acc17.substring(0, 8));
							return "" + evento0;
							//break;

						case 111:	//FRENANDA BRUSCA
							long evento1 = 0; 
							evento1 = JENR.BinToLong(acc17.substring(8, 16));
							return "" + evento1;
							//break;

						case 112:	// GIRO IZQUIERDA BRUSCO
							long evento2 = 0; 
							evento2 = JENR.BinToLong(acc17.substring(16, 24));
							return "" + evento2;
							//break;

						case 113:	//GIRO DERECHA BRUSCO
							long evento3 = 0; 
							evento3 = JENR.BinToLong(acc17.substring(24, 32));
							return "" + evento3;
							//break;

						default:
							return "-1";
							//break;
						}
						//return null;
					}
					else return "-1";
				}catch(Exception e){
					return "-1";
				}
			}
			else return "-1";
		}
		catch (Exception e) {
			System.err.println("ERROR: CLMPAPI: get EventoValorSP: " + e.toString());
			return "-1";
		}

	}

	@Override
	public String getImpactData() {
		// ABEL -- EN EL ACUMULADOR 26 SE IDENTIFICA LOS DATOS DE IMPACTO
		try {
			if( ( Type == 1 && (SubType == 117 || SubType == 121 || SubType == 189 || SubType == 81 || B_PDU.length == 137 ) ) || Type == 2 ){
				try{					
					if (SubType == 189) {
						long impactData=0;
						//String acc26 = Bin.substring(1320, 1352);
						//System.out.println("ABEL - IMPACT DATA ACC 26 = " + acc26);
						impactData = JENR.BinToLong((Bin.substring(1320, 1352)));	//ACC 26
						return "" + impactData;
					}
					else return "-1";
				}catch( Exception e)
				{ return "-1"; }
			}
			else return "-1";

		} catch (Exception e) {
			System.err.println("ERROR: CLMPAPI: get ImpactData: " + e.toString());
			return "-1";
		}
	}

}
