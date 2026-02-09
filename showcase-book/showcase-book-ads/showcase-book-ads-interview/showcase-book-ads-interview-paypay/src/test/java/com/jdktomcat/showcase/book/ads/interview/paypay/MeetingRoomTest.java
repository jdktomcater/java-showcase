package com.jdktomcat.showcase.book.ads.interview.paypay;

import org.junit.Test;

import static org.junit.Assert.*;

public class MeetingRoomTest {

    @Test
    public void meetingRoom() {
        int[][] schedules1 = new int[][]{{0, 30}, {5, 10}, {15, 20}};
        int rooms1 = 2;
        assertEquals(rooms1, MeetingRoom.meetingRoom(schedules1));
        int[][] schedules2 = {{0, 30}, {5, 10}, {15, 20}};
        assertEquals(2, MeetingRoom.meetingRoom(schedules2));
        int[][] schedules3 = {{7, 10}, {2, 4}};
        assertEquals(1, MeetingRoom.meetingRoom(schedules3));
        int[][] schedules4 = {{1, 2}, {2, 3}};
        assertEquals(1, MeetingRoom.meetingRoom(schedules4));
        int[][] schedules5 = {{1, 10}, {2, 3}, {4, 5}, {6, 7}};
        assertEquals(2, MeetingRoom.meetingRoom(schedules5));
        int[][] schedules6 = {{1, 4}, {2, 5}, {3, 6}, {4, 7}};
        assertEquals(3, MeetingRoom.meetingRoom(schedules6));
        int[][] schedules7 = {{5, 10}};
        assertEquals(1, MeetingRoom.meetingRoom(schedules7));
        int[][] schedules8 = {};
        assertEquals(0, MeetingRoom.meetingRoom(schedules8));
        int[][] schedules9 = {{1, 4}, {1, 3}, {1, 2}};
        assertEquals(3, MeetingRoom.meetingRoom(schedules9));
    }
}