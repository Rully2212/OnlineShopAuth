# Online Shop Auth (Tugas 1)

Proyek Android Kotlin untuk Register, Login, Auto-Login, dashboard berdasarkan role, profil, dan Logout. Pola kode: **MVVM + Repository**. API diakses dengan Retrofit2 + Gson + Coroutines, layar menggunakan ViewBinding, dan sesi (`token`, `username`, `role`) disimpan di SharedPreferences.

## Menjalankan

1. Buka folder ini di Android Studio dan tunggu Gradle Sync.
2. Pastikan Android SDK API 35 terpasang. Proyek memerlukan JDK 17 dan Gradle 8.13 (wrapper sudah disediakan).
3. Jalankan di emulator atau perangkat Android dengan akses internet.
4. Base URL ada di `app/build.gradle.kts`: `https://pasarrakyatbangetayukulon.my.id/OnlineShop/`. Jika memakai XAMPP lokal pada Android Emulator, ubah ke `http://10.0.2.2/OnlineShop/` dan aktifkan HTTP cleartext untuk pengembangan lokal saja.

Endpoint hosting telah diperiksa pada 28 Oktober 2026: `POST /OnlineShop/auth/login.php` tanpa data memberi HTTP 400 dengan envelope JSON dari backend. Domain tanpa jalur `/OnlineShop/` memberi 404.

## Alur aplikasi

- Splash memeriksa apakah `token`, `username`, dan `role` lengkap di SharedPreferences. Role `admin` menuju AdminDashboardActivity, role `user` menuju UserDashboardActivity, selain itu menuju LoginActivity.
- Login mengirim form `username` dan `password` ke `auth/login.php`, lalu menyimpan data sesi dari respons.
- Register mengirim `username`, `email`, `alamat`, `no_whatsapp`, dan `password` ke `auth/register.php`. Backend tidak memberi token saat register, sehingga aplikasi melakukan login otomatis setelah register berhasil.
- Logout mengirim Bearer token ke `auth/logout.php`, menghapus semua data sesi lokal, lalu kembali ke LoginActivity. Sesi lokal tetap dihapus jika server tidak dapat dihubungi.

## Lokasi kode untuk screenshot laporan

- `app/src/main/java/com/example/onlineshopauth/data/ApiService.kt` — definisi endpoint Retrofit.
- `app/src/main/java/com/example/onlineshopauth/data/AuthRepository.kt` — respons API dan penanganan error.
- `app/src/main/java/com/example/onlineshopauth/ui/AuthViewModel.kt` — login, register, logout dengan Coroutines.
- `app/src/main/java/com/example/onlineshopauth/data/SessionManager.kt` dan `app/src/main/java/com/example/onlineshopauth/ui/SessionRouter.kt` — SharedPreferences dan role routing.

## Checklist laporan PDF

Isi identitas **Nama dan NIM**, lalu lampirkan screenshot kode di atas dan screenshot aplikasi berjalan untuk: Splash Screen, Register, Login, Dashboard Admin, Dashboard User, dan Logout (profil sebelum dan layar Login sesudah logout). Semua screenshot running harus diambil dari emulator atau perangkat yang menjalankan aplikasi ini.

## Catatan backend

Sumber API: <https://github.com/NandaAdisaputra/BackendOnlineShop>. Register selalu membuat akun `user`. Akun `admin` harus sudah tersedia di database backend. Source backend mencantumkan contoh kredensial admin di dokumentasinya; gunakan hanya pada server yang memang Anda kelola atau yang diizinkan dosen.
