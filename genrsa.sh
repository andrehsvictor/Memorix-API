DIR="certs"
if [ ! -d "$DIR" ]; then
  mkdir "$DIR"
fi

openssl genrsa -out "$DIR/private.pem" 2048
openssl rsa -in "$DIR/private.pem" -pubout -out "$DIR/public.pem"
echo "RSA private key and public key generated in '$DIR/' directory."