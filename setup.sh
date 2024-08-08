mkdir -p keys && cd keys
openssl genpkey -algorithm RSA -out private.key -pkeyopt rsa_keygen_bits:2048
openssl rsa -pubout -in private.key -out public.key

cd ..
cp .env.example .env

echo "Please fill in the .env file with your own values"