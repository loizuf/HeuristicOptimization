import java.io.*;
import java.util.*;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class KPMPSolution implements Comparable<KPMPSolution>, Serializable {

    // Internal Class representation of Arcs
    private class Arc implements Comparable<Arc>, Serializable {
        // start and end are just names not the spineOrderIndex
        private int start, end;
        private int page;

        // Constructor, page is initialized as -1
        private Arc(int start, int end){
            this.start = start;
            this.end = end;

            page = -1;
        }

        /* START Getter */
        private int getStart() {
            return start;
        }

        private int getEnd() {
            return end;
        }

        private int getPage() {
            return page;
        }
        /* END Getter */

        private void setPage(int page) {
            this.page = page;
        }

        // Comparison by first and then by second endpoint
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

    // Constructor
    public KPMPSolution(int pageNumber, int vertexNumber) {
        SpineOrder = new int[vertexNumber];
        ArcsPerPage = new ArrayList[pageNumber];
        for(int i = 0; i < pageNumber; i++){
            ArcsPerPage[i] = new ArrayList<Arc>();
        }
    }

    /* START Getter */
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
    /* END Getter */

    // called during initial solution to set an initial Spine ordering. REMEMBER: Index is NAME, Value is Position
    public void setNewSpineOrder(int[] newOrder){
        SpineOrder = newOrder;
    }

    // called during initial solution construction to create a new arc and add it to the page specified
    public void addArc(int nameA, int nameB, int page) {
        Arc newArc = new Arc(nameA, nameB);
        newArc.setPage(page);
        ArcsPerPage[page].add(newArc);
        value += objectiveFunctionAddArc(newArc, page);

    }

    // Uses NAME of vertex order to switch two vertices
    public void switchSpineVertex(int firstVertex, int secondVertex){
        int temp = SpineOrder[firstVertex];
        SpineOrder[firstVertex] = SpineOrder[secondVertex];
        SpineOrder[secondVertex] = temp;
        value = objectiveFunctionVertexSwap();
    }

    /*here go the other neighbourhoods*/

    // returns best neighbour of the SpineSwap Neighbourhood
    public KPMPSolution getBestSpineSwapNeighbour(){
        KPMPSolution currentBest = this;
        KPMPSolution newSolution;
        for (int i = 0; i < SpineOrder.length-1; i++) {
            for (int j = i+1; j < SpineOrder.length; j++) {

                newSolution = deepClone(this);
                newSolution.switchSpineVertex(i, j);
                if(newSolution.compareTo(currentBest)<0){
                    currentBest = deepClone(newSolution);
                }
            }
        }
        return currentBest;
    }

    // returns first better neighbour of the SpineSwap Neighbourhood
    public KPMPSolution getFirstSpineSwapNeighbour(){
        KPMPSolution currentBest = this;
        KPMPSolution newSolution;
        for (int i = 0; i < SpineOrder.length-1; i++) {
            for (int j = i+1; j < SpineOrder.length; j++) {
                newSolution = deepClone(this);
                newSolution.switchSpineVertex(i, j);
                if(newSolution.compareTo(currentBest)<0){
                    return newSolution;
                }
            }
        }
        return currentBest;
    }

    // returns a random neighbour of the SpineSwap Neighbourhood, not necessarily better
    public KPMPSolution getRandomSpineSwapNeighbour(){
        Random rand = new Random();
        int i = rand.nextInt(getSpineOrder().length);
        int j = rand.nextInt(getSpineOrder().length);
        KPMPSolution newSolution = deepClone(this);
        newSolution.switchSpineVertex(i, j);
        return newSolution;
    }

    // Moves the given arc from its current page to the page specified in the arguments
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
            for(int j=0; j< ArcsPerPage[i].size(); j++){
                for(int k=0; k<ArcsPerPage.length; k++){
                    newSolution = deepClone(this);
                    Arc arc = newSolution.getArcsPerPage()[i].get(j);
                    newSolution.moveArc(arc, k);
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
            for(int j=0; j< ArcsPerPage[i].size(); j++){
                for(int k=0; k<ArcsPerPage.length; k++){
                    newSolution = deepClone(this);
                    Arc arc = newSolution.getArcsPerPage()[i].get(j);
                    newSolution.moveArc(arc, k);
                    if(newSolution.compareTo(currentBest)<0){
                        return newSolution;
                    }
                }
            }
        }
        return currentBest;
    }

    // returns a random neighbour of the MoveArc Neighbourhood, not necessarily better
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
        KPMPSolution newSolution = deepClone(this);
        Arc randArc = newSolution.getArcsPerPage()[randFromPageNumber].get(randArcNumber);
        newSolution.moveArc(randArc, randToPageNumber);
        return newSolution;
    }

    // Incremental evaluation for swapping two vertices
    private int objectiveFunctionVertexSwap(){
        int newValue = 0;
        for (int c = 0; c < ArcsPerPage.length; c++){
            for (int k = 0; k < ArcsPerPage[c].size(); k++) {
                Arc currentArc = ArcsPerPage[c].get(k);
                for (int l = k+1; l < ArcsPerPage[c].size(); l++) {
                    if(doArcsCross(currentArc, ArcsPerPage[c].get(l))){
                        newValue++;
                    }
                }

            }
        }
        return newValue;
    }

    // Incremental evaluation for moving an Arc
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

    // Incremental evaluation for initial adding of arcs to solution
    private int objectiveFunctionAddArc(Arc arc, int toPage){
        int deltaValue = 0;

        ArrayList<Arc> page = ArcsPerPage[toPage];
        Iterator<Arc> itr = page.iterator();
        while (itr.hasNext()) {
            Arc currentArc = itr.next();
            if (doArcsCross(arc, currentArc)){
                deltaValue++;
            }
        }
        return deltaValue;
    }
    /*
    * step specifies step function strategy (0 for random, <0 for first, >0 for best)
    * neighborhood specifies which neighborhood structure is being used to determine the neighbor (<0 for arc movement, >=0 for spine swap)
     */
    public KPMPSolution localSearchStep(int step, int neighborhood){

        if(neighborhood < 0){
            if(step == 0){
                return this.getRandomArcMoveNeighbour();
            }
            else if (step < 0) {
                return this.getFirstArcMoveNeighbour();
            }
            else {
                return this.getBestArcMoveNeighbour();
            }
        }
        else {
            if(step == 0){
                return this.getRandomSpineSwapNeighbour();
            }
            else if (step < 0) {
                return this.getFirstSpineSwapNeighbour();
            }
            else {
                return this.getBestSpineSwapNeighbour();
            }
        }

    }
    /*
    * step specifies step function strategy (0 for random, <0 for first, >0 for best)
    * neighborhood specifies which neighborhood structure is being used to determine the neighbor (<0 for arc movement, >=0 for spine swap)
    * improvementRequired is a stopping criterium, if it's set to true, we do not continue local search if there was no improvement in the
    * previous step
    * maxIterations is a stopping criterium, if it's set to greater than 0 local search will stop after maxIterations iterations,
    * if it's set to <= 0, it is ignored.
    * TODO: limit this to 15 minutes CPU time
    */
    public KPMPSolution localSearch(int step, int neighborhood, boolean improvementRequired, int maxIterations){
        KPMPSolution bestSolution = this;
        KPMPSolution solution = this;
        boolean cont = true;
        int iteration = maxIterations;

        while(cont) {
            System.out.println("Current solution in while "+solution);
            solution = solution.localSearchStep(step, neighborhood);
            if(solution.compareTo(bestSolution) < 0){
                bestSolution = deepClone(solution);
            }
            else{
                if(improvementRequired){
                    cont = false;
                }
            }
            if(iteration > 0){
                    iteration -= 1;
                    cont = !(iteration == 0);
            }

        }
        return bestSolution;
    }

    // checks two arcs for crossing
    private boolean doArcsCross(Arc arc1, Arc arc2){
        int start1, end1, start2, end2;

        start1 = min(getSpineOrderIndex(arc1.getStart()), getSpineOrderIndex(arc1.getEnd()));
        end1 =  max(getSpineOrderIndex(arc1.getStart()), getSpineOrderIndex(arc1.getEnd()));
        start2 = min(getSpineOrderIndex(arc2.getStart()), getSpineOrderIndex(arc2.getEnd()));
        end2 =  max(getSpineOrderIndex(arc2.getStart()), getSpineOrderIndex(arc2.getEnd()));

        if (start1 < start2 && end1 < end2 && start2 < end1){
            return true;
        } else if (start2 < start1 && end2 < end1 && start1 < end2){
            return true;
        }

        return false;

    }

    // creates a clone with actually new subobjects instead of references to old Arcs
    private static KPMPSolution deepClone(Object object) {
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

    @Override
    public String toString(){
        String toString = "Spine-Order: ";
        for (int i = 0; i < SpineOrder.length; i++) {
            toString += SpineOrder[i];
        }

        toString += "\nValue: " + value;
        toString += "\nPages:";
        for (int i = 0; i < ArcsPerPage.length; i++) {
            toString += "\n(";
            for (int j = 0; j < ArcsPerPage[i].size(); j++) {
                toString += getArcsPerPage()[i].get(j);
            }
            toString += ")";
        }
        return toString;
    }
}
