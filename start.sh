if [ -f certs/public.pem ] && [ -f certs/private.pem ]; then
    echo "[INFO] RSA keys found in certs/ directory."
else
    echo "[INFO] No RSA keys found in certs/ directory. Generating new keys..."
    chmod +x genrsa.sh
    ./genrsa.sh
    if [ $? -ne 0 ]; then
        echo "[ERROR] Failed to generate RSA keys."
        exit 1
    fi
    echo "[INFO] RSA keys generated successfully."
fi

docker-compose -f docker-compose.dev.yml up

# Wait 5 seconds for the services to start
sleep 5

# Run the application
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev