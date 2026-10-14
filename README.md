Markdown


# Simple Journey Indonesia - DevOps Technical Test

Repositori ini berisi implementasi aplikasi Go sederhana, Dockerfile multi-stage, skenario hotfix, dan pipeline Jenkins untuk PT Simple Journey Indonesia[cite: 1, 2, 3, 4].

## Cara Build, Run, dan Deploy

### 1. Build Docker Image
Jalankan perintah berikut untuk membangun image dengan menyertakan injeksi versi:
\`\`\`bash
docker build --build-arg VERSION=1.0.0 -t simple-journey-app:1.0.0 .
\`\`\`

### 2. Run Container
Jalankan container dengan kebijakan *restart* otomatis dan *port mapping*:
\`\`\`bash
docker run -d --name app-container --restart unless-stopped -p 8080:8080 simple-journey-app:1.0.0
\`\`\`

### 3. Skenario Hotfix (Swap Binary Tanpa Rebuild Image)
Untuk memperbarui aplikasi secara cepat tanpa membangun ulang image Docker dari awal[cite: 4]:
\`\`\`bash
# 1. Buat binary versi baru di host
CGO_ENABLED=0 go build -ldflags="-X main.version=1.1.0" -o myapp .

# 2. Salin binary ke dalam container yang sedang berjalan
docker cp myapp app-container:/root/myapp

# 3. Restart proses aplikasi di dalam container
docker restart app-container
\`\`\`

## Write-ups Deliverables
- **Part I (Build):** Menggunakan base image `alpine` karena ukurannya yang sangat ringan dan aman, digabungkan dengan kompilasi statis (`CGO_ENABLED=0`) agar binary berdiri sendiri tanpa dependensi shared library dari OS host[cite: 3].
- **Part II (Deploy):** Menggunakan pendekatan `docker cp` untuk hotfix produksi karena menghemat waktu tanpa harus menarik image baru dari registry, sehingga meminimalkan downtime[cite: 4].
- **Part III (CI/CD):** Jenkinsfile menggunakan *credentials binding* agar informasi rahasia aman dari hardcode, serta mendukung mekanisme rollback berbasis pencadangan binary lama jika tahap deploy mengalami kendala[cite: 4].
>>>>>>> b89b347 (Initial commit: DevOps Technical Test Simple Journey)
