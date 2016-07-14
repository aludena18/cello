package PDULibrary;

import java.net.DatagramPacket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class QPDU implements PDU
{
	private String IP,S_PDU;
	
	private int Puerto;
	
	private int Type=0, SubType=0;
	
	private byte[] B_PDU;
	
	private String [] Parametros;
	
	public QPDU( DatagramPacket tmpPDU )
	{
		try{
			
			byte[] tmpB_PDU;
			
			tmpB_PDU = tmpPDU.getData();

			B_PDU = new byte[ tmpPDU.getLength() ];
			
			System.arraycopy(tmpB_PDU, 0, B_PDU , 0, tmpPDU.getLength() );
			
			S_PDU = new String(B_PDU);
			
			if( S_PDU.indexOf('$') > 0 )
				S_PDU = S_PDU.substring( 0, S_PDU.indexOf('$') );
			
			try{
				IP = tmpPDU.getAddress().toString();
				Puerto= tmpPDU.getPort();
			}catch( Exception e)
			{
				IP = null;
				Puerto = 0;
			}
			
			System.err.println(S_PDU);
			this.IdentifyType();
			
		}catch( Exception e )
		{
			System.err.println("Exception: QPDU: " + e.toString() );
		}
	}

	private synchronized void IdentifyType()
	{
		try{
			if( S_PDU != null )
			{
				Parametros = S_PDU.split(",");
				
			}
			if( Parametros != null && Parametros.length == 24 )
			{
				if( Parametros[3].compareTo( "GTFRI" ) == 0 )
				{
					Type = 1;
				}
				else if( Parametros[3].compareTo( "GTGEO" ) == 0 )
				{
					Type = 2;
					try{
						SubType = Integer.parseInt( Parametros[7]);
					}catch( Exception ee){ SubType=9; }
					
					try{
						SubType = SubType  * 100;
						SubType = SubType + Integer.parseInt( Parametros[8]);
					}catch( Exception ee){  }
				}
				else if( Parametros[3].compareTo( "GTSPD" ) == 0 )
				{
					Type = 3;
					try{
						SubType = SubType + Integer.parseInt( Parametros[8]);
					}catch( Exception ee){  }
				}
				else if( Parametros[3].compareTo( "GTSOS" ) == 0 )
				{
					Type = 4;
				}
				else if( Parametros[3].compareTo( "GTRTL" ) == 0 )
				{
					Type = 5;
				}
				else if( Parametros[3].compareTo( "GTPNL" ) == 0 )
				{
					Type = 6;
				}
				else if( Parametros[3].compareTo( "GTNMR" ) == 0 )
				{
					Type = 7;
					try{
						SubType = SubType + Integer.parseInt( Parametros[8]);
					}catch( Exception ee){  }
				}
				else if( Parametros[3].compareTo( "GTLBC" ) == 0 )
				{
					Type = 8;
				}
				else if( Parametros[3].compareTo( "GTGCR" ) == 0 )
				{
					Type = 9;
					try{
						SubType = SubType + Integer.parseInt( Parametros[8]);
					}catch( Exception ee){  }
				}
				else if( Parametros[3].compareTo( "GTPNA" ) == 0 )
				{
					Type = 10;
				}
				else if( Parametros[3].compareTo( "GTPFA" ) == 0 )
				{
					Type = 11;
				}
				else if( Parametros[3].compareTo( "GTEPN" ) == 0 )
				{
					Type = 12;
				}
				else if( Parametros[3].compareTo( "GTEPF" ) == 0 )
				{
					Type = 13;
				}
				else if( Parametros[3].compareTo( "GTBPL" ) == 0 )
				{
					Type = 14;
				}
				else if( Parametros[3].compareTo( "GTBTC" ) == 0 )
				{
					Type = 15;
				}
				else if( Parametros[3].compareTo( "GTSTC" ) == 0 )
				{
					Type = 16;
				}
				else if( Parametros[3].compareTo( "GTSTT" ) == 0 )
				{
					Type = 17;
					try{
						SubType = SubType + Integer.parseInt( Parametros[8]);
					}catch( Exception ee){  }
				}
				else if( Parametros[3].compareTo( "GTANT" ) == 0 )
				{
					Type = 18;
					try{
						SubType = Integer.parseInt( Parametros[7]);
					}catch( Exception ee){ SubType=9; }
				}
				else if( Parametros[3].compareTo( "GTPDP" ) == 0 )
				{
					Type = 19;
				}
				else if( Parametros[3].compareTo( "GTSWG" ) == 0 )
				{
					Type = 20;
					try{
						SubType = SubType + Integer.parseInt( Parametros[8]);
					}catch( Exception ee){  }
				}
				else if( Parametros[3].compareTo( "GTDIS" ) == 0 )
				{
					Type = 21;
					SubType = Integer.parseInt( Parametros[8]);
				}
			}
			else if( Parametros != null && Parametros.length == 9 && Parametros[3].compareTo( "GTHBD" ) == 0)
			{
				Type = 22;
				
			}
			else if( Parametros != null && Parametros.length > 0 && Parametros[0] != null && Parametros[0].indexOf("+ACK") >= 0)
			{
				Type = 100;				
			}
		}catch( Exception e )
		{
			System.err.println( "Exception: QPDU: IdentifyType: " +  e.toString() );
		}
		
	}
	
	public synchronized String getLatitude()
	{
		try{	
			if( Type != 0 )
			{
				if( Parametros[15] != null && Parametros[15].length() > 0)
				{
					return Parametros[15];
				}
			}
			return null;
		}catch( Exception e )
		{
			System.err.println( "Exception: QPDU: getLatitude: " +  e.toString() );
			return null;
		}
	}
	
	public synchronized String getLongitude()
	{
		try{
			if( Type != 0 )
			{
				if( Parametros[14] != null && Parametros[14].length() > 0)
				{
					return Parametros[14];
				}
			}
			return null;
		}catch( Exception e )
		{
			System.err.println( "Exception: QPDU: getLongitude: " +  e.toString() );
			return null;
		}
	}
	
	public synchronized String getSpeed()
	{
		double velocidad;
		try{
			if( Type != 0 )
			{
				if( Parametros[11] != null && Parametros[11].length() > 0)
				{
					velocidad = Double.parseDouble( Parametros[11] );
					velocidad = velocidad * 0.621371192;
					return "" + (int)velocidad;
				}
				
			}
			return null;
			
		}catch( Exception e )
		{
			System.err.println( "Exception: QPDU: getSpeed: " +  e.toString() );
			return null;
		}
	
	}	
	
	public synchronized String getHeading()
	{
		try{
			if( Type != 0 )
			{
				if( Parametros[12] != null && Parametros[12].length() > 0)
				{
					return Parametros[12];
				}
			}
			return null;
		}catch( Exception e )
		{
			System.err.println( "Exception: QPDU: getHeading: " +  e.toString() );
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
			System.err.println("ERROR: QPDU: ReportDateTime: " + e.toString() + " " + getGPSDate() + " " + getRTC() );
			
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
			System.err.println("ERROR: QPDU: ReportDateTimeRTC: " + e.toString());
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
			System.err.println("ERROR: QPDU: ReportDateTimeGPS: " + e.toString());
			return null;
		}
		
	}
	
	public synchronized String getDateTimeSQL()
	{
		try{
			
			return "20" + getGPSDate().substring(4,6) + getGPSDate().substring(2,4) + getGPSDate().substring(0, 2) +
			" " + getGPSTime().substring(0,2) + ":" + getGPSTime().substring(2,4) + ":" + getGPSTime().substring(4,6);
			
		}catch( Exception e)
		{
			System.err.println("ERROR: QPDU: ReportDateTimeGPS: " + e.toString());
			return null;
		}
	}

	public synchronized String getReportDateTime(int i)
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
			System.err.println("ERROR: QPDU: ReportDateTime:" + e.toString());
			
			return null;
		}
	}

	public synchronized String getRTC()
	{	
		String  GPSDate;
		GPSDate = getGPSDate();
		
		try{
			return "20" + GPSDate.substring(4) + GPSDate.substring(2,4) + GPSDate.substring(0,2) + getGPSTime();
		}catch( Exception e)
		{
			System.err.println("ERROR: QPDU: getRTC:" + e.toString());
			return null;
		}
	}
		
	public synchronized String getModemID() 
	{
		try{
			if( Type >  0  && Type < 100 )
			{
				if( Parametros[5] != null && Parametros[5].length() > 0)
				{
					return Parametros[5];
				}
			}
			else if( Type == 100 )
			{
				if( Parametros[2] != null && Parametros[2].length() > 0)
				{
					return Parametros[2];
				}
			}
			return null;
		}catch( Exception e)
		{
			System.err.println("Exception: QPDU: getModemID: " + e.toString());
			return null;
		}
	}

	public synchronized String getInputEvent() 
	{
		return "" + ( (Type*10000) + SubType );
	}

	public synchronized String getAltitude()
	{
		try{
			if( Type != 0 )
			{
				return Parametros[13];
			}
			return null;
			
		}catch( Exception e )
		{
			System.err.println( "Exception: getAltitude: " +  e.toString() );
			return null;
		}
	
	}

	public synchronized String getOdometer() 
	{
		return null;
	}

	public synchronized String getGPSStatus()
	{
		int GPSStatus;
		try{
			if( Parametros[10] != null && Parametros[10].length() > 0)
			{
				GPSStatus = Integer.parseInt( Parametros[10] );
				
				if( GPSStatus > 0 )
					return "1";
				else
					return "9";
			}
			return "9";
		}catch( Exception e)
		{
			System.err.println("Exception: QPDU: getGPSStatus: " + e.toString());
			return null;
		}
	}

	public synchronized String getNumberOfSatellites()
	{
		try{
			if( Parametros[10] != null && Parametros[10].length() > 0)
			{
				return Parametros[10];
			}
			return "0";
		}catch( Exception e)
		{
			System.err.println("Exception: QPDU: getNumberOfSatellites: " + e.toString() );
			return null;
		}
	}
	
	public synchronized String getIO_STATE() 
	{
		return "11111111";
	}

	public synchronized String getIO_CFG()
	{
		return "00000000";
	}

	public synchronized String getIgnitionState() 
	{
		return null;
	}

	public synchronized String getUserSpecifiedNumber()
	{
		return "" + ( (Type*10000) + SubType );
	}

	public synchronized String getGPSDate() 
	{
		try{
				
			if( Type != 0 )
			{
				if( Parametros[16] != null && Parametros[16].length() > 0 && Integer.parseInt( Parametros[10] ) > 3)
				{
					return Parametros[16].substring(6,8) + Parametros[16].substring(4, 6) + Parametros[16].substring(2, 4);
				}
				else
				{
					return Parametros[22].substring(6,8) + Parametros[22].substring(4, 6) + Parametros[22].substring(2, 4);
				}
			}
			return null;
		}catch( Exception e)
		{
			System.err.println("Exception: QPDU: getGPSDate: " + e.toString());
			
			if( Parametros != null && Parametros.length >= 23 )
				return Parametros[22].substring(6,8) + Parametros[22].substring(4, 6) + Parametros[22].substring(2, 4);
			return null;
		}
	}

	public synchronized String getGPSTime()
	{
		try{
			if( Type != 0 )
			{
				if( Parametros[16] != null && Parametros[16].length() > 0 && Integer.parseInt( Parametros[10] ) > 3 )
				{
					return Parametros[16].substring(8);
				}
				else
					return Parametros[22].substring(8);
			}
			return null;
		}catch( Exception e)
		{
			System.err.println("Exception: QPDU: getGPSTime: " + e.toString());
			
			if( Parametros != null && Parametros.length >= 23 )
				return Parametros[22].substring(8);
			return null;
		}
	}

	public synchronized String getCMDResponse() 
	{
		if( Type == 0 )
			return S_PDU;
		return null;
	}

	public synchronized int getIdProtocolo() 
	{
		return 4;
	}

	public synchronized int getType() 
	{
		return Type;
	}

	public synchronized int getSubType() 
	{
		return SubType;
	}

	public synchronized String getIP() 
	{
		return IP;
	}

	public synchronized int getPuerto() 
	{
		return Puerto;
	}

	public synchronized String getS_PDU()
	{
		return S_PDU;
	}

	public synchronized boolean isPDUofPhoto() 
	{
		return false;
	}

	public synchronized boolean isPositionReportPDU() 
	{
		
		if( Type > 0 && Type <= 21 && getLatitude() != "0")
		{
			return true;
		}
		return false;
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

	public synchronized boolean isCMDResponse()
	{
		if( Type == 0)
			return true;
		return false;
	}

	public synchronized String getEstado() 
	{
		return null;
	}

	public synchronized int getNumeroMensaje() 
	{
		return 0;
	}

	public synchronized String getMensaje() 
	{
		return null;
	}

	public synchronized int getTiempoOcupado() 
	{
		return 0;
	}

	public synchronized float getImporteAPagar()
	{
		return 0;
	}

	public synchronized int getTiempoEspera()
	{
		return 0;
	}

	public synchronized int getKilometraje() 
	{
		return 0;
	}

	public synchronized int getNumeroCarrera() 
	{
		return 0;
	}

	public synchronized int getNumeroVoucher()
	{
		return 0;
	}

	public synchronized byte[] getB_PDU() 
	{
		return null;
	}

	public synchronized float getADC1() 
	{
		return 0;
	}

	public synchronized float getADC2() 
	{
		return 0;
	}
	
	public synchronized int getBatteryLevel() 
	{
		return 0;
	}

	public synchronized String getACK()
	{
		try{
			if( Type > 0 && Type < 100 )
			{
				if( Parametros[2].compareTo("1") == 0 )
				{
					return "+SACK:" + Parametros[ Parametros.length - 1 ] + "$"; 
				}
			}
			else
				return "+SACK:" + Parametros[ Parametros.length - 1 ] + "$";
			return null;
			
		}catch( Exception e )
		{
			System.err.println( "Expception: QPDU: getACK: " + e.toString()	);
			return null;
		}
	}

	@Override
	public byte[] getBACK() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTemperatura1()
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
		}	}

	@Override
	public String getTemperatura2() 
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
