public class Vertex {
    int name, spineIndex;

    public Vertex(int name) {
        this.name = name;
        this.spineIndex = -1;
    }

    public int getSpineIndex() {
        return spineIndex;
    }

    public void setSpineIndex(int spineIndex) {
        this.spineIndex = spineIndex;
    }
}
