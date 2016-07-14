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

public class OdometerEngine extends Thread {
	AppProperty AP;
	PDU PDU;
	String DBParameters;

	public GestorCola BufferEntrada = new GestorCola();
	Connection DBConection = null;// ,DBConection2=null;
	Statement stm, stm1, stm2, stm3, stm4;
	ResultSet rs = null, rs2 = null;

	boolean DebugERROR, DebugTRAN;

	private void AbrirDB() {
		try {
			DBConection = DriverManager.getConnection(DBParameters);
		} catch (SQLException e) {
			System.err.println("SQLException: OdometerEngine: AbrirDB: "
					+ e.toString());
			CerrarDB();

		} catch (Exception EXC) {
			System.err.println("Exception: OdometerEngine: AbrirDB: "
					+ EXC.toString());
			CerrarDB();
		} catch (Throwable T) {
			System.err.println("ERROR: OdometerEngine: AbrirDB: ");
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
			if (rs != null) {
				rs.close();
				rs = null;
			}
		} catch (Exception e) {
			rs = null;
		}

		try {
			if (rs2 != null) {
				rs2.close();
				rs2 = null;
			}
		} catch (Exception e) {
			rs2 = null;
		}

		try {
			if (stm != null) {
				stm.close();
				stm = null;
			}
		} catch (Exception e) {
			stm = null;
		}
		try {
			if (stm1 != null) {
				stm1.close();
				stm1 = null;
			}
		} catch (Exception e) {
			stm1 = null;
		}
		try {
			if (stm2 != null) {
				stm2.close();
				stm2 = null;
			}
		} catch (Exception e) {
			stm2 = null;
		}

		try {
			if (stm3 != null) {
				stm3.close();
				stm3 = null;
			}
		} catch (Exception e) {
			stm3 = null;
		}

		try {
			if (stm4 != null) {
				stm4.close();
				stm4 = null;
			}
		} catch (Exception e) {
			stm4 = null;
		}

		try {
			if (DBConection != null) {
				DBConection.close();
				DBConection = null;
			}
		} catch (Exception e2) {
			DBConection = null;
		}

	}

	public void run() {
		PDU PDU;
		boolean continuar = true;

		while (true) {
			try {

				if (BufferEntrada.tamano() > 0) {
					if (DBConection == null)
						AbrirDB();

					if (DBConection != null) {
						continuar = true;
						PDU = (PDU) BufferEntrada.getElemento();

						stm = DBConection.createStatement();

						try {
							stm.execute("INSERT INTO LECTURAODOMETRO VALUES('"
									+ PDU.getModemID()
									+ "','"
									+ PDU.getDateTimeSQL()
									+ "',"
									+ (int) (Integer.parseInt(PDU.getOdometer()) / 1000)
									+ ")");
						} catch (Exception e) {
							continuar = false;
						} finally {
							if (stm != null) {
								try {
									stm.close();
									stm = null;
								} catch (Exception e) {
									stm = null;
								}
							}
						}

						stm4 = DBConection.createStatement();
						try {
							stm4.execute("exec spVehiculoKilometrajeActualizar '"
									+ PDU.getModemID()
									+ "',"
									+ PDU.getOdometer()
									+ ",'"
									+ PDU.getDateTimeSQL() + "'");
						} catch (Exception e5) {
							System.err.println("ERROR: OdometerEngine: Run: "
									+ e5.toString());
							continuar = false;
						} finally {
							try {
								if (stm4 != null) {
									stm4.close();
									stm4 = null;
								}
							} catch (Exception e) {
								stm4 = null;
							}
						}

						if (continuar) {
							stm1 = DBConection.createStatement();

							rs = stm1
									.executeQuery("select SGLK.IdGrupo, SGLK.IdSubgrupo, SGLK.Limite,SGLK.PorcentajeAlerta "
											+ "from VehiculoSubGrupoMonitoreoOdometro as VSG, SubGrupoMonitoreoOdometroLimiteKilometraje as SGLK "
											+ "where VSG.vid like '"
											+ PDU.getModemID()
											+ "' "
											+ "and "
											+ (int) (Integer.parseInt(PDU
													.getOdometer()) / 1000)
											+ ">=SGLK.Limite*(1-convert(float,SGLK.PORCENTAJEALERTA)/100) "
											+ "and SGLK.IdGrupo = VSG.IdGrupo and SGLK.IdSubGrupo = VSG.IdSubGrupo "
											+ "order by SGLK.IdGrupo, SGLK.IdSubgrupo,SGLK.Limite");

							String StrGrupo = null, StrSubGrupo = null;
							long Limite;

							while (rs.next()) {
								StrGrupo = rs.getString(1);
								StrSubGrupo = rs.getString(2);
								Limite = rs.getInt(3);

								if ((StrGrupo != null || StrSubGrupo != null)
										&& (StrGrupo.length() > 0 && StrSubGrupo
												.length() > 0)) {
									String ScriptSelect = "SELECT * FROM ALERTALIMITEKILOMETRAJE WHERE IDGRUPO = "
											+ StrGrupo
											+ " AND IdSubgrupo = "
											+ StrSubGrupo
											+ " AND Limite = "
											+ Limite
											+ " AND VID like '"
											+ PDU.getModemID() + "'";
									stm2 = DBConection.createStatement();
									rs2 = stm2.executeQuery(ScriptSelect);

									if (rs2.next() == false) {
										String ScriptInsert = "INSERT INTO ALERTALIMITEKILOMETRAJE VALUES("
												+ StrGrupo
												+ ","
												+ StrSubGrupo
												+ ","
												+ Limite
												+ ",'"
												+ PDU.getModemID()
												+ "','"
												+ PDU.getDateTimeSQL()
												+ "',"
												+ Integer.parseInt(PDU
														.getOdometer())
												/ 1000
												+ ",null,null)";
										stm3 = DBConection.createStatement();

										try {
											stm3.executeUpdate(ScriptInsert);
										} catch (Exception eee) {

										} finally {
											try {
												if (stm3 != null) {
													stm3.close();
													stm3 = null;
												}
											} catch (Exception eee) {
												stm3 = null;
											}
										}

									}

									try {

										if (rs2 != null) {
											rs2.close();
											rs2 = null;
										}
									} catch (Exception e) {
										rs2 = null;
									}

									try {

										if (stm2 != null) {
											stm2.close();
											stm2 = null;
										}
									} catch (Exception e) {
										stm2 = null;
									}
								}
							}
						}

						try {

							if (rs != null) {
								rs.close();
								rs = null;
							}
						} catch (Exception e) {
							rs = null;
						}

						try {

							if (stm1 != null) {
								stm1.close();
								stm1 = null;
							}
						} catch (Exception e) {
							stm1 = null;
						}
					}
				}

				PDU = null;

				try {
					Thread.sleep(50);
				} catch (Exception e) {

				}
			} catch (SQLException se) {
				System.err.println("SQLException: OdometerEnginte: Run: "
						+ se.toString());
				CerrarDB();
			} catch (Exception e) {
				System.err.println("Exception: OdometerEnginte: Run: "
						+ e.toString());
				CerrarDB();
			} catch (Throwable e) {
				System.err.println("ERROR: OdometerEngine: ");
				e.printStackTrace();
			}
		}
	}

	public OdometerEngine(AppProperty tmpAP) {
		try {

			this.setName(this.getName() + ": OdometerEngine ");

			AP = tmpAP;

			DBParameters = AP.getProperty("JdbcUrl") + "://"
					+ AP.getProperty("DBOdometerAddress") + ":"
					+ AP.getProperty("DBOdometerPort") + ";Database="
					+ AP.getProperty("DBOdometerName") + ";User="
					+ AP.getProperty("DBOdometerUser") + ";Password="
					+ AP.getProperty("DBOdometerPWD");

			if (AP.getProperty("DebugUDPAPIOdometerError").compareTo("true") == 0)
				DebugERROR = true;
			else
				DebugERROR = false;

			if (AP.getProperty("DebugUDPAPIOdometerTran").compareTo("true") == 0)
				DebugTRAN = true;
			else
				DebugTRAN = false;

			this.start();

		} catch (Exception e) {
			try {
				if (DebugERROR)
					System.err
							.println("ERROR: OdometerEngine: " + e.toString());
			} catch (Exception e2) {
				System.err
						.println("ERROR: OdometerEngine: Error para visualizar motivo de excepcion");
			}
			DBParameters = "";
		}

	}

}
