# DAT250 Experiment 7

In this experiment, I've dockerized the poll application, with a multi-stage build process,
building the frontend, backend, and moving the final result into a lightweight runtime container.

During this process, I've discovered the fact that Spring Boot dependencies can be injected in the
constructor of a Spring-managed class, not just autowired as a class field.
This makes the process of building a Spring application far more pleasant.

I needed this because I wanted my valkey connection string to be defined by the runtime environment,
injected as an environment variable.

I've added a Dockerfile that is capable of building the application, along with a docker-compose that
can run it, along with its valkey dependency, with said environment variable wired up correctly.
In addition, I've created a Github CI action that builds the image on pushes to the main branch.

I encountered no major technical issues when solving this assignment,
and believe there are no remaining problems to solve.
