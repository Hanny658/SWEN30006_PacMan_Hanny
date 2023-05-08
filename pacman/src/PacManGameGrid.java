// PacGrid.java
package src;

import ch.aplu.jgamegrid.*;

public class PacManGameGrid
{
  private int nbHorzCells;
  private int nbVertCells;
  private int[][] mazeArray;

  public PacManGameGrid(int nbHorzCells, int nbVertCells)
  {
    this.nbHorzCells = nbHorzCells;
    this.nbVertCells = nbVertCells;
    mazeArray = new int[nbVertCells][nbHorzCells];
    String maze =
      "xxxxxxxxxxxxxxxxxxxx" + // 0
      "x....x....g...x....x" + // 1
      "xgxx.x.xxxxxx.x.xx.x" + // 2
      "x.x.......i.g....x.x" + // 3
      "x.x.xx.xx  xx.xx.x.x" + // 4
      "x......x    x......x" + // 5
      "x.x.xx.xxxxxx.xx.x.x" + // 6
      "x.x......gi......x.x" + // 7
      "xixx.x.xxxxxx.x.xx.x" + // 8
      "x...gx....g...x....x" + // 9
      "xxxxxxxxxxxxxxxxxxxx";// 10


    // Copy structure into integer array
    for (int i = 0; i < nbVertCells; i++)
    {
      for (int k = 0; k < nbHorzCells; k++) {
        int value = toInt(maze.charAt(nbHorzCells * i + k));
        mazeArray[i][k] = value;
      }
    }
  }

  public int getCell(Location location)
  {
    return mazeArray[location.y][location.x];
  }
  private int toInt(char c)
  {
    if (c == 'x')
      return 0;
    if (c == '.')
      return 1;
    if (c == ' ')
      return 2;
    if (c == 'g')
      return 3;
    if (c == 'i')
      return 4;
    return -1;
  }
}
