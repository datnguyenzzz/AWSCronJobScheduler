DOCKER_NETWORK = awscronjob_default

build:
	mvn clean install
	docker-compose --env-file ./Env/test.env up -d 
	docker-compose ps 

destroy:
	docker-compose down
	docker volume prune  -f
	docker container prune  -f