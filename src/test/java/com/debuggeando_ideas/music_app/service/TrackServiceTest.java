package com.debuggeando_ideas.music_app.service;

import com.debuggeando_ideas.music_app.*;
import com.debuggeando_ideas.music_app.repository.*;
import com.debuggeando_ideas.music_app.service.impl.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.*;
import org.springframework.test.context.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class TrackServiceTest {

    @Mock
    private TrackRepository trackRepository;

    @InjectMocks
    private TrackServiceImpl trackService;

    private static final long VALID_ID = 1L;
    private static final long INVALID_ID = 2L;

    @BeforeEach
    void SetMocks(){
        when(trackRepository.findById(eq(VALID_ID)))
                .thenReturn(Optional.of(DataDummy.TRACK_1));

        when(trackRepository.findById(eq(INVALID_ID)))
                .thenReturn(Optional.empty());
    }

    @Test
    void findByID(){
        var result = trackService.findById(VALID_ID);
        assertEquals(DataDummy.TRACK_1, result);

        assertThrows(
                NoSuchElementException.class,
                () -> trackService.findById(INVALID_ID)
        );

    }
}
