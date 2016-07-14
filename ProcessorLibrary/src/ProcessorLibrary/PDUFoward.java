//V3.0.0

package ProcessorLibrary;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import PDULibrary.AppProperty;
import PDULibrary.GestorCola;
import PDULibrary.PDU;

public class PDUFoward extends Thread
{
	AppProperty AP;
	int Puerto;
	DatagramSocket UDPServidor;

	DatagramPacket packet2;
	String NombreServidor = null;

	PDU PDU = null;

	String strIPDestino;

	public GestorCola BufferEntrada = new GestorCola();
	
	String DBParameters;
	int IntentoConexionDB;
	
	Connection ConexionBase = null;
	Statement stm = null;
	ResultSet rs = null;
	
	private int FowardType=1;
	
	/*
	 * 1 : Dinamico
	 * 2 : Fijo
	 * 
	 */
	
	private String DireccionDestino;
	private int PuertoDestino;
	
	private void AbrirConexionDB() 
	{
		try {

			IntentoConexionDB++;
			ConexionBase = DriverManager.getConnection(DBParameters);
			IntentoConexionDB = 0;

		} catch (SQLException e) {
			if (IntentoConexionDB == 5) {
				System.err
						.println("ERROR: PDUFoward: AbrirConexionDB: "
								+ e.toString());
				IntentoConexionDB = 0;
			}
			CerrarConexionDB();

		} catch (Exception EXC) {
			System.err
					.println("ERROR: PDUFoward: AbrirConexionDB: "
							+ EXC.toString());
			CerrarConexionDB();
		} catch (Throwable T) {
			System.err
					.println("ERROR: PDUFoward: AbrirConexionDB: ");
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

	public void run()
	{
		InetAddress iaIPDestino;
		byte[] btPDUToSend;
		
		System.err.println("MSG: PDUFoward: Inicializado con parametros ");

		String Servidor=null;
		int Puerto=0;
		
		while ( true )
		{
			try {

				while (BufferEntrada.tamano() > 0) 
				{
					if( FowardType == 1 )
					{	
						if( ConexionBase == null )
							this.AbrirConexionDB();
						
						if( ConexionBase != null)
						{	
							PDU = (PDU) BufferEntrada.getElemento();
							
							if (PDU.isPositionReportPDU() == true) 
							{
								stm = ConexionBase.createStatement();
		
								rs = stm.executeQuery("select s.NombreServidor, s.IP, S.Puerto from gpsunitservidorfoward  as gus, servidor as s "
										+ "with (nolock) "
										+ "where gus.idservidor = s.idservidor "
										+ "and gus.id = '" + PDU.getModemID() + "'");
		
								while( rs.next() == true)
								{
									Servidor = rs.getString(2);
									Puerto = rs.getInt(3);
									
									try {
										iaIPDestino = InetAddress.getByName(Servidor);
									}catch (Exception e) 
									{
										iaIPDestino = null;
										System.err.println("ERROR: PDUFoward: No se pudo obtener dirección destino " + e.toString());
									}
									
									btPDUToSend = PDU.getB_PDU();
				
									packet2 = new DatagramPacket(btPDUToSend,btPDUToSend.length, iaIPDestino,Puerto );
									UDPServidor.send(packet2);
									
								}
								try {
									if (rs != null) 
									{
										rs.close();
										rs = null;
									}
								}catch (Exception e) 
								{
									rs = null;
								}
								try{
									if (stm != null) 
									{
										stm.close();
										stm = null;
									}
								}catch (Exception e) 
								{
									stm = null;
								}
							}
						}
					}
					else if( FowardType == 2)
					{
						PDU = (PDU) BufferEntrada.getElemento();
						
						try {
							iaIPDestino = InetAddress.getByName(DireccionDestino);
						}catch (Exception e) 
						{
							iaIPDestino = null;
							System.err.println("ERROR: PDUFoward: No se pudo obtener dirección destino " + e.toString());
						}
						
						btPDUToSend = PDU.getB_PDU();
						packet2 = new DatagramPacket(btPDUToSend,btPDUToSend.length, iaIPDestino,PuertoDestino );
						UDPServidor.send(packet2);
						System.err.println("PDU Fowared: " + PDU.getIP() + ":" + PDU.getPuerto() + " >>> " + DireccionDestino + ":" + PuertoDestino + "::" + btPDUToSend.length );
					}
				}

				try{
					Thread.sleep(50);
				}catch (Exception ee) 
				{
				}

			}catch (Exception e) 
			{
				System.err.println( "Exception: PDUFoward: " + e.toString() );
				System.err.println( "FowardType " + FowardType );
				BufferEntrada.putElemento(PDU);
				this.CerrarConexionDB();
			}
		}
	}

	public void finalize()
	{
		try {
			if (this.getState().compareTo(Thread.State.TERMINATED) != 0)
				System.err.println("MSG: PDUFoward: Finalize: " + this.getState());

			super.finalize();

		}catch (Throwable e) 
		{
			System.err.println("ERROR: PDUFoward: Finalize: ");
			e.printStackTrace();
		}
	}

	public PDUFoward(DatagramSocket tmpUDPServidor, String tmpNombreServidor,AppProperty tmpAP) 
	{
		Init(tmpUDPServidor, tmpNombreServidor, tmpAP);
	}
	
	public PDUFoward(DatagramSocket tmpUDPServidor, String tmpNombreServidor,AppProperty tmpAP, int tmpFowardType, String tmpDireccionDestino, int tmpPuertoDestino ) 
	{
		FowardType = tmpFowardType;
		DireccionDestino = tmpDireccionDestino;
		PuertoDestino = tmpPuertoDestino;
		Init(tmpUDPServidor, tmpNombreServidor, tmpAP);
	}

	private void Init(DatagramSocket tmpUDPServidor, String tmpNombreServidor, AppProperty tmpAP) {
		try {
			this.setName(this.getName() + ": PDUFoward");

			AP = tmpAP;
			UDPServidor = tmpUDPServidor;
			NombreServidor = tmpNombreServidor;
			
			DBParameters = AP.getProperty("JdbcUrl") + "://"
					+ AP.getProperty("DBGpsUnitAddress") + ":"
					+ AP.getProperty("DBGpsUnitPort") + ";Database="
					+ AP.getProperty("DBGpsUnitName") + ";User="
					+ AP.getProperty("DBGpsUnitUser") + ";Password="
					+ AP.getProperty("DBGpsUnitPWD");

			this.start();

		} catch (Exception e) {
			System.err.println("ERROR: PDUFoward: InitServer: " + e.toString());
		}
	}

}