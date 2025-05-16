/*
 *
 * Portions copyright (2009)  Eric G. Berkowitz
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * http://cs.roosevelt.edu/eric/console.html#about
 */

package gbs;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import java.awt.Font;
import java.lang.reflect.Field;
import java.util.Base64;
import java.util.EventListener;
import javax.imageio.ImageIO;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;

public class ConsoleApp extends Object
{
  
  // public static final ConsoleApp terminalWindow = new ConsoleApp("This is a Title");
  // public static final java.io.PrintStream screen = System.out;
  
  private PrintStream sout, serr;
  private InputStream sin; 
  private PipedInputStream piOut;
  private PipedInputStream piErr;
  private PipedOutputStream poOut;
  private BufferedOutputStream boOut;
  private BufferedOutputStream boErr;
  private PipedOutputStream poErr;
  private PipedInputStream piIn;
  private PipedOutputStream poIn;
  private PrintWriter PWin;
  private int insertPos = 0;
  private int maxBuffer = 0;
  private ConsoleLock PrintLock = new ConsoleLock();
  
  private JTextArea2 out;
  private JMenuBar menuBar;
  private JMenuItem exitMI;
  private JMenuItem onTopMI;
  private JMenuItem clearMI;
  private JFrame frame;
  private JSpinner jspin;
  private int printStart = 0;
  private int printMax = 0;
  private boolean okExit = false;

  private class MenuHandler implements ActionListener
  {
    @Override
    public void actionPerformed(ActionEvent ev)
    {
      if (ev.getSource() == exitMI)
      {
        frame.dispose();
        System.exit(1);
      }

      if (ev.getSource() == onTopMI)
      {
        frame.setAlwaysOnTop(onTopMI.isSelected());
      }

      if (ev.getSource() == clearMI)
      {
        out.setText("");
        out.setCaretPosition(0);
        insertPos = 0;
      }
    }
  }
  
  
  private class JTextArea2 extends JTextArea implements KeyListener
  {

    public JTextArea2(int r, int c)
    {
      super(r, c);
      super.addKeyListener(this);
      
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
      int keyCode = e.getKeyCode();
      if (okExit)
      {
        System.setIn(sin);
        frame.dispose();
        System.exit(1);
      }
      if (keyCode == KeyEvent.VK_HOME || keyCode == KeyEvent.VK_PAGE_UP)
      {
        out.setCaretPosition(insertPos);
        e.consume();
      }
      if (keyCode == KeyEvent.VK_UP)
      {
        e.consume();
        return;
      }
      if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
      {
        if (out.getCaretPosition() <= insertPos)
        {
          e.consume();
        }
        return;
      }
      if (e.getKeyCode() == KeyEvent.VK_LEFT)
      {
        if (out.getCaretPosition() <= insertPos)
        {
          e.consume();
        }
        return;
      }
      if ((e.getKeyCode() == KeyEvent.VK_ENTER))
      {
        out.setCaretPosition(out.getDocument().getLength());
      }
      synchronized (this)
      {
        System.out.flush();
        //out.setCaretPosition(out.getDocument().getLength());
      }

    }

    @Override
    public void keyReleased(KeyEvent e)
    {
      if ((e.getKeyCode() == KeyEvent.VK_ENTER))
      {
        synchronized (this)
        { 
          //Sycronize the display of characters in the TextArea
          out.setCaretPosition(out.getDocument().getLength());
          int docEnd = out.getDocument().getLength();
          int len = docEnd - insertPos;
          String InputText = null;
          try
          {
            InputText = out.getText(insertPos, len);
          }
          catch (BadLocationException be)
          {
            be.printStackTrace();
          }
          PWin.print(InputText);
          PWin.flush();
          out.setCaretPosition(out.getDocument().getLength());
          insertPos = docEnd;
        }
      }
    }

    @Override
    public void keyTyped(KeyEvent e)
    {
    }
  }

  protected ConsoleApp(String title)
  {
    
    if (frame == null)
    {
      frame = new JFrame(title);
      makeConsole(16384);
    }
  }


  
  public void setFontSize(int s)
  {
    if (s < 4)
    {
      s = 4;
    }
    if (s > 64)
    {
      s = 64;
    }
    out.setFont(new Font(Font.MONOSPACED, Font.BOLD, s));
    
    jspin.setValue(s);
    
    frame.pack();
    frame.setVisible(true);
  }

  public void setBackgroundColor(Color c)
  {
    if (c != null) out.setBackground(c);
  }

  public void setTextColor(Color c)
  {
    if (c != null)
    {
      out.setForeground(c);
      out.setCaretColor(c);
    }
  }
  
  public void setColors(Color text, Color back)
  {
    if (text != null && back != null)
    {
      out.setForeground(text);
      out.setCaretColor(text);
      out.setBackground(back);
    }
  }
  
  public void clearConsole()
  {
    out.setText("");
    out.setCaretPosition(0);
    insertPos = 0;
  }

  public void setTitle(String s)
  {
    s += "";
    if (s.length() > 64) s = s.substring(0, 64);
    frame.setTitle(s);
    JFrame.setDefaultLookAndFeelDecorated(true);
    frame.pack();
    frame.setVisible(true);
  }
  
  public void sleep(double timeInSeconds)
  {
    sout.flush();
    try
    {
      Thread.sleep((long)(timeInSeconds * 1000));
    }
    catch (InterruptedException ie)
    {
    }
  }
  
  private String getInfo()
  {
    return "\n"+
           " Always at Front: "+(onTopMI.isSelected() ? "on" : "off") + "\n" +
           "      Text Color: "+this.getColorName(out.getForeground()) + "\n" +
           "Background Color: "+this.getColorName(out.getBackground()) + "\n" +
           "       Font Size: "+out.getFont().getSize() + "\n" +
           "    Window Title: "+frame.getTitle() + "\n";
  }
  
  public void showStatus()
  {
    System.out.println(this.getInfo());
  }
  
  private String getColorName(Color c)
  {
    for (Field f : Color.class.getFields())
    {
      try
      {
        if (f.getType() == Color.class && f.get(null).equals(c) && Character.isUpperCase(f.getName().charAt(0)))
        {
          return f.getName();
        }
      }
      catch (IllegalAccessException e)
      {
      }
    }
    return c.toString();
}
  

  
  private void makeConsole(int maxBuffer)
  {
    this.maxBuffer = maxBuffer;


    String base64 = "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";
    try
    {
      frame.setIconImage(ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(base64))));
    }
    catch (IOException ioe)
    {
      //should never get to this since not reading from storage
    }
  

    try
    {
      sout = System.out;
      serr = System.err;
      sin = System.in;
      
      piOut = new PipedInputStream();
      poOut = new PipedOutputStream(piOut);
      boOut = new BufferedOutputStream(poOut);
      System.setOut(new PrintStream(boOut, true));

      piErr = new PipedInputStream();
      poErr = new PipedOutputStream(piErr);
      boErr = new BufferedOutputStream(poErr);
      System.setErr(new PrintStream(boOut, true)); //uncomment when finished debugging...

      poIn = new PipedOutputStream();
      piIn = new PipedInputStream(poIn);
      System.setIn(piIn);

      PWin = new PrintWriter(new OutputStreamWriter(poIn));

      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      JFrame.setDefaultLookAndFeelDecorated(true);

      menuBar = new JMenuBar();
      frame.setJMenuBar(menuBar);

      Font menuFont = new Font(Font.DIALOG, Font.BOLD, 15);
      Font menuItemFont = new Font(Font.DIALOG, Font.BOLD, 18);

      JMenu optionsMenu = new JMenu("Options");




      optionsMenu.setFont(menuItemFont);
      menuBar.add(optionsMenu);
      //menuBar.setLayout(new );


      MenuHandler mh = new MenuHandler();
      clearMI = new JMenuItem("Clear Console ");

      clearMI.setFont(menuFont);
      clearMI.addActionListener(mh);
      optionsMenu.add(clearMI);

      onTopMI = new JCheckBoxMenuItem("Keep Window at Front ");

      onTopMI.setFont(menuFont);
      onTopMI.addActionListener(mh);
      optionsMenu.add(onTopMI);

      exitMI = new JMenuItem("Exit ");

      exitMI.setFont(menuFont);
      exitMI.addActionListener(mh);
      optionsMenu.add(exitMI);
      
      
      
      JLabel separate = new JLabel("    |    ");
      separate.setFont(menuItemFont);
      menuBar.add(separate);
      
      JLabel fs = new JLabel("Font size: ");
      fs.setFont(new Font(Font.DIALOG, Font.BOLD | Font.ITALIC, 15));
      fs.setHorizontalAlignment(JLabel.RIGHT);
      menuBar.add(fs);

      
      jspin = new JSpinner(new SpinnerNumberModel(14, 4, 64, 1));
      
      
      Dimension d = new Dimension(40, 30);
      jspin.setPreferredSize(d);
      jspin.setMaximumSize(d);
      jspin.setMinimumSize(d);
      
      
      DefaultEditor edit = (DefaultEditor) jspin.getEditor();
      JTextField spinEd = edit.getTextField();
      spinEd.setFont(menuFont);
      spinEd.setEditable(false);
      spinEd.setHorizontalAlignment(JTextField.CENTER);
      
      
      jspin.addChangeListener(new ChangeListener(){
        @Override
        public void stateChanged(ChangeEvent ce)
        {
          int size =  (Integer)jspin.getValue();
          setFontSize(size);
          out.requestFocusInWindow();
        }
      });
      
      menuBar.add(jspin);
      
      
      
      out = new JTextArea2(25, 80);
      out.setMargin(new java.awt.Insets(4,4,4,4));
      out.setEditable(true);

      //out.setFont(new Font("Consolas", Font.BOLD, 20));
      
      out.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
      
      
      out.setSelectedTextColor(Color.BLUE);
      out.setBackground(Color.BLACK);
      out.setForeground(Color.GREEN);
      out.setCaretColor(Color.GREEN);
      out.setLineWrap(true);
      out.setWrapStyleWord(false);
      out.getActionMap().get("paste").setEnabled(false);
      out.getActionMap().get("paste-from-clipboard").setEnabled(false);
      
      
      //Remove mouse listeners to prevent user from placing the caret willy-nilly
      EventListener handlers[] = out.getListeners(MouseListener.class);
      for (int i = 0; i < handlers.length; i++) out.removeMouseListener((MouseListener) handlers[i]);
      handlers = out.getListeners (MouseMotionListener.class);
      for (int i = 0; i < handlers.length; i++) out.removeMouseMotionListener((MouseMotionListener)handlers[i]);
      
      
            // Create reader threads
      new ReaderThread(piOut, false).start();
      new ReaderThread(piErr, true).start();
      
      
      
      JScrollPane jsp = new JScrollPane(out, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      frame.getContentPane().add(jsp, BorderLayout.CENTER);
SwingUtilities.invokeLater( new Runnable() { 

public void run() { 
        
      frame.pack();
      frame.setVisible(true);
  out.requestFocusInWindow(); 
    } 
} );

     
      // frame.pack();
      // frame.setVisible(true);
      

    }
    catch (Exception e)
    {
      e.printStackTrace();
      System.err.println("Failed to create console\nExiting...");
      System.exit(0);
    }
    
    synchronized (this)
    {
      insertPos = out.getDocument().getLength();
      out.setCaretPosition(insertPos);
    }

  }

  class ReaderThread extends Thread
  {

    private PipedInputStream pi;
    private boolean errorThread;

    ReaderThread(PipedInputStream pi, boolean errorThread)
    {
      this.pi = pi;
      this.errorThread = errorThread;
    }

    public void run()
    {
      byte[] buf = new byte[1024];
      try
      {
        while (true)
        {
          final int len = pi.read(buf);
          if (len == -1)
          {
            break;
          }

          printStart = 0;
          printMax = len;

          try
          {
            PrintLock.lock();
          }
          catch (InterruptedException ie)
          {
            System.err.println(ie);
          }
          out.insert(new String(buf, printStart, printMax), insertPos);
          PrintLock.unlock();
          if (errorThread)
          {
            out.setForeground(new Color(255, 0, 0));
          }
          insertPos += printMax;
          // Make sure the last line is always visible
          out.setCaretPosition(out.getDocument().getLength());

          int tlen = out.getDocument().getLength();
          int maxExcess = 500;
          int excess = tlen - maxBuffer;
          int delrange;
          if (excess >= maxExcess)
          {
            try
            {
              delrange = Math.max(excess, out.getDocument().getText(0, tlen).indexOf('\n', excess));
            }
            catch (Exception te)
            {
              delrange = excess;
            }
            //Delete from the first '\n' if there is one
            out.replaceRange("", 0, delrange);
            out.setCaretPosition(out.getDocument().getLength());
            insertPos = out.getDocument().getLength();
          }




        }
      }
      catch (IOException e)
      {
      }
    }
  }


  private class ConsoleLock
  {

    private boolean isLocked = false;

    public synchronized void lock() throws InterruptedException
    {
      while (isLocked)
      {
        wait();
      }
      isLocked = true;
    }

    public synchronized void unlock()
    {
      isLocked = false;
      notify();
    }
  }

}
