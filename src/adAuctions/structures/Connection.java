package adAuctions.structures;

/**
 * Created by Daniel on 9/25/15.
 */

/**
 * A tuple containing indices for campaigns and users
 */

public class Connection {
    int campaignIndex;
    int userIndex;

    public Connection(int campaignIndex, int userIndex){
        this.campaignIndex = campaignIndex;
        this.userIndex = userIndex;
    }

    public Connection(Connection connection){
        this.campaignIndex = connection.campaignIndex;
        this.userIndex = connection.userIndex;
    }

}
