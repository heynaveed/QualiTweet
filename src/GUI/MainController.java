package GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;


public final class MainController {
    
    private JFrame frame;
    private static MainController instance;
    private Dashboard dashboard;
    
    public MainController() throws Exception{
        startProgram();
    }
    
    public static MainController getInstance(){
        return instance;
    }
    
    public void startProgram(){
        if(frame != null){
            frame.dispose();
        }
        try {
            loadDashboard();
        } catch (Exception ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void loadDashboard() throws Exception{
        dashboard = new Dashboard();
        frame = new JFrame("QualiTweet");
        frame.add(dashboard, BorderLayout.CENTER);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);
        initialiseFrame(frame);
    }
    
    public void initialiseFrame(JFrame frame) throws Exception{
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationByPlatform(true);
        frame.repaint();
        frame.validate();
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
    }
    
    public static void boot() throws Exception{
        instance = new MainController();
    }
    
    public static void main(String[] args) throws SQLException, Exception{
        SwingUtilities.invokeLater(() -> {
            try {
                boot();
            } catch (Exception ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
}
