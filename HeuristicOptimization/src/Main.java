import sun.security.provider.certpath.AdjacencyList;

public class Main {

    public static void main(String[] args) {

        try {
            KPMPInstance inst = KPMPInstance.readInstance("C:\\Users\\Riffa\\git_repositories\\HeuristicOptimization\\HeuristicOptimization\\instances\\instance_test.txt");
            System.out.println(inst);
            KPMPSolution sol = new KPMPSolution(inst.getK(), inst.getNumVertices());
            System.out.println(inst.getAdjacencyList());
            boolean[][] mat = inst.getAdjacencyMatrix();
            for (int i=0; i< mat.length; i++) {
                for (int j=0; j< mat[i].length; j++) {
                    if(mat[i][j]){
                        sol.addArc(i, j, 0);
                    }
                }
            }
            System.out.println("created arcs");
            int[] order = {0, 1, 2, 3, 4};
            sol.setNewSpineOrder(order);

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
