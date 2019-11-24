import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;

public class JDBC {
    
	private Connection connection = null;
    private Statement statement = null;
	
	/**
	 * Run's function in DB.
	 * @param  functionName The name of the function to be called.
	 * @param  inputVariables input variables of the function 
	 * @return ArrayList<String> each element is an string of single row from DB comma separated,in case of error will return error message in the first element. 
	 */
	public ArrayList<String> runDBFunction(String functionName,String inputVariables){
    	
    	ArrayList<String> result = new ArrayList<>();
    	
    	try {
    		
    		System.out.println(functionName+"('"+inputVariables+"')");
    		
    		// create callable statement for function call with input variables.
			CallableStatement cs;
			
			if(inputVariables!=null){
				cs = connection.prepareCall("{? = call "+functionName+"('"+inputVariables+"')}");
			}
			else{
				cs = connection.prepareCall("{? = call "+functionName+"}");
			}
			
			cs.registerOutParameter(1, Types.VARCHAR);
		    cs.execute();
		    
            // The returned result is lines separated with $ 
		    String[] lines = cs.getString(1).split("\\$");
		    for(int i=0; i<lines.length; i++){
		    	result.add(lines[i]);
		    }
		    
		    cs.close();
		    
    	} catch (SQLException e) {
    		e.printStackTrace();
        	
    		result.add(e.getMessage());
        	
    		StringWriter errors = new StringWriter();
        	e.printStackTrace(new PrintWriter(errors));
        	
        	Application_Manager.getInstance().writeToLogFile(errors.toString());
    	}
    	
    	
    	return result;
    }
	
	
	/**
	 * Run's procedure in DB.
	 * @param procedureName  The name of the procedure to be called.
	 * @param inputVariables input variables of the procedure 
	 * @param "INSERT_ROW" procedure inputVariables example: 
	 * LOCATION_ID,STREET_ADDRESS,POSTAL_CODE,CITY,STATE_PROVINCE,COUNTRY_ID
	 * "INSERT_ROW","'LOCATIONS','11,''HAMESILA'',37000,''TEL-AVIV'',''YAYA'',''AR'''"
	 */
	public String runDBProcedure(String procedureName,String inputVariables){
		
		String res=null;
		
		try {
			System.out.println("{call "+procedureName+"("+inputVariables+")}");
			CallableStatement cs = connection.prepareCall("{call "+procedureName+"("+inputVariables+")}");						
			cs.executeUpdate();
		} 
		catch (SQLException e) {
		
			e.printStackTrace();
        	res= e.getMessage();
        	StringWriter errors = new StringWriter();
        	e.printStackTrace(new PrintWriter(errors));
        	Application_Manager.getInstance().writeToLogFile(errors.toString());
		
		}
		
		return res;
	}
	/**
	 
	 * Loads JDBC oracle driver and create connection with DB.
	 * @param host, The host name to connect to
	 * @param username, DB user name
	 * @param passwrod, DB user password
	 */
    public void loadDriverAndConnnect(String host,String username,String passwrod){
		
        try {
            //loads the driver class in memory.
            Class.forName("oracle.jdbc.driver.OracleDriver");
            // establish connection
            connection = DriverManager.getConnection("jdbc:oracle:thin:"+username+"/"+passwrod+"@"+host);
            statement = connection.createStatement();
        } catch (Exception e) {
        	e.printStackTrace();
        	StringWriter errors = new StringWriter();
        	e.printStackTrace(new PrintWriter(errors));
        	Application_Manager.getInstance().writeToLogFile(errors.toString());
        }
    }	
	/**
	 * close current connection with DB.
	 */
    public void closeConnection(){
		try {
            statement.close();
            connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			Application_Manager.getInstance().writeToLogFile(errors.toString());
		}        
	}
	
}


