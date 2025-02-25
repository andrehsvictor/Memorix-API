mkdir -p .keys && cd .keys

if [ ! -f private.key ] && [ -f public.key ]; then
    echo "Found only public key. Regenerating RSA key pair..."
    rm -f public.key
    rm -f private.key
    openssl genrsa -out private.key 2048
    openssl rsa -in private.key -pubout -out public.key
elif [ -f private.key ] && [ ! -f public.key ]; then
    echo "Found only private key. Generating public key..."
    openssl rsa -in private.key -pubout -out public.key
elif [ ! -f private.key ] && [ ! -f public.key ]; then
    echo "Generating RSA key pair..."
    openssl genrsa -out private.key 2048
    openssl rsa -in private.key -pubout -out public.key
else
    echo "RSA key pair already exists."
fi