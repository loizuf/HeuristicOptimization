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
    Arc arc = new Arc(1,2);

    public KPMPSolution(int pageNumber, int vertexNumber) {
        SpineOrder = new int[vertexNumber];
        ArcsPerPage = new TreeSet[pageNumber];

        arc.getPage();
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
    
    public int objectiveFunction(int i, int j){
        // TODO implement
        return 0;
    }

}
