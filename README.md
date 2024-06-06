# YouTunify
YouTunify adalah sebuah aplikasi yang memungkinkan pengguna untuk mencari dan memutar lagu-lagu dari Spotify, serta menyimpan lagu favorit ke dalam library pribadi.
## Cara Penggunaan
1. Pencarian Lagu:
    * Buka aplikasi YouTunify dan masukkan kata kunci pencarian lagu di kotak pencarian.
    * Aplikasi akan menampilkan daftar lagu yang sesuai dengan kata kunci Anda.
2. Pemutaran Lagu:
    * Pilih lagu yang ingin Anda dengarkan dari daftar hasil pencarian.
    * Lagu akan mulai diputar menggunakan MediaPlayer.
3. Menyimpan Lagu Favorit:
    * Untuk menyimpan lagu ke dalam perpustakaan favorit, cukup sentuh ikon favorit pada lagu yang dipilih.
    * Lagu favorit Anda akan disimpan dan dapat diakses kapan saja melalui menu perpustakaan.
4. Media Control: Anda dapat play/pause lagu, dan memutar previous/next song dalam library

## Penjelasan Singkat Tentang Implementasi Teknis
* Mengambil Informasi Lagu: Aplikasi YouTunify menggunakan API Spotify Scraper untuk mendapatkan informasi trek dan tautan pemutaran dari Spotify. Ketika pengguna melakukan pencarian, aplikasi mengirim permintaan ke API.
* RecyclerView untuk Tampilan Hasil: Hasil pencarian lagu ditampilkan dalam RecyclerView. Setiap item dalam daftar berisi informasi seperti judul, artis, dan gambar cover.
* Pemutaran Lagu dengan MediaPlayer: Setelah memilih lagu, aplikasi mengambil tautan pemutaran dari API yang diambil dari youtube dan menggunakan kelas MediaPlayer di Android untuk memutar lagu tersebut. MediaPlayer menyediakan kontrol penuh atas pemutaran, termasuk play, pause, previous, dan next.
* Menyimpan Lagu Favorit Menggunakan SQLite:
    * Aplikasi menggunakan SQLite sebagai database lokal untuk menyimpan lagu ke library.
    * Setiap kali pengguna menambah lagu ke library, informasi lagu tersebut disimpan dalam tabel database SQLite.
    * Ketika pengguna membuka menu library, aplikasi menampilkan daftar lagu favorit dengan mengambil data dari database SQLite.
