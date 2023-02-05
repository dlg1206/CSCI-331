

/**
 * file: lab1.java
 *
 * CSCI-331 Lab 1
 *
 * @author Derek Garcia
 **/
public class lab1 {
    // 1px = 10.29m x 7.55m area

    // terrain image to read? (395x500)

    // elevation file (500x400-5)
    // maps 1 px

    // path-file
    // x0, y0: origin
    // xn, yn
    public static void main(String[] args) {
        // Check for correct args
        if(args.length != 4){
            System.err.println("Incorrect Number of arguments");
            System.err.println("Expected usage: java lab1 <terrain-image> <elevation-file> <path-file> <output-image-filename>");
        }

    }
}
