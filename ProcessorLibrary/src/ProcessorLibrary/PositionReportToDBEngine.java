//V3.0.0

package ProcessorLibrary;

//import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

//import com.microsoft.sqlserver.jdbc.SQLServerException;

import PDULibrary.AppProperty;
import PDULibrary.GestorCola;

import java.sql.CallableStatement;



public class PositionReportToDBEngine extends Thread {
	AppProperty AP;
	PDULibrary.PDU PDU;
	String DBParameters;

	Connection DBConection = null;
	Statement stmINS = null, stmSEL = null, stm = null, stm2 = null;
	ResultSet rs = null;
	String C2;
	String Temperatura1, Temperatura2;
	// private int i=0;
	// BufferProcessorX_ReverseGeocoder[] RG;

	public GestorCola BufferEntrada = new GestorCola();

	private int IntentoConexionDB = 0;

	boolean DebugERROR, DebugTRAN, EventAlert, toTerminalPDU = false;

	private void AbrirDB() 
	{
		try {

			IntentoConexionDB++;
			DBConection = DriverManager.getConnection(DBParameters);
			IntentoConexionDB = 0;

		} catch (SQLException e) {
			if (IntentoConexionDB == 5) {
				System.err.println("ERROR: PositionReportToDBEngine: AbrirDB: "
						+ e.toString());
				IntentoConexionDB = 0;
			}
			CerrarDB();

		} catch (Exception EXC) {
			System.err.println("ERROR: PositionReportToDBEngine: AbrirDB: "
					+ EXC.toString());
			CerrarDB();
		} catch (Throwable T) {
			System.err.println("ERROR: PositionReportToDBEngine: AbrirDB: ");
			T.printStackTrace();
			CerrarDB();
		} finally {
			if (DBConection != null) {
				try {
					Thread.sleep(2000);
				} catch (Exception ee) {

				}
			}
		}

	}

	private void CerrarDB() {
		try {
			if (rs != null)
				rs.close();
			rs = null;
		} catch (Exception e) {
			rs = null;
		}

		try {
			if (stm != null)
				stm.close();
			stm = null;
		} catch (Exception e) {
			stm = null;
		}

		try {
			if (stm2 != null)
				stm2.close();
			stm = null;
		} catch (Exception e) {
			stm2 = null;
		}

		try {
			if (stmINS != null)
				stmINS.close();
			stmINS = null;
		} catch (Exception e) {
			stmINS = null;
		}
		try {
			if (stmSEL != null)
				stmSEL.close();
			stmSEL = null;
		} catch (Exception e) {
			stmSEL = null;
		}
		try {
			if (DBConection != null)
				DBConection.close();
			DBConection = null;
		} catch (Exception e2) {
			DBConection = null;
		}
	}

	public void run() {
		//	int c=0;

		long t = System.currentTimeMillis();
		long pg = 0;
		//boolean ActualizarUltimaPosicion=true;

		while (true) 
		{
			try {
				if (BufferEntrada.tamano() > 0)
				{
					if (DBConection == null)
						this.AbrirDB();

					if (DBConection != null) 
					{
						PDU = (PDULibrary.PDU) BufferEntrada.getElemento();

						if (PDU != null && PDU.isPositionReportPDU() ) 
						{
							try{
								//System.err.println(PDU.getTemperatura1());

								if (PDU.getTemperatura1().equalsIgnoreCase("999.0") || PDU.getTemperatura1().equalsIgnoreCase("0.0") ){
									Temperatura1="null";
								}else{
									Temperatura1=PDU.getTemperatura1();
								}

								try{
									//System.out.println("Temperatura 2 = " + PDU.getTemperatura2());
									if (PDU.getTemperatura2().equalsIgnoreCase("999.0") || PDU.getTemperatura2().equalsIgnoreCase("0.0") || PDU.getTemperatura2().equalsIgnoreCase("")){
										Temperatura2="null";
									}
									else{
										Temperatura2=PDU.getTemperatura2();
									}
								}catch(Exception e){
									System.out.println("Error temperatura 2 =" + e);
								}


								CallableStatement prepareCall = DBConection.prepareCall("{call dbo.sp_Hades(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) }");


								prepareCall.setString(1,PDU.getReportDateTime());						
								prepareCall.setString(2, PDU.getModemID());
								prepareCall.setInt(3, Integer.parseInt(PDU.getUserSpecifiedNumber()));
								prepareCall.setString(4,PDU.getReportDateTime());
								prepareCall.setString(5,PDU.getReportDateTime());						

								prepareCall.setFloat(6, Float.parseFloat(PDU.getLatitude()));
								prepareCall.setFloat(7, Float.parseFloat(PDU.getLongitude()));
								prepareCall.setFloat(8, Float.parseFloat(PDU.getAltitude()));
								prepareCall.setFloat(9, Float.parseFloat(PDU.getSpeed()));

								prepareCall.setFloat(10,Integer.parseInt(PDU.getHeading())); 

								prepareCall.setInt(11, Integer.parseInt(PDU.getNumberOfSatellites()));
								prepareCall.setString(12, PDU.getGPSStatus());
								prepareCall.setFloat(13, Float.parseFloat(PDU.getOdometer()));
								prepareCall.setString(14, PDU.getIO_CFG());
								prepareCall.setString(15, PDU.getIO_STATE());

								prepareCall.setInt(16, Integer.parseInt(PDU.getInputEvent()));

								prepareCall.setInt(17, Integer.parseInt(PDU.getIgnitionState()));						
								prepareCall.setString(18,Temperatura1);

								prepareCall.setString(19,Temperatura2);		//TEMPERATURA 2

								prepareCall.setLong(20,-1);	

								prepareCall.setLong(21,Long.parseLong(PDU.getHorometer()));

								prepareCall.setString(22,PDU.getIP() + ":"	+ PDU.getPuerto());
								prepareCall.setInt(23,PDU.getIdProtocolo());

								try{
									prepareCall.setFloat(24, Float.parseFloat(PDU.getMaxSpeed()));
								}
								catch(Exception e){
									System.out.println("ERROR 24 - GETMAXSPEED");
								}

								try{
									prepareCall.setLong(25,Long.parseLong(PDU.getTimeMaxSpeed()));
								}
								catch(Exception e){
									System.out.println("ERROR 25");
								}

								try{
									prepareCall.setFloat(26, Float.parseFloat(PDU.getSpeedSetPoint()));
								}
								catch(Exception e){
									System.out.println("ERROR 26");
								}

								try{
									prepareCall.setFloat(27, Float.parseFloat(PDU.getEventoValor()));
								}
								catch(Exception e){
									System.out.println("ERROR 27");
								}

								try{
									prepareCall.setFloat(28, Float.parseFloat(PDU.getEventoValorSP()));
								}
								catch(Exception e){
									System.out.println("ERROR 28");
								}

								ResultSet executeQuery = prepareCall.executeQuery();

								while (executeQuery.next()) {						
									String Mensaje = executeQuery.getString(1);						
									if (Mensaje.length()>0) System.out.println(Mensaje);								
								}


								pg++;

							}catch (SQLException se){
								PDU = null;
								this.CerrarDB();
								System.err.println("SQLException: PositionReportToDBEngine SP-Hades 1: " + se.toString());


							}
							catch (Exception e)	{
								System.err.println("SQLException: PositionReportToDBEngine SP-Hades 2:  " + e.toString());
							}


						}

						PDU = null;
					}
					else
					{
						try{ sleep(1000); }catch ( Exception e ){ }
					}
				}

				if (System.currentTimeMillis() - t > 60000)
				{
					if (BufferEntrada.tamano() > 0)
						System.err.println("MSG: PositionReportToDBEngine: Run: PDU guardados "
								+ pg
								+ ": Buffer Pediente de Guardar "
								+ BufferEntrada.tamano()
								+ ": Durante "
								+ (System.currentTimeMillis() - t)
								+ " milisegundos");

					t = System.currentTimeMillis();

					pg = 0;

				}

				try{ sleep(30);	}catch( Exception e ){	}

			}
			catch(Exception e){
				try{
					System.err.println("Exception: PositionReportToDBEngine: Run: " + e.toString());
					System.err.println(C2);
				}catch (Exception e2) 
				{
					System.err.println("ERROR: PositionReportToDBEngine: Run: Error para visualizar motivo de excepcion");
				}
			}
			catch(Throwable e){
				System.err.println("ERROR: PositionReportToDBEngine: ");
				e.printStackTrace();
			}
		}
	}

	public void finalize() 
	{
		try {

			CerrarDB();

			if (this.getState().compareTo(Thread.State.TERMINATED) != 0)
				System.err.println("MSG: PositionReportToDBEngine: Finalize: " + this.getState());

			super.finalize();

		}catch (Throwable e) 
		{
			System.err.println("ERROR: PositionReportToDBEngine: Finalize: ");
			e.printStackTrace();
		}
	}

	public PositionReportToDBEngine(AppProperty tmpAP) 
	{
		try {
			this.setName(this.getName() + ": PositionReportPDUToDBEngine");

			AP = tmpAP;

			DBParameters = AP.getProperty("JdbcUrl") + "://"
					+ AP.getProperty("DBGpsUnitX_Address") + ":"
					+ AP.getProperty("DBGpsUnitX_Port") + ";Database="
					+ AP.getProperty("DBGpsUnitX_Name") + ";User="
					+ AP.getProperty("DBGpsUnitX_User") + ";Password="
					+ AP.getProperty("DBGpsUnitX_PWD");

			if( AP.getProperty( "DebugUDPAPItoDBError" ).compareTo( "true" ) == 0 )
				DebugERROR = true;
			else
				DebugERROR = false;

			if( AP.getProperty( "DebugUDPAPItoDBTran" ).compareTo( "true" ) == 0 )
				DebugTRAN = true;
			else
				DebugTRAN = false;

			this.start();

		}catch (Exception e) 
		{
			try{
				if (DebugERROR)
					System.err.println("ERROR: PositionReportPDUToDBEngine: " + e.toString());
			}catch (Exception e2) 
			{
				System.err.println("ERROR: PositionReportPDUToDBEngine: Error para visualizar motivo de excepcion");
			}
			DBParameters = "";
		}

	}

}
