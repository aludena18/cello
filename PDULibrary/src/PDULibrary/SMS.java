package PDULibrary;

public class SMS 
{	
	String Numero;
	String Msj;
	int TOL = 5;
	
	public String ObtenerNumero()
	{ 	
		if ( Numero.length() == 8)
		{
			// Luis Moscoso 
			System.err.println("Correccion de Numero por Error Puerto SMS");
			Numero = "593" + Numero;
		}
		
		return Numero;
	}
	
	public String ObtenerNumeroFormatoInternacional()
	{
		try{
			
			if( Numero.length() == 9 && Numero.charAt(0) == '0' ) //ECUADOR
			{
				return "593" + Numero.substring( 1 , 9 );
			}
			else if( Numero.length() == 8 && Numero.charAt(0) != '0' ) //ECUADOR
			{
				return "593" + Numero;
			}
			else if( Numero.length() == 9 && Numero.charAt(0) != '0' ) //PERU
			{
				return "51" + Numero;
			}
			else if( Numero.length() == 10 && Numero.charAt(0) == '0') //PERU
			{
				return "51" + Numero.substring( 1 , 10 );
			}
			
			return Numero;
		
		}catch( Exception e )
		{
			System.err.println( "ERROR: SMS: ObtenerNumeroFormatoInternacional " + e.toString() );
			return null;
		}
		
	}
	
	public String ObtenerMsj()
	{	return Msj;
	}
	
	public int getTOL()
	{
		return TOL;
		
	}
	
	public void reduceTOL()
	{
		TOL --;
	}
	
	public SMS( String tmpNumero , String tmpMsj )
	{	
		Numero = tmpNumero;
		if( tmpMsj != null && tmpMsj.length() > 159 )
			Msj = tmpMsj.substring( 0 , 159 );
		else
			Msj = tmpMsj;
	}
	
}