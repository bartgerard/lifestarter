# nginx drops inherited `add_header` directives in any block that declares one of its own, so these
# have to be included explicitly everywhere rather than set once on the server.
add_header X-Content-Type-Options nosniff always;
add_header X-Frame-Options        SAMEORIGIN always;
add_header Referrer-Policy        strict-origin-when-cross-origin always;
