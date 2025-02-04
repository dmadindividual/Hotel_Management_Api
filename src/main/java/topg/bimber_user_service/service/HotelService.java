package topg.bimber_user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import topg.bimber_user_service.dto.*;
import topg.bimber_user_service.exceptions.InvalidStateException;
import topg.bimber_user_service.exceptions.InvalidUserInputException;
import topg.bimber_user_service.models.Hotel;
import topg.bimber_user_service.models.Picture;
import org.springframework.web.multipart.MultipartFile;
import topg.bimber_user_service.models.State;
import topg.bimber_user_service.repository.HotelRepository;
import topg.bimber_user_service.repository.PictureRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service

public class HotelService implements IHotelService {

    private final HotelRepository hotelRepository;
    private final PictureRepository pictureRepository;

    // Creates a new hotel and saves it in the repository
    @Async
    public void savePictures(List<MultipartFile> pictures, Hotel hotel) {
        List<Picture> savedPictures = new ArrayList<>();
        for (MultipartFile file : pictures) {
            Picture picture = new Picture();
            picture.setHotel(hotel);
            picture.setFileName(file.getOriginalFilename());
            picture.setFileType(file.getContentType());
            try {
                picture.setData(file.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("Error reading file data", e);
            }

            savedPictures.add(picture);
        }

        // Save all pictures in a single batch
        pictureRepository.saveAll(savedPictures);
    }

    @Transactional
    @Override
    public HotelResponseDto createHotel(String name, String state, String location, String description, List<String> amenities, List<MultipartFile> pictures) {
        // Step 1: Create the Hotel from the request DTO
        State stateEnum = State.valueOf(state.toUpperCase());

        Hotel hotel = new Hotel();
        hotel.setName(name);
        hotel.setState(stateEnum);
        hotel.setLocation(location);
        hotel.setAmenities(amenities);
        hotel.setDescription(description);
        hotel = hotelRepository.save(hotel); // Save the hotel first

        // Step 2: Handle Pictures (Asynchronously)
        List<PictureResponseDto> pictureResponseDtos = new ArrayList<>();
        if (pictures != null && !pictures.isEmpty()) {
            savePictures(pictures, hotel);  // Asynchronous save for pictures

            // After saving, we fetch the saved pictures from the repository
            List<Picture> savedPictures = pictureRepository.findByHotelId(hotel.getId());

            // Map the saved pictures to PictureResponseDto
            pictureResponseDtos = savedPictures.stream()
                    .map(picture -> new PictureResponseDto(picture.getId(), picture.getFileName(), picture.getFileType()))
                    .collect(Collectors.toList());
        }

        HotelDto hotelDto = new HotelDto(hotel.getId(), hotel.getName(), hotel.getState(), hotel.getLocation(), hotel.getAmenities(), hotel.getDescription(), pictureResponseDtos);

        return new HotelResponseDto(true, hotelDto, pictureResponseDtos);
    }

    @Override
    public List<HotelDtoFilter> getHotelsByState(String stateName) {
        State state = State.valueOf(stateName.toUpperCase());
        List<Hotel> hotels = hotelRepository.findByState(state);

        if (hotels.isEmpty()) {
            return new ArrayList<>(); // Returning an empty list instead of a ResponseEntity for consistency
        }

        return hotels.stream()
                .map(hotel -> {
                    List<String> pictureUrls = hotel.getPictures().stream()
                            .map(Picture::getFileName) // Assuming the Picture entity has a method getFileName
                            .collect(Collectors.toList());

                    return new HotelDtoFilter(
                            hotel.getId(),
                            hotel.getName(),
                            hotel.getState(),
                            hotel.getLocation(),
                            hotel.getAmenities(),
                            hotel.getDescription(),
                            pictureUrls
                    );
                })
                .collect(Collectors.toList());
    }

    // Edits an existing hotel by its ID
    @Transactional
    @Override
    public String editHotelById(Long id, HotelRequestDto hotelRequestDto) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new InvalidUserInputException("Id not found"));

        if (hotelRequestDto.name() != null &&
                !hotel.getName().equals(hotelRequestDto.name()) &&
                hotelRepository.findByName(hotelRequestDto.name()).isPresent()) {
            throw new InvalidUserInputException("Name is already taken");
        }
        if (hotelRequestDto.name() != null) {
            hotel.setName(hotelRequestDto.name());
        }
        if (hotelRequestDto.description() != null) {
            hotel.setDescription(hotelRequestDto.description());
        }

        if (hotelRequestDto.state() != null) {
            hotel.setState(hotelRequestDto.state());
        }

        if (hotelRequestDto.location() != null) {
            hotel.setLocation(hotelRequestDto.location());
        }

        hotel = hotelRepository.save(hotel);
        return "Hotel with Id : " + hotel.getId() + " has successfully been edited";
    }

    @Override
    public HotelDtoFilter getHotelById(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new InvalidUserInputException("Id not found"));
        List<String> pictures = hotel.getPictures().stream()
                .map(Picture::getFileName).collect(Collectors.toList());
        return new HotelDtoFilter(
                hotel.getId(),
                hotel.getName(),
                hotel.getState(),
                hotel.getLocation(),
                hotel.getAmenities(),
                hotel.getDescription(),
                pictures
        );

    }


    @Override
    public String deleteHotelById(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new InvalidUserInputException("Id not found"));

        hotelRepository.delete(hotel);
        return hotel.getName() + " located at " + hotel.getState() + " has been deleted";
    }

    @Override
    public Integer getTotalHotelsInState(String state) {
        State stateName = State.valueOf(state.toUpperCase());
        return hotelRepository.countByState(stateName);
    }

    @Override
    public List<HotelDtoFilter> getMostBookedHotelsByState(String stateName) {

        if (stateName == null || stateName.trim().isEmpty()) {
            throw new InvalidStateException("State cannot be empty.");
        }

        State state = State.valueOf(stateName.toUpperCase());

        List<Hotel> hotels = hotelRepository.findMostBookedHotelsByState(state);

        return hotels.stream()
                .map(hotel -> {
                    List<String> pictureUrls = hotel.getPictures().stream()
                            .map(Picture::getFileName)
                            .collect(Collectors.toList());

                    return new HotelDtoFilter(
                            hotel.getId(),
                            hotel.getName(),
                            hotel.getState(),
                            hotel.getLocation(),
                            hotel.getAmenities(),
                            hotel.getDescription(),
                            pictureUrls
                    );
                })
                .collect(Collectors.toList());
    }


}