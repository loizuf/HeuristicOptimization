import sun.security.provider.certpath.AdjacencyList;

public class Main {

    public static void main(String[] args) {

        try {
            KPMPInstance inst = KPMPInstance.readInstance("instances/instance_test.txt");
            KPMPSolution sol = new KPMPSolution(inst.getK(), inst.getNumVertices());

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

            for (int i = 0; i < 10; i++) {
                KPMPSolution random = sol.getRandomSpineSwapNeighbour();
                System.out.println("\nRandom: \n" + random);
            }




        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // this creates an initial solution which is hopefully not all that bad
    private KPMPSolution deterministicConstruction(){
        return null;
    }

    // this returns a non deterministic initial solution which might diversify starting points
    private KPMPSolution randomConstruction(){
        return null;
    }
}
