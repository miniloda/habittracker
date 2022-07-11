package com.jacob.learn;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;

import java.sql.*;
import java.io.FileWriter;
import javax.swing.JToggleButton;
import java.awt.SystemColor;
public class Main extends JFrame implements ActionListener {
	private JTextField titleField;
	Connection con;
	PreparedStatement pst;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					Main frame = new Main();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * @throws SQLException 
	 */
	public Main() throws SQLException {
		dayState = "";
		writeDBFile();
		connect();
		System.out.println(firstinit);
		if(firstinit == true) {
			createTables();
			initDBRecords();
		}
		
		checkDay();
		checkGap();
		createGUI();
		init();
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent e) {

				if (elapsedTime != 0) {
					stop();
				}
			}
		});

	}
	File db;
	boolean firstinit = false;
	public String generatePath ()  throws Exception{
		return new File(Main.class.getProtectionDomain().getCodeSource().getLocation()
			    .toURI()).getPath();
	}
	String path = "~";
	/**
	 * Creates the sqlite.db file
	 */
	public void writeDBFile() {
		
		
		try {
			path = generatePath();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		File theDir = new File(path+"/source");
		if (!theDir.exists()){
		    theDir.mkdirs();
		}
		 db = new File(path+"/source/historytracker.db");
		 if(!checkDBExists()) {
			try {
				db.createNewFile();
				firstinit = true;

				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	private JComboBox themeBox;
	/**
	 * Sets the theme
	 */
	public void setTheme(String theme) {
		if(theme.equals("Dark")) {
			typeLabel.setForeground(Color.WHITE);
			platformLabel.setForeground(Color.WHITE);
			subjectLabel.setForeground(Color.WHITE);
			titleLabel.setForeground(Color.WHITE);
			languageLabel.setForeground(Color.WHITE);
			upSkillLabel.setForeground(Color.WHITE);
			getContentPane().setBackground(Color.DARK_GRAY);
		}else {
			typeLabel.setForeground(Color.BLACK);
			platformLabel.setForeground(Color.BLACK);
			subjectLabel.setForeground(Color.BLACK);
			titleLabel.setForeground(Color.BLACK);
			languageLabel.setForeground(Color.BLACK);
			upSkillLabel.setForeground(Color.BLACK);
			getContentPane().setBackground(Color.WHITE);
		}
		
	}
	/**
	 * Adds the none choices to the tables
	 * @throws SQLException 
	 * 
	 */
	public void initDBRecords() throws SQLException {
		
			sql = "INSERT INTO languages (language)VALUES('NONE')";
			pst =  con.prepareStatement(sql);  
			pst.execute();
			sql = "INSERT INTO platforms (platform)VALUES('NONE')";
			pst =  con.prepareStatement(sql);  
			pst.execute();
			sql = "INSERT INTO types (type)VALUES('NONE')";
			pst =  con.prepareStatement(sql);  
			pst.execute();
			sql = "INSERT INTO subjects (subject)VALUES('NONE')";
			pst =  con.prepareStatement(sql);  
			pst.execute();
		
	}
	public boolean checkDBExists() {
		if(db.exists()) {
			return true;
		}
		else {
			return false;
		}
		
	}
	
	/**
	 * connect to mySQL database
	 */
	private void connect() {

		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		}
		try {
			con = (Connection) DriverManager.getConnection("jdbc:sqlite:historytracker.db");
			
		} catch (SQLException e) {

			e.printStackTrace();
		}

	}
	String sql; 
	public void createTables() throws SQLException {
		 sql = "CREATE TABLE IF NOT EXISTS history ('day_number'	INTEGER, 'day_state' TEXT"
					+",'date_performed'	TEXT, "
			        + "'type'	INTEGER,"
					+"'platform'	INTEGER, "
					+"'subject'	INTEGER,     "
					+"'title'	TEXT,        "
					+"'language'	INTEGER, "
					+"'time_elapsed'	TEXT,"
					+"'pause_duration'	TEXT,"
					+"'rest_duration'	TEXT,"
					+"'session_end'	TEXT,    "
					+"'pause_reason'	TEXT )";
				
		pst =  con.prepareStatement(sql);  
		pst.execute();
		
		sql = "CREATE TABLE IF NOT EXISTS types ('typeid' INTEGER PRIMARY KEY AUTOINCREMENT, 'type' TEXT , 'state' TEXT DEFAULT 'ADDED' )";
		pst =  con.prepareStatement(sql);  
		pst.execute();
		sql = "CREATE TABLE IF NOT EXISTS platforms ('platformid' INTEGER PRIMARY KEY AUTOINCREMENT, 'platform' TEXT,'state' TEXT DEFAULT 'ADDED' )";
		pst =  con.prepareStatement(sql);  
		pst.execute();
		sql = "CREATE TABLE IF NOT EXISTS subjects ('subjectid' INTEGER PRIMARY KEY AUTOINCREMENT, 'subject' TEXT, 'state' TEXT DEFAULT 'ADDED' )";
		pst =  con.prepareStatement(sql);  
		pst.execute();
		sql = "CREATE TABLE IF NOT EXISTS languages ('languageid' INTEGER PRIMARY KEY AUTOINCREMENT, 'language' TEXT, 'state' TEXT DEFAULT 'ADDED' )";
		pst =  con.prepareStatement(sql);  
		pst.execute();
		

	}
	
/**
 * Adds the contents of the checkboxes
 * @throws SQLException
 */
	@SuppressWarnings("unchecked")
	public void addCheckBoxItems() throws SQLException {
		typeBox.removeAllItems();
		subjectBox.removeAllItems();
		languageBox.removeAllItems();
		platformBox.removeAllItems();
		typeBox.removeAllItems();
		typeMap = new HashMap<Integer, String>();    
		platformMap = new HashMap<Integer, String>();
		subjectMap = new HashMap<Integer, String>(); 
		languageMap = new HashMap<Integer, String>();                                                           
		sql = "SELECT typeid,type FROM 'types'";
		rs = con.createStatement().executeQuery(sql);
		while (rs.next()) {
		typeMap.put(rs.getInt("typeid"), rs.getString("type"));
		typeBox.addItem(rs.getString("type"));
		}
		sql = "SELECT platformid,platform FROM 'platforms'";
		rs = con.createStatement().executeQuery(sql);
		while (rs.next()) {
		platformMap.put(rs.getInt("platformid"), rs.getString("platform"));
		platformBox.addItem(rs.getString("platform"));
		}
		sql = "SELECT languageid,language FROM 'languages'";
		rs = con.createStatement().executeQuery(sql);
		while (rs.next()) {
		languageMap.put(rs.getInt("languageid"), rs.getString("language"));
		languageBox.addItem(rs.getString("language"));
		}
		sql = "SELECT subjectid,subject FROM 'subjects'";
		rs = con.createStatement().executeQuery(sql);
		while (rs.next()) {
		subjectMap.put(rs.getInt("subjectid"), rs.getString("subject"));
		subjectBox.addItem(rs.getString("subject"));
		}
		
			
		
		
	}
	/**
	 * Creates the graphic interface
	 */
	public void createGUI() {
		
		getContentPane().setBackground(Color.DARK_GRAY);
		
		upSkillLabel = new JLabel("UpSkill!");
		upSkillLabel.setBounds(153, 12, 143, 34);
		upSkillLabel.setForeground(Color.WHITE);
		upSkillLabel.setFont(new Font("Papyrus", Font.BOLD, 29));

		platformLabel = new JLabel("Platform");
		platformLabel.setBounds(56, 132, 72, 15);
		platformLabel.setForeground(Color.WHITE);

		platformBox = new JComboBox();
		platformBox.setBounds(138, 129, 158, 20);
		platformBox.setBackground(Color.GRAY);

		titleLabel = new JLabel("Title");
		titleLabel.setBounds(56, 221, 72, 15);
		titleLabel.setForeground(Color.WHITE);

		titleField = new JTextField();
		titleField.setBounds(138, 218, 158, 19);
		titleField.setBackground(Color.GRAY);
		titleField.setColumns(10);

		
		subjectLabel = new JLabel("Subject");
		subjectLabel.setBounds(56, 163, 66, 15);
		subjectLabel.setForeground(Color.WHITE);

		subjectBox = new JComboBox();
		subjectBox.setBounds(139, 160, 158, 20);
		subjectBox.setBackground(Color.GRAY);
		

		languageBox = new JComboBox();
		languageBox.setBounds(138, 190, 158, 20);
		languageBox.setBackground(Color.GRAY);

		typeBox = new JComboBox();
		typeBox.setBounds(138, 98, 158, 20);

		typeBox.setBackground(Color.GRAY);
		typeBox.addActionListener(this);
		languageLabel = new JLabel("Language");
		languageLabel.setBounds(56, 193, 86, 15);
		languageLabel.setForeground(Color.WHITE);
		
		typeLabel = new JLabel("Type");
		typeLabel.setBounds(56, 101, 72, 15);
		typeLabel.setForeground(Color.WHITE);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 440, 537);

		startDayButton = new JButton("Start Day");
		startDayButton.setBounds(89, 58, 113, 25);
		startDayButton.setEnabled(true);

		endDayButton = new JButton("End Day");
		endDayButton.setBounds(231, 58, 111, 25);
		endDayButton.setEnabled(true);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(0, 0, 3, 3);

		scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(0, 0, 3, 3);
		getContentPane().setLayout(null);
		getContentPane().add(scrollPane);
		getContentPane().add(scrollPane_1);
		getContentPane().add(upSkillLabel);
		getContentPane().add(startDayButton);
		getContentPane().add(endDayButton);
		getContentPane().add(typeLabel);
		getContentPane().add(typeBox);
		getContentPane().add(platformLabel);
		getContentPane().add(platformBox);
		getContentPane().add(subjectLabel);
		getContentPane().add(subjectBox);
		getContentPane().add(languageLabel);
		getContentPane().add(languageBox);
		getContentPane().add(titleLabel);
		getContentPane().add(titleField);
		
		addTypeButton = new JButton("Add");
		addTypeButton.setBounds(305, 96, 80, 25);
		getContentPane().add(addTypeButton);
		
				timerPanel = new JPanel();
				timerPanel.setBounds(89, 248, 262, 222);
				getContentPane().add(timerPanel);
				timerPanel.setBackground(Color.LIGHT_GRAY);
				timerPanel.setLayout(null);
				
						startButton = new JButton("Start");
						startButton.setBounds(42, 138, 89, 23);
						timerPanel.add(startButton);
						startButton.addActionListener(this);
						timeLabel = new JLabel("");
						timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
						timeLabel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
						timeLabel.setFont(new Font("Verdana", Font.PLAIN, 35));
						timeLabel.setBounds(42, 32, 184, 91);
						timerPanel.add(timeLabel);
						stopButton = new JButton("Stop");
						stopButton.setBounds(137, 138, 89, 23);
						timerPanel.add(stopButton);
						
								pauseButton = new JButton("Pause");
								pauseButton.setBounds(42, 173, 89, 23);
								timerPanel.add(pauseButton);
								
										restButton = new JButton("Rest");
										restButton.setBounds(137, 173, 89, 23);
										
												timerPanel.add(restButton);
												
												addPlatformButton = new JButton("Add");
												addPlatformButton.setBounds(305, 127, 80, 25);
												getContentPane().add(addPlatformButton);
												
												addSubjectButton = new JButton("Add");
												addSubjectButton.setBounds(305, 158, 80, 25);
												getContentPane().add(addSubjectButton);
												
												addLanguageButton = new JButton("Add");
												addLanguageButton.setBounds(305, 188, 80, 25);
												getContentPane().add(addLanguageButton);
												
												JLabel themeLabel = new JLabel("Theme");
												themeLabel.setForeground(SystemColor.text);
												themeLabel.setBounds(294, 12, 70, 15);
												getContentPane().add(themeLabel);
												
												themeBox = new JComboBox();
												themeBox.setBounds(348, 7, 80, 24);
												getContentPane().add(themeBox);
		restButton.addActionListener(this);
		pauseButton.addActionListener(this);
		stopButton.addActionListener(this);
		startDayButton.addActionListener(this);
		endDayButton.addActionListener(this);
		addTypeButton.addActionListener(this);
		addPlatformButton.addActionListener(this);
		addSubjectButton.addActionListener(this);
		addLanguageButton.addActionListener(this);
		setLocationRelativeTo(null);
		if (dayState.equals("STARTED") || dayState.equals("IN PROGRESS")) {
			startDayButton.setEnabled(false);
			endDayButton.setEnabled(true);
			startButton.setEnabled(true);

		} else {
			startButton.setEnabled(false);
			endDayButton.setEnabled(false);
			startDayButton.setEnabled(true);
		}
		try {
			addCheckBoxItems();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		themeBox.addItem("Dark");
		themeBox.addItem("Light");
		themeBox.setSelectedItem("Dark");
		String defaultTheme = (String) themeBox.getSelectedItem();
		setTheme(defaultTheme);
		themeBox.addActionListener(this);
	}
	/**
	 * Initializes the data that needs to be initialized in every session
	 */
	public void init() {

		pauseTimer.stop();
		restTimer.stop();

		restButton.setEnabled(false);
		pauseButton.setEnabled(false);
		stopButton.setEnabled(false);
		time = 0;
		elapsedTime = 0;
		seconds = 0;
		minutes = 0;
		hours = 0;
		rest = 0;
		restDuration = 0;
		restTime = 0;
		restMinute = 0;
		restSecond = 0;
		timesUpClose = 0;
		seconds_string = String.format("%02d", seconds);
		minutes_string = String.format("%02d", minutes);
		hours_string = String.format("%02d", hours);
		timeLabel.setText(hours_string + ":" + minutes_string + ":" + seconds_string);
		pauseDuration = 0;
	}

	JPanel timerPanel;

	/**
	 * The logic for rest, creates the JFrame and then adds event listeners
	 */
	void rest() {
		timer.stop();
		restLabel = new JLabel();
		restFrame = new JFrame();
		restLabel.setText("00" + ":0" + restMinute + ":0" + restSecond);
		restLabel.setBounds(100, 100, 200, 100);
		restLabel.setFont(new Font("Verdana", Font.PLAIN, 35));
		restLabel.setBorder(BorderFactory.createBevelBorder(1));
		restLabel.setOpaque(true);
		restLabel.setHorizontalAlignment(JTextField.CENTER);
		restFrame.getContentPane().add(restLabel);
		restFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		restFrame.setSize(400, 400);
		restFrame.getContentPane().setLayout(null);
		restFrame.setVisible(true);
		restFrame.setLocationRelativeTo(null);
		restTimer.start();
		restFrame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent e) {
				timer.start();
				restTimer.stop();
				restMinute = 0;
				restSecond = 0;
			}
		});

	}

	/**
	 * Changes the state of the buttons and then starts the Timer timer object
	 */
	void start() {
		startButton.setEnabled(false);
		if (typeBox.getSelectedItem().equals("Leisure")) {
			restButton.setEnabled(false);
			pauseButton.setEnabled(false);
		} else {
			restButton.setEnabled(true);
			pauseButton.setEnabled(true);
		}

		stopButton.setEnabled(true);

		timer.start();

	}

	void clearFields() {
		platformBox.setSelectedItem("None");
		subjectBox.setSelectedItem("None");
		languageBox.setSelectedItem("None");
		titleField.setText("");
		typeBox.setSelectedItem("None");
		pauseReason = new StringBuilder();
	}

	void pause() {
		String temp;
		timer.stop();
		pauseTimer.start();
		temp = JOptionPane.showInputDialog("For what reason?");
		pauseReason.append(temp);

		startButton.setEnabled(true);

	}

	StringBuilder pauseReason = new StringBuilder();

	void stop() {
		startButton.setEnabled(true);
		sessionEnd = "User";
		getDetails();
		sendToDB();
		checkDay();
		init();

	}

	String pauseDur;
	String restDur;
	String timeDur;
	String type;
	String dayState;
	int dayNumber;
	ResultSet rs;

	public void checkDay() {
		dayState = "";
		
		try {
			sql = "SELECT day_state, day_number, MAX(date_performed) FROM 'history' ";
			rs = con.createStatement().executeQuery(sql);
			try {
				rs.next();
				dayState = rs.getString("day_state");
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			
			
			try {

				dayNumber = Integer.parseInt(rs.getString("day_number"));

			} catch (Exception ex) {
				ex.printStackTrace();
				dayNumber = 0;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
			dayNumber = 0;
		}

	}
/**
 * Gets the details of every field
 */
	void getDetails() {
		type = (String) typeBox.getSelectedItem();
		platform = (String) platformBox.getSelectedItem();
		subject = (String) subjectBox.getSelectedItem();
		language = (String) languageBox.getSelectedItem();
		if (titleField.getText().equals("")) {
			title = "None";
		} else {
			title = titleField.getText();
		}

		timer.stop();
		timeLabel.setText(hours_string + ":" + minutes_string + ":" + seconds_string);
		pauseDur = String.format("%02d", (int) ((pauseDuration / 3600000) % 60)) + ":"
				+ String.format("%02d", (int) ((pauseDuration / 60000) % 60)) + ":"
				+ String.format("%02d", (int) ((pauseDuration / 1000) % 60));
		restDur = String.format("%02d", (restDuration / 3600000) % 60) + ":"
				+ String.format("%02d", (restDuration / 60000) % 60) + ":"
				+ String.format("%02d", (restDuration / 1000) % 60);
		timeDur = String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":"
				+ String.format("%02d", seconds);
		if (dayState.equals("STARTED") || dayState.equals("IN PROGRESS")) {
			dayStatePass = "IN PROGRESS";
		} else {
			dayStatePass = "STARTED";
		}
	}

	/**
	 * Sends the session to the database
	 */
	public void sendToDB() {
		
		for (Entry<Integer, String> entry: typeMap.entrySet()) {
			
			if (entry.getValue().equals(type)) {
				typeid = entry.getKey();
            }
        
		}
		for (Entry<Integer, String> entry: platformMap.entrySet()) {
			
			if (entry.getValue().equals(platform)) {
				platformid = entry.getKey();

     
            }
        
		}
		for (Entry<Integer, String> entry: languageMap.entrySet()) {
			
			if (entry.getValue().equals(language)) {
				languageid = entry.getKey();
				
            }
        
		}
		for (Entry<Integer, String> entry: subjectMap.entrySet()) {
			System.out.println(entry.getValue());
			if (entry.getValue().equals(subject)) {
				subjectid = entry.getKey();
	               
                
            }
        
		}
	
	
		try {
			//Send the session to history
			pst = con.prepareStatement(
					"insert into history(day_number, `day_state`, `date_performed`, `type`, `platform`, `subject`, `title`, `language`, `time_elapsed`, `pause_duration`, `rest_duration`, `session_end`, `pause_reason`)"
							+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)");
			pst.setInt(1, dayNumber);
			pst.setString(2, dayStatePass);
			pst.setString(3, date);
			pst.setInt(4, typeid);
			pst.setInt(5, platformid);
			pst.setInt(6, subjectid);
			pst.setString(7, title);
			pst.setInt(8, languageid);
			pst.setString(9, timeDur);
			pst.setString(10, pauseDur);
			pst.setString(11, restDur);
			pst.setString(12, sessionEnd);
			pst.setString(13, pauseReason.toString());

			pst.executeUpdate();
			JOptionPane.showMessageDialog(null,"Session ended and recorded successfully.");
		} catch (SQLException ex) {

			File error = new File(path+"/source/error.log");
			if(!error.exists()) {
				try {
					error.createNewFile();

					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			FileWriter logger;
			try {
				logger = new FileWriter(error,true);
				logger.write("Error occured. SQL Statement is as follows:\n----START OF SQL STATEMENT----\n"
						+ "\"insert into history(day_number, `day_state`, `date_performed`, "
						+ "`type`, `platform`, `subject`, `title`, `language`, `time_elapsed`, "
						+ "`pause_duration`, `rest_duration`, `session_end`, `pause_reason`)"
						+ String.format("VALUES(%d,%s,%s,%d,%d,%d,%s,%d,%s,%s,%s,%s,%s)",dayNumber, dayStatePass, date,typeid,platformid,subjectid,title,languageid, timeDur,
						pauseDur,restDur,sessionEnd, pauseReason) + "\n----END OF SQL STATEMENT----\n\n");
				logger.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			String path = error.getAbsolutePath();
			JOptionPane.showMessageDialog(null,"An erorr occured. Please refer to the log file in "+ path);
			
			

			
			
		}
		
		clearFields();
	}

	public String getDate() {
		currentDate = new Date();
		timeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		return timeFormat.format(currentDate);
	}

	public void endDay() throws SQLException {
		pst = con.prepareStatement("SELECT `day_state` FROM history ORDER BY date_performed DESC limit 1");
		rs = pst.executeQuery();
		rs.next();
		dayState = rs.getString("day_state");
		try {

			if (rs.getString("day_state") != "SKIPPED") {
				pst = con.prepareStatement("UPDATE history SET `day_state` = 'ENDED' WHERE `date_performed` = "
						+ "(SELECT MAX(`date_performed`) from history)");
				pst.executeUpdate();
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
	float total;
	/**
	 * When the End day button is clicked, this will calculate the total time for the given day
	 */
	public float calculateDay() {
		try {
			pst = con.prepareStatement("SELECT time_elapsed FROM history WHERE day_number = " + dayNumber);
			rs = pst.executeQuery();
			total = 0;
			while(rs.next()) {
				Timestamp test = new Timestamp(0);
				System.out.println(test);
				Timestamp totalDay = Timestamp.valueOf("1970-01-01 "+ rs.getString("time_elapsed"));
				total += (totalDay.getTime()+(8*60*60*1000));
				
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return total/(3600*1000);//returns the hours
	}

	public void checkGap() {
		try {

			pst = con.prepareStatement("SELECT MAX(`date_performed`) as date FROM `history`");
			rs = pst.executeQuery();
			
			Timestamp lastDate = Timestamp.valueOf(rs.getString("date"));
			System.out.println(lastDate);
			Date dateNow = new Date();
			long lastDateTS = lastDate.getTime();
			long dateNowTS = dateNow.getTime();

			double difference = (double) ((dateNowTS / 1) - lastDateTS) / (60000 * 60);
			int tempDay = 24;
			if (difference > 48) {
				dayNumber += 1;
			}
			while (difference > 48) {

				SimpleDateFormat tempformat = new SimpleDateFormat("yyyy-MM-dd");
				pst = con.prepareStatement(
						"INSERT INTO history(day_number,date_performed, day_state) VALUES (?,?,?)");
				pst.setInt(1, dayNumber);
				pst.setString(3, "SKIPPED");
				pst.setString(2, tempformat.format(lastDateTS + tempDay * 1000 * 60 * 60));
				System.out.println(difference);
				pst.executeUpdate();
				dayNumber += 1;
				difference -= 24;
				tempDay += 24;
				dayState = "SKIPPED";
				break;

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addChoice(String column, String choice) {
		try {
			sql = "Insert into '"+ column + "s'(" +column+")VALUES(?)" ;
			System.out.println(sql);
			
			pst = con.prepareStatement(sql);
			pst.setString(1, choice);
			pst.execute();
			JOptionPane.showMessageDialog(null,"Successfully added!");
		}catch(SQLException e) {
			JOptionPane.showMessageDialog(null,"Failed");
			e.printStackTrace();
		}
		
	}
	public boolean checkChoice(String column, String choice){
		try {
			sql = "SELECT "+column+" FROM "+column+"s WHERE LOWER("+column+") = LOWER('" + choice + "')";
			System.out.println(sql);
			pst = con.prepareStatement(sql);
			rs = pst.executeQuery();
			if(rs.isBeforeFirst()) {
				JOptionPane.showMessageDialog(null,"Duplicate entry");
				return false;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
	String input;
	@SuppressWarnings("unchecked")
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		if (e.getSource() == addTypeButton) {
			
			while (true) {
			input = JOptionPane.showInputDialog(null, "Input the type");
			if(input ==  null) {
				break;
			}
			else if (input.isEmpty()) {
				
				JOptionPane.showMessageDialog(null,"No input given");
			}
			else{
				if(checkChoice("Type", input)) {
				addChoice("Type", input);
			
				}
			}
			
		}
			try {
				addCheckBoxItems();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
	}
		if (e.getSource() == addPlatformButton) {
			
			while (true) {
			input = JOptionPane.showInputDialog(null, "Input the platform");
			if(input ==  null) {
				break;
			}
			else if (input.isEmpty()) {
				
				JOptionPane.showMessageDialog(null,"No input given");
			}
			else{
				if(checkChoice("Platform", input)) {
				addChoice("Platform", input);
			
				}
			}
		}
			try {
				addCheckBoxItems();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	}
		if (e.getSource() == addSubjectButton) {
		
			while (true) {
			input = JOptionPane.showInputDialog(null, "Input the Subject");
			if(input ==  null) {
				break;
			}
			else if (input.isEmpty()) {
				
				JOptionPane.showMessageDialog(null,"No input given");
			}
			else{
				if(checkChoice("Subject", input)) {
				addChoice("Subject", input);
			
				}
			}
		}
			try {
				addCheckBoxItems();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	}
		if (e.getSource() == themeBox) {
			System.out.println((String)themeBox.getSelectedItem());
			setTheme((String)themeBox.getSelectedItem());
		}
		if (e.getSource() == addLanguageButton) {
			
			while (true) {
			input = JOptionPane.showInputDialog(null, "Input the language");
			if(input ==  null) {
				break;
			}
			else if (input.isEmpty()) {
				
				JOptionPane.showMessageDialog(null,"No input given");
			}
			else{
				if(checkChoice("Language", input)) {
				addChoice("Language", input);
			
				}
			}
		}
			try {
				addCheckBoxItems();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	}

		if (e.getSource() == startButton) {
			endDayButton.setEnabled(false);
			 DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");
			LocalDateTime dateLocal = LocalDateTime.now();
			date = dtf.format(dateLocal);
			
			pauseTimer.stop();
			start();
		}
		if (e.getSource() == stopButton) {
			endDayButton.setEnabled(true);
			stop();
		}
		if (e.getSource() == pauseButton) {
			pause();
		}
		if (e.getSource() == restButton) {
			rest();
		}
		if (e.getSource() == endDayButton) {
			
			try {
				endDay();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			JOptionPane.showMessageDialog(null, "Great Job today, self! Total time is: " + calculateDay() + " hours");
			System.exit(0);
		}
		if (e.getSource() == startDayButton) {
			dayNumber += 1;
			startButton.setEnabled(true);
			endDayButton.setEnabled(false);
			startDayButton.setEnabled(false);

		}
		
		

	}
	
	Timer timer = new Timer(1000, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			restSecString = String.format("%02d", restSecond); 
			restMinString = String.format("%02d", restMinute); 
			restHourString = String.format("%02d", restHour);  
			seconds_string = String.format("%02d", seconds);   
			minutes_string = String.format("%02d", minutes);   
			hours_string = String.format("%02d", hours);       
			elapsedTime = elapsedTime + 1000;
			hours = (elapsedTime / 3600000);
			minutes = (elapsedTime / 60000) % 60;
			seconds = (elapsedTime / 1000) % 60;
			seconds_string = String.format("%02d", seconds);
			minutes_string = String.format("%02d", minutes);
			hours_string = String.format("%02d", hours);
			timeLabel.setText(hours_string + ":" + minutes_string + ":" + seconds_string);
			time = (double) elapsedTime / 60000;
			restTime += (0.2 * 1000);

		}
	});

	Timer restTimer = new Timer(1000, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			restDuration += 1000;
			restTime = restTime - 1000;
			restMinute = (restTime / 60000) % 60;
			restSecond = (restTime / 1000) % 60;
			restHour = (restTime / 3600000);
			restHourString = String.format("%02d", restHour);
			restSecString = String.format("%02d", restSecond);
			restMinString = String.format("%02d", restMinute);
			restLabel.setText(restHourString + ":" + restMinString + ":" + restSecString);

			if (restTime <= 0) {
				URL url = getClass().getResource("Ring.wav");
				File file = new File(url.getPath());
				AudioInputStream audioStream;
				try {
					audioStream = AudioSystem.getAudioInputStream(file);
					clip = AudioSystem.getClip();
					clip.open(audioStream);
				} catch (UnsupportedAudioFileException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (LineUnavailableException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				timesUp.start();
				clip.loop(Clip.LOOP_CONTINUOUSLY);
				int response = 1;

				Object[] options = { "OK" };
				response = JOptionPane.showOptionDialog(restFrame, "No more rest time remaining", "Time's up!",
						JOptionPane.PLAIN_MESSAGE, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
				if (response == JOptionPane.OK_OPTION) {
					timesUp.stop();
					timesUpClose = 0;
					clip.stop();
					restFrame.setVisible(false);
					restTimer.stop();
					timer.start();
					clip.stop();
				}

			}

		}
	});
	Clip clip;
	JFrame restFrame;
	Timer pauseTimer = new Timer(1000, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {

			pauseDuration = pauseDuration + 1000;

		}
	});
	Timer timesUp = new Timer(1000, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (timesUpClose == 60000) {
				clip.stop();
				restTimer.stop();
				sessionEnd = "Inactivity";
				timer.stop();
				stop();
				timesUp.stop();
				restFrame.setVisible(false);

				JOptionPane.showMessageDialog(null, "Session ended due to inactivity");

			}
			timesUpClose += 1000;

		}
	});

	private Date currentDate;
	private SimpleDateFormat timeFormat;
	JLabel timeLabel;
	JButton startButton;
	JButton stopButton;
	private JLabel subjectLabel;
	private JComboBox subjectBox;
	String platform;
	String subject;
	String title;
	private JButton pauseButton;
	private JButton restButton;
	String date;
	JLabel restLabel;
	private JScrollPane scrollPane_1;
	private JButton addTypeButton;
	private JButton addPlatformButton;
	private JButton addSubjectButton;
	private JButton addLanguageButton;
	String dayStatePass;
	String language;
	int typeid;
	int platformid;
	int subjectid;
	int languageid;
	JComboBox platformBox;
	int elapsedTime = 0;
	double pauseDuration = 0;
	int seconds = 0;
	int minutes = 0;
	int hours = 0;
	double rest;
	double time;
	int restDuration = 0;
	int restTime = 0;
	int restMinute = 0;
	int restSecond = 0;
	int restHour = 0;
	int timesUpClose = 0; // increments when JOPtionPaneDialog is opened, will wait for user input, and if
							// a minute has passed and no input from the user, automatic stop session
	boolean started = false;
	String restSecString;
	String restMinString;
	String restHourString;
	String seconds_string;
	String minutes_string; 
	String hours_string; 
	String sessionEnd;
	HashMap<Integer, String>typeMap;
	HashMap<Integer, String>platformMap;
	HashMap<Integer, String>subjectMap;
	HashMap<Integer, String>languageMap;
	JComboBox languageBox;
	JLabel titleLabel;
	JComboBox typeBox;
	JButton startDayButton;
	JButton endDayButton;
	JLabel typeLabel;
	JLabel languageLabel;
	JLabel platformLabel;
	JLabel upSkillLabel;
}