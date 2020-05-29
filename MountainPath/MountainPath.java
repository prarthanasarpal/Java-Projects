/*
 * Name:        Prarthana Sarpal
 * Date:        April 2nd, 2019
 * Version:     1.04
 * Description: This program reads and stores a set of topographic (land elevation)
                data into a 2D array. There are methods to compute some paths through 
                the mountains (best path and every other path) as well as visualize them.
                Utilizies DrawingPanel to visualize data.
 */

package MountainPath;

import MountainPath.DrawingPanel;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 *
 * @author prarthanasarpal
 */
public class MountainPath {

    static final String FS = File.separator;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        // ***********************************
        // TASK 1:  read data into a 2D Array
        // VARIABLES
        // OBJECTS
        System.out.println("TASK 1: READ DATA");
        int[][] data = read("." + FS + "data" + FS + "mountain.paths" + FS + "Colorado.844x480.dat");
        //int[][] data = read("." + FS + "data" + FS + "mountain.paths" + FS + "etopo1.dat");

        // ***********************************
        // Construct DrawingPanel, and get its Graphics context
        //
        DrawingPanel panel = new DrawingPanel(data[0].length, data.length);
        Graphics g = panel.getGraphics();
        
        // ***********************************
        // TASK 2:  find HIGHEST & LOWEST elevation; for GRAY SCALE
        //
        System.out.println("TASK 2: HIGHEST / LOWEST ELEVATION");
        int min = findMinValue(data);
        System.out.println("\tLowest Elevation: " + min);

        int max = findMaxValue(data);
        System.out.println("\tHighest Elevation: " + max);

        // ***********************************
        // TASK 3:  Draw The Map
        //
        System.out.println("TASK 3: DRAW MAP");
        drawMap(g, data);

        // ***********************************
        // TASK 4A:  implement indexOfMinInCol
        //
        System.out.println("TASK 4A: INDEX OF LOWEST ELEVATION IN COL 0");
        int minRow = indexOfMinInCol(data, 0);
        System.out.println("\tRow with lowest Col 0 Value: " + minRow);

        // ***********************************
        // TASK 4B:  use minRow as starting point to draw path
        //
        System.out.println("TASK 4B: PATH FROM LOWEST STARTING ELEVATION");
        g.setColor(Color.RED);
        int totalChange = drawLowestElevPath(g, data, minRow, 0); //
        System.out.println("\tLowest-Elevation-Change Path starting at row " + minRow + " gives total change of: " + totalChange);

        // ***********************************
        // TASK 5:  determine the BEST path
        //
        g.setColor(Color.RED);
        int bestRow = indexOfLowestElevPath(g, data);
        
        // ***********************************
        // TASK 6:  draw the best path
        //
        System.out.println("TASK 6: DRAW BEST PATH");
        //drawMap.drawMap(g); //use this to get rid of all red lines
        g.setColor(Color.GREEN); //set brush to green for drawing best path
        totalChange = drawLowestElevPath(g, data, bestRow, 0);
        System.out.println("\tThe Lowest-Elevation-Change Path starts at row: " + bestRow + " and gives a total change of: " + totalChange);


    }
    
    /**
     * This method reads a 2D data set from the specified file. The Graphics'
     * industry standard is width by height (width x height), while programmers
     * use rows x cols / (height x width).
     *
     * @param fileName the name of the file
     * @return a 2D array (rows x cols) of the data from the file read
     */
    public static int[][] read(String fileName) throws FileNotFoundException {

        // VARIABLES
        int[][] data = null;
        String line;
        int rows = 0;
        int cols = 0;

        // Creating file and scanning
        File file = new File(fileName);
        Scanner in = new Scanner(file);
        
        // Determining number of cols
        StringTokenizer st = new StringTokenizer(in.nextLine());
        cols = st.countTokens();

        // Resets file to determine rows
        in = new Scanner(file);
        do {
            in.nextLine();
            rows++;
        } while (in.hasNextLine());

        System.out.println(" row, col " + rows + " " + cols);

        // Resetting data size
        data = new int[rows][cols];

        // Reset file to store data
        in = new Scanner(file);

        for (int row = 0; row < rows; row++) {
            line = in.nextLine();
            st = new StringTokenizer(line);

            for (int col = 0; col < cols; col++) {
                data[row][col] = Integer.parseInt(st.nextToken());
            }
        }

        return data;
    }

    /**
     * @param grid a 2D array from which you want to find the smallest value
     * @return the smallest value in the given 2D array
     */
    public static int findMinValue(int[][] data) {

        int min = data[0][0];
        int i = 1;
        for (int row = 0; row < data.length; row++) {
            for (int col = 0; col < data[0].length; col++) {
                if (data[row][col] < min) {
                    min = data[row][col];
                }
            }
        }

        return min;
    }

    /**
     * @param grid a 2D array from which you want to find the largest value
     * @return the largest value in the given 2D array
     */
    public static int findMaxValue(int[][] data) {

        int max = data[0][0];
        int i = 1;
        for (int row = 0; row < data.length; row++) {
            for (int col = 0; col < data[0].length; col++) {
                if (data[row][col] > max) {
                    max = data[row][col];
                }
            }
        }

        return max;
    }

    /**
     * Given a 2D array of elevation data create a image of size rows x cols,
     * drawing a 1x1 rectangle for each value in the array whose color is set to
     * a a scaled gray value (0-255). Note: to scale the values in the array to
     * 0-255 you must find the min and max values in the original data first.
     *
     * @param g a Graphics context to use
     * @param grid a 2D array of the data
     */
    public static void drawMap(Graphics g, int[][] data) {

        // black 0, white 255
        //   low       high
        // VARIABLES
        int max, min, m, gs;

        max = findMaxValue(data);
        min = findMinValue(data);
        
        m = (max - min) / 255;

        for (int row = 0; row < data.length; row++) {
            for (int col = 0; col < data[0].length; col++) {
                // gs = 13 (gs changes for every 13 ft of elv)
                gs = ((data[row][col]) - (min)) / m;
                g.setColor(new Color(gs, gs, gs));
                g.fillRect(col, row, 1, 1);
            }
        }
    }

    /**
     * Scan a single column of a 2D array and return the index of the row that
     * contains the smallest value
     *
     * @param grid a 2D array
     * @col the column in the 2D array to process
     * @return the index of smallest value from grid at the given col
     */
    public static int indexOfMinInCol(int[][] data, int col) {

        int min = data[0][col];
        int r = 0;

        for (int row = 0; row < data.length; row++) {
            if (data[row][col] < min) {
                min = data[row][col];
                r = row;
            }
        }

        return r;
    }

    /**
     * Find the minimum elevation-change route from West-to-East in the given
     * grid, from the given starting row, and draw it using the given graphics
     * context
     *
     * @param g - the graphics context to use
     * @param grid - the 2D array of elevation values
     * @param row - the starting row for traversing to find the min path
     * @return total elevation of the route
     */
    public static int drawLowestElevPath(Graphics g, int[][] data, int row, int col) {
        // - Calculate total elevation
        // - Draws red path

        if (col == data[row].length - 1) return 0;

        if (row < 0 || row == data.length) return 0;

        // VARIABLES
        int elv = 0;
        int deltaElevation = 0;
        int x = 0, y = 0, z = 0;

        if (col > 0 && col < data[0].length - 1 && row > 0 && row <= data.length - 2) {
            x = Math.abs(data[row - 1][col + 1] - data[row][col]); // fwd up
            y = Math.abs(data[row][col + 1] - data[row][col]); // fwd 
            z = Math.abs(data[row + 1][col + 1] - data[row][col]); // fwd down

        } else if (row <= 0 && col < data[0].length - 1) { // Special Case TOP ROW
            x = 1000000; // fwd up row not possible
            y = Math.abs(data[row][col + 1] - data[row][col]); // fwd 
            z = Math.abs(data[row + 1][col + 1] - data[row][col]); // fwd down 

        } else if (row >= data.length - 1 && col < data[0].length - 1) { // Special case LAST ROW
            x = Math.abs(data[row - 1][col + 1] - data[row][col]); // fwd up
            y = Math.abs(data[row][col + 1] - data[row][col]); // fwd 
            z = 10000000; // fwd down not possible
        }

        // Finding least elevation difference and resetting row to row moved
        if (x <= y && x <= z) {
            if (x == z) {
                // Random thing
                double r = Math.random() * 2;
                if (r < 1) {
                    elv = z;
                    g.fillRect(col + 1, row + 1, 1, 1); // fwd down
                    row = row + 1;

                    deltaElevation = deltaElevation + elv;                
                }
            } else {
                elv = x;               
                g.fillRect(col + 1, row - 1, 1, 1); // fwd up
                row = row - 1;
            }

        } else if (y <= x && y <= z) {
            elv = y;
            g.fillRect(col + 1, row, 1, 1); // fwd

            deltaElevation = deltaElevation + elv;
        
        } else {
            elv = z;
            g.fillRect(col + 1, row + 1, 1, 1); // fwd down
            row = row + 1;

            deltaElevation = deltaElevation + elv;
        }

        return deltaElevation + drawLowestElevPath(g, data, row, col + 1);    
    }

    /**
     * Generate all west-to-east paths, find the one with the lowest total
     * elevation change, and return the index of the row that path starts on.
     *
     * @param g - the graphics context to use
     * @param grid - the 2D array of elevation values
     * @return the index of the row where the lowest elevation-change path
     * starts.
     */
    public static int indexOfLowestElevPath(Graphics g, int[][] data) {

        int minElv = drawLowestElevPath(g, data, 0, 0);
        int bestRow = 0;
        int row = 0;

        for (; row < data.length - 1; row++) {
            drawLowestElevPath(g, data, row, 0);
            if ((drawLowestElevPath(g, data, row, 0)) < minElv) {
                minElv = drawLowestElevPath(g, data, row, 0);
                bestRow = row;
            }
        }

        return bestRow;
    }
    
}
