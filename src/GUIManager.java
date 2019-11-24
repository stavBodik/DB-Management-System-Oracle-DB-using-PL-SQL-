import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/** 
 * @author stav bodik
 * Application GUI Manager , loads the UI and manage changes in the UI by user actions .
 */
public class GUIManager {

	private double screenWith,ScreenHeight;
	private int mainPanelWith,mainPanelHeight;
	private int windowWith,windowHeight;
	private JFrame mainWindowFrame;
	private JPanel mainPanel,topMenuPanel,homePanel,tableControlPanel,qPanel;
	private TableListPanel tableListPanel;
	private TabelByFunctionNamePanel showTableByFunctionInfoPanel;
	private SpringLayout mainWindowFramelLayOut;
	private static Application_Manager appManager;
	private String currentViewingTableName="";
	private JTable table=null;
	private int showStartRow,showEndRow;
	private int tableViewMode=Const.TABLE_VIEW_MODE_BROWSE_TABLE;
	private int pageView=Const.PAGE_VIEW_HOME;
	private ArrayList<JButtonImage> tableHeaders;
	// used to indicate about table sort for each column : 0 sort ASC 1 SORT DESC
	private ArrayList<Integer> sortByArray; 	
			    
	// indicates whenever some error happen.
	private boolean errorAcuire=false;
	
	public GUIManager() {
    	// get application manager instance
		appManager = Application_Manager.getInstance();
		initUI();
	}
	public void initUI(){
		
		setWindowsLookAndFeel();
		loadScreenSize();
		initMainWindowFrame();
		initMainPanel();	
		initTopMenuPanel();
		initHomePanel();
		mainWindowFrame.setVisible(true);

	}
	/**
	 * Initiate main window JFrame of the application background,title,location,icon
	 */
	public void initMainWindowFrame(){
		mainWindowFrame = new JFrame("KalOnline DB Manager");
		mainWindowFrame.setLayout(mainWindowFramelLayOut);
		windowWith=(int)(screenWith*0.6);
		windowHeight=(int)(ScreenHeight*0.8);
		mainPanelWith=(int)(windowWith*0.8);
		mainPanelHeight=(int)(windowHeight*0.8);
		mainWindowFrame.setSize(windowWith,windowHeight);
		mainWindowFrame.setLocation((int)(screenWith/2-screenWith*0.6/2), (int)(ScreenHeight*0.1));
		mainWindowFrame.setResizable(false);
		mainWindowFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		ImageIcon img = new ImageIcon(this.getClass().getResource("images/appicon.png"));
		mainWindowFrame.setIconImage(img.getImage());
		
		JImage mainBackGround = new JImage("images/backmain.jpg");
		mainWindowFrame.setContentPane(mainBackGround);
		
		mainWindowFramelLayOut = new SpringLayout();
		mainWindowFrame.setLayout(mainWindowFramelLayOut);
		
		mainWindowFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				appManager.getJdbc().closeConnection();
				super.windowClosing(e);
			}
		});
	}	
	public void initMainPanel(){
		
		mainPanel = new JPanel();
		mainPanel.setBackground(Color.RED);
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setSize(mainPanelWith,mainPanelHeight);
		mainWindowFrame.add(mainPanel);
		
		mainWindowFramelLayOut.putConstraint(SpringLayout.WEST, mainPanel, windowWith/2-mainPanelWith/2, SpringLayout.WEST, mainWindowFrame);
		mainWindowFramelLayOut.putConstraint(SpringLayout.NORTH, mainPanel, (int)(windowHeight*0.1), SpringLayout.NORTH, mainWindowFrame);
	}	
	public void initTopMenuPanel(){
		
		topMenuPanel = new JPanel();
		topMenuPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		int topMenuPanelHeight = (int)(mainPanelHeight*0.1);
		topMenuPanel.setPreferredSize(new Dimension(mainPanelWith, topMenuPanelHeight));
		SpringLayout topMenuLayOut = new SpringLayout();
		topMenuPanel.setLayout(topMenuLayOut);
		
		int iconSize = (int)(topMenuPanelHeight*0.7);
		
		JButtonImage homeBT = new JButtonImage("images/homeicon.png","",iconSize,iconSize);
		homeBT.setToolTipText("Home button");
		topMenuPanel.add(homeBT);
		topMenuLayOut.putConstraint(SpringLayout.WEST, homeBT, (int)(mainPanelWith*0.02), SpringLayout.WEST, topMenuPanel);
		topMenuLayOut.putConstraint(SpringLayout.NORTH, homeBT,(int)(topMenuPanelHeight)/2-iconSize/2, SpringLayout.NORTH, topMenuPanel);

		homeBT.addActionListener(new ActionListener() {
				
			@Override
			public void actionPerformed(ActionEvent arg0) {

				if(pageView==Const.PAGE_VIEW_BROWSE){
				sortByArray=null;
				mainPanel.remove(tableListPanel);
				mainPanel.remove(tableControlPanel);				
				}else if(pageView==Const.PAGE_VIEW_SHOW_TABLE_BY_FUNCTION){
					sortByArray=null;
					mainPanel.remove(showTableByFunctionInfoPanel);
				}else if(pageView==Const.PAGE_VIEW_QUERY){
					sortByArray=null;
					mainPanel.remove(qPanel);
				}else if(pageView==Const.PAGE_VIEW_HOME){
					mainPanel.remove(homePanel);
				}
				
				initHomePanel();
				mainPanel.revalidate();
				mainPanel.repaint();
				pageView=Const.PAGE_VIEW_HOME;

			}
		});
		
		
		/*JButtonImage searchBT = new JButtonImage("images/searchicon.png","",iconSize,iconSize);
		searchBT.setToolTipText("Search value in DB");
		topMenuPanel.add(searchBT);
		topMenuLayOut.putConstraint(SpringLayout.WEST, searchBT, (int)(mainPanelWith*0.1), SpringLayout.WEST, homeBT);
		topMenuLayOut.putConstraint(SpringLayout.NORTH, searchBT,(int)(topMenuPanelHeight)/2-iconSize/2, SpringLayout.NORTH, topMenuPanel);
		 */
		
		JButtonImage sqlBT = new JButtonImage("images/sqlicon.png","",iconSize,iconSize);
		sqlBT.setToolTipText("Run query");
		topMenuPanel.add(sqlBT);
		topMenuLayOut.putConstraint(SpringLayout.WEST, sqlBT, (int)(mainPanelWith*0.1), SpringLayout.WEST, homeBT);
		topMenuLayOut.putConstraint(SpringLayout.NORTH, sqlBT,(int)(topMenuPanelHeight)/2-iconSize/2, SpringLayout.NORTH, topMenuPanel);
		 
		sqlBT.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {

				if(pageView==Const.PAGE_VIEW_SHOW_TABLE_BY_FUNCTION){
					mainPanel.remove(showTableByFunctionInfoPanel);
				}else if (pageView==Const.PAGE_VIEW_BROWSE){
					mainPanel.remove(tableListPanel);
					mainPanel.remove(tableControlPanel);
					sortByArray=null;
				}else if(pageView==Const.PAGE_VIEW_HOME){
					mainPanel.remove(homePanel);
				}else if(pageView==Const.PAGE_VIEW_QUERY){
					return;
				}
				
				initQueryPanel();
				mainPanel.revalidate();
				mainPanel.repaint();	
				pageView=Const.PAGE_VIEW_QUERY;
				
			}
		});
		 
		ArrayList<String> users_table = appManager.getJdbc().runDBFunction("GET_TABLE_TO_STRING", "players");

		
		JPanel selectUserByIdPanel = new JPanel();
		selectUserByIdPanel.setLayout(new BorderLayout());
		selectUserByIdPanel.setSize(new Dimension((int) (mainPanelWith*0.4), (int)(topMenuPanelHeight)));
		
		final JComboBox<String> c = new JComboBox<String>();
		for (int i = 1; i < users_table.size(); i++)
		c.addItem("user id : "+users_table.get(i).split(",")[2]+" name : "+users_table.get(i).split(",")[3]);
		selectUserByIdPanel.add(c,BorderLayout.SOUTH);
		selectUserByIdPanel.add(new JLabel("View User info : "),BorderLayout.NORTH);

		topMenuPanel.add(selectUserByIdPanel);
		topMenuLayOut.putConstraint(SpringLayout.WEST, selectUserByIdPanel, (int)(mainPanelWith*0.1), SpringLayout.WEST, sqlBT);
		topMenuLayOut.putConstraint(SpringLayout.NORTH, selectUserByIdPanel,(int)(topMenuPanelHeight*0.2f), SpringLayout.NORTH, topMenuPanel);

		
		Button viewUserInfoBT = new Button("View");
		topMenuPanel.add(viewUserInfoBT);
		topMenuLayOut.putConstraint(SpringLayout.WEST, viewUserInfoBT, (int)(mainPanelWith*0.01), SpringLayout.EAST, selectUserByIdPanel);
		topMenuLayOut.putConstraint(SpringLayout.NORTH, viewUserInfoBT,(int)(topMenuPanelHeight*0.5f), SpringLayout.NORTH, topMenuPanel);

		
		viewUserInfoBT.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {

				if(pageView==Const.PAGE_VIEW_SHOW_TABLE_BY_FUNCTION){
					mainPanel.remove(showTableByFunctionInfoPanel);
				}else if (pageView==Const.PAGE_VIEW_BROWSE){
					mainPanel.remove(tableListPanel);
					mainPanel.remove(tableControlPanel);
					sortByArray=null;
				}else if(pageView==Const.PAGE_VIEW_HOME){
					mainPanel.remove(homePanel);
				}else if(pageView==Const.PAGE_VIEW_QUERY){
					mainPanel.remove(qPanel);
				}
				
				initShowTabelByFunctionPanel("JOIN_ALL_BY_UID",c.getSelectedItem().toString().split(" ")[3].trim(),BorderLayout.CENTER);
				mainPanel.revalidate();
				mainPanel.repaint();	
				pageView=Const.PAGE_VIEW_SHOW_TABLE_BY_FUNCTION;
				
			}
		});
		  
		
		mainPanel.add(topMenuPanel,BorderLayout.NORTH);
		
		
		
	}
	public void initHomePanel(){
		homePanel = new JPanel();
		homePanel.setBorder(BorderFactory.createLineBorder(Color.black));
		homePanel.setPreferredSize(new Dimension(mainPanelWith, (int)(mainPanelHeight*0.9)));
		ArrayList<String> tableNames = appManager.getJdbc().runDBFunction("GET_ALL_TABLES_NAMES",null);
		for(int i=1; i<tableNames.size(); i++)homePanel.add(new TableRowPanel(tableNames.get(i).split(",")[1]));
		mainPanel.add(homePanel,BorderLayout.CENTER);
	}
	public void initTableControlPanel(){
		tableControlPanel = new JPanel();
		int controlPanelHeight=(int)(mainPanelHeight*0.07);
		tableControlPanel.setBorder(BorderFactory.createLineBorder(Color.black) );
		tableControlPanel.setPreferredSize(new Dimension(mainPanelWith,controlPanelHeight));
		
		SpringLayout controlPanelLayOut = new SpringLayout();
		tableControlPanel.setLayout(controlPanelLayOut);
		
		int iconSize = (int)(controlPanelHeight*0.6);
		
		JButtonImage addRowBT = new JButtonImage("images/addrowicon_control.png","insert",iconSize,iconSize);
		tableControlPanel.add(addRowBT);
		controlPanelLayOut.putConstraint(SpringLayout.WEST, addRowBT, (int)(mainPanelWith*0.02), SpringLayout.WEST, tableControlPanel);
		controlPanelLayOut.putConstraint(SpringLayout.NORTH, addRowBT,(int)(controlPanelHeight)/2-iconSize/2, SpringLayout.NORTH, tableControlPanel);

		addRowBT.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {

				insertRowPopUp row = new insertRowPopUp(currentViewingTableName);
				mainWindowFrame.setEnabled(false);
				if(!errorAcuire){
					row.setVisible(true);
					tableViewMode=Const.TABLE_VIEW_MODE_INSERT_ROW;
					
				}
				
				row.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						mainWindowFrame.setEnabled(true);
						tableViewMode=Const.TABLE_VIEW_MODE_BROWSE_TABLE;
						super.windowClosing(e);
					}
				});
			}
		});
		JButtonImage deleteRowBT = new JButtonImage("images/deleteicon.png","delete",iconSize,iconSize);
		tableControlPanel.add(deleteRowBT);
		controlPanelLayOut.putConstraint(SpringLayout.WEST, deleteRowBT, (int)(mainPanelWith*0.1), SpringLayout.WEST, addRowBT);
		controlPanelLayOut.putConstraint(SpringLayout.NORTH, deleteRowBT,(int)(controlPanelHeight)/2-iconSize/2, SpringLayout.NORTH, tableControlPanel);

		deleteRowBT.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteTableRows();				
			}
		});
		
		JButtonImage commitBT = new JButtonImage("images/commiticon.png","commit",iconSize,iconSize);
		tableControlPanel.add(commitBT);
		controlPanelLayOut.putConstraint(SpringLayout.WEST, commitBT, (int)(mainPanelWith*0.1), SpringLayout.WEST, deleteRowBT);
		controlPanelLayOut.putConstraint(SpringLayout.NORTH, commitBT,(int)(controlPanelHeight)/2-iconSize/2, SpringLayout.NORTH, tableControlPanel);

		commitBT.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				commitTableChanges();				
			}
		});
		
		JLabel showTableRange = new JLabel("Show :   start row");
		showTableRange.setPreferredSize(new Dimension((int)(mainPanelWith*0.15), iconSize));
		tableControlPanel.add(showTableRange);
		controlPanelLayOut.putConstraint(SpringLayout.WEST, showTableRange, (int)(mainPanelWith*0.2), SpringLayout.WEST, commitBT);
		controlPanelLayOut.putConstraint(SpringLayout.NORTH, showTableRange,iconSize/2, SpringLayout.NORTH, tableControlPanel);

		final JTextField rowStart = new JTextField();
		rowStart.setText(Integer.toString(showStartRow));
		rowStart.setPreferredSize(new Dimension((int)(mainPanelWith*0.07), iconSize));
		tableControlPanel.add(rowStart);
		controlPanelLayOut.putConstraint(SpringLayout.WEST, rowStart, 0, SpringLayout.EAST, showTableRange);
		controlPanelLayOut.putConstraint(SpringLayout.NORTH, rowStart,iconSize/2, SpringLayout.NORTH, tableControlPanel);

		JLabel numberOfRowsLabel = new JLabel(" number of rows :");
		numberOfRowsLabel.setPreferredSize(new Dimension((int)(mainPanelWith*0.15), iconSize));
		tableControlPanel.add(numberOfRowsLabel);
		controlPanelLayOut.putConstraint(SpringLayout.WEST, numberOfRowsLabel, 0, SpringLayout.EAST, rowStart);
		controlPanelLayOut.putConstraint(SpringLayout.NORTH, numberOfRowsLabel,iconSize/2, SpringLayout.NORTH, tableControlPanel);

		final JTextField numberOfRows = new JTextField();
		numberOfRows.setText(Integer.toString(showEndRow-1));
		numberOfRows.setPreferredSize(new Dimension((int)(mainPanelWith*0.07), iconSize));
		tableControlPanel.add(numberOfRows);
		controlPanelLayOut.putConstraint(SpringLayout.WEST, numberOfRows, 0, SpringLayout.EAST, numberOfRowsLabel);
		controlPanelLayOut.putConstraint(SpringLayout.NORTH, numberOfRows,iconSize/2, SpringLayout.NORTH, tableControlPanel);

		JButtonImage refreshBT = new JButtonImage("images/refreshicon.png","refresh",iconSize,iconSize);
		tableControlPanel.add(refreshBT);
		controlPanelLayOut.putConstraint(SpringLayout.WEST, refreshBT,0, SpringLayout.EAST, numberOfRows);
		controlPanelLayOut.putConstraint(SpringLayout.NORTH, refreshBT,(int)(controlPanelHeight)/2-iconSize/2, SpringLayout.NORTH, tableControlPanel);

		refreshBT.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
				showStartRow=(Integer.parseInt(rowStart.getText().toString()));
				showEndRow=(Integer.parseInt(numberOfRows.getText().toString())+1);
				}catch(Exception e1){
					 showErrorMessage("Range must be a numerical value");
					 return;
				}
				
				if(showEndRow>tableListPanel.getNumberOfRowsInTable()+1){
						showErrorMessage("Wrong range , this table has " + tableListPanel.getNumberOfRowsInTable()+" rows");
						return;
				}else if(showStartRow<1 || showStartRow>tableListPanel.getNumberOfRowsInTable()+1){
					showErrorMessage("Wrong range , start row range must be between 1 and "+tableListPanel.getNumberOfRowsInTable() );
					return;
				}else if(showStartRow>showEndRow-1 || showEndRow<showStartRow-1){
					showErrorMessage("Wrong start or end row range must be between 1 and "+tableListPanel.getNumberOfRowsInTable());
					return;
				}
				
				mainPanel.remove(tableListPanel);
				initTablePanel(currentViewingTableName,showStartRow,showEndRow);
				mainPanel.revalidate();
				mainPanel.repaint();
				
				
			}
		});
		
		mainPanel.add(tableControlPanel,BorderLayout.CENTER);
	}
	public void initTablePanel(String tableName,int showStartRow,int showEndRow){
		tableListPanel= new TableListPanel(tableName,false,showStartRow,showEndRow);
		tableListPanel.setPreferredSize(new Dimension(mainPanelWith,(int)(mainPanelHeight*0.83)));
		mainPanel.add(tableListPanel,BorderLayout.SOUTH);	
	}
	public void initShowTabelByFunctionPanel(String functionName ,String inputVriebels,String layOutLocation){
		showTableByFunctionInfoPanel= new TabelByFunctionNamePanel(functionName,inputVriebels);
		//showUserInfoPanel.setPreferredSize(new Dimension(mainPanelWith,(int)(mainPanelHeight)));
		mainPanel.add(showTableByFunctionInfoPanel,layOutLocation);	
	}
	public void initQueryPanel(){
		
		qPanel = new QueryPanel();
		mainPanel.add(qPanel,BorderLayout.CENTER);

	}
	
	public void setWindowsLookAndFeel(){
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

	}
	public void showErrorMessage(String message){
	        JOptionPane op = new JOptionPane(message,JOptionPane.ERROR_MESSAGE);
	        JDialog dialog = op.createDialog("Error");
	        ImageIcon img = new ImageIcon(this.getClass().getResource("images/appicon.png"));
	        dialog.setIconImage(img.getImage());
	        dialog.setAlwaysOnTop(true); 
	        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	        dialog.setVisible(true);
	        errorAcuire=false;
	}
	public void showOkMessage(String message,String titel){
        JOptionPane op = new JOptionPane(message,JOptionPane.INFORMATION_MESSAGE);
        JDialog dialog = op.createDialog(titel);
        ImageIcon img = new ImageIcon(this.getClass().getResource("images/appicon.png"));
        dialog.setIconImage(img.getImage());
        dialog.setAlwaysOnTop(true); 
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
        errorAcuire=false;
    }
	public int  showChoseMessage(String message,String titel){
		return JOptionPane.showConfirmDialog(null,message,titel,JOptionPane.YES_NO_OPTION);
	}
	public void commitTableChanges(){
		
		int numberOfTableCols =table.getModel().getColumnCount();
		int numberOfTableRows =table.getModel().getRowCount();
		boolean isError=false;
		
		for(int i=0;i<numberOfTableRows;i++){
			for(int j=3; j<numberOfTableCols; j++){
				String colName = table.getModel().getColumnName(j).split("\\(")[0];
				String pkID=(String) table.getModel().getValueAt(i, 2);
				String newValue = (String) table.getModel().getValueAt(i, j);
				String res =appManager.getJdbc().runDBProcedure("UPDATE_COL_VALUE", "'"+currentViewingTableName+"','"+pkID+"','"+colName+"','"+newValue+"'");

				if(res!=null){
					showErrorMessage("Error commit in line " + (i+1) +" column "+(j+1)+" please check values types\n"+res);
					isError=true;
					break;
				}
			}
			if(isError)break;
		}
		
		if(!isError)showOkMessage("Commint changes", "Chnages commited successfully");

	}
	public void deleteTableRows(){
		
		boolean isError=false;
		for(int i=0;i<table.getModel().getRowCount();i++)
        {
          if ((Boolean) table.getModel().getValueAt(i,0))
          {  
        	  
        	  if(!currentViewingTableName.equals("INVENTORIES")){
	        	  String res=appManager.jdbc.runDBProcedure("DELETE_ROW_BY_PK","'"+currentViewingTableName+"',"+(String)table.getModel().getValueAt(i,2));
	        	  if(res!=null){
	        		  showErrorMessage("Cannot delete PK \n"+ res);
	        		  isError=true;
	        		  break;
	        	  }
        	  }else{
        		  String res=appManager.jdbc.runDBProcedure("DELETE_INVENTORY",(String)table.getModel().getValueAt(i,2)+","+(String)table.getModel().getValueAt(i,3));
	        	  if(res!=null){
	        		  showErrorMessage("Cannot delete PK \n"+ res);
	        		  isError=true;
	        		  break;
	        	  }
        	  }
          
          }
       }
		if(!isError){
			showOkMessage("rows deleted successfully", "Delete rows");
			mainPanel.remove(tableListPanel);
			initTablePanel(currentViewingTableName,-1,-1);
			mainPanel.revalidate();
			mainPanel.repaint();
		}
	}
	/**
	 * loads screen size used to set dimensions of UI components.
	 */
	public void loadScreenSize(){
		 Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		 screenWith = screenSize.getWidth();
		 ScreenHeight = screenSize.getHeight();
	}
	/**
	 * This class used to draw simple JComponent with background
	 */
	private class JImage extends JComponent{
		private static final long serialVersionUID = Const.serialVersionUID;
		private ImageIcon img;
		
		public JImage(String path) {
	        URL imgURL = GUIManager.class.getResource(path);
	        img = new ImageIcon(imgURL);
		}
		@Override
		protected void paintComponent(Graphics g) {
	        g.drawImage(img.getImage(), 0, 0, windowWith, windowHeight, null); 			
	        super.paintComponent(g);
		}
		public ImageIcon getImg() {
			return img;
		}
	}
    /**
     * TableListPanel is a scroll able JPanel with JTable inside from DB 
     * Used to browse table from DB or to view simple pop-up for inserting new row to DB.
     * @author stav bodik
     */
	private class TableListPanel extends JPanel {
    	
		private static final long serialVersionUID = Const.serialVersionUID;
		private int numberOfRowsInTable=0;
				
    	public TableListPanel(final String tableaName,boolean isInsertNewRow,int startRow,int numberOfRows) {
    	
    		setLayout(new BorderLayout());
    		currentViewingTableName=tableaName;
    		
    		// get columns data types by table name from db
			final ArrayList<String> colDataTypes = appManager.getJdbc().runDBFunction("GET_TABLE_COLUMNS_TYPES", tableaName);
			colDataTypes.add(0, ""); // row number at column 0 no type
			if(colDataTypes.size()==1){
				showErrorMessage("Connection DB error : " + colDataTypes.get(0) +",please view log file");
				errorAcuire=true;
				return;
			}
			
			// create default sort by array all is ASC, if this array is null means first time initiate this table and no sort chosen by user
    		if(sortByArray==null){
    			sortByArray = new ArrayList<>();
    			for (int i=2; i<colDataTypes.size(); i++)sortByArray.add(-1);
    		}
    		
    		String sortBy="";

    		// creates sort by string for DB function input
    		for(int i=0; i<sortByArray.size(); i++){
    			if(sortByArray.get(i)!=-1){
	    			if(sortByArray.get(i)==0)sortBy+=colDataTypes.get(i+2).split(",")[1] +" ASC,";
	    			else sortBy+=colDataTypes.get(i+2).split(",")[1] +" DESC,";
    			}
    		}
    		
    		if(sortBy.length()>0)
    		sortBy=sortBy.substring(0, sortBy.length()-1);

    		// get table from DB
			ArrayList<String> dbTable = appManager.getJdbc().runDBFunction("GET_TABLE_TO_STRING_SORTED",tableaName+","+sortBy);
			if(dbTable.size()==1){
				showErrorMessage("Connection DB error : " + dbTable.get(0) +",please view log file");
				errorAcuire=true;
				return;
			}
			this.numberOfRowsInTable=dbTable.size()-1;
			
			myTableModel model  = new myTableModel();
    		
			// create columns for table
			final String colsFromDB[] = dbTable.get(0).split(",");
			Object columnNames[] = null;
    		if(!isInsertNewRow){
				columnNames = new Object[colsFromDB.length+1];
				columnNames[0]=""; // checkboxesColumn
				for(int i=0; i<colsFromDB.length; i++){
					if(i!=0)columnNames[i+1]=colsFromDB[i]+"("+colDataTypes.get(i+1).split(",")[2]+")";
					else columnNames[i+1]=colsFromDB[i];
				}
    		}else{
    			 columnNames = new Object[colsFromDB.length-1];
 				for(int i=1; i<colsFromDB.length; i++){
 				    columnNames[i-1]=colsFromDB[i]+"("+colDataTypes.get(i+1).split(",")[2]+")";
 				}
    		}
    		
    		
    		model.setColumnIdentifiers(columnNames);

			// create rows for table
    		Object rowData[][]=null;
    		if(startRow==-1)showStartRow=1;
    		if(numberOfRows==-1)showEndRow=dbTable.size();
    		if(!isInsertNewRow){
    			rowData = new Object[(dbTable.size()-1)][colsFromDB.length+1];
				for(int i=showStartRow; i<showEndRow; i++){
					String valuesForRow[] = dbTable.get(i).split(",");
					rowData[(i-1)][0]= new Boolean(false);
					for(int j=0; j<valuesForRow.length; j++){
		                if(valuesForRow[j].contains("null"))valuesForRow[j]="none";
		    			rowData[(i-1)][j+1]=valuesForRow[j];
		    		}
	    			model.addRow(rowData[(i-1)]);
				}
    		}else{
    			rowData = new Object[1][colsFromDB.length-1];
    			rowData[0][0]=dbTable.size();
    			for(int i=1; i<colsFromDB.length-1; i++){
	    			rowData[0][i]="";
    			}
    			model.addRow(rowData[0]);
    		}

    		
    		table = new JTable(rowData,columnNames);
    		table.setRowHeight(30);
    		model.setTableInstance(table);
    		table.setModel(model);

    		// add icons to table header
    		if(!isInsertNewRow){
    			tableHeaders = new ArrayList<JButtonImage>();
    			for(int i=0; i<colDataTypes.size()-2; i++){
	    			
	    			if(sortByArray.get(i)==-1)
	    				tableHeaders.add(new JButtonImage("images/nosorticon.png",(String)columnNames[i+2],10,25));
	    			else if(sortByArray.get(i)==0)
		    			tableHeaders.add(new JButtonImage("images/arrowupicon.png",(String)columnNames[i+2],10,25));
	    			else
		    			tableHeaders.add(new JButtonImage("images/arrowdownicon.png",(String)columnNames[i+2],10,25));

	    			tableHeaders.get(tableHeaders.size()-1).setBorderPainted(true);
					Border headerBorder = UIManager.getBorder("TableHeader.cellBorder");
					tableHeaders.get(tableHeaders.size()-1).setBorder(headerBorder);
				    TableCellRenderer renderer = new JComponentTableCellRenderer();
				    TableColumnModel columnModel = table.getColumnModel();
				    TableColumn column = columnModel.getColumn(i+2);
				    column.setHeaderRenderer(renderer);
				    column.setHeaderValue(tableHeaders.get(tableHeaders.size()-1));
	    		}
    		}

    		// table column 0 used to show check boxes for delete row option in view table mode
    		if(!isInsertNewRow){
    	    TableColumn tc = table.getColumnModel().getColumn(0);
    		tc.setCellEditor(table.getDefaultEditor(Boolean.class));
    	    tc.setCellRenderer(table.getDefaultRenderer(Boolean.class));
    		}
    		
    		
    		// listener for table column for sorting table
    		table.getTableHeader().addMouseListener(new MouseAdapter() {
    			
    			@Override
    			public void mouseClicked(MouseEvent e) {
    				
    				int index = table.convertColumnIndexToModel(table.columnAtPoint(e.getPoint()));
    		        if (index > 0) {
    		        	if(sortByArray.get(index-2)==-1){
    		        	    		        	
    		        	tableHeaders.get(index-2).setImage("images/arrowupicon.png",10,25);
    		        	
    		        	sortByArray.set(index-2, 0);
    		        	mainPanel.remove(tableListPanel);
    		        	initTablePanel(currentViewingTableName, showStartRow, showEndRow);
    		        	mainPanel.revalidate();
    		        	mainPanel.repaint();
    		        	}
    		        	else if(sortByArray.get(index-2)==0){
    		        		tableHeaders.get(index-2).setImage("images/arrowdownicon.png",10,25);
        		        	
    		        		sortByArray.set(index-2, 1);
        		        	mainPanel.remove(tableListPanel);
        		        	initTablePanel(currentViewingTableName, showStartRow, showEndRow);
        		        	mainPanel.revalidate();
        		        	mainPanel.repaint();
    		        	}else if(sortByArray.get(index-2)==1){
    		        		tableHeaders.get(index-2).setImage("images/nosorticon.png",10,25);
        		        	
    		        		sortByArray.set(index-2, -1);
        		        	mainPanel.remove(tableListPanel);
        		        	initTablePanel(currentViewingTableName, showStartRow, showEndRow);
        		        	mainPanel.revalidate();
        		        	mainPanel.repaint();
    		        	}
    		        }
    				super.mouseClicked(e);
    			}
			});
    		
    		// listener for table cells
    		table.addMouseListener(new java.awt.event.MouseAdapter() {
    		    @Override
    		    public void mouseClicked(java.awt.event.MouseEvent evt) {
    		    	
    		    	int col = table.columnAtPoint(evt.getPoint());
    		    	    		    	
    		        if(tableViewMode==Const.TABLE_VIEW_MODE_BROWSE_TABLE){
	    		        if (col==1 || col==2) {
	    		        	showOkMessage("This Column is auto generated","None Editable column");
	    		        }
    		        }else{
    		        	
    		        	if (col==0) {
	    		        	showOkMessage("This Column is auto generated","None Editable column");
	    		        }
    		        }
    		    }
    		});
    		
    		JScrollPane playersListScrollPane = new JScrollPane(table,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    		playersListScrollPane.setPreferredSize(new Dimension((int)(windowWith*0.8f), (int)(ScreenHeight*0.1)));
    		playersListScrollPane.setBorder(BorderFactory.createEmptyBorder());

    		add(playersListScrollPane,BorderLayout.CENTER);
    		
    		
    		if(isInsertNewRow){
				
    			JButtonImage save = new JButtonImage("images/addrowicon.png", "", 35, 35);
				add(save,BorderLayout.EAST);
				
				save.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
		
						String values="";
						for(int i=0; i<colsFromDB.length-1; i++){
							if(colDataTypes.get(i+2).contains("VARCHAR"))
							values+="''"+table.getValueAt(0, i)+"''";
							else values+=table.getValueAt(0, i);
							if(i!=colsFromDB.length-2)values+=",";
						}
						
						//"INSERT_ROW","'LOCATIONS','11,''HAMESILA'',37000,''TEL-AVIV'',''YAYA'',''AR'''"
						String res = appManager.getJdbc().runDBProcedure("INSERT_ROW","'"+tableaName+"','"+ values+"'");
						if(res!=null){
							showErrorMessage("Insert new row error ,please check inputs \n"+res);
						}else{
							showOkMessage("Row added successfully","Insert new row");
							table.setValueAt((int)table.getValueAt(0, 0)+1, 0, 0);
							for(int i=1; i<colsFromDB.length-1; i++)table.setValueAt("", 0, i);
							
							// if current viwing table is not empty, we are in view table mode 
							// need to update view[
							if(pageView==Const.PAGE_VIEW_BROWSE){
								mainPanel.remove(tableListPanel);
								initTablePanel(currentViewingTableName,-1,-1);
								mainPanel.revalidate();
								mainPanel.repaint();
							}
							
							
						}
					}
				});
    		
    		}

    		
    	}
	public int getNumberOfRowsInTable() {
		return numberOfRowsInTable;
	}
    }
	private class TabelByFunctionNamePanel extends JPanel {
    	
		private static final long serialVersionUID = Const.serialVersionUID;
				
    	public TabelByFunctionNamePanel(String functionName ,String inputVriebels) {
    	
    		setLayout(new BorderLayout());
    
    		// get table from DB
			ArrayList<String> dbTable = appManager.getJdbc().runDBFunction(functionName, inputVriebels);
			if(dbTable.size()==1){
				showErrorMessage("Connection DB error : " + dbTable.get(0) +",please view log file");
				errorAcuire=true;
				return;
			}
			
			myTableModel model  = new myTableModel();
    		
			// create columns for table
			final String colsFromDB[] = dbTable.get(0).split(",");
			Object columnNames[] = null;
				columnNames = new Object[colsFromDB.length];
				for(int i=0; i<colsFromDB.length; i++){
					columnNames[i]=colsFromDB[i];
				}
    		
    		
    		
    		model.setColumnIdentifiers(columnNames);

			// create rows for table
    		Object rowData[][]=null;
    		showStartRow=1;
    		showEndRow=dbTable.size();
    			rowData = new Object[showEndRow][colsFromDB.length];
				for(int i=showStartRow; i<showEndRow; i++){
					String valuesForRow[] = dbTable.get(i).split(",");
					for(int j=0; j<valuesForRow.length; j++){
		                if(valuesForRow[j].contains("null"))valuesForRow[j]="none";
		    			rowData[i][j]=valuesForRow[j];
		    		}
	    			model.addRow(rowData[(i)]);
				}
    		

    		
    		table = new JTable(rowData,columnNames);
    		table.setRowHeight(30);
    		model.setTableInstance(table);
    		table.setModel(model);
    		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    		//table.setPreferredScrollableViewportSize(Toolkit.getDefaultToolkit().getScreenSize());
    		JScrollPane playersListScrollPane = new JScrollPane(table,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    		//playersListScrollPane.setPreferredSize(new Dimension((int)(windowWith*0.1f), (int)(ScreenHeight*0.1)));
    		playersListScrollPane.setBorder(BorderFactory.createEmptyBorder());
    		
    		add(playersListScrollPane,BorderLayout.CENTER);
    		
    		
    		

    		
    	}
	
    }
    private class JButtonImage extends JButton{
		private static final long serialVersionUID = Const.serialVersionUID;

		private String text;
		private JLabel background;
    	public JButtonImage(String imagePath,String text,int w,int h) {


    		this.text=text;
    		ImageIcon ButtonBackground = new JImage(imagePath).getImg();
            background = new JLabel(new ImageIcon(ButtonBackground.getImage().getScaledInstance(w, h, java.awt.Image.SCALE_SMOOTH)));
            setContentAreaFilled( false );
            setBorderPainted(false);
            setFocusPainted(false);
            add(background); 
            background.setText(text);
            addMouseListener(new java.awt.event.MouseAdapter() {
        	    public void mouseEntered(java.awt.event.MouseEvent evt) {
        	    	setContentAreaFilled( true );
        	    	setBackground(Color.GREEN);
        	    }

        	    public void mouseExited(java.awt.event.MouseEvent evt) {
        	    	setContentAreaFilled( false );

        	        setBackground(UIManager.getColor("control"));
        	    }
        	});
            
    	
    	}
    	
    	public void setImage(String imagePath,int w,int h){
    		ImageIcon ButtonBackground = new JImage(imagePath).getImg();
    		background.setIcon(new ImageIcon(ButtonBackground.getImage().getScaledInstance(w, h, java.awt.Image.SCALE_SMOOTH)));
    	}
    	
    	public String getBTText(){
    		return text;
    	}
    	
    }
    private class TableRowPanel extends JPanel{
		private static final long serialVersionUID = Const.serialVersionUID;

        private String tableName;
    	public TableRowPanel(final String tableName) {
    		this.tableName=tableName;
    		
    		setBorder(BorderFactory.createLineBorder(Color.black));
            SpringLayout rowLayOut = new SpringLayout();
            setLayout(rowLayOut);
    		int rowHeight = (int)(mainPanelHeight*0.05);
    		int rowWith=(int)(mainPanelWith*0.95);
    		setPreferredSize(new Dimension(rowWith, rowHeight));
    		
    		JLabel tableNameLabel = new JLabel(this.tableName,SwingConstants.CENTER);
    		tableNameLabel.setOpaque(true);
    		tableNameLabel.setPreferredSize(new Dimension((int)(rowWith*0.4),(int)(rowHeight*0.9)));
    		add(tableNameLabel);
    		
    		rowLayOut.putConstraint(SpringLayout.NORTH, tableNameLabel, 0, SpringLayout.NORTH, this);
    		rowLayOut.putConstraint(SpringLayout.WEST, tableNameLabel, 0, SpringLayout.WEST, this);

    		
    		JButtonImage browseBT= new JButtonImage("images/browseicon.png","Browse",(int)(rowHeight*0.8),(int)(rowHeight*0.8));
    		JPanel browsePanel = new JPanel();
    		browsePanel.setLayout(new BorderLayout());
    		browsePanel.setPreferredSize(new Dimension((int)(rowWith*0.2),(int)(rowHeight*0.9)));
    		browsePanel.add(browseBT,BorderLayout.CENTER);
    		add(browsePanel);
    		
    		rowLayOut.putConstraint(SpringLayout.NORTH, browsePanel, 0, SpringLayout.NORTH, this);
    		rowLayOut.putConstraint(SpringLayout.WEST, browsePanel, 0, SpringLayout.EAST, tableNameLabel);

    		browseBT.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					mainPanel.remove(homePanel);
					initTablePanel(tableName,-1,-1);
					initTableControlPanel();
					mainPanel.revalidate();
					mainPanel.repaint();
					
					currentViewingTableName=tableName;
					
					pageView=Const.PAGE_VIEW_BROWSE;
				}
			});

    		JButtonImage insertBT= new JButtonImage("images/inserticon.png","Insert",(int)(rowHeight*0.8),(int)(rowHeight*0.8));
    		JPanel insertPanel = new JPanel();
    		insertPanel.setLayout(new BorderLayout());
    		insertPanel.setPreferredSize(new Dimension((int)(rowWith*0.2),(int)(rowHeight*0.9)));
    		insertPanel.add(insertBT,BorderLayout.CENTER);
    		add(insertPanel);
    		
    		
    		insertBT.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {

					insertRowPopUp row = new insertRowPopUp(tableName);
					if(!errorAcuire){
						mainWindowFrame.setEnabled(false);
						row.setVisible(true);
						tableViewMode=Const.TABLE_VIEW_MODE_INSERT_ROW;
					}
					
					row.addWindowListener(new WindowAdapter() {
						@Override
						public void windowClosing(WindowEvent e) {
							mainWindowFrame.setEnabled(true);
							tableViewMode=Const.TABLE_VIEW_MODE_BROWSE_TABLE;
							super.windowClosing(e);
						}
					});
					
					
				}
			});
    		
    		rowLayOut.putConstraint(SpringLayout.NORTH, insertPanel, 0, SpringLayout.NORTH, this);
    		rowLayOut.putConstraint(SpringLayout.WEST, insertPanel, 0, SpringLayout.EAST, browsePanel);

    		JButtonImage dropBT= new JButtonImage("images/dropicon.png","Drop",(int)(rowHeight*0.8),(int)(rowHeight*0.8));
    		JPanel dropPanel = new JPanel();
    		dropPanel.setLayout(new BorderLayout());
    		dropPanel.setPreferredSize(new Dimension((int)(rowWith*0.2),(int)(rowHeight*0.9)));
    		dropPanel.add(dropBT,BorderLayout.CENTER);
    		add(dropPanel);
    		
    		dropBT.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {

					int res  =showChoseMessage("Drop CASCADE table will cause all CONSTRAINTS forgein keys assosiated with this table to be lost,are you sure ? ","Drop table");
					if(res==JOptionPane.OK_OPTION){
						String ress = appManager.getJdbc().runDBProcedure("DROP_TABLE","'"+tableName+"'");
						if(ress==null){
						showOkMessage("Table " +tableName+" dropped", "Drop table");
						mainPanel.remove(homePanel);
						initHomePanel();
						mainPanel.revalidate();
						mainPanel.repaint();
						}
						else{
							showErrorMessage("Drop table error : " + ress);
						}	
					}
				}
			});

    		rowLayOut.putConstraint(SpringLayout.NORTH, dropPanel, 0, SpringLayout.NORTH, this);
    		rowLayOut.putConstraint(SpringLayout.WEST, dropPanel, 0, SpringLayout.EAST, insertPanel);

    		
    	
    	}
    	
    	
    }   
    private class insertRowPopUp extends JFrame{
		private static final long serialVersionUID = Const.serialVersionUID;

    	public insertRowPopUp(String tableName) {
    		
    		setTitle("Insert new row into " + tableName);
    		mainWindowFrame.setLayout(new BorderLayout());
    		setSize(windowWith,(int)(windowHeight*0.15));
    		setLocation((int)(screenWith/2-screenWith*0.6/2), (int)(ScreenHeight*0.4));
    		setResizable(false);
	        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    		ImageIcon img = new ImageIcon(this.getClass().getResource("images/appicon.png"));
    		setIconImage(img.getImage());
    		
    		TableListPanel row = new TableListPanel(tableName,true,-1,-1);
    		setContentPane(row);
    	
    	}
    	
    	
    	
    }
    private class myTableModel extends DefaultTableModel{
		private static final long serialVersionUID = Const.serialVersionUID;

    	JTable t;
    	
    	@Override
    	public boolean isCellEditable(int row, int column) {
    		if(tableViewMode==Const.TABLE_VIEW_MODE_INSERT_ROW){
    		
				if(column==0){
					t.setRowSelectionAllowed(false);
		    		t.setCellSelectionEnabled(false);
		        	return false;
		        }
		        else {
		        	t.setRowSelectionAllowed(true);
		    		t.setCellSelectionEnabled(true);
		        	return true;
		        }
    		}else{
    			if(column==1 || column==2){
					t.setRowSelectionAllowed(false);
		    		t.setCellSelectionEnabled(false);
		        	return false;
		        }
		        else {
		        	t.setRowSelectionAllowed(true);
		    		t.setCellSelectionEnabled(true);
		        	return true;
		        }
    			
    		}
    	}
    	
    	
    	public void setTableInstance(JTable t) {
			this.t = t;
		}
    	
    }
    private class QueryPanel extends JPanel{
		private static final long serialVersionUID = 1L;
		private JPanel resultPanel;
    	
    	public QueryPanel() {

    		int panelHeight = (int)(mainPanelHeight*0.9f);
    		int panelWith=(int)(mainPanelWith);
    		setPreferredSize(new Dimension(panelWith, panelHeight));
    		setLayout(new BorderLayout());
    		
    		JPanel QueryTextPanel = new JPanel();

    		Font font = new Font("Verdana", Font.PLAIN, 16);
    		
    		final JTextArea inputQueryTF = new JTextArea();
    		inputQueryTF.setFont(font);
    		inputQueryTF.setText("select * from \nusers inner join players using(user_id)");
    		inputQueryTF.setBorder(BorderFactory.createLineBorder(Color.black));
    		inputQueryTF.setPreferredSize(new Dimension((int) (panelWith*0.9f), (int) (panelHeight*0.4f)));
    		QueryTextPanel.add(inputQueryTF);
    		
    		JButtonImage executeQBT = new JButtonImage("images/exeicon.png", null, 30, 30);
    		QueryTextPanel.add(executeQBT);
    		
    		executeQBT.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					String query = inputQueryTF.getText();
					query = query.replace(";", "");
					if(query.isEmpty())showErrorMessage("Please insert query");
					else{
					if(showTableByFunctionInfoPanel!=null){
						resultPanel.remove(showTableByFunctionInfoPanel);
					}
					showTableByFunctionInfoPanel = new TabelByFunctionNamePanel("GET_TABLE_TO_STRING", query);
					resultPanel.add(showTableByFunctionInfoPanel,BorderLayout.CENTER);
					resultPanel.revalidate();
					resultPanel.repaint();
					}
					
				}
			});

    		add(QueryTextPanel,BorderLayout.NORTH);
    		
    		resultPanel = new JPanel();
    		resultPanel.setLayout(new BorderLayout());
    		//resultPanel.setPreferredSize(new Dimension((int) (panelWith*0.9f), (int) (panelHeight*0.6f)));
    		add(resultPanel,BorderLayout.CENTER);

    		
    	
    	}
    	
    }
    class JComponentTableCellRenderer implements TableCellRenderer {
    	  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
    	      boolean hasFocus, int row, int column) {
    	    return (JComponent) value;
    	  }
    	}
    
}
