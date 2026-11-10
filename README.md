# Online Shop Auth and Product Catalog

An Android app written in Kotlin for registration, login, automatic login, role-based dashboards, profiles, logout, and a product catalog. The project follows the **MVVM + Repository** pattern. It uses Retrofit 2, Gson, and Coroutines to access the REST API; ViewBinding for the UI; and SharedPreferences to store the session (`token`, `username`, and `role`).

## Getting started

1. Open this project in Android Studio and wait for Gradle Sync to finish.
2. Install Android SDK API 35. The project requires JDK 17 and uses the included Gradle 8.13 wrapper.
3. Run the app on an emulator or Android device with internet access.
4. The base URL is defined in `app/build.gradle.kts`: `https://pasarrakyatbangetayukulon.my.id/OnlineShop/`. For a local XAMPP server on the Android Emulator, change it to `http://10.0.2.2/OnlineShop/` and enable cleartext HTTP traffic for local development only.

The hosted endpoint was checked on October 28, 2026: `POST /OnlineShop/auth/login.php` without form data returned HTTP 400 with the backend's JSON response. The domain without the `/OnlineShop/` path returned 404.

## Authentication flow

- **Splash:** Checks whether the saved `token`, `username`, and `role` are complete. An `admin` session opens `AdminDashboardActivity`; a `user` session opens `UserDashboardActivity`; otherwise, the app opens `LoginActivity`.
- **Login:** Sends `username` and `password` to `auth/login.php`, then saves the session data from the response.
- **Register:** Sends `username`, `email`, `alamat`, `no_whatsapp`, and `password` to `auth/register.php`. The backend does not return a token after registration, so the app logs in automatically after a successful registration.
- **Logout:** Sends the Bearer token to `auth/logout.php`, clears the local session, and returns to `LoginActivity`. The local session is still cleared if the server cannot be reached.

## Meeting 3: product catalog

After signing in as a `user`, the dashboard displays products in pages of five. Scroll down to load the next page. Enter a keyword and tap **Search** (or press the keyboard search action) to search product names and descriptions. Clearing the search field restores the full list. Tap a product to open its detail page with its image, price, description, and creation date.

These requests use the saved session token in the `Authorization: Bearer <token>` header:

| Feature | Endpoint | Parameters |
| --- | --- | --- |
| Paginated list | `GET products/listproduct.php` | `page`, `limit` |
| Search | `GET products/search.php` | `q` |
| Product details | `GET products/detail.php` | `id` |

The catalog displays loading, empty, and error states. A **Retry** button appears when a request fails. Product images are loaded with Glide.

## Meeting 4: profile and shopping cart

The profile screen can update the signed-in user's username and optionally change the password. A password change requires both the current password and a new password of at least six characters. After a successful username update, the saved local session is refreshed without discarding its active token.

On a product detail screen, choose a quantity and tap **Add to Cart**. The **Cart** button on the user dashboard opens the current user's order list with product images, quantities, subtotals, total item count, and grand total. Loading, empty, and retryable error states are included.

All Meeting 4 requests also use `Authorization: Bearer <token>`:

| Feature | Endpoint | Form fields |
| --- | --- | --- |
| Edit profile | `POST auth/update-profile.php` | `username`, `old_password`, `new_password` |
| Add to cart | `POST cart/add.php` | `product_id`, `quantity` |
| List cart orders | `GET cart/listpesanan.php` | none |

## Key source files

- `app/src/main/java/com/example/onlineshopauth/data/ApiService.kt` - Retrofit endpoint definitions.
- `app/src/main/java/com/example/onlineshopauth/data/AuthRepository.kt` - API response handling and error validation.
- `app/src/main/java/com/example/onlineshopauth/ui/AuthViewModel.kt` - Login, registration, and logout with Coroutines.
- `app/src/main/java/com/example/onlineshopauth/data/SessionManager.kt` and `app/src/main/java/com/example/onlineshopauth/ui/SessionRouter.kt` - Session storage and role-based navigation.
- `app/src/main/java/com/example/onlineshopauth/data/ProductModels.kt` and `ProductRepository.kt` - Product API models and requests.
- `app/src/main/java/com/example/onlineshopauth/ui/ProductViewModel.kt` and `ProductDetailViewModel.kt` - Catalog and detail screen state.
- `app/src/main/java/com/example/onlineshopauth/ui/ProductAdapter.kt`, `UserDashboardActivity.kt`, and `ProductDetailActivity.kt` - Product list, search, pagination, and detail UI.
- `app/src/main/java/com/example/onlineshopauth/data/CartModels.kt` and `CartRepository.kt` - Cart response models and Bearer-authenticated cart requests.
- `app/src/main/java/com/example/onlineshopauth/ui/AddToCartViewModel.kt`, `CartViewModel.kt`, `CartActivity.kt`, and `CartAdapter.kt` - Checkout-to-cart flow and cart order list.

## Meeting 1 report evidence

The accompanying report includes code screenshots and emulator captures of the splash screen, registration and login screens, user profile, admin dashboard and profile, and the login screen after logout.

## Backend

REST API source: <https://github.com/NandaAdisaputra/BackendOnlineShop>. Registration always creates a `user` account. An `admin` account must already exist in the backend database. The backend documentation includes example admin credentials; use them only on a server you manage or are authorized to access.
