public class Sample {
    // This class represents individual "samples" from our training file to use during the learning process.
    String img;
    String printableImg;

    public Sample(String imgFromFile) {
        this.printableImg = imgFromFile;
        this.img = imgFromFile.replaceAll("\n", "");
    }
}