public class Sample {
    // This class represents individual "samples" from our training file to use during the learning process.
    int[] img;
    String printableImg;

    public Sample(String imgFromFile) {
        this.printableImg = imgFromFile;
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