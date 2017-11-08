import static java.lang.Math.max;
import static java.lang.Math.min;

public class Arc implements Comparable<Arc> {

    private int page;
    // start and end are just names not the spineOrderIndex
    private int start, end;
    private KPMPSolution solution;

    public Arc(int start, int end, KPMPSolution solution){
        this.solution = solution;
        this.start = start;
        this.end = end;

        page = -1;
    }

    public void sortStartAndEnd(){

    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public int compareTo(Arc compareArc) {
        if (solution.getSpineOrderIndex(start) <= solution.getSpineOrderIndex(end)){
            int minimum = start;
            int maximum = end;
        }
        if (solution.getSpineOrderIndex(compareArc.start) <= solution.getSpineOrderIndex(end)){
            int minimum = start;
            int maximum = end;
        }
        return 0;
    }
}
