package PDULibrary;

import java.net.DatagramPacket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import PDULibrary.JENR;
import PDULibrary.AppProperty;

public class BPDU implements PDU
{
	private String IP,S_PDU;
	
	private int Puerto;
	
	private int Type=0, SubType=0;
	
	private byte[] B_PDU;
	
	private String [] Parametros;
	
	private String Latitud=null,Longitud=null,Altitud=null,EstadoGPS=null,NumeroSatelites=null;
	
	private String MID;
	
	long Time;
	
	AppProperty AP;
	
	public BPDU( DatagramPacket tmpPDU , AppProperty tmpAP )
	{		
			String DBParameters=null;
		try{
			
			Connection DBConection = null;
			Statement stm = null;
			ResultSet rs = null;
					
			Time =  System.currentTimeMillis();
			
			AP = tmpAP;
			
			byte[] tmpB_PDU;
			
			tmpB_PDU = tmpPDU.getData();

			B_PDU = new byte[ tmpPDU.getLength() ];
			
			System.arraycopy(tmpB_PDU, 0, B_PDU , 0, tmpPDU.getLength() );
			
			S_PDU = new String(B_PDU);
			
			System.err.println("donde estas?");
			System.err.println(S_PDU);
			
			try{
				IP = tmpPDU.getAddress().toString();
				Puerto= tmpPDU.getPort();
			}catch( Exception e)
			{
				IP = null;
				Puerto = 0;
			}
			
			if( AP != null )
			{
				try{
					DBParameters = AP.getProperty("JdbcUrl") + "://"
								+ AP.getProperty("DBGpsUnitX_Address") + ":"
								+ AP.getProperty("DBGpsUnitX_Port") + ";Database="
								+ AP.getProperty("DBGpsUnitX_Name") + ";User="
								+ AP.getProperty("DBGpsUnitX_User") + ";Password="
								+ AP.getProperty("DBGpsUnitX_PWD");
				}catch( Exception e )
				{
					
				}
			}
			
			System.err.println( DBParameters );
			
			try{
				DBConection = DriverManager.getConnection(DBParameters);
				stm = DBConection.createStatement();
				
				rs = stm.executeQuery( "SELECT * FROM REPORTEPOSICION_LAST WITH (NOLOCK) WHERE SOURCE LIKE '%" + IP + ":%' AND IDPROTOCOLO = 1" );
				
				System.err.println(    "SELECT * FROM REPORTEPOSICION_LAST WITH (NOLOCK) WHERE SOURCE LIKE '%" + IP + ":%' AND IDPROTOCOLO = 1");
				
				if( rs.next() )
				{
					MID = rs.getString(2);
					Latitud = rs.getString(6);
					Longitud =  rs.getString(7);
					Altitud =  rs.getString(8);
					NumeroSatelites = rs.getString(11);
					EstadoGPS = rs.getString(12);
					
					System.err.println( Latitud + " " + Longitud + " "+ Altitud + " " + NumeroSatelites + " " + EstadoGPS );
				}
			}catch( SQLException SQLe)
			{
				System.err.println("SQLException: BPDU: " + SQLe.toString() );
			}catch( Exception e )
			{
				System.err.println("Exception: BPDU: " + e.toString() );		
			}finally
			{			
				try{
					if( rs != null)
						rs.close();
				}catch( Exception e ){}finally{ rs = null; }
				
				try{
					if( stm != null)
						stm.close();
				}catch( Exception e ){}finally{ stm = null; }
							
				try{
					if( DBConection != null)
						DBConection.close();
				}catch( Exception e ){}finally{ DBConection = null; }
			}
			this.IdentifyType();
			
			System.err.println( "" + Type + " " + SubType + " " + this.getModemID() );
			
		}catch( Exception e )
		{
			System.err.println("Exception: BPDU: " + e.toString() );
		}
	}

	private synchronized void IdentifyType()
	{
		try{
			if( Latitud != null && Latitud.length() > 0 && B_PDU.length == 6 )
			{
				Type = 1;
			}
			else 
				Type = 2;
		}catch( Exception e )
		{
			System.err.println( "Exception: BPDU: IdentifyType: " +  e.toString() );
		}
		
	}
	
	public synchronized String getLatitude()
	{
		try{	

			return Latitud;
			
		}catch( Exception e )
		{
			System.err.println( "Exception: BPDU: getLatitude: " +  e.toString() );
			return null;
		}
	}
	
	public synchronized String getLongitude()
	{
		try{
			return Longitud;
		}catch( Exception e )
		{
			System.err.println( "Exception: BPDU: getLongitude: " +  e.toString() );
			return null;
		}
	}
	
	public synchronized String getSpeed()
	{
		try{
			return null;
			
		}catch( Exception e )
		{
			System.err.println( "Exception: BPDU: getSpeed: " +  e.toString() );
			return null;
		}
	
	}	
	
	public synchronized String getHeading()
	{
		try{
			return null;
		}catch( Exception e )
		{
			System.err.println( "Exception: BPDU: getHeading: " +  e.toString() );
			return null;
		}
	}
	
	public synchronized String getReportDateTime()
	{
		try{
			//yyyy-dd-MM HH:mm:ss
			return "20" + getGPSDate().substring(4,6) + "-" + getGPSDate().substring(0, 2) + "-" + getGPSDate().substring(2,4) +
					" " + getGPSTime().substring(0,2) + ":" + getGPSTime().substring(2,4) + ":" + getGPSTime().substring(4,6); 
			
		}catch( Exception e)
		{
			System.err.println("ERROR: BPDU: ReportDateTime: " + e.toString() + " " + getGPSDate() + " " + getRTC() );
			
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
			System.err.println("ERROR: BPDU: ReportDateTimeRTC: " + e.toString());
			return null;
		}
		
	}

	public synchronized String getReportDateTimeGPS()
	{
		try{
	
			if( Type == 1)
			{
				return "20" + getGPSDate().substring(4,6) + "-" + getGPSDate().substring(0, 2) + "-" + getGPSDate().substring(2,4) +
						" " + getGPSTime().substring(0,2) + ":" + getGPSTime().substring(2,4) + ":" + getGPSTime().substring(4,6);
			}
			else
			{
				String dt;
				Calendar C;
				C = new GregorianCalendar();
				C.setTimeInMillis(Time);
				C.add(Calendar.HOUR, 5);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
				dt = sdf.format( C.getTime());
				return dt;
			}
			
		}catch( Exception e)
		{
			System.err.println("ERROR: BPDU: ReportDateTimeGPS: " + e.toString());
			return null;
		}
		
	}
	
	public synchronized String getDateTimeSQL()
	{
		try{
			
			if( Type == 1 )
			{
				return "20" + getGPSDate().substring(4,6) + getGPSDate().substring(2,4) + getGPSDate().substring(0, 2) +
						" " + getGPSTime().substring(0,2) + ":" + getGPSTime().substring(2,4) + ":" + getGPSTime().substring(4,6);
			}
			else
			{
				String dt;
				Calendar C;
				C = new GregorianCalendar();
				C.setTimeInMillis(Time);
				//C.add(Calendar.HOUR, 5);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
				dt = sdf.format( C.getTime());
				return dt;
			}
			
		}catch( Exception e)
		{
			System.err.println("ERROR: BPDU: ReportDateTimeSQL: " + e.toString());
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
			System.err.println("ERROR: BPDU: ReportDateTime:" + e.toString());
			
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
			System.err.println("ERROR: BPDU: getRTC:" + e.toString());
			return null;
		}
	}
		
	public synchronized String getModemID() 
	{
		try{
			if( Type == 1 )
			{
				return JENR.ByteToHex( B_PDU[2] ) + JENR.ByteToHex( B_PDU[3] ) + JENR.ByteToHex( B_PDU[4] ); 
			}
			else if( Type == 2 )
			{	
				return MID;
			}
			return null;
		}catch( Exception e)
		{
			System.err.println("Exception: BPDU: getModemID: " + e.toString());
			return null;
		}
	}

	public synchronized String getInputEvent() 
	{
		return "122333";
	}

	public synchronized String getAltitude()
	{
		try{
			return Altitud;
			
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
		try{
			return EstadoGPS;
		}catch( Exception e)
		{
			System.err.println("Exception: BPDU: getGPSStatus: " + e.toString());
			return null;
		}
	}

	public synchronized String getNumberOfSatellites()
	{
		try{
			return NumeroSatelites;
		}catch( Exception e)
		{
			System.err.println("Exception: BPDU: getNumberOfSatellites: " + e.toString() );
			return null;
		}
	}
	
	public synchronized String getIO_STATE() 
	{
		return "00000000";
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
		return "122333";
	}

	public synchronized String getGPSDate() 
	{
		try{
				
			if( Type == 1 )
			{
				String dt;
				Calendar C;
				
				C = new GregorianCalendar();
						
				C.setTimeInMillis(Time);
				C.add(Calendar.HOUR, 5);
				SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
				dt = sdf.format( C.getTime());
				
				return dt;
			}
			return null;
		}catch( Exception e)
		{
			System.err.println("Exception: BPDU: getGPSDate: " + e.toString());
			
			if( Parametros != null && Parametros.length >= 23 )
				return Parametros[22].substring(6,8) + Parametros[22].substring(4, 6) + Parametros[22].substring(2, 4);
			return null;
		}
	}

	public synchronized String getGPSTime()
	{
		try{
			if( Type == 1 )
			{
				String dt;
				Calendar C;
				
				C = new GregorianCalendar();
						
				C.setTimeInMillis(Time);
				C.add(Calendar.HOUR, 5);
				SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
				dt = sdf.format( C.getTime());
				
				return dt;
			}
			return null;
		}catch( Exception e)
		{
			System.err.println("Exception: BPDU: getGPSTime: " + e.toString());
			
			if( Parametros != null && Parametros.length >= 23 )
				return Parametros[22].substring(8);
			return null;
		}
	}

	public synchronized String getCMDResponse() 
	{
		if( Type == 2 )
			return JENR.ByteArrayToHexString( B_PDU );
		return null;
	}

	public synchronized int getIdProtocolo() 
	{
		return 5;
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
		if( Type == 1 )
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
		if( Type == 2 )
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
		return B_PDU;
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
			return null;			
		}catch( Exception e )
		{
			System.err.println( "Expception: BPDU: getACK: " + e.toString()	);
			return null;
		}
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
