# DAT250 Experiment 4 report

In this experiment, I've implemented a new model for the Poll application, that is compatible with JPA and hibernate.
Further, I've made this model compatible with the given tests in a way so that they run successfully.

I've kept this experiment in the same repository as prior experiments, so my code for experiment 2 is here.
Note that in this experiment, I've only added the `jpa.polls` subpackage of my existing project.

A technical problem I encountered during installation of this was Gradle automatically installing the JPA API v3.1.0,
as a dependency of Hibernate v7.1.1.Final.
This version does not include `PersistenceConfiguration`, needed to programmatically configure the service.
I resolved the issue by explicitly depending on the JPA API v3.2.0 in my Gradle config.
This version was compatible with the given Hibernate version, so it caused no further issues.

I've inspected the database tables by changing the configuration to a file-based H2 database, and running one test case.
This gets me a file with all tables and inserted data. I then used the built-in IntelliJ tool for inspecting it.
It created the following tables:
- `POLL`
- `POLL_VOTEOPTION` (joining many VoteOption-s to one Poll)
- `USERS`
- `USERS_POLL` (joining many Poll-s to one User)
- `USERS_VOTE` (joining many Vote-s to one User)
- `VOTE`
- `VOTEOPTION`
- `VOTEOPTION_VOTE` (joining many Votes-s to one VoteOption)

I'm quite disappointed with this generated structure.
All these many-to-one relations can be implemented without using intermediate tables.
This causes many-to-many relations here to be valid in terms of the database schema, although they are nonsensical
in our domain model.
My guess is that I can configure my entities so that Hibernate will do this in a more reasonable way,
but from initial Googling, this does not seem trivially easy while maintaining the Object's two-way relationships.

There are no pending issues I've not managed to solve in the assignment.
