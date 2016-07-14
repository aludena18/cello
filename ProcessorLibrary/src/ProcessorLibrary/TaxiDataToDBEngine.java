/*
 * VERSION ACTUAL 1.0.0
 * INCLUIDA DESDE LA VERSION 6.0.0 
 * 2009-09-15
 *  
 * 
 */
package ProcessorLibrary;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import PDULibrary.AppProperty;
import PDULibrary.GestorCola;
import PDULibrary.PDU;

public class TaxiDataToDBEngine extends Thread {
	AppProperty AP;
	PDU PDU;
	String DBParametersX;

	Connection DBConectionX = null;

	private int IntentoConexionDB = 0;

	Statement stmXINS;

	boolean DebugERROR, DebugTRAN, procesar = false;

	public GestorCola BufferEntrada = new GestorCola();

	private void AbrirDBX() {
		try {

			IntentoConexionDB++;
			DBConectionX = DriverManager.getConnection(DBParametersX);
			IntentoConexionDB = 0;

		} catch (SQLException e) {
			if (IntentoConexionDB == 5) {
				System.err.println("ERROR: TaxiDataToDBEngine: AbrirDBX: "
						+ e.toString());
				IntentoConexionDB = 0;
			}
			CerrarDBX();

		} catch (Exception EXC) {
			System.err.println("ERROR: TaxiDataToDBEngine: AbrirDBX: "
					+ EXC.toString());
			CerrarDBX();
		} catch (Throwable T) {
			System.err.println("ERROR: TaxiDataToDBEngine: AbrirDBX: ");
			T.printStackTrace();
			CerrarDBX();

		} finally {
			if (DBConectionX == null) {
				try {

					Thread.sleep(2000);
				} catch (Exception ee) {

				}
			}
		}

	}

	private void CerrarDBX() {

		try {
			if (stmXINS != null) {
				stmXINS.close();
				stmXINS = null;
			}
		} catch (Exception e) {
			stmXINS = null;
		}
		try {
			if (DBConectionX != null) {
				DBConectionX.close();
				DBConectionX = null;
			}
		} catch (Exception e2) {
			DBConectionX = null;
		}
	}

	public void finalize() {
		try {

			CerrarDBX();

			super.finalize();

		} catch (Throwable e) {
		}
	}

	public void run() {
		long t = System.currentTimeMillis();
		long pg = 0;

		while (true) {
			try {

				if (BufferEntrada.tamano() > 0) {
					if (DBConectionX == null) {
						AbrirDBX();
					}

					if (DBConectionX != null) {
						PDU = (PDU) BufferEntrada.getElemento();

						if (PDU != null && PDU.isTaxiDataPDU()) {
							stmXINS = DBConectionX.createStatement();

							if (PDU.isTaxiDataEstadoPDU()) // >RUS
							{
								stmXINS.executeUpdate("INSERT INTO DISPOSITIVOESTADO VALUES('"
										+ PDU.getModemID()
										+ "','"
										+ PDU.getDateTimeSQL()
										+ "','"
										+ PDU.getEstado() + "')");
							} else if (PDU.isTaxiDataMensajePDU()) // >RVQRMN
							{
								stmXINS.executeUpdate("INSERT INTO DISPOSITIVOMENSAJE VALUES('"
										+ PDU.getModemID()
										+ "','"
										+ PDU.getDateTimeSQL()
										+ "',"
										+ PDU.getNumeroMensaje()
										+ ",'"
										+ PDU.getMensaje() + "')");
							} else if (PDU.isTaxiDataTaximetroPDU()) // >RAX
							{
								stmXINS.executeUpdate("INSERT INTO DISPOSITIVOTAXIMETRO VALUES('"
										+ PDU.getDateTimeSQL()
										+ "','"
										+ PDU.getModemID()
										+ "',"
										+ PDU.getTiempoOcupado()
										+ ","
										+ (float) (PDU.getKilometraje() / 10)
										+ ","
										+ ((float) PDU.getImporteAPagar() / 10000)
										+ ","
										+ PDU.getTiempoEspera()
										+ ","
										+ PDU.getNumeroCarrera()
										+ ","
										+ PDU.getNumeroVoucher() + ")");
							}

							pg++;

							if (stmXINS != null) {
								try {
									stmXINS.close();
									stmXINS = null;
								} catch (Exception e) {
									stmXINS = null;
								}
							}
						}

						PDU = null;
					} else {
						try {
							Thread.sleep(1000);
						} catch (Exception ee) {
						}
					}

				}

				try {
					Thread.sleep(20);
				} catch (Exception e) {

				}

				if (System.currentTimeMillis() - t > 60000) {
					if (BufferEntrada.tamano() > 0)
						System.err
								.println("MSG: TaxiDataToDBEngine: Run: PDU guardados "
										+ pg
										+ ": Buffer Pediente de Guardar "
										+ BufferEntrada.tamano());

					t = System.currentTimeMillis();

					pg = 0;
				}

			} catch (SQLException se) {
				if (se.toString().compareTo("PRIMARY KEY") > 0) {
					if (stmXINS != null) {
						try {
							stmXINS.close();
							stmXINS = null;
						} catch (Exception e) {
							stmXINS = null;
						}
					}
				} else {
					System.err
							.println("SQLException: TaxiDataToDBEngine: Run: "
									+ se.toString());
					BufferEntrada.putElemento(PDU);
					PDU = null;
					CerrarDBX();
				}

			} catch (Exception e) {
				System.err.println("Exception: TaxiDataToDBEngine: Run: "
						+ e.toString());
				BufferEntrada.putElemento(PDU);
				PDU = null;
				CerrarDBX();
			} catch (Throwable te) {
				System.err.println("ERROR: TaxiDataToDBEngine: Run: "
						+ te.toString());
			}

		}
	}

	public TaxiDataToDBEngine(AppProperty tmpAP) {
		try {

			this.setName(this.getName() + ": ProcessorXVMToDB");

			AP = tmpAP;

			PDU = null;

			DBParametersX = AP.getProperty("JdbcUrl") + "://"
					+ AP.getProperty("DBGpsUnitX_Address") + ":"
					+ AP.getProperty("DBGpsUnitX_Port") + ";Database="
					+ AP.getProperty("DBGpsUnitX_Name") + ";User="
					+ AP.getProperty("DBGpsUnitX_User") + ";Password="
					+ AP.getProperty("DBGpsUnitX_PWD");

			if (AP.getProperty("DebugUDPAPItoDBError").compareTo("true") == 0)
				DebugERROR = true;
			else
				DebugERROR = false;

			if (AP.getProperty("DebugUDPAPItoDBTran").compareTo("true") == 0)
				DebugTRAN = true;
			else
				DebugTRAN = false;
			procesar = true;

			this.start();
		} catch (Exception e) {

			try {
				if (DebugERROR)
					System.err.println("ERROR: TaxiDataToDBEngine: "
							+ e.toString());
			} catch (Exception e2) {
				System.err
						.println("ERROR: TaxiDataToDBEngine: Error para visualizar motivo de excepcion");
			}

		}

	}

}
