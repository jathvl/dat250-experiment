# DAT250 Experiment 6

In this experiment, I've implemented event sourcing for voting in a poll, using Valkey's Pub/Sub functionality.
I did this in the PollManager, spawning a new thread to take care of listening for messages.
This thread listens to dynamically subscribed to topics, for each possible vote option in the program.
When a user votes for an option, a message is published to the appropriate topic.
The background subscriber thread picks this up and handles storing the vote to the repository.

To make this work like before, I also needed to add a small delay when submitting the vote for the user to receive 
confirmation that it is accepted.
This is a clear disadvantage of the approach, as catching potential errors is much harder in a "fire and forget" model.

Note the fact that I'm creating topics per VoteOption and not Poll. This felt much more natural for my application,
since a vote is for an option, not a poll. Doing the latter would require a lot more resolution of objects in the
repository on either side of the publish/receive interaction.

I did not encounter any technical problems implementing this, and I don't think there are remaining problems to solve.
