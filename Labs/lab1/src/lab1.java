import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;

/**
 * file: lab1.java
 *
 * CSCI-331 Lab 1
 *
 * @author Derek Garcia
 **/
public class lab1 {

    private static class Coordinate{
        public int x;
        public int y;

        public Coordinate(int x, int y){
            this.x = x;
            this.y = y;
        }
        @Override
        public String toString(){
            return "x: " + this.x + " y: " + this.y;
        }
    }



//    private class Terrain extends Coordinate{
//
//        public Terrain(int x, int y, Color pxlColor) {
//            super(x, y);
//        }
//    }
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
    private static void doAStarSearch(BufferedImage terrain){

    }

    private static LinkedList<Coordinate> pathFileToCoords(String pathFile) throws Exception {
        try{
            BufferedReader br = new BufferedReader(new FileReader(pathFile));
            LinkedList<Coordinate> coords = new LinkedList<>();
            while(br.ready()){
                String[] points = br.readLine().split(" ");
                coords.add(new Coordinate(Integer.parseInt(points[0]), Integer.parseInt(points[1])));
            }
            br.close();
            return coords;
        } catch (Exception e){
            throw new Exception("Unable to Read Path File");
        }
    }

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
            LinkedList<Coordinate> goals = pathFileToCoords(args[2]);
            drawPath(terrain, goals);
            ImageIO.write(terrain, "png", new File(args[3]));
        } catch (Exception e){
            System.err.println("Failed to load arguments | Message: " + e.getMessage());
        }

    }
}
