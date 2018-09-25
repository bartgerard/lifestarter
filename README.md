# lifestarter

# HARD RESET
sudo rm -rf /usr/local/bin/npm /usr/local/share/man/man1/node* /usr/local/lib/dtrace/node.d ~/.npm ~/.node-gyp

To completely uninstall node + npm is to do the following:

go to /usr/local/lib and delete any node and node_modules
go to /usr/local/include and delete any node and node_modules directory
if you installed with brew install node, then run brew uninstall node in your terminal
check your Home directory for any local or lib or include folders, and delete any node or node_modules from there
go to /usr/local/bin and delete any node executable

sudo rm -rf /opt/local/bin/node /opt/local/include/node /opt/local/lib/node_modules
sudo rm -rf /usr/local/bin/npm /usr/local/share/man/man1/node.1 /usr/local/lib/dtrace/node.d

#
sudo npm cache clean -f
sudo npm update npm -g n
sudo n stable
sudo npm install -g npm
node -v
npm -v
# rm -rf /usr/local/bin/ng
sudo npm uninstall -g @angular/cli
sudo npm cache verify 
sudo npm install -g @angular/cli
sudo ng new my-app

sudo npm audit fix
sudo npm audit fix --force
sudo npm audit
sudo npm update ws --depth 4


sudo npm install primeng --save
sudo npm install primeicons --save
sudo npm install ngx-bootstrap bootstrap --save

// Don't forget to add:
- "node_modules/bootstrap/dist/css/bootstrap.min.css"

sudo npm i -g npm-check-updates
sudo ncu -u
sudo npm install


sudo chmod -R 777 .




sudo npm run build




https://www.devglan.com/spring-boot/spring-boot-angular-deployment

# TODO
    "build": "ng build --prod --output-path ../server/web/src/main/resources/static/",

sudo npm run build

sudo npm start




http://localhost:8080/browser/index.html#/


curl http://localhost:8080/guests





ng generate module app-routing --flat --module=app



# find linux version
cat /etc/*-release
lsb_release -a
uname -a
uname -mrs
cat /proc/version

# Mac OS X
brew install mongodb
port install mongodb

# http://yannickloriot.com/2016/04/install-mongodb-and-node-js-on-a-raspberry-pi/
sudo apt-get update
sudo apt-get upgrade
sudo apt-get install mongodb-server

# start on startup
sudo service mongod start
# restart
sudo service mongod restart
# to stop
sudo service mongod stop

mongo

export PATH=<mongodb-install-directory>/bin:$PATH
<path>/mongod
<path>/mongod --dbpath <path to data directory>
Wait for -> [initandlisten] waiting for connections on port 27017
mongo --host 127.0.0.1:27017

show collections


~/./workspace/mongodb-osx-x86_64-4.0.2/bin/mongod
~/./workspace/mongodb-osx-x86_64-4.0.2/bin/mongo





# NGINX (reverse proxy)


## Remove

    sudo apt-get remove --purge nginx

## In case you did delete /etc/nginx

    sudo apt-get install --reinstall nginx-common
    
## Install

    sudo apt-get install nginx

## Start sServer

    sudo /etc/init.d/nginx start

## Auto start on reboot
    sudo systemctl start nginx
    sudo systemctl enable nginx

# Change config
(https://www.digitalocean.com/community/tutorials/how-to-configure-nginx-with-ssl-as-a-reverse-proxy-for-jenkins)

(https://www.linode.com/docs/development/java/how-to-deploy-spring-boot-applications-nginx-ubuntu-16-04/)

sudo nano /etc/nginx/conf.d/lifestarter.conf

server {
    listen 80;
    listen [::]:80;

    server_name example.com;

    location / {
         proxy_pass http://localhost:8080/;
         proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
         proxy_set_header X-Forwarded-Proto $scheme;
         proxy_set_header X-Forwarded-Port $server_port;
    }
}


sudo nginx -t
sudo systemctl restart nginx
sudo systemctl reload nginx

https://www.linode.com/docs/web-servers/nginx/use-nginx-reverse-proxy/


# https
https://drissamri.be/blog/java/enable-https-in-spring-boot/


https://www.linode.com/docs/web-servers/nginx/use-nginx-reverse-proxy/
https://www.linode.com/docs/web-servers/nginx/how-to-configure-nginx/
https://gist.github.com/soheilhy/8b94347ff8336d971ad0


## Determine to which package belongs a file
    dpkg -S /etc/nginx