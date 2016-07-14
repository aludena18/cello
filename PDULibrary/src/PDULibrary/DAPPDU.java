package PDULibrary;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DAPPDU implements PDU
{
	private String ModemID, FechaHora;
	byte[] data,dataBinario;
	XMLPDU XML;
	
	int Type=0, SubType=0;
	
	String CS;
	
	private byte valorEnByte( char c )
	{
		try{
			if( c == '0')
				return 0x00;
			else if( c == '1')
				return 0x01;
			else if( c == '2')
				return 0x02;
			else if( c == '3')
				return 0x03;
			else if( c == '4')
				return 0x04;
			else if( c == '5')
				return 0x05;
			else if( c == '6')
				return 0x06;
			else if( c == '7')
				return 0x07;
			else if( c == '8')
				return 0x08;
			else if( c == '9')
				return 0x09;
			else if( c == 'A')
				return 0x0A;
			else if( c == 'B')
				return 0x0B;
			else if( c == 'C')
				return 0x0C;
			else if( c == 'D')
				return 0x0D;
			else if( c == 'E')
				return 0x0E;
			else if( c == 'F')
				return 0x0F;
			else 
				return 0x00;
		}catch( Exception e )
		{
			System.err.println("Exception: valorEnByte: " + e.toString() );
			return 0;
		}
		
	}
	
	public DAPPDU( String tmpXML , String tmpCS )
	{
		try{
		
			CS = tmpCS;
			XML = new XMLPDU(tmpXML);
			
			ModemID = XML.getElement("AdC");
			FechaHora = XML.getPropiedadElemento( "MessageStatus", "time");
			
			System.err.println( XML.getXML() );
			System.err.println( FechaHora + " " + ModemID);
			
			String tmpPDU =  XML.getElement("MessageData");
			data = new byte[ tmpPDU.length() ];
			dataBinario = new byte[ data.length / 2 ];
			
			for( int i = 0 ; i < data.length ; i += 2 )
			{
				data[i] = (byte)tmpPDU.charAt(i);
				data[i+1]= (byte)tmpPDU.charAt(i+1);
				dataBinario[i/2] = (byte) (( this.valorEnByte( tmpPDU.charAt(i) ) << 4 ) | ( this.valorEnByte( tmpPDU.charAt(i+1) ) ));
			}
			IdentifyType();
		}catch( Exception e )
		{
			System.err.println("Exception: DAPPDU: " + e.toString() );
			System.err.println( XML );
			
		}
		
	}

	public String getStringBinario()
	{	
		try{
			if( dataBinario == null )
				return null;
			byte BYTE;
			String cadena="";
			int i, j;
			
			for( i=0; i < dataBinario.length; i++ )
			{
				for( j=7 ; j >=0 ; j-- )
				{
					BYTE = dataBinario[i];
					BYTE >>= j;
					BYTE &= 0x01;
					if( BYTE == 0x01 )
						cadena += "1";
					else if( BYTE == 0x00)
						cadena += "0";
					else 
						cadena += "*";
					
				}
				//cadena += " ";
			}
			return cadena;
		}catch( Exception e)
		{
			System.err.println( "Exception: getStringBinario: " +  e.toString() );
			return null;
		}
	}
	
	public double StringToDouble( String cadena)
	{
		try{
			double valor=0; int i;
			
			for ( i=0; i < cadena.length() ; i++ )
			{
				if( cadena.charAt(i) == '1' )
					valor+= Math.pow( 2, cadena.length() - i - 1 );
			}
				
			return valor;
		}catch( Exception e)
		{
			System.err.println( "Exception: StringToDouble: " +  e.toString() );
			return 0;
		}
		
	}
	
	public String BinarioPositivo( String binario )
	{
		try{
			String cadena= "";
			
			if( binario.charAt(0) == '1' )
			{
				for( int i=0; i < binario.length() ; i++ )
				{
					if( binario.charAt( i) == '1' )
					{
						cadena += "0";
					}
					else if( binario.charAt(i ) == '0' )
					{
						cadena += "1";
					}
				}
			}
			else
				cadena += binario;
			return cadena;
		}catch( Exception e)
		{
			System.err.println( "Exception: BinarioPositivo: " +  e.toString() );
			return null;
		}
	}
	
	public String getSubStringBinario0aN( int i, int j)
	{
		try{
			String cadenaBinaria;
			cadenaBinaria = this.getStringBinario();
			return cadenaBinaria.substring( i , j );
		}catch( Exception e)
		{
			System.err.println( "Exception: getSubStringBinario0aN: " +  e.toString() );
			return null;
		}
	}
	
	public String getSubStringBinarioNa0( int i , int j)
	{
		try{
			int t = dataBinario.length*8;
			return this.getSubStringBinario0aN( t-i-1, t-j-1 );
		}catch( Exception e)
		{
			System.err.println( "Exception: getSubStringBinarioNa0: " +  e.toString() );
			return null;
		}
	}
	
	private void IdentifyType()
	{
		try{
		
			String cadena;
			
			cadena = this.getSubStringBinario0aN(14, 20);
			
			if( cadena.compareTo("000000") == 0 )
			{
				Type = 0x00;
			}
			else if( cadena.compareTo("000001") == 0 )
			{
				Type = 0x01;
			}
			else if( cadena.compareTo("000010") == 0 )
			{
				Type = 0x02;
			}
			else if( cadena.compareTo("000011") == 0 )
			{
				Type = 0x03;
			}
			else if( cadena.compareTo("000100") == 0 )
			{
				Type = 0x04;
			}
			else if( cadena.compareTo("000101") == 0 )
			{
				Type = 0x05;
			}
			else if( cadena.compareTo("000110") == 0 )
			{
				Type = 0x06;
			}
			else if( cadena.compareTo("000111") == 0 )
			{
				Type = 0x07;
			}
			else if( cadena.compareTo("001000") == 0 )
			{
				Type = 0x08;
			}
			else if( cadena.compareTo("001001") == 0 )
			{
				Type = 0x09;
			}
			else if( cadena.compareTo("001010") == 0 )
			{
				Type = 0x0A;
			}
			else if( cadena.compareTo("001011") == 0 )
			{
				Type = 0x0B;
			}
			else if( cadena.compareTo("001100") == 0 )
			{
				Type = 0x0C;
			}
			else if( cadena.compareTo("001101") == 0 )
			{
				Type = 0x0D;
			}
			else if( cadena.compareTo("001110") == 0 )
			{
				Type = 0x0E;
			}
			else if( cadena.compareTo("001111") == 0 )
			{
				Type = 0x0F;
			}
			else if( cadena.compareTo("010000") == 0 )
			{
				Type = 0x10;
			}
			else if( cadena.compareTo("010001") == 0 )
			{
				Type = 0x11;
			}
			else if( cadena.compareTo("010010") == 0 )
			{
				Type = 0x12;
			}
			else if( cadena.compareTo("010011") == 0 )
			{
				Type = 0x13;
			}
			else if( cadena.compareTo("010100") == 0 )
			{
				Type = 0x14;
			}
			else if( cadena.compareTo("010101") == 0 )
			{
				Type = 0x15;
			}
			else if( cadena.compareTo("010110") == 0 )
			{
				Type = 0x16;
			}
			else if( cadena.compareTo("010111") == 0 )
			{
				Type = 0x17;
			}
			else if( cadena.compareTo("011000") == 0 )
			{
				Type = 0x18;
			}
			else if( cadena.compareTo("011001") == 0 )
			{
				Type = 0x19;
			}
			else if( cadena.compareTo("011010") == 0 )
			{
				Type = 0x1A;
			}
			else if( cadena.compareTo("011011") == 0 )
			{
				Type = 0x1B;
			}
			else if( cadena.compareTo("011100") == 0 )
			{
				Type = 0x1C;
			}
			else if( cadena.compareTo("011101") == 0 )
			{
				Type = 0x1D;
			}
			else 
			{
				Type = 0x00;
			}
		}catch( Exception e )
		{
			System.err.println( "Exception: IdentifyType: " +  e.toString() );
		}
		
	}
	
	public String getLatitude()
	{
		String stringLatitud;
		int i=0,j=0;
		double doubleLatitud,divisor=3600;
		
		try{	
			
			
			if( Type == 0x07)
			{
				i=20;j=41;divisor=7200;
			}
			else if( Type == 0x08)
			{
				i=20;j=40;
			}
			else if( Type == 0x09)
			{
				i=20;j=40;
			}
			else if(Type == 0x0A)
			{
				i=20;j=40;
			}
			else if( Type == 0x0B)
			{
				i=20;j=46;divisor=360000;
			}
			else if( Type == 0x0C)
			{
				i=20;j=40;
			}
			else if( Type == 0x0D)
			{
				i=20;j=43;divisor=18000;
			}
			else if( Type == 0x0E)
			{
				i=20;j=40;
			}
			else if( Type == 0x10)
			{
				i=32;j=52;
			}
			else if( Type == 0x11)
			{
				i=31;j=48;divisor=360;
			}
			else if( Type == 0x12)
			{
				i=32;j=49;divisor=360;
			}
			else if( Type == 0x13)
			{
				i=31;j=48;divisor=360;
			}
			else if( Type == 0x14)
			{
				i=20;j=43;divisor=36000;
			}
			else
			{
				return null;
			}
						
			stringLatitud = this.getSubStringBinario0aN( i , j );
			
			if( stringLatitud.charAt(0) == '1' )
				divisor *= -1;
			stringLatitud = BinarioPositivo( stringLatitud );
			doubleLatitud = StringToDouble( stringLatitud );
			return "" + ( doubleLatitud / divisor );
		
		}catch( Exception e )
		{
			System.err.println( "Exception: getLatitude: " +  e.toString() );
			System.err.println( "" + i + " " + j + " " + divisor + " " + this.getStringBinario().length() );
			return null;
		}
	}
	
	public String getLongitude()
	{
		try{
			String stringLongitud;
			int i=0,j=0;
			double doubleLongitud,divisor=3600;
			
			if( Type == 0x07 )
			{
				i=41;j=63;divisor=7200;
			}
			else if( Type == 0x08)
			{
				i=40;j=61;
			}
			else if( Type == 0x09)
			{
				i=40;j=61;
			}
			else if( Type == 0x0A)
			{
				i=40;j=61;
			}
			else if( Type == 0x0B)
			{
				i=46;j=73;divisor=360000;
			}
			else if( Type == 0x0C)
			{
				i=40;j=61;
			}
			else if( Type == 0x0D)
			{
				i=43;j=67;divisor=18000;
			}
			else if( Type == 0x0E)
			{
				i=40;j=61;
			}
			else if( Type == 0x10)
			{
				i=52;j=73;
			}
			else if( Type == 0x11)
			{
				i=48;j=66;divisor=360;
			}
			else if( Type == 0x12)
			{
				i=49;j=67;divisor=360;
			}
			else if( Type == 0x13)
			{
				i=48;j=66;divisor=360;
			}
			else if( Type == 0x14)
			{
				i=43;j=67;divisor=36000;
			}
			else
			{
				return null;
			}
						
			stringLongitud = this.getSubStringBinario0aN( i , j );
			//System.err.println( stringLongitud );
			
			if( stringLongitud.charAt(0) == '1' )
				divisor *= -1;
					
			stringLongitud = BinarioPositivo( stringLongitud );
			//System.err.println( stringLongitud );
			
			doubleLongitud = StringToDouble( stringLongitud );
			//System.err.println( doubleLongitud );
				
			//System.err.println( "" + doubleLongitud / divisor );
			return "" + (doubleLongitud / divisor);
		}catch( Exception e )
		{
			System.err.println( "Exception: getLongitude: " +  e.toString() );
			return null;
		}
		
	}
	
	public String getSpeed()
	{
		try{	
			String stringVelocidad;
			int i=0,j=0;
			double doubleVelocidad,divisor=1;
			
			if( Type == 0x07)
			{
				i=70;j=78;divisor=0.2;
			}
			else if( Type == 0x08)
			{
				i=68;j=76;
			}
			else if( Type == 0x0C)
			{
				i=68;j=73;divisor=0.1;
			}
			else if( Type == 0x0E)
			{
				i=61;j=66;divisor=0.1;
			}
			else if( Type == 0x11)
			{
				i=78;j=84;divisor=0.1;
			}
			else if( Type == 0x13)
			{
				i=72;j=78;divisor=0.1;
			}
			else if( Type == 0x14)
			{
				i=76;j=83;divisor=2;
			}
			else
			{
				return null;
			}
						
			stringVelocidad = this.getSubStringBinario0aN( i , j );
			//System.err.println( stringVelocidad );
			
			doubleVelocidad = StringToDouble( stringVelocidad );
			//System.err.println( doubleVelocidad );
				
			//System.err.println( "" + doubleVelocidad / divisor );
			return "" + (int)((doubleVelocidad / divisor)/1.609344);
		}catch( Exception e )
		{
			System.err.println( "Exception: getSpeed: " +  e.toString() );
			return null;
		}
	
	}	
	
	public String getHeading()
	{
		try{
			String stringRumbo;
			int i=0,j=0;
			double doubleRumbo,divisor=1;
			
			if( Type == 0x07)
			{
				i=63;j=70;divisor=0.2;
			}
			else if( Type == 0x08)
			{
				i=61;j=68;divisor=0.2;
			}
			else if( Type == 0x0A)
			{
				i=75;j=84;
			}
			else if( Type == 0x0C)
			{
				i=61;j=68;divisor=0.2;
			}
			else if( Type == 0x0E)
			{
				i=66;j=73;divisor=0.2;
			}
			else if( Type == 0x11)
			{
				i=72;j=78;divisor=0.16667;
			}
			else if( Type == 0x12)
			{
				i=78;j=84;divisor=0.16667;
			}
			else if( Type == 0x13)
			{
				i=78;j=84;divisor=0.16667;
			}
			else if( Type == 0x14)
			{
				i=67;j=76;
			}
			else
			{
				return null;
			}
						
			stringRumbo = this.getSubStringBinario0aN( i , j );
				
			doubleRumbo = StringToDouble( stringRumbo );
			
			return "" + (int)( doubleRumbo / divisor );
		}catch( Exception e )
		{
			System.err.println( "Exception: IdentifyType: " +  e.toString() );
			return null;
		}
	}
	
	public synchronized String getReportDateTime()
	{
		try{
			
			return "20" + getGPSDate().substring(4,6) + "-" + getGPSDate().substring(0, 2) + "-" + getGPSDate().substring(2,4) +
					" " + getGPSTime().substring(0,2) + ":" + getGPSTime().substring(2,4) + ":" + getGPSTime().substring(4,6); 
			
		}catch( Exception e)
		{
			System.err.println("ERROR: XVMPDU: ReportDateTime: " + e.toString() + " " + getGPSDate() + " " + getRTC() );
			
			return null;
		}
	}
	
	public synchronized String getReportDateTimeRTC()
	{
		try{
	
			return getRTC().substring(0,4) + "-" + getRTC().substring(6,8) + "-" + getRTC().substring( 4 , 6 ) +
			" " + getRTC().substring(8, 10) + ":" + getRTC().substring(10, 12) + ":" + getRTC().substring(12,14); 
		}catch( Exception e)
		{
			System.err.println("ERROR: XVMPDU: ReportDateTimeRTC: " + e.toString());
			return null;
		}
		
	}

	public synchronized String getReportDateTimeGPS()
	{
		try{
	
			return "20" + getGPSDate().substring(4,6) + "-" + getGPSDate().substring(0, 2) + "-" + getGPSDate().substring(2,4) +
			" " + getGPSTime().substring(0,2) + ":" + getGPSTime().substring(2,4) + ":" + getGPSTime().substring(4,6);
			
		}catch( Exception e)
		{
			System.err.println("ERROR: XVMPDU: ReportDateTimeGPS: " + e.toString());
			return null;
		}
		
	}
	
	@Override
	public String getDateTimeSQL()
	{
		try{
			
			return "20" + getGPSDate().substring(4,6) + getGPSDate().substring(2,4) + getGPSDate().substring(0, 2) +
			" " + getGPSTime().substring(0,2) + ":" + getGPSTime().substring(2,4) + ":" + getGPSTime().substring(4,6);
			
		}catch( Exception e)
		{
			System.err.println("ERROR: XVMPDU: ReportDateTimeGPS: " + e.toString());
			return null;
		}
	}

	public String getReportDateTime(int i)
	{
		try{
			String dt,d,t;
			Calendar C;
			
			
			C = new GregorianCalendar();
					
			d = this.getGPSDate();
			t = this.getGPSTime();
			C.set( Integer.parseInt( "20" + d.substring( 4,6) ), Integer.parseInt( d.substring(2,4))-1, Integer.parseInt( d.substring(0,2)), Integer.parseInt( t.substring(0,2)), Integer.parseInt( t.substring(2,4)), Integer.parseInt( t.substring(4,6)));
				
			
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss");
			C.add(Calendar.HOUR_OF_DAY, i );
			dt = sdf.format( C.getTime());
			
			return dt;
		}catch( Exception e)
		{
			System.err.println("ERROR: UDPAPIPDU: ReportDateTime:" + e.toString());
			
			return null;
		}
	}

	public synchronized String getRTC()
	{	
		String  GPSDate;
		GPSDate = getGPSDate();
		return "20" + GPSDate.substring(4) + GPSDate.substring(2,4) + GPSDate.substring(0,2) + getGPSTime();
	}
	
	@Override
	public String getModemID() {
		// TODO Auto-generated method stub
		return ModemID;
	}

	@Override
	public String getInputEvent() 
	{
		return "" + ( 700 + Type );
	}

	public String getAltitude()
	{
		try{
			String stringAltitud;
			int i=0,j=0;
			double doubleAltitud,multiplicador;
			
			if( Type == 0x09)
			{
				i=41;j=46;multiplicador=100;
			}
			else if(Type == 0x0A)
			{
				i=41;j=54;multiplicador=1;
			}
			else if( Type == 0x0B)
			{
				i=53;j=63;multiplicador=1;
			}
			else if( Type == 0x0C)
			{
				i=53;j=56;multiplicador=200;
			}
			else if( Type == 0x13)
			{
				i=46;j=51;multiplicador=2000;
			}
			else 
			{
				return null;
			}
						
			stringAltitud = this.getSubStringBinario0aN( i , j );
			stringAltitud = BinarioPositivo( stringAltitud );
			doubleAltitud = StringToDouble( stringAltitud );
			return "" + ( doubleAltitud * multiplicador);
		}catch( Exception e )
		{
			System.err.println( "Exception: getAltitude: " +  e.toString() );
			return null;
		}
	
	}

	@Override
	public String getOdometer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getGPSStatus()
	{
		return "1";
	}

	public String getNumberOfSatellites()
	{
		return null;
	}
	
	public String getIO_STATE() 
	{
		return "11111111";
	}

	public String getIO_CFG()
	{
		return "00000000";
	}

	@Override
	public String getIgnitionState() 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUserSpecifiedNumber()
	{
		return "" + ( 700 + Type );
	}

	public String getGPSDate() 
	{
		try{
			String GPSDATE=null;
			
			if( FechaHora.length() == 19)
			{
				GPSDATE = FechaHora.substring(8 , 10) + FechaHora.substring(5, 7) + FechaHora.substring(2, 4);
			}
			
			return GPSDATE;
			
		}catch( Exception e)
		{
			return null;
		}
		
		
	}

	@Override
	public String getGPSTime()
	{
		
		
		try{
			String GPSTIME = null;
			
			if( FechaHora.length() == 19 )
			{
				GPSTIME = FechaHora.substring(11, 13) + FechaHora.substring(14, 16) + FechaHora.substring(17); 
			}
			
			return GPSTIME;
				
		}catch( Exception e )
		{
			return null;
		}
		
	}

	@Override
	public String getCMDResponse() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getIdProtocolo() 
	{
		// TODO Auto-generated method stub
		return 3;
	}

	public int getType() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getSubType() 
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public String getIP() {
		// TODO Auto-generated method stub
		return CS;
	}

	@Override
	public int getPuerto() 
	{
		String ocean;
		ocean = XML.getPropiedadElemento("AdC", "ocean");
		if( ocean == null )
			return 0;
		else
		{ 
			int puerto;
			try{
				puerto = Integer.parseInt(ocean);
			}catch( Exception e )
			{
				puerto = -1;
			}
			
			if( puerto == -1 )
			{
				if ( ocean.compareTo("PACCGL") == 0 )
				{
					return 1;
				}
				else if( ocean.compareTo("AORWGL") == 0 )
				{
					return 2;
				}	 
				else if ( ocean.compareTo("AOREGL") == 0 )
				{
					return 3;
				}
				else if ( ocean.compareTo("IORGL") == 0 )
				{
					return 4;
				}
				else if ( ocean.compareTo("PORGL") == 0 )
				{
					return 5;
				}
				else 
				{
					return 6; 
				}
					
			}
			else
				return puerto;
		}
	}

	@Override
	public String getS_PDU() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isPDUofPhoto() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPositionReportPDU() {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean isTaxiDataPDU() 
	{
		return false;
	}

	public boolean isTaxiDataEstadoPDU() 
	{
		return false;
	}

	public boolean isTaxiDataMensajePDU() 
	{
		return false;
	}

	public boolean isTaxiDataTaximetroPDU()
	{
		return false;
	}

	public boolean isCMDResponse()
	{
		return false;
	}

	public String getEstado() 
	{
		return null;
	}

	public int getNumeroMensaje() 
	{
		return 0;
	}

	public String getMensaje() 
	{
		return null;
	}

	public int getTiempoOcupado() 
	{
		return 0;
	}

	public float getImporteAPagar()
	{
		return 0;
	}

	public int getTiempoEspera()
	{
		return 0;
	}

	public int getKilometraje() 
	{
		return 0;
	}

	public int getNumeroCarrera() 
	{
		return 0;
	}

	public int getNumeroVoucher()
	{
		return 0;
	}

	public byte[] getB_PDU() 
	{
		return null;
	}

	public float getADC1() 
	{
		return 0;
	}

	public float getADC2() 
	{
		return 0;
	}
	
	public int getBatteryLevel() 
	{
		return 0;
	}
	
	public synchronized String getACK()
	{
		return null;
	}

	@Override
	public byte[] getBACK() {
		// TODO Auto-generated method stub
		return null;
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