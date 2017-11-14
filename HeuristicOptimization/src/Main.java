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
            System.out.println("Current objective value: " + sol.getValue());
            KPMPSolution first = sol.getFirstArcMoveNeighbour();
            System.out.println("Current first-objective value: " + first.getValue());
            KPMPSolution best = sol.getBestArcMoveNeighbour();
            System.out.println("Current best-objective value: " + best.getValue());
            for (int i = 0; i < 100; i++) {
                KPMPSolution random = sol.getRandomArcMoveNeighbour();
                //System.out.println("Current random-objective value: " + random.getValue());
            }




        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
