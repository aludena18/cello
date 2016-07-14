package ProcessorLibrary;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.CallableStatement;

//import java.sql.Time;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
//import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import PDULibrary.AppProperty;
import PDULibrary.GestorCola;
import PDULibrary.PDU;

public class AlertEngine extends Thread 
{

	AppProperty AP;
	PDU PDU;
	String DBParameters;
	String SMTPServer, AlertSource, AlertServer, BCC;

	String Rumbo = null, EGPS = null , Velocidad = null;

	int TipoAlerta = 0;
	int Sensor;
	
	float tmp,tmpId,tmpmx,tmpmn;
	
	Connection ConexionBase = null;// ,DBConection2=null;

	Statement stmSEL = null, stmSEL2 = null, stmSEL3 = null, stmINS = null,stmINS2;

	ResultSet rs1 = null, rs2 = null, rs3 = null;

	public GestorCola BufferEntrada = new GestorCola();

	int IntentoConexionDB = 0;

	Boolean Ejecutar = true;

	DecimalFormat Speedformat = new DecimalFormat("###");
	
	DecimalFormat ODOformat = new DecimalFormat("### ### ###.##");
	
	boolean DebugERROR, DebugTRAN;
	
	private int i;
	
	private int idActivo, idAlerta;
	
	//private String Correo, SMS, NombreEvento, alias;
	private String Correo, SMS,NombreEvento, alias;
	//private String[] Correos, SMSS;String htmlemail;
	//private String[] Correos;String htmlemail;
	private String[] Correos, SMSS;String htmlemail;
	
	
	
	private void establecerVelocidad()
	{
		try{
			if( PDU.getSpeed() != null || PDU.getSpeed().length() > 0 )
			{
				Velocidad = Speedformat.format( Float.parseFloat(PDU.getSpeed()) * 1.609344 );
			}
		}catch (Exception h) 
		{
			Velocidad = "";
		}

	}
	
	private float odometro; 
	
	private void establerRumbo()
    {
		try {

			if (Rumbo == null) 
			{
				int r = Integer.parseInt( PDU.getHeading() );

				/*if (r == 0)
					Rumbo = "Ninguno";
				else if ((r >= 351 && r <= 360) || (r >= 1 && r <= 10))
					Rumbo = "Norte"; 
				else if (r >= 11 && r <= 80)
					Rumbo = "NorEste";
				else if (r >= 81 && r <= 100)
					Rumbo = "Este";
				else if (r >= 101 && r <= 170)
					Rumbo = "SurEste";
				else if (r >= 171 && r <= 190)
					Rumbo = "Sur";
				else if (r >= 191 && r <= 260)
					Rumbo = "SurOeste";
				else if (r >= 261 && r <= 280)
					Rumbo = "Oeste";
				else if (r >= 281 && r <= 350)
					Rumbo = "NorOeste";*/
				if ((r == 0) || (r == 360))
					Rumbo = "Norte";
				else if (r >= 1 && r <= 89)
					Rumbo = "NorEste";
				else if (r == 90)
					Rumbo = "Este";
				else if (r >= 91 && r <= 179)
					Rumbo = "SurEste";
				else if (r == 180)
					Rumbo = "Sur";
				else if (r >= 181 && r <= 269)
					Rumbo = "SurOeste";
				else if (r == 270)
					Rumbo = "Oeste";
				else if (r >= 271 && r <= 359)
					Rumbo = "NorOeste";
			}

		} catch (Exception e) 
		{
			Rumbo = "N/D";
			System.err.println("ERROR: "
					+ e);
		}

	}

	private void establerCalidadPosicion()
    {
		try {

			if (EGPS == null) 
			{

				if (PDU.getGPSStatus().compareTo("1") == 0)
					EGPS = "ACTUALIZADA";
				else
					EGPS = "DESACTUALIZADA";
			}

		} catch (Exception e) {
			EGPS = "Desconocida";
		}

	}

	private void establecerOdometro()
	{
		try{
			odometro = (float) Integer.parseInt(PDU.getOdometer());
			odometro = odometro / 1000;

		}catch (Exception h) 
		{
			odometro = 0;
		}

	}
	
	private void SendEMAIL(String destinatario, String remitente,
			String subject, String body) {
		try {

			// System.err.println( SMTPServer + "," + destinatario + "," +
			// remitente + "," + subject + "," + body);
			Properties props = new Properties();
			props.put("mail.smtp.host", SMTPServer);
			Session session = Session.getInstance(props, null);
			MimeMessage message = new MimeMessage(session);

			MimeBodyPart htmlbody = new MimeBodyPart();
			MimeMultipart htmlmensaje = new MimeMultipart();

			message.setFrom(new InternetAddress(remitente));

			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					destinatario));

			if (BCC != null)
				message.addRecipient(Message.RecipientType.BCC,
						new InternetAddress(BCC));
			message.setSubject(subject);

			htmlbody.setContent(body, "text/html");
			htmlmensaje.addBodyPart(htmlbody);
			message.setContent(htmlmensaje);

			Transport.send(message);

		} catch (Exception e) {
			System.err.println("ERROR: AlertEngine: No se pudo enviar email: "
					+ e);
		}
	}

	private void AbrirConexionDB() {
		try {

			IntentoConexionDB++;
			ConexionBase = DriverManager.getConnection(DBParameters);
			IntentoConexionDB = 0;

		} catch (SQLException e) {
			if (IntentoConexionDB == 5) {
				System.err.println("ERROR: AlertEngine: AbrirConexionDB: "
						+ e.toString());
				IntentoConexionDB = 0;
			}
			CerrarConexionDB();

		} catch (Exception EXC) {
			System.err.println("ERROR: AlertEngine: AbrirConexionDB: "
					+ EXC.toString());
			CerrarConexionDB();
		} catch (Throwable T) {
			System.err.println("ERROR: AlertEngine: AbrirConexionDB: ");
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
			if (rs1 != null) {
				rs1.close();
				rs1 = null;
			}
		} catch (Exception e1) {
			rs1 = null;
		}
		try {
			if (rs2 != null) {
				rs2.close();
				rs2 = null;
			}
		} catch (Exception e1) {
			rs2 = null;
		}
		try {
			if (rs3 != null) {
				rs3.close();
				rs3 = null;
			}

		} catch (Exception e1) {
			rs3 = null;
		}

		try {
			if (stmSEL != null) {
				stmSEL.close();
				stmSEL = null;
			}

		} catch (Exception e1) {
			stmSEL = null;
		}

		try {
			if (stmSEL2 != null) {
				stmSEL2.close();
				stmSEL2 = null;
			}

		} catch (Exception e1) {
			stmSEL2 = null;
		}

		try {
			if (stmSEL3 != null) {
				stmSEL3.close();
				stmSEL3 = null;
			}

		} catch (Exception e1) {
			stmSEL3 = null;
		}

		try {
			if (stmINS != null) {
				stmINS.close();
				stmINS = null;
			}
		} catch (Exception e1) {
			stmINS = null;
		}

		try {
			if (stmINS2 != null) {
				stmINS2.close();
				stmINS2 = null;
			}

		} catch (Exception e1) {
			stmINS2 = null;
		}

		try {
			if (ConexionBase != null) {
				ConexionBase.close();
				ConexionBase = null;
			}

		} catch (Exception e2) {
			ConexionBase = null;
		}

	}

	private void GenerarAlertas()
	{
		try{
			
			// ENVIAR CORREOS
			try
			{
				Correos = Correo.split(",");
			}catch (Exception e2) 
			{
				Correos = null;
			}
						
			htmlemail = "<P ALIGN=CENTER STYLE=\"margin-bottom: 0cm\"><FONT SIZE=4>"
					+ alias
					+ ": "
					+ NombreEvento
					+ "</FONT></P>"
					+ "<P STYLE=\"margin-bottom: 0cm\">FECHA HORA : "
					+ PDU.getReportDateTime(-5)
					+ "</P>"
					+ "<P STYLE=\"margin-bottom: 0cm\">VELOCIDAD  : "
					+ Velocidad
					+ " KM/H</P>";


			htmlemail +="<P STYLE=\"margin-bottom: 0cm\">RUMBO      : "
					+ Rumbo + "</P>";
	
			if (odometro != 0)
				htmlemail += "<P STYLE=\"margin-bottom: 0cm\">ODOMETRO   : "
						+ ODOformat.format(Integer.parseInt(PDU.getOdometer()) / 1000)
						+ " KILOMETROS</P>";
	
			htmlemail += "<P STYLE=\"margin-bottom: 0cm\">POSICION  : "
					+ EGPS
					+ "</P>"
					+ "<P STYLE=\"margin-bottom: 0cm;text-align:justify\"><FONT FACE=\"calibri\" SIZE=1>"+ PDU.getModemID()+"</P>"
					+ "<A HREF=\"http://maps.google.com/maps/api/staticmap?center="
					+ PDU.getLatitude()
					+ ","
					+ PDU.getLongitude()
					+ "&zoom=14&size=512x512&maptype=roadmap"
					+ "&markers=color:red|label:S|"
					+ PDU.getLatitude()
					+ ","
					+ PDU.getLongitude()
					+ "&sensor=false\">"
					+ "<IMG SRC=\"http://maps.google.com/maps/api/staticmap?center="
					+ PDU.getLatitude() + "," + PDU.getLongitude()
					+ "&zoom=14&size=512x512&maptype=roadmap"
					+ "&markers=color:red|label:S|" + PDU.getLatitude()
					+ "," + PDU.getLongitude() + "&sensor=false\"></A>";
	
			i = 0;
			while (Correos != null && i < Correos.length)
			{
				System.err.println("MSG: AlertEngine: "
						+ alias + " ->> " + NombreEvento + ">>>"
						+ Correos[i]);
				this.SendEMAIL(Correos[i], AlertSource, alias + ": "
						+ NombreEvento, htmlemail);
				i++;
			}
	
			// ESCRIBIR SMS
			/*try 
			{
				SMSS = SMS.split(",");
			}catch (Exception e2)
			{
				SMSS = null;
			}
	
			i = 0;
			while (SMSS != null && i < SMSS.length) 
			{
				stmINS = ConexionBase.createStatement();
	
				String sms = "insert into BufferSmsSaliente ( FechaHoraEscritura,Usuario, MIN,SMS) values "
						+ "(GETDATE(),'geosys','"
						+ SMSS[i]
						+ "','"
						+ alias
						+ ": "
						+ NombreEvento
						+ "\n"
						+ "Fecha Hora: "
						+ PDU.getReportDateTime(-5)
						+ "\n"
						+ "Velocidad: "
						+ Speedformat.format( Integer.parseInt(PDU.getSpeed()) * 1.609344 )
						+ " km/h\nRumbo: " + Rumbo + "\n";
	
				if (odometro != 0)
					sms += "Odometro: "
							+ ODOformat.format(Integer.parseInt(PDU.getOdometer()) / 1000)
							+ " kms";
	
				sms += "')";
	
				try 
				{
					stmINS.execute(sms);
				} catch (Exception ee) 
				{
				}
	
				try 
				{
					stmINS.close();
					stmINS = null;
				} catch (Exception eeee) 
				{
					stmINS = null;
				}
	
				i++;
			}*/
		}catch( Exception e )
		{
			System.err.println("ERROR: AlertEngine: GenerarAlertas: " + e.toString() );
		}
	}
	
	
	private void GenerarAlertaTemp()
	{
		try{	
			// ENVIAR CORREOS
			try
			{
				Correos = Correo.split(",");
			}catch (Exception e2) 
			{
				Correos = null;
			}
						
			htmlemail = "<P ALIGN=CENTER STYLE=\"margin-bottom: 0cm\"><FONT SIZE=4>"
					+ alias
					+ ": "
					+ NombreEvento
					+ "</FONT></P>"
					+ "<P STYLE=\"margin-bottom: 0cm\">FECHA HORA: "
					+ PDU.getReportDateTime(-5) //Hera Change
					+ "</P>"
					+ "<P STYLE=\"margin-bottom: 0cm\">VEL: "
					+ Speedformat.format( Float.parseFloat(PDU.getSpeed())  ) //Hera Change
					+ " KM/H | RUMBO: "
					+ Rumbo + " | ";
	
			if (odometro != 0)
				htmlemail += "ODOMETRO: "
						+ ODOformat.format(Integer.parseInt(PDU.getOdometer()) / 1000)
						+ " Km</P>";
	
			htmlemail += "<P STYLE=\"margin-bottom: 0cm\">POSICION: "
					+ EGPS
					+ "</P>"
					+ "<P STYLE=\"margin-bottom: 0cm\">";
					
					if (Sensor == 1)
					{
					htmlemail += "TEMP. ACTUAL: S1: "
								+ PDU.getTemperatura1();
					}
					else if (Sensor == 2)
					{
						htmlemail += "TEMP. ACTUAL: S2: "
								+ PDU.getTemperatura2();
					}
					else if (Sensor == 3)
					{
						htmlemail += "TEMP. ACTUAL: S1: "
						+ PDU.getTemperatura1()
						+ " | S2: "
						+ PDU.getTemperatura2();
					}
			htmlemail += "</P>"
					+ "<P STYLE=\"margin-bottom: 0cm\">SP: "
					+ tmpId
					+ "�C | MAX: "
					+ tmpmx
					+ "�C | MIN: "
					+ tmpmn
					+ "�C</P>"
					+ "<P STYLE=\"margin-bottom: 0cm;text-align:justify\"><FONT FACE=\"calibri\" SIZE=1>"+ PDU.getModemID()+"</P>";	
			
			htmlemail += "<A HREF=\"http://maps.google.com/maps/api/staticmap?center="
					+ PDU.getLatitude()
					+ ","
					+ PDU.getLongitude()
					+ "&zoom=14&size=512x512&maptype=roadmap"
					+ "&markers=color:red|label:S|"
					+ PDU.getLatitude()
					+ ","
					+ PDU.getLongitude()
					+ "&sensor=false\">"
					+ "<IMG SRC=\"http://maps.google.com/maps/api/staticmap?center="
					+ PDU.getLatitude() + "," + PDU.getLongitude()
					+ "&zoom=14&size=512x512&maptype=roadmap"
					+ "&markers=color:red|label:S|" + PDU.getLatitude()
					+ "," + PDU.getLongitude() + "&sensor=false\"></A>";
	
			i = 0;
			while (Correos != null && i < Correos.length)
			{
				System.err.println("MSG: AlertEngine: "
						+ alias + " ->> " + NombreEvento + ">>>"
						+ Correos[i]);
				this.SendEMAIL(Correos[i], AlertSource, alias + ": "
						+ NombreEvento, htmlemail);
				i++;
			}
	
			// ESCRIBIR SMS
			try 
			{
				SMSS = SMS.split(",");
			}catch (Exception e2)
			{
				SMSS = null;
			}
	
			i = 0;
			while (SMSS != null && i < SMSS.length) 
			{
				stmINS = ConexionBase.createStatement();
	
				String sms = "insert into BufferSmsSaliente ( FechaHoraEscritura,Usuario, MIN,SMS) values "
						+ "(GETDATE(),'geosys','"
						+ SMSS[i]
						+ "','"
						+ alias
						+ ": "
						+ NombreEvento
						+ "\n"
						+ "Fecha Hora: "
						+ PDU.getReportDateTime(-5) //Hera Change
						+ "\n"
						+ "Velocidad: "
						+ Speedformat.format( Float.parseFloat( PDU.getSpeed())  ) //Hera Change
						+ " km/h\nRumbo: " + Rumbo + "\n";
	
				if (odometro != 0)
					sms += "Odometro: "
							+ ODOformat.format(Integer.parseInt(PDU.getOdometer()) / 1000)
							+ " kms";
	
				sms += "')";
	
				try 
				{
					stmINS.execute(sms);
				} catch (Exception ee) 
				{
				}
	
				try 
				{
					stmINS.close();
					stmINS = null;
				} catch (Exception eeee) 
				{
					stmINS = null;
				}
	
				i++;
			}
		}catch( Exception e )
		{
			System.err.println("ERROR: AlertEngine: GenerarAlertas: " + e.toString() );
		}
	}
	
	private int RegistrarAlerta()
	{
		// REGISTRAR ALERTA EN BASE DE DATOS
		

		try {
			stmINS = ConexionBase.createStatement();
			
			stmINS.execute("INSERT INTO AlertaActivo (IdAlerta,IdActivo,FechaHoraOcurrencia,Latitud,Longitud,Velocidad,Rumbo,Odometro) "
					+ "VALUES ("
					+ idAlerta
					+ ","
					+ idActivo
					+ ",'"
					+ PDU.getReportDateTime()
					+ "',"
					+ PDU.getLatitude()
					+ ","
					+ PDU.getLongitude()
					+ ","
					+ PDU.getSpeed()
					+ ","
					+ PDU.getHeading()
					+ ","
					+ PDU.getOdometer()
					+ ")");
		}catch (Exception e7) 
		{
			if( e7.toString().indexOf("PRIMARY KEY") >=0)
			{
				try{
					stmINS.close();
					stmINS = null;
				}catch (Exception e1) 
				{
					stmINS = null;
				}
				return -3;
			}
			else 
			{
				System.err.println("ERROR: AlertEngine: RegistoAlerta: " + e7.toString() );
				return -2;
			}
		}
		
		try{
			stmINS.close();
			stmINS = null;
		}catch (Exception e1)
		{
			stmINS = null;
		}
		return 0;
	}
	
	private int RegistrarAlertaTemp()
	{
		// REGISTRAR ALERTA EN BASE DE DATOS		
		try {
			stmINS = ConexionBase.createStatement();
			
			stmINS.execute("INSERT INTO AlertaActivo (IdAlerta,IdActivo,FechaHoraOcurrencia,Latitud,Longitud,Velocidad,Rumbo,Odometro,Temp,Comb) "
					+ "VALUES ("
					+ idAlerta
					+ ","
					+ idActivo
					+ ",'"
					+ PDU.getReportDateTime()
					+ "',"
					+ PDU.getLatitude()
					+ ","
					+ PDU.getLongitude()
					+ ","
					+ PDU.getSpeed()
					+ ","
					+ PDU.getHeading()
					+ ","
					+ PDU.getOdometer()
					+ ","
					+ PDU.getTemperatura1()
					+ ","
					+ PDU.getTemperatura2()
					+ ")");

		}catch (Exception e7) 
		{
			if( e7.toString().indexOf("PRIMARY KEY") >=0)
			{
				try{
					stmINS.close();
					stmINS = null;
				}catch (Exception e1) 
				{
					stmINS = null;
				}
				return -3;
			}
			else 
			{
				System.err.println("ERROR: AlertEngine: RegistoAlerta: " + e7.toString() );
				return -2;
			}
		}
		
		try{
			stmINS.close();
			stmINS = null;
		}catch (Exception e1)
		{
			stmINS = null;
		}
		return 0;
	}
	
	
	
	private int AlertaEvento()
	{

		int NumeroEvento;
		
		try {
			NumeroEvento = rs1.getInt(7);
			
			if (NumeroEvento == Integer.parseInt(PDU.getInputEvent())) 
			{
				idActivo = rs1.getInt(1);
				alias = rs1.getString(2);
				idAlerta = rs1.getInt(3);
				NombreEvento = rs1.getString(5);
				Correo = rs1.getString(11);
				//SMS = rs1.getString(12);

				if( RegistrarAlerta() == -3 )
				{
					return -3;
				}
				
				GenerarAlertas();
				
			}
			
			return 0;
		}catch (SQLException SE) 
		{
			CerrarConexionDB();
			BufferEntrada.putElemento(PDU);
			return -2;

		} catch (Exception e) 
		{
			System.err.println("ERROR: AlertEngine: AlertaEvento " + e.toString());
			return -1;
		}
	}

	private int AlertaOdometro() 
	{		
		float limite, rango;
		
		try {

			limite = (float) rs1.getInt(9);
			rango = (float) rs1.getFloat(10);

			if (limite < 0)
				limite = 0;

			if (rango < 0)
				rango = 0;

			if (odometro >= (limite * (1 - rango / 100))
					&& odometro <= (limite * (1 + rango / 100))) {
				idActivo = rs1.getInt(1);
				alias = rs1.getString(2);
				idAlerta = rs1.getInt(3);
				NombreEvento = rs1.getString(5);

				Correo = rs1.getString(11);
				//SMS = rs1.getString(12);

				// VER SI EXISTE ALERTA REGISTRADA
				stmSEL2 = ConexionBase.createStatement();
				rs2 = stmSEL2.executeQuery("SELECT * FROM AlertaActivo WITH (nolock)"
								+ "WHERE idAlerta = "
								+ idAlerta
								+ " and idactivo = " + idActivo);

				if (rs2.next())
				{
					try {
						rs2.close();
						rs2 = null;
					} catch (Exception e) {
						rs2 = null;
					}

					try {
						stmSEL2.close();
						stmSEL2 = null;
					} catch (Exception e) {
						stmSEL2 = null;
					}
					return 0;
				} else {
					try {
						rs2.close();
						rs2 = null;
					} catch (Exception e) {
						rs2 = null;
					}

					try {
						stmSEL2.close();
						stmSEL2 = null;
					} catch (Exception e) {
						stmSEL2 = null;
					}
				}

				if( RegistrarAlerta() == -3 )
				{
					return -3;
				}
				
				GenerarAlertas();
				
			}

			return 0;
		} catch (SQLException SE) {
			return -2;
		} catch (Exception e) {
			System.err.println("ERROR: AlertEngine: AlertaOdometro "
					+ e.toString());
			return -1;
		}

	}

	private int AlertaGeocerca() 
	{
		
		int idGeocerca = 0, tipoGeocerca = 0, Parametro = 0;
			
		int Distancia = 0, Distancia2 = -1;

		try {

			idGeocerca = rs1.getInt(8);

			stmSEL2 = ConexionBase.createStatement();

			rs2 = stmSEL2.executeQuery("SELECT GeoPoints.STDistance( geography::Point("
							+ PDU.getLatitude()
							+ ","
							+ PDU.getLongitude()
							+ ",4326)) AS DISTANCE, G.Tipo, G.Parametro1  FROM Geocerca as G with (nolock) "
							+ "WHERE IdGeocerca = " + idGeocerca);

			boolean AlertaActivada = false;

			if (rs2.next()) {
				Distancia = rs2.getInt(1);
				tipoGeocerca = rs2.getInt(2);
				Parametro = rs2.getInt(3);
			}

			try {
				rs2.close();
				rs2 = null;
			} catch (Exception e) {
				rs2 = null;
			}

			try {
				stmSEL2.close();
				stmSEL2 = null;
			} catch (Exception e) {
				stmSEL2 = null;
			}

			// GEO IN
			if (TipoAlerta == 3) {
				// POLIGONAL O CIRCULAR
				if ((tipoGeocerca == 1 || tipoGeocerca == 3) && Distancia == 0) {
					AlertaActivada = true;
				}
				// LINEAL
				else if (tipoGeocerca == 2 && Distancia <= Parametro) {
					AlertaActivada = true;
				}
			}
			// GEO OUT
			else if (TipoAlerta == 4) {
				// POLIGONAL O CIRCULAR
				if ((tipoGeocerca == 1 || tipoGeocerca == 3) && Distancia > 0) {
					AlertaActivada = true;
				}
				// LINEAL
				else if (tipoGeocerca == 2 && Distancia > Parametro) {
					AlertaActivada = true;
				}
			}

			// VERIFICAR SI NO SE ALERTO POR REPORTE INMEDIATAMENTE ANTERIOR
			if ( AlertaActivada ) 
			{

				stmSEL2 = ConexionBase.createStatement();

				rs2 = stmSEL2.executeQuery("SELECT TOP 1 Latitud,Loogitud FROM ReportePosicion WITH (NOLOCK) "
								+ "WHERE FechaHora < '"
								+ PDU.getReportDateTime()
								+ "' "
								+ "AND ID = '"
								+ PDU.getModemID()
								+ "' "
								+ "ORDER BY FechaHora DESC");

				if (rs2.next()) {
					stmSEL3 = ConexionBase.createStatement();
					rs3 = stmSEL3
							.executeQuery("SELECT GeoPoints.STDistance( geography::Point("
									+ rs2.getFloat(1)
									+ ","
									+ rs2.getFloat(2)
									+ ",4326)) AS DISTANCE "
									+ "FROM Geocerca as G with (nolock) "
									+ "WHERE IdGeocerca = " + idGeocerca);

					if (rs3.next()) {
						Distancia2 = rs3.getInt(1);

						// GEO IN
						if (TipoAlerta == 3) {
							// POLIGONAL O CIRCULAR
							if ((tipoGeocerca == 1 || tipoGeocerca == 3)
									&& Distancia2 == 0) {
								AlertaActivada = false;
							}
							// LINEAL
							else if (tipoGeocerca == 2
									&& Distancia2 <= Parametro) {
								AlertaActivada = false;
							}
						}

						// GEO OUT
						else if (TipoAlerta == 4) {
							// POLIGONAL o CIRCULAR
							if ((tipoGeocerca == 1 || tipoGeocerca == 3)
									&& Distancia2 > 0) {
								AlertaActivada = false;
							}
							// LINEAL
							else if (tipoGeocerca == 2
									&& Distancia2 > Parametro) {
								AlertaActivada = false;
							}
						}

					}

					try {
						rs3.close();
						rs3 = null;
					} catch (Exception e) {
						rs3 = null;
					}
					try {
						stmSEL3.close();
						stmSEL3 = null;
					} catch (Exception e) 
					{
						stmSEL3 = null;
					}

				} else
					AlertaActivada = true;

				try {
					rs2.close();
					rs2 = null;
				} catch (Exception e) {
					rs2 = null;
				}

				try {
					stmSEL2.close();
					stmSEL2 = null;
				} catch (Exception e) {
					stmSEL2 = null;
				}

			}

			// GENERAR ALERTA
			if ( AlertaActivada == true ) 
			{
				idActivo = rs1.getInt(1);
				alias = rs1.getString(2);
				idAlerta = rs1.getInt(3);
				NombreEvento = rs1.getString(5);

				Correo = rs1.getString(11);
				//SMS = rs1.getString(12);

				if( RegistrarAlerta() == -3 )
				{
					return -3;
				}				
				GenerarAlertas();
			}

			return 0;
		} catch (SQLException SE) {
			BufferEntrada.putElemento(PDU);
			CerrarConexionDB();
			return -2;
		} catch (Exception e) {
			System.err.println("ERROR: AlertEngine: AlertaGeocerca: "
					+ e.toString());
			return -1;
		}
	}

	private int AlertaMulticriterio()
	{		
		/*Calendar C;
		SimpleDateFormat sdf;
		Date D;Time T;*/
		
		boolean AlertaActivada=true;
		
		try {

			/*stmSEL2 = ConexionBase.createStatement();
			
			//System.err.println("Multicriterio  : PDU.getReportDateTime() : "+PDU.getReportDateTime()+" ,VID : "+PDU.getModemID() );

			rs2 = stmSEL2.executeQuery("SELECT TOP 1 Latitud,Loogitud,EventoEntrada,Velocidad,FechaHora FROM ReportePosicion WITH (NOLOCK) "
							+ "WHERE FechaHora < '"
							+ PDU.getReportDateTime()
							+ "' "
							+ "AND ID = '"
							+ PDU.getModemID()
							+ "' "
							+ "ORDER BY FechaHora DESC");
						
			if (rs2.next()) 
			{
				stmSEL3 = ConexionBase.createStatement();
				
				sdf = new SimpleDateFormat("HH:mm:ss");
				
				D = rs2.getDate(5);
				T = rs2.getTime(5);
				C = new GregorianCalendar();
				
				C.setTime(D);
				C.add( Calendar.MILLISECOND, (int)T.getTime());	
							
				C.add( Calendar.HOUR_OF_DAY, -10 );
				String FechaHora = sdf.format( C.getTime());	
			
				rs3 = stmSEL3.executeQuery("SELECT DA.idAlerta,DA.TipoAlerta, DA.Nombre, DA.idTipoDispositivo,DA.Evento,DA.IdGeocerca, DA.Kilometraje,DA.PorcentajeAnticipacion,EDA.EmailsAlerta,EDA.SMSAlerta,DA.HoraDesde,DA.HoraHasta,DA.LimiteVelocidad,DA.DentroGeo  "
											+ "FROM    D_Alerta as DA, EntidadD_Alerta AS EDA WITH (NOLOCK) "
											+ "WHERE   DA.IdAlerta = " + rs1.getInt(3)  + " AND "
											+ "DA.IdAlerta = EDA.IdAlerta AND "
											+ "EDA.IdEntidad IN (SELECT E.IdEntidad FROM  Entidad as E, EntidadActivo as EA, Activo as A, ActivoDispositivo as AD with (nolock) "
																+ "where  E.IdEntidad = EA.IdEntidad and "
																+ "EA.IdActivo = A.IdActivo and "
																+ "A.IdActivo = AD.IdActivo and "
																+ "AD.IdDispositivo = '" + PDU.getModemID()	+ "') AND " 
											+ "EDA.Estado = 'A' AND " 
											+ "( DA.TipoAlerta = 6 AND (" + rs2.getInt(3) + "= ISNULL(DA.Evento," +  rs2.getInt(3) + ") or DA.Evento = 0 ) AND ( ISNULL(DA.HoraDesde,'"+ FechaHora +"') <= '" + FechaHora + "' AND '" + FechaHora + "' <= ISNULL(DA.HoraHasta,'" + FechaHora + "')) AND " + rs2.getFloat(4) + " >= ISNULL(DA.LimiteVelocidad, " + rs2.getFloat(4) + ") AND dbo.CumpleCriterioGeocerca(DA.IdGeocerca,DA.DentroGeo," + rs2.getFloat(1) +"," + rs2.getFloat(2) +") = 1 AND ( DA.Evento != 0 or ( DA.IdGeocerca !=0 AND DA.DentroGeo is not null) or DA.HoraDesde IS NOT NULL or DA.HoraHasta IS NOT NULL or DA.LimiteVelocidad IS NOT NULL )   )  "
						
						);
						
				if ( rs3.next() ) 
				
					AlertaActivada = false;
				
				else*/
					AlertaActivada = true;

				/*try
				{
					rs3.close();
					rs3 = null;
				} catch (Exception e) 
				{
					rs3 = null;
				}

				
				try {
					stmSEL3.close();
					stmSEL3 = null;
				} catch (Exception e)
				{
					stmSEL3 = null;
				}
				
				try {
					rs2.close();
					rs2 = null;
				} catch (Exception e) {
					rs2 = null;
				}

				try {
					stmSEL2.close();
					stmSEL2 = null;
				} catch (Exception e) {
					stmSEL2 = null;
				}
				

			}*/

			// GENERAR ALERTA
			if ( AlertaActivada == true ) 
			{
				
				
				idActivo = rs1.getInt(1);
				alias = rs1.getString(2);
				idAlerta = rs1.getInt(3);
				NombreEvento = rs1.getString(5);

				Correo = rs1.getString(11);
				//SMS = rs1.getString(12);

				if( RegistrarAlerta() == -3 )
				{
					return -3;
				}
				
				GenerarAlertas();
							
			}

			return 0;
		} catch (SQLException SE) 
		{
			System.err.println(SE.toString());
			BufferEntrada.putElemento(PDU);
			CerrarConexionDB();
			return -2;
		} catch (Exception e) {
			System.err.println("ERROR: AlertEngine: AlertaMultiple: "
					+ e.toString());
			return -1;
		}
	}
	
	
	private int AlertaUpgrade(){	
		try {
			System.err.println("entraste store?");
			CallableStatement prepareCall = ConexionBase.prepareCall("{call dbo.sp_AlertasUpgrade(?,?,?) }");
			prepareCall.setString(1,PDU.getModemID());									
			prepareCall.setInt(2, Integer.parseInt(PDU.getUserSpecifiedNumber()));
			prepareCall.setInt(3, PDU.getIdProtocolo());			
			ResultSet executeQuery = prepareCall.executeQuery();			
			while (executeQuery.next()) {			
				alias = executeQuery.getString(2);
				idAlerta = Integer.parseInt(PDU.getUserSpecifiedNumber());
				NombreEvento = executeQuery.getString(3);
				Correo = executeQuery.getString(4);		
				idActivo=executeQuery.getInt(5);
				idAlerta=executeQuery.getInt(6);						
				if( RegistrarAlerta() == -3 )
				{return -3;	}				
				GenerarAlertas();
			}
			return 0;			
		} catch (SQLException SE) {
			System.err.println("ERROR: AlertEngine: Alerta Upgrade 1 " + SE.toString());
			CerrarConexionDB();
			BufferEntrada.putElemento(PDU);
			return -2;
		} catch (Exception e){			
			System.err.println("ERROR: AlertEngine: Alerta Upgrade 2 " + e.toString());
			return -1;			
		}		

		
	}

	
	
	private int AlertaTemperatura() 
	{
		float tmp;
		try{
		tmp = Float.valueOf(PDU.getTemperatura1());
		}catch(Exception e){
			System.err.println("ERROR: AlertEngine Datos Incorrectos " + e.toString());
			return -1;			
		}			
		boolean AlertaActivada = false;
		try {
			float tmpn = rs1.getFloat("TempBaj");
			float tmpx = rs1.getFloat("TempSub");
			if (rs1.getInt("IdSensor") == 1)
			{
				if (tmp > tmpx || tmp < tmpn)
					AlertaActivada = true;
			}
	
			if ( AlertaActivada == true ) 
			{
				idActivo = rs1.getInt("IdActivo");
				alias = rs1.getString(2);
				idAlerta = rs1.getInt(3);
				NombreEvento = rs1.getString(5);
				Correo = rs1.getString(11);
				SMS = rs1.getString(12);							
				Sensor = rs1.getInt("IdSensor");
				tmpId = rs1.getFloat("TempIdeal");
				tmpmx = rs1.getFloat("TempSub");
				tmpmn = rs1.getFloat("TempBaj");
				if( RegistrarAlertaTemp() == -3 )
				{ 	return -3; 	}				
				GenerarAlertaTemp();
			}
			return 0;
		} catch (SQLException SE) {
			CerrarConexionDB();
			BufferEntrada.putElemento(PDU);
			return -2;
		} catch (Exception e){
			System.err.println("ERROR: AlertEngine: AlertaTemperatura " + e.toString());
			return -1;			
		}		
	}
	
	public void run() 
	{
		try
		{
	
		long t = System.currentTimeMillis();
		long pg = 0;
		boolean EnviaAlertaUpgrade=true;	
		
		String Query = "",FechaHora="";
		String date,time;
		Calendar C;
		SimpleDateFormat sdf;
		double vel = 0;
		
		while (Ejecutar) 
		{
			if (BufferEntrada.tamano() > 0)
			{
				if (ConexionBase == null) 
				{
					AbrirConexionDB();
				}
				if (ConexionBase != null) 
				{
					PDU = (PDU) BufferEntrada.getElemento();
										
					try
					{
						vel = Double.parseDouble(PDU.getSpeed());
					}
					catch (Exception e)
					{
						vel = 0;
					}
					
				
					if (vel >= 124.30)
						System.err.println("ADVERTENCIA : Velocidad mayor a 200, no se consultaran alertas. Velocidad : "+vel* 1.609344+" ,VID : "+PDU.getModemID() );
					else
					{
						
						this.establerRumbo();
						this.establerCalidadPosicion();
						this.establecerOdometro();
						this.establecerVelocidad();
						
						C = new GregorianCalendar();
								
						date = PDU.getGPSDate();
						time = PDU.getGPSTime();
									
						C.set( Integer.parseInt( "20" + date.substring( 4,6) ), Integer.parseInt( date.substring(2,4))-1, Integer.parseInt( date.substring(0,2)), Integer.parseInt( time.substring(0,2)), Integer.parseInt( time.substring(2,4)), Integer.parseInt( time.substring(4,6)));
						sdf = new SimpleDateFormat("HH:mm:ss");
						C.add(Calendar.HOUR_OF_DAY, -5 );
						FechaHora = sdf.format( C.getTime());
						
						try{
						
							EnviaAlertaUpgrade=false;
							if (    (PDU.getUserSpecifiedNumber().equalsIgnoreCase("82") ||
									PDU.getUserSpecifiedNumber().equalsIgnoreCase("86") ||
									PDU.getUserSpecifiedNumber().equalsIgnoreCase("87") 
									) &&  PDU.getIdProtocolo()==99 ){								
								EnviaAlertaUpgrade=true;								
								AlertaUpgrade();								
							}													
						}catch (Exception Alertas ) 
						{
							System.err.println("EXCEPCION : AlertEngine: Alerta (82 o 86 o 87): " + Alertas.toString());
							EnviaAlertaUpgrade=false;	
						}

						
						if(!EnviaAlertaUpgrade)
						{					
						 try {
						
							// CONSULTA DE ALERTAS CONFIGURADAS PARA VID
							stmSEL = ConexionBase.createStatement();

							Query = "SELECT (SELECT TOP 1 AC.IdActivo FROM Activo AS AC,ActivoDispositivo AS ACD WITH (NOLOCK) WHERE AC.IdActivo = ACD.IdActivo AND ACD.IdDispositivo = '"
									+ PDU.getModemID()
									+ "' order by Alias desc ) AS IdActivo,"
									+ "(SELECT TOP 1 AC.Alias FROM Activo AS AC,ActivoDispositivo AS ACD WITH (NOLOCK) WHERE AC.IdActivo = ACD.IdActivo AND ACD.IdDispositivo = '"
									+ PDU.getModemID()
									+ "' order by Alias desc )    AS Placa,"
									+ "DA.idAlerta,DA.TipoAlerta, DA.Nombre, DA.idTipoDispositivo,DA.Evento,DA.IdGeocerca, DA.Kilometraje,DA.PorcentajeAnticipacion,EDA.EmailsAlerta,EDA.SMSAlerta,DA.HoraDesde,DA.HoraHasta,DA.LimiteVelocidad,DA.DentroGeo ,DA.IdSensor,DA.TempBaj,DA.TempSub,DA.TempIdeal "
									+ "FROM    D_Alerta as DA, EntidadD_Alerta AS EDA WITH (NOLOCK) "
									+ "WHERE   DA.IdAlerta = EDA.IdAlerta AND "
									+ "EDA.IdEntidad IN (SELECT E.IdEntidad FROM  Entidad as E, EntidadActivo as EA, Activo as A, ActivoDispositivo as AD with (nolock) "
									+ "where  E.IdEntidad = EA.IdEntidad and "
									+ "EA.IdActivo = A.IdActivo and "
									+ "A.IdActivo = AD.IdActivo and "
									+ "AD.IdDispositivo = '" + PDU.getModemID() + "') AND " + "EDA.Estado = 'A' AND ( (DA.TipoAlerta = 1 AND DA.Evento = "
									+ PDU.getInputEvent() + " ) OR " + "(DA.TipoAlerta = 2 AND ( " 	+ PDU.getOdometer()
									+ "/1000 >= (DA.KILOMETRAJE *100 - DA.PorcentajeAnticipacion*DA.Kilometraje)/100 )  AND ( "
									+ PDU.getOdometer() + "/1000 <= (DA.KILOMETRAJE *100 + DA.PorcentajeAnticipacion*DA.Kilometraje)/100 )   ) OR "
									+ "(DA.tipoalerta = 3) OR (DA.tipoalerta = 4) OR "
									+ "(DA.TipoAlerta = 6 AND (" + PDU.getInputEvent() + "= ISNULL(DA.Evento," +  PDU.getInputEvent() + ") or DA.Evento = 0 ) AND ( ISNULL(DA.HoraDesde,'"+ FechaHora +"') <= '" + FechaHora + "' AND '" + FechaHora + "' <= ISNULL(DA.HoraHasta,'" + FechaHora + "')) AND " + PDU.getSpeed() + " >= ISNULL(DA.LimiteVelocidad, " + PDU.getSpeed() + ") AND dbo.CumpleCriterioGeocerca(DA.IdGeocerca,DA.DentroGeo," + PDU.getLatitude() +"," + PDU.getLongitude() +") = 1 AND ( DA.Evento != 0 or ( DA.IdGeocerca !=0 AND DA.DentroGeo is not null) or DA.HoraDesde IS NOT NULL or DA.HoraHasta IS NOT NULL or DA.LimiteVelocidad IS NOT NULL )  or (DA.TipoAlerta = 7) )  "
									+ ") "
									+ "UNION " +
									"SELECT A.IdActivo,A.Alias,DA.idAlerta,DA.TipoAlerta, DA.Nombre, DA.idTipoDispositivo,DA.Evento,DA.IdGeocerca, DA.Kilometraje,DA.PorcentajeAnticipacion,ADA.EmailsAlerta,"
									+ "ADA.SMSAlerta,DA.HoraDesde,DA.HoraHasta,DA.LimiteVelocidad,DA.DentroGeo ,DA.IdSensor,DA.TempBaj,DA.TempSub,DA.TempIdeal "
									+ "FROM    D_Alerta as DA,  ActivoD_Alerta AS ADA, Activo as A, ActivoDispositivo as AD, Dispositivo as D WITH (NOLOCK) "
									+ "WHERE   D.idDispositivo = '" + PDU.getModemID() + "' AND "
									+ "D.IdDispositivo = AD.IdDispositivo and "
									+ "AD.IdActivo = A.IdActivo and  " + "A.IdActivo = ADA.IdActivo and " + "ADA.IdAlerta = DA.IdAlerta and "  
									+ "A.Estado = 'A' AND " + "ADA.Estado = 'A' AND "
									+ "((DA.TipoAlerta = 1 AND DA.Evento = " + PDU.getUserSpecifiedNumber() + " ))"
									+ "ORDER BY TipoAlerta ASC";
							
							
							//System.out.println(Query);
							
							rs1 = stmSEL.executeQuery(Query);
							
									
							// PROCESAR ALERTAS
							while (rs1 != null && rs1.next()) 
							{
								TipoAlerta = rs1.getInt(4);
								//System.out.println("TipoAlerta: "+ Integer.toString(TipoAlerta));
								switch (TipoAlerta) 
								{
									// EVENTO
									case 1: {
										AlertaEvento();
										break;
									}
									// ODOMETRO
									case 2: {
										AlertaOdometro();
										break;
									}
									// GEOCERCA IN
									case 3: {
										if (PDU.getGPSStatus().compareTo("1") == 0)
											AlertaGeocerca();
										break;
									}// GEOCERCA OUT
									case 4: {
										if (PDU.getGPSStatus().compareTo("1") == 0)
											AlertaGeocerca();
										break;
									}//MULTICRITERIO
									case 6: 
									{
										AlertaMulticriterio();
										break;
									}
									case 7:{
										AlertaTemperatura();
										break;
									}
								}
							}

							try {
								if (rs1 != null) {
									rs1.close();
									rs1 = null;
								}
							} catch (Exception e) {
							}

							try {
								if (stmSEL != null) {
									stmSEL.close();
									rs1 = null;
								}
							} catch (Exception e) {
							}
							pg++;
						}catch (SQLException se) 
						{
							BufferEntrada.putElemento(PDU);
							System.err.println("SQLEXCEPCION : AlertEngine: Run: " + se.toString());
							CerrarConexionDB();

						}catch (Exception e3) 
						{
							System.err.println("EXCEPCION : AlertEngine: Run: " + e3.toString());
							CerrarConexionDB();

						}catch (Throwable e) 
						{
							System.err.println("ERROR: AlertEngine: Run: ");
							e.printStackTrace();
						}

					 }
						PDU = null;
						Rumbo=null;
						EGPS=null;
					}
					
				} else {
					try {
						Thread.sleep(1000);
					} catch (Exception ee) {
					}
				}
			}

			if (System.currentTimeMillis() - t > 60000) 
			{
				if (BufferEntrada.tamano() > 0)
					System.err
							.println("ADVERTENCIA: AlertEngine: Run: PDU procesados "
									+ pg
									+ ": PDU pendientes de procesar "
									+ BufferEntrada.tamano()
									+ ": Durante "
									+ (System.currentTimeMillis() - t)
									+ " milisegundos");

				t = System.currentTimeMillis();

				pg = 0;
			}

			try {
				Thread.sleep(50);
			} catch (Exception e) {

			}
		}
		
		}
		catch(Exception e)
		{
			System.err.println("ERROR: AlertEngine: Run:"
					+ e.toString());
		}
	}

	public AlertEngine(AppProperty tmpAP)
	{
		try {

			AP = tmpAP;

			DBParameters = AP.getProperty("JdbcUrl") + "://"
					+ AP.getProperty("DBGpsUnitX_Address") + ":"
					+ AP.getProperty("DBGpsUnitX_Port") + ";Database="
					+ AP.getProperty("DBGpsUnitX_Name") + ";User="
					+ AP.getProperty("DBGpsUnitX_User") + ";Password="
					+ AP.getProperty("DBGpsUnitX_PWD");

			BCC = AP.getProperty("BCC");

			SMTPServer = AP.getProperty("SMTPServer");

			if (SMTPServer == null)
				SMTPServer = "127.0.0.1";

			AlertSource = AP.getProperty("AlertSource");

			if (AlertSource == null)
				AlertSource = "geosys@huntermonitoreo.com";

			AlertServer = AP.getProperty("AlertServer");

			if (AlertServer == null)
				AlertServer = "GEOSYS";

			if (AP.getProperty("DebugUDPAPItoDBError").compareTo("true") == 0)
				DebugERROR = true;
			else
				DebugERROR = false;

			if (AP.getProperty("DebugUDPAPItoDBTran").compareTo("true") == 0)
				DebugTRAN = true;
			else
				DebugTRAN = false;

			// System.err.println("MSG: SE INICIO PROCESADOR DE ALERTA PARA DISPOSITIVO "
			// + PDU.getModemID() );

			this.start();

		} catch (Exception e) {
			System.err.println("ERROR: AlertEngine: " + e.toString());
		}

	}

}
