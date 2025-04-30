# Project Structure Guideline

## Overview
This document provides a comprehensive guide to the project structure, explaining the organization and purpose of each component.

## Project Information
- **Name**: TeamProject
- **Type**: Spring Boot Web Application
- **Java Version**: 17
- **Spring Boot Version**: 3.4.4
- **Database**: MySQL
- **Port**: 8082

## Directory Structure

### Root Directory
- `build.gradle`: Gradle build configuration file
- `settings.gradle`: Gradle settings file
- `gradlew` and `gradlew.bat`: Gradle wrapper scripts
- `data.sql`: SQL data initialization script

### Source Code Structure

#### Main Application
- `src/main/java/com/example/pro/ProApplication.java`: Main application entry point

#### Configuration
- `src/main/java/com/example/pro/config/CustomSecurityConfig.java`: Spring Security configuration
- `src/main/java/com/example/pro/config/auth/PrincipalDetail.java`: User details implementation for Spring Security
- `src/main/java/com/example/pro/config/auth/PrincipalDetailService.java`: Service for loading user details

#### Controllers
- `src/main/java/com/example/pro/controller/HomeController.java`: Main page controller
- `src/main/java/com/example/pro/controller/UserController.java`: User management controller
- `src/main/java/com/example/pro/controller/RecipeController.java`: Recipe management controller
- `src/main/java/com/example/pro/controller/ReviewController.java`: Review management controller
- `src/main/java/com/example/pro/controller/NoticeController.java`: Notice/announcement controller
- `src/main/java/com/example/pro/controller/CSBoardController.java`: Customer service board controller

#### Services
Services follow the interface-implementation pattern:
- `src/main/java/com/example/pro/service/UserService.java` and `UserServiceImpl.java`: User management
- `src/main/java/com/example/pro/service/RecipeService.java` and `RecipeServiceImpl.java`: Recipe management
- `src/main/java/com/example/pro/service/RecipeIngredientsService.java` and `RecipeIngredientsServiceImpl.java`: Recipe ingredients
- `src/main/java/com/example/pro/service/RecipeStepService.java` and `RecipeStepServiceImpl.java`: Recipe steps
- `src/main/java/com/example/pro/service/IngredientService.java` and `IngredientServiceImpl.java`: Ingredient management
- `src/main/java/com/example/pro/service/ReviewService.java` and `ReviewServiceImpl.java`: Review management
- `src/main/java/com/example/pro/service/NoticeService.java` and `NoticeServiceImpl.java`: Notice management
- `src/main/java/com/example/pro/service/CSBoardService.java` and `CSBoardServiceImpl.java`: Customer service board
- `src/main/java/com/example/pro/service/FileService.java` and `FileServiceImpl.java`: File handling

#### Repositories
- `src/main/java/com/example/pro/repository/UserRepository.java`: User data access
- `src/main/java/com/example/pro/repository/RecipeRepository.java`: Recipe data access
- `src/main/java/com/example/pro/repository/RecipeIngredientsRepository.java`: Recipe ingredients data access
- `src/main/java/com/example/pro/repository/RecipeStepRepository.java`: Recipe steps data access
- `src/main/java/com/example/pro/repository/IngredientRepository.java`: Ingredient data access
- `src/main/java/com/example/pro/repository/ReviewRepository.java`: Review data access
- `src/main/java/com/example/pro/repository/NoticeRepository.java`: Notice data access
- `src/main/java/com/example/pro/repository/CSBoardRepository.java`: Customer service board data access

#### Entities
- `src/main/java/com/example/pro/entity/BaseEntity.java`: Base entity with common fields
- `src/main/java/com/example/pro/entity/UserEntity.java`: User entity
- `src/main/java/com/example/pro/entity/RecipeEntity.java`: Recipe entity
- `src/main/java/com/example/pro/entity/RecipeIngredientsEntity.java`: Recipe ingredients entity
- `src/main/java/com/example/pro/entity/RecipeStepEntity.java`: Recipe steps entity
- `src/main/java/com/example/pro/entity/IngredientEntity.java`: Ingredient entity
- `src/main/java/com/example/pro/entity/ReviewEntity.java`: Review entity
- `src/main/java/com/example/pro/entity/NoticeEntity.java`: Notice entity
- `src/main/java/com/example/pro/entity/CSBoardEntity.java`: Customer service board entity
- `src/main/java/com/example/pro/entity/CartEntity.java`: Shopping cart entity
- `src/main/java/com/example/pro/entity/CartItemEntity.java`: Shopping cart item entity
- `src/main/java/com/example/pro/entity/PaymentEntity.java`: Payment entity
- `src/main/java/com/example/pro/entity/AllPaymentEntity.java`: All payments entity

#### DTOs (Data Transfer Objects)
- `src/main/java/com/example/pro/dto/UserDTO.java`: User data transfer
- `src/main/java/com/example/pro/dto/RecipeDTO.java`: Recipe data transfer
- `src/main/java/com/example/pro/dto/RecipeIngredientsDTO.java`: Recipe ingredients data transfer
- `src/main/java/com/example/pro/dto/RecipeStepDTO.java`: Recipe steps data transfer
- `src/main/java/com/example/pro/dto/IngredientDTO.java`: Ingredient data transfer
- `src/main/java/com/example/pro/dto/ReviewDTO.java`: Review data transfer
- `src/main/java/com/example/pro/dto/NoticeDTO.java`: Notice data transfer
- `src/main/java/com/example/pro/dto/CSBoardDTO.java`: Customer service board data transfer
- `src/main/java/com/example/pro/dto/CartDTO.java`: Shopping cart data transfer
- `src/main/java/com/example/pro/dto/CartItemDTO.java`: Shopping cart item data transfer
- `src/main/java/com/example/pro/dto/upload/UploadFileDTO.java`: File upload data
- `src/main/java/com/example/pro/dto/upload/UploadResultDTO.java`: File upload result

### Resources

#### Configuration
- `src/main/resources/application.properties`: Application configuration

#### Templates (Thymeleaf)
- `src/main/resources/templates/home.html`: Main page template
- `src/main/resources/templates/layout/`: Layout templates (headers, footers, etc.)
- `src/main/resources/templates/userinfo/`: User information templates
- `src/main/resources/templates/recipe/`: Recipe templates
- `src/main/resources/templates/reviews/`: Review templates
- `src/main/resources/templates/notice/`: Notice templates
- `src/main/resources/templates/csboard/`: Customer service board templates
- `src/main/resources/templates/cart/`: Shopping cart templates
- `src/main/resources/templates/payment/`: Payment templates

#### Static Resources
- `src/main/resources/static/css/`: CSS stylesheets
- `src/main/resources/static/js/`: JavaScript files
- `src/main/resources/static/assets/`: Images and other media files
- `src/main/resources/static/index.html`: Static index page

## Key Features
1. **User Management**: Registration, login, profile management
2. **Recipe Management**: Create, read, update, delete recipes with ingredients and steps
3. **Review System**: Users can review recipes
4. **Notice Board**: Announcements and notices
5. **Customer Service Board**: Support and inquiries
6. **Shopping Cart**: Add ingredients to cart
7. **Payment Processing**: Process payments for ingredients
8. 
## Database Configuration
- **Driver**: MySQL
- **URL**: jdbc:mysql://localhost:3306/bab
- **Username**: root
- **Password**: root
- **Schema Generation**: Automatic update (spring.jpa.hibernate.ddl-auto=update)

## File Upload Configuration
- **Location**: c:\upload
- **Max File Size**: 10MB
- **Max Request Size**: 30MB

## Dependencies
- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Spring Boot Starter Security
- Spring Boot Starter Thymeleaf
- Spring Boot Starter Validation
- Thymeleaf Layout Dialect
- Thymeleaf Extras Spring Security
- MySQL Connector
- Lombok
- QueryDSL
- Thumbnailator (for image processing)

## Development Guidelines
1. **Package Structure**: Follow the existing package structure for new components
2. **Naming Conventions**: 
   - Controllers: *Controller.java
   - Services: *Service.java and *ServiceImpl.java
   - Repositories: *Repository.java
   - Entities: *Entity.java
   - DTOs: *DTO.java
3. **Layer Separation**: Maintain clear separation between controllers, services, and repositories
4. **DTO Usage**: Use DTOs for data transfer between layers, don't expose entities directly
5. **Security**: Follow security best practices, use authentication for protected resources
6. **File Uploads**: Use the FileService for handling file uploads
7. **Error Handling**: Implement proper error handling and validation

## Build and Run
1. Build: `./gradlew build`
2. Run: `./gradlew bootRun` or `java -jar build/libs/pro-0.0.1-SNAPSHOT.jar`
3. Access: http://localhost:8082