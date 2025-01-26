
---

# Hotel Booking System API

This is a Spring Boot-based Hotel Booking System API that provides users (customers and admins) with the ability to manage hotel rooms, make bookings, and perform administrative tasks. The system includes functionalities for user registration, authentication, hotel and room management, and booking operations.

## Features

- **User Authentication**: Users can register, log in, and manage their accounts.
- **Hotel Management**: Admins can create, edit, and delete hotels.
- **Room Management**: Admins can create, update, delete, and filter rooms based on availability, type, and price.
- **Booking System**: Customers can make, update, cancel, and view bookings.
- **Account Verification**: Users and admins can verify accounts via email tokens.
- **Admin Dashboard**: Admins can manage user accounts, hotel details, and room reservations.

## Endpoints

### Authentication

- **POST /api/auth/user/register**: Register a new user.
- **GET /api/v1/user/accountVerification/{verificationToken}**: Verify user account using a token.
- **POST /api/auth/login**: Log in as a registered user.
- **GET /api/v1/user/me/{userId}**: Get details of the logged-in user.
- **PUT /api/v1/user/me/edit/{userId}**: Update user details.
- **DELETE /api/v1/user/me/delete/{userId}**: Delete the user's account.

### Hotel Management (Admin)

- **POST /api/v1/hotel/create**: Create a new hotel.
- **GET /api/v1/hotel/hotels**: List all hotels.
- **GET /api/v1/hotel/hotels/{hotelId}**: Get details of a specific hotel.
- **PUT /api/v1/hotel/edit/{hotelId}**: Update hotel details.
- **DELETE /api/v1/hotel/delete/{hotelId}**: Delete a hotel.

### Room Management (Admin)

- **POST /api/v1/rooms**: Add a new room to a hotel.
- **GET /api/v1/rooms/hotel/{hotelId}**: Get a list of rooms in a hotel.
- **PUT /api/v1/rooms/{roomId}**: Update a room's details.
- **DELETE /api/v1/rooms/{roomId}**: Delete a room from the hotel.
- **GET /api/v1/rooms/availability/{roomId}**: Check room availability.
- **GET /api/v1/rooms/available**: List all available rooms.
- **PATCH /api/v1/rooms/deactivate/{roomId}**: Deactivate a room.
- **PATCH /api/v1/rooms/reactivate/{roomId}**: Reactivate a room.
- **GET /api/v1/rooms/filter/type?type={roomType}**: Filter rooms by type (e.g., SINGLE).
- **GET /api/v1/rooms/filter/price?minPrice={minPrice}&maxPrice={maxPrice}**: Filter rooms by price range.

### Booking

- **POST /api/v1/bookings/book**: Make a booking for a room.
- **PUT /api/v1/bookings/cancel/{bookingId}**: Cancel a booking.
- **GET /api/v1/bookings**: View all bookings.
- **PUT /api/v1/bookings/update/{bookingId}**: Update an existing booking.

## API Documentation

You can use **Postman** to interact with the API. Follow these steps:

1. **Postman Collection**:  
   Download and import the [Postman collection](https://www.postman.com/avionics-explorer-29622376/dmadinidvidual/collection/csroadq/hotel-management?action=share&creator=29599021) to test the API endpoints.

2. **Swagger**:  
   Swagger is integrated into the application for interactive API documentation. To access it, run the application and navigate to:
   ```
   http://localhost:9090/swagger-ui.html
   ```

## Requirements

- Java 11 or higher
- Spring Boot 2.x
- Maven or Gradle
- JDK and IDE setup (e.g., IntelliJ IDEA, Eclipse)

## Setup & Installation

1. **Clone the repository**:
   ```bash
   git clone <repository_url>
   cd hotel-booking-system
   ```

2. **Install dependencies**:
   If using Maven:
   ```bash
   mvn install
   ```

3. **Configure application properties**:
   In `src/main/resources/application.properties`, configure your database and other environment variables.

4. **Run the application**:
   To run the Spring Boot application, use the following command:
   ```bash
   mvn spring-boot:run
   ```

   Or run it directly from your IDE.

5. **Access the API**:
   The API will be available at `http://localhost:9090` (default port). You can use Postman or any API client to test the endpoints.

## Contributing

1. Fork the repository.
2. Create a new branch (`git checkout -b feature/your-feature-name`).
3. Commit your changes (`git commit -m 'Add new feature'`).
4. Push to your branch (`git push origin feature/your-feature-name`).
5. Create a pull request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---
