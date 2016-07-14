//V5.0.0
//2009-06-30 

package PDULibrary;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class AppProperty extends Thread
{
	
	private Properties AppP; 

	public synchronized String getProperty( String tmpProperty )
	{
		try{
			
			synchronized(AppP)
			{
				return AppP.getProperty( tmpProperty );
			}
				
		}catch( Exception e)
		{
			System.err.println("ERROR: AppProperty: getProperty: " + e.toString() );
			return null;
		}
	}

	public void run()
	{		
		FileInputStream FIS;
		
		while( true )
		{
			try{
				Thread.sleep( 60000 );
			}catch( Exception e)
			{
				
			}
			try{
				System.gc();
			}catch( Exception e)
			{
				System.err.println( "ERROR: AppProperty: Run: GC: " + e.toString() );
			}
			try{
				
				
				synchronized( AppP)
				{
					File F =  new File("Hades.ini");
					if( F.exists() == false )
					{
						System.err.println("MSG: AppProperty: Run: No existe archivo " + F.getAbsolutePath() );
					}
					else
					{
						FIS = new FileInputStream("Hades.ini");
						AppP.load( FIS );
						FIS.close();
					}
				}			
			}catch( Exception e)
			{
				System.err.println( "ERROR: AppProperty: Run: " + e.toString() );
			}
		
		}
		
	}
	
	public AppProperty()
	{	
		try{
			
			FileInputStream FIS;
			AppP = new Properties();
			
			this.setName( this.getName() + ": AppProperty");
			
			FIS = new FileInputStream("Hades.ini");
			
			AppP.load( FIS );
						
			FIS.close();
			
		}catch( Exception e)
		{
			System.err.println( "ERROR: AppProperty: AppProperty: " + e.toString() );
		}
			
		this.start();
	}

}
