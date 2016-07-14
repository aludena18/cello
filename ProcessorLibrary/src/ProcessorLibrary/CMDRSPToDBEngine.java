//V3.0.0

package ProcessorLibrary;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import PDULibrary.AppProperty;
import PDULibrary.GestorCola;
import PDULibrary.PDU;

public class CMDRSPToDBEngine extends Thread {
	AppProperty AP;
	PDULibrary.PDU PDU;
	String DBParameters;

	Connection DBConection = null;
	Statement stmINS = null, stmSEL = null;
	ResultSet rs = null;
	String C = null;

	private int IntentoConexionDB = 0;

	public GestorCola BufferEntrada = new GestorCola();

	boolean DebugERROR, DebugTRAN;

	private void AbrirDB() {
		try {

			IntentoConexionDB++;
			DBConection = DriverManager.getConnection(DBParameters);
			IntentoConexionDB = 0;

		} catch (SQLException e) {
			if (IntentoConexionDB == 5) {
				System.err
						.println("ERROR: ProcessorUDPAPIPDUCMDRSPToDB: AbrirDB: "
								+ e.toString());
				IntentoConexionDB = 0;
			}
			CerrarDB();

		} catch (Exception EXC) {
			System.err.println("ERROR: ProcessorUDPAPIPDUCMDRSPToDB: AbrirDB: "
					+ EXC.toString());
			CerrarDB();
		} catch (Throwable T) {
			System.err
					.println("ERROR: ProcessorUDPAPIPDUCMDRSPToDB: AbrirDB: ");
			T.printStackTrace();
			CerrarDB();
		} finally {
			if (DBConection == null) {
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
		String VID = "";

		while (true) {

			if (BufferEntrada.tamano() > 0) {
				if (DBConection == null)
					this.AbrirDB();

				if (DBConection != null) {
					try {

						PDU = (PDU) BufferEntrada.getElemento();

						if (PDU.isCMDResponse()) 
						{
							
							VID = PDU.getModemID();

							if (VID == null) 
							{
								stmSEL = DBConection.createStatement();

								rs = stmSEL.executeQuery("SELECT ID FROM REPORTEPOSICION_LAST WITH (NOLOCK) WHERE SOURCE LIKE '"
												+ PDU.getIP()
												+ ":%' ORDER BY FECHAHORA DESC");

								if (rs.next()) {
									VID = rs.getString(1).trim();

								} else {
									VID = PDU.getIP() + ":" + PDU.getPuerto();

								}
							}

						} else {
							C = null;
						}

					} catch (Exception e) {
						C = null;
					} catch (Throwable e) {
						C = null;
						System.err.println("ERROR: CMDRSPToDBEngine: ");
						e.printStackTrace();
					} finally {
						try {
							if (rs != null) {
								rs.close();
								rs = null;
							}
						} catch (Exception e) {
							rs = null;
						}

						try {
							if (stmSEL != null) {
								stmSEL.close();
								stmSEL = null;
							}
						} catch (Exception e) {
							stmSEL = null;
						}

					}

					try {

						String[] CS;
						CS = PDU.getCMDResponse().split(
								"" + (char) 0x0D + "" + (char) 0x0A);

						if (CS != null) {
							for (int i = 0; i < CS.length; i++) {
								if (CS[i] != null && CS[i].length() > 1) {
									C = "INSERT INTO CMDRESPONSE VALUES('"
											+ PDU.getDateTimeSQL() + "','"
											+ VID + "','" + CS[i].trim() + "')";

									System.err.println("MSG: CMDRSPToDBEngine: Run: Se recibio respuesta de CMD de "
													+ VID
													+ ":>>>"
													+ CS[i].trim() + "<<<");

									stmINS = DBConection.createStatement();

									try {
										stmINS.execute(C);
										if (stmINS != null) {
											stmINS.close();
											stmINS = null;
										}
									} catch (Exception e) {
										System.err.println("ERROR: "
												+ e.toString());
										stmINS = null;
									}
								}
							}

						}

					} catch (Exception e) {
						if (e.toString().indexOf("PRIMARY KEY") > 0) {

						} else {
							BufferEntrada.putElemento(PDU);
							CerrarDB();
						}
					} catch (Throwable e) {
						C = null;
						System.err.println("ERROR: CMDRSPToDBEngine: ");
						e.printStackTrace();
					} finally {

						try {
							if (stmINS != null) {
								stmINS.close();
								stmINS = null;
							}
						} catch (Exception e) {
							stmINS = null;
						}
					}
				}

			}

			PDU = null;

			try {
				Thread.sleep(20);
			} catch (Exception e) {

			}

		}
	}

	public void finalize() {
		try {

			CerrarDB();

			if (this.getState().compareTo(Thread.State.TERMINATED) != 0)
				System.err.println("MSG: CMDRSPToDBEngine: Finalize: "
						+ this.getState());

			super.finalize();

		} catch (Throwable e) {
			System.err.println("ERROR: CMDRSPToDBEngine: Finalize: ");
			e.printStackTrace();
		}

	}

	public CMDRSPToDBEngine(AppProperty tmpAP) {
		try {

			this.setName(this.getName() + ": CMDRSPToDBEngine");

			AP = tmpAP;

			DBParameters = AP.getProperty("JdbcUrl") + "://"
					+ AP.getProperty("DBGpsUnitX_Address") + ":"
					+ AP.getProperty("DBGpsUnitX_Port") + ";Database="
					+ AP.getProperty("DBGpsUnitX_Name") + ";User="
					+ AP.getProperty("DBGpsUnitX_User") + ";Password="
					+ AP.getProperty("DBGpsUnitX_PWD");

			this.start();
		} catch (Exception e) {
			try {
				if (DebugERROR)
					System.err.println("ERROR: CMDRSPToDBEngine: "
							+ e.toString());
			} catch (Exception e2) {
				System.err
						.println("ERROR: CMDRSPToDBEngine: Error para visualizar motivo de excepcion");
			}
			DBParameters = "";
		}

	}

}
