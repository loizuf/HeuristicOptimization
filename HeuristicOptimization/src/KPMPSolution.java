import java.io.*;
import java.util.*;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class KPMPSolution implements Comparable<KPMPSolution>, Serializable {

    private class Arc implements Comparable<Arc>, Serializable {
        private int page;
        // start and end are just names not the spineOrderIndex
        private int start, end;

        private Arc(int start, int end){
            this.start = start;
            this.end = end;

            page = -1;
        }

        /*
        private Arc(Arc arc){
            this.start = arc.getStart();
            this.end = arc.getEnd();
            this.page = arc.getPage();
        }*/

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

        @Override
        public String toString(){
            return "(" + this.start + "," + this.end + ")";
        }
    }

    // Index is the name of the vertex, value is the current place on the spine
    private int value;

    private int[] SpineOrder;
    private ArrayList<Arc>[] ArcsPerPage;

    public KPMPSolution(int pageNumber, int vertexNumber) {
        SpineOrder = new int[vertexNumber];
        ArcsPerPage = new ArrayList[pageNumber];
        for(int i = 0; i < pageNumber; i++){
            ArcsPerPage[i] = new ArrayList<Arc>();
        }
    }

    public void setNewSpineOrder(int[] newOrder){
        SpineOrder = newOrder;
    }

    // gives back the current index of vertex with the NAME name
    public int getSpineOrderIndex(int name) {
        return SpineOrder[name];
    }

    public int getValue() {
        return value;
    }

    public int[] getSpineOrder() {
        return SpineOrder;
    }

    public ArrayList<Arc>[] getArcsPerPage() {
        return ArcsPerPage;
    }

    /*
        ** called during initial solution construction to create a new arc and
        ** add it to the page specified
         */
    public void addArc(int nameA, int nameB, int page) {
        Arc newArc = new Arc(nameA, nameB);
        newArc.setPage(page);
        ArcsPerPage[page].add(newArc);
        value += objectiveFunctionAddArc(newArc, page);

    }

    // Uses index of spine order to switch two vertices
    public void switchSpineVertex(int firstVertex, int secondVertex){
        int temp = SpineOrder[firstVertex];
        SpineOrder[firstVertex] = SpineOrder[secondVertex];
        SpineOrder[secondVertex] = temp;
        value += objectiveFunctionVertexSwap(firstVertex, secondVertex);
    }

    /*
    ** Moves the given arc from its current page to
    * the page specified in the arguments
     */
    public void moveArc(Arc arc, int toPage){
        int fromPage = arc.getPage();
        ArcsPerPage[fromPage].remove(arc);
        ArcsPerPage[toPage].add(arc);
        arc.setPage(toPage);
        value += objectiveFunctionArcMove(arc, fromPage, toPage);
    }

    // returns best neighbour of the MoveArc Neighbourhood
    public KPMPSolution getBestArcMoveNeighbour(){
        KPMPSolution currentBest = this;
        KPMPSolution newSolution;
        for(int i=0; i<ArcsPerPage.length; i++){
            for(Arc arc : ArcsPerPage[i]){
                for(int j=0; j<ArcsPerPage.length; j++){
                    newSolution = deepClone(this);
                    newSolution.moveArc(arc, j);
                    if(newSolution.compareTo(currentBest)<0){
                        currentBest = deepClone(newSolution);
                    }
                }
            }
        }
        return currentBest;
    }

    // returns first better neighbour of the MoveArc Neighbourhood
    public KPMPSolution getFirstArcMoveNeighbour(){
        KPMPSolution currentBest = this;
        KPMPSolution newSolution;
        for(int i=0; i<ArcsPerPage.length; i++){
            for(Arc arc : ArcsPerPage[i]){
                for(int j=0; j<ArcsPerPage.length; j++){
                    newSolution = deepClone(this);
                    newSolution.moveArc(arc, j);
                    if(newSolution.compareTo(currentBest)<0){
                        return newSolution;
                    }
                }
            }
        }
        return currentBest;
    }

    // this still doesn't work
    public KPMPSolution getRandomArcMoveNeighbour(){
        Random rand = new Random();
        int randFromPageNumber;
        do{
            randFromPageNumber = rand.nextInt(ArcsPerPage.length-1);
        }while(ArcsPerPage[randFromPageNumber].size()<1);
        int randToPageNumber;
        do{
            randToPageNumber = rand.nextInt(ArcsPerPage.length-1);
        }while(randFromPageNumber==randToPageNumber);
        int randArcNumber = rand.nextInt(ArcsPerPage[randFromPageNumber].size()-1);
        Arc randArc = ArcsPerPage[randFromPageNumber].get(randArcNumber);
        System.out.println("arc: " + randArc + ", from: " + randFromPageNumber + ", to: "+ randToPageNumber);
        KPMPSolution newSolution = deepClone(this);
        newSolution.moveArc(randArc, randToPageNumber);
        System.out.println(newSolution.getValue());
        return newSolution;
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

    private int objectiveFunctionVertexSwap(int i, int j){
        int deltaValue = 0;
        for (int c = 0; c < ArcsPerPage.length; c++){
            Iterator<Arc> itr = ArcsPerPage[c].iterator();
            while(itr.hasNext()){
                Arc currentArc = itr.next();
                if (    getSpineOrderIndex(currentArc.getStart()) == i ||
                        getSpineOrderIndex(currentArc.getStart()) == j ||
                        getSpineOrderIndex(currentArc.getEnd()) == i ||
                        getSpineOrderIndex(currentArc.getEnd()) == j){
                    deltaValue += objectiveFunctionArcMove(currentArc, currentArc.getPage(), currentArc.getPage());
                }
            }
        }
        return deltaValue;

    }

    // We only need to check two pages (from and to)
    private int objectiveFunctionArcMove(Arc arc, int fromPage, int toPage){
        int deltaValue = 0;

        ArrayList<Arc> page = ArcsPerPage[toPage];
        Iterator<Arc> itr = page.iterator();
        while (itr.hasNext()) {
            Arc currentArc = itr.next();
            if (doArcsCross(arc, currentArc)){
                deltaValue++;
            }
        }

        page = ArcsPerPage[fromPage];
        itr = page.iterator();
        while (itr.hasNext()) {
            Arc currentArc = itr.next();
            if (doArcsCross(arc, currentArc)){
                deltaValue--;
            }
        }

        return deltaValue;
    }

    private int objectiveFunctionAddArc(Arc arc, int toPage){
        int deltaValue = 0;

        ArrayList<Arc> page = ArcsPerPage[toPage];
        Iterator<Arc> itr = page.iterator();
        while (itr.hasNext()) {
            Arc currentArc = itr.next();
            if (doArcsCross(arc, currentArc)){
                System.out.println("found a cross with:" + arc + " " + currentArc);
                deltaValue++;
            }
        }
        return deltaValue;
    }

    private boolean doArcsCross(Arc arc1, Arc arc2){
        int start1, end1, start2, end2;

        if(arc1.compareTo(arc2) <= 0){
            start1 = min(getSpineOrderIndex(arc1.getStart()), getSpineOrderIndex(arc1.getEnd()));
            end1 =  max(getSpineOrderIndex(arc1.getStart()), getSpineOrderIndex(arc1.getEnd()));
            start2 = min(getSpineOrderIndex(arc2.getStart()), getSpineOrderIndex(arc2.getEnd()));
            end2 =  max(getSpineOrderIndex(arc2.getStart()), getSpineOrderIndex(arc2.getEnd()));
        }
        else{
            start2 = min(getSpineOrderIndex(arc1.getStart()), getSpineOrderIndex(arc1.getEnd()));
            end2 =  max(getSpineOrderIndex(arc1.getStart()), getSpineOrderIndex(arc1.getEnd()));
            start1 = min(getSpineOrderIndex(arc2.getStart()), getSpineOrderIndex(arc2.getEnd()));
            end1 =  max(getSpineOrderIndex(arc2.getStart()), getSpineOrderIndex(arc2.getEnd()));
        }

        if (start1 < start2 && end1 < end2 && start2 < end1){
            return true;
        }

        return false;
    }

    public static KPMPSolution deepClone(Object object) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (KPMPSolution) ois.readObject();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int compareTo(KPMPSolution o) {
        return Integer.compare(value, o.getValue());
    }
}
