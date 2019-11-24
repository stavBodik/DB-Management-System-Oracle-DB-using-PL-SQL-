public class Main {

	private static Application_Manager appManager;
	
	public static void main(String[] args) {
		// get application manager instance
		appManager = Application_Manager.getInstance();
		
		// initiate connection to DB server 
		appManager.loadJDBC();
		appManager.getJdbc().loadDriverAndConnnect("localhost", "HR", "HR");
		
		// load GUI
		new GUIManager();
	}

}
