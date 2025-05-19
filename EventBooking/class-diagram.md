# Event Booking System - UML Class Diagram

```plantuml
@startuml EventBookingSystem
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
  package "com.eventBooking.models.user" {
    class User {
      -email: String
      -username: String
      -password: String
      +getters/setters
      +toFileString()
      +{static} fromFileString(line)
    }

    class Admin {
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
}

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

' Service Classes
together {
  package "com.eventBooking.services" {
    class UserService {
      -USER_FILE: String
      +getAllUsers(): List<User>
      +registerUser(user): boolean
      +getUserByUsername(username): User
      +validateLogin(username, password): boolean
      +updateUser(username, updatedUser): boolean
      +deleteUser(username): boolean
    }

    class AdminService {
      -ADMIN_FILE: String
      +validateAdminLogin(username, password): boolean
    }
  }

  package "com.eventBooking.services" as services2 {
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

  package "com.eventBooking.services" as services3 {
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

    class ReviewService {
      -REVIEW_FILE: String
      +createReview(review): boolean
      +getAllReviews(): List<Review>
      +getReviewsByProvider(providerName): List<Review>
      +getAverageRatingForProvider(providerName): double
      +updateReview(reviewId, review): boolean
      +deleteReview(reviewId): boolean
    }
  }
}

' Controller Classes
together {
  package "com.eventBooking.controllers" as controllers1 {
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

    class GalleryController {
      -providerService: ProviderService
      +gallery()
    }

    class PackageController {
      -packageService: PackageService
      +listPackages()
      +viewPackageDetails()
    }
  }

  package "com.eventBooking.controllers" as controllers2 {
    class ReviewController {
      -bookingService: BookingService
      -reviewService: ReviewService
      +showNewReviewForm()
      +submitReview()
      +viewProviderReviews()
    }

    class AuthController {
      -userService: UserService
      -bookingService: BookingService
      -adminService: AdminService
      -logger: Logger
      +home()
      +dashboard()
      +login()
      +adminLogin()
      +register()
      +about()
      +logout()
    }
  }

  package "com.eventBooking.controllers" as controllers3 {
    class AdminController {
      -userService: UserService
      -bookingService: BookingService
      -providerService: ProviderService
      -packageService: PackageService
      -reviewService: ReviewService
      .. User Management ..
      +adminUsers()
      +showCreateUserForm()
      +showEditUserForm()
      +createUser()
      +updateUser()
      +deleteUser()
      .. Booking Management ..
      +adminBookings()
      +showCreateBookingForm()
      +createBooking()
      +deleteBooking()
      +confirm()
      +complete()
      +completeBookingAsAdmin()
      .. Provider Management ..
      +adminProviders()
      +addProvider()
      +removeProvider()
      .. Package Management ..
      +adminPackages()
      +showCreateForm()
      +showEditForm()
      +createPackage()
      +updatePackage()
      +deletePackage()
      .. Review Management ..
      +adminReviews()
      +showEditReviewForm()
      +updateReview()
      +deleteReview()
      .. Other ..
      +dashboard()
      +isAdmin()
    }
  }
}

' Relationships

' Layout hints
left to right direction
skinparam groupInheritance 2

' Inheritance relationships
Admin -|> User
PhotographyPackage -|> Package
VideographyPackage -|> Package

' Composition and aggregation
Provider *-- ProviderType

' Service-Model relationships
UserService -- User : manages
AdminService -- Admin : manages
BookingService -- Booking : manages
ProviderService -- Provider : manages
PackageService -- Package : manages
ReviewService -- Review : manages

' Controller-Service relationships
BookingController -- BookingService
BookingController -- ProviderService
BookingController -- PackageService

AdminController -- UserService
AdminController -- BookingService
AdminController -- ProviderService
AdminController -- PackageService
AdminController -- ReviewService

AuthController -- UserService
AuthController -- BookingService
AuthController -- AdminService

GalleryController -- ProviderService
PackageController -- PackageService
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

## Design Patterns and Architectural Decisions

1. **MVC Architecture**:
   - Models: User, Admin, Provider, Booking, Review, Package, etc.
   - Views: Thymeleaf templates (not shown in diagram)
   - Controllers: BookingController, AdminController, AuthController, GalleryController, PackageController, ReviewController

2. **Service Layer Pattern**:
   - Services encapsulate business logic and data access
   - Controllers depend on services rather than directly accessing data

3. **File-based Persistence**:
   - All data is stored in text files rather than a database
   - Each model class has toFileString() and fromFileString() methods for serialization/deserialization

4. **Inheritance Hierarchy**:
   - Admin extends User
   - PhotographyPackage and VideographyPackage extend Package

5. **Enum Types**:
   - ProviderType enum for categorizing providers

6. **Spring Framework**:
   - Uses Spring MVC for web layer
   - Uses Spring's dependency injection for services
