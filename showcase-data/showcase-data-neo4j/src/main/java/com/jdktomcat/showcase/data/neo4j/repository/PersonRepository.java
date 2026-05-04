package com.jdktomcat.showcase.data.neo4j.repository;

import com.jdktomcat.showcase.data.neo4j.domain.Person;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends Neo4jRepository<Person, Long> {

    Optional<Person> findByUserId(String userId);

    List<Person> findAllByOrderByNameAsc();

    @Query("MATCH (p:Person {userId: $userId})-[:FRIEND_WITH]-(friend:Person) "
            + "RETURN DISTINCT friend ORDER BY friend.name")
    List<Person> findDirectFriends(@Param("userId") String userId);

    @Query("MATCH (p:Person {userId: $userId})-[:FRIEND_WITH*2]-(candidate:Person) "
            + "WHERE candidate <> p AND NOT (p)-[:FRIEND_WITH]-(candidate) "
            + "RETURN DISTINCT candidate ORDER BY candidate.name")
    List<Person> findSecondDegreeFriends(@Param("userId") String userId);

    @Query("MATCH (a:Person {userId: $firstUserId})-[:FRIEND_WITH]-(mutual:Person)-[:FRIEND_WITH]-(b:Person {userId: $secondUserId}) "
            + "WHERE a <> b "
            + "RETURN DISTINCT mutual ORDER BY mutual.name")
    List<Person> findMutualFriends(@Param("firstUserId") String firstUserId, @Param("secondUserId") String secondUserId);
}
