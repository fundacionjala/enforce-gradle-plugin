# EnForce website setup

The website is based on [Jekyll](http://jekyllrb.com/docs/quickstart/), if you want to run the website it is required ruby, and I would like to suggest a linux environment.

## Install dependencies
``` bash
  $ sudo apt-get update
  $ sudo apt-get install git-core curl zlib1g-dev build-essential libssl-dev libreadline-dev libyaml-dev libsqlite3-dev sqlite3 libxml2-dev libxslt1-dev libcurl4-openssl-dev python-software-properties libffi-dev
  $ sudo apt-get install libgdbm-dev libncurses5-dev automake libtool bison libffi-dev
```

## Install rvm
``` bash  
  $ curl -sSL https://rvm.io/mpapis.asc | gpg --import -
  $ curl -L https://get.rvm.io | bash -s stable
  $ source ~/.rvm/scripts/rvm
```

## Install ruby
``` bash
  $ rvm install ruby
  $ rvm use ruby
```

## Install nodejs
``` bash
  $ sudo add-apt-repository ppa:chris-lea/node.js
  $ sudo apt-get update
  $ sudo apt-get install nodejs
```

## Install Jekyll gem
``` bash
  $ gem install jekyll
```

## Start Jekyll server
Go to website repository folder, and execute
``` bash
  jekyll serve -w
```
Open the url: http://localhost:4000 on a browser

Start changing the .md files, and refresh the browser in order to see changes

