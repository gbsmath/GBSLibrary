package gbs;

/*
 * File Name: Matrix.java
 *   Created: Aug 23, 2014
 *    Author: 
 */
import java.util.ArrayList;

public class Matrix<ItemType>
{
  private ArrayList< ArrayList<ItemType> > mat;
  private int numRow, numCol;

  /***
   *Create a rectangular matrix of <code>ItemType</code> with all the elements set to <code>null</code>
   *
   *@param nr  the number of rows in the matrix
   *@param nc  the number of columns in the matrix
   *
   *
   *@throws IllegalArgumentException if <code>nr</code> or <code>nc</code> are not positive
   *
   */
  public Matrix(int nr, int nc)
  {
    this(nr, nc, null);
  }


  /***
   *Create a rectangular matrix of <code>ItemType</code> with all the elements set to a particular value
   *
   *@param nr  the number of rows in the matrix
   *@param nc  the number of columns in the matrix
   *@param initVal  each of the elements in the matrix will be initialized to this value
   *
   *@throws IllegalArgumentException if <code>nr</code> or <code>nc</code> are not positive
   *
   */
  public Matrix(int nr, int nc, ItemType initVal)
  {
    initialize(nr, nc, initVal);
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj instanceof Matrix)
    {
      Matrix rhs = (Matrix) obj;
      if (rhs.numRow != numRow || rhs.numCol != numCol) return false;
      for (int r = 0; r < numRow; r++)
      {
        for (int c = 0; c < numCol; c++)
        {
          if (mat.get(r).get(c).equals( ((ArrayList) (rhs.mat.get(r))).get(c)) == false) return false;
        }
      }
      return true;
    }
    else return false;
  }


  /**
   *Sets all the values in this matrix to the specified value
   *
   *@param value all the elements in the matrix will be set to this value
   *
   */
  public void fillWith(ItemType value)
  {
    initialize(numRow, numCol, value);
  }

  /***
   *Retrieves an element in the matrix at the specified location
   *
   *@param r the row of the element to retrieve
   *@param c the column of the element to retrieve
   *
   *@return the element in the matrix at row = <code>r</code> and column = <code>c</code>
   *
   *@throws IndexOutOfBoundsException if <code>r < 0 </code> or <code> r <u> ></u> getNumRows() </code>
   *@throws IndexOutOfBoundsException if <code>c < 0 </code> or <code> c <u> ></u> getNumColumns() </code>
   */
  public ItemType get(int r, int c)
  {
    if (r > -1 && c > -1 && r < numRow && c < numCol)
    {
      return mat.get(r).get(c);
    }
    else
    {
      String s = "Illegal parameter in get method:";
      if (r < 0 || r >= numRow) s += "\n    row cannot be "+r;
      if (c < 0 || c >= numCol) s += "\n    column cannot be "+c;
      throw new IndexOutOfBoundsException(s);
    }
  }

  /***
   *Retrieves an entire column of the matrix as an <code>ArrayList&LTItemType&GT</code>
   *
   *@param c the column of the matrix to retrieve
   *
   *@return the <code>c</code><sup>th</sup> column of the matrix as an <code>ArrayList&LTItemType&GT</code>
   *
   *@throws IndexOutOfBoundsException if <code>c < 0 </code> or <code> c <u> ></u> getNumColumns() </code>
   */
  public ArrayList<ItemType> getColumn(int c)
  {
    if (c > -1 && c < numCol)
    {
      ArrayList<ItemType> col = new ArrayList<ItemType>(numRow);
      for (int r = 0; r < numRow; r++) col.add(mat.get(r).get(c));
      return col;
    }
    else
    {
      String s = "Illegal parameter in getColumn method:";
      s += "\n    column cannot be "+c;
      throw new IndexOutOfBoundsException(s);
    }
  }

  /***
   *Returns the number of columns in this matrix
   *
   *@return the number of columns in this matrix
   */
  public int getNumColumns()
  {
    return numCol;
  }


  /***
   *Returns the number of rows in this matrix
   *
   *@return the number of rows in this matrix
   */
  public int getNumRows()
  {
    return numRow;
  }


  /***
   *Retrieves an entire row of the matrix as an <code>ArrayList&LTItemType&GT</code>
   *
   *@param r the row of the matrix to retrieve
   *
   *@return the <code>r</code><sup>th</sup> row of the matrix as an <code>ArrayList&LTItemType&GT</code>
   *
   *@throws IndexOutOfBoundsException if <code>r < 0 </code> or <code> r <u> ></u> getNumRows() </code>
   */
  public ArrayList<ItemType> getRow(int r)
  {
    if (r > -1 && r < numRow)
    {
      ArrayList<ItemType> row = new ArrayList<ItemType>(numCol);
      for (int c = 0; c < numCol; c++) row.add(mat.get(r).get(c));
      return row;
    }
    else
    {
      String s = "Illegal parameter in getRow method:";
      s += "\n    row cannot be "+r;
      throw new IndexOutOfBoundsException(s);
    }
  }


  /***
   *Replaces an element in the matrix at the specified location
   *
   *@param r the row of the element to change
   *@param c the column of the element to change
   *@param val change the element at row = r and col = c to this value.
   *
   *@return the element at row = <code>r</code> and column = <code>c</code> that was displaced by this method call
   *
   *@throws IndexOutOfBoundsException if <code>r < 0 </code> or <code> r <u> ></u> getNumRows() </code>
   *@throws IndexOutOfBoundsException if <code>c < 0 </code> or <code> c <u> ></u> getNumColumns() </code>
   */
  public ItemType set(int r, int c, ItemType val)
  {
    if (r > -1 && c > -1 && r < numRow && c < numCol)
    {
      return mat.get(r).set(c, val);
    }
    else
    {
      String s = "Illegal parameter in set method:";
      if (r < 0 || r >= numRow) s += "\n    row cannot be "+r;
      if (c < 0 || c >= numCol) s += "\n    column cannot be "+c;
      throw new IndexOutOfBoundsException(s);
    }
  }

  @Override
  public String toString()
  {
    String endl = System.getProperty("line.separator");
    String output = "";
    for (int r = 0; r < numRow; r++)
    {
      output += "row = "+r+":"+endl;
      for (int c = 0; c < numCol; c++)
      {
        output += "     col = "+c+":  "+mat.get(r).get(c)+endl;
      }
    }
    return output;
  }

  private void initialize(int nr, int nc, ItemType initVal)
  {
    if (nr > 0 && nc > 0)
    {
      mat = new ArrayList< ArrayList<ItemType> >(nr);
      for (int r = 0; r < nr; r++)
      {
        mat.add(new ArrayList<ItemType>(nc));
        for (int c = 0; c < nc; c++)
        {
          mat.get(r).add(initVal);
        }
      }
      numRow = nr;
      numCol = nc;
    }
    else
    {
      String s = "\nIllegal parameter in constructor: ";
      if (nr <= 0) s += "\n    rows cannot be "+nr;
      if (nc <= 0) s += "\n    columns cannot be "+nc;
      throw new IllegalArgumentException(s);
    }
  }

}
