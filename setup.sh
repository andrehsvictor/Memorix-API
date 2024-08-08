animated_message() {
    local message=$1
    clear
    echo "$message"
    sleep 1
    clear
    echo "$message."
    sleep 1
    clear
    echo "$message.."
    sleep 1
    clear
    echo "$message..."
    sleep 1
    clear
}

animated_message "Generating RSA keys"

mkdir -p keys && cd keys
openssl genpkey -algorithm RSA -out private.key -pkeyopt rsa_keygen_bits:2048 
openssl rsa -pubout -in private.key -out public.key

cd ..

clear
read -p "Type the database name: " dbName
read -p "Type the database port: " dbPort
read -p "Type the database user: " dbUser
read -sp "Type the database password: " dbPassword

clear
read -p "Type the JWT access token expiry time (Ex.: 15m, 1h, 24h): " accessTokenExpiry
read -p "Type the JWT refresh token expiry time (Ex.: 24h, 30d): " refreshTokenExpiry

clear
read -p "Type the SMTP host: " mailHost
read -p "Type the SMTP port: " mailPort
read -p "Type the SMTP user: " mailUser
read -sp "Type the SMTP password: " mailPassword

clear
read -p "Type the CORS allowed origins (Ex.: http://localhost:3000,http://localhost:3001): " allowedOrigins

animated_message "Creating .env file"
{
    echo "# Database"
    echo "DB_NAME=$dbName"
    echo "DB_USER=$dbUser"
    echo "DB_PASSWORD=$dbPassword"
    echo "DB_PORT=$dbPort"
    echo ""
    echo "# JWT"
    echo "RSA_PRIVATE_KEY_PATH=file:./keys/private.key"
    echo "RSA_PUBLIC_KEY_PATH=file:./keys/public.key"
    echo "ACCESS_TOKEN_EXPIRY=$accessTokenExpiry"
    echo "REFRESH_TOKEN_EXPIRY=$refreshTokenExpiry"
    echo ""
    echo "# SMTP"
    echo "MAIL_HOST=$mailHost"
    echo "MAIL_PORT=$mailPort"
    echo "MAIL_USERNAME=$mailUser"
    echo "MAIL_PASSWORD=$mailPassword"
    echo ""
    echo "# CORS"
    echo "CORS_ALLOWED_ORIGINS=$allowedOrigins"
} > .env

echo "Done!"