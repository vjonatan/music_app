package com.debuggeando_ideas.music_app.service;

import com.debuggeando_ideas.music_app.*;
import com.debuggeando_ideas.music_app.entity.*;
import com.debuggeando_ideas.music_app.repository.*;
import com.debuggeando_ideas.music_app.service.impl.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TrackServiceTest extends ServiceSpec{

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

    @Test
    void getAll(){
        var expected = Set.of(DataDummy.TRACK_1, DataDummy.TRACK_2);
        when(trackRepository.findAll())
                .thenReturn(expected);
        var result = trackService.getAll();

        assertEquals(2, result.size());
        assertEquals(expected, result);
        ;
    }

    @Test
    void save(){
        this.trackService.save(DataDummy.TRACK_1);
        verify(trackRepository, times(1)).save(any(TrackEntity.class));
    }

    @Test
    void findById(){
        when(trackRepository.findById(VALID_ID))
                .thenReturn(Optional.of(DataDummy.TRACK_1));
        TrackEntity result = this.trackService.findById(VALID_ID);
        assertEquals(DataDummy.TRACK_1, result);
        assertThrows(NoSuchElementException.class,
                () -> this.trackService.findById(INVALID_ID));
    }

    @Test
    void delete() {
        this.trackService.delete(VALID_ID);
        verify(this.trackRepository, times(1)).deleteById(eq(VALID_ID));
    }

    @Test
    void update() {
        when(this.trackRepository.existsById(VALID_ID))
                .thenReturn(true);

        when(this.trackRepository.existsById(INVALID_ID))
                .thenReturn(false);

        when(this.trackRepository.findById(VALID_ID))
                .thenReturn(Optional.of(DataDummy.TRACK_1));

        when(this.trackRepository.save(any(TrackEntity.class)))
                .thenReturn(DataDummy.TRACK_2);

        TrackEntity result = this.trackService.update(DataDummy.TRACK_1, VALID_ID);

        assertEquals(DataDummy.TRACK_2, result);
        assertThrows(
                NoSuchElementException.class,
                () -> trackService.update(DataDummy.TRACK_1, INVALID_ID)
        );
        verify(this.trackRepository, times(2)).existsById(any(Long.class));
    }
}
