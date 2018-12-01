# How to set up

*bold* **italic** __*neither*__ --> Aaah OCD :-)

![image](/images/logo.png)

## First

Check version of Debian OS

    lsb_release -a

## NGINX

### Reverse Proxy

:80 --> :8080 or ...

Source:
> https://github.com/Juriy/easyio/tree/master/conf/1-reverse-proxy

1. Install

        sudo apt-get install nginx    

1. Start

        sudo systemctl start nginx
    
1. Auto start on server startup

        sudo systemctl enable nginx
    
1. Add configuration
    
        cd /etc/nginx/conf.d
    sudo touch lifestarter.conf
    sudo nano lifestarter.conf

    * lifestarter.conf

          server {
              listen       80;
              listen       [::]:80;
              server_name  wedding.xplained.be;
              # root         /usr/share/nginx/html;
      
              proxy_set_header Host $http_host;
              proxy_set_header X-Real-IP $remote_addr;
              proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
      
              location /socket.io/ {
                  proxy_http_version 1.1;
      
                  proxy_set_header Upgrade $http_upgrade;
                  proxy_set_header Connection "upgrade";
      
                  proxy_pass "http://localhost:8080/socket.io/";        
              }
      
              location / {
                  proxy_pass "http://localhost:8080/";
              }
      
              error_page 404 /404.html;
                  location = /40x.html {
              }
      
              error_page 500 502 503 504 /50x.html;
                  location = /50x.html {
              }
          }

1. Test the configuration

        sudo nginx -t

1. Restart after configuration change

        sudo systemctl restart nginx
    
1. Verify status

        sudo systemctl status nginx

1. What is running?

        netstat -tln
    
1. To stop
    
        sudo systemctl stop nginx

## HTTPS

1. Install certbot

        sudo apt-get install certbot

1. Run certbot

        sudo certbot certonly --standalone -d wedding.xplained.be

1. Message:

        Congratulations! Your certificate and chain have been saved at
        /etc/letsencrypt/live/wedding.xplained.be/fullchain.pem. Your cert
        will expire on 2018-12-26. To obtain a new or tweaked version of
        this certificate in the future, simply run certbot again. To
        non-interactively renew *all* of your certificates, run "certbot
        renew"

1. Make back-up

        sudo cp -r /etc/letsencrypt/ ~/Downloads/
        sudo tar czf letsencrypt.tar.gz letsencrypt/

### SSL

* Documentation:

    > https://wiki.mozilla.org/Security/Server_Side_TLS

1. Check nginx version

        nginx -v

1. Generate SSL configuration

    > [SSL-generator](https://mozilla.github.io/server-side-tls/ssl-config-generator/)
    
    * __Server Version__
    
        Set to version of nginx e.g. <code>1.10.3</code>

1. Add SSL certification

    sudo nano /etc/nginx/conf.d/lifestarter.conf

* lifestarter.conf

        server {
          listen       80;
          listen       [::]:80;
          server_name  wedding.xplained.be;
          return 301 https://$host$request_uri;
        }
        
        server {
            listen 443 ssl http2;
            listen [::]:443 ssl http2;
            server_name  wedding.xplained.be;
            # root         /usr/share/nginx/html;
        
            # certs sent to the client in SERVER HELLO are concatenated in ssl_certificate
            ssl_certificate /etc/letsencrypt/live/wedding.xplained.be/fullchain.pem;
            ssl_certificate_key /etc/letsencrypt/live/wedding.xplained.be/privkey.pem;
            ssl_session_timeout 1d;
            ssl_session_cache shared:SSL:50m;
            ssl_session_tickets off;
        
        
            # modern configuration. tweak to your needs.
            ssl_protocols TLSv1.2;
            ssl_ciphers 'ECDHE-ECDSA-AES256-GCM-SHA384:ECDHE-RSA-AES256-GCM-SHA384:ECDHE-ECDSA-CHACHA20-POLY1305:ECDHE-RSA-CHACHA20-POLY1305:ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES128-GCM-SHA256:ECDHE-ECDSA-AES256-SHA384:ECDHE-RSA-AES256-SHA384:ECDHE-ECDSA-AES128-SHA256:ECDHE-RSA-AES128-SHA256';
            ssl_prefer_server_ciphers on;
        
            # HSTS (ngx_http_headers_module is required) (15768000 seconds = 6 months)
            add_header Strict-Transport-Security max-age=15768000;
        
            # OCSP Stapling ---
            # fetch OCSP records from URL in ssl_certificate and cache them
            ssl_stapling on;
            ssl_stapling_verify on;
        
            ## verify chain of trust of OCSP response using Root CA and Intermediate certs
            # ssl_trusted_certificate /path/to/root_CA_cert_plus_intermediates;
        
            resolver 8.8.8.8; # Google DNS resolver
        
            proxy_set_header Host $http_host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        
            location /socket.io/ {
                proxy_http_version 1.1;
        
                proxy_set_header Upgrade $http_upgrade;
                proxy_set_header Connection "upgrade";
        
                proxy_pass "http://localhost:8443/socket.io/";        
            }
        
            location / {
                proxy_pass "http://localhost:8443/";
            }
        
            error_page 404 /404.html;
                location = /40x.html {
            }
        
            error_page 500 502 503 504 /50x.html;
                location = /50x.html {
            } 
        }

### Spring Boot

Permissions

    ls -la

1. Generate keystore
        
        sudo chmod -R 777 /etc/letsencrypt/live/
        
        cd /etc/letsencrypt/live/wedding.xplained.be/
        
        sudo openssl pkcs12 -export -in fullchain.pem \
                       -inkey privkey.pem \
                       -out keystore.p12 \
                       -name tomcat \
                       -CAfile chain.pem \
                       -caname root

* application.properties

        server.port=8443
        server.ssl.password=
        server.ssl.key-store=/etc/letsencrypt/live/wedding.xplained.be/fullchain.pem
        server.ssl.key-store-password=<password>
        server.ssl.key-store-type=PKCS12
        server.ssl.key-alias=tomcat

## App

1. Copy to server

        sudo scp ~/workspace/lifestarter/server/web/target/starter-web-0.0.1-SNAPSHOT.jar <username>@<host>:~/Downloads/

1. Run

        nohup java -jar ~/Downloads/starter-web-0.0.1-SNAPSHOT.jar &
        
## Custom Scripts

1. Change permissions

        chomd u+x <script>
        
1. Execute

        ./<script>
        
