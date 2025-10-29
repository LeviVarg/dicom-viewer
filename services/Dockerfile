# Stage 1: Build the executable
FROM golang:1.24-alpine AS builder

WORKDIR /app

# Copy go.mod and go.sum and download dependencies
COPY go.mod go.sum ./
RUN go mod download

# Copy the source code
COPY . .

# Build the application statically linked against C library
RUN CGO_ENABLED=0 go build -ldflags "-s -w" -o /go-service ./main.go # <--- Update path to your main package

# Stage 2: Create a minimal production image
FROM alpine:latest

# Set time zone data
RUN apk --no-cache add ca-certificates tzdata

# Set working directory
WORKDIR /root/

# Copy the executable from the builder stage
COPY --from=builder /go-service .

# Expose the port the app runs on (e.g., 8081)
EXPOSE 8081

# Command to run the executable
CMD ["./go-service"]
