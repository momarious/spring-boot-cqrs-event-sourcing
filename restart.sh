sudo systemctl stop jetty9
sudo systemctl stop postgresql
sudo systemctl stop nginx

echo Stopping all running containers...
docker stop $(docker ps -a -q)
docker container prune

docker volume rm pret_pg_data
# docker volume rm pret_pgadmin_data
docker volume rm pret_zookeeper_data
docker volume rm pret_mongodb_data
docker volume rm pret_kafka_data
docker volume prune

docker-compose up --build -d
