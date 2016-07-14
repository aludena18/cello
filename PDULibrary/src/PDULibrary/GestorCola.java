package PDULibrary;

import java.util.LinkedList;

public class GestorCola 
{
    private LinkedList<Object> queueData;

    public GestorCola() 
    {
    	try{
    		queueData = new LinkedList<Object>();
    	}catch( Exception e)
    	{
    		System.err.println("ERROR: GestorCola: GestorCola: " + e.toString() );
    		queueData = null;
    	}
    }

    public synchronized int tamano()
    {
    	try{
    		
    		return queueData.size();
    		
    	}catch( Exception e )
    	{
    		System.err.println("ERROR: GestorCola: Tamano: " + e.toString() );
    		return -1;
    	}
    }
    
    public synchronized Object getElemento()
    {   
    	try{
	    	Object first = null;
	    	if ( queueData.size() > 0)
	    	{
	    		first = queueData.removeFirst();
	    	}
	    	return first;
    	}catch( Exception e)
    	{
    		System.err.println("ERROR: GestorCola: getElemento: " + e.toString() );
    		return null;
    	}
    }
    
    public synchronized void putElemento(Object obj) 
    {
    	try{
    		queueData.add(obj);
    	}catch( Exception e)
    	{
    		System.err.println("ERROR: GestorCola: putElemento: " + e.toString() );
    	}
    }
    
    public synchronized void Limpiar()
    {
    	try{
    		queueData.clear();
    	}catch( Exception e)
    	{
    		System.err.println("ERROR: GestorCola: Limpiar: " + e.toString() );
    	}
    }
  
}
