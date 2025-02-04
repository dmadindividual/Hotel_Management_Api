package topg.bimber_user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import topg.bimber_user_service.dto.CommentResponseDto;
import topg.bimber_user_service.models.Booking;
import topg.bimber_user_service.models.Comment;
import topg.bimber_user_service.models.Hotel;
import topg.bimber_user_service.models.User;
import topg.bimber_user_service.repository.BookingRepository;
import topg.bimber_user_service.repository.CommentRepository;
import topg.bimber_user_service.repository.HotelRepository;
import topg.bimber_user_service.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService implements ICommentService {
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;

    // Define inappropriate words
    private static final List<String> PROHIBITED_WORDS = List.of("spam", "hate", "violence", "racist", "abuse", "fuck", "kill", "sex", "bad", "poor");

    @Override
    public CommentResponseDto addComment(String userId, Long hotelId, String content) {
        // Fetch user from the repository
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new IllegalArgumentException("Hotel not found"));
        // Check if the user has a valid booking for this hotel
        Optional<Booking> bookingOpt = bookingRepository.findByUserIdAndHotelId(userId, hotelId);

        if (bookingOpt.isEmpty()) {
            throw new IllegalArgumentException("You must have a valid booking to leave a comment.");
        }

        Booking booking = bookingOpt.get();

        // Ensure the user has checked in and checked out
        if (booking.getStartDate() == null || booking.getEndDate() == null) {
            throw new IllegalArgumentException("You must have checked in and checked out to leave a comment.");
        }

        // Ensure the user is commenting within 30 days of checkout
        LocalDate today = LocalDate.now();
        if (booking.getEndDate().plusDays(30).isBefore(today)) {
            throw new IllegalArgumentException("You can only leave a comment within 30 days after checkout.");
        }

        // Ensure the booking was not canceled
        if (booking.getStatus().equals("CANCELLED")) {
            throw new IllegalArgumentException("You cannot leave a comment on a canceled booking.");
        }

        // Ensure the user hasn't already left a comment for this booking
        if (commentRepository.existsByUserIdAndHotelId(userId, hotelId)) {
            throw new IllegalArgumentException("You can only leave one comment per booking.");
        }

        // Filter inappropriate content
        if (containsProhibitedWords(content)) {
            throw new IllegalArgumentException("Your comment contains inappropriate content.");
        }

        // Save the comment with the fetched username
        Comment comment = Comment.builder()
                .hotel(hotel)
                .userId(userId)
                .userName(user.getUsername()) // Fetch username dynamically
                .content(content)
                .timestamp(LocalDateTime.now())
                .build();
        commentRepository.save(comment);

        return new CommentResponseDto(
                comment.getId(),
                comment.getContent(),
                comment.getTimestamp(),
                comment.getUserName()

        );
    }


    @Override
    public boolean containsProhibitedWords(String content) {
        String lowerContent = content.toLowerCase();
        return PROHIBITED_WORDS.stream().anyMatch(lowerContent::contains);
    }


    @Override
    public List<CommentResponseDto> getCommentsByHotel(Long hotelId) {
 List<Comment> comments =  commentRepository.findByHotelId(hotelId);

 return  comments.stream()
         .map( comment -> {
             return new CommentResponseDto(
                     comment.getId(),
                     comment.getContent(),
                     comment.getTimestamp(),
                     comment.getUserName()
             );
         }).toList();
    }


    @Override
    public CommentResponseDto getCommentsByUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Comment comment = commentRepository.findByUserId(userId);
        return new CommentResponseDto(
                comment.getId(),
                comment.getContent(),
                comment.getTimestamp(),
                comment.getUserName()

        );
    }

    @Override
    public String deleteComment(Long hotelId, Long commentId, String userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        if (!comment.getHotel().getId().equals(hotelId)) {
            throw new IllegalArgumentException("This comment does not belong to the specified hotel.");
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty() || !comment.getUserName().equals(userOpt.get().getUsername())) {
            throw new IllegalArgumentException("You can only delete your own comment.");
        }

        commentRepository.delete(comment);

        return "You have successfully deleted your comment";
    }


}
