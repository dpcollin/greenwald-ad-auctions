package adAuctions.algorithms;

import adAuctions.structures.Allocation;
import adAuctions.structures.Game;

/**
 * Created by Daniel on 9/30/15.
 */

public class returnSet {
    public Allocation[][] allocations;
    public Game game;

    public returnSet(Allocation[][] allocations, Game game){
        this.allocations = allocations;
        this.game = game;
    }
}
