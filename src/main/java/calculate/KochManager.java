/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package calculate;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fun3kochfractalfx.FUN3KochFractalFX;
import timeutil.TimeStamp;

/**
 *
 * @author Nico Kuijpers
 * Modified for FUN3 by Gertjan Schouten
 */
public class KochManager {
    
    private KochFractal koch;
    private ArrayList<Edge> edges;
    private FUN3KochFractalFX application;
    private TimeStamp tsCalc;
    private TimeStamp tsDraw;

    private CalculateLeftEdge left;
    private CalculateBottomEdge bottom;
    private CalculateRightEdge right;

    private ExecutorService pool = Executors.newFixedThreadPool(3);

    private int drawing;

    public static boolean abort = false;
    
    public KochManager(FUN3KochFractalFX application) {
        this.edges = new ArrayList<Edge>();
        this.koch = new KochFractal(this);
        this.application = application;
        this.tsCalc = new TimeStamp();
        this.tsDraw = new TimeStamp();
    }


    public void changeLevel(int nxt) {
        tsCalc.init();
        tsCalc.setBegin("Begin calculating");

        this.koch.setLevel(nxt);

        left = new CalculateLeftEdge(this, nxt);
        bottom = new CalculateBottomEdge(this, nxt);
        right = new CalculateRightEdge(this, nxt);

        application.bindProgressBars(left, right, bottom);

        pool.submit(left);
        pool.submit(bottom);
        pool.submit(right);
    }

    public void doneChanging() {
        if (left == null || bottom == null || right == null || !left.done || !bottom.done || !right.done || abort) {
            return;
        }

        edges.clear();
        edges.addAll(left.edges);
        edges.addAll(bottom.edges);
        edges.addAll(right.edges);

        tsCalc.setEnd("End calculating");
        application.setTextNrEdges("" + koch.getNrOfEdges());
        application.setTextCalc(tsCalc.toString());
        drawEdges();

        left = null;
        bottom = null;
        right = null;
    }
    
    public void drawEdges() {

        tsDraw.init();
        tsDraw.setBegin("Begin drawing");
        application.clearKochPanel();
        for (Edge e : edges) {
            if (e != null) {
                application.drawEdge(e);
            }
        }
        tsDraw.setEnd("End drawing");
        application.setTextDraw(tsDraw.toString());
    }

    public synchronized void addEdgeList(ArrayList<Edge> edgeList){
        edges.addAll(edgeList);
    }


    public void stop(){
        pool.shutdown();
        System.out.println("[Pool stopped] Poos has been shut down");
    }

}
