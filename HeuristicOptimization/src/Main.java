
import java.util.*;

public class Main {

    private static int arcNumber;
    public static final int TIMEOUT = 300; //in seconds

    public static void main(String[] args) {
        //StopwatchCPU time = new StopwatchCPU();
        //long start = System.currentTimeMillis();
        try {
            KPMPInstance inst = KPMPInstance.readInstance("instances/instance-05.txt");

            List<List<Integer>> list = inst.getAdjacencyList();
            arcNumber = 0;
            for (int i = 0; i < list.size(); i++) {
                arcNumber += list.get(i).size();
            }
            KPMPSolution sol = deterministicConstruction(inst);
            System.out.println(sol);
            //sol = sol.simulatedAnnealing(1, sol.getValue(), -1, -1, 0.95, arcNumber*arcNumber*inst.getK());
            sol = sol.localSearch(0,1,false,-1);
            //sol = sol.vndSearch();
            System.out.println(sol);
            //System.out.println(time.elapsedTime());





            /*
            int[] order = {0, 1, 2, 3, 4, 5, 6, 7};
            sol.setNewSpineOrder(order);

            boolean[][] mat = inst.getAdjacencyMatrix();
            for (int i=0; i< mat.length; i++) {
                for (int j=i; j< mat[i].length; j++) {
                    if(mat[i][j]){
                        //System.out.println("added arc (" + i + "," + j + ")");
                        sol.addArc(i, j,0);
                    }
                }
            }

            System.out.println();
            System.out.println("All arcs initialized to page 0");
            System.out.println("Current objective value: " + sol.getValue() + "\n");

            KPMPSolution first = sol.getFirstSpineSwapNeighbour();
            System.out.println("First: \n" + first);

            KPMPSolution best = sol.getBestSpineSwapNeighbour();
            System.out.println("\nBest: \n" + best);

            /*KPMPSolution bestLocalSearch = sol.simulatedAnnealing(-1,4,-1,3,0.95,8);
            System.out.println("\nBest VND:" + bestLocalSearch);*/

            /*for (int i = 0; i < 10; i++) {
                KPMPSolution random = sol.getRandomSpineSwapNeighbour();
                System.out.println("\nRandom: \n" + random);

        */




        }catch(Exception e){
            e.printStackTrace();
        }
        //long end = System.currentTimeMillis();
        //System.out.println(end-start);
    }

    // this creates an initial solution which is hopefully not all that bad
    private static KPMPSolution deterministicConstruction(KPMPInstance inst){
        // Setup
        KPMPSolution sol = new KPMPSolution(inst.getK(), inst.getNumVertices());
        List<List<Integer>> list = inst.getAdjacencyList();

        // Sort vertices by degree
        int[][] array = new int[list.size()][2];
        for(int i=0; i< list.size(); i++){
            array[i][0] =  i;
            array[i][1] = list.get(i).size();
        }
        java.util.Arrays.sort(array, new java.util.Comparator<int[]>() {
            public int compare(int[] a, int[] b) {
                return Integer.compare(b[1], a[1]);
            }
        });
        int[] order = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            if(i%2 == 0){
                order[i/2] = array[i][0];
            } else {
                order[array.length - 1 - i/2] = array[i][0];
            }
        }
        sol.setNewSpineOrder(order);

        // distribute arcs among pages
        // one starting point is alsways the same page
        /*
        int currentPage = 0;
        boolean[][] mat = inst.getAdjacencyMatrix();
        for(int i = 0; i < mat.length; i++){
            boolean change = false;
            for (int j = i; j < mat[i].length; j++) {
                if(mat[i][j]){
                    sol.addArc(i, j, currentPage);
                    change = true;
                }
            }
            if (change) {
                currentPage++;
                if (currentPage == inst.getK()) {
                    currentPage = 0;
                }
            }
        }
        return sol;*/

        // The part below this point sorts arcs by their length
        int arcNumber = 0;
        for (int i = 0; i < list.size(); i++) {
            arcNumber += list.get(i).size();
        }
        int [][] array2 = new int[arcNumber/2][3];
        int c = 0;
        boolean[][] mat = inst.getAdjacencyMatrix();
        for(int i = 0; i < mat.length; i++){
            for (int j = i; j < mat[i].length; j++) {
                if(mat[i][j]){
                    array2[c][0] = i;
                    array2[c][1] = j;
                    array2[c][2] = j-i;
                    c++;
                }
            }
        }
        // This sorts arcs by their length
        java.util.Arrays.sort(array2, new java.util.Comparator<int[]>() {
            public int compare(int[] a, int[] b) {
                return Integer.compare(a[2], b[2]);
            }
        });
        for (int i = 0; i < array2.length; i++) {
            sol.addArcToBestPage(array2[i][0], array2[i][1]);
        }
        return sol;

        // Now we add arcs to their repsective best Page
    }

    // this returns a non deterministic initial solution which might diversify starting points
    private static KPMPSolution randomConstruction(KPMPInstance inst){
        // Setup
        KPMPSolution sol = new KPMPSolution(inst.getK(), inst.getNumVertices());
        List<List<Integer>> list = inst.getAdjacencyList();

        // Sort vertices by degree
        int[][] array = new int[list.size()][2];
        for(int i=0; i< list.size(); i++){
            array[i][0] =  i;
            array[i][1] = list.get(i).size();
        }
        java.util.Arrays.sort(array, new java.util.Comparator<int[]>() {
            public int compare(int[] a, int[] b) {
                return Integer.compare(b[1], a[1]);
            }
        });
        int[] order = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            if(i%2 == 0){
                order[i/2] = array[i][0];
            } else {
                order[array.length - 1 - i/2] = array[i][0];
            }
        }
        shuffleArray(order);
        sol.setNewSpineOrder(order);

        // distribute arcs among pages
        // seperate the arc set into k random subsets

        int[] rands = new int[arcNumber];
        for (int i = 0; i < arcNumber; i++) {
            rands[i] = i%inst.getK();
        }
        shuffleArray(rands);


        int currentPage = 0;
        boolean[][] mat = inst.getAdjacencyMatrix();
        for(int i = 0; i < mat.length; i++){
            for (int j = i; j < mat[i].length; j++) {
                if(mat[i][j]){
                    sol.addArc(i, j, rands[currentPage]);
                    currentPage++;
                }
            }
        }
        return sol;
    }

    static void shuffleArray(int[] ar)
    {
        // If running on Java 6 or older, use `new Random()` on RHS here
        Random rnd = new Random();
        for (int i = ar.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            int a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }

}
