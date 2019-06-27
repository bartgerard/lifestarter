# Node

1. Install

    1. Download and install node-v<version>.pkg

        https://nodejs.org/en/download/current/
     
    1. Result

            This package has installed:
            	•	Node.js v11.7.0 to /usr/local/bin/node
            	•	npm v6.5.0 to /usr/local/bin/npm
            Make sure that /usr/local/bin is in your $PATH.

1. Update

       node -v
       sudo npm cache clean -f
       sudo npm install -g n stable

or n latest or n <version>

# NPM

1. Install latest version

        npm -v
        sudo npm install -g npm
        
1. Issues

    https://docs.npmjs.com/downloading-and-installing-node-js-and-npm
        
# Angular CLI

1. Install (globally)

        ng --version
        sudo npm uninstall -g angular-cli
        sudo npm cache verify
        sudo npm install -g @angular/cli@latest
        
1. Install (locally)
        
        cd client
        sudo rm -rf node_modules
        sudo rm package-lock.json
        sudo npm uninstall --save-dev angular-cli
        sudo npm install --save-dev @angular/cli@latest
        sudo npm install
        
        
-- sudo chmod -R 777 .

1. And when it doesn't work because of permissions bs

        sudo chmod -R 777 .
        sudo sudo npm audit --unsafe-perm=true --allow-root
        sudo sudo npm audit fix --unsafe-perm=true --allow-root
        sudo sudo npm audit fix --unsafe-perm=true --allow-root --force
        sudo npm install --save-dev @angular/cli@latest --unsafe-perm=true --allow-root
        sudo npm install --unsafe-perm=true --allow-root
        
        
# Resolving EACCES permissions errors when installing packages globally

https://docs.npmjs.com/resolving-eacces-permissions-errors-when-installing-packages-globally

# EACCES

https://docs.npmjs.com/resolving-eacces-permissions-errors-when-installing-packages-globally

# Angular

1. Update

        sudo ng update --all --force
        
1. Build

        sudo npm run build
        sudo npm start

#

sudo chown -R $(whoami):staff /usr/local/lib/node_modules
sudo chown -R root:staff /usr/local/lib/node_modules

sudo npm install --unsafe-perm=true --allow-root

#

sudo ng update @angular/cli @angular/core
npm install -g @angular/cli
ng update

#

Never import from primeng/primeng
Add primeng/primeng to the import-blacklist of tslint.json