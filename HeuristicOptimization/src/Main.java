
import java.io.FileWriter;
import java.util.*;

public class Main {

    private static int arcNumber;
    public static final int TIMEOUT = 60; //in seconds

    public static void main(String[] args) {

        for (int j = 1; j < 16; j++) {

            try {
                String path = "instances/instance-";
                if (j<10) path += "0";
                path += j + ".txt";
                KPMPInstance inst = KPMPInstance.readInstance(path);

                List<List<Integer>> list = inst.getAdjacencyList();
                arcNumber = 0;
                for (int i = 0; i < list.size(); i++) {
                    arcNumber += list.get(i).size();
                }
                KPMPSolution sol = deterministicConstruction(inst);
                //System.out.println(sol);
                sol = sol.simulatedAnnealing(1, sol.getValue(), 0.0000000001, -1, 0.95, arcNumber);
                //sol = sol.localSearch(0,1,false,-1);
                //sol = sol.vndSearch();
                //System.out.println(sol);
                //System.out.println(time.elapsedTime());

                FileWriter fw = new FileWriter(inst.getName() + "_SA.txt");
                //fw.write(sol.toString());
                getSolutionWriter(sol).write(fw);
                fw.close();
                System.out.println(path + " done!\n" + sol.toString() + "\n\n");


               // getSolutionWriter(sol).print();

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
    }

    // this creates an initial solution which is hopefully not all that bad
    private static KPMPSolution deterministicConstruction(KPMPInstance inst){
        // Setup
        KPMPSolution sol = new KPMPSolution(inst.getK(), inst.getNumVertices(), inst.getName());
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
            sol.addArc(array2[i][0], array2[i][1], i%inst.getK());
        }
        return sol;

        // Now we add arcs to their repsective best Page
    }

    // this returns a non deterministic initial solution which might diversify starting points
    private static KPMPSolution randomConstruction(KPMPInstance inst){
        // Setup
        KPMPSolution sol = new KPMPSolution(inst.getK(), inst.getNumVertices(), inst.getName());
        List<List<Integer>> list = inst.getAdjacencyList();

        // Sort vertices by degree
        int[] order = new int[list.size()];
        for(int i=0; i< list.size(); i++){
            order[i] =  i;
        }
        shuffleArray(order);
        sol.setNewSpineOrder(order);

        boolean[][] mat = inst.getAdjacencyMatrix();
        for(int i = 0; i < mat.length; i++){
            for (int j = i; j < mat[i].length; j++) {
                if(mat[i][j]){
                        sol.addArcToRandomPage(i, j);
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

    static KPMPSolutionWriter getSolutionWriter(KPMPSolution solution){
        KPMPSolutionWriter solutionWriter = new KPMPSolutionWriter(solution.getArcsPerPage().length);
        solutionWriter.setSpineOrder(solution.getVertexOrder());

        List<int[]> arcs = solution.getAllArcs();
        for (int[] arc: arcs) {
            solutionWriter.addEdgeOnPage(arc[0], arc[1],arc[2]);
        }
        return solutionWriter;
    }

}
