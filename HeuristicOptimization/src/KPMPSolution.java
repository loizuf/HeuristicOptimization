import javafx.scene.paint.Stop;
import org.omg.CORBA.NO_IMPLEMENT;

import java.io.*;
import java.util.*;

import static java.lang.Math.*;

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
    private String instName;

    // Constructor
    public KPMPSolution(int pageNumber, int vertexNumber, String name) {
        instName = name;
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

    // called during initial solution construction to create a new arc and add it to the page specified
    public void addArcToBestPage(int nameA, int nameB) {
        Arc newArc = new Arc(nameA, nameB);
        newArc.setPage(0);
        int addPage = getBestPageForArc(newArc);
        newArc.setPage(addPage);
        ArcsPerPage[addPage].add(newArc);
        value += objectiveFunctionAddArc(newArc, addPage);
    }

    public void addArcToRandomPage(int nameA, int nameB) {
        Arc newArc = new Arc(nameA, nameB);
        Random r = new Random();
        int addPage = r.nextInt(ArcsPerPage.length);
        newArc.setPage(addPage);
        ArcsPerPage[addPage].add(newArc);
        value += objectiveFunctionAddArc(newArc, addPage);
    }

    // returns best neighbour of the MoveArc Neighbourhood
    private int getBestPageForArc(Arc arc){
        int bestPage = 0;
        KPMPSolution currentBest = this;
        KPMPSolution newSolution;
        for(int k=0; k<ArcsPerPage.length; k++){
            newSolution = deepClone(this);
            newSolution.moveArc(arc, k);
            if(newSolution.compareTo(currentBest)<0){
                currentBest = deepClone(newSolution);
                bestPage = k;
            }
        }
        return bestPage;
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
        System.out.println("Getting the best vertex swap now...");
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
        System.out.println("Getting the best arc movement now...");
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
        //System.out.println("getteing next random arc movement neighbour");
        Random rand = new Random();
        int randFromPageNumber;
        do{
            randFromPageNumber = rand.nextInt(ArcsPerPage.length);
        }while(ArcsPerPage[randFromPageNumber].size()<1);
        int randToPageNumber;
        do{
            randToPageNumber = rand.nextInt(ArcsPerPage.length);
        }while(randFromPageNumber==randToPageNumber);
        int randArcNumber = rand.nextInt(ArcsPerPage[randFromPageNumber].size());
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
    * neighborhood specifies which neighborhood structure is being used to determine the neighbor (>=0 for spine swap, <0 for arc movement)
     */
    private KPMPSolution getNeighbor(int step, int neighborhood){

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
    * neighborhood specifies which neighborhood structure is being used to determine the neighbor (>=0 for arc movement, <0 for spine swap)
    * improvementRequired is a stopping criterium, if it's set to true, we do not continue local search if there was no improvement in the
    * previous step
    * maxIterations is a stopping criterium, if it's set to greater than 0 local search will stop after maxIterations iterations,
    * if it's set to <= 0, it is ignored.
    * TODO: limit this to 15 minutes CPU time
    */
    public KPMPSolution localSearch(int step, int neighborhood, boolean improvementRequired, int maxIterations){
        StopwatchCPU time = new StopwatchCPU();
        KPMPSolution bestSolution = this;
        KPMPSolution solution = this;
        boolean stop = false;
        int iteration = maxIterations;

        while(!stop) {
            solution = solution.getNeighbor(step, neighborhood);
            if(solution.compareTo(bestSolution) < 0){
                bestSolution = deepClone(solution);
            }
            else{
                if(improvementRequired){
                    stop = true;
                }
            }
            if(iteration > 0){
                    iteration -= 1;
                    stop = (iteration == 0);
            }
            if(time.elapsedTime() > Main.TIMEOUT){
                stop = true;
            }

        }
        return bestSolution;
    }

    public KPMPSolution vndSearch(){
        StopwatchCPU time = new StopwatchCPU();
        KPMPSolution currentSolution = this;
        int neighborhood = -1; //start with the smaller neighborhood, in our case spine swap (<0)

        do{
            KPMPSolution nextSolution = currentSolution.getNeighbor(1, neighborhood); //get best neighbour wrt currently considered neigh
            if(nextSolution.compareTo(currentSolution) < 0){
                currentSolution = nextSolution;
                neighborhood = -1;
            }
            else{
                neighborhood++;
            }
        }while(time.elapsedTime() < Main.TIMEOUT || neighborhood <= 0);
        return currentSolution;
    }

      /*
      *TODO limit this to 15 min CPU time
      * currently only geometric cooling supported with T = T * alpha, where alpha is a given parameter < 1
      * neighborhood - specifies which neighborhood structure to use (<0 for spine swap, >=0 for arc movement)
      * initialTemp - set initial temperature
      * endTemp - if negative then it's ignored, otherwise the search stops when endTemp temperature is reached
      * maxNoImprovement - if negative it's ignored, otherwise the search stops when there were no improvements
      * over the last maxNoImprovement number of iterations
      * int equilibrium - the number of iterations before the temperature is changed
      */
    public KPMPSolution simulatedAnnealing(int neighborhood, double initAccProb, double endTemp, int maxNoImprovement, double alpha, int givenEquilibrium){
        //long timeStart  = System.currentTimeMillis();

        StopwatchCPU time = new StopwatchCPU();
        int t = 0;
        double temperature = getInitialTemperature(initAccProb);
        boolean stop = false;
        KPMPSolution currentSolution = this;
        int noImprovements = 0;
        int equilibrium = givenEquilibrium;
        int visualizationCounter = 0;
        int bestValue = currentSolution.value;
        try {
            FileWriter fwbest = null;
            FileWriter fwcurrent = null;
            BufferedWriter bwbest = null;
            BufferedWriter bwcurrent = null;
            fwbest = new FileWriter("_bestResult.csv");
            fwcurrent = new FileWriter("currentResult.csv");
            bwbest = new BufferedWriter(fwbest);
            bwcurrent = new BufferedWriter(fwcurrent);
            bwbest.write("index;value;\n");
            bwcurrent.write("index;value;\n");



            do {
                do {
                    double r = Math.random();
                    KPMPSolution nextSolution;
                    if (r <0.05) {
                        nextSolution = currentSolution.getNeighbor(0, 1);
                    } else {
                        nextSolution = currentSolution.getNeighbor(0, -1);
                    }
                    if (nextSolution.compareTo(currentSolution) < 0) {
                        currentSolution = nextSolution;
                        if(bestValue > currentSolution.value){
                            bestValue = currentSolution.value;
                            bwbest.write(visualizationCounter + ";" + currentSolution.getValue() + ";\n");
                        }
                    } else {
                        r = Math.random();
                        if (r < exp(-abs(currentSolution.getValue() - nextSolution.getValue()) / temperature)) {
                            currentSolution = nextSolution;
                            noImprovements++;
                        }
                    }
                    bwcurrent.write(visualizationCounter + ";" + currentSolution.getValue() + ";\n");
                    visualizationCounter++;
                    t++;
                    equilibrium--;
                } while (equilibrium != 0);
                //System.out.println("tempchange" + temperature);
                equilibrium = givenEquilibrium;
                temperature = temperature * alpha;
                //long timeNow = System.currentTimeMillis();
                double timeElapsed = time.elapsedTime();
                if (timeElapsed > Main.TIMEOUT || (endTemp >= 0 && temperature < endTemp) || (maxNoImprovement >= 0 && noImprovements >= maxNoImprovement)) {
                    stop = true;
                    System.out.println("Simulated annealing took: " + timeElapsed + " seconds");
                }
            } while (!stop);

            bwbest.close();
            fwbest.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        return currentSolution;
    }

    private double getInitialTemperature(double initAcc) {
        double average = 0;
        for (int i = 0; i < ArcsPerPage.length; i++) {
            KPMPSolution solution;
            do{
                solution = this.getRandomArcMoveNeighbour();

            } while (solution.value<= this.value);
            average += solution.value;
        }
        average = average/ArcsPerPage.length;
        double initialAcceptance = initAcc;
        return (average/Math.log(initialAcceptance));
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

    public List<Integer> getVertexOrder(){
        ArrayList<Integer> spineList = new ArrayList<Integer>() {{ for (int i : SpineOrder) add(i); }};
        List<Integer> spineOrder = new LinkedList<>();
        for (int i = 0; i < spineList.size(); i++) {
            spineOrder.add(spineList.indexOf(i));
        }
        return spineOrder;
    }

    public List<int[]> getAllArcs(){
        List<int[]> arcs = new ArrayList<>();

        for(int i = 0; i<ArcsPerPage.length; i++){
            for(int j = 0; j<ArcsPerPage[i].size(); j++){
                int[] arc = new int[3];
                arc[0] = ArcsPerPage[i].get(j).getStart();
                arc[1] = ArcsPerPage[i].get(j).getEnd();
                arc[2] = ArcsPerPage[i].get(j).getPage();
                arcs.add(arc);
            }
        }
        return arcs;
    }

    @Override
    public int compareTo(KPMPSolution o) {
        return Integer.compare(value, o.getValue());
    }

    @Override
    public String toString(){
        String toString = "Spine-Order: ";
        ArrayList<Integer> spineList = new ArrayList<Integer>() {{ for (int i : SpineOrder) add(i); }};
        for (int i = 0; i < SpineOrder.length; i++) {
            toString += spineList.indexOf(i) + " ";
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
