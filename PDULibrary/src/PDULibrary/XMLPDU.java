package PDULibrary;

public class XMLPDU 
{
	String XML;
		
	public XMLPDU( String tmpXML )
	{
		//XMLPDU = tmpXML.toUpperCase();
		XML = tmpXML;
		
	}
	
	/* <ELEMENTO>VALOR</ELEMENTO>
	 * XMLPDU.getElement = "VALOR" 
	 */
	public String getElement( String tmpLabel )
	{	
		String Element;
		int i=-1,j,k;
		
		try{
			i = XML.indexOf("<" + tmpLabel);
			if( i == -1)
			{
				Element = null;
			}
			else
			{
				j = XML.indexOf(">",i );
				k = XML.indexOf( "<", j +1);
				Element = XML.substring(j+1,k);
			}
			return Element;
		
		}catch( Exception e)
		{
			System.err.print( "#" + e.toString() + XML);
			return null;
		}
		
	}
	
	public String getPropiedadElemento( String tmpElemento, String tmpPropiedad )
	{	
		int i,j;
		String PDUElemento;
		
		PDUElemento = getNodo( tmpElemento );
		
		if( PDUElemento != null )
		{
			i = PDUElemento.indexOf( tmpPropiedad );
			if( i != -1 )
			{
				j = PDUElemento.indexOf("\"", i+tmpPropiedad.length()+ 2);
				
				if( j != -1 )
				{
					return PDUElemento.substring(i+tmpPropiedad.length()+ 2,j);
				}
				else
					return null;
			}
			else
			{
				return null;
			}
			
		}
		else
		{
			return null;
		}
	}

	public String getNodo( String tmpLabel )
	{
		int i, j;
		
		i = XML.indexOf( "<" + tmpLabel );
		
		if( i == -1 )
			return null;
		
		j = XML.indexOf( "</" + tmpLabel +">" );
		if( j < 0 )
			return null;
		
		j = j + tmpLabel.length() + 3;
		
		return XML.substring( i , j );
	}
	
	public String getXML()
	{
		return XML;
	}

}
