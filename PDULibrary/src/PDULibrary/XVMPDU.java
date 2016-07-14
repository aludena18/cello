/*
 * VERSION ACTUAL 1.0.0
 * INCLUIDA DESDE LA VERSION 6.0.0 
 * 2009-09-15
 *  
 * 
 */
package PDULibrary;

import java.net.*;
import java.text.*;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class XVMPDU implements PDU
{	
	//PROPIEDADES
	
	public byte[] B_PDU;
	String Bin="",Hex="",S_PDU="";
	String[] SA_PDU,SA0;
	
	
	int Type=0,SubType=0;
	
	String IP=null;
	int Puerto=0;
	
	long Time;
	
	public int getIdProtocolo()
	{
		return 2;
	}
	
	public synchronized String getCMDResponse()
	{
		try{
			if( Type == 12 )
			{
				return S_PDU.trim();
			}
			else
				return null;
		}catch(Exception e )
		{
			System.err.println("ERROR: XVMPDU: getCMDResponse: " + e.toString() );
			return null;
		}
	}
	
	private synchronized GregorianCalendar getCalendario()
	{
		try{
				
			if( Type == 3)
			{	
				int Semanas=0;int Segundos=0;
				int shift;
				
				byte valor[],tmpvalor[];
				
				valor = subbyte(B_PDU, 22, 25);
				
				tmpvalor = new byte[4];
				
				tmpvalor[0] = 0;
				tmpvalor[1] = 0;
				tmpvalor[2] = (byte) ((valor[3]&0xf0)>>4); 
				tmpvalor[3] = (byte) ((( valor[3]& 0x0f )<< 4 ) | ( ( valor[2]& 0xf0) >> 4 ));
				
				valor = tmpvalor;
				
				for (int i = 0; i < 4; i++) 
			    {
					shift = (4 - 1 - i) * 8;
			        Semanas += (valor[i] & 0x000000FF) << shift;
			    }
				
				valor = subbyte(B_PDU, 22, 25);
				
				//tmpvalor = new byte[4];
				
				tmpvalor[0] = 0;
				tmpvalor[1] = (byte)(valor[2] & 0x0f);
				tmpvalor[2] = valor[1];
				tmpvalor[3] = valor[0];
				
				valor = tmpvalor;
				
				for (int i = 0; i < 4; i++) 
			    {
			        shift = (4 - 1 - i) * 8;
			        Segundos += (valor[i] & 0x000000FF) << shift;
			    }
			   	    		    
			    GregorianCalendar Calendario = new GregorianCalendar();
			    
			    Calendario.set( 1980, 0,6,0,0,0);
			    		        
			    Calendario.add(  Calendar.HOUR , Semanas*7*24 );
				Calendario.add(  Calendar.SECOND , Segundos );
				
				return Calendario;
			}	
			return null;
		}catch( Exception e )
		{
			System.err.println("ERROR: XVMPDU: getCalendario:" + e.toString() );
			return null;
		}
		
	}
	
	public synchronized String getEstado()
	{
		try{
			
			
			if( getType() == 7 )
			{				
				if( SubType == 2 || SubType == 3 )
				{
					return SA0[1];
				}
				else 
					return SA0[2];
			}
			else if( Type == 9 && SubType == 2 )
				return SA0[3];
			else if( Type == 9 && SubType == 3 )
				return "1";
			return null;
			
		}catch( Exception e)
		{	
			System.err.println("ERROR: XVMPDU: getEstadoRUS: "  + e.toString() );
			return null;
		}
	}
	
	public synchronized String getReportDateTime()
	{
		try{
			
			if( getGPSStatus().compareTo("1") == 0 )
			{
				return "20" + getGPSDate().substring(4,6) + "-" + getGPSDate().substring(0, 2) + "-" + getGPSDate().substring(2,4) +
					" " + getGPSTime().substring(0,2) + ":" + getGPSTime().substring(2,4) + ":" + getGPSTime().substring(4,6); 
			}
			else
			{
				return getRTC().substring(0,4) + "-" + getRTC().substring(6,8) + "-" + getRTC().substring( 4 , 6 ) +
					" " + getRTC().substring(8, 10) + ":" + getRTC().substring(10, 12) + ":" + getRTC().substring(12,14); 
			}
		}catch( Exception e)
		{
			System.err.println("ERROR: XVMPDU: ReportDateTime: " + e.toString() + " " + getGPSDate() + " " + getRTC() );
			
			return null;
		}
	}
	
	public synchronized String getReportDateTimeRTC()
	{
		try{
	
			return getRTC().substring(0,4) + "-" + getRTC().substring(6,8) + "-" + getRTC().substring( 4 , 6 ) +
			" " + getRTC().substring(8, 10) + ":" + getRTC().substring(10, 12) + ":" + getRTC().substring(12,14); 
		}catch( Exception e)
		{
			System.err.println("ERROR: XVMPDU: ReportDateTimeRTC: " + e.toString());
			return null;
		}
		
	}

	public synchronized String getReportDateTimeGPS()
	{
		try{
	
			return "20" + getGPSDate().substring(4,6) + "-" + getGPSDate().substring(0, 2) + "-" + getGPSDate().substring(2,4) +
			" " + getGPSTime().substring(0,2) + ":" + getGPSTime().substring(2,4) + ":" + getGPSTime().substring(4,6);
			
		}catch( Exception e)
		{
			System.err.println("ERROR: XVMPDU: ReportDateTimeGPS: " + e.toString());
			return null;
		}
		
	}
	
	public synchronized String getIP()
	{
		return IP;
	}
	
	public synchronized int getPuerto()
	{
		
		return Puerto;
	}
	
	public synchronized int SBinToInt( String tmpBin )
	{
		int valor=0,length=tmpBin.length();
		
		for( int i=length ; i>0 ; i-- )
		{
			if( tmpBin.charAt( i-1 ) == '1' )
				valor+= 1*Math.pow(2, length-i);
			else if( tmpBin.charAt( i-1 ) == '0' )
				valor+=0;
			else 
				return -1;
		}
		return valor;
		
	}
	
	public synchronized String StringToSBin( String tmpString )
	{
		byte arreglo[];
		String Valor;
		
		Valor="";
		arreglo = new byte[tmpString.length()];
		
		for( int i=0 ; i<tmpString.length() ; i++ )
			arreglo[i] = (byte)tmpString.charAt(i);
		
		for( int i=0 ; i<arreglo.length ; i++ )
			Valor+=ByteToString( arreglo[i] );
		return Valor;
	}
	
	public synchronized String ByteToString( byte tmpByte )
	{	
		String S;
		S="";
		byte bt,b1;
		b1= tmpByte;
		
		for( int i=0; i<8;i++)
		{
			bt= (byte) (b1&1);
			b1>>=1;
			if( bt == 1 )
				S="1"+S;
			else 
				S="0"+S;
		}
		return S;
	}
	
	public synchronized int StringToUInt( String tmpString )
	{
		return SBinToInt( StringToSBin( tmpString ) );
	}

	private synchronized String ByteToHex( byte b )
	{
		 String pseudo[] = {"0", "1", "2",
				 "3", "4", "5", "6", "7", "8",
				 "9", "A", "B", "C", "D", "E",
				 "F"};
		 
		 byte ch;
		 String Hex="";
		 ch = (byte) (b & 0xF0); // Strip off high nibble

		 ch = (byte) (ch >>> 4); // shift the bits down

		 ch = (byte) (ch & 0x0F); // must do this is high order bit is on!

		 Hex+=pseudo[ (int) ch]; // convert the nibble to a String Character

		 ch = (byte) (b & 0x0F); // Strip off low nibble 

		 Hex+=pseudo[ (int) ch]; // convert the nibble to a String Character
		 
		 return Hex;
	}
	
	boolean DebugERROR,DebugTRAN;
		
	//CONSTRUCTORES
	public XVMPDU( DatagramPacket tmpPDU )
	{
		int c=0;
		try{
			c++;
			Time = System.currentTimeMillis();
			c++;
			byte[] tmpB_PDU;
			//DG_PDU = tmpPDU;
			
			c++;
			IP = tmpPDU.getAddress().toString();
			c++;
			Puerto = tmpPDU.getPort();
			c++;
			
			
			tmpB_PDU = tmpPDU.getData();
		
			c++;
			B_PDU = new byte[ tmpPDU.getLength() ];
			
			c++;
			
			for( int i=0; i < tmpPDU.getLength(); i++)
			{
				B_PDU[i] = tmpB_PDU[i];
			}
			
			c++;
			Hex = this.toHex();
			c++;
			Bin = this.toBin();
			c++;
			S_PDU = new String( B_PDU );
			c++;
			
			try{
				IP = tmpPDU.getAddress().toString();
				c++;
				Puerto = tmpPDU.getPort();
				c++;
			}catch( Exception e)
			{
				System.err.println("ERROR: XVM: " + c + " " + e.toString() );
			}
			
			this.IdentifyType();
		}catch( Exception e )
		{
			System.err.println("ERROR: XVM: " + c + " " + e.toString() );
		}
		
	}
		
	public XVMPDU( byte[] tmpPDU )
	{
		Time = System.currentTimeMillis();
		B_PDU = tmpPDU;
		
		Hex = this.toHex();
		Bin = this.toBin();
		
		S_PDU = new String( B_PDU );
		
		this.IdentifyType();
	}
	
	public XVMPDU( String tmpData, String tmpID, String tmpNofM)
	{
		int c=0;
		try{
			
			Time = System.currentTimeMillis();
			c++;
			S_PDU = ">" + tmpData + ";ID=" + tmpID + ";#" + tmpNofM + ";";
			c++;

			byte checksum;
			c++;
			checksum=0;
			c++;
			int i;
			c++;
	
			for( i=0 ; i < S_PDU.length() ; i++ )
			{
				c++;
				checksum = (byte)(checksum ^ ( (byte)S_PDU.charAt(i) ));
				c++;
			}
			S_PDU += "*" + this.ByteToHex(checksum) + "<" + (char)0x0D + (char)0x0A + (char)0x00;
			c++;
			
		}catch( Exception e )
		{
			System.err.println("ERROR: XVM: " + c + " " + e.toString() );
		}
	}
	
	//FUNCIONES
	public synchronized String getS_PDU( )
	{
		return S_PDU;
	}
	
	private synchronized boolean isXVMStructure()
	{
		try{
			
			SA_PDU = new String[4];
			if( S_PDU.indexOf(">" ) == -1 )
				return false;
			if( S_PDU.indexOf("<") == -1 )
				return false;
			
			if( S_PDU.startsWith(">JLX"))
			{
				SA_PDU[0]=S_PDU.substring(0,136);
				SA_PDU[1]=S_PDU.substring(137,144);
				SA_PDU[2]=S_PDU.substring(145,150);;
				SA_PDU[3]=S_PDU.substring(151);
					
			}
//			else if( S_PDU.startsWith(">JLJ"))
//			{
//				SA_PDU[0]=S_PDU;
//				SA_PDU[0]=S_PDU;
//				SA_PDU[0]=S_PDU;
//				SA_PDU[0]=S_PDU;
//				
//			}
//			else if( S_PDU.startsWith(">JLF"))
//			{	
//				SA_PDU[0]=S_PDU;
//				SA_PDU[0]=S_PDU;
//				SA_PDU[0]=S_PDU;
//				SA_PDU[0]=S_PDU;
//			}
			else 
			{	int i=0,c=0,j=0;
						
				while( i<S_PDU.length() && j >= 0 && c < 4)
				{
					j=S_PDU.indexOf( ";", i);
					
					if( j>=0 )
					{
						SA_PDU[c] = S_PDU.substring( i , j );
						i=j+1;
					}
					else
					{
						SA_PDU[c] = S_PDU.substring(i);
						i=S_PDU.length();
					}
					
					c++;
				}
							
				if( c != 4 )
					return false;
			}	
			return true;	
		}catch( Exception e)
		{
			System.err.println("isXVM: " + e.toString() );
			return false;
		}
	}
	
	private synchronized void IdentifyType()
	{
		if( isXVMStructure() == true )
		{
			//1 GP
			if( SA_PDU[0].indexOf("GP")== 2 )
				Type = 1;
			//2 TT
			else if( SA_PDU[0].indexOf("TT") ==  2)
				Type = 2;
			//3 JLX
			else if( SA_PDU[0].startsWith(">JLX"))
				Type = 3;
			//4 JLJ_EOP
			else if( SA_PDU[0].startsWith(">JLJ_EOP"))
				Type = 8;
			//5 JLF
			else if( SA_PDU[0].startsWith(">JLF"))
				Type = 5;
			//6 VGJL
			else if( SA_PDU[0].startsWith(">VGJL") )
				Type = 6;
			//4 JLJ
			else if( SA_PDU[0].startsWith(">JLJ"))
				Type = 4;
			//7 >RUS
			else if( SA_PDU[0].startsWith(">RUS"))
			{
				Type = 7;
				
				SA0 = SA_PDU[0].split(",");
					
				if( SA0.length == 2 )
				{
					/*
					 * RUS00,1
					 */
					
					SubType=2;
				}
				else if( SA0.length == 3 && SA0[2].startsWith("RGP") == true )
				{
					/*
					 * >RUS00,1,RGP230211215922-0215265-07989324000229300DF0001;ID=6199;#0163;*3D<
					 */
					SubType=3;
				}
				else	
					/*
					 * RUS00,ESTADO,1
					 */
					SubType=1;
			}
			//28	>RVQRMN
			else if( SA_PDU[0].startsWith(">RVQRMN"))
				Type = 28;
			else if( SA_PDU[0].startsWith(">RVQRAL"))
				Type = 18;
			//9	>RAX
			else if( SA_PDU[0].startsWith(">RAX"))
			{
				Type = 9;
				SA0 = SA_PDU[0].split(",");
				
				if( SA0.length == 4 && SA0[1].startsWith("RGP") == true )
				{
					/*
					 * >RAX61,RGP230211224212-0215261-07989328000229300DF0001,123456789,2;ID=6199;#023F;*4A<
					 */
					
					SubType=2;
				}
				else if( SA0.length == 8 && SA0[7].startsWith("RGP") == true )
				{
					SubType=3;
				}
				else 
				{
					SubType=1;
				}
				
			}
			else 
				Type = 12;
		}
		
	}
	
	public synchronized String obtenerElementoRAX(int i)
	{
		try{
		
			if( i < SA_PDU[0].split(",").length)
			{
				return SA_PDU[0].split(",")[i];
			}
			return "";
		}catch( Exception e)
		{
			return "";
		}
	}
	
	public synchronized int NumeroElementosRAX()
	{
		try{
			
			return SA_PDU[0].split(",").length;
			
			
		}catch( Exception e)
		{
			System.err.println("ERROR: XVMPDU: NumeroElementosRAX: " + e.toString() );
			return 0;
		}
	}
	
	public synchronized int getTiempoOcupado()
	{
		try{
			if( Type==9 && ( SubType == 1 || SubType == 3 ) )
			{
				return Integer.parseInt( SA0[1] );
				
			}
			return -1;
		}
		catch( Exception e)
		{
			System.err.println("ERROR: XVMPDU: getTiempoOcupado: " + e.toString() );
			System.err.print( ":" + S_PDU );
			return -1;
		}
		
	}
	
	public synchronized int getKilometraje()
	{
		
		try{
			if( Type==9 && ( SubType == 1 || SubType == 3 ) )
			{
				return Integer.parseInt( SA0[2] );
			}
			return -1;
		}
		catch( Exception e)
		{
			System.err.println("ERROR: XVMPDU: getKilometraje: " + e.toString() );
			return -1;
		}
		
	}
	
	public synchronized float getImporteAPagar()
	{
		try{
			if( Type==9 && ( SubType == 1 || SubType == 3 ) )
			{
				return Integer.parseInt( SA0[3] );
			}
			return -1;
		}
		catch( Exception e)
		{
			System.err.println("ERROR: XVMPDU: getImporteAPagar: " + e.toString() );
			return -1;
		}
	}
	
	public synchronized int getTiempoEspera()
	{
		try{
			if( Type==9 && ( SubType == 1 || SubType == 3 ) )
			{
				return Integer.parseInt(SA0[4]);
			}
			return -1;
		}
		catch( Exception e)
		{
			System.err.println("ERROR: XVMPDU: getTiempoEspera: " + e.toString() );
			System.err.print( S_PDU );
			return -1;
		}
	}
	
	public synchronized int getNumeroCarrera()
	{
		try{
			if( Type==9 && ( SubType == 1 || SubType == 3 ) )
			{
				return Integer.parseInt(SA0[5]);
			}
			return -1;
		}
		catch( Exception e)
		{
			System.err.println("ERROR: XVMPDU: getNumeroCarrera: " + e.toString() );
			System.err.print( S_PDU );
			return -1;
		}
	}
	
	public synchronized int getNumeroVoucher()
	{
		try{
			if( Type == 9 && ( SubType == 1 || SubType == 3 ) )
			{
				return Integer.parseInt( SA0[6]);
			}
			return 0;
		}
		catch( Exception e)
		{
			System.err.println("ERROR: XVMPDU: getNumeroCarrera: " + e.toString() );
			System.err.print( S_PDU );
			return 0;
		}
	}
	
	private synchronized String toHex()
	{
		Hex = "";
		byte H1,H2;
		try{
			for( int i=0; i < B_PDU.length; i++ )
			{
				H1= B_PDU[i];
				H2 = H1;
				H1 >>= 4;
				H1 = (byte) (H1 & (byte)0x0F);
				H2 = (byte) (H2 & (byte)0x0F);
				
				switch( H1 )
				{
					case 0:
						Hex+="0";
						break;		
					case 1:
						Hex+="1";
						break;
					case 2:
						Hex+="2";
						break;
					case 3:
						Hex+="3";
						break;
					case 4:
						Hex+="4";
						break;
					case 5:
						Hex+="5";
						break;
					case 6:
						Hex+="6";
						break;
					case 7:
						Hex+="7";
						break;
					case 8:
						Hex+="8";
						break;
					case 9:
						Hex+="9";
						break;
					case 10:
						Hex+="A";
						break;
					case 11:
						Hex+="B";
						break;
					case 12:
						Hex+="C";
						break;
					case 13:
						Hex+="D";
						break;
					case 14:
						Hex+="E";
						break;
					case 15:
						Hex+="F";
						break;
				}
				
				switch( H2 )
				{
					case 0:
						Hex+="0";
						break;		
					case 1:
						Hex+="1";
						break;
					case 2:
						Hex+="2";
						break;
					case 3:
						Hex+="3";
						break;
					case 4:
						Hex+="4";
						break;
					case 5:
						Hex+="5";
						break;
					case 6:
						Hex+="6";
						break;
					case 7:
						Hex+="7";
						break;
					case 8:
						Hex+="8";
						break;
					case 9:
						Hex+="9";
						break;
					case 10:
						Hex+="A";
						break;
					case 11:
						Hex+="B";
						break;
					case 12:
						Hex+="C";
						break;
					case 13:
						Hex+="D";
						break;
					case 14:
						Hex+="E";
						break;
					case 15:
						Hex+="F";
						break;
				}
				
			}
		}catch( Exception e )
		{
		}
		
		return Hex;
	}

	public synchronized String getIO_CFG()
	{
		//VIRLOS XVM
		if( Type == 1 || Type == 2 || ( Type == 7 && SubType == 3) || ( Type == 9 && (SubType == 2 || SubType == 3 ) ) )
			return "00000000";
		return "";
	}
	
	public synchronized String getIO_STATE()
	{
		String IO_STATE_TMP= null;
		
		//VIRLOC XVM
		try{
			//RGP
			if( Type == 1 || Type == 2)
			{ 
				IO_STATE_TMP = SA_PDU[0].substring(42,44);
			}
			//RUS CON RGP
			else if( Type == 7 && SubType == 3)
			{ 
				IO_STATE_TMP = SA0[2].substring(41,43);
			}
			//RAX61 CON RGP
			else if( Type == 9 && SubType == 2)
			{ 
				IO_STATE_TMP = SA0[1].substring(41,43);
			}
			//RAX10 CON RGP
			else if( Type == 9 && SubType == 3)
			{ 
				IO_STATE_TMP = SA0[7].substring(41,43);
			}
			if( IO_STATE_TMP != null )
			{	
				int i=0;
				String Bin="";
				
				for( i=0; i<2; i++ )
				{
					switch( IO_STATE_TMP.charAt(i))
					{
						case '0':
							Bin+="0000";
							break;
						case '1':
							Bin+="0001";
							break;
						case '2':
							Bin+="0010";
							break;
						case '3':
							Bin+="0011";
							break;
						case '4':
							Bin+="0100";
							break;
						case '5':
							Bin+="0101";
							break;
						case '6':
							Bin+="0110";
							break;
						case '7':
							Bin+="0111";
							break;
						case '8':
							Bin+="1000";
							break;
						case '9':
							Bin+="1001";
							break;
						case 'A':
							Bin+="1010";
							break;
						case 'B':
							Bin+="1011";
							break;
						case 'C':
							Bin+="1100";
							break;
						case 'D':
							Bin+="1101";
							break;
						case 'E':
							Bin+="1110";
							break;
						case 'F':
							Bin+="1111";
							break;
					}
					
				}
				return Bin;
			}
			return "";
		}catch( Exception e)
		{
			return "";
		}
	}
	
	public synchronized String  getIgnitionState()
	{
		try{
			return "" + this.getIO_STATE().charAt(0);
		}catch( Exception e )
		{
			return "null";
		}
		
	}
		
	private synchronized String toBin()
	{
		Bin = "";
		try{
			
			for( int i = 0; i < Hex.length(); i++ )
			{
				switch( Hex.charAt(i ))
				{
					case '0':
						Bin+="0000";
						break;
					case '1':
						Bin+="0001";
						break;
					case '2':
						Bin+="0010";
						break;
					case '3':
						Bin+="0011";
						break;
					case '4':
						Bin+="0100";
						break;
					case '5':
						Bin+="0101";
						break;
					case '6':
						Bin+="0110";
						break;
					case '7':
						Bin+="0111";
						break;
					case '8':
						Bin+="1000";
						break;
					case '9':
						Bin+="1001";
						break;
					case 'A':
						Bin+="1010";
						break;
					case 'B':
						Bin+="1011";
						break;
					case 'C':
						Bin+="1100";
						break;
					case 'D':
						Bin+="1101";
						break;
					case 'E':
						Bin+="1110";
						break;
					case 'F':
						Bin+="1111";
						break;
				}
			}
			
			
		}catch(Exception e)
		{
			Bin = "";
		}
		return Bin;
	}
		
	public synchronized String getBin()
	{
		return Bin;
	}
		
	public synchronized String getHex()
	{
		return Hex;
	}
		
	public synchronized String getUDPAPINumber()
	{
		//VIRLOC XVM
		return "";
	}
	
	public synchronized String getCommandType()
	{
		//VIRLOC XVM
		return "";
	}
	
	public synchronized String getMessageHeaderReserved()
	{
		//VIRLOC XVM
		return "";
		
	}
		
	public synchronized String getUserSpecifiedNumber()
	{
		//VIRLOC XVM
		return "0";
	}	
	
	public synchronized String getDateTimeSQL()
	{	
		String dt,d,t;
		Calendar C;
				
		try{
			if( Type == 2 || Type == 1 || Type == 3 )
			{	
				C = new GregorianCalendar();
				d = this.getGPSDate();
				t = this.getGPSTime();
		
				//C.set(Integer.parseInt( "20" + d.substring( 4,6) ), Integer.parseInt( d.substring(2,4))-1, Integer.parseInt( d.substring(0,2)), Integer.parseInt( t.substring(0,2)), Integer.parseInt( t.substring(2,4)), Integer.parseInt( t.substring(4,6)));
				C.set(Integer.parseInt( "20" + d.substring( 4 )), Integer.parseInt( d.substring(2,4))-1, Integer.parseInt( d.substring(0,2)), Integer.parseInt( t.substring(0,2)), Integer.parseInt( t.substring(2,4)), Integer.parseInt( t.substring(4)));
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
				C.add(Calendar.HOUR_OF_DAY, -5 );
				dt = sdf.format( C.getTime());
				return dt;
			}
			else if(  Type == 8 || Type == 18 || Type == 12)
			{
				C = new GregorianCalendar();
				C.setTimeInMillis( Time );
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS");
				dt = sdf.format( C.getTime());
				return dt;
			}
			else if( ( Type == 7 && SubType == 3 ) || ( Type == 9 && ( SubType == 2 || SubType == 3 ) ))
			{
				
					C = new GregorianCalendar();
					d = this.getGPSDate();
					t = this.getGPSTime();
		
					//C.set(Integer.parseInt( "20" + d.substring( 4,6) ), Integer.parseInt( d.substring(2,4))-1, Integer.parseInt( d.substring(0,2)), Integer.parseInt( t.substring(0,2)), Integer.parseInt( t.substring(2,4)), Integer.parseInt( t.substring(4,6)));
					C.set(Integer.parseInt( "20" + d.substring( 4 )), Integer.parseInt( d.substring(2,4))-1, Integer.parseInt( d.substring(0,2)), Integer.parseInt( t.substring(0,2)), Integer.parseInt( t.substring(2,4)), Integer.parseInt( t.substring(4)));
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
					C.add(Calendar.HOUR_OF_DAY, -5 );
					dt = sdf.format( C.getTime());
					return dt;
			}			
			else
			{
				C = new GregorianCalendar();
				C.setTimeInMillis( Time );
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS");
				dt = sdf.format( C.getTime());
				return dt;
				
			}
			
		}catch( Exception e )
		{
			System.err.println(e.toString());
			return "";
		}
			
	}
	
	public synchronized int getNumeroMensaje()
	{
		try{
			if( Type == 28)
			{
				return Integer.parseInt(SA_PDU[0].substring( 7, 14 ));
			}
			else if( Type == 18)
			{
				return Integer.parseInt( SA_PDU[0].substring( 7 ));
			}
			else
				return -1;
			
		}catch( Exception e )
		{
			return -1;
		}
		
	}
	
	public synchronized String getMensaje()
	{
		try{
			if( Type == 28 )
			{
				return SA_PDU[0].substring( 14 );
			}
			else if(Type == 18)
			{
				return "";
			}
			else
				return null;
			
		}catch( Exception e )
		{
			return null;
		}

	}
	
	public synchronized String getGPSTime()
	{
		//VIRLOC XVM
		String GPSTime;
		if( Type == 1 || Type == 2 )
		{ 
			try{
				
				GPSTime = SA_PDU[0].substring(10, 16);
				
				return GPSTime;
				
			}catch( Exception e)
			{
				return "";
			}
		}
		else if( Type == 3)
		{
			String Time="";

		    GregorianCalendar Calendario = this.getCalendario();
						
			if( Calendario.get( Calendar.HOUR_OF_DAY) <= 9 )
				Time+="0";
			Time+=Calendario.get( Calendar.HOUR_OF_DAY);
			if( Calendario.get( Calendar.MINUTE) <= 9 )
				Time+="0";
			Time+=Calendario.get( Calendar.MINUTE);
			if( Calendario.get( Calendar.SECOND) <= 9 )
				Time+="0";
			Time+=Calendario.get( Calendar.SECOND);
			return Time;
		}
		/*RUS CON RGP*/
		else if( Type == 7 && SubType == 3 )
		{
			try{
				
				GPSTime = SA0[2].substring(9, 15);
				
				return GPSTime;
					
			}catch( Exception e)
			{
				return "";
			}
		}
		/*RAX61 CON RGP*/
		else if( Type == 9 && SubType == 2 )
		{
			try{
				
				GPSTime = SA0[1].substring(9, 15);
				
				return GPSTime;
					
			}catch( Exception e)
			{
				return "";
			}
		}
		/*RAX10 CON RGP*/
		else if( Type == 9 && SubType == 3 )
		{
			try{
				
				GPSTime = SA0[7].substring(9, 15);
				
				return GPSTime;
					
			}catch( Exception e)
			{
				return "";
			}
		}
		else
		{
			return "";
		}
			
	}
	
	public synchronized String getGPSDate()
	{
		//VIRLOC XVM
		String GPSDate;
		if( Type == 1 || Type == 2 )
		{ 
			try{
				GPSDate = SA_PDU[0].substring(4, 10);
			
				return GPSDate;
					
			}catch( Exception e)
			{
				return "";
			}
		}
		/*FOTO*/
		else if( Type == 3)
		{
			String Date="";
			
			GregorianCalendar Calendario = this.getCalendario();
			
			//YYMMDD
			if( Calendario.get( Calendar.DAY_OF_MONTH) <= 9 )
				Date+="0";
			Date+=Calendario.get( Calendar.DAY_OF_MONTH);
		
			if( Calendario.get( Calendar.MONTH) < 9 )
				Date+="0";
			Date+=( Calendario.get( Calendar.MONTH ) + 1 );
			
			if( (""+ Calendario.get( Calendar.YEAR )).length() == 4 )
				Date+=(""+Calendario.get( Calendar.YEAR)).substring(2);
			else if( (""+ Calendario.get( Calendar.YEAR )).length() == 2 )
				Date+= Calendario.get( Calendar.YEAR);
			else if( (""+ Calendario.get( Calendar.YEAR )).length() == 1 )
				Date+= "0" + Calendario.get( Calendar.YEAR);	
			
			return Date;
		}
		/*RUS CON RGP*/
		else if( Type == 7 && SubType == 3 )
		{
			try{
				GPSDate = SA0[2].substring(3, 9);
			
				return GPSDate;
					
			}catch( Exception e)
			{
				return "";
			}
		}
		/*RAX61 CON RGP*/
		else if( Type == 9 && SubType == 2 )
		{
			try{
				GPSDate = SA0[1].substring(3, 9);
			
				return GPSDate;
					
			}catch( Exception e)
			{
				return "";
			}
		}
		/*RAX10 CON RGP*/
		else if( Type == 9 && SubType == 3 )
		{
			try{
				GPSDate = SA0[7].substring(3, 9);
			
				return GPSDate;
					
			}catch( Exception e)
			{
				return "";
			}
		}
		else
		{
			return "";
		}
	}
	
	public synchronized String getLatitude()
	{
		try{
			long f=0;
			int value = 0;
			
			//VIRLOC XVM
			if( Type == 1 ||Type == 2 )
			{
				if( SA_PDU[0].substring(16, 24).charAt(0) == '+' )
					f= Long.valueOf( SA_PDU[0].substring(17, 24) );
				else 	
					f= Long.valueOf( SA_PDU[0].substring(16, 24) );
				return "" + ((float)f)/100000;
			}
			else if( Type == 7 && SubType == 3 )
			{	//RUS CON RGP
				if( SA0[2].substring(15, 23).charAt(0) == '+' )
					f= Long.valueOf( SA0[2].substring(16, 23) );
				else 	
					f= Long.valueOf( SA0[2].substring(15, 23) );
				return "" + ((float)f)/100000;
			}
			else if( Type == 9 && SubType == 2 )
			{	//RAX CON RGP
				if( SA0[1].substring(15, 23).charAt(0) == '+' )
					f= Long.valueOf( SA0[1].substring(16, 23) );
				else 	
					f= Long.valueOf( SA0[1].substring(15, 23) );
				return "" + ((float)f)/100000;
			}
			else if( Type == 9 && SubType == 3 )
			{	//RAX CON RGP
				if( SA0[7].substring(15, 23).charAt(0) == '+' )
					f= Long.valueOf( SA0[7].substring(16, 23) );
				else 	
					f= Long.valueOf( SA0[7].substring(15, 23) );
				return "" + ((float)f)/100000;
			}
			else if( Type == 3 )
			{	
				byte valor[],tmpvalor[];
				
				valor = this.subbyte(B_PDU, 42, 45);
				
				tmpvalor = new byte[4];
				
				tmpvalor[0] = valor[3];
				tmpvalor[1] = valor[2];
				tmpvalor[2] = valor[1];
				tmpvalor[3] = valor[0];
				
				valor = tmpvalor;
				
			    for (int i = 0; i < 4; i++) 
			    {
			        int shift = (4 - 1 - i) * 8;
			        value += (valor[i] & 0x000000FF) << shift;
			    }
			    return "" + ((float)value)/100000;
			}
			System.err.println("*" + S_PDU );
			return "";
		}catch( Exception e)
		{
			System.err.println("ERROR: XVMPDU: GETLATITUDE: " + getType() + ": " + e.toString());
			System.err.println( new String(B_PDU));
			return null;
		}
	}
	
	public synchronized String getLongitude()
	{
		try{
			long f=0;
			int value=0;
			//VIRLOC XVM
			if( Type == 1 || Type == 2 )
			{
				if( SA_PDU[0].substring(24, 33).charAt(0) == '+' )
					f = Long.valueOf( SA_PDU[0].substring(25, 33) );
				else
					f = Long.valueOf( SA_PDU[0].substring(24, 33) );
				return "" + ((float)f)/100000;
			}
			else if( Type == 7 && SubType == 3 )
			{	//RUS CON RGP
				if( SA0[2].substring(23, 32).charAt(0) == '+' )
					f = Long.valueOf( SA0[2].substring(24, 32) );
				else
					f = Long.valueOf( SA0[2].substring(23, 32) );
				return "" + ((float)f)/100000;
			}
			else if( Type == 9 && SubType == 2 )
			{	//RAX CON RGP
				if( SA0[1].substring(23, 32).charAt(0) == '+' )
					f = Long.valueOf( SA0[1].substring(24, 32) );
				else
					f = Long.valueOf( SA0[1].substring(23, 32) );
				return "" + ((float)f)/100000;
			}
			else if( Type == 9 && SubType == 3 )
			{	//RAX CON RGP
				if( SA0[7].substring(23, 32).charAt(0) == '+' )
					f = Long.valueOf( SA0[7].substring(24, 32) );
				else
					f = Long.valueOf( SA0[7].substring(23, 32) );
				return "" + ((float)f)/100000;
			}
			else if( Type == 3 )
			{	
				byte valor[], tmpvalor[]
				                       ;
				valor = this.subbyte(B_PDU, 46, 49);
				
				tmpvalor = new byte[4];
				
				tmpvalor[0] = valor[3];
				tmpvalor[1] = valor[2];
				tmpvalor[2] = valor[1];
				tmpvalor[3] = valor[0];
				
				valor = tmpvalor;
						
			    for (int i = 0; i < 4; i++) 
			    {
			        int shift = (4 - 1 - i) * 8;
			        value += (valor[i] & 0x000000FF) << shift;
			    }
			    return "" + ((float)value)/100000;
			}
			System.err.println("*" + S_PDU );
			return "";
		}catch( Exception e)
		{
			System.err.println("ERROR: XVMPDU: GETLONGITUDE: " + getType() + ": " + e.toString());
			System.err.println( new String(B_PDU));
			return null;
		}
	}

	public synchronized String getSpeed()
	{
		//VIRLOC XVM
		
		// RGP o RTT
		if( Type == 1 || Type == 2 )
		{ 
			return SA_PDU[0].substring(33,36);
			
		}
		else if( Type == 7 && Type == 3 )
		{ 
			//RUX CON RGP
			return SA0[2].substring(32,35);
			
		}
		else if( Type == 9 && SubType == 2 )
		{ 
			//RAX CON RGP
			return SA0[1].substring(32,35);
			
		}
		else if( Type == 9 && SubType == 3 )
		{ 
			//RAX CON RGP
			return SA0[7].substring(32,35);
			
		}
		else if( Type == 3 && getNumeroByteInicial() == 0 )
		{
			return "" + StringToUInt( SA_PDU[0].substring( 32 , 34 ) );
		}
		else
		{
			return "null";
		}
			
	}
	
	public synchronized String getHeading()
	{
		//VIRLOC XVM
		
		// RGP o RTT
		if( Type == 1 || Type == 2)
		{ 
			return SA_PDU[0].substring(36,39);
			
		}
		else if( Type == 7 && Type == 3)
		{
			//RUS CON RGP
			return SA0[2].substring(35,38);
		}
		else if( Type == 9 && SubType == 2)
		{
			//RAX CON RGP
			return SA0[1].substring(35,38);
			
		}
		else if( Type == 9 && SubType == 3)
		{
			//RAX CON RGP
			return SA0[7].substring(35,38);
			
		}
		else
		{
			return "null";
			
		}
			
	}
	
	public synchronized String getOdometer()
	{
		//RAX CON RGP Y ODOMETRO
		if( Type == 9 && SubType == 2)
			return SA0[2];
		return "null";
	}
	
	public synchronized String getGPSStatus()
	{
		String V=null;
				
		// GP Y TT
		if( Type == 1 || Type == 2 )
		{ 
			V = SA_PDU[0].substring( 39 , 40);
			
		}
		//RUS CON RGP
		else if( Type == 7 && Type == 3 )
		{ 
			V = SA0[2].substring( 38 , 39);
		}
		//RAX CON RGP
		else if( Type == 9 && SubType == 2 )
		{ 
			V = SA0[1].substring( 38 , 39);
		}
		//RAX CON RGP
		else if( Type == 9 && SubType == 3 )
		{ 
			V = SA0[7].substring( 38 , 39);
		}
		
		if( V != null )
		{	
			if( V.compareTo( "0" ) == 0 )
			{
				return "8";
			}
			else if( V.compareTo("1" ) == 0 )
			{
				return "8";
			}
			else if( V.compareTo("2" ) == 0 )
			{
				return "9";
			}
			else if( V.compareTo("3" ) == 0 )
			{
				return "1";
			}
			else if( V.compareTo("4" ) == 0 )
			{
				return "7";
			}
			else if( V.compareTo("5" ) == 0 )
			{
				return "7";
			}
			else if( V.compareTo("6" ) == 0 )
			{
				return "6";
			}
			else if( V.compareTo("8" ) == 0 )
			{
				return "9";
			}
			else if( V.compareTo("9" ) == 0 )
			{
				return "9";
			}
		}
		return "";
	}
	
	public synchronized String getNumberOfSatellites()
	{
		String V=null;
		//VIRLOC XVM
		
		// GP Y TT
		if( Type == 1 || Type == 2 )
		{ 
			V = SA_PDU[0].substring( 39 , 40);
		}
		//RUS CON RGP
		else if( Type == 7 && SubType == 3 )
		{ 
			V = SA0[2].substring( 38 , 39);
		}
		//RAX CON RGP
		else if( Type == 9 && SubType == 2 )
		{ 
			V = SA0[1].substring( 38 , 39);
		}
		//RAX CON RGP
		else if( Type == 9 && SubType == 3 )
		{ 
			V = SA0[7].substring( 38 , 39);
		}	
		
		if( V != null )
		{
			if( V.compareTo( "0" ) == 0 )
			{
				return "8";
			}
			else if( V.compareTo("1" ) == 0 )
			{
				return "8";
			}
			else if( V.compareTo("2" ) == 0 )
			{
				return "1";
			}
			else if( V.compareTo("3" ) == 0 )
			{
				return "1";
			}
			else if( V.compareTo("4" ) == 0 )
			{
				return "7";
			}
			else if( V.compareTo("5" ) == 0 )
			{
				return "7";
			}
			else if( V.compareTo("6" ) == 0 )
			{
				return "6";
			}
			else if( V.compareTo("8" ) == 0 )
			{
				return "9";
			}
			else if( V.compareTo("9" ) == 0 )
			{
				return "9";
			}
		}
			
		return "";
	}
	
	public synchronized String getNumberOfMessage()
	{
		//VIRLOC XVM
		/* if( Type == 1 || Type == 2 )
		{
			return SA_PDU[2].substring( 1 );
		}
		else if( Type == 7 || Type == 9 )
		{
			return SA_PDU[2].substring( 1 );
		}
		else if( Type == 18 )
			return SA_PDU[]
		*/
		try{
			if( SA_PDU!=null && SA_PDU.length >=3 )
				return SA_PDU[2].substring( 1 );
			else	
				return "0000";
		}catch( Exception e)
		{
			System.err.println("ERROR: XVMPDU: GetNumberOfMessage: " + e.toString() );
			System.err.println("ERROR: XVMPDU: GetNumberOfMessage: " + SA_PDU );
			
			if( SA_PDU != null )
			{
				System.err.println("ERROR: XVMPDU: GetNumberOfMessage: " + SA_PDU.length );
				if( SA_PDU.length >= 3 )
				{
					System.err.println("ERROR: XVMPDU: GetNumberOfMessage: " + SA_PDU[2] );
					
				}
				
			}
			System.err.println("ERROR: XVMPDU: GetNumberOfMessage: " + S_PDU );
			return "0000";

		}
	}
	
	public synchronized String getCheckSum()
	{
		//VIRLOC XVM
		if( Type == 1 )
		{
			return SA_PDU[3].substring( 1 );
		}
		return "00";
	}
	
	public synchronized String getData()
	{
		//VIRLOC XVM
		if( Type == 1 )
		{
			return SA_PDU[0].substring( 1 );
		}
		return "0";
	}

	public synchronized String getRTC()
	{	
		//VIRLOC XVM
		if( Type == 1 || Type == 2 || ( Type == 7 && SubType ==3) || ( Type == 9 && ( SubType == 2 || SubType == 3 ) ) )
		{
			String  GPSDate;
			GPSDate = getGPSDate();
			return "20" + GPSDate.substring(4) + GPSDate.substring(2,4) + GPSDate.substring(0,2) + getGPSTime();
		}	
		return "0";
	}	
	
	public synchronized String getAltitude()
	{
		//VIRLOC XVM
		return "null";
	}
	
	public synchronized String getModemID()
	{
		//VIRLOC XVM
		try{
			
			return SA_PDU[1].substring(3);
			
		}catch( Exception e )
		{
			System.err.println("ERROR: XVMPDU: GetModemID: " + e.toString() );
			System.err.println("ERROR: XVMPDU: GetModemID: " + SA_PDU );
			
			if( SA_PDU != null )
			{
				System.err.println("ERROR: XVMPDU: GetModemID: " + SA_PDU.length );
				if( SA_PDU.length >= 2 )
				{
					System.err.println("ERROR: XVMPDU: GetModemID: " + SA_PDU[1] );
					
				}
				
			}
			System.err.println("ERROR: XVMPDU: GetModemID: " + S_PDU );
			System.err.println("ERROR: XVMPDU: GetModemID: " + IP );
			return null;
		}
			
	}
	
	public synchronized String getInputEvent( )
	{
		if( Type == 1 || Type == 2  )
		{
			return SA_PDU[0].substring(44,46);
		}	
		else if ( Type == 7 && SubType ==3 )
		{
			//RGP IN RUS
			return SA0[2].substring(43,45);
		}
		else if( Type == 9 && SubType == 2)
		{
			//RGP IN RAX
			return "61";
		}
		else if( Type == 9 && SubType == 3)
		{
			//RGP IN RAX
			return "10";
		}
		return "0";
	}
	
	public synchronized int BinToInt( String tmpBin )
	{
		int valor=0,i=0;
		
		try{
			for( i=0; i < tmpBin.length(); i ++ )
			{
				if( tmpBin.charAt(i) == '1' )
				{
					valor+=Math.pow( 2 ,  tmpBin.length()- ( i + 1 ));
				}
				
			}
		}catch( Exception e )
		{
			
		}
		return valor;
	}
	
	public synchronized String ToString()
	{
		String C="";
		if ( Type == 2)
        {	
			C += "SOURCE: " + this.getIP();
			C += ":" + this.getPuerto();
	        C += "; PDU LENGTH: " + B_PDU.length + "\n";
	        C += "UDP API Number: " + this.getUDPAPINumber();
	        C += "; Command Type: " + this.getCommandType();
	        C += "; Reserved: " + this.getMessageHeaderReserved() + "\n";
	        C += "GPSDate: " + this.getGPSDate();
	        C += "; GPSTime: " + this.getGPSTime();
	        C += "; ModemID: " + this.getModemID();
	        C += "; UserSpecifiedNumber: " + this.getUserSpecifiedNumber();
	        C += "; InputEvent: " + this.getInputEvent();
	        C += "; Latitude: " + this.getLatitude();
	        C += "; Longitude: " + this.getLongitude();
	        C += "; GPSAltitude: " + this.getAltitude();
	        C += "; GPSSpeed: " + this.getSpeed();
	        C += "; GPSHeading: " + this.getHeading();
	        C += "; NumberOfSatellites: " + this.getNumberOfSatellites();
	        C += "; GPSStatus: " + this.getGPSStatus();
	        C += "; Odometer: " + this.getOdometer();
	        C += "; IO_CFG: " + this.getIO_CFG();
	        C += "; IO_STATE: " + this.getIO_STATE();
	        
	        if( Type == 1 )
	        	C += "; RTC: " + this.getRTC();
        
	        return C;
        }
        return "";
		
	}
	
	public synchronized long BinToLong( String tmpBin )
	{
		long valor=0; int i=0;
		
		try{
			for( i=0; i < tmpBin.length(); i ++ )
			{
				if( tmpBin.charAt(i) == '1' )
				{
					valor+=Math.pow( 2 ,  tmpBin.length()- ( i + 1 ));
				}
				
			}
		}catch( Exception e )
		{
			return 0;
		}
		return valor;
	}
	
	public synchronized boolean isTaxiDataPDU()
	{
		if( getType() == 7 || ( getType() == 9 && getSubType() == 2) ) //>RUS
		{
			return true;
		}
		else if( getType() == 28 || getType() == 18 ) //>RVQRMN
		{
			return true;
									
		}	
		else if( getType() == 9  && ( getSubType() == 1 || getSubType() == 3 ) ) //>RAX
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public synchronized boolean isTaxiDataEstadoPDU()
	{
		if( getType() == 7 || ( getType() == 9 && getSubType() == 2) ) //>RUS
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public synchronized boolean isTaxiDataMensajePDU()
	{
		if( getType() == 28 || getType() == 18 ) //>RVQRMN
		{
			return true;
		}	
		else
		{
			return false;
		}
	}
	
	public synchronized boolean isTaxiDataTaximetroPDU()
	{
		if( getType() == 9  && ( getSubType() == 1 || getSubType() == 3 ) ) //>RAX
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public synchronized boolean isPositionReportPDU()
	{
		//VIRLOC XVM
				if( Type == 1 || Type == 2 || ( Type == 7 && SubType == 3 ) || ( Type == 9 && ( SubType == 2 || SubType == 3 ) )  )
					return true;
				else 
					return false;
	}
			
	public synchronized boolean isPDUofPhoto()
	{
		//VIRLOC XVM
		if( Type == 3 || Type == 4 || Type == 5 || Type == 6 || Type == 8 )
			return true;
		else 
			return false;
	}
	
	public synchronized boolean isCMDResponse()
	{
		if( this.getType() == 12 )
			return true;
		return false;
	}
	
	public synchronized int getType()
	{
		return Type;
	}
	
	public synchronized int getSubType()
	{
		return SubType;
	}
	
	public synchronized int getNumeroByteInicial()
	{
		try{
			if( Type == 3)
				return this.StringToUInt( "" + SA_PDU[0].charAt(5) +  SA_PDU[0].charAt(4));
			return -1;
		}catch( Exception e)
		{
			try{
				System.err.println("ERROR: XVMPDU: getNumeroByteInicial: " + e.toString());
				System.err.println("ERROR: XVMPDU: getNumeroByteInicial: " + SA_PDU);
				System.err.println("ERROR: XVMPDU: getNumeroByteInicial: " + SA_PDU[0]);
				System.err.println("ERROR: XVMPDU: getNumeroByteInicial: " + B_PDU);
			}catch( Exception e2)
			{
				System.err.println( "ERROR: XVMPDU: getNumeroByteInicial: TRY: " + e2 );
			}
			return -1;
		}
		
	}
	
	public synchronized byte[] getFotoPayload()
	{
	
		
		try{
			byte tmp[];
			tmp = new byte[128];
			
			if( Type == 3 && getNumeroByteInicial() != 0)
			{
				for( int i=0 ; i<128 ; i++ )
				{
					tmp[i] = B_PDU[ 6+i];
				}
				return tmp;
			}
		}catch( Exception e)
		{
			return null;
		}
		return null;
	}
	
	public synchronized String getCabeceraFoto( )
	{	
		try{
			if( Type == 3 && getNumeroByteInicial() == 0 )
			{
				return SA_PDU[0].substring(6,14);
			}
		}catch( Exception e)
		{
			return "";
		}
		return "";
	}
	
	public synchronized String getCabeceraFotoDescripcion( )
	{	
		try{
			if( Type == 3 && getNumeroByteInicial() == 0 )
			{
				return SA_PDU[0].substring(14,22);
			}
		}catch( Exception e)
		{
			return "";
		}
		return "";
	}
		
	public synchronized String getComando( )
	{	
		try{
			if( Type == 3 && getNumeroByteInicial() == 0 )
			{
				return SA_PDU[0].substring(14,22);
			}
		}catch( Exception e)
		{
			return "";
		}
		return "";
	}

	public synchronized int getTamanoFoto( )
	{
		int value=0;
		
		if( Type == 3 && getNumeroByteInicial() == 0 )
		{
			
			byte valor[],tmpvalor[];
			
			valor = this.subbyte(B_PDU, 30, 31);
			
			tmpvalor = new byte[4];
			
			tmpvalor[0] = 0;
			tmpvalor[1] = 0;
			tmpvalor[2] = valor[1];
			tmpvalor[3] = valor[0];
			
			valor = tmpvalor;
			
		    for (int i = 0; i < 4; i++) 
		    {
		        int shift = (4 - 1 - i) * 8;
		        value += (valor[i] & 0x000000FF) << shift;
		    }
		    return value;
			
		}
		return -1;
	}
	
	public synchronized int getNumeroFoto( )
	{
		try{
			if( Type == 3 && getNumeroByteInicial() == 0 )
			{
				byte valor[],tmpvalor[];
				long value=0;
				
				tmpvalor = this.subbyte( B_PDU, 26, 29);
				
				valor = new byte[8];
							
				valor[0] = tmpvalor[3];
				valor[1] = tmpvalor[2];
				valor[2] = tmpvalor[1];
				valor[3] = tmpvalor[0];
				valor[4] = 0x00;
				valor[5] = 0x00;
				valor[6] = 0x00;
				valor[7] = 0x00;
				
			    for (int i = 0; i < 8; i++) 
			    {
			        int shift = (8 - 1 - i) * 8;
			        value += (valor[i] & 0x00000000000000FF) << shift;
			    }
			    return (int)value;
			}
			else if( Type == 4 )
			{
				return (int)Long.parseLong(new String( subbyte(B_PDU, 12, 21)));
			}
			else if( Type == 8 )
			{
				return (int)Long.parseLong(new String( subbyte(B_PDU, 9, 18)));
			}
			return -1;
			
		}catch( Exception e)
		{
			System.err.println("" + S_PDU);
			System.err.println("ERROR: XVMPDU: GetNumeroFoto: " + e.toString() );
			return -1;
		}
		
	}
	
	public synchronized int getMarcaFoto() 
	{
		if( Type == 3 )
		{
			byte valor[],tmpvalor[];
			int value=0;
			
			valor = this.subbyte(B_PDU, 134, 135);
			
			tmpvalor = new byte[4];
			
			tmpvalor[0] = 0;
			tmpvalor[1] = 0;
			tmpvalor[2] = valor[0];
			tmpvalor[3] = valor[1];
			
			valor = tmpvalor;
			
		    for (int i = 0; i < 4; i++) 
		    {
		        int shift = (4 - 1 - i) * 8;
		        value += (valor[i] & 0x000000FF) << shift;
		    }
		    return value;
			
			
		}
		else if( Type == 4 || Type == 8 )
		{
			return ( getNumeroFoto() & 0x0000ffff );
		}
		return -1;
	}
		
	private synchronized byte[] subbyte( byte[] bytes , int i , int j )
	{
		
		
		try{
			
			if( bytes == null  )
				return new byte[1];
			
			if( i > j )
				i=0;
			
			if( j >= bytes.length )
				j = bytes.length - 1;
			
			int ii=0;
			
			byte[] subbytes = new byte[ (j-i)+1 ];
			
			
			while( i <= j)
			{
				subbytes[ii] = bytes[i];
				i++;ii++;
			
			}
			return subbytes;
			
		}catch( Exception e )
		{
			System.err.println( "XVMPDU: subbyte: " + e.toString());
			return new byte[1];
		}
		
	}
	
	public synchronized String getReportDateTime( int gmt ) 
	{
		try{
			String dt,d,t;
			Calendar C;
			
			
			C = new GregorianCalendar();
			
			
			if( getGPSStatus().compareTo("1") == 0 )
			{
				d = this.getGPSDate();
				t = this.getGPSTime();
				C.set( Integer.parseInt( "20" + d.substring( 4,6) ), Integer.parseInt( d.substring(2,4))-1, Integer.parseInt( d.substring(0,2)), Integer.parseInt( t.substring(0,2)), Integer.parseInt( t.substring(2,4)), Integer.parseInt( t.substring(4,6)));
				
			}
			else
			{
				d = this.getGPSDate();
				t = this.getGPSTime();
				C.set( Integer.parseInt( "20" + d.substring( 4,6) ), Integer.parseInt( d.substring(2,4))-1, Integer.parseInt( d.substring(0,2)), Integer.parseInt( t.substring(0,2)), Integer.parseInt( t.substring(2,4)), Integer.parseInt( t.substring(4,6)));
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss");
			C.add(Calendar.HOUR_OF_DAY, gmt );
			dt = sdf.format( C.getTime());
			
			return dt;
		}catch( Exception e)
		{
			System.err.println("ERROR: UDPAPIPDU: ReportDateTime:" + e.toString());
			
			return null;
		}
	}

	public synchronized byte[] getB_PDU()
	{
		return B_PDU;
	}

	public synchronized float getADC1()
	{
		return -1;
	}
	
	public synchronized float getADC2()
	{
		return -1;
	}
	
	public synchronized int getBatteryLevel()
	{
		return -1;
	}
	
	public synchronized String getACK()
	{
		return null;
	}

	@Override
	public byte[] getBACK() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTemperatura1() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTemperatura2() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getEA1() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getEA2() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getEA3() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getSA1() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getSA2() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getSA3() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getVoltajeAlimentacion() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getPuntoVisita() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getPuntoVisitaEstado() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int CellID0() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int SenalCellID0() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int CellID1() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int SenalCellID1() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int CellID2() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int SenalCellID2() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int CellID3() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int SenalCellID3() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int CellID4() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int SenalCellID4() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int CellID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int SenalCellID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int MCC() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int LAC() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int rpmOBD() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int PosicionAcceleradorOBD() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int OdometroOBD() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int OdometroViajeOBD() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float NivelGasolinaOBD() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float CombustibleRestanteOBD() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int EngraneTransmisionOBD() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float TemperaturaRefrigeranteOBD() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float IndiceGasolinaOBD() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float VoltajeAlimentacionOBD() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int EstadoSeñalesGiroOBD() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float GasolinaConsumidaPorViaje() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String IndicadoresOBD() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getHorometer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDTC() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getCheckEngine() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getMaxSpeed() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTimeMaxSpeed() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSpeedSetPoint() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEventoValor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEventoValorSP() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getImpactData() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
