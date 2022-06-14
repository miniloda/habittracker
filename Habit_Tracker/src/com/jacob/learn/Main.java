package com.jacob.learn;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;

import com.mysql.jdbc.Connection;

public class Main extends JFrame implements ActionListener {
	private JTextField courseField;
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
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Main() {
		dayStarted = "";
		connect();
		checkDay();
		checkGap();
		getContentPane().setBackground(Color.DARK_GRAY);

		JLabel lblNewLabel = new JLabel("UpSkill!");
		lblNewLabel.setBounds(113, 16, 143, 47);
		lblNewLabel.setForeground(Color.WHITE);
		lblNewLabel.setFont(new Font("Papyrus", Font.BOLD, 29));

		JLabel lblNewLabel_1 = new JLabel("Platform");
		lblNewLabel_1.setBounds(56, 142, 72, 14);
		lblNewLabel_1.setForeground(Color.WHITE);

		platformBox = new JComboBox();
		platformBox.setBackground(Color.GRAY);
		platformBox.setBounds(138, 139, 158, 20);

		titleField = new JLabel("Title");
		titleField.setBounds(56, 231, 72, 14);
		titleField.setForeground(Color.WHITE);

		courseField = new JTextField();
		courseField.setBackground(Color.GRAY);
		courseField.setBounds(138, 228, 158, 20);
		courseField.setColumns(10);

		JPanel panel = new JPanel();
		panel.setBounds(56, 259, 241, 203);
		panel.setBackground(Color.LIGHT_GRAY);
		panel.setLayout(new CardLayout(0, 0));

		panel_1 = new JPanel();
		panel_1.setBackground(Color.LIGHT_GRAY);
		panel.add(panel_1, "init");
		panel_1.setLayout(null);

		startButton = new JButton("Start");
		startButton.setBounds(30, 138, 89, 23);
		panel_1.add(startButton);
		startButton.addActionListener(this);
		timeLabel = new JLabel("");
		timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		timeLabel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		timeLabel.setFont(new Font("Verdana", Font.PLAIN, 35));
		timeLabel.setBounds(30, 36, 184, 91);
		panel_1.add(timeLabel);
		cardLayout = (CardLayout) (panel.getLayout());

		String[] platforms = { "None", "Youtube", "Coursera", "CodeCademy", "CodeWars", "Kaggle", "Physical Book",
				"Others" };
		for (String platform : platforms) {
			platformBox.addItem(platform);
		}
		lblNewLabel_3 = new JLabel("Subject");
		lblNewLabel_3.setBounds(56, 173, 66, 14);
		lblNewLabel_3.setForeground(Color.WHITE);

		subjectBox = new JComboBox();
		subjectBox.setBackground(Color.GRAY);
		subjectBox.setBounds(139, 170, 158, 20);
		String[] subjects = { "None", "Web Development", "Data Science", "Programming", "Math", "Science", "Philosophy",
				"Others" };
		for (String subject : subjects) {
			subjectBox.addItem(subject);
		}

		languageBox = new JComboBox();
		languageBox.setBackground(Color.GRAY);
		languageBox.setBounds(138, 200, 158, 20);
		String[] languages = { "None", "Python", "R", "SQL", "Scala", "Java", "C#", "C++", "JavaScript", "Others" };
		for (String language : languages) {
			languageBox.addItem(language);
		}
		typeBox = new JComboBox();
		String[] types = { "None", "Project", "Academic", "Self-Learning" };
		for (String type : types) {
			typeBox.addItem(type);
		}

		typeBox.setBackground(Color.GRAY);
		typeBox.setBounds(138, 108, 158, 20);
		getContentPane().add(typeBox);

		JLabel lblNewLabel_3_1 = new JLabel("Language");
		lblNewLabel_3_1.setBounds(56, 203, 66, 14);
		lblNewLabel_3_1.setForeground(Color.WHITE);
		getContentPane().setLayout(null);
		getContentPane().add(lblNewLabel);
		getContentPane().add(lblNewLabel_3_1);
		getContentPane().add(languageBox);
		getContentPane().add(titleField);
		getContentPane().add(courseField);
		getContentPane().add(panel);
		getContentPane().add(lblNewLabel_1);
		getContentPane().add(platformBox);
		getContentPane().add(lblNewLabel_3);
		getContentPane().add(subjectBox);

		JLabel lblNewLabel_1_1 = new JLabel("Type");
		lblNewLabel_1_1.setForeground(Color.WHITE);
		lblNewLabel_1_1.setBounds(56, 111, 72, 14);
		getContentPane().add(lblNewLabel_1_1);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 364, 531);
		stopButton = new JButton("Stop");
		stopButton.setBounds(129, 138, 89, 23);
		panel_1.add(stopButton);

		pauseButton = new JButton("Pause");
		pauseButton.setBounds(30, 169, 89, 23);
		panel_1.add(pauseButton);

		restButton = new JButton("Rest");
		restButton.setBounds(129, 169, 89, 23);

		panel_1.add(restButton);

		startDayButton = new JButton("Start Day");
		startDayButton.setEnabled(true);
		startDayButton.setBounds(80, 74, 89, 23);
		getContentPane().add(startDayButton);

		endDayButton = new JButton("End Day");
		endDayButton.setEnabled(true);
		endDayButton.setBounds(186, 74, 89, 23);
		getContentPane().add(endDayButton);
		restButton.addActionListener(this);
		pauseButton.addActionListener(this);
		stopButton.addActionListener(this);
		startDayButton.addActionListener(this);
		endDayButton.addActionListener(this);
		setLocationRelativeTo(null);
		if (dayStarted.equals("STARTED")) {
			startDayButton.setEnabled(false);
			endDayButton.setEnabled(true);
			startButton.setEnabled(true);

		} else {
			startButton.setEnabled(false);
			endDayButton.setEnabled(false);
			startDayButton.setEnabled(true);

		}
		init();
	}

	JComboBox languageBox;
	JLabel titleField;
	JComboBox typeBox;
	JButton startDayButton;
	JButton endDayButton;

	/**
	 * connect to mySQL database
	 */
	private void connect() {

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		}
		try {
			con = (Connection) DriverManager.getConnection("jdbc:mysql://localhost/javacrud", "root", "");
		} catch (SQLException e) {

			e.printStackTrace();
		}

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
	}

	JPanel panel_1;

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
		pauseButton.setEnabled(true);
		stopButton.setEnabled(true);
		restButton.setEnabled(true);
		timer.start();

	}

	void clearFields() {
		platformBox.setSelectedItem("None");
		subjectBox.setSelectedItem("None");
		languageBox.setSelectedItem("None");
		courseField.setText("");
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
	String dayStarted;
	int dayNumber;
	ResultSet rs;
	String dayState;

	public void checkDay() {
		System.out.println(dayNumber + "3");
		try {
			pst = con.prepareStatement(
					"SELECT `day_number`, `day_state`  FROM `habit_tracker` ORDER BY `dateperformed` DESC LIMIT 1");
			rs = pst.executeQuery();
			rs.first();
			System.out.println(rs.getString("day_state"));
			if (rs.getString("day_state").equals("STARTED") || rs.getString("day_state").equals("SKIPPED")) {
				dayStarted = rs.getString("day_state");
				try {

					dayNumber = Integer.parseInt(rs.getString("day_number"));
				} catch (Exception ex) {
					ex.printStackTrace();

				}

			} else {
				try {
					dayNumber = Integer.parseInt(rs.getString("day_number"));
					dayStarted = "ENDED";
				} catch (Exception ex) {
					ex.printStackTrace();
					dayNumber = 0;
				}

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
			dayNumber = 0;
		}

	}

	void getDetails() {
		type = (String) typeBox.getSelectedItem();
		platform = (String) platformBox.getSelectedItem();
		subject = (String) subjectBox.getSelectedItem();
		if (courseField.getText().equals("")) {
			course = "None";
		} else {
			course = courseField.getText();
		}
		date = getDate();
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
	}

	void sendToDB() {
		System.out.println(dayNumber);
		try {
			pst = con.prepareStatement(
					"insert into `habit_tracker`(`day_number`, `dateperformed`, `type`, `platform`, `subject`, `course`, `language`, `time_elapsed`, `pause_duration`, `rest_duration`, `session_end`, `pauseReason`)"
							+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
			pst.setInt(1, dayNumber);
			pst.setString(2, date);
			pst.setString(3, type);
			pst.setString(4, platform);
			pst.setString(5, subject);
			pst.setString(6, course);
			pst.setString(7, (String) languageBox.getSelectedItem());
			pst.setString(8, timeDur);
			pst.setString(9, pauseDur);
			pst.setString(10, restDur);
			pst.setString(11, sessionEnd);
			pst.setString(12, pauseReason.toString());

			pst.executeUpdate();

		} catch (SQLException ex) {
			Logger logger = Logger.getAnonymousLogger();
			Exception e1 = new Exception();
			Exception e2 = new Exception(e1);
			logger.log(Level.SEVERE, "an exception was thrown", e2);
			System.out.println(ex);
		}
		clearFields();
	}

	public String getDate() {
		currentDate = new Date();
		timeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		return timeFormat.format(currentDate);
	}

	public void endDay() throws SQLException {
		pst = con.prepareStatement("UPDATE `habit_tracker` SET `day_state` = 'ENDED' WHERE `dateperformed` = "
				+ "(SELECT MAX(`dateperformed`) from `habit_tracker`)");
		pst.executeUpdate();
	}

	public void checkGap() {
		try {

			pst = con.prepareStatement("SELECT MAX(`dateperformed`) as date FROM `habit_tracker`");
			rs = pst.executeQuery();
			rs.first();
			Timestamp lastDate = rs.getTimestamp("date");
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
						"INSERT INTO `habit_tracker`(day_number,dateperformed, day_state) VALUES (?,?,?)");
				pst.setInt(1, dayNumber);
				pst.setString(3, "SKIPPED");
				pst.setString(2, tempformat.format(lastDateTS + tempDay * 1000 * 60 * 60));
				pst.executeUpdate();
				dayNumber += 1;
				difference -= 24;
				tempDay += 24;
				dayStarted = "";

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == startButton) {
			System.out.println("Start" + dayNumber);
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
			JOptionPane.showMessageDialog(null, "Great Job today, self!");
			System.exit(0);
		}
		if (e.getSource() == startDayButton) {
			dayNumber += 1;
			startButton.setEnabled(true);
			endDayButton.setEnabled(false);
			startDayButton.setEnabled(false);

		}

	}

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
	int timesUpClose = 0; // increments when JOPtionPaneDialog is opened, will wait for user input, and if
							// a minute has passed and no input from the user, automatic stop session
	boolean started = false;
	String restSecString = String.format("%02d", restSecond);
	String restMinString = String.format("%02d", restMinute);
	String seconds_string = String.format("%02d", seconds);
	String minutes_string = String.format("%02d", minutes);
	String hours_string = String.format("%02d", hours);
	String sessionEnd;
	Timer timer = new Timer(1000, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			elapsedTime = elapsedTime + 1000;
			hours = (elapsedTime / 3600000);
			minutes = (elapsedTime / 60000) % 60;
			seconds = (elapsedTime / 1000) % 60;
			seconds_string = String.format("%02d", seconds);
			minutes_string = String.format("%02d", minutes);
			hours_string = String.format("%02d", hours);
			timeLabel.setText(hours_string + ":" + minutes_string + ":" + seconds_string);
			time = (double) elapsedTime / 60000;
			restTime += (0.33333333333 * 1000);

		}
	});

	Timer restTimer = new Timer(1000, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			restDuration += 1000;
			restTime = restTime - 1000;
			restMinute = (restTime / 60000) % 60;
			restSecond = (restTime / 1000) % 60;
			restSecString = String.format("%02d", restSecond);
			restMinString = String.format("%02d", restMinute);
			restLabel.setText("00" + ":0" + restMinute + ":" + restSecond);

			if (restTime <= 0) {
				File file = new File(
						"C:\\Users\\jacob\\git\\habittracker\\Habit_Tracker\\src\\Danger Alarm Meme Sound Effect.wav");
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

	CardLayout cardLayout;
	private Date currentDate;
	private SimpleDateFormat timeFormat;
	JLabel timeLabel;
	JButton startButton;
	JButton stopButton;
	private JLabel lblNewLabel_3;
	private JComboBox subjectBox;
	String platform;
	String subject;
	String course;
	private JButton pauseButton;
	private JButton restButton;
	String date;
	JLabel restLabel;
}
