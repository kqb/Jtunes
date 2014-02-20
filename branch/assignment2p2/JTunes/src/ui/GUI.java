/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import persistence.Cloud;
import persistence.Playlist;
import model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JToggleButton;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Font;
import java.awt.Desktop;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.Cursor;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.GroupLayout.Alignment;
import java.awt.Component;
import java.io.IOException;

import javax.swing.LayoutStyle.ComponentPlacement;

/**
 * A graphical user interface for jTunes
 * @author Katie Lo
 */
public class GUI extends javax.swing.JFrame {

    /**
     * non GUI related members
     */
    private int[] cloudSelection;
    private int[] userSelection;
    private int[] PLSelection;
    private DefaultTableModel defaultTable;
	//members related to storing user info
    private String email;
    private String password;
    private User user;
    private Object[][] allSongs; //
    private Library library;
    private List<Playlist> playlists;
    private int currIndex; //keep track of index of current loaded song
    private JTable currTable; //keep track of index of current table the player is playing from
    //members related to play back
    boolean chkSame = true; //used for checking whether current index and random generated index are the same
    private SongPlayer player;
    boolean songLoaded = false; //whether song is loaded to the player's sequencer
    boolean playing = false; 
    boolean paused = false;
    boolean invalidSong = false; //check if song selected to play is not downloaded.
	private Timer timer; //used to control the song progress slider

    /**
     * initialize GUI
     */
    public GUI() {
    	setResizable(false);
    	setPreferredSize(new Dimension(1280, 675));
    	setIconImage(Toolkit.getDefaultToolkit().getImage(GUI.class.getResource("/Batch-master-icons/PNG/32x32/vinyl.png")));
        initComponents();
        player = new SongPlayer();

        defaultTable = new DefaultTableModel(
            	new Object[][] {
            	},
            	new String[] {
            		"Please Log in, or create a new user account on menu bar first to see the library"
            	}
            );
    }
	/**
	 * Methods related to user and library function of GUI
	 */
    private void updatePlaylist(){
     	playlists = library.getPlaylists();
    	PLTable.setModel(new PlaylistModel());
    }
    private void updateLibPlaylists(){
    	library = new Library(user);
    	playlists = library.getPlaylists();
    	Vector<String> plNames = new Vector<String>();
    	for (Playlist pl : playlists) {
    		plNames.add(pl.getPlaylistName());
    	}
    	playlistSelection.setModel(new DefaultComboBoxModel(plNames));
    	
    }

    
    
	/**
	 * Methods related to play back function of GUI
	 */
    
	private boolean isDownloaded(int index, JTable table) {
		try{
			return table.getModel().getValueAt(index, 2).toString().equals("true");
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
		  return false;
		}
	}
	private void play(int[] tableSelection){
    	//stop player first if playing
    	if ((playing||!player.sequencerIsEmpty())&&!paused) {
    		invalidSong = true;
    		player.stop();
    	}
    	
    	//resume song if paused
    	if (paused) {
    		player.play();
    		playing = true;
    		paused = false;
    		pauseSong.setSelected(false);
    		timer.start();
    		return;
    		}
    	
    		if (tableSelection == null) {
        		JFrame frame = new JFrame("");
        		JOptionPane.showMessageDialog(frame,
    	                "Please select a song from playlist library or user library to play first.",
    	                "No song selected",
    	                JOptionPane.PLAIN_MESSAGE);
        		return;
    		}
    		//design does not allow multiple song selected for playing
    		if (tableSelection.length > 1) {
        		JFrame frame = new JFrame("");
        		JOptionPane.showMessageDialog(frame,
    	                "Please only ONE song from playlist library or user library to play.",
    	                "Multiple selection",
    	                JOptionPane.PLAIN_MESSAGE);
        		return;
    			
    		}
    		//check if playing from user library, or playlist library
    		int sel = tableSelection[0];
    		currIndex = sel;
    		if (tableSelection == userSelection){
	    		if (!isDownloaded(sel, userLibraryTable)) {
	        		JFrame frame = new JFrame("");
	        		JOptionPane.showMessageDialog(frame,
	    	                "Song is not donloaded yet, please download it first from the cloud library",
	    	                "Cannot play song",
	    	                JOptionPane.PLAIN_MESSAGE);
	        		invalidSong = true;
	        		return;
	    		}
	    		loadSong(sel, userLibraryTable);
    		} else {
    	    		if (!isDownloaded(sel, PLTable)) {
    	        		JFrame frame = new JFrame("");
    	        		JOptionPane.showMessageDialog(frame,
    	    	                "Song is not donloaded yet, please download it first from the cloud library",
    	    	                "Cannot play song",
    	    	                JOptionPane.PLAIN_MESSAGE);
    	        		invalidSong = true;
    	        		return;
    	    		}
    	    		loadSong(sel, PLTable);
    		
    	}
    	
    	player.play();
    	playing = true;
    	progress.setValue(0);
    	paused = false;
    	timer.start();
    	invalidSong = false;
	}
	private void stop(){
		if (player.sequencerIsEmpty()) return;
		player.stop();
		playing = false;
		progress.setValue(0);
		songLabel.setText("Currently playing :");
	}
	
	private void nextSong(){
		try {
		//stop current song
		stop();
		//get the maximum index
		int maxIndex = currTable.getRowCount()-1;
		if (shuffleToggle.isSelected()){
			//keep randomizing index if random index equals to current index
			while (chkSame){
			//randomized index to play random song in library
			int rndIndex = (int)(Math.random()*(maxIndex+1));
			chkSame = (currIndex == rndIndex);
			currIndex = rndIndex;
			}
			chkSame = true;
		} else {
			if (currIndex < maxIndex) currIndex++;
			else currIndex = 0;
		}
		//skips over songs that are not downloaded, but is in library
		while(!isDownloaded(currIndex, currTable)) {
			currIndex++;
			if (currIndex > maxIndex) currIndex = 0;
		}
		loadSong(currIndex, currTable);
		player.play();
		playing = true;
		progress.setValue(0);
		paused = false;
		timer.start();
		} catch (java.lang.NullPointerException e) { //happens when no song is currently selected
			//simply return
			return;
		}
	}
	private void prevSong(){
		try {
		//get the maximum index
		int maxIndex = currTable.getRowCount()-1;
		//stop current song
		stop();
		if (currIndex > 0) currIndex--;
		else currIndex = 0;
		//skips over songs that are not downloaded, but is in library
		while(!isDownloaded(currIndex, currTable)) {
			currIndex--;
			if (currIndex < 0) currIndex = maxIndex;
		}
		loadSong(currIndex, currTable);
		player.play();
		playing = true;
		progress.setValue(0);
		paused = false;
		timer.start();
		} catch (java.lang.NullPointerException e) { //happens when no song is currently selected
			//simply return
			return;
		}
	}
	private void loadSong(int index, JTable table) {
		Map<List<String>,Song> userSongs = library.getUserSongs();
		List<String> attributes = new ArrayList<String>();
		for (int j=0; j<2; j++){
    		String info = table.getModel().getValueAt(index, j).toString();
    		attributes.add(info);
    	}
		Song song = userSongs.get(attributes);
		player.initSong(song);
		songLabel.setText("Currently playing :    " + attributes.get(1) + " - " + attributes.get(0));
		songLoaded = true;
		currTable = table;
		progress.setMaximum(player.audioLength);
		}
	
	 
	 private void tick() {
	     if (player.getSequencer().isRunning( )) {
	         player.audioPosition = (int)player.getSequencer().getTickPosition( );
	         progress.setValue(player.audioPosition);
	     }
	     else if((!player.getSequencer().isRunning( ))){
	    	if (!playMode.isSelected() || invalidSong) {
	    		stop();
	    		invalidSong = false;
	     } 
	    	 if (!playMode.isSelected()) {
		    		stop();
		    		invalidSong = false;
		     } 
	    	else {
	    	 //happens when stop is clicked and in shuffle mode, need to stop without playing the next song
	    	 if (progress.getValue()==0) {stop(); return;}
	    	 
	    	 stop();
	    	 nextSong();
	     }
	     }
	 }
	
	 /** Skip to the specified position */
	 private void skipSong(int position) { // Called when user drags the slider
	     if (position < 0 || position > player.audioLength) return;
	     player.audioPosition = position;
	     player.getSequencer().setTickPosition(position);
	 }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        // timer calls tick() 10 times a second to keep
        // the progress slider syncronized with the music.
        timer = new javax.swing.Timer(100, new ActionListener( ) {
                public void actionPerformed(ActionEvent e) { tick(); }
            });
        userLibLabel = new javax.swing.JLabel();
        userLibLabel.setBounds(651, 43, 352, 32);
        userLibLabel.setIcon(new ImageIcon(GUI.class.getResource("/Batch-master-icons/PNG/32x32/folder-2.png")));
        playSong = new javax.swing.JButton();
        playSong.setBounds(536, 547, 60, 40);
        playSong.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        playSong.setIcon(new ImageIcon(GUI.class.getResource("/Batch-master-icons/PNG/32x32/play.png")));
        stopSong = new javax.swing.JButton();
        stopSong.setBounds(680, 547, 60, 40);
        stopSong.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        stopSong.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		stop();
        	}
        });
        stopSong.setIcon(new ImageIcon(GUI.class.getResource("/Batch-master-icons/PNG/32x32/stop.png")));
        userLibScroll = new javax.swing.JScrollPane();
        userLibScroll.setBounds(651, 78, 598, 350);
        userLibraryTable = new javax.swing.JTable();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem1.setIcon(new ImageIcon(GUI.class.getResource("/Batch-master-icons/PNG/16x16/delete.png")));
        helpMenu = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("jTunes");

        userLibLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        userLibLabel.setText("User local library");
        playSong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playAction(evt);
            }
        });

        userLibraryTable.setModel(new DefaultTableModel(
        	new Object[][] {
        	},
        	new String[] {
        		"Please Log in, or create a new user account on menu bar first to see the library"
        	}
        ));
        userLibraryTable.setRowSelectionAllowed(true);
        userLibraryTable.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        userLibraryTable.getSelectionModel().addListSelectionListener(new UserSelectionListener());
        userLibScroll.setViewportView(userLibraryTable);

        fileMenu.setText("File");

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem1.setText("Exit");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        
        logIn = new JMenuItem("Log in");
        logIn.setIcon(new ImageIcon(GUI.class.getResource("/Batch-master-icons/PNG/16x16/cloud-add-2.png")));
        
        //Log in user
        logIn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		  	JFrame frame = new JFrame(" ");
        	        email = JOptionPane.showInputDialog("Please enter your email:");
        	        if (email==null||email.isEmpty()){
        	        	JOptionPane.showMessageDialog(frame,
        	                "Email cannot be empty!",
        	                "Login Failure!",
        	                JOptionPane.ERROR_MESSAGE);
        	        	return;
        	        	} 
        	        password = JOptionPane.showInputDialog("Please enter your password:");
        	        if (password==null||password.isEmpty()){
        	        	JOptionPane.showMessageDialog(frame,
        	                "Password cannot be empty!",
        	                "Login Failure!",
        	                JOptionPane.ERROR_MESSAGE);
        	        	return;
        	        	} 
        	       
	        	        User tempUser = new User(email, password);
	
	        	        boolean check = Cloud.checkUserValidity(tempUser);
	        	        if (check) {
	        	            JOptionPane.showMessageDialog(frame,
	        	                "Please enjoy JTunes!",
	        	                "Login Success!",JOptionPane.PLAIN_MESSAGE);
	        	            user = tempUser;
	        	            library = new Library(user);
	        	            updateUserSongList();
	        	            updateCloudSongList();
	        	            updatePlaylist();
	        	            updateLibPlaylists();
	        	            
	        	            
	        	        } else {
	        	            JOptionPane.showMessageDialog(frame,
	        	                "Please log in again, or create a new account!",
	        	                "Login Failure!",
	        	                JOptionPane.ERROR_MESSAGE);
	        	        }

        	}
        });
        fileMenu.add(logIn);
        
        logOut = new JMenuItem("Log out");
        logOut.setIcon(new ImageIcon(GUI.class.getResource("/Batch-master-icons/PNG/16x16/user-4-remove.png")));
        
        //log out user
        logOut.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if (!(user == null)) {
        		//remove user and the user's library
        		user = null;
        		library = null;
        		//reset to default tables
                cloudLibraryTable.setModel(defaultTable);
                userLibraryTable.setModel(defaultTable);
                PLTable.setModel(defaultTable);
                //show goodbye message after logging out
      		    JFrame frame = new JFrame(" ");
                JOptionPane.showMessageDialog(frame,
    	                "Thank you for using jTunes!",
    	                "Bye",
    	                JOptionPane.PLAIN_MESSAGE);
        		} else {
        		JFrame frame = new JFrame(" ");
    			JOptionPane.showMessageDialog(frame,
    	                "Cannot log out if you aren't even logged in yet!",
    	                "Error!",
    	                JOptionPane.ERROR_MESSAGE);
        		}
        }});
                
        fileMenu.add(logOut);
        
        createUser = new JMenuItem("Create user");
        createUser.setIcon(new ImageIcon(GUI.class.getResource("/Batch-master-icons/PNG/16x16/user-4-add.png")));
        
        //create a new user
        createUser.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		
                JFrame frame = new JFrame(" ");
                String tempName = JOptionPane.showInputDialog("Please enter your name:");
                if (tempName==null||tempName.isEmpty()) {
        			JOptionPane.showMessageDialog(frame,
        	                "Your name cannot be empty!",
        	                "Error!",
        	                JOptionPane.ERROR_MESSAGE);
        			return;
                }
                String tempEmail = JOptionPane.showInputDialog("Please enter your email:");
                Pattern validEmail = Pattern.compile(
                        "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$");
                if (!validEmail.matcher(tempEmail).matches()) {
        			JOptionPane.showMessageDialog(frame,
        	                "This is not a valid email address!",
        	                "Error!",
        	                JOptionPane.ERROR_MESSAGE);
        			return;    	
                }
                String tempPw = JOptionPane.showInputDialog("Please enter your password:");
                if (tempName==null||tempName.isEmpty()) {
        			JOptionPane.showMessageDialog(frame,
        	                "Your password cannot be empty!",
        	                "Error!",
        	                JOptionPane.ERROR_MESSAGE);
        			return;
                }
                User tempUser = new User(tempEmail, tempPw, tempName); //create new user using new typed information
                boolean createUser = Cloud.createUser(tempUser);
                if (createUser){
                    JOptionPane.showMessageDialog(frame,
                    "Congratulations! You have succesfully created a new account!",
                    "Success!",JOptionPane.PLAIN_MESSAGE);
                    user = tempUser;
                    library = new Library(user);
                    updateUserSongList();
                    updateCloudSongList();
    	            updatePlaylist();
    	            updateLibPlaylists();

                }else if(!createUser){
                    JOptionPane.showMessageDialog(frame,
                    "Sorry user already exists or your input is invalid, try again!",
                    "Failure!", JOptionPane.ERROR_MESSAGE);
                }
        	}
        });
        fileMenu.add(createUser);
        fileMenu.add(jMenuItem1);

        jMenuBar1.add(fileMenu);

        helpMenu.setText("Help");
        jMenuBar1.add(helpMenu);
        
        userGuide = new JMenuItem("User Guide");
        userGuide.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        			if (Desktop.isDesktopSupported()) {
        				//open user guide
        	            try {
        	                File guide = new File("data/users/UserGuide.pdf");
        	                Desktop.getDesktop().open(guide);
        	            } catch (IOException e) {
        	                e.printStackTrace();// no application registered for PDFs
        	            }
        	        }
        	}
        });
        userGuide.setIcon(new ImageIcon(GUI.class.getResource("/Batch-master-icons/PNG/16x16/paper-roll.png")));
        helpMenu.add(userGuide);

        setJMenuBar(jMenuBar1);
        
        JButton lastSong = new JButton("");
        lastSong.setBounds(336, 547, 60, 40);
        lastSong.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		prevSong();
        	}
        });
        lastSong.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lastSong.setIcon(new ImageIcon(GUI.class.getResource("/Batch-master-icons/PNG/32x32/previous.png")));
        
        JButton nextSong = new JButton("");
        nextSong.setBounds(889, 547, 60, 40);
        nextSong.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		nextSong();
        	}
        });
        nextSong.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        nextSong.setIcon(new ImageIcon(GUI.class.getResource("/Batch-master-icons/PNG/32x32/next.png")));
        
        progress = new JSlider();
        progress.setBounds(336, 529, 613, 16);
        progress.setValue(0);
        progress.addChangeListener(new ChangeListener( ) {
            public void stateChanged(ChangeEvent e) {
            	if (!songLoaded) return;
                int value = progress.getValue( );
                // Update time label
                time.setText(value + "");

                // Skip to position if not already there
                if (value != player.audioPosition) skipSong(value);
            }
        });
        
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setBounds(0, 0, 649, 468);
        tabbedPane.addChangeListener(new ChangeListener() {
        	public void stateChanged(ChangeEvent arg0) {
        		if (!(addSongPL==null)){
        			
	        		if  (addSongPL.isVisible()) {
	        			addSongPL.setVisible(false);
        			}
	        		else if (!(addSongPL.isVisible())){
	        				addSongPL.setVisible(true);
	        		}
        		}
        	}
        	
        });
        
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        tabbedPane.addTab("Cloud", new ImageIcon(GUI.class.getResource("/Batch-master-icons/PNG/16x16/cloud.png")), panel, "Display songs from cloud library");
        
                cloudLibLabel = new javax.swing.JLabel();
                cloudLibLabel.setBounds(20, 13, 337, 32);
                cloudLibLabel.setIcon(new ImageIcon(GUI.class.getResource("/Batch-master-icons/PNG/32x32/cloud.png")));
                
                        cloudLibLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
                        cloudLibLabel.setText("jTunesCloud library");
        cloudLibScroll = new javax.swing.JScrollPane();
        cloudLibScroll.setBounds(20, 48, 598, 350);
        cloudLibraryTable = new javax.swing.JTable();
        cloudLibraryTable.setShowGrid(false);
        
                cloudLibraryTable.setModel(new DefaultTableModel(
                	new Object[][] {
                	},
                	new String[] {
                		"Please Log in, or create a new user account on menu bar first to see the library"
                	}
                ) {
                	boolean[] columnEditables = new boolean[] {
                		false
                	};
                	public boolean isCellEditable(int row, int column) {
                		return columnEditables[column];
                	}
                });
                cloudLibraryTable.getColumnModel().getColumn(0).setPreferredWidth(742);
                cloudLibraryTable.setRowSelectionAllowed(true);
                cloudLibraryTable.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                cloudLibraryTable.getSelectionModel().addListSelectionListener(new CloudSelectionListener());
                cloudLibScroll.setViewportView(cloudLibraryTable);
        downloadSong = new javax.swing.JButton();
        downloadSong.setBounds(225, 395, 178, 40);
        downloadSong.setFont(new Font("SansSerif", Font.PLAIN, 10));
        downloadSong.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        downloadSong.setIcon(new ImageIcon(GUI.class.getResource("/Batch-master-icons/PNG/32x32/cloud-download.png")));
        downloadSong.setToolTipText("Select a song, or multiple songs from the jTunesCloud library to download, and add to your user library");
        
                downloadSong.setText("Download song(s)");
                downloadSong.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton5ActionPerformed(evt);
                    }
                });
        panel.setLayout(null);
        panel.add(cloudLibLabel);
        panel.add(cloudLibScroll);
        panel.add(downloadSong);
        
        JPanel panel_1 = new JPanel();
        panel_1.setOpaque(false);
        tabbedPane.addTab("Playlists", new ImageIcon(GUI.class.getResource("/Batch-master-icons/PNG/16x16/list.png")), panel_1, "Display songs from playlists");
        
        PLScroll = new JScrollPane();
        PLScroll.setBounds(20, 48, 598, 350);
        
        PLTable = new JTable();
        PLTable.setShowGrid(false);
        
        PLTable.setModel(new DefaultTableModel(
        	new Object[][] {
        	},
        	new String[] {
        		"Please Log in, or create a new user account on menu bar first to see the library"
        	}
        ) {
        	boolean[] columnEditables = new boolean[] {
        		false
        	};
        	public boolean isCellEditable(int row, int column) {
        		return columnEditables[column];
        	}
        });
        PLTable.getColumnModel().getColumn(0).setPreferredWidth(742);
        PLTable.setRowSelectionAllowed(true);
        PLTable.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        PLTable.getSelectionModel().addListSelectionListener(new PLSelectionListener());
        PLScroll.setViewportView(PLTable);
        
        JLabel PLLabel = new JLabel();
        PLLabel.setBounds(20, 13, 188, 32);
        PLLabel.setIcon(new ImageIcon(GUI.class.getResource("/Batch-master-icons/PNG/32x32/list.png")));
        PLLabel.setText("Playlist library");
        PLLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
        
        JButton delSongPL = new JButton("Remove song(s)");
        delSongPL.setBounds(275, 395, 156, 40);
        delSongPL.setFont(new Font("SansSerif", Font.PLAIN, 10));
        delSongPL.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
    	    	if (PLSelection == null) {
    	    		JFrame frame = new JFrame("");
    	    		JOptionPane.showMessageDialog(frame,
    		                "Please select song(s) to add to playlist first.",
    		                "Error!",
    		                JOptionPane.ERROR_MESSAGE);
    	    		return;
    	    	}
    	        for (int i : PLSelection) {
    	        	String[] attributes = new String[2];
    	        	for (int j=0; j<2; j++){
    	        		String info = PLTable.getModel().getValueAt(i, j).toString();
    	        		attributes[j] = info;
    	        	}
    	        	playlists.get(playlistSelection.getSelectedIndex()).delSong(attributes[0], attributes[1]);
    	        }
    	        //update the library then the userSongList to reflect changes
    	        library = new Library(user);
    	        updatePlaylist();
    	        
        	
        		
        	}
        });
        delSongPL.setIcon(new ImageIcon(GUI.class.getResource("/Batch-master-icons/PNG/32x32/remove.png")));
        
        libSelection = new JComboBox();
        libSelection.setBounds(540, 19, 108, 26);
        libSelection.setFont(new Font("SansSerif", Font.PLAIN, 10));
        libSelection.setModel(new DefaultComboBoxModel(new String[] {"User library", "Playlist library"}));
        libSelection.setSelectedIndex(0);
        
        lblPlayFromWhich = new JLabel();
        lblPlayFromWhich.setBounds(395, 14, 188, 32);
        lblPlayFromWhich.setText("Play from which library?");
        lblPlayFromWhich.setFont(new Font("Tahoma", Font.PLAIN, 14));
        
        playlistLbl = new JLabel();
        playlistLbl.setBounds(185, 13, 188, 32);
        playlistLbl.setText("Select playlist");
        playlistLbl.setFont(new Font("Tahoma", Font.PLAIN, 14));
        
        playlistSelection = new JComboBox();
        playlistSelection.setBounds(275, 17, 108, 26);
        playlistSelection.setFont(new Font("SansSerif", Font.PLAIN, 9));
        playlistSelection.addItemListener(new ItemListener() {
        	public void itemStateChanged(ItemEvent arg0) {
        		updatePlaylist();
        	}
        });
        playlistSelection.setModel(new DefaultComboBoxModel(new String[] {"no playlists"}));
        playlistSelection.setSelectedIndex(0);
        
        newPlaylist = new JButton("New playlist");
        newPlaylist.setBounds(20, 395, 153, 40);
        newPlaylist.setFont(new Font("SansSerif", Font.PLAIN, 10));
        newPlaylist.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
    		  	JFrame frame = new JFrame(" ");
    	        String plName = JOptionPane.showInputDialog("Please enter playlist name:");
    	        if (plName==null||plName.isEmpty()){
    	        	JOptionPane.showMessageDialog(frame,
    	                "Playlist name cannot be empty!",
    	                "Cannot Create",
    	                JOptionPane.ERROR_MESSAGE);
    	        	return;
    	        	}
    	        Playlist pl = new Playlist(user.getId(), plName);
    	        pl.storePlaylist();
        		updateLibPlaylists();
        	}
        });
        newPlaylist.setIcon(new ImageIcon(GUI.class.getResource("/Batch-master-icons/PNG/32x32/document-add.png")));
        panel_1.setLayout(null);
        panel_1.add(PLScroll);
        panel_1.add(PLLabel);
        panel_1.add(delSongPL);
        panel_1.add(libSelection);
        panel_1.add(lblPlayFromWhich);
        panel_1.add(playlistLbl);
        panel_1.add(playlistSelection);
        panel_1.add(newPlaylist);
        
        addSongPL = new JButton("Add song(s)");
        addSongPL.setBounds(886, 425, 152, 40);
        addSongPL.setFont(new Font("SansSerif", Font.PLAIN, 10));
        addSongPL.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        	    	if (userSelection == null) {
        	    		JFrame frame = new JFrame("");
        	    		JOptionPane.showMessageDialog(frame,
        		                "Please select song(s) to add to playlist first.",
        		                "Error!",
        		                JOptionPane.ERROR_MESSAGE);
        	    		return;
        	    	}
        	        for (int i : userSelection) {
        	        	String[] attributes = new String[3];
        	        	for (int j=0; j<3; j++){
        	        		String info = userLibraryTable.getModel().getValueAt(i, j).toString();
        	        		attributes[j] = info;
        	        	}
        	        	playlists.get(playlistSelection.getSelectedIndex()).addSong(
        	        			new Song(new SongInfo(attributes[0], attributes[1])));
        	        }
        	        //update the library then the userSongList to reflect changes
        	        library = new Library(user);
        	        updatePlaylist();
        	        
        	}
        });
        addSongPL.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addSongPL.setVisible(false);
        addSongPL.setIcon(new ImageIcon(GUI.class.getResource("/Batch-master-icons/PNG/32x32/add.png")));
        
        playMode = new JToggleButton("Play mode");
        playMode.setBounds(663, 466, 138, 40);
        playMode.setFont(new Font("SansSerif", Font.PLAIN, 10));
        playMode.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        playMode.setSelected(true);
        playMode.setToolTipText("Press button to toggle between single song play, or continuous play.");
        playMode.setSelectedIcon(new ImageIcon(GUI.class.getResource("/Batch-master-icons/PNG/32x32/repeat-2.png")));
        playMode.setIcon(new ImageIcon(GUI.class.getResource("/Batch-master-icons/PNG/32x32/return.png")));
        
        time = new JLabel("0");
        time.setBounds(283, 529, 55, 16);
        
        songLabel = new JLabel("Currently playing :");
        songLabel.setBounds(434, 503, 472, 32);
        songLabel.setIcon(new ImageIcon(GUI.class.getResource("/Batch-master-icons/PNG/16x16/vinyl.png")));
        songLabel.setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, 12));
        
        pauseSong = new JToggleButton();
        pauseSong.setBounds(608, 547, 60, 40);
        pauseSong.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		//if no song is loaded, or not playing return button to unselected state and return
        		if (!songLoaded||!playing) {pauseSong.setSelected(false); return;}
        		player.pause();
        		timer.stop( );
        		paused = true;
        		playing = false;
        		invalidSong = false;
        	}
        });
        pauseSong.setIcon(new ImageIcon(GUI.class.getResource("/Batch-master-icons/PNG/32x32/pause.png")));
        
        shuffleToggle = new JToggleButton("Shuffle");
        shuffleToggle.setBounds(493, 466, 138, 40);
        shuffleToggle.setFont(new Font("SansSerif", Font.PLAIN, 10));
        shuffleToggle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        shuffleToggle.setIcon(new ImageIcon(GUI.class.getResource("/Batch-master-icons/PNG/32x32/shuffle.png")));
        getContentPane().setLayout(null);
        getContentPane().add(tabbedPane);
        getContentPane().add(time);
        getContentPane().add(addSongPL);
        getContentPane().add(userLibScroll);
        getContentPane().add(playMode);
        getContentPane().add(progress);
        getContentPane().add(userLibLabel);
        getContentPane().add(shuffleToggle);
        getContentPane().add(songLabel);
        getContentPane().add(lastSong);
        getContentPane().add(playSong);
        getContentPane().add(pauseSong);
        getContentPane().add(stopSong);
        getContentPane().add(nextSong);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    public class PlaylistModel extends AbstractTableModel {
    	PlaylistModel(){
	   	 	List<Object[]> tempColList = new ArrayList<Object[]>();
	   	 	int ind = playlistSelection.getSelectedIndex();
	   	 	Playlist currPlaylist = playlists.get(ind);
	   	 	Map<List<String>,Song> PlSongs = currPlaylist.getPlaylist();
	            //run a for loop over the to get all the songs
	        for (Song song : PlSongs.values()) {
	             //process each song here
	             List<Object> tempRowList = new ArrayList<Object>();
	             SongInfo info = song.getInfo();
	             tempRowList.add(info.getName());
	             tempRowList.add(info.getArtist());
	             tempRowList.add(song.hasContent());
	             tempColList.add(tempRowList.toArray(new Object[tempRowList.size()]));
	        }
	        allSongs = new Object[tempColList.size()][3];
	        for (int i=0;i<tempColList.size();i++) {
	            allSongs[i] = tempColList.get(i);
	        }
	        data = allSongs;
        }
        
        
	    private Object[][] data;
	    private String[] columnNames = {
	    		"Song", "Artist", "Downloaded"
	   };
	    
        public int getColumnCount() {
            return columnNames.length;
        }
	
        public int getRowCount() {
            return data.length;
        }
 
        public String getColumnName(int col) {
            return columnNames[col];
        }
 
        public Object getValueAt(int row, int col) {
            return data[row][col];
        }
   }
    
    public class UserSongListModel extends AbstractTableModel {
    	UserSongListModel(){
	   	 	List<Object[]> tempColList = new ArrayList<Object[]>();
	        Map<List<String>,Song> userSongs = library.getUserSongs();
	            //run a for loop over the to get all the songs
	        for (Song song : userSongs.values()) {
	             //process each song here
	             List<Object> tempRowList = new ArrayList<Object>();
	             SongInfo info = song.getInfo();
	             tempRowList.add(info.getName());
	             tempRowList.add(info.getArtist());
	             tempRowList.add(song.hasContent());
	             tempColList.add(tempRowList.toArray(new Object[tempRowList.size()]));
	        }
	        allSongs = new Object[tempColList.size()][3];
	        for (int i=0;i<tempColList.size();i++) {
	            allSongs[i] = tempColList.get(i);
	        }
	        data = allSongs;
        }
        
        
	    private Object[][] data;
	    private String[] columnNames = {
	    		"Song", "Artist", "Downloaded"
	   };
	    
        public int getColumnCount() {
            return columnNames.length;
        }
	
        public int getRowCount() {
            return data.length;
        }
 
        public String getColumnName(int col) {
            return columnNames[col];
        }
 
        public Object getValueAt(int row, int col) {
            return data[row][col];
        }
   }
    private void updateUserSongList(){
         userLibraryTable.setModel(new UserSongListModel());
     }
    
    public class CloudSongListModel extends AbstractTableModel {
    	CloudSongListModel(){
    		 List<String[]> tempColList = new ArrayList<String[]>();
    	        List<SongInfo> cloudSongs = library.getCloudSongInfos();
    	            //run a for loop over the PlayableCollection's iterable to get all the songs
    	        for (SongInfo info : cloudSongs) {
    	             //process each song here
    	             List<String> tempRowList = new ArrayList<String>();
    	             tempRowList.add(info.getName());
    	             tempRowList.add(info.getArtist());
    	             tempRowList.add(info.getId());
    	             tempColList.add(tempRowList.toArray(new String[tempRowList.size()]));
    	        }
    	        allSongs = new Object[tempColList.size()][3];
    	        for (int i=0;i<tempColList.size();i++) {
    	            allSongs[i] = tempColList.get(i);
    	        }
	        data = allSongs;
        }
    	  
	    private Object[][] data;
	    private String[] columnNames = {
	    		"Song", "Artist", "Song ID"
	   };
	
        public int getColumnCount() {
            return columnNames.length;
        }
	
        public int getRowCount() {
            return data.length;
        }
 
        public String getColumnName(int col) {
            return columnNames[col];
        }
 
        public Object getValueAt(int row, int col) {
            return data[row][col];
        }
 
    }
    
    private void updateCloudSongList(){     
    	cloudLibraryTable.setModel(new CloudSongListModel());
     }
    
    	//action event handlers
    private class CloudSelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent event) {
            if (event.getValueIsAdjusting()) {
                return;
            }
            cloudSelection = cloudLibraryTable.getSelectedRows();
        }
    }
    
    private class UserSelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent event) {
            if (event.getValueIsAdjusting()) {
                return;
            }
            userSelection = userLibraryTable.getSelectedRows();
        }
    } 
    
    private class PLSelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent event) {
            if (event.getValueIsAdjusting()) {
                return;
            }
            PLSelection = PLTable.getSelectedRows();
        }
    } 

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        System.exit(0);
        this.dispose();
    }//GEN-LAST:event_jMenuItem1ActionPerformed
    


    private void playAction(java.awt.event.ActionEvent evt) {
//    	cloudLibraryTable.setRowSelectionInterval(0, 0);//GEN-FIRST:event_playAction
    	if (libSelection.getSelectedIndex() == 0) play(userSelection);
    	else play(PLSelection);
    	

    }//GEN-LAST:event_playAction

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
    	if (cloudSelection == null) {
    		JFrame frame = new JFrame("");
    		JOptionPane.showMessageDialog(frame,
	                "Please select songs to download first.",
	                "Error!",
	                JOptionPane.ERROR_MESSAGE);
    		return;
    	}
        for (int i : cloudSelection) {
        	String[] attributes = new String[3];
        	for (int j=0; j<3; j++){
        		String info = cloudLibraryTable.getModel().getValueAt(i, j).toString();
        		attributes[j] = info;
        	}
        	Cloud.downloadSong(user, new SongInfo(attributes[2], attributes[0], attributes[1]));
        }
        //update the library then the userSongList to reflect changes
        library = new Library(user);
        updateUserSongList();
        
    }//GEN-LAST:event_jButton5ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GUI().setVisible(true);
            }
        });
    }
    private javax.swing.JButton playSong;
    private javax.swing.JButton stopSong;
    private javax.swing.JButton downloadSong;
    private javax.swing.JLabel cloudLibLabel;
    private javax.swing.JLabel userLibLabel;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JScrollPane cloudLibScroll;
    private javax.swing.JScrollPane userLibScroll;
    private javax.swing.JTable cloudLibraryTable;
    private javax.swing.JTable userLibraryTable;
    private JMenuItem logIn;
    private JMenuItem logOut;
    private JMenuItem userGuide;
    private JMenuItem createUser;
    private JTabbedPane tabbedPane;
    private JScrollPane PLScroll;
    private JTable PLTable;
    private JButton addSongPL;
    private JToggleButton playMode;
    private JLabel lblPlayFromWhich;
    private JSlider progress; 
    private JLabel time;
    private JComboBox libSelection;
    private JLabel songLabel;
    private JToggleButton pauseSong;
    private JToggleButton shuffleToggle;
    private JLabel playlistLbl;
    private JComboBox playlistSelection;
    private JButton newPlaylist;
}
