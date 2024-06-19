package com.debuggeando_ideas.music_app.repository;


import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AlbumRepositoryTest extends RepositorySpec{

    @Autowired
    AlbumRepository albumRepository;

    private static final Long VALID_ID = 100L;
    private static final Long INVALID_ID = 20L;

    private static final Double PRICE_MIN = 270.0D;
    private static final Double PRICE_MAX = 290.0D;

    @Test
    void findById(){
        var result = this.albumRepository.findById(VALID_ID);
        assertTrue(result.isPresent());

        var album = result.get();

        assertAll(
                () -> assertEquals("fear of the dark", album.getName()),
                () -> assertEquals(280.50, album.getPrice())
        );

        var notFound = this.albumRepository.findById(INVALID_ID);
        assertTrue(notFound.isEmpty());
    }

    @Test
    void save(){
        var result = this.albumRepository.findById(VALID_ID).get();
        var objectSaved = this.albumRepository.save(result);
        assertEquals(result, objectSaved);
    }

    @Test
    void findByPriceBetween(){
        var result = this.albumRepository.findByPriceBetween(PRICE_MIN, PRICE_MAX);
        assertAll(
                () -> assertFalse(result.isEmpty()),
                () -> assertEquals(2, result.size())
        );
    }


}
