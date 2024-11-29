package ch.heig.sio.lab2.groupF;

import ch.heig.sio.lab2.tsp.Edge;

public class TwoSwap {
    private  Edge first;
    private  Edge second;

    public TwoSwap(Edge first, Edge second) {
        this.first = first;
        this.second = second;
    }

    public Edge getFirst() {
        return first;
    }

    public Edge getSecond() {
        return second;
    }

    public void setFirst(Edge first) {
        this.first = first;
    }
    public void setSecond(Edge second) {
        this.second = second;
    }
}
