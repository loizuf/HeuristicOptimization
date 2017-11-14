import java.util.*;

public class KPMPSolution {

    private class Arc implements Comparable<Arc> {
        private int page;
        // start and end are just names not the spineOrderIndex
        private int start, end;
        private KPMPSolution solution;

        public Arc(int start, int end){
            this.start = start;
            this.end = end;

            page = -1;
        }

        private int getStart() {
            return start;
        }

        private int getEnd() {
            return end;
        }

        private int getPage() {
            return page;
        }

        private void setPage(int page) {
            this.page = page;
        }

        @Override
        public int compareTo(Arc compareArc) {
            int minimum = start;
            int maximum = end;
            int compMinimum = compareArc.getStart();
            int compMaximum = compareArc.getEnd();
            if (SpineOrder[start] > SpineOrder[end]){
                minimum = end;
                maximum = start;
            }
            if ((SpineOrder[compareArc.getStart()]) > SpineOrder[compareArc.getEnd()]){
                compMinimum = compareArc.getEnd();
                compMaximum = compareArc.getStart();
            }
            if(minimum == compMinimum){
                return Integer.compare(maximum, compMaximum);
            }
            return Integer.compare(minimum, compMinimum);
        }
    }

    // Index is the name of the vertex, value is the current place on the spine
    private int[] SpineOrder;
    private Set<Arc>[] ArcsPerPage;

    public KPMPSolution(int pageNumber, int vertexNumber) {
        SpineOrder = new int[vertexNumber];
        ArcsPerPage = new TreeSet[pageNumber];
    }

    public void setNewSpineOrder(int[] newOrder){
        SpineOrder = newOrder;
    }

    // gives back the current index of vertex with the NAME name
    public int getSpineOrderIndex(int name) {
        return SpineOrder[name];
    }

    // Uses index of spine order to switch two vertices
    public void switchSpineVertex(int firstVertex, int secondVertex){
        int temp = SpineOrder[firstVertex];
        SpineOrder[firstVertex] = SpineOrder[secondVertex];
        SpineOrder[secondVertex] = temp;
    }

    public void moveArc(Arc arc, int toPage){
        ArcsPerPage[arc.getPage()].remove(arc);
        ArcsPerPage[toPage].add(arc);
        arc.setPage(toPage);
        
    }

    /* TODO implement
     * Here we want to go through the ArcsPerPage and compare them for crossings using the formula
     * Ignore arcs with:
     *      - Both endpoints to the left of i
     *      - Both endpoints to the right of j
     *      - Both endpoints between i and j
     *      - One endpoint to the left of i, one endpoint to the right of j
     *
     * For VertexSwap, this needs to be exclusive, for ArcMove, these checks can be inclusive of i,j
     * Also VertexSwap needs to compute for every page, ArcMove just for the new one
     *
     * Seperate Methods for: Complete, VertexSwap, ArcMove
     */

    public int objectiveFunction(){
        int penaltyValue = 0;
        for (int c = 0; c < ArcsPerPage.length; c++){
            for (int d = 0; d < ArcsPerPage[c].size()-1; d++){
            }
        }
        return penaltyValue;
    }

    public int objectiveFunctionVertexSwap(int i, int j){
        return 0;
    }

    public int objectiveFunctionArcMove(int i, int j){
        return 0;
    }

}
