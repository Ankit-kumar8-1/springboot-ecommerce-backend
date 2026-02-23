# E-Commerce Backend API

A comprehensive RESTful API for an e-commerce platform, supporting user authentication, seller onboarding, product and category management, shopping carts, addresses, payments (via Razorpay), orders, and more. This backend is designed for scalability, with role-based access control (Admin, Seller, Customer) and features like email verification, OTP/password resets, and order lifecycle management.

Built as a Spring Boot application, it handles core e-commerce workflows end-to-end.

## Features
- **Authentication & Authorization**: JWT-based login/signup, email verification, password reset (link/OTP), role-based access (ADMIN, SELLER, CUSTOMER).
- **User Management**: Admin views users; customers manage addresses and profiles.
- **Seller Onboarding**: Intent verification via email, application submission, admin approval/rejection with remarks.
- **Category Management**: Hierarchical categories (root/subcategories), CRUD for admins, public search/slug-based fetching.
- **Product Management**: Sellers create/update/delete products; admins toggle status/delete; public filtering, search, and related products.
- **Shopping Cart**: Add/update/remove items, coupon application/removal, totals calculation with discounts.
- **Addresses**: CRUD operations, set default, auto-fallback to first address.
- **Payments**: Razorpay integration for order creation, verification, and failure handling; multi-seller order grouping.
- **Orders**: User/seller views, status updates (with validation), cancellations (with stock restore), statistics for sellers.
- **Additional**: Wishlists, reviews, transactions, seller reports, home category sections.

## Tech Stack
- **Framework**: Spring Boot 3.5.10
- **Language**: Java 17
- **Build Tool**: Maven
- **Database**: MySQL (via Spring Data JPA with Hibernate)
- **Security**: Spring Security with JWT (HS512), BCrypt for passwords
- **Email**: JavaMailSender (SMTP for verification/resets; tested with Mailinator)
- **Payments**: Razorpay SDK
- **Utilities**: Lombok (for boilerplate reduction), JPA for entity mappings (OneToMany, ManyToMany, Embedded)
- **Other**: CORS configuration, global exception handling, pagination utilities

## Project Structure
The project follows a standard Spring Boot layout with layered architecture (controllers, services, repositories, entities).

```
E:\JAVA FULL COURSE\SPRING BOOT TUTORIAL\SPRING BOOT PROJECTS\ECOMMERCE-BACKEND\SRC
├───main
│ ├───java
│ │ └───com
│ │     └───ankitsaahariya
│ │     │   EcommerceBackendApplication.java
│ │     │
│ │     ├───configuration
│ │     │   CorsConfig.java
│ │     │   RazorpayConfig.java
│ │     │   SecurityConfig.java
│ │     │
│ │     ├───controller
│ │     │   AddressController.java
│ │     │   AdminCategoryController.java
│ │     │   AdminProductController.java
│ │     │   AuthController.java
│ │     │   CartController.java
│ │     │   OrderController.java
│ │     │   PaymentController.java
│ │     │   PublicCategoryController.java
│ │     │   PublicProductController.java
│ │     │   SellerController.java
│ │     │   SellerOrderController.java
│ │     │   SellerProductController.java
│ │     │   UserController.java
│ │     │
│ │     ├───dao
│ │     │   AddressRepository.java
│ │     │   CartItemRepository.java
│ │     │   CartRepository.java
│ │     │   CategoryRepository.java
│ │     │   CouponRepository.java
│ │     │   EmailVerificationTokenRepository.java
│ │     │   OrderItemRepository.java
│ │     │   OrderRepository.java
│ │     │   PaymentOrderRepository.java
│ │     │   ProductRepository.java
│ │     │   SellerIntentTokenRepository.java
│ │     │   SellerProfileRepository.java
│ │     │   TransactionRepository.java
│ │     │   UserRepository.java
│ │     │
│ │     ├───domain
│ │     │   AccountStatus.java
│ │     │   AddressType.java
│ │     │   BusinessType.java
│ │     │   HomeCategorySection.java
│ │     │   OrderStatus.java
│ │     │   PaymentMethod.java
│ │     │   PaymentOrderStatus.java
│ │     │   PaymentStatus.java
│ │     │   Role.java
│ │     │   SellerIntentStatus.java
│ │     │   SellerIntentTokenStatus.java
│ │     │   SellerVerificationStatus.java
│ │     │
│ │     ├───dto
│ │     │   ├───request
│ │     │   │   AddressRequest.java
│ │     │   │   AddToCartRequest.java
│ │     │   │   ApplyCouponRequest.java
│ │     │   │   CategoryRequest.java
│ │     │   │   ChangePasswordUsingOtpRequest.java
│ │     │   │   EmailRequest.java
│ │     │   │   LoginRequest.java
│ │     │   │   PaymentVerificationRequest.java
│ │     │   │   ProductRequest.java
│ │     │   │   SellerApplicationRequest.java
│ │     │   │   SellerStatusUpdateRequest.java
│ │     │   │   SignupRequest.java
│ │     │   │   TokenWithNewPasswordRequest.java
│ │     │   │   UpdateOrderStatusRequest.java
│ │     │   │   UpdateQuantityRequest.java
│ │     │   │
│ │     │   └───response
│ │     │       AddressResponse.java
│ │     │       CartItemResponse.java
│ │     │       CartResponse.java
│ │     │       CategoryResponse.java
│ │     │       LoginResponse.java
│ │     │       MessageResponse.java
│ │     │       OrderItemResponse.java
│ │     │       OrderResponse.java
│ │     │       PageResponse.java
│ │     │       PaymentResponse.java
│ │     │       ProductResponse.java
│ │     │       SellerApplicationDetailResponse.java
│ │     │       SellerProfileResponse.java
│ │     │       UserResponse.java
│ │     │
│ │     ├───entities
│ │     │   Address.java
│ │     │   Cart.java
│ │     │   CartItem.java
│ │     │   Category.java
│ │     │   Coupon.java
│ │     │   Deal.java
│ │     │   EmailVerificationToken.java
│ │     │   Home.java
│ │     │   HomeCategory.java
│ │     │   Order.java
│ │     │   OrderItem.java
│ │     │   PaymentDetails.java
│ │     │   PaymentOrder.java
│ │     │   Product.java
│ │     │   Review.java
│ │     │   SellerIntentToken.java
│ │     │   SellerProfile.java
│ │     │   Transaction.java
│ │     │   UserEntity.java
│ │     │   VerificationCode.java
│ │     │   WishList.java
│ │     │
│ │     ├───Exception
│ │     │   AccountDisabledException.java
│ │     │   BadRequestException.java
│ │     │   EmailAlreadyExistsException.java
│ │     │   EmailNotVerifiedException.java
│ │     │   EmailSendFailedException.java
│ │     │   ForbiddenException.java
│ │     │   forgotPasswordRequestAlreadyAccepted.java
│ │     │   GlobleExceptionHandler.java
│ │     │   GstNumberAlreadyUsed.java
│ │     │   InvalidCredentialsException.java
│ │     │   InvalidVerificationTokenException.java
│ │     │   PasswordResetNotVerified.java
│ │     │   ResourceNotFoundException.java
│ │     │   SellerProfileAlreadyExistsException.java
│ │     │   UserAlreadyVerifiedException.java
│ │     │   UserNotFoundException.java
│ │     │   VerificationTokenExpiredException.java
│ │     │   VerificationTokenStillValidException.java
│ │     │   WrongOtpException.java
│ │     │
│ │     ├───security
│ │     │   JwtAccessDeniedHandler.java
│ │     │   JwtAuthenticationEntryPoint.java
│ │     │   JwtAuthenticationFilter.java
│ │     │   JwtUtil.java
│ │     │
│ │     ├───Service
│ │     │   AddressService.java
│ │     │   AdminCategoryService.java
│ │     │   AdminProductService.java
│ │     │   AuthService.java
│ │     │   CartService.java
│ │     │   CategoryService.java
│ │     │   EmailService.java
│ │     │   OrderService.java
│ │     │   PaymentService.java
│ │     │   PublicCategoryService.java
│ │     │   PublicProductService.java
│ │     │   SellerOrderService.java
│ │     │   SellerProductService.java
│ │     │   SellerService.java
│ │     │   UserService.java
│ │     │
│ │     ├───ServiceImp
│ │     │   AddressServiceImpl.java
│ │     │   AdminCategoryServiceImpl.java
│ │     │   AdminProductServiceImpl.java
│ │     │   AuthServiceImp.java
│ │     │   CartServiceImpl.java
│ │     │   CustomUserDetailsService.java
│ │     │   EmailServiceImp.java
│ │     │   OrderServiceImpl.java
│ │     │   PaymentServiceImpl.java
│ │     │   PublicCategoryServiceImpl.java
│ │     │   PublicProductServiceImpl.java
│ │     │   SellerOrderServiceImpl.java
│ │     │   SellerProductServiceImpl.java
│ │     │   SellerServiceImpl.java
│ │     │   UserServiceImpl.java
│ │     │
│ │     └───Util
│ │         PaginationUtil.java
│ │
│ └───resources
│     │ application.properties
│     │
│     ├───static
│     │     payment_test.html
│     │
│     └───templates
└───test
    └───java
        └───com
            └───ankitsaahariya
                    EcommerceBackendApplicationTests.java
```

- **main/java**: Core application logic, including entry point, configs, controllers, repositories, entities, DTOs, exceptions, security, services, and utils.
- **resources**: Configuration files (application.properties), static assets (payment test HTML), templates (for emails if using Thymeleaf).
- **test/java**: Unit/integration tests.

## Prerequisites
- Java JDK 17+
- Maven 3.8+
- MySQL 8+ (or compatible database)
- Postman (for API testing)
- Razorpay test account (for payment simulation)
- SMTP server (e.g., Gmail) or Mailinator for email testing

## Installation
1. **Clone the Repository**:
   ```
   git clone https://github.com/your-username/ecommerce-backend.git
   cd ecommerce-backend
   ```

2. **Install Dependencies**:
   ```
   mvn clean install
   ```

3. **Set Up Database**:
   - Create a MySQL database: `CREATE DATABASE ecommerce_db;`
   - JPA will handle schema creation/migration on startup (ddl-auto=update).

## Configuration
Edit `src/main/resources/application.properties`:
```
# Server
server.port=8080

# Database
spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT
jwt.secret=your_secure_secret_key_here  # Use a strong key

# Razorpay
razorpay.key_id=rzp_test_your_key_id
razorpay.key_secret=your_key_secret

# Email (SMTP for production; Mailinator for testing)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password  # Use app password for Gmail
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# CORS (for frontend)
cors.allowed-origins=http://localhost:3000  # Update as needed
```

For production, use environment variables or a secure vault.

## Database Setup
- Entities include UserEntity, Address, Cart, Product, Category, SellerProfile, Order, etc., with JPA mappings (OneToMany, ManyToMany, Embedded).
- Enums for statuses (e.g., OrderStatus, PaymentStatus, Role).
- Run the app once to auto-generate tables via Hibernate.

## Running the Project
- Locally:
  ```
  mvn spring-boot:run
  ```
- API base URL: `http://localhost:8080` (or configured port).
- Test payments with `static/payment_test.html` for Razorpay simulation.

For production build:
```
mvn package
java -jar target/ecommerce-backend-0.0.1-SNAPSHOT.jar
```

## API Endpoints
Base URL: `/` (e.g., `http://localhost:8080`). Use Postman for testing; import the provided collection.

Protected endpoints require Bearer JWT (from `/auth/login`).

### User (Admin Only)
| Endpoint | Method | Path |
|----------|--------|------|
| Get User by ID | GET | /user/getUserById/{id} |
| Test | GET | /user/test |
| Get All Users | GET | /user/getAllUser?page={page}&size={size} |

### Authentication
| Endpoint | Method | Path |
|----------|--------|------|
| Signup | POST | /auth/signup |
| Resend Verification | POST | /auth/resend-verification |
| Login | POST | /auth/login |
| Forgot Password Request | POST | /auth/forgot-password-request |
| Change Forgot Password | POST | /auth/change-forgot-password |
| Change Password Request (OTP) | POST | /auth/change-password-request-usingOtp |
| Change Password (OTP) | POST | /auth/change-password-usingOtp |
| Get Current User | GET | /auth/getCurrentUser |

### Seller
| Endpoint | Method | Path |
|----------|--------|------|
| Apply for Seller | POST | /seller/applyForSeller |
| Get Applications (Admin) | GET | /seller/admin/getApplication?status={status}&page={page}&size={size} |
| Get Application Details | GET | /seller/{id} |
| Update Status (Admin) | PUT | /seller/admin/sellers/{id}/status |
| Request Seller Intent | POST | /seller/requestSellerIntent |

### Admin - Category
| Endpoint | Method | Path |
|----------|--------|------|
| Create Category | POST | /admin/categories/create |
| Update Category | PUT | /admin/categories/update/{id} |
| Delete Category | DELETE | /admin/categories/delete/{id} |
| Get All Categories | GET | /admin/categories |

### Public Category
| Endpoint | Method | Path |
|----------|--------|------|
| Get Root Categories | GET | /categories/root |
| Get Subcategories | GET | /categories/subcategories/{id} |
| Get by Slug | GET | /categories/slug/{slug} |
| Get by Parent ID | GET | /categories/parent/{id}/subcategories |
| Search Categories | GET | /categories/search?keyword={keyword} |

### Seller Products
| Endpoint | Method | Path |
|----------|--------|------|
| Create Product | POST | /seller/products/create |
| Update Product | PUT | /seller/products/update/{id} |
| Delete Product | DELETE | /seller/products/delete/{id} |
| Get Seller Products | GET | /seller/products |
| Get Product by ID | GET | /seller/products/getByProductId/{id} |

### Admin Products
| Endpoint | Method | Path |
|----------|--------|------|
| Get All Products | GET | /admin/products |
| Get by ID | GET | /admin/products/{id} |
| Search Products | GET | /admin/products/search?keyword={keyword} |
| Toggle Status | PUT | /admin/products/{id}/toggle-status |
| Delete Product | DELETE | /admin/products/delete/{id} |

### Public Products
| Endpoint | Method | Path |
|----------|--------|------|
| Get All Products | GET | /public/products |
| Get by ID | GET | /public/products/getById/{id} |
| Get by Category | GET | /public/products/getByCategory/{id} |
| Get by Seller | GET | /public/products/getProductBySeller/{id} |
| Search Products | GET | /public/products/search?keyword={keyword} |
| Get Related | GET | /public/products/{id}/related |

### Cart
| Endpoint | Method | Path |
|----------|--------|------|
| Add Item | POST | /cart/add |
| Update Quantity | PUT | /cart/item/{id}/quantity |
| Remove Item | DELETE | /cart/item/remove/{id} |
| Get Cart | GET | /cart/get |
| Clear Cart | DELETE | /cart/clear |
| Apply Coupon | POST | /cart/apply-coupon |
| Remove Coupon | DELETE | /cart/remove-coupon |

### Addresses
| Endpoint | Method | Path |
|----------|--------|------|
| Add Address | POST | /addresses/add |
| Update Address | PUT | /addresses/update/{id} |
| Delete Address | DELETE | /addresses/delete/{id} |
| Get Addresses | GET | /addresses/get |
| Set Default | PUT | /addresses/{id}/set-default |
| Get Default | GET | /addresses/default |

### Payments
| Endpoint | Method | Path |
|----------|--------|------|
| Create Order | POST | /payments/create-order?addressId={id} |
| Verify Payment | POST | /payments/verify |
| Handle Failure | POST | /payments/failure?orderId={id} |

### Seller Orders
| Endpoint | Method | Path |
|----------|--------|------|
| Get Orders | GET | /seller/orders |
| Get by ID | GET | /seller/orders/{id} |
| Update Status | PUT | /seller/orders/{id}/status |
| Get Stats | GET | /seller/orders/stats |

### User Orders
| Endpoint | Method | Path |
|----------|--------|------|
| Get Orders | GET | /orders/public |
| Get by ID | GET | /orders/public/{id} |
| Get by Order ID | GET | /orders/public/order-id/{orderId} |
| Cancel Order | PUT | /orders/public/{id}/cancel |

## Usage Examples
1. **Signup & Verify**:
   - POST `/auth/signup` → Get verification email (check Mailinator).
   - Verify via link → Login with `/auth/login` to get JWT.

2. **Become Seller**:
   - POST `/seller/requestSellerIntent` → Verify email → POST `/seller/applyForSeller`.

3. **Add Product (Seller)**:
   - POST `/seller/products/create` with product details.

4. **Place Order**:
   - Add to cart → Apply coupon → POST `/payments/create-order` → Verify with Razorpay → Orders created.

Use Postman collection for full flows.

## Testing
- Unit Tests: Run `mvn test` (includes EcommerceBackendApplicationTests).
- Integration: Use Postman with provided collection; test emails via Mailinator.
- Payments: Use Razorpay test mode; simulate with `payment_test.html`.

## Commit History Overview
The project evolved through iterative features:
- Initial setup: Spring Boot init, dependencies, DB config.
- Entities: User, Address, Cart, Product, Category, Seller, Orders, Payments.
- Auth: JWT, security config, verification/resets.
- Seller: Intent/verification, applications, admin approvals.
- Categories/Products: CRUD, hierarchies, public APIs.
- Cart: Items management, coupons.
- Addresses: Management with defaults.
- Payments: Razorpay integration, verification.
- Orders: Listings, updates, cancellations, stats.

Full history in Git commits.

## Contributing
- Fork and branch: `git checkout -b feat/your-feature`.
- Commit: Follow semantic conventions (e.g., feat:, fix:).
- PR: Describe changes, reference commits.

Code Style: Java conventions, Lombok usage, meaningful names.

## License
MIT License. See [LICENSE](LICENSE) for details.


