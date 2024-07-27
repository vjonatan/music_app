package com.debuggeando_ideas.music_app.controller;

import com.debuggeando_ideas.music_app.*;
import com.debuggeando_ideas.music_app.dto.*;
import com.debuggeando_ideas.music_app.service.*;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.mock.mockito.*;
import org.springframework.http.*;
import org.springframework.test.web.servlet.*;

import javax.xml.crypto.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AlbumControllerTest extends AlbumControllerSpec {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    IAlbumService albumServiceMock;

    ObjectMapper objectMapper;

    private static final Long VALID_ID = 1L;
    private static final Long INVALID_ID = 2L;
    private static final String RESOURCE_PATH = "/v1/album";

    @BeforeEach
    void init(){
        this.objectMapper = new ObjectMapper();
    }

    @BeforeEach
    void setupMocks(){
        when(this.albumServiceMock.findById(eq(VALID_ID)))
                .thenReturn(DataDummy.ALBUM_DTO);

        when(this.albumServiceMock.save(eq(DataDummy.ALBUM_DTO)))
                .thenReturn(DataDummy.ALBUM_DTO);
    }

    @Test
    void findById() throws Exception {
        var uri = RESOURCE_PATH + "/" + VALID_ID;
        mockMvc.perform(
                get(uri).contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(DataDummy.ALBUM_DTO.getName()))
                .andExpect(jsonPath("$.autor").value(DataDummy.ALBUM_DTO.getAutor()))
                .andExpect(jsonPath("$.price").value(DataDummy.ALBUM_DTO.getPrice()));
    }

    @Test
    void save() throws Exception {
        mockMvc.perform(
                post(RESOURCE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(DataDummy.ALBUM_DTO))
                )
                .andExpect(status().isCreated());
        verify(this.albumServiceMock).save(eq(DataDummy.ALBUM_DTO));
    }

    @Test
    void update() throws Exception {
        var uri = RESOURCE_PATH + "/" + VALID_ID;
        final var albumToUpdate = DataDummy.ALBUM_DTO;
        albumToUpdate.setName("Los Playeros");

        when(this.albumServiceMock.update(eq(albumToUpdate), eq(VALID_ID)))
                .thenReturn(albumToUpdate);

        mockMvc.perform(
                put(uri).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(albumToUpdate)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Los Playeros"));

        verify(this.albumServiceMock).update(eq(DataDummy.ALBUM_DTO), eq(VALID_ID));
    }
}
