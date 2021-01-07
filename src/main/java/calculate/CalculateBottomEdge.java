package calculate;

import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class CalculateBottomEdge extends Task<ArrayList<Edge>> implements Observer {

    public ArrayList<Edge> edges;
    public boolean done;
    public KochFractal koch;
    private KochManager man;

    private int currentEdgeCount = 0;
    private int maxEdgeCount;


    public CalculateBottomEdge(KochManager manager, int nxt) {
        edges = new ArrayList<Edge>();
        this.koch = new KochFractal(manager);
        koch.setLevel(nxt);
        koch.addObserver(this);
        done = false;
        this.man = manager;

        maxEdgeCount = koch.getNrOfEdges() / 3;
    }

    @Override
    public void update(Observable o, Object arg) {
        edges.add((Edge)arg);
        currentEdgeCount++;

        updateProgress(currentEdgeCount, maxEdgeCount);
        updateMessage(String.valueOf(currentEdgeCount));
    }


    @Override
    protected ArrayList<Edge> call() throws Exception {
        koch.generateBottomEdge(edges);
        done = true;

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                man.doneChanging();
            }
        });

        return this.edges;
    }
}
