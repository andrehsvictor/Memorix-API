mkdir -p .keys && cd .keys

if [ ! -f private.pem ] && [ -f public.pem ]; then
    echo "Found only public key. Regenerating RSA key pair..."
    rm -f public.pem
    rm -f private.pem
    openssl genrsa -out private.pem 2048
    openssl rsa -in private.pem -pubout -out public.pem
elif [ -f private.pem ] && [ ! -f public.pem ]; then
    echo "Found only private key. Generating public key..."
    openssl rsa -in private.pem -pubout -out public.pem
elif [ ! -f private.pem ] && [ ! -f public.pem ]; then
    echo "Generating RSA key pair..."
    openssl genrsa -out private.pem 2048
    openssl rsa -in private.pem -pubout -out public.pem
else
    echo "RSA key pair already exists."
fi