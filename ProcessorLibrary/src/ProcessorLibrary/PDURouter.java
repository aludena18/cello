//V3.0.0

package ProcessorLibrary;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import PDULibrary.AppProperty;
import PDULibrary.GestorCola;
import PDULibrary.PDU;

public class PDURouter extends Thread {
	AppProperty AP;
	int Puerto;
	DatagramSocket UDPServidor;

	DatagramPacket packet2;
	String NombreServidor = null;

	PDU PDU = null;

	String strIPDestino, SMTPServer;

	String[] BCC, TO;

	public GestorCola BufferEntrada = new GestorCola();

	private void SendEMAIL(String remitente, String subject, String body) {
		try {
			Properties props = System.getProperties();
			props.put("mail.smtp.host", SMTPServer);
			Session session = Session.getDefaultInstance(props, null);
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(remitente));

			if (TO != null) {
				for (int i = 0; i < TO.length; i++)
					message.addRecipient(Message.RecipientType.TO,
							new InternetAddress(TO[i]));
			}

			if (BCC != null) {
				for (int i = 0; i < BCC.length; i++)
					message.addRecipient(Message.RecipientType.BCC,
							new InternetAddress(BCC[i]));
			}

			message.setSubject(subject);
			message.setText(body);
			Transport.send(message);
		} catch (Exception e) {
			System.err
					.println("ERROR: PDURouter: SendEMAIL: No se pudo enviar email: "
							+ e);
		} catch (Throwable e) {
			System.err
					.println("ERROR: PDURouter: SendEMAIL: No se pudo enviar email");
			e.printStackTrace();
		}
	}

	public void run() {
		InetAddress iaIPDestino;
		byte[] btPDUToSend;
		int c = 0;

		System.err.println("MSG: PDURouter: Inicializado con parametros "
				+ strIPDestino + " TO: ");

		if (TO != null) {
			for (int i = 0; i < TO.length; i++)
				System.err.println(TO[i]);
		}

		System.err.println("BCC: ");

		if (BCC != null) {
			for (int i = 0; i < BCC.length; i++)
				System.err.println(BCC[i]);
		}

		try {
			iaIPDestino = InetAddress.getByName(strIPDestino);
		} catch (Exception e) {
			iaIPDestino = null;
			System.err
					.println("ERROR: PDURouter: No se pudo obtener dirección destino "
							+ e.toString());
		}

		while (iaIPDestino != null) {
			try {

				if (BufferEntrada.tamano() > 0) {

					PDU = (PDU) BufferEntrada.getElemento();

					if (PDU.isPositionReportPDU() == true) {
						if (c == 10)
							c = 0;

						btPDUToSend = PDU.getB_PDU();

						packet2 = new DatagramPacket(btPDUToSend,
								btPDUToSend.length, iaIPDestino,
								PDU.getPuerto() + c);
						UDPServidor.send(packet2);

						if ((PDU.getInputEvent().compareTo("0") == 0
								|| PDU.getInputEvent().compareTo("48") == 0 || PDU
								.getInputEvent().compareTo("200") == 0)
								&& TO != null) {
							System.err
									.println("MSG: ProcessorX_AlertaBotonPanico: Se genero ALERTA DE BOTON DE PANICO de "
											+ PDU.getModemID());
							SendEMAIL(
									PDU.getModemID() + "@hunterlojack.com",
									"BOTON DE PANICO: " + PDU.getModemID(),
									"Boton de panico de Vehiculo "
											+ PDU.getModemID() + " a las "
											+ PDU.getReportDateTime());

						}

						c++;
					}

				}

				try {
					Thread.sleep(50);
				} catch (Exception ee) {
				}

			} catch (Exception e) {

			}

		}

	}

	public void finalize() {
		try {

			if (this.getState().compareTo(Thread.State.TERMINATED) != 0)
				System.err.println("MSG: PDURouter: Finalize: "
						+ this.getState());

			super.finalize();

		} catch (Throwable e) {
			System.err.println("ERROR: PDURouter: Finalize: ");
			e.printStackTrace();
		}

	}

	public PDURouter(DatagramSocket tmpUDPServidor, String tmpNombreServidor,
			AppProperty tmpAP) {
		Init(tmpUDPServidor, tmpNombreServidor, tmpAP);
	}

	private void Init(DatagramSocket tmpUDPServidor, String tmpNombreServidor,
			AppProperty tmpAP) {
		try {
			this.setName(this.getName() + ": PDURouter");

			AP = tmpAP;
			UDPServidor = tmpUDPServidor;

			NombreServidor = tmpNombreServidor;

			strIPDestino = AP.getProperty("PDURouterPort"
					+ UDPServidor.getLocalPort());

			String tmpTO = AP.getProperty("PanicAlertTO");

			if (tmpTO != null) {
				TO = tmpTO.split(",");
			}

			String tmpBCC = AP.getProperty("PanicAlertBCC");

			if (tmpBCC != null) {
				BCC = tmpBCC.split(",");
			}

			SMTPServer = AP.getProperty("SMTPServer");

			if (SMTPServer == null)
				SMTPServer = "127.0.0.1";

			this.start();

		} catch (Exception e) {
			System.err.println("ERROR: PDURouter: InitServer: " + e.toString());
		}
	}

}