package com.jdktomcat.showcase.data.neo4j.web;

import com.jdktomcat.showcase.data.neo4j.dto.MomentFeedItem;
import com.jdktomcat.showcase.data.neo4j.dto.PersonSummary;
import com.jdktomcat.showcase.data.neo4j.service.FriendCircleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/people/friends")
    public List<PersonSummary> findDirectFriends(@RequestParam String userId) {
        return friendCircleService.findDirectFriends(userId);
    }

    @GetMapping("/people/friends/suggestions")
    public List<PersonSummary> findFriendSuggestions(@RequestParam String userId) {
        return friendCircleService.findFriendSuggestions(userId);
    }

    @GetMapping("/people/mutual-friends")
    public List<PersonSummary> findMutualFriends(@RequestParam String userId, @RequestParam String otherUserId) {
        return friendCircleService.findMutualFriends(userId, otherUserId);
    }

    @GetMapping("/people/timeline")
    public List<MomentFeedItem> findFriendTimeline(@RequestParam String userId) {
        return friendCircleService.findFriendTimeline(userId);
    }
}
