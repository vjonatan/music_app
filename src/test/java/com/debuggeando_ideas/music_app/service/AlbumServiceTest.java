package com.debuggeando_ideas.music_app.service;


import com.debuggeando_ideas.music_app.*;
import com.debuggeando_ideas.music_app.entity.*;
import com.debuggeando_ideas.music_app.repository.*;
import com.debuggeando_ideas.music_app.service.impl.*;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.mock.mockito.*;

import javax.xml.crypto.*;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class AlbumServiceTest extends ServiceSpec{

    @MockBean
    private AlbumRepository albumRepositoryMock;
    @MockBean
    private TrackRepository trackRepositoryMock;
    @MockBean
    private RecordCompanyRepository recordCompanyRepositoryMock;

    @Autowired
    private AlbumServiceImpl albumService;

    private static final long VALID_ID = 1L;
    private static final long INVALID_ID = 2L;

    @BeforeEach
    void setMocks() {
        when(this.albumRepositoryMock.findById(VALID_ID))
                .thenReturn(Optional.of(DataDummy.ALBUM));

        when(this.albumRepositoryMock.findById(INVALID_ID))
                .thenReturn(Optional.empty());
    }

    @Test
    void findById(){
        var result = this.albumService.findById(VALID_ID);
        verify(this.albumRepositoryMock, times(1)).findById(VALID_ID);
        assertEquals(DataDummy.ALBUM_DTO, result);

        assertThrows(
                NoSuchElementException.class,
                () ->{ this.albumService.findById(INVALID_ID);
                       verify(this.albumRepositoryMock, times(1)).findById(INVALID_ID);
                     }
                );
    }

    @Test
    void getAll() {
        when(this.albumRepositoryMock.findAll())
                .thenReturn(Collections.emptyList());
        assertThrows(NoSuchElementException.class,
                () -> {this.albumService.getAll();
                        verify(this.albumRepositoryMock.findAll());
                      }
                );

        when(this.albumRepositoryMock.findAll())
                .thenReturn(List.of(DataDummy.ALBUM));

        var result = this.albumService.getAll();
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void save() {
        when(this.recordCompanyRepositoryMock.findById(DataDummy.ALBUM.getRecordCompany().getTittle()))
                .thenReturn(Optional.of(DataDummy.RECORD_COMPANY));

        when(this.albumRepositoryMock.save(any(AlbumEntity.class)))
                .thenReturn(DataDummy.ALBUM);

        var result = this.albumService.save(DataDummy.ALBUM_DTO);

        assertEquals(DataDummy.ALBUM_DTO, result);
        verify(this.recordCompanyRepositoryMock, times(1)).findById(anyString());
        verify(this.albumRepositoryMock, times(1)).save(any(AlbumEntity.class));
    }

    @Test
    void delete(){
        assertThrows(NoSuchElementException.class,
                () -> {
                        this.albumService.delete(INVALID_ID);
                        verify(this.albumRepositoryMock, times(1)).findById(INVALID_ID);
                      }
                );
    }

    @Test
    void update(){
        assertThrows(NoSuchElementException.class,
                () -> {
                        this.albumService.update(DataDummy.ALBUM_DTO, INVALID_ID);
                        verify(this.albumRepositoryMock, times(1)).findById(INVALID_ID);
                }
                );

        when(this.albumRepositoryMock.save(any(AlbumEntity.class)))
                .thenReturn(DataDummy.ALBUM);

        var result = this.albumService.update(DataDummy.ALBUM_DTO, VALID_ID);

        assertEquals(DataDummy.ALBUM_DTO, result);
    }

    @Test
    void findBetweenPrice(){
        when(this.albumRepositoryMock.findByPriceBetween(10.0, 20.0))
                .thenReturn(Set.of(DataDummy.ALBUM));

        var result = this.albumService.findBetweenPrice(10.0, 20.0);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());

        assertThrows(NoSuchElementException.class,
                () -> {
                        this.albumService.findBetweenPrice(0.0, 0.0);
                        verify(this.albumRepositoryMock, times(1)).findByPriceBetween(0.0, 0.0);
                }
                );
    }

    @Test
    void addTrack(){
        when(this.albumRepositoryMock.findById(VALID_ID))
                .thenReturn(Optional.of(DataDummy.ALBUM));

        when(this.albumRepositoryMock.save(any(AlbumEntity.class)))
                .thenReturn(DataDummy.ALBUM);

        var result = this.albumService.addTrack(DataDummy.TRACK_1_DTO, VALID_ID);
        assertEquals(DataDummy.ALBUM_DTO, result);
        verify(this.albumRepositoryMock, times(1)).save(any(AlbumEntity.class));

        assertThrows(NoSuchElementException.class,
                () -> {
                        this.albumService.addTrack(DataDummy.TRACK_1_DTO, INVALID_ID);
                        verify(this.albumRepositoryMock, times(1)).findById(INVALID_ID);
                }
                );
    }

    @Test
    void removeTrack(){
        when(this.trackRepositoryMock.existsById(INVALID_ID))
                .thenReturn(false);

        assertThrows(NoSuchElementException.class,
                () -> {
                        this.albumService.removeTrack(DataDummy.TRACK_1_DTO, INVALID_ID);
                        verify(this.trackRepositoryMock, times(1)).existsById(INVALID_ID);
                }
        );

        when(this.trackRepositoryMock.existsById(DataDummy.TRACK_1_DTO.getTrackId()))
                .thenReturn(true);
        when(this.albumRepositoryMock.save(any(AlbumEntity.class)))
                .thenReturn(DataDummy.ALBUM);
        var result = this.albumService.removeTrack(DataDummy.TRACK_1_DTO, VALID_ID);
        assertEquals(DataDummy.ALBUM_DTO, result);

    }
}
