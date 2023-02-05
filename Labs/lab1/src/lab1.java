import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;

/**
 * file: lab1.java
 *
 * CSCI-331 Lab 1
 *
 * @author Derek Garcia
 **/
public class lab1 {

    private class Coordinate{
        public int x;
        public int y;

        public Coordinate(int x, int y){
            this.x = x;
            this.y = y;
        }
    }
    // 1px = 10.29m x 7.55m area

    // terrain image to read? (395x500)

    // elevation file (500x400-5)
    // maps 1 px

    // path-file
    // x0, y0: origin
    // xn, yn
//    private enum TERRAIN_TYPE {
//        OPEN_LAND,
//
//    }

    private static void drawPath(BufferedImage terrain, LinkedList<Coordinate> path){
        for(Coordinate c : path)
            terrain.setRGB(c.x, c.y, Color.red.getRGB());
    }

    public static void main(String[] args) {
        // Check for correct args
        if(args.length != 4){
            System.err.println("Incorrect Number of arguments");
            System.err.println("Expected usage: java lab1 <terrain-image> <elevation-file> <path-file> <output-image-filename>");
            return;
        }

        // attempt to load objects
        try{
            BufferedImage terrain = ImageIO.read(new File(args[0]));
            drawPath(terrain, new LinkedList<>());
            ImageIO.write(terrain, "png", new File(args[3]));
        } catch (Exception e){
            System.err.println("Failed to load arguments | Message: " + e.getMessage());
        }

    }
}
