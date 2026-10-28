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

## Meeting 2: product catalog

After signing in as a `user`, the dashboard displays products in pages of five. Scroll down to load the next page. Enter a keyword and tap **Search** (or press the keyboard search action) to search product names and descriptions. Clearing the search field restores the full list. Tap a product to open its detail page with its image, price, description, and creation date.

These requests use the saved session token in the `Authorization: Bearer <token>` header:

| Feature | Endpoint | Parameters |
| --- | --- | --- |
| Paginated list | `GET products/listproduct.php` | `page`, `limit` |
| Search | `GET products/search.php` | `q` |
| Product details | `GET products/detail.php` | `id` |

The catalog displays loading, empty, and error states. A **Retry** button appears when a request fails. Product images are loaded with Glide.

## Key source files

- `app/src/main/java/com/example/onlineshopauth/data/ApiService.kt` - Retrofit endpoint definitions.
- `app/src/main/java/com/example/onlineshopauth/data/AuthRepository.kt` - API response handling and error validation.
- `app/src/main/java/com/example/onlineshopauth/ui/AuthViewModel.kt` - Login, registration, and logout with Coroutines.
- `app/src/main/java/com/example/onlineshopauth/data/SessionManager.kt` and `app/src/main/java/com/example/onlineshopauth/ui/SessionRouter.kt` - Session storage and role-based navigation.
- `app/src/main/java/com/example/onlineshopauth/data/ProductModels.kt` and `ProductRepository.kt` - Product API models and requests.
- `app/src/main/java/com/example/onlineshopauth/ui/ProductViewModel.kt` and `ProductDetailViewModel.kt` - Catalog and detail screen state.
- `app/src/main/java/com/example/onlineshopauth/ui/ProductAdapter.kt`, `UserDashboardActivity.kt`, and `ProductDetailActivity.kt` - Product list, search, pagination, and detail UI.

## Meeting 1 report evidence

The accompanying report includes code screenshots and emulator captures of the splash screen, registration and login screens, user profile, admin dashboard and profile, and the login screen after logout.

## Backend

REST API source: <https://github.com/NandaAdisaputra/BackendOnlineShop>. Registration always creates a `user` account. An `admin` account must already exist in the backend database. The backend documentation includes example admin credentials; use them only on a server you manage or are authorized to access.
