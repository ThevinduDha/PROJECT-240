# Booking Management System - UML Class Diagram

```plantuml
@startuml BookingManagementSystem
!pragma layout smetana
skinparam {
  backgroundColor white
  classAttributeIconSize 0
  classFontStyle bold
  classFontSize 12
  packageStyle rectangle
  padding 2
  nodesep 40
  ranksep 25
  roundCorner 5
  shadowing false
  linetype ortho
  ArrowColor #33668E
  ClassBorderColor #33668E
  ClassBackgroundColor #F8F9FA
}

hide circle
hide empty methods
hide empty fields

' Model Classes
together {
  package "com.eventBooking.models.booking" {
    class Booking {
      -bookingId: String
      -username: String
      -providerName: String
      -eventDate: String
      -location: String
      -eventType: String
      -formattedDate: String
      -packageName: String
      -packagePrice: int
      -status: String
      +getters/setters
      +toFileString()
      +{static} fromFileString(line)
      +toString()
    }
  }

  package "com.eventBooking.models.review" {
    class Review {
      -reviewId: String
      -bookingId: String
      -username: String
      -providerName: String
      -rating: int
      -comment: String
      -createdAt: LocalDateTime
      +getters/setters
      +toFileString()
      +{static} fromFileString(line)
    }
  }
}

package "com.eventBooking.models.pricing" {
  class Package {
    -name: String
    -price: int
    -duration: int
    -packageType: String
    +getters/setters
    +toFileString()
    +{static} fromFileString(line)
  }

  class PhotographyPackage {
    -maxPhotoCount: int
    +getters/setters
    +toFileString()
    +{static} fromFileString(line)
  }

  class VideographyPackage {
    -maxVideoDuration: int
    +getters/setters
    +toFileString()
    +{static} fromFileString(line)
  }
}

package "com.eventBooking.models.provider" {
  class Provider {
    -name: String
    -specialty: String
    -rating: int
    -resolution: String
    -providerType: ProviderType
    +getters/setters
    +toFileString()
    +{static} fromFileString(line)
  }

  enum ProviderType {
    PHOTOGRAPHER
    VIDEOGRAPHER
  }
}

package "com.eventBooking.models.users" {
  class User {
    -email: String
    -username: String
    -password: String
    +getters/setters
    +toFileString()
    +{static} fromFileString(line)
  }
}

' Service Classes
together {
  package "com.eventBooking.services" {
    class BookingService {
      -BOOKING_FILE: String
      -bookingQueue: Queue<Booking>
      +getBookingById(bookingId, username): Booking
      +getAllBookings(): List<Booking>
      +getBookingsByUser(username): List<Booking>
      +createBooking(booking): boolean
      +updateBookingStatus(username, providerName, newStatus): void
      +completeBooking(bookingId): void
      +deleteBooking(username, bookingId): void
      +findBookingById(bookingId): Optional<Booking>
      -loadBookingsToQueue(): void
      -saveQueueToFile(): void
    }

    class ReviewService {
      -REVIEW_FILE: String
      +createReview(review): boolean
      +getAllReviews(): List<Review>
      +getReviewsByProvider(providerName): List<Review>
      +getAverageRatingForProvider(providerName): double
      +updateReview(reviewId, review): boolean
      +deleteReview(reviewId): boolean
    }

    class PackageService {
      -PACKAGES_FILE: String
      +getAllPackages(): List<Package>
      +getPackageByName(name): Optional<Package>
      +addPackage(type, pkg): void
      +updatePackage(oldName, type, updatedPackage): boolean
      +deletePackage(name): boolean
      +getPhotographyPackages(): List<PhotographyPackage>
      +getVideographyPackages(): List<VideographyPackage>
    }

    class ProviderService {
      -PROVIDER_FILE: String
      +getAllProviders(): List<Provider>
      +getProvidersByType(type): List<Provider>
      +addProvider(provider): void
      +removeProvider(name): boolean
      +updateProvider(name, updatedProvider): boolean
      +getProvidersSortedByRating(type): List<Provider>
      +getAllProvidersSortedByRating(): List<Provider>
      +getAllProvidersSortedByRatingAscending(): List<Provider>
    }
  }
}

' Controller Classes
together {
  package "com.eventBooking.controllers" {
    class BookingController {
      -bookingService: BookingService
      -providerService: ProviderService
      -packageService: PackageService
      +showBookingForm()
      +createBooking()
      +success()
      +viewBookings()
      +showBooking()
      +cancelBooking()
      +dashboard()
      +upcomingBookings()
    }

    class ReviewController {
      -bookingService: BookingService
      -reviewService: ReviewService
      +showNewReviewForm()
      +submitReview()
      +viewProviderReviews()
    }
  }
}

' Inheritance relationships
PhotographyPackage -|> Package
VideographyPackage -|> Package

' Composition and aggregation
Provider *-- ProviderType

' Service-Model relationships
BookingService -- Booking : manages
ReviewService -- Review : manages
PackageService -- Package : manages
ProviderService -- Provider : manages

' Controller-Service relationships
BookingController -- BookingService
BookingController -- ProviderService
BookingController -- PackageService
ReviewController -- BookingService
ReviewController -- ReviewService

' Model dependencies
Booking ..> User
Booking ..> Provider
Booking ..> Package
Review ..> Booking
Review ..> User
Review ..> Provider

@enduml
```

## Booking Management System Architecture

1. **Models**:
   - **Booking**: Represents a booking made by a user for a provider's services. Contains details like booking ID, username, provider name, event date, location, event type, package details, and status.
   - **Review**: Represents a review left by a user for a provider after a booking is completed. Contains details like review ID, booking ID, username, provider name, rating, comment, and creation date.
   - **Package**: Represents a service package that can be booked. Contains details like name, price, and duration.
   - **Provider**: Represents a service provider (photographer or videographer) that can be booked.
   - **User**: Represents a user who can make bookings and leave reviews.

2. **Services**:
   - **BookingService**: Manages booking operations like creating, retrieving, updating, and deleting bookings.
   - **ReviewService**: Manages review operations like creating, retrieving, updating, and deleting reviews.
   - **PackageService**: Manages package operations like retrieving packages.
   - **ProviderService**: Manages provider operations like retrieving providers.

3. **Controllers**:
   - **BookingController**: Handles HTTP requests related to bookings, such as creating a booking, viewing bookings, and canceling bookings.
   - **ReviewController**: Handles HTTP requests related to reviews, such as creating a review and viewing reviews for a provider.

4. **Data Flow**:
   - Users create bookings for providers through the BookingController.
   - BookingController uses BookingService to manage bookings.
   - After a booking is completed, users can leave reviews through the ReviewController.
   - ReviewController uses ReviewService to manage reviews.
   - Both controllers use their respective services to interact with the model classes.

5. **File-based Persistence**:
   - All data is stored in text files rather than a database.
   - Each model class has toFileString() and fromFileString() methods for serialization/deserialization.

6. **Relationships**:
   - A Booking is associated with a User, Provider, and optionally a Package.
   - A Review is associated with a Booking, User, and Provider.
   - PhotographyPackage and VideographyPackage are specialized types of Package.
   - Provider has a ProviderType (PHOTOGRAPHER or VIDEOGRAPHER).
