
/**
 * Maze
 *
 * Represents a 2D maze with walls ('W') and paths ('P').
 *
 * @author Ethan Wang
 * @version December 2, 2024
 */
public class Maze {

    private final String name;
    private final char[][] grid;
    private final int[] start;
    private final int[] end;
    private int[][] path;

 
    public Maze(String name1, char[][] grid1, int[] start1, int[] end1) {
        this.name = name1;
        this.grid = grid1;
        this.start = start1;
        this.end = end1;
        this.path = null;
    }

    public String getName() {
        return name;
    }

    public char[][] getGrid() {
        return grid;
    }

    public int[] getStart() {
        return start;
    }
   
    public int[] getEnd() {
        return end;
    }

    public int[][] getPath() {
        return path;
    }

    public void setPath(int[][] path) {
        this.path = path;
    }

    public String pathString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("\n");
        sb.append("Moves: ").append(path.length).append("\n");
        sb.append("Start\n");
        for (int[] move : path) {
            sb.append(move[0]).append("-").append(move[1]).append("\n");
        }
        sb.append("End\n");
        return sb.toString();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("\n");
        sb.append("Start: ").append(start[0]).append("-").append(start[1]).append("\n");
        sb.append("End: ").append(end[0]).append("-").append(end[1]).append("\n");
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                sb.append(grid[i][j]);
                if (j < grid[i].length - 1) {
                    sb.append(",");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}