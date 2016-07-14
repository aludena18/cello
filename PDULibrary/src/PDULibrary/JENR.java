package PDULibrary;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JENR
{
	public synchronized static String ByteToHex( final byte b )
	{
		 String pseudo[] = {"0", "1", "2",
				 			"3", "4", "5",
				 			"6", "7", "8",
				 			"9", "A", "B", 
				 			"C", "D", "E",
				 			"F"};
		 
		 byte ch;
		 String Hex="";
		 ch = (byte) (b & 0xF0); // Strip off high nibble
		 ch = (byte) (ch >>> 4); // shift the bits down
		 ch = (byte) (ch & 0x0F);// must do this is high order bit is on!
		 Hex+=pseudo[ (int) ch]; // convert the nibble to a String Character
		 ch = (byte) (b & 0x0F); // Strip off low nibble 
		 Hex+=pseudo[ (int) ch]; // convert the nibble to a String Character
		 return Hex;
	}
	
	public synchronized static String ByteArrayToHexString( final byte b[] )
	{
		String Hex = "";
		int i=0;
		
		while( b != null && i < b.length )
		{
			Hex = Hex + ByteToHex(b[i]);
			i++;
		}
		
		return Hex;
	}
	
	public static long BinToLong( String tmpBin )
	{
		try{
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
		}catch( Exception e)
		{
			System.err.println("JENR: BinToLong: " + e.toString() );
			return 0;
		}
	}
	
	public static int BinToInt( String tmpBin )
	{
		try{	
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
		}catch( Exception e)
		{
			System.err.println("JENR: BinToInt: " + e.toString() );
			return 0;
		}
	}
	
	public static String HexToBin( String Hex )
	{
		String Bin = "";
		
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
			System.err.println("ERROR: UDPAPIPDU: HeXtoBIN: " + e.toString() );
			Bin = "";
		}
		return Bin;
	}
	
	public static boolean isValidDate(String date)
	{
	    Date testDate = null;
	    SimpleDateFormat sdf=new SimpleDateFormat( "yyyyMMdd HH:mm:ss" );;
		try
	    {
			testDate = sdf.parse(date);
	
	    }catch (ParseException e)
	    {
	      
	      return false;
	    }
		if (!sdf.format(testDate).equals(date))
	    {
	      return false;
	    }
	    return true;
	} 	
	
	public static double BinToDouble( String cadena)
	{
		try{
			double valor=0; int i;
			
			for ( i=0; i < cadena.length() ; i++ )
			{
				if( cadena.charAt(i) == '1' )
					valor+= Math.pow( 2, cadena.length() - i - 1 );
			}
				
			return valor;
		}catch( Exception e)
		{
			System.err.println( "JENR: Exception: BinToDouble: " +  e.toString() );
			return 0;
		}
		
	}
	
	public String BinToComplemento2( String binario )
	{
		try{
			String cadena= "";
			
			if( binario.charAt(0) == '1' )
			{
				for( int i=0; i < binario.length() ; i++ )
				{
					if( binario.charAt( i) == '1' )
					{
						cadena += "0";
					}
					else if( binario.charAt(i ) == '0' )
					{
						cadena += "1";
					}
				}
			}
			else
				cadena += binario;
			return cadena;
		}catch( Exception e)
		{
			System.err.println( "JENR: Exception: BinarioPositivo: " +  e.toString() );
			return null;
		}
	}
	
}
