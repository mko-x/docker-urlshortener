FROM nginx:latest
MAINTAINER https://m-ko-x.de Markus Kosmal <code@m-ko-x.de>

# Env var defaulting
ENV DB_HOST mysql
ENV APP_URL localhost
ENV APP_NAME Polr
ENV REG_TYPE none
ENV ADMIN_USER admin
ENV ADMIN_PASSWORD voll_geheim_ey
ENV ADMIN_EMAIL admin@example.tld
ENV SETUP_PASSWORD none
ENV IP_METHOD \$_SERVER['REMOTE_ADDR']
ENV PRIVATE false

RUN apt-get update -y -qq && \
    DEBIAN_FRONTEND=noninteractive apt-get install -y -qq --no-install-recommends \
    php5-cli php5-fpm php5-mysqlnd php5-mcrypt git
    
RUN rm /etc/nginx/conf.d/*

ADD conf/dockercfg.php /scripts/
ADD conf/polr.conf /etc/nginx/conf.d/
ADD run.sh /

CMD bash /run.sh