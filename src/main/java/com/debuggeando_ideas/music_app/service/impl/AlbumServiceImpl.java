package com.debuggeando_ideas.music_app.service.impl;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.debuggeando_ideas.music_app.dto.AlbumDTO;
import com.debuggeando_ideas.music_app.dto.TrackDTO;
import com.debuggeando_ideas.music_app.entity.AlbumEntity;
import com.debuggeando_ideas.music_app.entity.RecordCompanyEntity;
import com.debuggeando_ideas.music_app.entity.TrackEntity;
import com.debuggeando_ideas.music_app.repository.AlbumRepository;
import com.debuggeando_ideas.music_app.repository.RecordCompanyRepository;
import com.debuggeando_ideas.music_app.repository.TrackRepository;
import com.debuggeando_ideas.music_app.service.IAlbumService;
import com.debuggeando_ideas.music_app.util.JsonUtil;

@Service
@Transactional
public class AlbumServiceImpl implements IAlbumService {
	
	private static final Logger log = LoggerFactory.getLogger(AlbumServiceImpl.class);

	private final AlbumRepository albumRpository;
	private final TrackRepository trackRepository;
	private final RecordCompanyRepository recordCompanyRepository;

	@Autowired
	public AlbumServiceImpl(
			AlbumRepository albumRpository,
			TrackRepository trackRepository,
			RecordCompanyRepository recordCompanyRepository
			) {
		this.albumRpository = albumRpository;
		this.trackRepository = trackRepository;
		this.recordCompanyRepository = recordCompanyRepository;
	}

	@Override
	public Set<AlbumDTO> getAll() {
		Set<AlbumEntity> response = new HashSet<>();
		this.albumRpository.findAll().forEach(response::add);
		if(response.isEmpty()) {
			new NoSuchElementException("No data");
		}
		return response.stream()
				.map(a -> (AlbumDTO) JsonUtil.bodyMapper(a, AlbumDTO.class))
				.collect(Collectors.toSet());
	}

	@Override
	public AlbumDTO save(AlbumDTO album) {
		AlbumEntity toSave = (AlbumEntity) JsonUtil.bodyMapper(album, AlbumEntity.class);
		if(toSave.getRecordCompany() != null) {
			Optional<RecordCompanyEntity> response = this.recordCompanyRepository.findById(toSave.getRecordCompany().getTittle());
			if(response.isPresent()) {
				toSave.setRecordCompany(response.get());
			}
		}
		log.info("save {}", toSave.toString());
		return (AlbumDTO) JsonUtil.bodyMapper(this.albumRpository.save(toSave), AlbumDTO.class);
	}

	@Override
	public AlbumDTO findById(Long id) {
		return (AlbumDTO) JsonUtil.bodyMapper(this.albumRpository.findById(id).orElseThrow(), AlbumDTO.class);
	}

	@Override
	public void delete(Long id) {
		if(this.albumRpository.findById(id).isEmpty()) {
			throw new NoSuchElementException("The id dont exist");
		}
		this.albumRpository.delete(this.albumRpository.findById(id).get());
	}

	@Override
	public AlbumDTO update(AlbumDTO album, Long id) {
		if(this.albumRpository.findById(id).isEmpty()) {
			throw new NoSuchElementException("The id dont exist");
		}
		AlbumEntity toUpdate = this.albumRpository.findById(id).get();
		toUpdate.setAutor(album.getAutor());
		toUpdate.setName(album.getName());
		toUpdate.setPrice(album.getPrice());
		return (AlbumDTO) JsonUtil.bodyMapper(this.albumRpository.save(toUpdate), AlbumDTO.class);
	}

	@Override
	public AlbumDTO addTrack(TrackDTO track, Long id) {
		if(!this.albumRpository.findById(id).isPresent()) {
			throw new NoSuchElementException("The id dont exist");
		}
		AlbumEntity toUpdate = this.albumRpository.findById(id).get();
		toUpdate.addTrack((TrackEntity) JsonUtil.bodyMapper(track, TrackEntity.class));
		return (AlbumDTO) JsonUtil.bodyMapper(this.albumRpository.save(toUpdate), AlbumDTO.class);
	}

	@Override
	public AlbumDTO removeTrack(TrackDTO track, Long id) {
		if(this.albumRpository.findById(id).isEmpty()) {
			throw new NoSuchElementException("The id dont exist");
		}
		AlbumEntity toUpdate = this.albumRpository.findById(id).get();
		
		if(!this.trackRepository.existsById(track.getTrackId())) {
			throw new NoSuchElementException("The track dont exist");
		}
		toUpdate.removeTrack((TrackEntity) JsonUtil.bodyMapper(track, TrackEntity.class));
		this.albumRpository.save(toUpdate);
		return (AlbumDTO) JsonUtil.bodyMapper(this.albumRpository.save(toUpdate), AlbumDTO.class);
	}

	@Override
	public Set<AlbumDTO> findBetweenprice(Double min, Double max) {
		 Set<AlbumEntity> response = this.albumRpository.findByPriceBetween(min, max);
		 if (response.size() == 0) {
			 throw new NoSuchElementException("Not records");
		 }
		 return response.stream()
					.map(a -> (AlbumDTO) JsonUtil.bodyMapper(a, AlbumDTO.class))
					.collect(Collectors.toSet());
	}

}
