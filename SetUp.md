# How to set up

## NGINX

### Reverse Proxy

Source: https://github.com/Juriy/easyio/tree/master/conf/1-reverse-proxy

Install

    sudo apt-get install nginx    

Start

    sudo systemctl start nginx
    
Auto start on server startup

    sudo systemctl enable nginx
    
Add configuration
    
    cd /etc/nginx/conf.d
    sudo touch lifestarter.conf
    sudo nano lifestarter.conf

lifestarter.conf

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

Test the configuration

    sudo nginx -t

Restart after configuration change

    sudo systemctl restart nginx
    
Verify status

    sudo systemctl status nginx

What is running?

    netstat -tln

## App

Copy to server

    sudo scp ~/workspace/lifestarter/server/web/target/starter-web-0.0.1-SNAPSHOT.jar <username>@<host>:~/Downloads/

Run

    nohup java -jar ~/Downloads/starter-web-0.0.1-SNAPSHOT.jar &