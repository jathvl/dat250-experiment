# DAT250 Experiment 2 report

In this assignment, I've modeled a Polling app as Java Classes.

The `PollManager` class is responsible for managing the entire application state, rooted in a HashMap containing all Users.
A user contains references to its polls and votes. Other objects in this graph also contain references back.

Emulating a database in this way (while trying to guarantee consistency and preventing data races) was very finicky, and I doubt the implementation is perfect.
A better approach might be to store each class of object in separate hashmaps, joining them manually (more like tables).

I do believe the API is decently designed, with quite rich responses when querying data. Obviously, access control and authentication is missing, and without implementing a client using it you don't know what is strictly needed.

It works according to the test scenario described in the task description, see `PollIntegrationTest.java`.

I did not experience any technical problems with installing this, nor do I think there are pending issues to be fixed before continuing development of the application.
