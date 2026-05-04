# Showcase Data Neo4j

This module demonstrates a small friend-circle graph with Spring Data Neo4j.

Graph model:

- `Person` nodes represent users.
- `FRIEND_WITH` relationships connect friends and store `since` and `closeness`.
- `MomentPost` nodes represent friend-circle posts.
- `PUBLISHED` relationships connect users to posts.

Start Neo4j locally:

```bash
docker run --rm --name showcase-neo4j \
  -p 7474:7474 -p 7687:7687 \
  -e NEO4J_AUTH=neo4j/password \
  neo4j:5
```

Run the demo:

```bash
mvn -pl showcase-data-neo4j spring-boot:run
```

Useful endpoints:

```bash
curl -X POST http://localhost:8080/api/friend-circle/sample-data
curl http://localhost:8080/api/friend-circle/people
curl http://localhost:8080/api/friend-circle/people/alice/friends
curl http://localhost:8080/api/friend-circle/people/alice/friends/suggestions
curl http://localhost:8080/api/friend-circle/people/alice/mutual-friends/david
curl http://localhost:8080/api/friend-circle/people/alice/timeline
```

Override connection settings when needed:

```bash
NEO4J_URI=bolt://localhost:7687 \
NEO4J_USERNAME=neo4j \
NEO4J_PASSWORD=password \
mvn -pl showcase-data-neo4j spring-boot:run
```
