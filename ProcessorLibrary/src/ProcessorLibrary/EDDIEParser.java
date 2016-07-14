package ProcessorLibrary;

import java.util.GregorianCalendar;
import java.util.TimeZone;

import PDULibrary.PDU;

public class EDDIEParser {
	String EDDIE;

	public String toEDDIE() {
		return EDDIE;
	}

	public EDDIEParser(PDU tmpPDU) {
		try {
			if (tmpPDU.isPositionReportPDU() == true) {
				String d, t;

				long V, F, diferencia = 315964800L, semana, dia, segundos;
				EDDIE = ">REV";

				EDDIE += tmpPDU.getInputEvent();

				// CALCULO DE FECHA Y HORA
				GregorianCalendar C = new GregorianCalendar();

				d = tmpPDU.getGPSDate();
				t = tmpPDU.getGPSTime();

				C.setTimeZone(TimeZone.getTimeZone("GMT+0"));
				C.set(Integer.parseInt("20" + d.substring(4, 6)),
						Integer.parseInt(d.substring(2, 4)) - 1,
						Integer.parseInt(d.substring(0, 2)),
						Integer.parseInt(t.substring(0, 2)),
						Integer.parseInt(t.substring(2, 4)),
						Integer.parseInt(t.substring(4, 6)));

				V = C.getTimeInMillis();
				V /= 1000;

				F = V - diferencia;

				semana = (int) F / 604800;
				F %= 604800;
				dia = (int) F / 86400;
				F %= 86400;
				segundos = F;

				// NUMERO DE SEMANA
				if (semana <= 9)
					EDDIE += "000" + semana;
				else if (semana <= 99)
					EDDIE += "00" + semana;
				else if (semana <= 999)
					EDDIE += "0" + semana;
				else if (semana <= 9999)
					EDDIE += semana;

				// DIA DE LA SEMANA
				EDDIE += dia;

				// SEGUNDOS
				if (segundos <= 9)
					EDDIE += "0000" + segundos;
				else if (segundos <= 99)
					EDDIE += "000" + segundos;
				else if (segundos <= 999)
					EDDIE += "00" + segundos;
				else if (segundos <= 9999)
					EDDIE += "0" + segundos;
				else if (segundos <= 99999)
					EDDIE += segundos;

				// LATITUD

				int latitud = (int) (Float.parseFloat(tmpPDU.getLatitude()) * 100000);

				if (latitud >= 0)
					EDDIE += "+";
				else
					EDDIE += "-";

				if (Math.abs(latitud) <= 99999)
					EDDIE += "00" + Math.abs(latitud);
				else if (Math.abs(latitud) <= 999999)
					EDDIE += "0" + Math.abs(latitud);
				else if (Math.abs(latitud) <= 9999999)
					EDDIE += Math.abs(latitud);

				// LONGITUD
				int longitud = (int) (Float.parseFloat(tmpPDU.getLongitude()) * 100000);

				if (longitud >= 0)
					EDDIE += "+";
				else
					EDDIE += "-";

				if (Math.abs(longitud) <= 99999)
					EDDIE += "000" + Math.abs(longitud);
				else if (Math.abs(longitud) <= 999999)
					EDDIE += "00" + Math.abs(longitud);
				else if (Math.abs(longitud) <= 9999999)
					EDDIE += "0" + Math.abs(longitud);
				else if (Math.abs(longitud) <= 99999999)
					EDDIE += Math.abs(longitud);

				// VELOCIDAD
				int velocidad = Integer.parseInt(tmpPDU.getSpeed());

				velocidad = (int) (velocidad / 1.618);

				if (velocidad <= 9)
					EDDIE += "00" + velocidad;
				else if (velocidad <= 99)
					EDDIE += "0" + velocidad;
				else if (velocidad <= 999)
					EDDIE += velocidad;

				// DIRECCION
				EDDIE += tmpPDU.getHeading();

				// FUENTE DE DATOS
				EDDIE += tmpPDU.getGPSStatus();

				// EDAD DE DATOS
				EDDIE += "2";

				// ID
				EDDIE += ";ID=" + tmpPDU.getModemID();

				// CRC
				EDDIE += ";*99<" + (char) 0x0A + (char) 0x0D;

			}
		} catch (Exception e) {
			System.err.println("ERROR: EDDIEParser: " + e.toString());
			System.err.println(EDDIE + " " + tmpPDU.getS_PDU());
			EDDIE = null;
		}
	}

}
