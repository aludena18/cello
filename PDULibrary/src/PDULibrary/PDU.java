package PDULibrary;

public interface PDU
{
	//GPS DATA
	String getDateTimeSQL();
	
	String getReportDateTime( int i );
	
	String getReportDateTime( );
	 
	String getReportDateTimeGPS();
	
	String getReportDateTimeRTC();

	String getModemID();
	
	String getInputEvent();
	
	String getLatitude();

	String getLongitude();
	
	String getSpeed();
	
	String getHeading();
	
	String getAltitude();

	String getOdometer();
	
	String getGPSStatus();
	
	String getNumberOfSatellites();
	
	String getIO_STATE();

	String getIO_CFG();
	
	String getIgnitionState();
	
	String getUserSpecifiedNumber();
	
	String getGPSDate();
	
	String getGPSTime();
	
	String getCMDResponse();
		
	int getIdProtocolo();
	
	int getType();

	int getSubType();
	
	String getIP();

	int getPuerto();
	
	String getS_PDU();
	
	//boolean isPDUofGPSDATA();

	boolean isPDUofPhoto();
	
	boolean isPositionReportPDU();
	
	boolean isTaxiDataPDU();
	
	boolean isTaxiDataEstadoPDU();
	
	boolean isTaxiDataMensajePDU();
	
	boolean isTaxiDataTaximetroPDU();
	
	boolean isCMDResponse();

	String getEstado();

	int getNumeroMensaje();

	String getMensaje();

	int getTiempoOcupado();

	float getImporteAPagar();

	int getTiempoEspera();

	int getKilometraje();

	int getNumeroCarrera();

	int getNumeroVoucher();

	byte[] getB_PDU();
	
	float getADC1();
	
	float getADC2();
	
	int getBatteryLevel();
	
	String getACK();
	
	byte [] getBACK();
	
	String getTemperatura1();
	
	String getTemperatura2();
	
	float getEA1();

	float getEA2();

	float getEA3();

	float getSA1();

	float getSA2();

	float getSA3();

	float getVoltajeAlimentacion();

	int getPuntoVisita();

	int getPuntoVisitaEstado();

	int CellID0();

	int SenalCellID0();

	int CellID1();

	int SenalCellID1();

	int CellID2();

	int SenalCellID2();

	int CellID3();

	int SenalCellID3();

	int CellID4();

	int SenalCellID4();

	int CellID();

	int SenalCellID();

	int MCC();

	int LAC();

	int rpmOBD();

	int PosicionAcceleradorOBD();

	int OdometroOBD();

	int OdometroViajeOBD();

	float NivelGasolinaOBD();

	float CombustibleRestanteOBD();

	int EngraneTransmisionOBD();

	float TemperaturaRefrigeranteOBD();

	float IndiceGasolinaOBD();

	float VoltajeAlimentacionOBD();

	int EstadoSeñalesGiroOBD();

	float GasolinaConsumidaPorViaje();

	String IndicadoresOBD();
	
	String getDTC();
	
	boolean getCheckEngine();
	
	String getHorometer();
	
	//---- A.L.G. UPDATES ---------
	
	String getMaxSpeed();
	
	String getTimeMaxSpeed();
	
	String getSpeedSetPoint();
	
	String getEventoValor();
	
	String getEventoValorSP();
	
	String getImpactData();
	
	//--------------------------

	}
