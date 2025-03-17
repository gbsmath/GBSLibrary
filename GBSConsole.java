package gbs;

import java.awt.Color;

/*
 * File Name: GBSConsole.java
 *   Created: Jul 23, 2022
 *    Author: 
 */

public class GBSConsole extends Object
{
  static
  {
    javax.swing.JFrame.setDefaultLookAndFeelDecorated(true);
  }

  protected static final ConsoleApp terminalWindow = new ConsoleApp("CS 371H");
  protected static final java.io.PrintStream out = System.out;
  
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
}
