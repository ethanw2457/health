import java.io.*;
import java.util.*;

/**
 * MazeSolver
 *
 * Handles reading, solving, and writing maze solutions.
 * Includes functionality to validate maze structure, solve it using BFS,
 * and save the solution to a file.
 * 
 * @author Ethan Wang
 * @version December 2, 2024
 */
public class MazeSolver {

    private Maze maze; 

    /**
     * Constructs a MazeSolver object with no initialized maze.
     */
    public MazeSolver() {
        this.maze = null;
    }

    /**
     * Reads a maze from a file and initializes the Maze object.
     *
     * @param filename The name of the file containing the maze.
     * @throws InvalidMazeException If the maze file is improperly formatted.
     * @throws IOException If an I/O error occurs while reading the file.
     */
    public void readMaze(String filename) throws InvalidMazeException, IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String name = reader.readLine();
            if (name == null || name.trim().isEmpty()) {
                throw new InvalidMazeException("Maze must have a name.");
            }

            String startLine = reader.readLine();
            String endLine = reader.readLine();

            if (startLine == null || endLine == null) {
                throw new InvalidMazeException("Maze must have start and end points.");
            }

            int[] start = parsePoint(startLine, "Start");
            int[] end = parsePoint(endLine, "End");

            List<char[]> gridList = new ArrayList<>();
            String line;
            int expectedColumns = -1;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                if (expectedColumns == -1) {
                    expectedColumns = tokens.length;
                } else if (tokens.length != expectedColumns) {
                    throw new InvalidMazeException("Maze is not rectangular.");
                }

                char[] row = new char[tokens.length];
                for (int i = 0; i < tokens.length; i++) {
                    char c = tokens[i].charAt(0);
                    if (c != 'P' && c != 'W') {
                        throw new InvalidMazeException("Maze contains invalid characters.");
                    }
                    row[i] = c;
                }
                gridList.add(row);
            }

            if (gridList.isEmpty()) {
                throw new InvalidMazeException("Maze grid cannot be empty.");
            }

            char[][] grid = gridList.toArray(new char[gridList.size()][]);
            validateStartEnd(start, end, grid);

            this.maze = new Maze(name, grid, start, end);
        }
    }

    /**
     * Parses a point (row and column) from a line in the maze file.
     *
     * @param line The line containing the point.
     * @param type A description of the point (e.g., "Start" or "End").
     * @return An array containing the row and column of the point.
     * @throws InvalidMazeException If the point is improperly formatted.
     */
    private int[] parsePoint(String line, String type) throws InvalidMazeException {
        try {
            String[] parts = line.split(":");
            if (parts.length != 2) {
                throw new InvalidMazeException(type + " line is improperly formatted.");
            }
            String[] coords = parts[1].trim().split("-");
            if (coords.length != 2) {
                throw new InvalidMazeException(type + " coordinates are improperly formatted.");
            }
            int row = Integer.parseInt(coords[0]);
            int col = Integer.parseInt(coords[1]);
            return new int[]{row, col};
        } catch (NumberFormatException e) {
            throw new InvalidMazeException(type + " coordinates must be integers.");
        }
    }

    /**
     * Validates that the start and end points are within maze boundaries and on valid paths.
     *
     * @param start The start point.
     * @param end The end point.
     * @param grid The maze grid.
     * @throws InvalidMazeException If the points are invalid.
     */
    private void validateStartEnd(int[] start, int[] end, char[][] grid) throws InvalidMazeException {
        int rows = grid.length;
        int cols = grid[0].length;

        if (!isWithinGrid(start, rows, cols)) {
            throw new InvalidMazeException("Start point is out of maze boundaries.");
        }
        if (!isWithinGrid(end, rows, cols)) {
            throw new InvalidMazeException("End point is out of maze boundaries.");
        }
        if (grid[start[0]][start[1]] != 'P') {
            throw new InvalidMazeException("Start point is not on a path.");
        }
        if (grid[end[0]][end[1]] != 'P') {
            throw new InvalidMazeException("End point is not on a path.");
        }
    }

    /**
     * Checks whether a point is within the maze grid boundaries.
     *
     * @param point The point to check.
     * @param rows The number of rows in the grid.
     * @param cols The number of columns in the grid.
     * @return True if the point is within bounds, false otherwise.
     */
    private boolean isWithinGrid(int[] point, int rows, int cols) {
        int row = point[0];
        int col = point[1];
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    /**
     * Solves the maze using Breadth-First Search (BFS) to find the shortest path.
     * Updates the Maze object with the computed path.
     */
    public void solveMaze() {
        if (maze == null) {
            throw new IllegalStateException("Maze has not been initialized.");
        }

        char[][] grid = maze.getGrid();
        int[] start = maze.getStart();
        int[] end = maze.getEnd();

        int rows = grid.length;
        int cols = grid[0].length;

        boolean[][] visited = new boolean[rows][cols];
        int[][] prev = new int[rows * cols][2];
        for (int[] row : prev) {
            Arrays.fill(row, -1);
        }

        Queue<int[]> queue = new LinkedList<>();
        queue.offer(start);
        visited[start[0]][start[1]] = true;

        boolean found = false;

        int[][] directions = {
                {-1, 0}, {1, 0}, {0, -1}, {0, 1}
        };

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            if (Arrays.equals(current, end)) {
                found = true;
                break;
            }

            for (int[] dir : directions) {
                int newRow = current[0] + dir[0];
                int newCol = current[1] + dir[1];

                if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols &&
                        grid[newRow][newCol] == 'P' && !visited[newRow][newCol]) {

                    queue.offer(new int[]{newRow, newCol});
                    visited[newRow][newCol] = true;
                    prev[newRow * cols + newCol][0] = current[0];
                    prev[newRow * cols + newCol][1] = current[1];
                }
            }
        }

        if (found) {
            List<int[]> pathList = new ArrayList<>();
            int[] at = end;
            while (!Arrays.equals(at, start)) {
                pathList.add(at);
                int prevRow = prev[at[0] * cols + at[1]][0];
                int prevCol = prev[at[0] * cols + at[1]][1];
                at = new int[]{prevRow, prevCol};
            }
            pathList.add(start);
            Collections.reverse(pathList);
            int[][] path = pathList.toArray(new int[0][]);
            maze.setPath(path);
        } else {
            maze.setPath(new int[0][0]);
        }
    }

    /**
     * Writes the solution path of the maze to a file.
     *
     * @param filename The name of the file to write the solution to.
     * @throws IllegalStateException If the maze has not been solved yet.
     */
    public void writeSolution(String filename) {
        if (maze == null || maze.getPath() == null) {
            throw new IllegalStateException("Maze has not been solved yet.");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(maze.pathString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
