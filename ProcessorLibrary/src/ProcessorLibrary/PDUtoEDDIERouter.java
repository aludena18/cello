//V3.0.0

package ProcessorLibrary;

import java.io.OutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import PDULibrary.AppProperty;
import PDULibrary.GestorCola;
import PDULibrary.PDU;

public class PDUtoEDDIERouter extends Thread {
	AppProperty AP;
	int Puerto = 0;
	String Servidor = null;

	PDU PDU;
	Socket Conexion = null;
	OutputStream OS;

	String NombreServidor = null;

	boolean DebugERROR, DebugTRAN;

	String DBParameters;
	Connection ConexionBase = null;
	Statement stm = null;
	ResultSet rs = null;

	private int IntentoConexionDB = 0;

	public GestorCola BufferEntrada = new GestorCola();

	private void AbrirConexionDB() {
		try {

			IntentoConexionDB++;
			ConexionBase = DriverManager.getConnection(DBParameters);
			IntentoConexionDB = 0;

		} catch (SQLException e) {
			if (IntentoConexionDB == 5) {
				System.err
						.println("ERROR: ProcessorXVMtoEDDIERouter: AbrirConexionDB: "
								+ e.toString());
				IntentoConexionDB = 0;
			}
			CerrarConexionDB();

		} catch (Exception EXC) {
			System.err
					.println("ERROR: ProcessorXVMtoEDDIERouter: AbrirConexionDB: "
							+ EXC.toString());
			CerrarConexionDB();
		} catch (Throwable T) {
			System.err
					.println("ERROR: ProcessorXVMtoEDDIERouter: AbrirConexionDB: ");
			T.printStackTrace();
			CerrarConexionDB();
		} finally {
			if (ConexionBase == null) {
				try {
					Thread.sleep(2000);
				} catch (Exception ee) {

				}
			}
		}
	}

	private void CerrarConexionDB() {
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
			if (ConexionBase != null)
				ConexionBase.close();
			ConexionBase = null;

		} catch (Exception ee) {
			ConexionBase = null;
		}
	}

	public void run() {
		while (true) {
			if (BufferEntrada.tamano() > 0) {
				try {

					if (ConexionBase == null)
						AbrirConexionDB();

					if (ConexionBase != null) {
						PDU = (PDU) BufferEntrada.getElemento();

						stm = ConexionBase.createStatement();

						rs = stm.executeQuery("select s.NombreServidor, s.IP, S.Puerto from gpsunitservidor as gus, servidor as s "
								+ "with (nolock) "
								+ "where gus.idservidor = s.idservidor "
								+ "and gus.id = '" + PDU.getModemID() + "'");

						if (rs.next() == true) {
							Servidor = rs.getString(2);
							Puerto = rs.getInt(3);

							try {
								if (rs != null) {
									rs.close();
									rs = null;
								}
							} catch (Exception e) {
								rs = null;
							}
							try {
								if (stm != null) {
									stm.close();
									stm = null;
								}
							} catch (Exception e) {
								stm = null;
							}

						} else {
							Servidor = null;
							Puerto = 0;
						}
					}
				} catch (SQLException se) {
					System.err
							.println("SQLException: PDUtoEDDIERouter: Run: BASE DE DATOS: "
									+ se.toString());
					Servidor = null;
					Puerto = 0;
					CerrarConexionDB();

				} catch (Exception e) {
					System.err
							.println("Exception: PDUtoEDDIERouter: Run: BASE DE DATOS: "
									+ e.toString());
					Servidor = null;
					Puerto = 0;
				}

				if (Servidor != null && Puerto != 0) {
					try {
						Conexion = new Socket(Servidor, Puerto);
						OS = Conexion.getOutputStream();
						OS.write((new EDDIEParser(PDU)).toEDDIE().getBytes());

						OS.close();
						Conexion.close();

					} catch (Exception e2) {

						BufferEntrada.putElemento(PDU);
						try {
							OS.close();
							OS = null;
						} catch (Exception ee) {
							OS = null;
						}

						try {
							Conexion.close();
							Conexion = null;
						} catch (Exception ee) {
							Conexion = null;
						}
					}
				}
			} else {
				CerrarConexionDB();
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

			CerrarConexionDB();

			super.finalize();

		} catch (Throwable e) {

		}

	}

	public PDUtoEDDIERouter(AppProperty tmpAP) {
		try {
			this.setName(this.getName() + ": ProcessorXVMtoEDDIERouter");

			AP = tmpAP;

			DBParameters = AP.getProperty("JdbcUrl") + "://"
					+ AP.getProperty("DBGpsUnitAddress") + ":"
					+ AP.getProperty("DBGpsUnitPort") + ";Database="
					+ AP.getProperty("DBGpsUnitName") + ";User="
					+ AP.getProperty("DBGpsUnitUser") + ";Password="
					+ AP.getProperty("DBGpsUnitPWD");

			this.start();
		} catch (Exception e) {
			System.err.println("ERROR: ProcessorXVMtoEDDIERouter: InitServer: "
					+ e.toString());
		}
	}

}
