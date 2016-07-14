
//V1.1.1

package ProcessorLibrary;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import PDULibrary.AppProperty;

public class ProcessorTaurus extends Thread 
{
	
	private Connection ConexionDB = null;

	private Statement stmSEL1, stmSEL2;

	private ResultSet rs1, rs2;

	private String DBParameters = null;

	private File FILE = null;

	private RandomAccessFile RAFILE = null;

	private String FechaHora;

	private AppProperty AP;
	
	int Año, Mes, Dia, Hora, Minuto, MinutosAtras;

	private void AbrirDB() {
		try {
			ConexionDB = DriverManager.getConnection(DBParameters);
			System.err
					.println("MSG: ProcessorTaurus: AbrirDB: Conexion a la base de datos abierta");

		} catch (SQLException e) {
			System.err.println("SQLException: ProcessorTaurus: AbrirDB: "
					+ e.toString());
			CerrarDB();
		} catch (Exception EXC) {
			System.err.println("Exception: ProcessorTaurus: AbrirDB: "
					+ EXC.toString());
			CerrarDB();
		} catch (Throwable T) {
			System.err.println("Throwable: ProcessorTaurus: AbrirDB: ");
			T.printStackTrace();
			CerrarDB();
		} finally {
			if (ConexionDB == null) {
				try {
					Thread.sleep(20000);
				} catch (Exception ee) {
				}
			}
		}
	}

	private void CerrarDB() {
		try {
			if (rs1 != null) {
				rs1.close();
				rs1 = null;
			}
		} catch (Exception e) {
			rs1 = null;
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
			if (stmSEL1 != null) {
				stmSEL1.close();
				stmSEL1 = null;
			}
		} catch (Exception e) {
			stmSEL1 = null;
		}

		try {
			if (stmSEL2 != null) {
				stmSEL2.close();
				stmSEL2 = null;
			}
		} catch (Exception e) {
			stmSEL2 = null;
		}

		try {
			if (ConexionDB != null) {
				ConexionDB.close();
				ConexionDB = null;
			}
		} catch (Exception e) {
			ConexionDB = null;
		}
	}

	private void AbrirArchivo(String Nombre) {
		try {
			FILE = new File(Nombre + ".txt");

			if (FILE.exists()) {
				System.err
						.println("MSG: ProcessorTaurus: AbrirArchivo: Archivo "
								+ FILE.getAbsolutePath()
								+ " existe. Borrando...");
				FILE.delete();
			}
			FILE.createNewFile();
			RAFILE = new RandomAccessFile(FILE, "rw");

		} catch (Exception e) {
			System.err.println("Exception: ProcessorTaurus: AbrirArchivo: "
					+ e.toString());
		}
	}

	private void CerrarArchivo() {
		try {
			if (RAFILE != null) {
				RAFILE.close();
			}
		} catch (Exception E) {

		} finally {
			FILE = null;
			RAFILE = null;
		}
	}

	public void run() 
	{
		System.err.println("MSG: ProcessorTaurus: Run: Iniciando ejecución");
		
		long t;
		
		if (DBParameters != null) 
		{
			while (ConexionDB == null) 
			{
				AbrirDB();
			}

			try 
			{

				System.err.println("MSG: ProcessorTaurus: Run: Consultando Usuarios asociados a TAURUS");

				t = System.currentTimeMillis();
				String UsuarioProcesando = null, Usuario = null;
				stmSEL1 = ConexionDB.createStatement();
				rs1 = stmSEL1.executeQuery("SELECT U.Usuario,D.IdDispositivo, A.Alias FROM Aplicacion as APP , UsuarioAplicacion as UAPP, Usuario as U,UsuarioEntidad as UE,EntidadActivo as EA, Activo as A , ActivoDispositivo as AD, Dispositivo as D WITH (NOLOCK) "
										   + "WHERE APP.Nombre = 'TAURUS' AND "
										   + "APP.IdAplicacion = UAPP.IdAplicacion AND "
										   + "UAPP.IdUsuario = U.IdUsuario AND "
										   + "U.IdUsuario = UE.IdUsuario AND "
										   + "UE.IdEntidad = EA.IdEntidad AND "
										   + "EA.IdActivo = A.IdActivo AND "
										   + "A.IdActivo = AD.IdActivo AND "
										   + "AD.IdDispositivo = D.IdDispositivo "
										   + "ORDER BY Usuario,Alias ");

				System.err.println("MSG: ProcessorTaurus: Run: Consulta finalizada en "
									+ (System.currentTimeMillis() - t)
									+ " milisegundos");

				int RegistrosLeidos=0;
				
				while (rs1.next()) 
				{
					Usuario = rs1.getString(1);

					if (UsuarioProcesando != null && Usuario.compareTo(UsuarioProcesando) != 0 && RAFILE != null) 
					{
						CerrarArchivo();
						
						if( RegistrosLeidos > 0)
							new FtpUploadFile(AP, "TAURUS", UsuarioProcesando + ".txt");
						else
							System.err.println("MSG: ProcessorTaurus: Run: No hay archvio para transmitir " );
					}

					/*
					if ( RAFILE == null) 
					 
					{
						AbrirArchivo(Usuario);
						UsuarioProcesando = Usuario;
						System.err.println("MSG: ProcessorTaurus: Run: Inicio de proceso para usuario "
											+ UsuarioProcesando);
					}
					*/

					stmSEL2 = ConexionDB.createStatement();
									
					String Query= "SELECT Convert(varchar,dateadd( HOUR , -5 , FechaHora),103) + ' ' + Convert(varchar,dateadd( HOUR , -5 , FechaHora),108) as FechaHora ,Latitud AS Latitud, Loogitud AS longitud FROM ReportePosicion WITH (NOLOCK) "
									+ "WHERE ID = '" + rs1.getString(2) + "' AND ";
					
					if( MinutosAtras >= 0 )
						Query += "dateadd( HOUR , -5 , FechaHora ) >=  dateADD( MINUTE ,-" + MinutosAtras + ",'" + FechaHora + "') and ";
					else
						Query += "dateadd( HOUR , -5 , FechaHora ) >=  dateADD( MINUTE ,"  + MinutosAtras + ",'" + FechaHora + "') and ";
								
					Query += "dateadd( HOUR , -5 , FechaHora) <= '" + FechaHora + "' order by 1 asc";

					rs2 = stmSEL2.executeQuery( Query );

					String Alias = rs1.getString(3);

					float l;
					
					RegistrosLeidos=0;
					
					while (rs2.next()) 
					{
						if ( RAFILE == null) 
						{
							AbrirArchivo(Usuario);
							UsuarioProcesando = Usuario;
							System.err.println("MSG: ProcessorTaurus: Run: Inicio de proceso para usuario " + UsuarioProcesando);
						}
						
						RegistrosLeidos++;
						RAFILE.write((Alias + (char) 0x09 + rs2.getString(1)).getBytes());

						l = rs2.getFloat(2);

						RAFILE.write(((char) 0x09 + "" + Math.abs(l)).getBytes());
						
						if (l > 0)
							RAFILE.write(("N ").getBytes());
						else
							RAFILE.write(("S ").getBytes());

						l = rs2.getFloat(3);

						RAFILE.write(("" + Math.abs(l)).getBytes());
						if (l > 0)
							RAFILE.write(("E" + (char) 0x0A).getBytes());
						else
							RAFILE.write(("W" + (char) 0x0A).getBytes());
					}

					try 
					{
						rs2.close();
					}finally 
					{
						rs2 = null;
					}

					try
					{
						stmSEL2.close();
					} finally {
						stmSEL2 = null;
					}
				}

				CerrarArchivo();

				if ( UsuarioProcesando != null && RegistrosLeidos > 0)
				{
					new FtpUploadFile(AP, "TAURUS", UsuarioProcesando + ".txt");
				}
				else
				{
					System.err.println("MSG: ProcessorTaurus: Run: No hay archvio para transmitir " );
				}

			}catch (SQLException SE) 
			{
				System.err.println("IOException: ProcessorTaurus: Run: " + SE.toString());

			}catch (IOException IOE)
			{
				System.err.println("IOException: ProcessorTaurus: Run: " + IOE.toString());
			}catch (Exception E)
			{
				System.err.println("Exception: ProcessorTaurus: Run: " + E.toString());

			}finally
			{
				CerrarDB();
				CerrarArchivo();
				System.err.println("MSG: ProcessorTaurus: Run: Finalizando Ejecución");
			}

		}
	}

	public ProcessorTaurus(AppProperty tmpAP, int tempAño, int tempMes, int tempDia, int tempHora, int tempMinuto , int tmpMinutosAtras )
	{
		try {

			this.setName(this.getName() + ": ProcessorTaurus");

			AP = tmpAP;

			Año = tempAño;
			Mes = tempMes;
			Dia = tempDia;
			Hora = tempHora;
			Minuto = tempMinuto;
			MinutosAtras = tmpMinutosAtras;
						
			if( Año >= 1900 && Año <= 3000  )
				FechaHora = "" + Año;
			
			if( Mes >= 1 && Mes <= 12 )
			{
				if( Mes >=1 && Mes <= 9 )
					FechaHora += "0" + Mes;
				else
					FechaHora += Mes;
			}
			if( Dia >= 1 && Dia <= 31 )
			{
				if( Dia >= 1 && Dia <= 9 )
					FechaHora += "0" + Dia;
				else
					FechaHora +=  Dia;
			}
			FechaHora += " ";
			
			if( Hora >= 0 && Hora <= 24 )
			{
				if( Hora >=0 && Hora <= 9 )
					FechaHora += "0" + Hora;
				else
					FechaHora += Hora;
			}
			FechaHora += ":";		
			
			if( Minuto >= 0 && Minuto <= 60 )
			{
				if( Minuto >= 0 && Minuto <= 9)
					FechaHora += "0" + Minuto;
				else
					FechaHora += Minuto;
			}
			
			FechaHora += ":00";
			
			if( FechaHora.length() != 17 )
			{
				System.err.println("ERROR: ProcessorTaurus: Parametros no validos " + FechaHora + " : " + MinutosAtras );
				return;
			}
			
			DBParameters = AP.getProperty("JdbcUrl") + "://"
					+ AP.getProperty("DBGpsUnitX_Address") + ":"
					+ AP.getProperty("DBGpsUnitX_Port") + ";Database="
					+ AP.getProperty("DBGpsUnitX_Name") + ";User="
					+ AP.getProperty("DBGpsUnitX_User") + ";Password="
					+ AP.getProperty("DBGpsUnitX_PWD");

			System.err.println("MSG: ProcessorTaurus: Inicializado con parametro " + FechaHora + " : " + MinutosAtras ); 
		

			this.start();

		} catch (Exception e) {
			System.err.println("Exception: ProcessorTaurus: " + e.toString());
			DBParameters = null;
		}

	}

}
