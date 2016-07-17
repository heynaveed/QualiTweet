/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Analysis.CompareUsers;
import Entity.User;
import Utils.BearerToken;
import Utils.JSONKeys;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;

/**
 *
 * @author Naveed PC
 */
public class Dashboard extends javax.swing.JPanel {
    
    private static final int starImageSize = 35;
    private static final DecimalFormat dF3 = new DecimalFormat();
    
    private final BearerToken bToken;
    private final ArrayList<JButton> toolbarButtons;
    private final ArrayList<JLabel> visualLabels;
    private final ArrayList<JLabel> topWordLabels;
    
    private User loggedInUser;
    private User searchedUser;
    private String username;
    private BufferedImage profileImage1;
    private BufferedImage profileImage2;
    private BufferedImage verifiedImage1;
    private BufferedImage verifiedImage2;
    private BufferedImage starImage;
    private JLabel profileImageLabel1;
    private JLabel profileImageLabel2;
    private JLabel verifiedImageLabel1;
    private JLabel verifiedImageLabel2;
    private JLabel labelPopularity1;
    private JLabel labelPopularity2;
    private JPanel popularityPanel1;
    private JPanel popularityPanel2;
    private JLabel labelRating1;
    private JLabel labelRating2;
    private boolean isLoggedIn;
    private boolean isSearchingAgain;
    private boolean isComparingAgain;
    private boolean lastUserWasVerified;
    private boolean lastLoginWasVerified;
    private final DateFormat dF1;
    private final DateFormat dF2;
    private final Random random;
    
    private int currentButton;

    /**
     * Creates new form Dashboard
     * @throws java.io.IOException
     */
    public Dashboard() throws IOException {
        initComponents();
        bToken = new BearerToken();
        username = "";
        toolbarButtons = new ArrayList<>();
        visualLabels = new ArrayList<>();
        topWordLabels = new ArrayList<>();
        isLoggedIn = false;
        isSearchingAgain = false;
        isComparingAgain = false;
        lastUserWasVerified = false;
        lastLoginWasVerified = false;
        dF1 = new SimpleDateFormat("dd-MM-yyyy");
        dF2 = new SimpleDateFormat("HH:mm:ss");
        dF3.setMaximumFractionDigits(1);
        random = new Random();
        currentButton = 0;
        
        jLabelLoggedInStatus.setText("Username not provided. Please provide a username to get started!");
        
        initSwingVariables();
        updateDateAndTime();
    }
    
    private void switchMainDisplayTo(JScrollPane newPane){
        jPanelMainDisplay.getComponent(0).setEnabled(true);
        jPanelMainDisplay.removeAll();
        jPanelMainDisplay.repaint();
        jPanelMainDisplay.revalidate();
        jPanelMainDisplay.add(newPane);
        jPanelMainDisplay.repaint();
        jPanelMainDisplay.revalidate();
    }
    
    private void initSwingVariables(){
        toolbarButtons.add(jButtonHome);
        toolbarButtons.add(jButtonProfile);
        toolbarButtons.add(jButtonSearchUser);
        toolbarButtons.add(jButtonCompareUsers);
        toolbarButtons.add(jButtonVisualizer);
        
        for(int i = 0; i < toolbarButtons.size(); i++){
            toolbarButtons.get(i).setEnabled(false);
        }
        
        jLabelloginsuccessful.setVisible(false);
        jLabelpleaseusethetoolbarabovetonavigate.setVisible(false);
        jLabelincorrectusernamepleasetryagain.setVisible(false);
        jPanelResults.setVisible(false);
    }
    
    private void updateDateAndTime(){
        Thread clockTicker;
        clockTicker = new Thread(){
            @Override
            public void run(){
                for(;;){                                    
                    jLabelDate.setText(dF1.format(Calendar.getInstance().getTime()));
                    jLabelTime.setText(dF2.format(Calendar.getInstance().getTime()));
                    try {
                        sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
        
        clockTicker.start();
    }

    private void updateLoginStatus(boolean isLoggingIn){
        
        for(int i = 1; i < toolbarButtons.size(); i++){
            toolbarButtons.get(i).setEnabled(isLoggingIn);
        }
        
        jTextFieldLogin.setEnabled(!isLoggingIn);
        jLabelloginsuccessful.setVisible(isLoggingIn);
        jLabelpleaseusethetoolbarabovetonavigate.setVisible(isLoggingIn);
        jLabelincorrectusernamepleasetryagain.setVisible(!isLoggingIn);
        
        if(isLoggingIn){
            jLabelLoggedInStatus.setText("Logged in as: " + jTextFieldLogin.getText());
            jButtonLogin.setText("Logout");
            jLabelpleaseenteryourusernamebelow.setText("        You are now logged in.");
        }
        else{
            jLabelLoggedInStatus.setText("Username not provided. Please provide a username to get started!");
            jLabelpleaseenteryourusernamebelow.setText("Please enter your username below:");
            jLabelincorrectusernamepleasetryagain.setVisible(false);
            jButtonLogin.setText("Log In");
            jPanelProfile.remove(profileImageLabel1);
            jPanelProfile.remove(popularityPanel1);
            
            if(lastLoginWasVerified){
                jPanelProfile.remove(verifiedImageLabel1);
            }
            jPanelProfile.repaint();
            
            for(int i = visualLabels.size()-1; i >= 0; i--){
                jPanelVisualizer.remove(visualLabels.get(i));
                visualLabels.remove(i);
            }
            jPanelVisualizer.repaint();
        }
    }
    
    private void initProfile() throws IOException{
        loggedInUser = new User(username, bToken);
        
        popularityPanel1 = new JPanel();
        popularityPanel1.setBounds(jPanelProfile.getWidth()/2-160, 390, 407, 173);
        popularityPanel1.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        popularityPanel1.setLayout(null);
        
        if(loggedInUser.getIsValidUsername()){
            
            labelRating1 = new JLabel();
            if(loggedInUser.getUserRating().getPopularity() < 10){
                labelRating1.setText("0" + loggedInUser.getUserRating().getPopularity() + "/10");
            }
            else{
                labelRating1.setText(loggedInUser.getUserRating().getPopularity() + "/10");
            }
            labelRating1.setFont(new Font("Tahoma", Font.BOLD, 36));
            labelRating1.setBounds(popularityPanel1.getWidth()/2 - 46, popularityPanel1.getHeight()/2 - 36, 125, 50);
            
            labelPopularity1 = new JLabel();
            labelPopularity1.setText("Popularity");
            labelPopularity1.setFont(new Font("Tahoma", Font.PLAIN, 24));
            labelPopularity1.setBounds(popularityPanel1.getWidth()/2 - 40, 5, 125, 40);
            
            starImage = ImageIO.read(new File("./src/Images/star.png"));
            Image starVerifiedImg = starImage.getScaledInstance(starImageSize, starImageSize, Image.SCALE_DEFAULT);
            JLabel starImageLabel;
            
            for(int i = 0; i < loggedInUser.getUserRating().getPopularity(); i++){
                starImageLabel = new JLabel(new ImageIcon(starVerifiedImg));
                starImageLabel.setBounds(30+(starImageSize*i), 120, starImageSize, starImageSize);
                popularityPanel1.add(starImageLabel);
            }
            
            profileImage1 = ImageIO.read(new URL(loggedInUser.getProfileInfo().get("profile_image_url").toString()));
            Image scaledProfileImg = profileImage1.getScaledInstance(100, 100, Image.SCALE_DEFAULT);
            profileImageLabel1 = new JLabel(new ImageIcon(scaledProfileImg));
            profileImageLabel1.setBounds(1175, 25, 100, 100);
            profileImageLabel1.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            
            if(loggedInUser.getProfileInfo().get("verified").toString().equals("true")){
                verifiedImage1 = ImageIO.read(new File("./src/Images/twitterVerifiedLogo.png"));
                Image scaledVerifiedImg = verifiedImage1.getScaledInstance(75, 75, Image.SCALE_DEFAULT);
                verifiedImageLabel1 = new JLabel(new ImageIcon(scaledVerifiedImg));
                verifiedImageLabel1.setBounds(1075, 37, 75, 75);
                jPanelProfile.add(verifiedImageLabel1);
                lastLoginWasVerified = true;
            }
            else{
                lastLoginWasVerified = false;
            }
            
            jLabelProfileHyperlink.setText(loggedInUser.getProfileInfo().get("url").toString());
            
            jLabelProfileUsername.setText("@" + loggedInUser.getUsername());
            jLabelProfileName.setText("Name: " + loggedInUser.getProfileInfo().get("name").toString());
            jLabelProfileDescription.setText("Biography: " + loggedInUser.getProfileInfo().get(JSONKeys.description.toString()).toString());
            jLabelProfileLocation.setText("Location: " + loggedInUser.getProfileInfo().get(JSONKeys.location.toString()).toString());

            jLabelProfileHyperlink.addMouseListener(new MouseAdapter(){
                public void mouseClicked(MouseEvent e){
                    if (Desktop.isDesktopSupported()) {
                        Desktop desktop = Desktop.getDesktop();
                        try {
                            URI uri = new URI(loggedInUser.getProfileInfo().get("url").toString());
                            desktop.browse(uri);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        } catch (URISyntaxException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });

            jLabelProfileTweets.setText("Tweets: " + loggedInUser.getProfileInfo().get(JSONKeys.tweetCount.toString()).toString());
            jLabelProfileFollowing.setText("Following: " + loggedInUser.getProfileInfo().get(JSONKeys.followingCount.toString()).toString());
            jLabelProfileFollowers.setText("Followers: " + loggedInUser.getProfileInfo().get(JSONKeys.followersCount.toString()).toString());
            jLabelProfileLikes.setText("Likes: " + loggedInUser.getProfileInfo().get(JSONKeys.likes.toString()).toString());
            jLabelProfileAverageWordCount.setText("AVG. Word Count: " + dF3.format(loggedInUser.getTweetStatistics().getAveragesList().get(1)));
            jLabelProfileAverageSpamWords.setText("AVG. Spam Words: " + dF3.format(loggedInUser.getTweetStatistics().getAveragesList().get(2)));
            jLabelProfileAverageHashtags.setText("AVG. Hashtags: " + dF3.format(loggedInUser.getTweetStatistics().getAveragesList().get(5)));
            jLabelProfileAverageMentions.setText("AVG. Mentions: " + dF3.format(loggedInUser.getTweetStatistics().getAveragesList().get(6)));
            jLabelProfileDateCreated.setText("Date Created: " + loggedInUser.getProfileInfo().get("created_at").toString());
            jLabelProfileTimeZone.setText("Time Zone: " + loggedInUser.getProfileInfo().get("time_zone").toString());
            jLabelProfileVerified.setText("Verified: " + loggedInUser.getProfileInfo().get("verified").toString());
            jLabelProfileLanguage.setText("Language: " + loggedInUser.getProfileInfo().get("lang").toString());
            
            popularityPanel1.add(labelPopularity1);
            popularityPanel1.add(labelRating1);
            jPanelProfile.add(profileImageLabel1);
            jPanelProfile.add(popularityPanel1);
            jPanelProfile.add(jLabelProfileHyperlink);
            jPanelProfile.repaint();    
            jPanelProfile.add(popularityPanel1);
        }
    }
    
    private void initVisualizer(){
        
        int randomX = 0;
        int randomY = 0;
        int count = 0;
        
        JLabel label;
        for (Map.Entry pair : loggedInUser.getTextVisualizer().getVisualMap().entrySet()) {
            
            randomX = random.nextInt(1000) + 5;
            randomY = random.nextInt(350) + 5;        
            
            for(int i = 0; i < visualLabels.size(); i++){
                if(Math.abs(randomX - visualLabels.get(i).getX()) < 250){
                    randomX = random.nextInt(1000) + 5;
                    i = 0;
                }
                
                else if(Math.abs(randomY - visualLabels.get(i).getY()) < 125){
                    randomY = random.nextInt(350) + 5;
                    i = 0;
                }
                
                count++;
                
                if(count > 50000){
                    break;
                }
            }
            
            label = new JLabel();
            label.setText(pair.getKey().toString());
            label.setFont(new Font("Tahoma", Font.BOLD, (int)pair.getValue()));
            label.setBounds(randomX, randomY, 1300, 300);
            label.setForeground(new Color(244, (int)pair.getValue(), 32 + (int)pair.getValue()));
            visualLabels.add(label);
        }
        
        for(int i = 0; i < visualLabels.size(); i++){
            jPanelVisualizer.add(visualLabels.get(i));
        }
        
        jPanelVisualizer.repaint();
    }
    
    private void initSearchUser(String username) throws IOException{
        
        if(isSearchingAgain){
            jPanelSearchUser.remove(profileImageLabel2);
            jPanelSearchUser.remove(popularityPanel2);
            
            if(lastUserWasVerified){
                jPanelSearchUser.remove(verifiedImageLabel2);
            }
            jPanelSearchUser.repaint();
        }
        
        searchedUser = new User(username, bToken);
        
        popularityPanel2 = new JPanel();
        popularityPanel2.setBounds(jPanelSearchUser.getWidth()/2-160, 390, 407, 173);
        popularityPanel2.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        popularityPanel2.setLayout(null);
        
        if(searchedUser.getIsValidUsername()){
            
            labelRating2 = new JLabel();
            if(searchedUser.getUserRating().getPopularity() < 10){
                labelRating2.setText("0" + searchedUser.getUserRating().getPopularity() + "/10");
            }
            else{
                labelRating2.setText(searchedUser.getUserRating().getPopularity() + "/10");
            }
            labelRating2.setFont(new Font("Tahoma", Font.BOLD, 36));
            labelRating2.setBounds(popularityPanel2.getWidth()/2 - 46, popularityPanel2.getHeight()/2 - 36, 125, 50);
            
            labelPopularity2 = new JLabel();
            labelPopularity2.setText("Popularity");
            labelPopularity2.setFont(new Font("Tahoma", Font.PLAIN, 24));
            labelPopularity2.setBounds(popularityPanel2.getWidth()/2 - 40, 5, 125, 40);
            
            starImage = ImageIO.read(new File("./src/Images/star.png"));
            Image starVerifiedImg = starImage.getScaledInstance(starImageSize, starImageSize, Image.SCALE_DEFAULT);
            JLabel starImageLabel;
            
            for(int i = 0; i < searchedUser.getUserRating().getPopularity(); i++){
                starImageLabel = new JLabel(new ImageIcon(starVerifiedImg));
                starImageLabel.setBounds(30+(starImageSize*i), 120, starImageSize, starImageSize);
                popularityPanel2.add(starImageLabel);
            }
            
            profileImage2 = ImageIO.read(new URL(searchedUser.getProfileInfo().get("profile_image_url").toString()));
            Image scaledProfileImg = profileImage2.getScaledInstance(100, 100, Image.SCALE_DEFAULT);
            profileImageLabel2 = new JLabel(new ImageIcon(scaledProfileImg));
            profileImageLabel2.setBounds(1175, 25, 100, 100);
            profileImageLabel2.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            
            if(searchedUser.getProfileInfo().get("verified").toString().equals("true")){
                verifiedImage2 = ImageIO.read(new File("./src/Images/twitterVerifiedLogo.png"));
                Image scaledVerifiedImg = verifiedImage2.getScaledInstance(75, 75, Image.SCALE_DEFAULT);
                verifiedImageLabel2 = new JLabel(new ImageIcon(scaledVerifiedImg));
                verifiedImageLabel2.setBounds(1075, 37, 75, 75);
                jPanelSearchUser.add(verifiedImageLabel2);
                lastUserWasVerified = true;
            }
            else{
                lastUserWasVerified = false;
            }
            
            jLabelSearchHyperlink.setText(searchedUser.getProfileInfo().get("url").toString());
            
            jLabelSearchUsername.setText("@" + searchedUser.getUsername());
            jLabelSearchName.setText("Name: " + searchedUser.getProfileInfo().get("name").toString());
            jLabelSearchDescription.setText("Biography: " + searchedUser.getProfileInfo().get(JSONKeys.description.toString()).toString());
            jLabelSearchLocation.setText("Location: " + searchedUser.getProfileInfo().get(JSONKeys.location.toString()).toString());

            jLabelSearchHyperlink.addMouseListener(new MouseAdapter(){
                public void mouseClicked(MouseEvent e){
                    if (Desktop.isDesktopSupported()) {
                        Desktop desktop = Desktop.getDesktop();
                        try {
                            URI uri = new URI(searchedUser.getProfileInfo().get("url").toString());
                            desktop.browse(uri);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        } catch (URISyntaxException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });

            jLabelSearchTweets.setText("Tweets: " + searchedUser.getProfileInfo().get(JSONKeys.tweetCount.toString()).toString());
            jLabelSearchFollowing.setText("Following: " + searchedUser.getProfileInfo().get(JSONKeys.followingCount.toString()).toString());
            jLabelSearchFollowers.setText("Followers: " + searchedUser.getProfileInfo().get(JSONKeys.followersCount.toString()).toString());
            jLabelSearchLikes.setText("Likes: " + searchedUser.getProfileInfo().get(JSONKeys.likes.toString()).toString());
            jLabelProfileAverageWordCount1.setText("AVG. Word Count: " + dF3.format(searchedUser.getTweetStatistics().getAveragesList().get(1)));
            jLabelProfileAverageSpamWords1.setText("AVG. Spam Words: " + dF3.format(searchedUser.getTweetStatistics().getAveragesList().get(2)));
            jLabelProfileAverageHashtags1.setText("AVG. Hashtags: " + dF3.format(searchedUser.getTweetStatistics().getAveragesList().get(5)));
            jLabelProfileAverageMentions1.setText("AVG. Mentions: " + dF3.format(searchedUser.getTweetStatistics().getAveragesList().get(6)));
            jLabelSearchDateCreated.setText("Date Created: " + searchedUser.getProfileInfo().get("created_at").toString());
            jLabelSearchTimeZone.setText("Time Zone: " + searchedUser.getProfileInfo().get("time_zone").toString());
            jLabelSearchVerified.setText("Verified: " + searchedUser.getProfileInfo().get("verified").toString());
            jLabelSearchLanguage.setText("Language: " + searchedUser.getProfileInfo().get("lang").toString());
            
            popularityPanel2.add(labelPopularity2);
            popularityPanel2.add(labelRating2);
            jPanelSearchUser.add(profileImageLabel2);
            jPanelSearchUser.add(popularityPanel2);
            jPanelSearchUser.add(jLabelSearchHyperlink);
            jPanelSearchUser.repaint();
            jPanelSearchUser.add(popularityPanel2);
            
            jTextFieldLogin.setText("");
            isSearchingAgain = true;
        }
    }
    
    /**
     * This method is called from within the constructor to initialise the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelInfoBar = new javax.swing.JPanel();
        jLabelTime = new javax.swing.JLabel();
        jLabelDate = new javax.swing.JLabel();
        jLabelLoggedInStatus = new javax.swing.JLabel();
        jPanelToolbar = new javax.swing.JPanel();
        jButtonHome = new javax.swing.JButton();
        jButtonProfile = new javax.swing.JButton();
        jButtonSearchUser = new javax.swing.JButton();
        jButtonCompareUsers = new javax.swing.JButton();
        jButtonVisualizer = new javax.swing.JButton();
        jPanelMainDisplay = new javax.swing.JPanel();
        jScrollPaneHome = new javax.swing.JScrollPane();
        jPanelHome = new javax.swing.JPanel();
        jLabelpleaseenteryourusernamebelow = new javax.swing.JLabel();
        jLabelthankyouforusingqualitweeter = new javax.swing.JLabel();
        jTextFieldLogin = new javax.swing.JTextField();
        jButtonLogin = new javax.swing.JButton();
        jLabelpleaseusethetoolbarabovetonavigate = new javax.swing.JLabel();
        jLabelloginsuccessful = new javax.swing.JLabel();
        jLabelincorrectusernamepleasetryagain = new javax.swing.JLabel();
        jScrollPaneProfile = new javax.swing.JScrollPane();
        jPanelProfile = new javax.swing.JPanel();
        jLabelProfileUsername = new javax.swing.JLabel();
        jLabelProfileName = new javax.swing.JLabel();
        jLabelProfileDescription = new javax.swing.JLabel();
        jLabelProfileLocation = new javax.swing.JLabel();
        jLabelProfileWebsite = new javax.swing.JLabel();
        jLabelProfileHyperlink = new javax.swing.JLabel();
        jLabelProfileTweets = new javax.swing.JLabel();
        jLabelProfileFollowing = new javax.swing.JLabel();
        jLabelProfileFollowers = new javax.swing.JLabel();
        jLabelProfileLikes = new javax.swing.JLabel();
        jLabelProfileName1 = new javax.swing.JLabel();
        jLabelProfileName2 = new javax.swing.JLabel();
        jLabelProfileName3 = new javax.swing.JLabel();
        jLabelProfileDateCreated = new javax.swing.JLabel();
        jLabelProfileTimeZone = new javax.swing.JLabel();
        jLabelProfileVerified = new javax.swing.JLabel();
        jLabelProfileLanguage = new javax.swing.JLabel();
        jLabelProfileAverageWordCount = new javax.swing.JLabel();
        jLabelProfileAverageSpamWords = new javax.swing.JLabel();
        jLabelProfileAverageHashtags = new javax.swing.JLabel();
        jLabelProfileAverageMentions = new javax.swing.JLabel();
        jScrollPaneSearchUser = new javax.swing.JScrollPane();
        jPanelSearchUser = new javax.swing.JPanel();
        jLabelSearchUsername = new javax.swing.JLabel();
        jLabelSearchName = new javax.swing.JLabel();
        jLabelSearchDescription = new javax.swing.JLabel();
        jLabelSearchLocation = new javax.swing.JLabel();
        jLabelSearchWebsite = new javax.swing.JLabel();
        jLabelSearchHyperlink = new javax.swing.JLabel();
        jLabelSearchTweets = new javax.swing.JLabel();
        jLabelSearchFollowing = new javax.swing.JLabel();
        jLabelSearchFollowers = new javax.swing.JLabel();
        jLabelSearchLikes = new javax.swing.JLabel();
        jLabelProfileName5 = new javax.swing.JLabel();
        jLabelProfileName6 = new javax.swing.JLabel();
        jLabelProfileName7 = new javax.swing.JLabel();
        jLabelSearchDateCreated = new javax.swing.JLabel();
        jLabelSearchTimeZone = new javax.swing.JLabel();
        jLabelSearchVerified = new javax.swing.JLabel();
        jLabelSearchLanguage = new javax.swing.JLabel();
        jTextFieldSearchUser = new javax.swing.JTextField();
        jButtonSearchUser2 = new javax.swing.JButton();
        jLabelProfileAverageWordCount1 = new javax.swing.JLabel();
        jLabelProfileAverageSpamWords1 = new javax.swing.JLabel();
        jLabelProfileAverageHashtags1 = new javax.swing.JLabel();
        jLabelProfileAverageMentions1 = new javax.swing.JLabel();
        jScrollPaneCompareUsers = new javax.swing.JScrollPane();
        jPanelCompareUsers = new javax.swing.JPanel();
        jLabelCompareUsersTitle = new javax.swing.JLabel();
        jPanelResults = new javax.swing.JPanel();
        jLabelResults = new javax.swing.JLabel();
        jLabelResultsthisdoesnot = new javax.swing.JLabel();
        jPanelLeft = new javax.swing.JPanel();
        jLabelWordMatchTitle = new javax.swing.JLabel();
        jPanelRight = new javax.swing.JPanel();
        jLabelWordMatchTitle1 = new javax.swing.JLabel();
        jLabelCPT1 = new javax.swing.JLabel();
        jLabelCPT2 = new javax.swing.JLabel();
        jLabelPop1 = new javax.swing.JLabel();
        jLabelPop2 = new javax.swing.JLabel();
        jTextFieldUser1 = new javax.swing.JTextField();
        jTextFieldUser2 = new javax.swing.JTextField();
        jButtonCompare = new javax.swing.JButton();
        jScrollPaneVisualizer = new javax.swing.JScrollPane();
        jPanelVisualizer = new javax.swing.JPanel();

        setPreferredSize(new java.awt.Dimension(1333, 750));

        jPanelInfoBar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabelTime.setText("Time");

        jLabelDate.setText("Date");

        jLabelLoggedInStatus.setText("Login Status");

        javax.swing.GroupLayout jPanelInfoBarLayout = new javax.swing.GroupLayout(jPanelInfoBar);
        jPanelInfoBar.setLayout(jPanelInfoBarLayout);
        jPanelInfoBarLayout.setHorizontalGroup(
            jPanelInfoBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelInfoBarLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelLoggedInStatus)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabelTime)
                .addGap(18, 18, 18)
                .addComponent(jLabelDate)
                .addContainerGap())
        );
        jPanelInfoBarLayout.setVerticalGroup(
            jPanelInfoBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelInfoBarLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanelInfoBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelTime)
                    .addComponent(jLabelDate)
                    .addComponent(jLabelLoggedInStatus))
                .addContainerGap())
        );

        jPanelToolbar.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        jButtonHome.setText("Home");
        jButtonHome.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jButtonHome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonHomeActionPerformed(evt);
            }
        });

        jButtonProfile.setText("Profile");
        jButtonProfile.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jButtonProfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonProfileActionPerformed(evt);
            }
        });

        jButtonSearchUser.setText("Search User");
        jButtonSearchUser.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jButtonSearchUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSearchUserActionPerformed(evt);
            }
        });

        jButtonCompareUsers.setText("Compare Users");
        jButtonCompareUsers.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jButtonCompareUsers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCompareUsersActionPerformed(evt);
            }
        });

        jButtonVisualizer.setText("Visualizer");
        jButtonVisualizer.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jButtonVisualizer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonVisualizerActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelToolbarLayout = new javax.swing.GroupLayout(jPanelToolbar);
        jPanelToolbar.setLayout(jPanelToolbarLayout);
        jPanelToolbarLayout.setHorizontalGroup(
            jPanelToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelToolbarLayout.createSequentialGroup()
                .addComponent(jButtonHome, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonProfile, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonSearchUser, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonCompareUsers, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonVisualizer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelToolbarLayout.setVerticalGroup(
            jPanelToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelToolbarLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanelToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonHome, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonProfile, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonSearchUser, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonCompareUsers, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addComponent(jButtonVisualizer, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanelMainDisplay.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanelMainDisplay.setLayout(new java.awt.CardLayout());

        jPanelHome.setMaximumSize(new java.awt.Dimension(1318, 613));
        jPanelHome.setMinimumSize(new java.awt.Dimension(1318, 613));

        jLabelpleaseenteryourusernamebelow.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabelpleaseenteryourusernamebelow.setText("Please enter your username below:");

        jLabelthankyouforusingqualitweeter.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabelthankyouforusingqualitweeter.setText("Thank you for using QualiTweet.");

        jTextFieldLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldLoginActionPerformed(evt);
            }
        });

        jButtonLogin.setText("Login");
        jButtonLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLoginActionPerformed(evt);
            }
        });

        jLabelpleaseusethetoolbarabovetonavigate.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabelpleaseusethetoolbarabovetonavigate.setText("Please use the toolbar above to navigate.");

        jLabelloginsuccessful.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabelloginsuccessful.setText("Click above to log out.");

        jLabelincorrectusernamepleasetryagain.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabelincorrectusernamepleasetryagain.setText("Incorrect username, please try again.");

        javax.swing.GroupLayout jPanelHomeLayout = new javax.swing.GroupLayout(jPanelHome);
        jPanelHome.setLayout(jPanelHomeLayout);
        jPanelHomeLayout.setHorizontalGroup(
            jPanelHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelHomeLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanelHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelHomeLayout.createSequentialGroup()
                        .addComponent(jLabelpleaseenteryourusernamebelow, javax.swing.GroupLayout.PREFERRED_SIZE, 404, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(426, 426, 426))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelHomeLayout.createSequentialGroup()
                        .addComponent(jLabelpleaseusethetoolbarabovetonavigate, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(472, 472, 472))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelHomeLayout.createSequentialGroup()
                .addContainerGap(424, Short.MAX_VALUE)
                .addGroup(jPanelHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelHomeLayout.createSequentialGroup()
                        .addGroup(jPanelHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jTextFieldLogin)
                            .addComponent(jButtonLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(527, 527, 527))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelHomeLayout.createSequentialGroup()
                        .addComponent(jLabelincorrectusernamepleasetryagain, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(483, 483, 483))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelHomeLayout.createSequentialGroup()
                        .addComponent(jLabelthankyouforusingqualitweeter, javax.swing.GroupLayout.PREFERRED_SIZE, 559, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(335, 335, 335))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelHomeLayout.createSequentialGroup()
                        .addComponent(jLabelloginsuccessful, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(514, 514, 514))))
        );
        jPanelHomeLayout.setVerticalGroup(
            jPanelHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelHomeLayout.createSequentialGroup()
                .addGap(90, 90, 90)
                .addComponent(jLabelthankyouforusingqualitweeter)
                .addGap(72, 72, 72)
                .addComponent(jLabelpleaseenteryourusernamebelow)
                .addGap(35, 35, 35)
                .addComponent(jTextFieldLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonLogin)
                .addGap(42, 42, 42)
                .addComponent(jLabelincorrectusernamepleasetryagain)
                .addGap(18, 18, 18)
                .addComponent(jLabelloginsuccessful)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelpleaseusethetoolbarabovetonavigate)
                .addContainerGap(265, Short.MAX_VALUE))
        );

        jScrollPaneHome.setViewportView(jPanelHome);

        jPanelMainDisplay.add(jScrollPaneHome, "card2");

        jPanelProfile.setMaximumSize(new java.awt.Dimension(1311, 732));
        jPanelProfile.setMinimumSize(new java.awt.Dimension(1311, 732));

        jLabelProfileUsername.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        jLabelProfileUsername.setText("Username");

        jLabelProfileName.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabelProfileName.setText("Name");

        jLabelProfileDescription.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabelProfileDescription.setText("Description");

        jLabelProfileLocation.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabelProfileLocation.setText("Location");

        jLabelProfileWebsite.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabelProfileWebsite.setText("Website:");

        jLabelProfileHyperlink.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabelProfileHyperlink.setText("Website");

        jLabelProfileTweets.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabelProfileTweets.setText("Tweets");

        jLabelProfileFollowing.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabelProfileFollowing.setText("Following");

        jLabelProfileFollowers.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabelProfileFollowers.setText("Followers");

        jLabelProfileLikes.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabelProfileLikes.setText("Likes");

        jLabelProfileName1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabelProfileName1.setText("Profile");

        jLabelProfileName2.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabelProfileName2.setText("Statistics");

        jLabelProfileName3.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabelProfileName3.setText("Other");

        jLabelProfileDateCreated.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabelProfileDateCreated.setText("Date Created");

        jLabelProfileTimeZone.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabelProfileTimeZone.setText("Time Zone");

        jLabelProfileVerified.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabelProfileVerified.setText("Verified");

        jLabelProfileLanguage.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabelProfileLanguage.setText("Language");

        jLabelProfileAverageWordCount.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabelProfileAverageWordCount.setText("Avg. Word Count");

        jLabelProfileAverageSpamWords.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabelProfileAverageSpamWords.setText("Avg. Spam Words");

        jLabelProfileAverageHashtags.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabelProfileAverageHashtags.setText("Avg. Hashtags");

        jLabelProfileAverageMentions.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabelProfileAverageMentions.setText("Avg. Mentions");

        javax.swing.GroupLayout jPanelProfileLayout = new javax.swing.GroupLayout(jPanelProfile);
        jPanelProfile.setLayout(jPanelProfileLayout);
        jPanelProfileLayout.setHorizontalGroup(
            jPanelProfileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelProfileLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanelProfileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelProfileLayout.createSequentialGroup()
                        .addGroup(jPanelProfileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelProfileLanguage)
                            .addComponent(jLabelProfileVerified)
                            .addComponent(jLabelProfileTimeZone)
                            .addComponent(jLabelProfileDateCreated)
                            .addComponent(jLabelProfileName3)
                            .addGroup(jPanelProfileLayout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addGroup(jPanelProfileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanelProfileLayout.createSequentialGroup()
                                        .addComponent(jLabelProfileWebsite)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabelProfileHyperlink, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabelProfileLocation)
                                    .addComponent(jLabelProfileName)
                                    .addComponent(jLabelProfileDescription)
                                    .addComponent(jLabelProfileName1))))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanelProfileLayout.createSequentialGroup()
                        .addGroup(jPanelProfileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelProfileName2)
                            .addComponent(jLabelProfileUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 1247, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanelProfileLayout.createSequentialGroup()
                                .addGroup(jPanelProfileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabelProfileTweets)
                                    .addComponent(jLabelProfileFollowing)
                                    .addComponent(jLabelProfileFollowers)
                                    .addComponent(jLabelProfileLikes))
                                .addGap(89, 89, 89)
                                .addGroup(jPanelProfileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabelProfileAverageWordCount)
                                    .addComponent(jLabelProfileAverageSpamWords)
                                    .addComponent(jLabelProfileAverageHashtags)
                                    .addComponent(jLabelProfileAverageMentions))))
                        .addGap(0, 52, Short.MAX_VALUE))))
        );
        jPanelProfileLayout.setVerticalGroup(
            jPanelProfileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelProfileLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabelProfileUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60)
                .addComponent(jLabelProfileName1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelProfileName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelProfileDescription)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelProfileLocation)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelProfileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelProfileWebsite)
                    .addComponent(jLabelProfileHyperlink))
                .addGap(24, 24, 24)
                .addComponent(jLabelProfileName2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelProfileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelProfileLayout.createSequentialGroup()
                        .addComponent(jLabelProfileTweets)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelProfileFollowing)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelProfileFollowers)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelProfileLikes))
                    .addGroup(jPanelProfileLayout.createSequentialGroup()
                        .addComponent(jLabelProfileAverageWordCount)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelProfileAverageSpamWords)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelProfileAverageHashtags)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelProfileAverageMentions)))
                .addGap(18, 18, 18)
                .addComponent(jLabelProfileName3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelProfileDateCreated)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelProfileTimeZone)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelProfileVerified)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelProfileLanguage)
                .addContainerGap(177, Short.MAX_VALUE))
        );

        jScrollPaneProfile.setViewportView(jPanelProfile);

        jPanelMainDisplay.add(jScrollPaneProfile, "card3");

        jPanelSearchUser.setMaximumSize(new java.awt.Dimension(1311, 732));
        jPanelSearchUser.setMinimumSize(new java.awt.Dimension(1311, 732));

        jLabelSearchUsername.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        jLabelSearchUsername.setText("@");

        jLabelSearchName.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabelSearchName.setText("Name: N/A");

        jLabelSearchDescription.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabelSearchDescription.setText("Description: N/A");

        jLabelSearchLocation.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabelSearchLocation.setText("Location: N/A");

        jLabelSearchWebsite.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabelSearchWebsite.setText("Website:");

        jLabelSearchHyperlink.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabelSearchHyperlink.setText("N/A");

        jLabelSearchTweets.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabelSearchTweets.setText("Tweets: N/A");

        jLabelSearchFollowing.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabelSearchFollowing.setText("Following: N/A");

        jLabelSearchFollowers.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabelSearchFollowers.setText("Followers: N/A");

        jLabelSearchLikes.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabelSearchLikes.setText("Likes: N/A");

        jLabelProfileName5.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabelProfileName5.setText("Profile");

        jLabelProfileName6.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabelProfileName6.setText("Statistics");

        jLabelProfileName7.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabelProfileName7.setText("Other");

        jLabelSearchDateCreated.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabelSearchDateCreated.setText("Date Created: N/A");

        jLabelSearchTimeZone.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabelSearchTimeZone.setText("Time Zone: N/A");

        jLabelSearchVerified.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabelSearchVerified.setText("Verified: N/A");

        jLabelSearchLanguage.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabelSearchLanguage.setText("Language: N/A");

        jTextFieldSearchUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldSearchUserActionPerformed(evt);
            }
        });

        jButtonSearchUser2.setText("Search");
        jButtonSearchUser2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSearchUser2ActionPerformed(evt);
            }
        });

        jLabelProfileAverageWordCount1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabelProfileAverageWordCount1.setText("Avg. Word Count");

        jLabelProfileAverageSpamWords1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabelProfileAverageSpamWords1.setText("Avg. Spam Words");

        jLabelProfileAverageHashtags1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabelProfileAverageHashtags1.setText("Avg. Hashtags");

        jLabelProfileAverageMentions1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabelProfileAverageMentions1.setText("Avg. Mentions");

        javax.swing.GroupLayout jPanelSearchUserLayout = new javax.swing.GroupLayout(jPanelSearchUser);
        jPanelSearchUser.setLayout(jPanelSearchUserLayout);
        jPanelSearchUserLayout.setHorizontalGroup(
            jPanelSearchUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSearchUserLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanelSearchUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelSearchUserLayout.createSequentialGroup()
                        .addGroup(jPanelSearchUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelSearchUserLayout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addGroup(jPanelSearchUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanelSearchUserLayout.createSequentialGroup()
                                        .addComponent(jLabelSearchWebsite)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabelSearchHyperlink))
                                    .addComponent(jLabelSearchLocation)
                                    .addComponent(jLabelSearchName)
                                    .addComponent(jLabelSearchDescription)
                                    .addComponent(jLabelProfileName5)))
                            .addComponent(jLabelSearchLanguage)
                            .addComponent(jLabelSearchVerified)
                            .addComponent(jLabelSearchTimeZone)
                            .addComponent(jLabelSearchDateCreated)
                            .addComponent(jLabelProfileName7))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanelSearchUserLayout.createSequentialGroup()
                        .addGroup(jPanelSearchUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelProfileName6)
                            .addComponent(jLabelSearchUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 1247, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanelSearchUserLayout.createSequentialGroup()
                                .addGroup(jPanelSearchUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabelSearchTweets)
                                    .addComponent(jLabelSearchFollowing)
                                    .addComponent(jLabelSearchFollowers)
                                    .addComponent(jLabelSearchLikes))
                                .addGap(60, 60, 60)
                                .addGroup(jPanelSearchUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabelProfileAverageWordCount1)
                                    .addComponent(jLabelProfileAverageSpamWords1)
                                    .addComponent(jLabelProfileAverageHashtags1)
                                    .addComponent(jLabelProfileAverageMentions1))))
                        .addGap(0, 52, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelSearchUserLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jTextFieldSearchUser, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonSearchUser2)
                .addGap(520, 520, 520))
        );
        jPanelSearchUserLayout.setVerticalGroup(
            jPanelSearchUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSearchUserLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabelSearchUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelSearchUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldSearchUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonSearchUser2))
                .addGap(29, 29, 29)
                .addComponent(jLabelProfileName5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelSearchName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelSearchDescription)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelSearchLocation)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelSearchUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelSearchWebsite)
                    .addComponent(jLabelSearchHyperlink))
                .addGap(24, 24, 24)
                .addComponent(jLabelProfileName6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelSearchUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelSearchUserLayout.createSequentialGroup()
                        .addComponent(jLabelSearchTweets)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelSearchFollowing)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelSearchFollowers)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelSearchLikes))
                    .addGroup(jPanelSearchUserLayout.createSequentialGroup()
                        .addComponent(jLabelProfileAverageWordCount1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelProfileAverageSpamWords1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelProfileAverageHashtags1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelProfileAverageMentions1)))
                .addGap(18, 18, 18)
                .addComponent(jLabelProfileName7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelSearchDateCreated)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelSearchTimeZone)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelSearchVerified)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelSearchLanguage)
                .addContainerGap())
        );

        jScrollPaneSearchUser.setViewportView(jPanelSearchUser);

        jPanelMainDisplay.add(jScrollPaneSearchUser, "card4");

        jLabelCompareUsersTitle.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        jLabelCompareUsersTitle.setText("Compare Users");

        jPanelResults.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabelResults.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabelResults.setText("Results are based on their last xxx Tweets");

        jLabelResultsthisdoesnot.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabelResultsthisdoesnot.setText("This does NOT include retweets and replies.");

        jPanelLeft.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabelWordMatchTitle.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabelWordMatchTitle.setText("Top Word Matches");

        javax.swing.GroupLayout jPanelLeftLayout = new javax.swing.GroupLayout(jPanelLeft);
        jPanelLeft.setLayout(jPanelLeftLayout);
        jPanelLeftLayout.setHorizontalGroup(
            jPanelLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLeftLayout.createSequentialGroup()
                .addGap(90, 90, 90)
                .addComponent(jLabelWordMatchTitle)
                .addContainerGap(91, Short.MAX_VALUE))
        );
        jPanelLeftLayout.setVerticalGroup(
            jPanelLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLeftLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelWordMatchTitle)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelRight.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabelWordMatchTitle1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabelWordMatchTitle1.setText("Miscellaneous");

        jLabelCPT1.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabelCPT1.setText("CPT1:");

        jLabelCPT2.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabelCPT2.setText("CPT2:");

        jLabelPop1.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabelPop1.setText("Pop1:");

        jLabelPop2.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabelPop2.setText("Pop2:");

        javax.swing.GroupLayout jPanelRightLayout = new javax.swing.GroupLayout(jPanelRight);
        jPanelRight.setLayout(jPanelRightLayout);
        jPanelRightLayout.setHorizontalGroup(
            jPanelRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelRightLayout.createSequentialGroup()
                .addContainerGap(112, Short.MAX_VALUE)
                .addComponent(jLabelWordMatchTitle1, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(97, 97, 97))
            .addGroup(jPanelRightLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanelRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabelCPT1, javax.swing.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE)
                    .addComponent(jLabelCPT2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelPop1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelPop2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelRightLayout.setVerticalGroup(
            jPanelRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelRightLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelWordMatchTitle1)
                .addGap(18, 18, 18)
                .addComponent(jLabelCPT1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelCPT2)
                .addGap(18, 18, 18)
                .addComponent(jLabelPop1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelPop2)
                .addContainerGap(109, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanelResultsLayout = new javax.swing.GroupLayout(jPanelResults);
        jPanelResults.setLayout(jPanelResultsLayout);
        jPanelResultsLayout.setHorizontalGroup(
            jPanelResultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelResultsLayout.createSequentialGroup()
                .addGap(66, 66, 66)
                .addComponent(jPanelLeft, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(59, 59, 59)
                .addComponent(jPanelRight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(94, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelResultsLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanelResultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelResultsLayout.createSequentialGroup()
                        .addComponent(jLabelResults, javax.swing.GroupLayout.PREFERRED_SIZE, 373, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(223, 223, 223))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelResultsLayout.createSequentialGroup()
                        .addComponent(jLabelResultsthisdoesnot, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(287, 287, 287))))
        );
        jPanelResultsLayout.setVerticalGroup(
            jPanelResultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelResultsLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabelResults)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelResultsthisdoesnot)
                .addGap(42, 42, 42)
                .addGroup(jPanelResultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanelLeft, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelRight, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(31, Short.MAX_VALUE))
        );

        jTextFieldUser1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldUser1ActionPerformed(evt);
            }
        });

        jButtonCompare.setText("Compare");
        jButtonCompare.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCompareActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelCompareUsersLayout = new javax.swing.GroupLayout(jPanelCompareUsers);
        jPanelCompareUsers.setLayout(jPanelCompareUsersLayout);
        jPanelCompareUsersLayout.setHorizontalGroup(
            jPanelCompareUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCompareUsersLayout.createSequentialGroup()
                .addContainerGap(246, Short.MAX_VALUE)
                .addGroup(jPanelCompareUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelCompareUsersLayout.createSequentialGroup()
                        .addComponent(jTextFieldUser1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(221, 221, 221)
                        .addComponent(jTextFieldUser2, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(366, 366, 366))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelCompareUsersLayout.createSequentialGroup()
                        .addComponent(jButtonCompare, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(577, 577, 577))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelCompareUsersLayout.createSequentialGroup()
                        .addComponent(jPanelResults, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(225, 225, 225))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelCompareUsersLayout.createSequentialGroup()
                        .addComponent(jLabelCompareUsersTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 409, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(431, 431, 431))))
        );
        jPanelCompareUsersLayout.setVerticalGroup(
            jPanelCompareUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCompareUsersLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelCompareUsersTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addGroup(jPanelCompareUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldUser1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldUser2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jButtonCompare, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanelResults, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(145, Short.MAX_VALUE))
        );

        jScrollPaneCompareUsers.setViewportView(jPanelCompareUsers);

        jPanelMainDisplay.add(jScrollPaneCompareUsers, "card5");

        javax.swing.GroupLayout jPanelVisualizerLayout = new javax.swing.GroupLayout(jPanelVisualizer);
        jPanelVisualizer.setLayout(jPanelVisualizerLayout);
        jPanelVisualizerLayout.setHorizontalGroup(
            jPanelVisualizerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1318, Short.MAX_VALUE)
        );
        jPanelVisualizerLayout.setVerticalGroup(
            jPanelVisualizerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 732, Short.MAX_VALUE)
        );

        jScrollPaneVisualizer.setViewportView(jPanelVisualizer);

        jPanelMainDisplay.add(jScrollPaneVisualizer, "card6");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelInfoBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelToolbar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelMainDisplay, javax.swing.GroupLayout.DEFAULT_SIZE, 1313, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelToolbar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelMainDisplay, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanelInfoBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        getAccessibleContext().setAccessibleDescription("");
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonHomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonHomeActionPerformed
        toolbarButtons.get(currentButton).setEnabled(true);
        switchMainDisplayTo(jScrollPaneHome);
        toolbarButtons.get(0).setEnabled(false);
        currentButton = 0;
    }//GEN-LAST:event_jButtonHomeActionPerformed

    private void jButtonProfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonProfileActionPerformed
        toolbarButtons.get(currentButton).setEnabled(true);
        switchMainDisplayTo(jScrollPaneProfile);
        toolbarButtons.get(1).setEnabled(false);
        currentButton = 1;
    }//GEN-LAST:event_jButtonProfileActionPerformed

    private void jButtonSearchUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSearchUserActionPerformed
        toolbarButtons.get(currentButton).setEnabled(true);
        switchMainDisplayTo(jScrollPaneSearchUser);
        toolbarButtons.get(2).setEnabled(false);
        currentButton = 2;
    }//GEN-LAST:event_jButtonSearchUserActionPerformed

    private void jButtonCompareUsersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCompareUsersActionPerformed
        toolbarButtons.get(currentButton).setEnabled(true);
        switchMainDisplayTo(jScrollPaneCompareUsers);
        toolbarButtons.get(3).setEnabled(false);
        currentButton = 3;
    }//GEN-LAST:event_jButtonCompareUsersActionPerformed

    private void jButtonVisualizerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonVisualizerActionPerformed
        toolbarButtons.get(currentButton).setEnabled(true);
        switchMainDisplayTo(jScrollPaneVisualizer);
        toolbarButtons.get(4).setEnabled(false);
        currentButton = 4;
    }//GEN-LAST:event_jButtonVisualizerActionPerformed

    private void jTextFieldLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldLoginActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldLoginActionPerformed

    private void jButtonLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLoginActionPerformed
        if(jTextFieldLogin.isEnabled() && !jTextFieldLogin.getText().equals("")){
            username = jTextFieldLogin.getText();
            
            try {
                initProfile();
            } catch (IOException ex) {
                Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            if(loggedInUser.getIsValidUsername()){
                initVisualizer();
                updateLoginStatus(true);
            }
            else{
                jLabelincorrectusernamepleasetryagain.setVisible(true);
            }
            
            jTextFieldLogin.setText("");
        }
        else{
            updateLoginStatus(false);
        }
    }//GEN-LAST:event_jButtonLoginActionPerformed

    private void jTextFieldSearchUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldSearchUserActionPerformed

    }//GEN-LAST:event_jTextFieldSearchUserActionPerformed

    private void jButtonSearchUser2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSearchUser2ActionPerformed
        if(!jTextFieldSearchUser.getText().equals("")){
            
            try {
                initSearchUser(jTextFieldSearchUser.getText());
            } catch (IOException ex) {
                Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jButtonSearchUser2ActionPerformed

    private void jTextFieldUser1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldUser1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldUser1ActionPerformed

    private void jButtonCompareActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCompareActionPerformed
        try {
            
            if(isComparingAgain){
                for(int i = topWordLabels.size()-1; i >= 0; i--){
                    jPanelLeft.remove(topWordLabels.get(i));
                    topWordLabels.remove(i);
                }
                jPanelLeft.repaint();
            }
            
            User user1 = new User(jTextFieldUser1.getText(), bToken);
            User user2 = new User(jTextFieldUser2.getText(), bToken);
            
            if(user1.getIsValidUsername() && user2.getIsValidUsername()){
                CompareUsers cU = new CompareUsers(user1, user2);

                jLabelResults.setText("Results are based on their last " + cU.getRegularUser().getJTweetArray().size() + " tweets.");       
                jPanelResults.setVisible(true);

                JLabel label;
                for(int i = 0; i < cU.getOrderedMapList().getList().size(); i++){

                    label = new JLabel();
                    label.setText((i+1) + ". " + cU.getOrderedMapList().getList().get(i).getKey());
                    label.setFont(new Font("Tahoma", Font.PLAIN, 16));
                    label.setBounds(15, 60+(20*i), 150, 20);
                    topWordLabels.add(label);
                    jPanelLeft.add(label);
                }

                jLabelCPT1.setText(cU.getRegularUser().getUsername() + " CPT: " + cU.getRegularUser().getCharacterCalc().getCharsPerTweet());
                jLabelCPT2.setText(cU.getSlicedUser().getUsername() + " CPT: " + cU.getSlicedUser().getCharacterCalc().getCharsPerTweet());
                jLabelPop1.setText(cU.getRegularUser().getUsername() + " Popularity: " + cU.getRegularUser().getUserRating().getPopularity() + "/10");
                jLabelPop2.setText(cU.getSlicedUser().getUsername() + " Popularity: " + cU.getSlicedUser().getUserRating().getPopularity() + "/10");

                jPanelRight.add(jLabelCPT1);
                jPanelRight.add(jLabelCPT2);
                jPanelRight.add(jLabelPop1);
                jPanelRight.add(jLabelPop2);

                jPanelLeft.repaint();
                jPanelRight.repaint();

                isComparingAgain = true;
            }

        } catch (IOException ex) {
            Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButtonCompareActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCompare;
    private javax.swing.JButton jButtonCompareUsers;
    private javax.swing.JButton jButtonHome;
    private javax.swing.JButton jButtonLogin;
    private javax.swing.JButton jButtonProfile;
    private javax.swing.JButton jButtonSearchUser;
    private javax.swing.JButton jButtonSearchUser2;
    private javax.swing.JButton jButtonVisualizer;
    private javax.swing.JLabel jLabelCPT1;
    private javax.swing.JLabel jLabelCPT2;
    private javax.swing.JLabel jLabelCompareUsersTitle;
    private javax.swing.JLabel jLabelDate;
    private javax.swing.JLabel jLabelLoggedInStatus;
    private javax.swing.JLabel jLabelPop1;
    private javax.swing.JLabel jLabelPop2;
    private javax.swing.JLabel jLabelProfileAverageHashtags;
    private javax.swing.JLabel jLabelProfileAverageHashtags1;
    private javax.swing.JLabel jLabelProfileAverageMentions;
    private javax.swing.JLabel jLabelProfileAverageMentions1;
    private javax.swing.JLabel jLabelProfileAverageSpamWords;
    private javax.swing.JLabel jLabelProfileAverageSpamWords1;
    private javax.swing.JLabel jLabelProfileAverageWordCount;
    private javax.swing.JLabel jLabelProfileAverageWordCount1;
    private javax.swing.JLabel jLabelProfileDateCreated;
    private javax.swing.JLabel jLabelProfileDescription;
    private javax.swing.JLabel jLabelProfileFollowers;
    private javax.swing.JLabel jLabelProfileFollowing;
    private javax.swing.JLabel jLabelProfileHyperlink;
    private javax.swing.JLabel jLabelProfileLanguage;
    private javax.swing.JLabel jLabelProfileLikes;
    private javax.swing.JLabel jLabelProfileLocation;
    private javax.swing.JLabel jLabelProfileName;
    private javax.swing.JLabel jLabelProfileName1;
    private javax.swing.JLabel jLabelProfileName2;
    private javax.swing.JLabel jLabelProfileName3;
    private javax.swing.JLabel jLabelProfileName5;
    private javax.swing.JLabel jLabelProfileName6;
    private javax.swing.JLabel jLabelProfileName7;
    private javax.swing.JLabel jLabelProfileTimeZone;
    private javax.swing.JLabel jLabelProfileTweets;
    private javax.swing.JLabel jLabelProfileUsername;
    private javax.swing.JLabel jLabelProfileVerified;
    private javax.swing.JLabel jLabelProfileWebsite;
    private javax.swing.JLabel jLabelResults;
    private javax.swing.JLabel jLabelResultsthisdoesnot;
    private javax.swing.JLabel jLabelSearchDateCreated;
    private javax.swing.JLabel jLabelSearchDescription;
    private javax.swing.JLabel jLabelSearchFollowers;
    private javax.swing.JLabel jLabelSearchFollowing;
    private javax.swing.JLabel jLabelSearchHyperlink;
    private javax.swing.JLabel jLabelSearchLanguage;
    private javax.swing.JLabel jLabelSearchLikes;
    private javax.swing.JLabel jLabelSearchLocation;
    private javax.swing.JLabel jLabelSearchName;
    private javax.swing.JLabel jLabelSearchTimeZone;
    private javax.swing.JLabel jLabelSearchTweets;
    private javax.swing.JLabel jLabelSearchUsername;
    private javax.swing.JLabel jLabelSearchVerified;
    private javax.swing.JLabel jLabelSearchWebsite;
    private javax.swing.JLabel jLabelTime;
    private javax.swing.JLabel jLabelWordMatchTitle;
    private javax.swing.JLabel jLabelWordMatchTitle1;
    private javax.swing.JLabel jLabelincorrectusernamepleasetryagain;
    private javax.swing.JLabel jLabelloginsuccessful;
    private javax.swing.JLabel jLabelpleaseenteryourusernamebelow;
    private javax.swing.JLabel jLabelpleaseusethetoolbarabovetonavigate;
    private javax.swing.JLabel jLabelthankyouforusingqualitweeter;
    private javax.swing.JPanel jPanelCompareUsers;
    private javax.swing.JPanel jPanelHome;
    private javax.swing.JPanel jPanelInfoBar;
    private javax.swing.JPanel jPanelLeft;
    private javax.swing.JPanel jPanelMainDisplay;
    private javax.swing.JPanel jPanelProfile;
    private javax.swing.JPanel jPanelResults;
    private javax.swing.JPanel jPanelRight;
    private javax.swing.JPanel jPanelSearchUser;
    private javax.swing.JPanel jPanelToolbar;
    private javax.swing.JPanel jPanelVisualizer;
    private javax.swing.JScrollPane jScrollPaneCompareUsers;
    private javax.swing.JScrollPane jScrollPaneHome;
    private javax.swing.JScrollPane jScrollPaneProfile;
    private javax.swing.JScrollPane jScrollPaneSearchUser;
    private javax.swing.JScrollPane jScrollPaneVisualizer;
    private javax.swing.JTextField jTextFieldLogin;
    private javax.swing.JTextField jTextFieldSearchUser;
    private javax.swing.JTextField jTextFieldUser1;
    private javax.swing.JTextField jTextFieldUser2;
    // End of variables declaration//GEN-END:variables
}
