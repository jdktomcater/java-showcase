package com.jdktomcat.showcase.data.neo4j.web;

import com.jdktomcat.showcase.data.neo4j.dto.MomentFeedItem;
import com.jdktomcat.showcase.data.neo4j.dto.PersonSummary;
import com.jdktomcat.showcase.data.neo4j.service.FriendCircleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/friend-circle")
public class FriendCircleController {

    private final FriendCircleService friendCircleService;

    public FriendCircleController(FriendCircleService friendCircleService) {
        this.friendCircleService = friendCircleService;
    }

    @PostMapping("/sample-data")
    public String resetSampleData() {
        friendCircleService.resetSampleGraph();
        return "sample data reset";
    }

    @GetMapping("/people")
    public List<PersonSummary> listPeople() {
        return friendCircleService.listPeople();
    }

    @GetMapping("/people/{userId}/friends")
    public List<PersonSummary> findDirectFriends(@PathVariable String userId) {
        return friendCircleService.findDirectFriends(userId);
    }

    @GetMapping("/people/{userId}/friends/suggestions")
    public List<PersonSummary> findFriendSuggestions(@PathVariable String userId) {
        return friendCircleService.findFriendSuggestions(userId);
    }

    @GetMapping("/people/{userId}/mutual-friends/{otherUserId}")
    public List<PersonSummary> findMutualFriends(@PathVariable String userId, @PathVariable String otherUserId) {
        return friendCircleService.findMutualFriends(userId, otherUserId);
    }

    @GetMapping("/people/{userId}/timeline")
    public List<MomentFeedItem> findFriendTimeline(@PathVariable String userId) {
        return friendCircleService.findFriendTimeline(userId);
    }
}
