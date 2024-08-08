echo "Setting up the project..."

mkdir -p keys && cd keys
openssl genpkey -algorithm RSA -out private.key -pkeyopt rsa_keygen_bits:2048
openssl rsa -pubout -in private.key -out public.key

cd ..

read -p "Type your database name: " dbName
read -p "Type your database user: " dbUser
read -sp "Type your database password: " dbPassword

echo "Now, let's set up the JWT configurations"
read -p "Type the JWT access token expiry time (ex: 15m, 1h, 24h): " accessTokenExpiryTime
read -p "Type the JWT refresh token expiry time (ex: 24h, 30d): " refreshTokenExpiryTime

echo "Now, let's set up the SMTP configurations"
read -p "Type the SMTP host: " mailHost
read -p "Type the SMTP port: " mailPort
read -p "Type the SMTP user: " mailUser
read -sp "Type the SMTP password: " mailPassword

echo "Now, let's set up the CORS configurations"
read -p "Type the allowed origins (ex: http://localhost:3000,http://localhost:3001): " allowedOrigins

{
    echo "DB_NAME=$dbName"
    echo "DB_USER=$dbUser"
    echo "DB_PASSWORD=$dbPassword"
    echo "RSA_PRIVATE_KEY_PATH=file:./keys/private.key"
    echo "RSA_PUBLIC_KEY_PATH=file:./keys/public.key"
    echo "ACCESS_TOKEN_EXPIRY=$accessTokenExpiryTime"
    echo "REFRESH_TOKEN_EXPIRY=$refreshTokenExpiryTime"
    echo "MAIL_HOST=$mailHost"
    echo "MAIL_PORT=$mailPort"
    echo "MAIL_USER=$mailUser"
    echo "MAIL_PASSWORD=$mailPassword"
    echo "ALLOWED_ORIGINS=$allowedOrigins"
} > .env

echo "Project setup completed!"