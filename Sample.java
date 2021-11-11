public class Sample {
    // By Joe
    // This class represents individual images from our training file to use during the learning process.
    int[] img;
    String printableImg;

    public Sample(String imgFromFile) {
        this.printableImg = imgFromFile;

        // Here the Sample class flattens the image into an array of binary inputs that can be used in the Hopfield algorithm
        String flatImg = imgFromFile.replaceAll("\n", "");
        img = new int[flatImg.length()];

        int i = 0;
        for (char c : flatImg.toCharArray()) {
            if (c == ' ') {
                img[i] = -1;
            }
            else if (c == 'O') {
                img[i] = 1;
            }
            else {
                System.out.println("Error in image...");
            }
            i++;
        }
    }
}