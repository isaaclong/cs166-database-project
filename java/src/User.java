/*
 * This class works in conjunction with ProfNetwork to
 * preload any needed user data when they log into
 * Visagetome so we have easy access to it throughout their login
 */

import java.util.List;
import java.util.ArrayList;
import java.sql.SQLException;

public class User {

    public List<List<String>> friends;
    public List<List<String>> pendingRequests;
    public List<List<String>> messages;
    public List<List<String>> profile;

    String friendsQuery;
    String profileQuery;
    String pendingRequestsQuery;

    String username;

    public User(ProfNetwork esql, String authorisedUser) throws SQLException {
        friendsQuery = String.format("SELECT * FROM connection_usr WHERE (userid = '%s' OR connectionid = '%s') AND status = 'friend'", authorisedUser, authorisedUser);
        profileQuery = String.format("SELECT * FROM usr WHERE userid = '%s'", authorisedUser);
        pendingRequestsQuery = String.format("SELECT * FROM connection_usr WHERE connectionid = '%s' AND status = 'pending'", authorisedUser);

        friends = esql.executeQueryAndReturnResult(friendsQuery);
        profile = esql.executeQueryAndReturnResult(profileQuery);
        pendingRequests = esql.executeQueryAndReturnResult(pendingRequestsQuery);
        username = profile.get(0).get(0);
        messages = new ArrayList<List<String>>();
    }

    public void refresh(ProfNetwork esql, String authorisedUser) throws SQLException {
        friends = esql.executeQueryAndReturnResult(friendsQuery);
        profile = esql.executeQueryAndReturnResult(profileQuery);
        pendingRequests = esql.executeQueryAndReturnResult(pendingRequestsQuery);

        messages = new ArrayList<List<String>>();
    }
}
