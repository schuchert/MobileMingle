package com.tw.mobilemingle;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.tw.mobilemingle.domain.Application;
import com.tw.mobilemingle.domain.Journey;

public class ApplicationWithAFewJourneysTest {
	Application a;
	
	@Before
	public void init() {
		a = new Application("");
		a.add(new Journey("j1", 10, 4));
		a.add(new Journey("j1", 12, 2));
	}
	
	@Test
	public void totalStoriesCorrect() {
        assertEquals(6, a.storyCount());
	}

    @Test
    public void totalPointsCorrect() {
        assertEquals(22, a.totalPoints());
    }
}
