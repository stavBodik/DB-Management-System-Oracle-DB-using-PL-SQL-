import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @author stav bodik
 *this class is used as singleton for manage application. 
 *includes instance for manage : DB,UI,Application Logic. 
 */
public class Application_Manager {

	// singleton instance
	private static Application_Manager instance = null;

	// instance of JDBC , used to connect and manage DB.
	JDBC jdbc;
	
	protected Application_Manager() {
		// Exists only to defeat instantiation.
	}
	/**
	 * Gets Application_Manager singleton instance
	 * @return instance of Application_Manager singleton.
	 */
	public static Application_Manager getInstance() {
		if (instance == null) {
			instance = new Application_Manager();
		}
		return instance;
	}
	/**
	 * Creates instance of Application_Manager singleton and connect to DB.
	 */
	public void loadJDBC(){
		jdbc = new JDBC();
		jdbc.loadDriverAndConnnect("localhost", "HR", "HR");
	}
	public JDBC getJdbc() {
		return jdbc;
	}
	public void writeToLogFile(String message){
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();

		String textToWrite = dateFormat.format(date)+" \n "+message;
		
		BufferedWriter bw = null;
		String filePath = System.getProperty("user.dir") + "/KalOnlineLog.txt";

		try {
			bw = new BufferedWriter(new FileWriter(filePath, true));
			bw.write(textToWrite);
			bw.newLine();
			bw.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (bw != null)
				try {
					bw.close();
				} catch (IOException ioe2) {

				}
		}
	}
	 
}

