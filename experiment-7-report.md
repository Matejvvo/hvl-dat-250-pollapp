# Report

- [GitHub repository](https://github.com/Matejvvo/hvl-dat-250-pollapp/)
- [Assignment](https://github.com/selabhvl/dat250public/blob/master/expassignments/expass7.md)
- [Docker Hub](https://hub.docker.com/repository/docker/matejvvo/pollapp/general)

I successfully managed to containerize the app and run the container.

```bash
# build
docker build -t matejvvo/pollap:latest .

# run
docker run -p 8080:8080 matejvvo/pollap:latest
```

Then I also created a basic CI to build the container and upload it to Docker Hub automatically.

Everything works as expected. However, due to the naive FS implementation of the DB, it is not persistent between container runs, which is expected.

To further finish the app, it could be composed together with some DB, RabbitMQ and Redis servers.
