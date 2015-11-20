package adAuctions.algorithms;

import adAuctions.structures.Allocation;
import adAuctions.structures.Market;

/**
 * Created by Daniel on 9/30/15.
 */

public class returnSet {
    public Allocation[][] allocations;
    public Market game;

    public returnSet(Allocation[][] allocations, Market game){
        this.allocations = allocations;
        this.game = game;
    }
}
