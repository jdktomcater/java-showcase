package com.jdktomcat.showcase.book.ads.interview.paypay;

import org.junit.Test;

import static org.junit.Assert.*;

public class MeetingRoomTest {

    @Test
    public void meetingRoom() {
        int[][] schedules1 = new int[][]{{0, 30}, {5, 10},{15,20}};
        int rooms1 = 2;
        assertEquals(rooms1, MeetingRoom.meetingRoom(schedules1));
    }
}