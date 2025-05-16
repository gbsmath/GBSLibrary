package gbs;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;


/*
 * File Name: GBSApp.java
 *   Created: Jul 24, 2022
 *    Author: David Rogers
 *
   Functionality to match a Java Applet
     - loadImage
     - createImage
     - getAudioClip - not working in replit
     - double buffering enabled 
 */

public class GBSApp extends JPanel implements ActionListener
{
  protected static final Color black = Color.black, BLACK = Color.black;
  protected static final Color blue = Color.blue, BLUE = Color.blue;
  protected static final Color cyan = Color.cyan, CYAN = Color.cyan;
  protected static final Color darkGray = Color.darkGray, DARK_GRAY = Color.darkGray;
  protected static final Color gray = Color.gray, GRAY = Color.gray;
  protected static final Color green = Color.green, GREEN = Color.green;
  protected static final Color lightGray = Color.lightGray, LIGHT_GRAY = Color.lightGray;
  protected static final Color magenta = Color.magenta, MAGENTA = Color.magenta;
  protected static final Color orange = Color.orange, ORANGE = Color.orange;
  protected static final Color pink = Color.pink, PINK = Color.pink;
  protected static final Color red = Color.red, RED = Color.red;
  protected static final Color white = Color.white, WHITE = Color.white;
  protected static final Color yellow = Color.yellow, YELLOW = Color.yellow;
  
  protected static final java.io.PrintStream out = System.out;
  
  public static final Image ICON;

  private static final String base64 = 
    "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";
  
  static
  { 
    Image temp = null;
    try
    {
      temp = ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(base64)));
    }
    catch (IOException ioe)
    {
      //should never get to this (unless the base64 string is corrupted?)
    }
    ICON = temp;
  }
  
  private LayoutManager defaultLM;
  ////private static int jFrameX, jFrameY;
  
  public GBSApp()
  {
    super.setOpaque(true);
    super.setBackground(Color.WHITE);
    super.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
    try {
      UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    defaultLM = super.getLayout();
  }
  
  public void autoArrangeGUIForMe(boolean choice)
  {
    if (choice)
    {
      super.setLayout(defaultLM);
    }
    else
    {
      super.setLayout(null);
    }    
  }
    
  @Override
  public void setSize(int w, int h)
  {
    Dimension size = new Dimension(w, h);
    super.setMinimumSize(size);
    super.setMaximumSize(size);
    super.setPreferredSize(size);
  }
  
  @Override
  public void paintComponent(Graphics g)
  {
    super.paintComponent(g);
    Graphics2D gr = (Graphics2D) g;
    gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
  }
  
  public Image getImage(String s)
  {
    return Toolkit.getDefaultToolkit().getImage(super.getClass().getResource(s));    
  }
  
  public Image createImage(int w, int h, boolean transparent)
  {
    int type = transparent ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;    
    return new BufferedImage(w, h, type);
  }
  
  public Clip getAudioClip(String fileName)
  {

    Clip sound = null;
    try
    {
      File f = new File(fileName);
      // File f = super.getClass().getResourceAsStream(fileName);
      AudioInputStream ais = AudioSystem.getAudioInputStream(f.getAbsoluteFile());
      sound = AudioSystem.getClip();
      sound.open(ais);
    }
    catch (IOException | LineUnavailableException | UnsupportedAudioFileException exc)
    {      
      exc.printStackTrace(System.err);            
    }
    return sound;
  }
  
  public InputStream openFileNamed(String s)
  {
    if (s == null) throw new NullPointerException("File name parameter must not be null");
    try
    {
      File f = new File(s);
      if(f.isFile() && !f.isDirectory())
      {
        Scanner sc = new Scanner(f);
        return super.getClass().getResourceAsStream(s);
      }
    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public void actionPerformed(ActionEvent ae)
  {
    //to be completed by subclasses...
  }
  
  public JFrame getFrame()
  {
    return (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);
  }
  
  public void setTitle(String s)
  {
    this.getFrame().setTitle(s);
  }
          
  public static void createFrame(GBSApp app, String title)
  {
    JFrame.setDefaultLookAndFeelDecorated(true);
    JFrame fr = new JFrame(title);    
    java.awt.EventQueue.invokeLater(new Runnable() 
    { 
      @Override
      public void run() 
      {        
        fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ////fr.setLocation(jFrameX += 20, jFrameY += 20);
        fr.setLocation(10, 10);
        fr.setResizable(false);
        fr.setIconImage(GBSApp.ICON);
        fr.setContentPane(app);
        fr.pack();
        fr.setVisible(true);
      }
    } 
    );
  }
}
