Dockerfile


# --- Stage 1: Build Statically Linked Binary ---
FROM golang:1.22-alpine AS builder

WORKDIR /app

# Salin seluruh file kode sumber
COPY . .

# Argumen untuk versi aplikasi yang diinjeksikan saat build
ARG VERSION=dev

# Kompilasi binary secara statis (CGO_ENABLED=0)
RUN CGO_ENABLED=0 GOOS=linux go build -ldflags="-X main.version=${VERSION}" -o myapp .

# --- Stage 2: Final Minimal Image ---
FROM alpine:latest

WORKDIR /root/

# Salin binary dari builder stage
COPY --from=builder /app/myapp .

EXPOSE 8080

CMD ["./myapp"]