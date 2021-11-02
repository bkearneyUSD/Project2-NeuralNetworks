public class Weights {
    // This class is designed to store data read in from the saved weights file.
    float[][] weights;
    String[] patterns;
    
    public Weights(float[][] weights, String[] patterns) {
        this.weights = weights;
        this.patterns = patterns;
    }
}